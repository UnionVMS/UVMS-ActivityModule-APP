package eu.europa.ec.fisheries.ers.service.mdrcache;

import com.google.common.base.Stopwatch;
import eu.europa.ec.fisheries.ers.service.exception.MdrLoadingException;
import eu.europa.ec.fisheries.ers.service.mdr.gateway.ActivityMdrGateway;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.mdr.model.mapper.MdrModuleMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListRequest;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListResponse;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetLastRefreshDateResponse;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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

   @Inject
   private ActivityMdrGateway activityMdrGateway;

    // This variable will store the date that the last entity in mdr (module) was refreshed.
    // @Info : refresh to 'last_success' column of 'mdr_codelist_status' table in mdr schema.
    private Date mdrRefreshDate;

    private boolean alreadyLoadedOnce = false;

    private Date lastTimeRefreshDateWasRetrieved;

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
                log.info("[START] MDR Cache was never loaded, loading now..");
                populateMdrCacheDateAndCheckIfRefreshDateChanged();
                populateAllMdrOneByOne();
                log.info("[END] MDR Cache was loaded..");
            } else if (oneMinuteHasPassed() && populateMdrCacheDateAndCheckIfRefreshDateChanged()) { // We fetch MdrCacheDate only once per minute.
                log.info("MDR Cache in MDR was updated.. Going to refresh Rules mdr cahce now...");
                populateAllMdrOneByOne();
            } else {
                log.info("No need to load MDR Cache..");
            }
        } catch (MdrLoadingException e) {
            log.error("Exception while trying to loadAllMdrCodeLists...");
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
            cache.put(type, mdrCodeListByAcronymType(type));
        }
        log.info("{} lists cached", cache.size());
        alreadyLoadedOnce = true;
        log.info("MDR refresh Date {}", mdrRefreshDate);
    }

    public List<ObjectRepresentation> getEntry(MDRAcronymType acronymType) {
        List<ObjectRepresentation> result;
        if (acronymType != null) {
            result = cache.get(acronymType);
            if (CollectionUtils.isEmpty(result)) {
                log.warn(" Reloading {}", acronymType);
                cache.put(acronymType, mdrCodeListByAcronymType(acronymType));
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
    private List<ObjectRepresentation> mdrCodeListByAcronymType(MDRAcronymType acronymType) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        MdrGetCodeListRequest fluxMdrGetCodeListRequest = MdrModuleMapper.createFluxMdrGetCodeListRequest(acronymType.name());
        MdrGetCodeListResponse mdrCodeList = activityMdrGateway.getMdrCodeList(fluxMdrGetCodeListRequest);

        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        if (elapsed > 100) {
            log.info("Loading {} took {} ", acronymType, stopwatch);
        }
        if (mdrCodeList == null) {
            return new ArrayList<>();
        }
        return mdrCodeList.getDataSets();
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

        MdrGetLastRefreshDateResponse lastRefreshDate = activityMdrGateway.getLastRefreshDate();
        if (lastRefreshDate != null) {
            return lastRefreshDate.getLastRefreshDate() != null ? DateUtils.xmlGregorianCalendarToDate(lastRefreshDate.getLastRefreshDate()) : null;
        }
        throw new MessageException("[FATAL-ERROR] Couldn't get LastRefreshDate from MDR Module! Mdr is deployed?");
    }

}

