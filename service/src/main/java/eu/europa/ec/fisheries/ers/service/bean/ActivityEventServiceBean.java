/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.bean;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;
import java.util.List;

import eu.europa.ec.fisheries.ers.service.ActivityRulesModuleService;
import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.ers.service.EventService;
import eu.europa.ec.fisheries.ers.service.FaCatchReportService;
import eu.europa.ec.fisheries.ers.service.FaQueryService;
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.exception.ActivityModuleException;
import eu.europa.ec.fisheries.ers.service.facatch.FACatchSummaryHelper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityRequestMapper;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityErrorMessageServiceBean;
import eu.europa.ec.fisheries.uvms.activity.message.event.ActivityMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.CreateAndSendFAQueryForTripEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.CreateAndSendFAQueryForVesselEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.CreateAndSendGetAttachmentsForGuidAndQueryPeriodEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.FindMovementGuidsByReportIdsAndAssetGuid;
import eu.europa.ec.fisheries.uvms.activity.message.event.ForwardFAReportFromPosition;
import eu.europa.ec.fisheries.uvms.activity.message.event.ForwardFAReportWithLogbook;
import eu.europa.ec.fisheries.uvms.activity.message.event.ForwardMultipleFAReports;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFACatchSummaryReportEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFishingActivityForTripsRequestEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFishingTripListEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetNonUniqueIdsRequestEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.ReceiveFishingActivityRequestEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.message.producer.ActivityResponseQueueProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.FaultCode;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityReportGenerationResults;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.AttachmentResponseObject;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.CreateAndSendFAQueryForTripRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.CreateAndSendFAQueryForVesselRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FindMovementGuidsByReportIdsAndAssetGuidRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FindMovementGuidsByReportIdsAndAssetGuidResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ForwardFAReportFromPositionRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetAttachmentsForGuidAndQueryPeriodRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetAttachmentsForGuidAndQueryPeriodResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ForwardFAReportWithLogbookRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ForwardMultipleFAReportsRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetFishingActivitiesForTripRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetFishingActivitiesForTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SetFLUXFAReportOrQueryMessageRequest;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.wsdl.subscription.module.ActivityReportGenerationResultsRequest;
import eu.europa.ec.fisheries.wsdl.subscription.module.AttachmentType;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionModuleMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.fluxfaquerymessage._3.FLUXFAQueryMessage;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

@LocalBean
@Stateless
@Slf4j
public class ActivityEventServiceBean implements EventService {

    private static final String GOT_JMS_INSIDE_ACTIVITY_TO_GET = "\n\nGot JMS inside Activity to get ";

    private static final String FLUX_LOCAL_NATION_CODE = "flux_local_nation_code";

    @Inject
    @ActivityMessageErrorEvent
    private Event<EventMessage> errorEvent;

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
    private ActivityRulesModuleService activityRulesModuleServiceBean;

    @EJB
    private FaReportSaverBean saveReportBean;

    @Inject
    private ActivityResponseQueueProducerBean activityResponseQueueProducerBean;

    @Inject
    private FishingActivityService  fishingActivityService;
    
    @Inject
    private FaQueryService faQueryService;

