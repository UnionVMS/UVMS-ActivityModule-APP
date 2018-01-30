/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.ers.service.EventService;
import eu.europa.ec.fisheries.ers.service.FaCatchReportService;
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.FluxMessageService;
import eu.europa.ec.fisheries.ers.service.facatch.FACatchSummaryHelper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityRequestMapper;
import eu.europa.ec.fisheries.ers.service.mapper.subscription.ActivityToSubscriptionMapper;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityErrorMessageServiceBean;
import eu.europa.ec.fisheries.uvms.activity.message.event.ActivityMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFACatchSummaryReportEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFishingActivityForTripsRequestEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFishingTripListEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetNonUniqueIdsRequestEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.MapToSubscriptionRequestEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.ReceiveFishingActivityRequestEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.message.producer.SubscriptionProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.FaultCode;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityIDType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityTableType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityUniquinessList;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetFishingActivitiesForTripRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetFishingActivitiesForTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.MapToSubscriptionRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.PluginType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SetFLUXFAReportOrQueryMessageRequest;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.fluxfaquerymessage._3.FLUXFAQueryMessage;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

@LocalBean
@Stateless
@Slf4j
public class ActivityEventServiceBean implements EventService {

    private static final String GOT_JMS_INSIDE_ACTIVITY_TO_GET = "Got JMS inside Activity to get ";

    @Inject
    @ActivityMessageErrorEvent
    private Event<EventMessage> errorEvent;

    @EJB
    private FluxMessageService fluxMessageService;

    @EJB
    private FishingTripService fishingTripService;

    @EJB
    private FaCatchReportService faCatchReportService;

    @EJB
    private ActivityMatchingIdsServiceBean matchingIdsService;

    @EJB
    private ActivityService activityServiceBean;

    @EJB
    private ActivityErrorMessageServiceBean producer;

    @EJB
    private SubscriptionProducerBean subscriptionProducer;

    @Override
    public void getMapToSubscriptionMessage(@Observes @MapToSubscriptionRequestEvent EventMessage message) {
        log.info(GOT_JMS_INSIDE_ACTIVITY_TO_GET + "MapToSubscriptionRequestEvent");
        try {
            TextMessage jmsMessage = message.getJmsMessage();
            String jmsCorrelationID = jmsMessage.getJMSMessageID();
            String messageReceived = jmsMessage.getText();
            SubscriptionDataRequest subscriptionDataRequest = null;
            MapToSubscriptionRequest baseRequest = JAXBUtils.unMarshallMessage(messageReceived, MapToSubscriptionRequest.class);
            switch (baseRequest.getMessageType()){
                case FLUX_FA_QUERY_MESSAGE:
                    FLUXFAQueryMessage fluxfaQueryMessage = JAXBUtils.unMarshallMessage(baseRequest.getRequest(), FLUXFAQueryMessage.class);
                    subscriptionDataRequest = ActivityToSubscriptionMapper.mapToSubscriptionDataRequest(fluxfaQueryMessage.getFAQuery());
                    break;
                case FLUX_FA_REPORT_MESSAGE:
                    FLUXFAReportMessage fluxFAReportMessage = JAXBUtils.unMarshallMessage(baseRequest.getRequest(), FLUXFAReportMessage.class);
                    subscriptionDataRequest = ActivityToSubscriptionMapper.mapToSubscriptionDataRequest(fluxFAReportMessage);
                    break;
                    default:
                        sendError(message, new IllegalArgumentException("VERBODEN VRUCHT"));
            }
            subscriptionProducer.sendMessageWithSpecificIds(JAXBUtils.marshallJaxBObjectToString(subscriptionDataRequest),
                    subscriptionProducer.getDestination(), JMSUtils.lookupQueue(MessageConstants.QUEUE_RULES),null, jmsCorrelationID);
        } catch ( JAXBException | JMSException e) {
            sendError(message, e);
        }
    }

