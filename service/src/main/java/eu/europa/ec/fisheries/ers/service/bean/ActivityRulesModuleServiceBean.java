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
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.ModuleService;
import eu.europa.ec.fisheries.ers.service.exception.ActivityModuleException;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.FaQueryFactory;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.ActivityRulesProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMapperException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionParameter;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionPermissionAnswer;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionPermissionResponse;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import un.unece.uncefact.data.standard.fluxfaquerymessage._3.FLUXFAQueryMessage;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQuery;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

@Stateless
@Transactional
@Slf4j
public class ActivityRulesModuleServiceBean extends ModuleService implements ActivityRulesModuleService {

    @EJB
    private ActivityRulesProducerBean rulesProducerBean;

    @EJB
    private ActivityConsumerBean activityConsumerBean;

    @EJB
    private FishingTripService fishingTripService;

    @EJB
    private ActivitySubscriptionPermissionChecker permissionChecker;

    @Override
    public void composeAndSendTripUpdateFaQueryToRules(String tripId) throws ActivityModuleException {
        String sendTo = fishingTripService.getOwnerFluxPartyFromTripId(tripId);
        if (StringUtils.isEmpty(sendTo)) {
            throw new ActivityModuleException("Owner for the provided Trip Id [ " + tripId + " ] was not found, so the Update Trip Query cannot be executed!");
        }
        // TODO : Ask cedric where to get this parameter from!
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
                rulesProducerBean.sendModuleMessage(RulesModuleRequestMapper.createSendFaQueryMessageRequest(faqReqStr, "FLUX", logId,
                        dataFlow, "BEL"), activityConsumerBean.getDestination());
            } else {
                throw new ActivityModuleException("The FaQuery that was build with the following parameters [" + tripId + "," + sendTo + ",consolidated : " + consolidated + "] doesn't match to any subscription!");
            }
        } catch (MessageException | RulesModelMapperException | ActivityModelMarshallException e) {
            log.error("[ERROR] Error while trying to ActivityRulesModuleService.composeAndSendTripUpdateFaQueryToRules(...)!", e);
            throw new ActivityModuleException("JAXBException or MessageException!", e);
        }
    }

    @Override
    public void sendFaReportToRules(FLUXFAReportMessage faReportXML, String onValue) throws ActivityModuleException {
        String sendTo = extractOwnerFromFLUXReportDocument(faReportXML.getFLUXReportDocument());
        if (StringUtils.isEmpty(sendTo)) {
            throw new ActivityModuleException("Owner for the provided FLUXFAReportMessage was not found, so FLUXFAReportMessage won't be sent!");
        }
        try {
            SubscriptionPermissionResponse subscriptionPermissionResponse = permissionChecker.checkPermissionForFaReport(faReportXML);
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
                String logId = faReportXML.getFLUXReportDocument().getIDS().get(0).getValue();
                final String faReportXMLStr = JAXBMarshaller.marshallJaxBObjectToString(faReportXML);
                // TODO : change this values (username, senderOrReceiver (Node name?)) when got answer from CEDRIC
                rulesProducerBean.sendModuleMessage(RulesModuleRequestMapper.createSendFLUXFAReportMessageRequest(faReportXMLStr, "FLUX", logId,
                        dataFlow, "BEL", onValue), activityConsumerBean.getDestination());
            } else {
                throw new ActivityModuleException("Error while trying to prepare the transmission of FaReportMessage!");
            }
        } catch (MessageException | RulesModelMapperException | ActivityModelMarshallException e) {
            log.error("[ERROR] Error while trying to ActivityRulesModuleService.composeAndSendTripUpdateFaQueryToRules(...)!", e);
            throw new ActivityModuleException("JAXBException or MessageException!", e);
        }
    }

    public String extractOwnerFromFLUXReportDocument(FLUXReportDocument fluxReportDocument) {
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
}
