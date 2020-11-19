/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.bean;


import eu.europa.ec.fisheries.ers.service.ActivityRulesModuleService;
import eu.europa.ec.fisheries.ers.service.FaQueryService;
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.ModuleService;
import eu.europa.ec.fisheries.ers.service.exception.ActivityModuleException;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.FaQueryFactory;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.ActivityResponseQueueProducerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.ActivityRulesProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.*;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMapperException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionParameter;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionPermissionAnswer;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionPermissionResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import un.unece.uncefact.data.standard.fluxfaquerymessage._3.FLUXFAQueryMessage;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQuery;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Stateless
@Transactional
@Slf4j
public class ActivityRulesModuleServiceBean extends ModuleService implements ActivityRulesModuleService {

    private static final String FLUX_LOCAL_NATION_CODE = "flux_local_nation_code";

    @EJB
    private ActivityRulesProducerBean rulesProducerBean;

    @EJB
    private ActivityConsumerBean activityConsumerBean;

    @EJB
    private FishingTripService fishingTripService;

    @EJB
    private ActivitySubscriptionPermissionChecker permissionChecker;

    @EJB
    private ActivityResponseQueueProducerBean activityResponseQueueProducer;

    @Inject
    ParameterService parameterService;

    @Inject
    FaQueryService faQueryService;

    @Inject
    SubscriptionReportForwarder subscriptionReportForwarder;

    private String localNodeName;

    @PostConstruct
    public void init() throws RuntimeException {
        try {
            localNodeName = parameterService.getParamValueById(FLUX_LOCAL_NATION_CODE);
        } catch (ConfigServiceException e) {
            log.error("[ERROR] Could no set localNodeName in ActivityRulesModuleServiceBean!");
            throw new RuntimeException("ConfigServiceException thrown: ", e);
        }
    }

    @Override
    public void composeAndSendTripUpdateFaQueryToRules(String tripId) throws ActivityModuleException {
        String sendTo = fishingTripService.getOwnerFluxPartyFromTripId(tripId);
        if (StringUtils.isEmpty(sendTo)) {
            throw new ActivityModuleException("Owner for the provided Trip Id [ " + tripId + " ] was not found, so the Update Trip Query cannot be executed!");
        }
        // TODO : Ask cedric where to get this parameter from! or ask Greg
        boolean consolidated = false;
        FAQuery faQueryForTrip = FaQueryFactory.createFaQueryForTrip(tripId, sendTo, consolidated);
        if (faQueryForTrip == null) {
            throw new ActivityModuleException("Fa Query is null!! Check the [tripId, sendTo] parameters!");
        }
        try {
            SubscriptionPermissionResponse subscriptionPermissionResponse = permissionChecker.checkPermissionForFaQuery(faQueryForTrip);
            if (SubscriptionPermissionAnswer.YES.equals(subscriptionPermissionResponse.getSubscriptionCheck())) {
                final List<SubscriptionParameter> parameters = subscriptionPermissionResponse.getParameters();
                String dataFlow = extractParameterByName(parameters, "DF");
                // TODO : mocking up df value untill subscription is ready
                if (StringUtils.isEmpty(dataFlow)) {
                    dataFlow = "urn:un:unece:uncefact:fisheries:FLUX:FA:EU:2";
                }
                // TODO : END Mocking
                if (StringUtils.isEmpty(dataFlow)) {
                    log.error("[ERROR] Subscription is missing the dataFlow parameter! Cannot send FaQuery! ");
                    throw new ActivityModuleException("Subscription is missing the dataFlow parameter! Cannot send FaQuery!");
                }
                String logId = faQueryForTrip.getID().getValue();
                FLUXFAQueryMessage fluxfaQueryMessage = new FLUXFAQueryMessage(faQueryForTrip);
                final String faqReqStr = JAXBMarshaller.marshallJaxBObjectToString(fluxfaQueryMessage);
                // TODO : change this values (username, senderOrReceiver (Node name?)) when got answer from CEDRIC
                // senderOrReceiver :  From subscription
                // FR : system parameter
                // username : Mock to FLUX
                rulesProducerBean.sendModuleMessage(RulesModuleRequestMapper.createSendFaQueryMessageRequest(faqReqStr, "FLUX", logId,
                        dataFlow, "BEL",null), activityConsumerBean.getDestination());
            } else {
                throw new ActivityModuleException("The FaQuery that was build with the following parameters [" + tripId + "," + sendTo + ",consolidated : " + consolidated + "] doesn't match to any subscription!");
            }
        } catch (MessageException | RulesModelMapperException | ActivityModelMarshallException e) {
            log.error("[ERROR] Error while trying to ActivityRulesModuleService.composeAndSendTripUpdateFaQueryToRules(...)!", e);
            throw new ActivityModuleException("JAXBException or MessageException!", e);
        }
    }

