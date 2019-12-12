/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityIDType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityTableType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.PluginType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SetFLUXFAReportOrQueryMessageRequest;
import eu.europa.ec.fisheries.uvms.activity.service.FluxMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Stateless
@LocalBean
public class FluxReportMessageSaver {

    @EJB
    private FluxMessageService fluxMessageService;

    @EJB
    private ActivityMatchingIdsService matchingIdsService;

    @EJB
    private ExchangeServiceBean exchangeServiceBean;

    public void saveFluxReportMessage(SetFLUXFAReportOrQueryMessageRequest request) {
        try {
            FLUXFAReportMessage fluxFAReportMessage = JAXBMarshaller.unmarshallTextMessage(request.getRequest(), FLUXFAReportMessage.class);
            deleteDuplicatedReportsFromXMLDocument(fluxFAReportMessage);
            if (CollectionUtils.isNotEmpty(fluxFAReportMessage.getFAReportDocuments())) {
                fluxMessageService.saveFishingActivityReportDocuments(fluxFAReportMessage, extractPluginType(request.getPluginType()));
            } else {
                log.error("After checking faReportDocuments IDs, all of them exist already in Activity DB. So nothing will be saved!!");
            }
        } catch (Exception e) {
            log.error("Failed to save FLUXFAReportMessage", e);
            exchangeServiceBean.updateExchangeMessage(request.getExchangeLogGuid(), e);
        }
    }

    private void deleteDuplicatedReportsFromXMLDocument(FLUXFAReportMessage fluxfaReportMessage) {
        List<ActivityIDType> documentIdList = collectAllIdsFromMessage(fluxfaReportMessage);

        List<ActivityIDType> matchingIds = matchingIdsService.getMatchingIds(documentIdList, ActivityTableType.RELATED_FLUX_REPORT_DOCUMENT_ENTITY);
        List<FAReportDocument> faReportDocuments = fluxfaReportMessage.getFAReportDocuments();
        deleteBranchesThatMatchWithTheIdsList(matchingIds, faReportDocuments);
    }

    private List<ActivityIDType> collectAllIdsFromMessage(FLUXFAReportMessage fluxfaReportMessage) {
        List<IDType> fluxFaReportMessageDocumentIdList = new ArrayList<>();

        if (fluxfaReportMessage == null) {
            return Collections.emptyList();
        }

        List<FAReportDocument> faReportDocuments = fluxfaReportMessage.getFAReportDocuments();
        for (FAReportDocument faReportDocument : faReportDocuments) {
            FLUXReportDocument relatedFLUXReportDocument = faReportDocument.getRelatedFLUXReportDocument();
            if (relatedFLUXReportDocument != null) {
                List<IDType> idTypes = new ArrayList<>(relatedFLUXReportDocument.getIDS());
                idTypes.add(relatedFLUXReportDocument.getReferencedID());
                idTypes.removeAll(Collections.singletonList(null));

                fluxFaReportMessageDocumentIdList.addAll(idTypes);
            }
        }

        return fluxFaReportMessageDocumentIdList
                .stream()
                .map(idType -> new ActivityIDType(idType.getValue(), idType.getSchemeID()))
                .collect(Collectors.toList());
    }

    private void deleteBranchesThatMatchWithTheIdsList(List<ActivityIDType> ids, List<FAReportDocument> faReportDocuments) {
        Iterator<FAReportDocument> iterator = faReportDocuments.iterator();
        while (iterator.hasNext()) {
            FAReportDocument faReportDocument = iterator.next();
            FLUXReportDocument relatedFLUXReportDocument = faReportDocument.getRelatedFLUXReportDocument();
            if (relatedFLUXReportDocument != null && reportDocumentIdsMatch(relatedFLUXReportDocument.getIDS(), ids)) {
                log.warn("Deleted FaReportDocument with id {} (from XML MSG Node) since it already exist in the Activity DB.", prettyPrintIdList(relatedFLUXReportDocument.getIDS()));
                iterator.remove();
                log.info("Remaining [{}] FaReportDocuments to be saved.", faReportDocuments.size());
            }
        }
    }

    private FaReportSourceEnum extractPluginType(PluginType pluginType) {
        if (pluginType == null) {
            return FaReportSourceEnum.FLUX;
        }
        return pluginType == PluginType.FLUX ? FaReportSourceEnum.FLUX : FaReportSourceEnum.MANUAL;
    }

    private boolean reportDocumentIdsMatch(List<IDType> ids, List<ActivityIDType> idsToMatch) {
        boolean match = true;
        for (IDType idType : ids) {
            if (!idExistsInList(idType, idsToMatch)) {
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
        for (ActivityIDType actId : idsToMatch) {
            if (actId.getValue().equals(value) && actId.getIdentifierSchemeId().equals(schemeID)) {
                match = true;
            }
        }
        return match;
    }

    private String prettyPrintIdList(List<IDType> ids) {
        StringBuilder strBuild = new StringBuilder();
        for (IDType id : ids) {
            strBuild.append("[ UUID : ").append(id.getValue()).append(" ]\n");
        }
        return strBuild.toString();
    }
}