    @Override
    public void getFishingActivityMessage(@Observes @ReceiveFishingActivityRequestEvent EventMessage eventMessage) {
        log.info(GOT_JMS_INSIDE_ACTIVITY_TO_GET + "SetFLUXFAReportOrQueryMessageRequest");
        try {
            TextMessage jmsMessage = eventMessage.getJmsMessage();
            SetFLUXFAReportOrQueryMessageRequest request = JAXBMarshaller.unmarshallTextMessage(jmsMessage, SetFLUXFAReportOrQueryMessageRequest.class);
            log.info("SetFLUXFAReportOrQueryMessageRequest unmarshalled");
            if(request==null){
                log.error("Unmarshalled SetFLUXFAReportOrQueryMessageRequest is null. Something went wrong during jms comm?!");
                return;
            }
            switch (eventMessage.getMethod()) {
                case GET_FLUX_FA_REPORT :
                    FLUXFAReportMessage fluxFAReportMessage = JAXBMarshaller.unmarshallTextMessage(request.getRequest(), FLUXFAReportMessage.class);
                    deleteDuplicatedReportsFromXMLDocument(fluxFAReportMessage);
                    if(CollectionUtils.isNotEmpty(fluxFAReportMessage.getFAReportDocuments())){
                        fluxMessageService.saveFishingActivityReportDocuments(fluxFAReportMessage, extractPluginType(request.getPluginType()));
                    } else {
                        log.error("[ERROR] After checking faReportDocuments IDs, all of them exist already in Activity DB.. So nothing will be saved!!");
                    }
                    break;
                case GET_FLUX_FA_QUERY:
                    FLUXFAQueryMessage fluxFAQueryMessage = JAXBMarshaller.unmarshallTextMessage(request.getRequest(), FLUXFAQueryMessage.class);
                    // TODO : Implement me... Map tp real HQl/SQL query and run the query and map the results to FLUXFAReportMessage and send it to rules...
                    log.error("TODO : FAQUERY mappers NOT implemented yet....");
                    break;
            }
        } catch (ActivityModelMarshallException | ServiceException e) {
            sendError(eventMessage, e);
        }
    }