    @Override
    public String composeAndSendVesselFaQueryToRules(CreateAndSendFAQueryForVesselRequest request) throws ActivityModuleException {
        try {
            FAQuery faQuery = FaQueryFactory.createFaQueryWithVesselId(localNodeName, request.getVesselIdentifiers(), request.isConsolidated(), request.getStartDate(), request.getEndDate());
            sendFAQueryToRules(faQuery, request.getDataflow(), request.getReceiver());
            return faQuery.getID().getValue();
        } catch (ActivityModelMarshallException | RulesModelMapperException | MessageException e) {
            log.error("[ERROR] Error while trying to ActivityRulesModuleService.composeAndSendTripFaQueryToRules(...)!", e);
            throw new ActivityModuleException("JAXBException or MessageException!", e);
        }
    }

    @Override
    public String composeAndSendTripFaQueryToRules(CreateAndSendFAQueryForTripRequest request) throws ActivityModuleException {
        try {
            FAQuery faQuery = FaQueryFactory.createFaQueryWithTripId(localNodeName, request.getTripId(), request.isConsolidated());
            sendFAQueryToRules(faQuery, request.getDataflow(), request.getReceiver());
            return faQuery.getID().getValue();
        } catch (ActivityModelMarshallException | RulesModelMapperException | MessageException e) {
            log.error("[ERROR] Error while trying to ActivityRulesModuleService.composeAndSendTripFaQueryToRules(...)!", e);
            throw new ActivityModuleException("JAXBException or MessageException!", e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void retrieveSubscriptionPermissionAndSendToRulesForFaQuery(TextMessage jmsMessage) throws ActivityModuleException {
        try {
            SetFLUXFAReportOrQueryMessageRequest request = JAXBMarshaller.unmarshallTextMessage(jmsMessage, SetFLUXFAReportOrQueryMessageRequest.class);
            FLUXFAQueryMessage fluxFAQueryMessage = JAXBMarshaller.unmarshallTextMessage(request.getRequest(), FLUXFAQueryMessage.class);
            FAQuery faQuery = fluxFAQueryMessage.getFAQuery();

            // fa reports needed in RulesFaQueryServiceBean so keep generating them
            FLUXFAReportMessage faReports = faQueryService.getReportsByCriteria(fluxFAQueryMessage.getFAQuery());

            ForwardQueryToSubscriptionRequest forwardQueryToSubscriptionRequest =
                    faQueryService.getForwardQueryToSubscriptionRequestByFAQuery(fluxFAQueryMessage.getFAQuery());

            SubscriptionPermissionResponse subscriptionPermissionResponse = subscriptionReportForwarder.
                    requestPermissionFromSubscription(forwardQueryToSubscriptionRequest).orElseThrow(() ->
                    new ActivityModuleException("MessageException! could not  define subscription for request " + request));
            SubscriptionPermissionAnswer subscriptionCheck = subscriptionPermissionResponse.getSubscriptionCheck();
            boolean isPermitted = SubscriptionPermissionAnswer.YES.equals(subscriptionCheck);
            enrichResponseAndSend(subscriptionPermissionResponse.getParameters(), faReports, "getTheOnValueFromSomeWhere", jmsMessage.getJMSMessageID(), isPermitted, extractFRValueFromRequest(jmsMessage));

        } catch (ActivityModelMarshallException | ServiceException | JMSException | RulesModelMapperException | MessageException e) {
            log.error("[ERROR] Error while trying to ActivityEventServiceBean.ReceiveFishingActivityRequestEvent(...)!", e);
            throw new ActivityModuleException("JAXBException or MessageException!", e);
        }
    }

    private String extractFRValueFromRequest(TextMessage jmsMessage) throws JMSException {
        return Optional.ofNullable(jmsMessage.getStringProperty("FLUX_FR")).orElse("");
    }

    // Please see @sendSyncAsyncFaReportToRules. It was added for compatibility reasons
    private void enrichResponseAndSend(List<SubscriptionParameter> parameters, FLUXFAReportMessage fLUXFAReportMessage,
                                       String onValue, String jmsMessageCorrId, boolean isPermitted, String senderOrReceiver) throws ActivityModuleException, ActivityModelMarshallException,
            RulesModelMapperException, MessageException {
        String sendFLUXFAReportMessageRequest = null;
        if (isPermitted) {
            String dataFlow = extractParameterByName(parameters, "DF");
            // TODO : mocking up df value untill subscription is ready
            if (StringUtils.isEmpty(dataFlow)) {
                dataFlow = "urn:un:unece:uncefact:fisheries:FLUX:FA:EU:2";
            }
            // TODO : END Mocking
            if (StringUtils.isEmpty(dataFlow)) {
                log.error("[ERROR] Subscription is missing the dataFlow parameter! Cannot send FaQuery! ");
                throw new ActivityModuleException("Subscription is missing the dataFlow parameter! Cannot send FaQuery!");
            }
            String logId = null;
            if (fLUXFAReportMessage.getFLUXReportDocument() != null && CollectionUtils.isNotEmpty(fLUXFAReportMessage.getFLUXReportDocument().getIDS())) {
                logId = fLUXFAReportMessage.getFLUXReportDocument().getIDS().get(0).getValue();
            }
            final String fLUXFAReportMessageString = clearEmptyTags(JAXBMarshaller.marshallJaxBObjectToString(fLUXFAReportMessage));
            // TODO : change this values (username, senderOrReceiver (Node name?)) when got answer from CEDRIC
            sendFLUXFAReportMessageRequest = RulesModuleRequestMapper.createSetFLUXFAQueryMessageRequest(fLUXFAReportMessageString, "FLUX",
                    logId, dataFlow, localNodeName, onValue, isEmptyReportMessage(fLUXFAReportMessage), isPermitted, senderOrReceiver);
        } else {
            final String fLUXFAReportMessageString = clearEmptyTags(JAXBMarshaller.marshallJaxBObjectToString(fLUXFAReportMessage));
            // TODO : change this values (username, senderOrReceiver (Node name?)) when got answer from CEDRIC
            sendFLUXFAReportMessageRequest = RulesModuleRequestMapper.createSetFLUXFAQueryMessageRequest(fLUXFAReportMessageString, "FLUX",
                    null, null, localNodeName, onValue, isEmptyReportMessage(fLUXFAReportMessage), isPermitted, senderOrReceiver);
        }

        activityResponseQueueProducer.sendMessageWithSpecificIds(sendFLUXFAReportMessageRequest,
                activityResponseQueueProducer.getDestination(), null, null, jmsMessageCorrId);
    }

    private void sendFAQueryToRules(FAQuery faQuery, String dataFlow, String receiver) throws ActivityModuleException, ActivityModelMarshallException, RulesModelMapperException, MessageException {
        SubscriptionPermissionResponse subscriptionPermissionResponse = permissionChecker.checkPermissionForFaQuery(faQuery);
        if (SubscriptionPermissionAnswer.YES.equals(subscriptionPermissionResponse.getSubscriptionCheck())) {
            if(StringUtils.isEmpty(dataFlow)) {
                log.error("[ERROR] Subscription is missing the dataFlow parameter! Cannot send FaQuery! ");
                throw new ActivityModuleException("Subscription is missing the dataFlow parameter! Cannot send FaQuery!");
            }
            String logId = faQuery.getID().getValue();
            FLUXFAQueryMessage fluxfaQueryMessage = new FLUXFAQueryMessage(faQuery);
            final String faqReqStr = JAXBMarshaller.marshallJaxBObjectToString(fluxfaQueryMessage);
            rulesProducerBean.sendModuleMessage(RulesModuleRequestMapper.createSendFaQueryMessageRequest(faqReqStr, "FLUX", logId,
                    dataFlow, receiver, receiver), activityConsumerBean.getDestination());
        } else {
            throw new ActivityModuleException("The FaQuery that was built doesn't match to any subscription!");
        }
    }

    @Override
    public void sendSyncAsyncFaReportToRules(FLUXFAReportMessage faReportXMLObj, String onValue, SyncAsyncRequestType type, String jmsMessageCorrId, String senderOrReceiver) throws ActivityModuleException {
        String sendTo = extractOwnerFromFLUXReportDocument(faReportXMLObj.getFLUXReportDocument());
        if (StringUtils.isEmpty(sendTo)) {
            throw new ActivityModuleException("Owner for the provided FLUXFAReportMessage was not found, so FLUXFAReportMessage won't be sent!");
        }
        try {
            SubscriptionPermissionResponse subscriptionPermissionResponse = permissionChecker.checkPermissionForFaReport(faReportXMLObj);
            if (SubscriptionPermissionAnswer.YES.equals(subscriptionPermissionResponse.getSubscriptionCheck())) {
                final List<SubscriptionParameter> parameters = subscriptionPermissionResponse.getParameters();
                String dataFlow = extractParameterByName(parameters, "DF");
                // TODO : mocking up df value untill subscription is ready
                if (StringUtils.isEmpty(dataFlow)) {
                    dataFlow = "urn:un:unece:uncefact:fisheries:FLUX:FA:EU:2";
                }
                // TODO : END Mocking
                if (StringUtils.isEmpty(dataFlow)) {
                    log.error("[ERROR] Subscription is missing the dataFlow parameter! Cannot send FaQuery! ");
                    throw new ActivityModuleException("Subscription is missing the dataFlow parameter! Cannot send FaQuery!");
                }
                String logId = null;
                if(faReportXMLObj.getFLUXReportDocument() != null && CollectionUtils.isNotEmpty(faReportXMLObj.getFLUXReportDocument().getIDS())){
                    logId = faReportXMLObj.getFLUXReportDocument().getIDS().get(0).getValue();
                }
                final String faReportXMLStr = clearEmptyTags(JAXBMarshaller.marshallJaxBObjectToString(faReportXMLObj));
                // TODO : change this values (username, senderOrReceiver (Node name?)) when got answer from CEDRIC
                final String sendFLUXFAReportMessageRequest = RulesModuleRequestMapper.createSendFLUXFAReportMessageRequest(faReportXMLStr, "FLUX",
                        logId, dataFlow, senderOrReceiver, onValue, isEmptyReportMessage(faReportXMLObj));
                if(SyncAsyncRequestType.SYNC.equals(type)){
                    activityResponseQueueProducer.sendMessageWithSpecificIds(sendFLUXFAReportMessageRequest, activityResponseQueueProducer.getDestination(), null, null, jmsMessageCorrId);
                } else {
                    rulesProducerBean.sendModuleMessage(sendFLUXFAReportMessageRequest, activityConsumerBean.getDestination());
                }
            } else {
                throw new ActivityModuleException("Error while trying to prepare the transmission of FaReportMessage!");
            }
        } catch (MessageException | RulesModelMapperException | ActivityModelMarshallException e) {
            log.error("[ERROR] Error while trying to ActivityRulesModuleService.composeAndSendTripUpdateFaQueryToRules(...)!", e);
            throw new ActivityModuleException("JAXBException or MessageException!", e);
        }
    }

    private boolean isEmptyReportMessage(FLUXFAReportMessage faReportXMLObj) {
        return CollectionUtils.isEmpty(faReportXMLObj != null ? faReportXMLObj.getFAReportDocuments() : null);
    }

    private String extractOwnerFromFLUXReportDocument(FLUXReportDocument fluxReportDocument) {
        String owner = null;
        if (fluxReportDocument != null) {
            final FLUXParty ownerFLUXParty = fluxReportDocument.getOwnerFLUXParty();
            if (ownerFLUXParty != null && CollectionUtils.isNotEmpty(ownerFLUXParty.getIDS())) {
                for (IDType idType : ownerFLUXParty.getIDS()) {
                    if ("FLUX_GP_PARTY".equals(idType.getSchemeID())) {
                        owner = idType.getValue();
                        break;
                    }
                }
            }
        }
        return owner;
    }

    private String extractParameterByName(List<SubscriptionParameter> parameters, String paramName) {
        String paramValue = null;
        if (CollectionUtils.isNotEmpty(parameters) && StringUtils.isNoneBlank(paramName)) {
            for (SubscriptionParameter param : parameters) {
                if (paramName.equalsIgnoreCase(param.getName())) {
                    paramValue = param.getValues().get(0);
                    break;
                }
            }
        }
        return paramValue;
    }

    @Override
    public void forwardFluxFAReportMessageToRules(FLUXFAReportMessage message, String dataFlow, String receiver) throws ActivityModuleException {
        try {
            String fluxFAReportMessageString = clearEmptyTags(JAXBMarshaller.marshallJaxBObjectToString(message));
            rulesProducerBean.sendModuleMessage(RulesModuleRequestMapper.createSendFLUXFAReportMessageRequest(fluxFAReportMessageString, "FLUX", message.getFLUXReportDocument().getIDS().get(0).getValue(),
                    dataFlow, receiver, null, false), activityConsumerBean.getDestination());
        } catch (ActivityModelMarshallException | RulesModelMapperException | MessageException e) {
            throw new ActivityModuleException("Exception thrown ", e);
        }
    }

    private String clearEmptyTags(String testSource) {
        testSource = testSource
                .replaceAll("<([a-zA-Z][a-zA-Z0-9]*)[^>]*/>", "") // clear tags like  <udt:IndicatorString/>
                .replaceAll("\n?\\s*<(\\w+)></\\1>", "")
                .replaceAll("<ram:SpecifiedPhysicalFLUXGeographicalCoordinate>\\s*</ram:SpecifiedPhysicalFLUXGeographicalCoordinate>", "")
                .replaceAll("<SpecifiedPhysicalFLUXGeographicalCoordinate>\\s*</SpecifiedPhysicalFLUXGeographicalCoordinate>", "")
                .replaceAll("<ram:ValueIndicator>\\s*</ram:ValueIndicator>","")
                .replaceAll("<ValueIndicator>\\s*</ValueIndicator>","")
                .replaceAll("(?m)^[ \t]*\r?\n", "");// clear empty lines

        return testSource;
    }
}
