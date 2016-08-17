/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusEnum;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.FaReportDocumentDetailsDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FLUXReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FishingActivity;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 5/13/2016.
 */
@Mapper(uses = {VesselTransportMeansMapper.class})
public abstract class FaReportDocumentMapper extends BaseMapper {

    public static FaReportDocumentMapper INSTANCE = Mappers.getMapper(FaReportDocumentMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(faReportDocument.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(faReportDocument.getTypeCode()))"),
            @Mapping(target = "acceptedDatetime", expression = "java(convertToDate(faReportDocument.getAcceptanceDateTime()))"),
            @Mapping(target = "fmcMarker", expression = "java(getCodeType(faReportDocument.getFMCMarkerCode()))"),
            @Mapping(target = "fmcMarkerListId", expression = "java(getCodeTypeListId(faReportDocument.getFMCMarkerCode()))"),
            @Mapping(target = "vesselTransportMeans", expression = "java(getVesselTransportMeansEntity(faReportDocument.getSpecifiedVesselTransportMeans(), faReportDocumentEntity))"),
            @Mapping(target = "fluxReportDocument", expression = "java(getFluxReportDocument(faReportDocument.getRelatedFLUXReportDocument(), faReportDocumentEntity))"),
            @Mapping(target = "faReportIdentifiers", expression = "java(mapToFAReportIdentifierEntities(faReportDocument.getRelatedReportIDs(), faReportDocumentEntity))"),
            @Mapping(target = "fishingActivities", expression = "java(getFishingActivityEntities(faReportDocument.getSpecifiedFishingActivities(),faReportDocumentEntity))"),
            @Mapping(target = "status", expression = "java(setStatusAsNew())")
    })
    public abstract FaReportDocumentEntity mapToFAReportDocumentEntity(FAReportDocument faReportDocument, @MappingTarget FaReportDocumentEntity faReportDocumentEntity);

    @Mappings({
            @Mapping(target = "correctionType", source = "status"),
            @Mapping(target = "correctionDate", source = "fluxReportDocument.creationDatetime"),
            @Mapping(target = "faReportIdentifier", source = "fluxReportDocument.fluxReportDocumentId"),
            @Mapping(target = "ownerFluxParty", source = "fluxReportDocument.ownerFluxPartyId")
    })
    public abstract FaReportCorrectionDTO mapToFaReportCorrectionDto(FaReportDocumentEntity faReportDocumentEntity);

    public abstract List<FaReportCorrectionDTO> mapToFaReportCorrectionDtoList(List<FaReportDocumentEntity> faReportDocumentEntities);

    @Mappings({
            @Mapping(target = "typeCode", source = "typeCode"),
            @Mapping(target = "fmcMarker", source = "fmcMarker"),
            @Mapping(target = "acceptedDateTime", source = "acceptedDatetime"),
            @Mapping(target = "creationDateTime", source = "fluxReportDocument.creationDatetime"),
            @Mapping(target = "fluxReportDocumentId", source = "fluxReportDocument.fluxReportDocumentId"),
            @Mapping(target = "purposeCode", source = "fluxReportDocument.purposeCode"),
            @Mapping(target = "referenceId", source = "fluxReportDocument.referenceId"),
            @Mapping(target = "ownerFluxPartyId", source = "fluxReportDocument.ownerFluxPartyId"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "vesselDetails", source = "vesselTransportMeans")
    })
    public abstract FaReportDocumentDetailsDTO mapToFaReportDocumentDetailsDTO(FaReportDocumentEntity faReportDocumentEntity);

    @Mappings({
            @Mapping(target = "faReportIdentifierId", expression = "java(getIdType(idType))"),
            @Mapping(target = "faReportIdentifierSchemeId", expression = "java(getIdTypeSchemaId(idType))")
    })
    protected abstract FaReportIdentifierEntity mapToFAReportIdentifierEntity(IDType idType);

    protected String setStatusAsNew() {
        return FaReportStatusEnum.NEW.getStatus();
    }

    protected Set<FishingActivityEntity> getFishingActivityEntities(List<FishingActivity> fishingActivities, FaReportDocumentEntity faReportDocumentEntity) {
        if (fishingActivities == null || fishingActivities.isEmpty()) {
            return null;
        }
        Set<FishingActivityEntity> fishingActivityEntities =  new HashSet<>();
        for (FishingActivity fishingActivity : fishingActivities) {
            FishingActivityEntity fishingActivityEntity = FishingActivityMapper.INSTANCE.mapToFishingActivityEntity(fishingActivity, faReportDocumentEntity, new FishingActivityEntity());
            fishingActivityEntities.add(fishingActivityEntity);
        }
        return fishingActivityEntities;
    }

    protected VesselTransportMeansEntity getVesselTransportMeansEntity(VesselTransportMeans vesselTransportMeans, FaReportDocumentEntity faReportDocumentEntity) {
        if (vesselTransportMeans == null) {
            return null;
        }
        return VesselTransportMeansMapper.INSTANCE.mapToVesselTransportMeansEntity(vesselTransportMeans, faReportDocumentEntity, new VesselTransportMeansEntity());
    }

    protected FluxReportDocumentEntity getFluxReportDocument(FLUXReportDocument fluxReportDocument, FaReportDocumentEntity faReportDocumentEntity) {
        if (fluxReportDocument == null) {
            return null;
        }
        return FluxReportDocumentMapper.INSTANCE.mapToFluxReportDocumentEntity(fluxReportDocument, faReportDocumentEntity, new FluxReportDocumentEntity());
    }

    protected Set<FaReportIdentifierEntity> mapToFAReportIdentifierEntities(List<IDType> idTypes, FaReportDocumentEntity faReportDocumentEntity) {
        if (idTypes == null || idTypes.isEmpty()) {
            return null;
        }
        Set<FaReportIdentifierEntity> faReportIdentifierEntities = new HashSet<>();
        for (IDType idType : idTypes) {
            FaReportIdentifierEntity faReportIdentifierEntity = FaReportDocumentMapper.INSTANCE.mapToFAReportIdentifierEntity(idType);
            faReportIdentifierEntity.setFaReportDocument(faReportDocumentEntity);
            faReportIdentifierEntities.add(faReportIdentifierEntity);
        }
        return faReportIdentifierEntities;
    }
}