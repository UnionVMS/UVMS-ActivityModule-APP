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
import java.util.*;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.service.FluxMessageService;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

@Slf4j
@Stateless
@LocalBean
public class FaReportSaverBean {

    @EJB
    private FluxMessageService fluxMessageService;

    @EJB
    private ActivityMatchingIdsServiceBean matchingIdsService;

    @EJB
    private ExchangeServiceBean exchangeServiceBean;

    public void handleFaReportSaving(SetFLUXFAReportOrQueryMessageRequest request) {

        try {
            FLUXFAReportMessage fluxFAReportMessage = JAXBMarshaller.unmarshallTextMessage(request.getRequest(), FLUXFAReportMessage.class);
            deleteDuplicatedReportsFromXMLDocument(fluxFAReportMessage);
            if(CollectionUtils.isNotEmpty(fluxFAReportMessage.getFAReportDocuments())){
                fluxMessageService.saveFishingActivityReportDocuments(fluxFAReportMessage, extractPluginType(request.getPluginType()));
            } else {
                log.error("[ERROR] After checking faReportDocuments IDs, all of them exist already in Activity DB.. So nothing will be saved!!");
            }
        } catch (Exception e){
            exchangeServiceBean.updateExchangeMessage(request.getExchangeLogGuid(), e);
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