    @Override
    public void receiveFishingActivityMessage(@Observes @ReceiveFishingActivityRequestEvent EventMessage eventMessage) {
        log.info(GOT_JMS_INSIDE_ACTIVITY_TO_GET + "SetFLUXFAReportOrQueryMessageRequest");
        try {
            TextMessage jmsMessage = eventMessage.getJmsMessage();
            SetFLUXFAReportOrQueryMessageRequest request = JAXBMarshaller.unmarshallTextMessage(jmsMessage, SetFLUXFAReportOrQueryMessageRequest.class);
            log.info("SetFLUXFAReportOrQueryMessageRequest unmarshalled");
            if(request == null){
                log.error("Unmarshalled SetFLUXFAReportOrQueryMessageRequest is null. Something went wrong during jms comm?!");
                return;
            }
            switch (eventMessage.getMethod()) {
                case GET_FLUX_FA_REPORT :
//                    PermissionData permissionData = JAXBUtils.unMarshallMessage(jmsMessage.getStringProperty("context"), PermissionData.class);
                    String permissionData = jmsMessage.getStringProperty("context");
                    saveReportBean.handleFaReportSaving(request, permissionData);
                    break;
                case GET_FLUX_FA_QUERY:
                    FLUXFAQueryMessage fluxFAQueryMessage = JAXBMarshaller.unmarshallTextMessage(request.getRequest(), FLUXFAQueryMessage.class);
                    FLUXFAReportMessage faReports = faQueryService.getReportsByCriteria(fluxFAQueryMessage.getFAQuery());
                    activityRulesModuleServiceBean.sendSyncAsyncFaReportToRules(faReports, "getTheOnValueFromSomewahre", request.getRequestType(), jmsMessage.getJMSMessageID());
                    break;
            }
        } catch (ActivityModelMarshallException | JMSException | ActivityModuleException e) {
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
            producer.sendResponseMessageToSender(message.getJmsMessage(), response, 3_600_000L);
            log.debug("Response sent back.");
        } catch (ActivityModelMarshallException | JMSException | ServiceException | MessageException e) {
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
            producer.sendResponseMessageToSender(message.getJmsMessage(), response);
        } catch (ActivityModelMarshallException | JMSException | ServiceException | MessageException e) {
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
            producer.sendResponseMessageToSender(message.getJmsMessage(), response);
        } catch (ActivityModelMarshallException | JMSException | MessageException e) {
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
            producer.sendResponseMessageToSender(message.getJmsMessage(), responseStr);
        } catch (ActivityModelMarshallException | JMSException | ServiceException | MessageException e) {
            sendError(message, e);
        }
    }

