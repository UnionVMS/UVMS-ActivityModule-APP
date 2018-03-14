/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.service.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.RelatedReportDto;
import eu.europa.ec.fisheries.ers.service.dto.view.ReportDocumentDto;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingActivity;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

@Mapper(uses = {VesselTransportMeansMapper.class, FishingActivityMapper.class})
public abstract class FaReportDocumentMapper extends BaseMapper {

    public static final FaReportDocumentMapper INSTANCE = Mappers.getMapper(FaReportDocumentMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "faReportDocument.typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "faReportDocument.typeCode.listID"),
            @Mapping(target = "acceptedDatetime", source = "faReportDocument.acceptanceDateTime.dateTime"),
            @Mapping(target = "fmcMarker", source = "faReportDocument.FMCMarkerCode.value"),
            @Mapping(target = "fmcMarkerListId", source = "faReportDocument.FMCMarkerCode.listID"),
            @Mapping(target = "status", constant = "new"),
            @Mapping(target = "source", source = "faReportSourceEnum.sourceType"),
            @Mapping(target = "vesselTransportMeans", expression = "java(getVesselTransportMeansEntity(faReportDocument.getSpecifiedVesselTransportMeans(), faReportDocumentEntity))"),
            @Mapping(target = "fluxReportDocument", expression = "java(getFluxReportDocument(faReportDocument.getRelatedFLUXReportDocument(), faReportDocumentEntity))"),
            @Mapping(target = "faReportIdentifiers", expression = "java(mapToFAReportIdentifierEntities(faReportDocument.getRelatedReportIDs(), faReportDocumentEntity))"),
            @Mapping(target = "fishingActivities", expression = "java(getFishingActivityEntities(faReportDocument.getSpecifiedFishingActivities(),faReportDocumentEntity))")
    })
    public abstract FaReportDocumentEntity mapToFAReportDocumentEntity(FAReportDocument faReportDocument, FaReportSourceEnum faReportSourceEnum);

    @Mappings({
            @Mapping(target = "correctionType", source = "status"),
            @Mapping(target = "creationDate", source = "fluxReportDocument.creationDatetime"),
            @Mapping(target = "acceptedDate", source = "acceptedDatetime"),
            @Mapping(target = "faReportIdentifiers", expression = "java(getReportIdMap(faReportDocumentEntity.getFluxReportDocument().getFluxReportIdentifiers()))"),
            @Mapping(target = "ownerFluxPartyName", source = "fluxReportDocument.fluxParty.fluxPartyName"),
            @Mapping(target = "purposeCode", source = "fluxReportDocument.purposeCode")
    })
    public abstract FaReportCorrectionDTO mapToFaReportCorrectionDto(FaReportDocumentEntity faReportDocumentEntity);

    public abstract List<FaReportCorrectionDTO> mapToFaReportCorrectionDtoList(List<FaReportDocumentEntity> faReportDocumentEntities);

    @Mappings({
            @Mapping(target = "faReportIdentifierId", expression = "java(getIdType(idType))"),
            @Mapping(target = "faReportIdentifierSchemeId", expression = "java(getIdTypeSchemaId(idType))")
    })
    protected abstract FaReportIdentifierEntity mapToFAReportIdentifierEntity(IDType idType);

    protected Set<VesselTransportMeansEntity> getVesselTransportMeansEntity(VesselTransportMeans vesselTransportMeans, FaReportDocumentEntity faReportDocumentEntity) {
        if (vesselTransportMeans == null) {
            return null;
        }
        Set<VesselTransportMeansEntity> entities = new HashSet<>();
        VesselTransportMeansEntity vesselTransportMeansEntity = VesselTransportMeansMapper.INSTANCE.mapToVesselTransportMeansEntity(vesselTransportMeans);
        vesselTransportMeansEntity.setFaReportDocument(faReportDocumentEntity);
        entities.add(vesselTransportMeansEntity);
        return entities;
    }

    protected Set<FishingActivityEntity> getFishingActivityEntities(List<FishingActivity> fishingActivities, FaReportDocumentEntity faReportDocumentEntity) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return Collections.emptySet();
        }
        Set<FishingActivityEntity> fishingActivityEntities =  new HashSet<>();
        for (FishingActivity fishingActivity : fishingActivities) {
            FishingActivityEntity fishingActivityEntity = FishingActivityMapper.INSTANCE.mapToFishingActivityEntity(fishingActivity, faReportDocumentEntity, new FishingActivityEntity());
            fishingActivityEntities.add(fishingActivityEntity);
            if (fishingActivityEntity.getAllRelatedFishingActivities() != null && !fishingActivityEntity.getAllRelatedFishingActivities().isEmpty()) {
                fishingActivityEntities.addAll(fishingActivityEntity.getAllRelatedFishingActivities());
            }
        }
        return fishingActivityEntities;
    }

    protected FluxReportDocumentEntity getFluxReportDocument(FLUXReportDocument fluxReportDocument, FaReportDocumentEntity faReportDocumentEntity) {
        if (fluxReportDocument == null) {
            return null;
        }
        FluxReportDocumentEntity fluxReportDocumentEntity = FluxReportDocumentMapper.INSTANCE.mapToFluxReportDocumentEntity(fluxReportDocument);
        fluxReportDocumentEntity.setFaReportDocument(faReportDocumentEntity);
        return fluxReportDocumentEntity;
    }

    protected Set<FaReportIdentifierEntity> mapToFAReportIdentifierEntities(List<IDType> idTypes, FaReportDocumentEntity faReportDocumentEntity) {
        if (CollectionUtils.isEmpty(idTypes)) {
            return Collections.emptySet();
        }
        Set<FaReportIdentifierEntity> faReportIdentifierEntities = new HashSet<>();
        for (IDType idType : idTypes) {
            FaReportIdentifierEntity faReportIdentifierEntity = FaReportDocumentMapper.INSTANCE.mapToFAReportIdentifierEntity(idType);
            faReportIdentifierEntity.setFaReportDocument(faReportDocumentEntity);
            faReportIdentifierEntities.add(faReportIdentifierEntity);
        }
        return faReportIdentifierEntities;
    }

    @Mappings({
            @Mapping(target = "type" , source = "typeCode"),
            @Mapping(target = "acceptedDate" , source = "acceptedDatetime", dateFormat = DateUtils.DATE_TIME_UI_FORMAT),
            @Mapping(target = "creationDate" , source = "fluxReportDocument.creationDatetime", dateFormat = DateUtils.DATE_TIME_UI_FORMAT),
            @Mapping(target = "owner", expression = "java(faReportDocument.getFluxReportDocument().getFluxPartyIdentifierBySchemeId(\"FLUX_GP_PARTY\"))"),
            @Mapping(target = "refId" , source = "fluxReportDocument.referenceId"),
            @Mapping(target = "purposeCode" , source = "fluxReportDocument.purposeCode"),
            @Mapping(target = "fmcMark" , source = "fmcMarker"),
            @Mapping(target = "relatedReports" , source = "faReportIdentifiers"),
            @Mapping(target = "id", expression = "java(faReportDocument.getFluxReportDocument().getFluxPartyIdentifierBySchemeId(\"UUID\"))"),
    })
    public abstract ReportDocumentDto mapFaReportDocumentToReportDocumentDto(FaReportDocumentEntity faReportDocument);

    @Mappings({
            @Mapping(target = "id" , source = "faReportIdentifierId"),
            @Mapping(target = "schemeId" , source = "faReportIdentifierSchemeId"),
    })
    public abstract RelatedReportDto mapFaReportDocumentEntityToRelatedReportDto(FaReportIdentifierEntity entity);


}