    @Override
    public void getFishingTripList(@Observes @GetFishingTripListEvent EventMessage message) {
        log.info(GOT_JMS_INSIDE_ACTIVITY_TO_GET + "FishingTripIds:");
        try {
            log.debug("JMS Incoming text message: {}", message.getJmsMessage().getText());
            FishingTripRequest baseRequest = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), FishingTripRequest.class);
            log.debug("FishingTriId Request Unmarshalled");
            FishingTripResponse baseResponse = fishingTripService.filterFishingTripsForReporting(FishingActivityRequestMapper.buildFishingActivityQueryFromRequest(baseRequest));
            log.debug("FishingTripResponse ::: "+FACatchSummaryHelper.printJsonstructure(baseResponse));
            String response = JAXBMarshaller.marshallJaxBObjectToString(baseResponse);
            log.debug("FishingTriId response marshalled");
            producer.sendModuleResponseMessage(message.getJmsMessage(), response, producer.getModuleName());
            log.debug("Response sent back.");
        } catch (ActivityModelMarshallException | JMSException | ServiceException e) {
            sendError(message, e);
        }
    }


    @Override
    public void getFACatchSummaryReport(@Observes @GetFACatchSummaryReportEvent EventMessage message)  {
        log.info(GOT_JMS_INSIDE_ACTIVITY_TO_GET + "FACatchSummaryReport:");
        try {
            log.debug("JMS Incoming text message: {}", message.getJmsMessage().getText());
            FACatchSummaryReportRequest baseRequest = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), FACatchSummaryReportRequest.class);
            FACatchSummaryReportResponse faCatchSummaryReportResponse= faCatchReportService.getFACatchSummaryReportResponse(FishingActivityRequestMapper.buildFishingActivityQueryFromRequest(baseRequest));
            String response = JAXBMarshaller.marshallJaxBObjectToString(faCatchSummaryReportResponse);
            producer.sendModuleResponseMessage(message.getJmsMessage(), response, producer.getModuleName());
        } catch (ActivityModelMarshallException | JMSException | ServiceException e) {
            sendError(message, e);
        }

    }

    @Override
    public void getNonUniqueIdsRequest(@Observes @GetNonUniqueIdsRequestEvent EventMessage message){
        log.info(GOT_JMS_INSIDE_ACTIVITY_TO_GET + "Non Matching Ids of different Tables : ");
        try {
            log.debug(message.getJmsMessage().getText());
            GetNonUniqueIdsRequest getNonUniqueIdsRequest = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), GetNonUniqueIdsRequest.class);
            GetNonUniqueIdsResponse faCatchSummaryReportResponse = matchingIdsService.getMatchingIdsResponse(getNonUniqueIdsRequest.getActivityUniquinessLists());
            String response = JAXBMarshaller.marshallJaxBObjectToString(faCatchSummaryReportResponse);
            producer.sendModuleResponseMessage(message.getJmsMessage(), response, producer.getModuleName());
        } catch (ActivityModelMarshallException | JMSException e) {
            sendError(message, e);
        }
    }

    @Override
    public void getFishingActivityForTripsRequest(@Observes @GetFishingActivityForTripsRequestEvent EventMessage message){
        log.info(GOT_JMS_INSIDE_ACTIVITY_TO_GET + " Fishing activities related to trips.");
        try {
            log.debug(message.getJmsMessage().getText());
            GetFishingActivitiesForTripRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), GetFishingActivitiesForTripRequest.class);
            GetFishingActivitiesForTripResponse response = activityServiceBean.getFaAndTripIdsFromTripIds(request.getFaAndTripIds());
            String responseStr = JAXBMarshaller.marshallJaxBObjectToString(response);
            producer.sendModuleResponseMessage(message.getJmsMessage(), responseStr, producer.getModuleName());
        } catch (ActivityModelMarshallException | JMSException | ServiceException e) {
            sendError(message, e);
        }
    }

    private FaReportSourceEnum extractPluginType(PluginType pluginType) {
        if(pluginType == null){
            return FaReportSourceEnum.FLUX;
        }
        return pluginType == PluginType.FLUX ? FaReportSourceEnum.FLUX : FaReportSourceEnum.MANUAL;
    }

    private void sendError(EventMessage message, Exception e) {
        log.error("[ Error in activity module. ] ", e);
        errorEvent.fire(new EventMessage(message.getJmsMessage(), ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Exception in activity [ " + e.getMessage())));
    }

    private Map<ActivityTableType, List<IDType>> collectAllIdsFromMessage(FLUXFAReportMessage faRepMessage) {
        Map<ActivityTableType, List<IDType>> idsmap = new EnumMap<>(ActivityTableType.class);
        idsmap.put(ActivityTableType.RELATED_FLUX_REPORT_DOCUMENT_ENTITY, new ArrayList<IDType>());
        if (faRepMessage == null) {
            return idsmap;
        }
        List<FAReportDocument> faReportDocuments = faRepMessage.getFAReportDocuments();
        if (CollectionUtils.isNotEmpty(faReportDocuments)) {
            for (FAReportDocument faRepDoc : faReportDocuments) {
                FLUXReportDocument relatedFLUXReportDocument = faRepDoc.getRelatedFLUXReportDocument();
                if (relatedFLUXReportDocument != null) {
                    List<IDType> idTypes = new ArrayList<>();
                    idTypes.addAll(relatedFLUXReportDocument.getIDS());
                    idTypes.add(relatedFLUXReportDocument.getReferencedID());
                    idTypes.removeAll(Collections.singletonList(null));
                    idsmap.get(ActivityTableType.RELATED_FLUX_REPORT_DOCUMENT_ENTITY).addAll(idTypes);
                }
            }
        }
        return idsmap;
    }

    private void deleteDuplicatedReportsFromXMLDocument(FLUXFAReportMessage repMsg) {
        GetNonUniqueIdsRequest getNonUniqueIdsRequest = null;
        try {
            getNonUniqueIdsRequest = ActivityModuleRequestMapper.mapToGetNonUniqueIdRequestObject(collectAllIdsFromMessage(repMsg));
        } catch (ActivityModelMarshallException e) {
            log.error("[ERROR] Error while trying to get the unique ids from FaReportDocumentIdentifiers table...");
        }
        GetNonUniqueIdsResponse matchingIdsResponse = matchingIdsService.getMatchingIdsResponse(getNonUniqueIdsRequest.getActivityUniquinessLists());
        List<ActivityUniquinessList> activityUniquinessLists = matchingIdsResponse.getActivityUniquinessLists();
        final List<FAReportDocument> faReportDocuments = repMsg.getFAReportDocuments();
        if(CollectionUtils.isNotEmpty(activityUniquinessLists)){
            for(ActivityUniquinessList unique : activityUniquinessLists){
                deleteBranchesThatMatchWithTheIdsList(unique.getIds(), faReportDocuments);
            }
        }
    }

    private void deleteBranchesThatMatchWithTheIdsList(List<ActivityIDType> ids, List<FAReportDocument> faReportDocuments) {
        final Iterator<FAReportDocument> iterator = faReportDocuments.iterator();
        while(iterator.hasNext()){
            FAReportDocument faRep = iterator.next();
            FLUXReportDocument relatedFLUXReportDocument = faRep.getRelatedFLUXReportDocument();
            if(relatedFLUXReportDocument != null && reportDocumentIdsMatch(relatedFLUXReportDocument.getIDS(), ids)){
                log.warn("[WARN] Deleted FaReportDocument (from XML MSG Node) since it already exist in the Activity DB..\n" +
                        "Following is the ID : { "+preetyPrintIds(relatedFLUXReportDocument.getIDS())+" }");
                iterator.remove();
                log.info("[INFO] Remaining [ "+faReportDocuments.size()+" ] FaReportDocuments to be saved.");
            }
        }
    }

    private String preetyPrintIds(List<IDType> ids) {
        StringBuilder strBuild = new StringBuilder();
        for(IDType id : ids){
            strBuild.append("[ UUID : ").append(id.getValue()).append(" ]\n");
        }
        return strBuild.toString();
    }

    private boolean reportDocumentIdsMatch(List<IDType> ids, List<ActivityIDType> idsToMatch) {
        boolean match = true;
        for(IDType idType : ids){
            if(!idExistsInList(idType, idsToMatch)){
                match = false;
                break;
            }
        }
        return match;
    }

    private boolean idExistsInList(IDType idType, List<ActivityIDType> idsToMatch) {
        boolean match = false;
        final String value = idType.getValue();
        final String schemeID = idType.getSchemeID();
        for(ActivityIDType actId : idsToMatch){
            if(actId.getValue().equals(value) && actId.getIdentifierSchemeId().equals(schemeID)){
                match = true;
            }
        }
        return match;
    }

}