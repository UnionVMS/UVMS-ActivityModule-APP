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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.europa.ec.fisheries.ers.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.service.FluxMessageService;
import eu.europa.ec.fisheries.ers.service.MdrModuleService;
import eu.europa.ec.fisheries.ers.service.mapper.FluxFaReportMessageMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FluxFaReportMessageMappingContext;
import eu.europa.ec.fisheries.uvms.activity.message.producer.ActivityRulesProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityIDType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityTableType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityUniquinessList;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.PluginType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SetFLUXFAReportOrQueryMessageRequest;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;


@Slf4j
@Stateless
@LocalBean
@Transactional
public class FaReportSaverBean {

    @EJB
    private FluxMessageService fluxMessageService;

    @EJB
    private ActivityMatchingIdsServiceBean matchingIdsService;

    @EJB
    private ExchangeServiceBean exchangeServiceBean;

    @EJB
    private MdrModuleService mdrModuleServiceBean;

    @EJB
    private FishingActivityEnricherBean activityEnricher;

    @EJB
    private ActivityRulesProducerBean activityRulesProducerBean;

    @Inject
    SubscriptionReportForwarder subscriptionReportForwarder;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void handleFaReportSaving(SetFLUXFAReportOrQueryMessageRequest request, String permissionData) {
        mdrModuleServiceBean.loadCache();
        FLUXFAReportMessage fluxFAReportMessage;
        try {
            fluxFAReportMessage = JAXBMarshaller.unmarshallTextMessage(request.getRequest(), FLUXFAReportMessage.class);
        } catch (ActivityModelMarshallException e) {
            log.error("[ERROR] Error while trying to unmarshall FLUXFAReportMessage! Cannot continue, message will be lost!");
            exchangeServiceBean.updateExchangeMessage(request.getExchangeLogGuid(), e);
            return;
        }
        deleteDuplicatedReportsFromXMLDocument(fluxFAReportMessage);
        FluxFaReportMessageMappingContext ctx = new FluxFaReportMessageMappingContext();
        FluxFaReportMessageEntity messageEntity = new FluxFaReportMessageMapper().mapToFluxFaReportMessage(ctx, fluxFAReportMessage, extractPluginType(request.getPluginType()), new FluxFaReportMessageEntity());
        try {
            if(CollectionUtils.isNotEmpty(fluxFAReportMessage.getFAReportDocuments())){
                // Enriches with asset and movement data before saving
                activityEnricher.enrichFaReportDocuments(ctx, messageEntity.getFaReportDocuments());
            } else {
                log.warn("[WARN] After checking faReportDocuments IDs, all of them exist already in Activity DB.. So nothing will be saved!!");
            }
        } catch (Exception e){
            log.error("[ERROR] Error while trying to Enrich faReportDocuments, the saving will continue!");
        }

        if (isRequestPermittedBySubscription(ctx, messageEntity)) {
            try {
                if (CollectionUtils.isNotEmpty(fluxFAReportMessage.getFAReportDocuments())) {
                    // Saves Reports and updates FaReport Corrections Or Cancellations and fish trip start end dates
                    fluxMessageService.saveFishingActivityReportDocuments(messageEntity);
                } else {
                    log.warn("[WARN] After checking faReportDocuments IDs, all of them exist already in Activity DB.. So nothing will be saved!!");
                }
                subscriptionReportForwarder.forwardReportToSubscription(ctx, messageEntity);

                Map<String, String> props = new HashMap<>();
                props.put("isPermitted", "true");
                activityRulesProducerBean.sendMessageToSpecificQueue(permissionData, JMSUtils.lookupQueue("jms/queue/UVMSRulesPermissionEvent"), null, props);
            } catch (Exception e) {
                log.error("[ERROR] Error while trying to FaReportSaverBean.handleFaReportSaving(...). Failed to save it! Going to change the state in exchange log!", e);
                exchangeServiceBean.updateExchangeMessage(request.getExchangeLogGuid(), e);
            }
        } else {
            log.debug("Subscription denied permission for {}", messageEntity.toString());
            try {
                Map<String, String> props = new HashMap<>();
                props.put("isPermitted", "false");
                activityRulesProducerBean.sendMessageToSpecificQueue(permissionData, JMSUtils.lookupQueue("jms/queue/UVMSRulesPermissionEvent"), null, props);
            } catch (MessageException e) {
                log.error("error sending permissions response to rules", e);
            }

        }
    }

    private boolean isRequestPermittedBySubscription(FluxFaReportMessageMappingContext ctx, FluxFaReportMessageEntity messageEntity) {
        try {
            return subscriptionReportForwarder.requestPermissionFromSubscription(ctx, messageEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // by default deny permission in case of an Error/Exception ??
            return false;
        }
    }

    private void deleteDuplicatedReportsFromXMLDocument(FLUXFAReportMessage repMsg) {
        GetNonUniqueIdsRequest getNonUniqueIdsRequest = null;
        try {
            getNonUniqueIdsRequest = ActivityModuleRequestMapper.mapToGetNonUniqueIdRequestObject(collectAllIdsFromMessage(repMsg));
        } catch (ActivityModelMarshallException e) {
            log.error("[ERROR] Error while trying to get the unique ids from FaReportDocumentIdentifiers table...");
        }
        GetNonUniqueIdsResponse matchingIdsResponse = matchingIdsService.getMatchingIdsResponse(getNonUniqueIdsRequest != null ? getNonUniqueIdsRequest.getActivityUniquinessLists() : null);
        List<ActivityUniquinessList> activityUniquinessLists = matchingIdsResponse.getActivityUniquinessLists();
        final List<FAReportDocument> faReportDocuments = repMsg.getFAReportDocuments();
        if(CollectionUtils.isNotEmpty(activityUniquinessLists)){
            for(ActivityUniquinessList unique : activityUniquinessLists){
                deleteBranchesThatMatchWithTheIdsList(unique.getIds(), faReportDocuments);
            }
        }
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
                    List<IDType> idTypes = new ArrayList<>(relatedFLUXReportDocument.getIDS());
                    idTypes.add(relatedFLUXReportDocument.getReferencedID());
                    idTypes.removeAll(Collections.singletonList(null));
                    idsmap.get(ActivityTableType.RELATED_FLUX_REPORT_DOCUMENT_ENTITY).addAll(idTypes);
                }
            }
        }
        return idsmap;
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

    private FaReportSourceEnum extractPluginType(PluginType pluginType) {
        if(pluginType == null){
            return FaReportSourceEnum.FLUX;
        }
        return pluginType == PluginType.FLUX ? FaReportSourceEnum.FLUX : FaReportSourceEnum.MANUAL;
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

    private String preetyPrintIds(List<IDType> ids) {
        StringBuilder strBuild = new StringBuilder();
        for(IDType id : ids){
            strBuild.append("[ UUID : ").append(id.getValue()).append(" ]\n");
        }
        return strBuild.toString();
    }

}


