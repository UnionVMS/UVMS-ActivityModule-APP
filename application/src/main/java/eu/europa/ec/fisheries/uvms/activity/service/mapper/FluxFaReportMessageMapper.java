
/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.uvms.activity.service.FishingTripCache;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FluxFaReportMessageMapper extends BaseMapper {

    @Inject
    FaReportDocumentMapper faReportDocumentMapper;

    @Inject
    FluxFaReportMessageMapper fluxFaReportMessageMapperImpl;

    @Mapping(target = "fluxReportDocument_Id", expression = "java(singleIDTypeValue(fluxFaReportMessage.getFLUXReportDocument().getIDS()))")
    @Mapping(target = "fluxReportDocument_IdSchemeId", expression = "java(singleIDTypeSchemeID(fluxFaReportMessage.getFLUXReportDocument().getIDS()))")

    @Mapping(target = "fluxReportDocument_ReferencedFaQueryMessageId", source = "FLUXReportDocument.referencedID.value")
    @Mapping(target = "fluxReportDocument_ReferencedFaQueryMessageSchemeId", source = "FLUXReportDocument.referencedID.schemeID")
    @Mapping(target = "fluxReportDocument_CreationDatetime", source = "FLUXReportDocument.creationDateTime.dateTime")
    @Mapping(target = "fluxReportDocument_PurposeCode", source = "FLUXReportDocument.purposeCode.value")
    @Mapping(target = "fluxReportDocument_PurposeCodeListId", source = "FLUXReportDocument.purposeCode.listID")
    @Mapping(target = "fluxReportDocument_Purpose", source = "FLUXReportDocument.purpose.value")

    @Mapping(target = "fluxParty_identifier", source = "FLUXReportDocument.ownerFLUXParty.IDS", qualifiedByName = "singleIDTypeValue")
    @Mapping(target = "fluxParty_schemeId", source = "FLUXReportDocument.ownerFLUXParty.IDS", qualifiedByName = "singleIDTypeSchemeID")
    @Mapping(target = "fluxParty_name", source = "FLUXReportDocument.ownerFLUXParty.names", qualifiedByName = "singleTextTypeValue")
    @Mapping(target = "fluxParty_nameLanguageId", source = "FLUXReportDocument.ownerFLUXParty.names", qualifiedByName = "singleTextTypeLanguageId")

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "faReportDocuments", ignore = true)
    public abstract FluxFaReportMessageEntity mapButExcludeFaReportDocuments(FLUXFAReportMessage fluxFaReportMessage);

    public FluxFaReportMessageEntity mapToFluxFaReportMessage(FLUXFAReportMessage fluxFaReportMessage, FaReportSourceEnum faReportSourceEnum) {
        if (fluxFaReportMessage == null || faReportSourceEnum == null) {
            return null;
        }

        FluxFaReportMessageEntity fluxFaReportMessageEntity = fluxFaReportMessageMapperImpl.mapButExcludeFaReportDocuments(fluxFaReportMessage);

        Set<FaReportDocumentEntity> faReportDocuments = mapFaReportDocuments(fluxFaReportMessage.getFAReportDocuments(), faReportSourceEnum, fluxFaReportMessageEntity);
        fluxFaReportMessageEntity.setFaReportDocuments(faReportDocuments);
        return fluxFaReportMessageEntity;

    }

    private Set<FaReportDocumentEntity> mapFaReportDocuments(List<FAReportDocument> faReportDocuments, FaReportSourceEnum faReportSourceEnum, FluxFaReportMessageEntity fluxFaReportMessage) {
        FishingTripCache fishingTripCache = new FishingTripCache();

        Set<FaReportDocumentEntity> faReportDocumentEntities = new HashSet<>();
        for (FAReportDocument faReportDocument : faReportDocuments) {
            FaReportDocumentEntity entity = faReportDocumentMapper.mapToFAReportDocumentEntity(faReportDocument, faReportSourceEnum);
            VesselTransportMeans specifiedVesselTransportMeans = faReportDocument.getSpecifiedVesselTransportMeans();
            Set<VesselTransportMeansEntity> vesselTransportMeansEntities = faReportDocumentMapper.mapVesselTransportMeansEntity(specifiedVesselTransportMeans, entity);
            Set<FishingActivityEntity> fishingActivityEntities = new HashSet<>();

            if (CollectionUtils.isNotEmpty(vesselTransportMeansEntities)) {
                VesselTransportMeansEntity vessTraspMeans = vesselTransportMeansEntities.iterator().next();
                fishingActivityEntities = faReportDocumentMapper.mapFishingActivityEntities(faReportDocument.getSpecifiedFishingActivities(), entity, vessTraspMeans, fishingTripCache);
                vessTraspMeans.setFaReportDocument(entity);
            }

            entity.setFishingActivities(fishingActivityEntities);
            entity.setFluxFaReportMessage(fluxFaReportMessage);
            entity.setVesselTransportMeans(vesselTransportMeansEntities);
            faReportDocumentEntities.add(entity);
        }
        return faReportDocumentEntities;
    }
}
