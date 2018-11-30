package eu.europa.ec.fisheries.ers.service.mdrcache;

import com.google.common.base.Stopwatch;
import eu.europa.ec.fisheries.ers.service.exception.MdrLoadingException;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.MdrProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
import eu.europa.ec.fisheries.uvms.mdr.model.exception.MdrModelMarshallException;
import eu.europa.ec.fisheries.uvms.mdr.model.mapper.MdrModuleMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListResponse;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetLastRefreshDateResponse;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller.unmarshallTextMessage;
import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * @author Gregory Rinaldi, Andi Kovi
 */
@Singleton
@Startup
@Slf4j
public class MDRCache {

    @Getter
    private Map<MDRAcronymType, List<ObjectRepresentation>> cache;

    @EJB
    private ActivityConsumerBean consumer;

    @EJB
    private MdrProducerBean producer;

    // This variable will store the date that the last entity in mdr (module) was refreshed.
    // @Info : refresh to 'last_success' column of 'mdr_codelist_status' table in mdr schema.
    private Date mdrRefreshDate;

    private boolean alreadyLoadedOnce = false;

    private Date lastTimeRefreshDateWasRetrieved;

    private static final long TIME_TO_LIVE_IN_MILLIS = 300000L;
    private static final long TIME_TO_CONSUME_IN_MILLIS = 60000L;


    @PostConstruct
    public void init() {
        cache = new ConcurrentHashMap<>();
    }

    /**
     * It fetches the latest refresh date from MDR and checks if the local cache needs to be refreshed.
     */
    @AccessTimeout(value = 10, unit = MINUTES)
    @Lock(LockType.WRITE)
    public void loadAllMdrCodeLists() {
        try {
            if (!alreadyLoadedOnce) {
                log.info("MDR Cache was never loaded, loading now..");
                populateMdrCacheDateAndCheckIfRefreshDateChanged();
                populateAllMdrOneByOne();
            } else if (oneMinuteHasPassed() && populateMdrCacheDateAndCheckIfRefreshDateChanged()) { // We fetch MdrCacheDate only once per minute.
                log.info("MDR Cache in MDR was updated.. Going to refresh Rules mdr cahce now...");
                populateAllMdrOneByOne();
            }
        } catch (MdrLoadingException e) {
            log.error("Exception while trying to loadAllMdrCodeLists...", e);
        }
    }

    private boolean oneMinuteHasPassed() {
        return (Math.abs(new Date().getTime() - lastTimeRefreshDateWasRetrieved.getTime()) / 1000) > 59;
    }

    /**
     * It loads all the mdr codelists one by one from the mdr module [GET_MDR_CODE_LIST] Request Type.
     */
    private void populateAllMdrOneByOne() {
        log.info("Loading MDR..");
        for (final MDRAcronymType type : MDRAcronymType.values()) {
            try {
                cache.put(type, mdrCodeListByAcronymType(type));
            } catch (MdrLoadingException e) {
                log.error(e.getMessage());
            }
        }
        log.info("{} lists cached", cache.size());
        alreadyLoadedOnce = true;
        log.info("MDR refresh Date {}", mdrRefreshDate);
    }

    @AccessTimeout(value = 10, unit = MINUTES)
    @Lock(LockType.READ)
    public List<ObjectRepresentation> getEntry(MDRAcronymType acronymType) {
        List<ObjectRepresentation> result;
        if (acronymType != null) {
            result = cache.get(acronymType);
            if (CollectionUtils.isEmpty(result)) {
                log.warn(" Reloading {}", acronymType);
                try {
                    cache.put(acronymType, mdrCodeListByAcronymType(acronymType));
                } catch (MdrLoadingException e) {
                    log.error("Error when trying to refresh codelist {}", acronymType);
                    return null;
                }
                result = cache.get(acronymType);
                if (CollectionUtils.isEmpty(result)) {
                    log.error(" Failed to reload {}", acronymType);
                }
            }
            return result;
        }
        return emptyList();
    }

    /**
     * Get one mdr codeList from MDR.
     *
     * @param acronymType
     * @return
     */
    private List<ObjectRepresentation> mdrCodeListByAcronymType(MDRAcronymType acronymType) throws MdrLoadingException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            String request = MdrModuleMapper.createFluxMdrGetCodeListRequest(acronymType.name());
            String corrId = producer.sendMessageToSpecificQueue(request, producer.getDestination(), consumer.getDestination(), TIME_TO_LIVE_IN_MILLIS, DeliveryMode.NON_PERSISTENT);
            TextMessage message = consumer.getMessage(corrId, TextMessage.class, TIME_TO_CONSUME_IN_MILLIS);
            long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            if (elapsed > 100) {
                log.info("Loading {} took {} ", acronymType, stopwatch);
            }
            if (message != null) {
                MdrGetCodeListResponse response = unmarshallTextMessage(message.getText(), MdrGetCodeListResponse.class);
                return response.getDataSets();
            }
        } catch (MessageException | MdrModelMarshallException | JMSException | ActivityModelMarshallException ex) {
            throw new MdrLoadingException("Error while trying to load mdr!", ex);
        }
        return new ArrayList<>();
    }

    /**
     * Checks if the refresh date from mdr module has changed.
     * If it has changed sets cacheDateChanged=true; and refreshed the mdrRefreshDate with the new date.
     */
    private boolean populateMdrCacheDateAndCheckIfRefreshDateChanged() throws MdrLoadingException {
        boolean cacheDateChanged = false;
        try {
            Date mdrDate = getLastTimeMdrWasRefreshedFromMdrModule();
            lastTimeRefreshDateWasRetrieved = new Date();
            if (mdrDate != null && !mdrDate.equals(mdrRefreshDate)) { // This means we have a new date from MDR module..
                mdrRefreshDate = mdrDate;
                cacheDateChanged = true;
            }
        } catch (MessageException e) {
            log.error(" Couldn't populate MDR Refresh date.. MDR Module is deployed?", e);
            throw new MdrLoadingException(e.getMessage());
        }
        return cacheDateChanged;
    }

    /**
     * Calls MDR module to get the latest date the MDR lists werisPresentInMDRListe refreshed.
     *
     * @return
     * @throws MessageException
     */
    private Date getLastTimeMdrWasRefreshedFromMdrModule() throws MessageException {
        try {
            String corrId = producer.sendMessageToSpecificQueue(MdrModuleMapper.createMdrGetLastRefreshDateRequest(), producer.getDestination(), consumer.getDestination(), TIME_TO_LIVE_IN_MILLIS, DeliveryMode.NON_PERSISTENT);
            TextMessage message = consumer.getMessage(corrId, TextMessage.class, 60000L);
            if (message != null) {
                MdrGetLastRefreshDateResponse response = JAXBUtils.unMarshallMessage(message.getText(), MdrGetLastRefreshDateResponse.class);
                return response.getLastRefreshDate() != null ? DateUtils.xmlGregorianCalendarToDate(response.getLastRefreshDate()) : null;
            }
            throw new MessageException("[FATAL-ERROR] Couldn't get LastRefreshDate from MDR Module! Mdr is deployed?");
        } catch (MdrModelMarshallException | JAXBException | JMSException e) {
            throw new MessageException("[FATAL-ERROR] Couldn't get LastRefreshDate from MDR Module! Mdr is deployed?", e);
        }
    }


    @AccessTimeout(value = 10, unit = MINUTES)
    @Lock(LockType.READ)
    public boolean isMdrCacheLoaded() {
        return cache.size() > 10;
    }

}