    @Override
    public void createAndSendFAQueryForVessel(@Observes @CreateAndSendFAQueryForVesselEvent EventMessage message) {
        try{
            CreateAndSendFAQueryForVesselRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), CreateAndSendFAQueryForVesselRequest.class);
            String messageId = activityRulesModuleServiceBean.composeAndSendVesselFaQueryToRules(request);
            activityResponseQueueProducerBean.sendResponseMessageToSender(message.getJmsMessage(), ActivityModuleResponseMapper.mapToCreateAndSendFAQueryResponse(messageId));
        } catch (ActivityModelMarshallException | ActivityModuleException | MessageException e) {
            sendError(message, e);
        }
    }

    @Override
    public void createAndSendFAQueryForTrip(@Observes @CreateAndSendFAQueryForTripEvent EventMessage message) {
        try{
            CreateAndSendFAQueryForTripRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), CreateAndSendFAQueryForTripRequest.class);
            String messageId = activityRulesModuleServiceBean.composeAndSendTripFaQueryToRules(request);
            activityResponseQueueProducerBean.sendResponseMessageToSender(message.getJmsMessage(), ActivityModuleResponseMapper.mapToCreateAndSendFAQueryResponse(messageId));
        } catch (ActivityModelMarshallException | ActivityModuleException | MessageException e) {
            sendError(message, e);
        }
    }

    @Override
    public void createAndSendGetAttachmentsForGuidAndQueryPeriod(@Observes @CreateAndSendGetAttachmentsForGuidAndQueryPeriodEvent EventMessage message){
        try{
            GetAttachmentsForGuidAndQueryPeriodRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), GetAttachmentsForGuidAndQueryPeriodRequest.class);
            List<AttachmentResponseObject> responseObjects = fishingTripService.getAttachmentsForGuidAndPeriod(request.getQuery());
            activityResponseQueueProducerBean.sendResponseMessageToSender(message.getJmsMessage(), JAXBMarshaller.marshallJaxBObjectToString(new GetAttachmentsForGuidAndQueryPeriodResponse(responseObjects)));
        } catch (ActivityModelMarshallException | ServiceException | MessageException e) {
            sendError(message, e);
        }
    }

    @Override
    public void forwardMultipleFAReports(@Observes @ForwardMultipleFAReports EventMessage message) {
        try{
            ForwardMultipleFAReportsRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), ForwardMultipleFAReportsRequest.class);
            ActivityReportGenerationResults response = fishingTripService.forwardMultipleFaReports(request);
            ActivityReportGenerationResultsRequest requestToSubscription = makeActivityReportGenerationResultsRequest(response);
            activityResponseQueueProducerBean.sendResponseMessageToSender(message.getJmsMessage(), JAXBMarshaller.marshallJaxBObjectToString(requestToSubscription));
        } catch (ActivityModelMarshallException | MessageException | ServiceException e) {
            sendError(message, e);
        }
    }

    @Override
    public void forwardFAReportWithLogbook(@Observes @ForwardFAReportWithLogbook EventMessage message) {
        try{
            ForwardFAReportWithLogbookRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), ForwardFAReportWithLogbookRequest.class);
            ActivityReportGenerationResults response = fishingTripService.forwardFaReportWithLogbook(request);
            ActivityReportGenerationResultsRequest requestToSubscription = makeActivityReportGenerationResultsRequest(response);
            activityResponseQueueProducerBean.sendResponseMessageToSender(message.getJmsMessage(), JAXBMarshaller.marshallJaxBObjectToString(requestToSubscription));
        } catch (ActivityModelMarshallException | MessageException | ServiceException e) {
            sendError(message, e);
        }
    }

    @Override
    public void forwardFAReportFromPosition(@Observes @ForwardFAReportFromPosition EventMessage message) {
        try{
            ForwardFAReportFromPositionRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), ForwardFAReportFromPositionRequest.class);
            ActivityReportGenerationResults response = fishingTripService.forwardFAReportFromPosition(request);
            ActivityReportGenerationResultsRequest requestToSubscription = makeActivityReportGenerationResultsRequest(response);
            activityResponseQueueProducerBean.sendResponseMessageToSender(message.getJmsMessage(), JAXBMarshaller.marshallJaxBObjectToString(requestToSubscription));
        } catch (ActivityModelMarshallException | MessageException | ServiceException e) {
            sendError(message, e);
        }
    }

    @Override
    public void findMovementGuidsByIdentifierIdsAssetGuid(@Observes @FindMovementGuidsByReportIdsAndAssetGuid EventMessage message) {
        try{
            FindMovementGuidsByReportIdsAndAssetGuidRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), FindMovementGuidsByReportIdsAndAssetGuidRequest.class);
            List<String> movementGuidList = fishingActivityService.findMovementGuidsByIdentifierIdsAndAssetGuid(request.getReportIds(),request.getAssetGuid());
            FindMovementGuidsByReportIdsAndAssetGuidResponse response = new FindMovementGuidsByReportIdsAndAssetGuidResponse();
            response.setMovementGuids(movementGuidList);
            producer.sendResponseMessageToSender(message.getJmsMessage(), JAXBMarshaller.marshallJaxBObjectToString(response));
        } catch (ActivityModelMarshallException | MessageException | ServiceException e) {
            sendError(message, e);
        }
    }

    private ActivityReportGenerationResultsRequest makeActivityReportGenerationResultsRequest(ActivityReportGenerationResults response) {
        ActivityReportGenerationResultsRequest requestToSubscription = new ActivityReportGenerationResultsRequest();
        requestToSubscription.setMethod(SubscriptionModuleMethod.ACTIVITY_REPORT_GENERATION_RESULTS_REQUEST);
        requestToSubscription.setExecutionId(response.getExecutionId());
        requestToSubscription.setMessageId(response.getMessageId());
        response.getResponseLists().stream()
                .map(attachment -> {
                    eu.europa.ec.fisheries.wsdl.subscription.module.AttachmentResponseObject attachmentResponseObject = new eu.europa.ec.fisheries.wsdl.subscription.module.AttachmentResponseObject();
                    attachmentResponseObject.setContent(attachment.getContent());
                    attachmentResponseObject.setTripId(attachment.getTripId());
                    attachmentResponseObject.setType(AttachmentType.fromValue(attachment.getType().value()));
                    return attachmentResponseObject;
                })
                .forEach(requestToSubscription.getAttachments()::add);
        return requestToSubscription;
    }

    private void sendError(EventMessage message, Exception e) {
        log.error("[ Error in activity module. ] ", e);
        errorEvent.fire(new EventMessage(message.getJmsMessage(), ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Exception in activity [ " + e.getMessage())));
    }
}
