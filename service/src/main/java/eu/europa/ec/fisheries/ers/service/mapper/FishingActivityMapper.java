/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import static org.hibernate.search.util.impl.CollectionHelper.asSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.AapProcessCodeEntity;
import eu.europa.ec.fisheries.ers.fa.entities.AapProcessEntity;
import eu.europa.ec.fisheries.ers.fa.entities.AapProductEntity;
import eu.europa.ec.fisheries.ers.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxPartyIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearProblemEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselStorageCharacteristicsEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationSchemeId;
import eu.europa.ec.fisheries.ers.service.dto.DelimitedPeriodDTO;
import eu.europa.ec.fisheries.ers.service.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.ers.service.dto.FluxCharacteristicsDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.ReportDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FishingGearDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FluxReportIdentifierDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingActivitySummary;
import eu.europa.ec.fisheries.uvms.mapper.GeometryMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FACatch;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingActivity;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingGear;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingTrip;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearProblem;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselStorageCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

@Mapper(uses = {FishingActivityIdentifierMapper.class, FaCatchMapper.class, DelimitedPeriodMapper.class,
        FishingGearMapper.class, GearProblemMapper.class, FishingTripMapper.class,
        FluxCharacteristicsMapper.class, FaReportDocumentMapper.class, GeometryMapper.class
})
@Slf4j
public abstract class FishingActivityMapper extends BaseMapper {

    public static final FishingActivityMapper INSTANCE = Mappers.getMapper(FishingActivityMapper.class);
    public static final String LOCATION_AREA = "AREA";
    public static final String LOCATION_PORT = "LOCATION";

    @Mappings({
            @Mapping(target = "typeCode", source = "fishingActivity.typeCode.value"),
            @Mapping(target = "typeCodeListid", expression = "java(getCodeTypeListId(fishingActivity.getTypeCode()))"),
            @Mapping(target = "occurence", source = "fishingActivity.occurrenceDateTime.dateTime"),
            @Mapping(target = "reasonCode", source = "fishingActivity.reasonCode.value"),
            @Mapping(target = "reasonCodeListId", expression = "java(getCodeTypeListId(fishingActivity.getReasonCode()))"),
            @Mapping(target = "vesselActivityCode", expression = "java(getCodeType(fishingActivity.getVesselRelatedActivityCode()))"),
            @Mapping(target = "vesselActivityCodeListId", expression = "java(getCodeTypeListId(fishingActivity.getVesselRelatedActivityCode()))"),
            @Mapping(target = "fisheryTypeCode", expression = "java(getCodeType(fishingActivity.getFisheryTypeCode()))"),
            @Mapping(target = "fisheryTypeCodeListId", expression = "java(getCodeTypeListId(fishingActivity.getFisheryTypeCode()))"),
            @Mapping(target = "speciesTargetCode", expression = "java(getCodeType(fishingActivity.getSpeciesTargetCode()))"),
            @Mapping(target = "speciesTargetCodeListId", expression = "java(getCodeTypeListId(fishingActivity.getSpeciesTargetCode()))"),
            @Mapping(target = "operationQuantity", source = "fishingActivity.operationsQuantity.value"),
            @Mapping(target = "operationQuantityCode", source = "fishingActivity.operationsQuantity.unitCode"),
            @Mapping(target = "calculatedOperationQuantity", expression = "java(getCalculatedQuantity(fishingActivity.getOperationsQuantity()))"),
            @Mapping(target = "fishingDurationMeasure", source = "fishingActivity.fishingDurationMeasure.value"),
            @Mapping(target = "fishingDurationMeasureCode", expression = "java(getMeasureUnitCode(fishingActivity.getFishingDurationMeasure()))"),
            @Mapping(target = "calculatedFishingDuration", expression = "java(getCalculatedMeasure(fishingActivity.getFishingDurationMeasure()))"),
            @Mapping(target = "faReportDocument", expression = "java(faReportDocumentEntity)"),
            @Mapping(target = "sourceVesselCharId", expression = "java(getSourceVesselStorageCharacteristics(fishingActivity.getSourceVesselStorageCharacteristic(), fishingActivityEntity))"),
            @Mapping(target = "destVesselCharId", expression = "java(getDestVesselStorageCharacteristics(fishingActivity.getDestinationVesselStorageCharacteristic(), fishingActivityEntity))"),
            @Mapping(target = "fishingActivityIdentifiers", expression = "java(mapToFishingActivityIdentifierEntities(fishingActivity.getIDS(), fishingActivityEntity))"),
            @Mapping(target = "delimitedPeriods", expression = "java(getDelimitedPeriodEntities(fishingActivity.getSpecifiedDelimitedPeriods(), fishingActivityEntity))"),
            @Mapping(target = "fishingTrips", expression = "java(getFishingTripEntities(fishingActivity.getSpecifiedFishingTrip(), fishingActivityEntity))"),
            @Mapping(target = "fishingGears", expression = "java(getFishingGearEntities(fishingActivity.getSpecifiedFishingGears(), fishingActivityEntity))"),
            @Mapping(target = "fluxCharacteristics", expression = "java(getFluxCharacteristicsEntities(fishingActivity.getSpecifiedFLUXCharacteristics(), fishingActivityEntity))"),
            @Mapping(target = "gearProblems", expression = "java(getGearProblemEntities(fishingActivity.getSpecifiedGearProblems(), fishingActivityEntity))"),
            @Mapping(target = "fluxLocations", expression = "java(getFluxLocationEntities(fishingActivity.getRelatedFLUXLocations(), fishingActivityEntity))"),
            @Mapping(target = "faCatchs", expression = "java(getFaCatchEntities(fishingActivity.getSpecifiedFACatches(), fishingActivityEntity))"),
            @Mapping(target = "vesselTransportMeans", expression = "java(getVesselTransportMeansEntity(fishingActivity, faReportDocumentEntity))"),
            @Mapping(target = "allRelatedFishingActivities", expression = "java(getAllRelatedFishingActivities(fishingActivity.getRelatedFishingActivities(), faReportDocumentEntity, fishingActivityEntity))"),
            @Mapping(target = "flagState", expression = "java(getFlagState(fishingActivity))")
    })
    public abstract FishingActivityEntity mapToFishingActivityEntity(FishingActivity fishingActivity, FaReportDocumentEntity faReportDocumentEntity, @MappingTarget FishingActivityEntity fishingActivityEntity);

    @Mappings({
            @Mapping(target = "fishingActivityId", source = "id"),
            @Mapping(target = "uniqueFAReportId", expression = "java(getUniqueId(entity))"),
            @Mapping(target = "faReportID", source = "faReportDocument.id"),
            @Mapping(target = "fromId", expression = "java(getFromId(entity))"),
            @Mapping(target = "fromName", source = "faReportDocument.fluxReportDocument.fluxParty.fluxPartyName"),
            @Mapping(target = "vesselTransportMeansName", source = "faReportDocument.vesselTransportMeans.name"),
            @Mapping(target = "purposeCode", source = "faReportDocument.fluxReportDocument.purposeCode"),
            @Mapping(target = "FAReportType", source = "faReportDocument.typeCode"),
            @Mapping(source = "typeCode", target = "activityType"),
            @Mapping(target = "areas", expression = "java(getFishingActivityLocationTypes(entity,LOCATION_AREA))"),
            @Mapping(target = "port", expression = "java(getFishingActivityLocationTypes(entity,LOCATION_PORT))"),
            @Mapping(target = "fishingGear", expression = "java(getFishingGears(entity))"),
            @Mapping(target = "speciesCode", expression = "java(getSpeciesCode(entity))"),
            @Mapping(target = "quantity", expression = "java(getQuantity(entity))"),
            @Mapping(target = "fluxLocations", expression = "java(null)"),
            @Mapping(target = "fishingGears", expression = "java(null)"),
            @Mapping(target = "fluxCharacteristics", expression = "java(null)"),
            @Mapping(target = "delimitedPeriod", expression = "java(null)"),
            @Mapping(target = "dataSource", source = "faReportDocument.source"),
            @Mapping(target = "vesselIdTypes", expression = "java(getVesselIdType(entity))"),
            @Mapping(target = "startDate", expression = "java(getStartDate(entity))"),
            @Mapping(target = "endDate", expression = "java(getEndDate(entity))"),
            @Mapping(target = "hasCorrection", expression = "java(getCorrection(entity))"),
            @Mapping(target = "fluxReportReferenceId", source = "faReportDocument.fluxReportDocument.referenceId"),
            @Mapping(target = "fluxReportReferenceSchemeId", source = "faReportDocument.fluxReportDocument.referenceSchemeId")
    })
    public abstract FishingActivityReportDTO mapToFishingActivityReportDTO(FishingActivityEntity entity);

    @Mappings({
            @Mapping(target = "activityType", source = "typeCode"),
            @Mapping(target = "geometry", source = "wkt"),
            @Mapping(target = "acceptedDateTime", source = "faReportDocument.acceptedDatetime"),
            @Mapping(target = "dataSource", source = "faReportDocument.source"),
            @Mapping(target = "reportType", source = "faReportDocument.typeCode"),
            @Mapping(target = "purposeCode", source = "faReportDocument.fluxReportDocument.purposeCode"),
            @Mapping(target = "vesselName", source = "faReportDocument.vesselTransportMeans.name"),
            @Mapping(target = "gears", expression = "java(getFishingGearTypeCodeList(entity))"),
            @Mapping(target = "species", expression = "java(getSpeciesCode(entity))"),
            @Mapping(target = "areas", expression = "java(getFishingActivityLocationTypes(entity,LOCATION_AREA))"),
            @Mapping(target = "ports", expression = "java(getFishingActivityLocationTypes(entity,LOCATION_PORT))"),
            @Mapping(target = "vesselIdentifiers", expression = "java(getVesselIdentifiers(entity))")

    })
    public abstract FishingActivitySummary mapToFishingActivitySummary(FishingActivityEntity entity);

    @Mappings({
            @Mapping(target = "fishingActivityId", source = "id"),
            @Mapping(target = "activityType", source = "typeCode"),
            @Mapping(target = "occurence", source = "occurence"),
            @Mapping(target = "reason", source = "reasonCode"),
            @Mapping(target = "faReportDocumentType", source = "faReportDocument.typeCode"),
            @Mapping(target = "faReportAcceptedDateTime", source = "faReportDocument.acceptedDatetime"),
            @Mapping(target = "correction", expression = "java(getCorrection(entity))"),
            @Mapping(target = "delimitedPeriod", expression = "java(getDelimitedPeriodDTOList(entity))"),
            @Mapping(target = "fluxLocations", ignore = true),
            @Mapping(target = "fluxCharacteristics", expression = "java(getFluxCharacteristicsDTOList(entity))"),
            @Mapping(target = "fishingGears", expression = "java(getFishingGearDTOList(entity))")
    })
    public abstract ReportDTO mapToReportDTO(FishingActivityEntity entity);

    @Mappings({
            @Mapping(target = "type", source = "typeCode"),
            @Mapping(target = "occurrence", source = "occurence"),
            @Mapping(target = "characteristics", expression = "java(mapCharacteristics(entity))"),
            @Mapping(target = "identifiers", source = "fishingActivityIdentifiers")
    })
    public abstract ActivityDetailsDto mapFishingActivityEntityToActivityDetailsDto(FishingActivityEntity entity);

    @Mappings({
            @Mapping(target = "faIdentifierId", source = "value"),
            @Mapping(target = "faIdentifierSchemeId", source = "schemeID")
    })
    protected abstract FishingActivityIdentifierEntity mapToFishingActivityIdentifierEntity(IDType idType);

    protected VesselTransportMeansEntity getVesselTransportMeansEntity(FishingActivity fishingActivity, FaReportDocumentEntity faReportDocumentEntity) {

        List<VesselTransportMeans> vesselList = fishingActivity.getRelatedVesselTransportMeans();
        if (vesselList == null || vesselList.isEmpty()) {
            return null;
        }
        return VesselTransportMeansMapper.INSTANCE.mapToVesselTransportMeansEntity(vesselList.get(0), faReportDocumentEntity, new VesselTransportMeansEntity());
    }

    protected List<FishingGearDTO> getFishingGearDTOList(FishingActivityEntity entity) {
        if (entity == null || entity.getFishingGears() == null || entity.getFishingGears().isEmpty()) {
            return Collections.emptyList();
        }

        List<FishingGearDTO> fishingGearDTOList = new ArrayList<>();
        for (FishingGearEntity fishingGear : entity.getFishingGears()) {
            fishingGearDTOList.add(FishingGearMapper.INSTANCE.mapToFishingGearDTO(fishingGear));
        }
        return fishingGearDTOList;
    }

    protected List<String> getFishingGearTypeCodeList(FishingActivityEntity entity) {
        if (entity == null || entity.getFishingGears() == null || entity.getFishingGears().isEmpty()) {
            return Collections.emptyList();
        }

        List<String> fishingGearTypecodeList = new ArrayList<>();
        for (FishingGearEntity fishingGear : entity.getFishingGears()) {
            fishingGearTypecodeList.add(fishingGear.getTypeCode());
        }
        return fishingGearTypecodeList;
    }

    protected List<FluxCharacteristicsDTO> getFluxCharacteristicsDTOList(FishingActivityEntity entity) {
        if (entity == null || entity.getFluxCharacteristics() == null || entity.getFluxCharacteristics().isEmpty()) {
            return Collections.emptyList();
        }

        List<FluxCharacteristicsDTO> fluxCharacteristicsDTOList = new ArrayList<>();
        for (FluxCharacteristicEntity fluxCharacteristic : entity.getFluxCharacteristics()) {
            fluxCharacteristicsDTOList.add(FluxCharacteristicsMapper.INSTANCE.mapToFluxCharacteristicsDTO(fluxCharacteristic));
        }

        return fluxCharacteristicsDTOList;
    }

    protected Date getStartDate(FishingActivityEntity entity) {
        if (entity == null || entity.getDelimitedPeriods() == null || entity.getDelimitedPeriods().isEmpty()) {
            return null;
        }
        return entity.getDelimitedPeriods().iterator().next().getStartDate();
    }

    protected Date getEndDate(FishingActivityEntity entity) {
        if (entity == null || entity.getDelimitedPeriods() == null || entity.getDelimitedPeriods().isEmpty()) {
            return null;
        }
        return entity.getDelimitedPeriods().iterator().next().getEndDate();
    }

    protected List<DelimitedPeriodDTO> getDelimitedPeriodDTOList(FishingActivityEntity entity) {
        if (entity == null || entity.getDelimitedPeriods() == null || entity.getDelimitedPeriods().isEmpty()) {
            return Collections.emptyList();
        }

        List<DelimitedPeriodDTO> delimitedPeriodDTOEntities = new ArrayList<>();
        for (DelimitedPeriodEntity dp : entity.getDelimitedPeriods()) {
            delimitedPeriodDTOEntities.add(DelimitedPeriodMapper.INSTANCE.mapToDelimitedPeriodDTO(dp));
        }
        return delimitedPeriodDTOEntities;
    }

    protected boolean getCorrection(FishingActivityEntity entity) {
        if (entity == null || entity.getFaReportDocument() == null || entity.getFaReportDocument().getFluxReportDocument() == null) {
            return false;
        }

        FluxReportDocumentEntity fluxReportDocument = entity.getFaReportDocument().getFluxReportDocument();
        if (fluxReportDocument.getReferenceId() != null && fluxReportDocument.getReferenceId().length() != 0) {
            return true;

        }
        return false;
    }

    protected List<FluxReportIdentifierDTO> getUniqueId(FishingActivityEntity entity) {
        if (entity == null || entity.getFaReportDocument() == null || entity.getFaReportDocument().getFluxReportDocument() == null) {
            return Collections.emptyList();
        }
        List<FluxReportIdentifierDTO> identifierDTOs = new ArrayList<>();
        Set<FluxReportIdentifierEntity> identifiers = entity.getFaReportDocument().getFluxReportDocument().getFluxReportIdentifiers();
        for (FluxReportIdentifierEntity fluxReportIdentifierEntity : identifiers) {
            identifierDTOs.add(FluxReportDocumentMapper.INSTANCE.mapToFluxReportIdentifierDTO(fluxReportIdentifierEntity));
        }
        return identifierDTOs;
    }

    protected List<String> getFromId(FishingActivityEntity entity) {
        if (entity == null || entity.getFaReportDocument() == null
                || entity.getFaReportDocument().getFluxReportDocument() == null
                || entity.getFaReportDocument().getFluxReportDocument().getFluxParty() == null
                || entity.getFaReportDocument().getFluxReportDocument().getFluxParty().getFluxPartyIdentifiers() == null) {
            return Collections.emptyList();
        }

        List<String> fromIdList = new ArrayList<>();

        Set<FluxPartyIdentifierEntity> idSet = entity.getFaReportDocument().getFluxReportDocument().getFluxParty().getFluxPartyIdentifiers();
        for (FluxPartyIdentifierEntity fluxPartyIdentifierEntity : idSet) {
            fromIdList.add(fluxPartyIdentifierEntity.getFluxPartyIdentifierId());
        }

        return fromIdList;
    }

    protected Map<String, String> getVesselIdType(FishingActivityEntity entity) {
        if (entity == null || entity.getFaReportDocument() == null || entity.getFaReportDocument().getVesselTransportMeans() == null
                || entity.getFaReportDocument().getVesselTransportMeans().getVesselIdentifiers() == null) {
            return Collections.emptyMap();
        }


        Map<String, String> vesselTransportIdList = new HashMap<>();
        Set<VesselIdentifierEntity> identifierList = entity.getFaReportDocument().getVesselTransportMeans().getVesselIdentifiers();

        for (VesselIdentifierEntity identity : identifierList) {
            vesselTransportIdList.put(identity.getVesselIdentifierSchemeId(), identity.getVesselIdentifierId());
        }
        return vesselTransportIdList;
    }

    protected List<String> getVesselIdentifiers(FishingActivityEntity entity) {
        if (entity == null || entity.getFaReportDocument() == null || entity.getFaReportDocument().getVesselTransportMeans() == null
                || entity.getFaReportDocument().getVesselTransportMeans().getVesselIdentifiers() == null) {
            return Collections.emptyList();
        }
        List<String> identifiers = new ArrayList<>();

        Set<VesselIdentifierEntity> identifierList = entity.getFaReportDocument().getVesselTransportMeans().getVesselIdentifiers();

        for (VesselIdentifierEntity identity : identifierList) {
            identifiers.add(identity.getVesselIdentifierId());
        }

        return identifiers;
    }

    protected List<String> getSpeciesCode(FishingActivityEntity entity) {
        if (entity == null || entity.getFaCatchs() == null) {
            return Collections.emptyList();
        }

        HashSet<String> speciesCode = new HashSet<>();
        Set<FaCatchEntity> faCatchList = entity.getFaCatchs();

        for (FaCatchEntity faCatch : faCatchList) {
            speciesCode.add(faCatch.getSpeciesCode());
            getSpeciesCodeFromAapProduct(faCatch, speciesCode);
        }

        return new ArrayList<>(speciesCode);
    }

    protected void getSpeciesCodeFromAapProduct(FaCatchEntity faCatch, HashSet<String> speciesCode) {
        if (faCatch.getAapProcesses() == null)
            return;

        for (AapProcessEntity aapProcessEntity : faCatch.getAapProcesses()) {
            Set<AapProductEntity> aapProductList = aapProcessEntity.getAapProducts();
            if (aapProductList != null) {
                for (AapProductEntity aapProduct : aapProductList) {
                    speciesCode.add(aapProduct.getSpeciesCode());
                }
            }
        }
    }

    protected List<Double> getQuantity(FishingActivityEntity entity) {
        if (entity == null || entity.getFaCatchs() == null) {
            return Collections.emptyList();
        }
        List<Double> quantity = new ArrayList<>();
        Set<FaCatchEntity> faCatchList = entity.getFaCatchs();

        for (FaCatchEntity faCatch : faCatchList) {
            quantity.add(faCatch.getWeightMeasure());
            getQuantityFromAapProduct(faCatch, quantity);
        }
        return quantity;
    }

    protected void getQuantityFromAapProduct(FaCatchEntity faCatch, List<Double> quantity) {
        if (faCatch.getAapProcesses() == null)
            return;

        for (AapProcessEntity aapProcessEntity : faCatch.getAapProcesses()) {
            Set<AapProductEntity> aapProductList = aapProcessEntity.getAapProducts();
            if (aapProductList != null) {
                for (AapProductEntity aapProduct : aapProductList) {
                    quantity.add(aapProduct.getWeightMeasure());
                }
            }
        }
    }

    protected List<String> getFishingGears(FishingActivityEntity entity) {
        if (entity == null || entity.getFishingGears() == null) {
            return Collections.emptyList();
        }
        List<String> gears = new ArrayList<>();
        Set<FishingGearEntity> gearList = entity.getFishingGears();

        for (FishingGearEntity gear : gearList) {
            gears.add(gear.getTypeCode());
        }
        return gears;
    }

    protected List<String> getFishingActivityLocationTypes(FishingActivityEntity entity, String locationType) {
        if (entity == null || entity.getFluxLocations() == null) {
            return Collections.emptyList();
        }
        List<String> areas = new ArrayList<>();
        Set<FluxLocationEntity> fluxLocations = entity.getFluxLocations();

        for (FluxLocationEntity location : fluxLocations) {
            if (locationType == null || locationType.equalsIgnoreCase(location.getTypeCode())) {
                areas.add(location.getFluxLocationIdentifier());
            }
        }
        return areas;
    }

    protected Set<FishingActivityEntity> getAllRelatedFishingActivities(List<FishingActivity> fishingActivity, FaReportDocumentEntity faReportDocumentEntity, FishingActivityEntity parentFishingActivity) {
        if (fishingActivity == null || fishingActivity.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FishingActivityEntity> relatedFishingActivityEntities = new HashSet<>();
        for (FishingActivity relatedFishingActivity : fishingActivity) {
            FishingActivityEntity relatedFishingActivityEntity = FishingActivityMapper.INSTANCE.mapToFishingActivityEntity(relatedFishingActivity, faReportDocumentEntity, new FishingActivityEntity());
            relatedFishingActivityEntity.setRelatedFishingActivity(parentFishingActivity);
            relatedFishingActivityEntities.add(relatedFishingActivityEntity);
        }
        return relatedFishingActivityEntities;
    }

    protected Set<FaCatchEntity> getFaCatchEntities(List<FACatch> faCatches, FishingActivityEntity fishingActivityEntity) {
        if (faCatches == null || faCatches.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FaCatchEntity> faCatchEntities = new HashSet<>();
        for (FACatch faCatch : faCatches) {
            FaCatchEntity faCatchEntity = FaCatchMapper.INSTANCE.mapToFaCatchEntity(faCatch);
            faCatchEntity.setFishingActivity(fishingActivityEntity);
            faCatchEntity.setGearTypeCode(extractFishingGearTypeCode(faCatchEntity.getFishingGears()));
            faCatchEntity.setPresentation(extractPresentation(faCatchEntity.getAapProcesses()));
            faCatchEntities.add(mapFluxLocationSchemeIds(faCatch, faCatchEntity));
        }
        return faCatchEntities;
    }

    private String extractPresentation(Set<AapProcessEntity> aapProcessEntities) {
        if (CollectionUtils.isEmpty(aapProcessEntities)) {
            return null;
        }

        for (AapProcessEntity aapProcessEntity : aapProcessEntities) {
            Set<AapProcessCodeEntity> codeEntities = aapProcessEntity.getAapProcessCode();
            if (CollectionUtils.isEmpty(codeEntities)) {
                continue;
            }

            for (AapProcessCodeEntity aapProcessCodeEntity : codeEntities) {
                if ("FISH_PRESENTATION".equals(aapProcessCodeEntity.getTypeCodeListId())) {
                    return aapProcessCodeEntity.getTypeCode();
                }
            }

        }
        return null;
    }

    private String extractFishingGearTypeCode(Set<FishingGearEntity> fishingGears) {
        String gearTypeCode = null;
        if (CollectionUtils.isNotEmpty(fishingGears)) {
            FishingGearEntity fishingGearEntity = fishingGears.iterator().next();
            return fishingGearEntity.getTypeCode();
        }
        return gearTypeCode;
    }

    private FaCatchEntity mapFluxLocationSchemeIds(FACatch faCatch, FaCatchEntity faCatchEntity) {
        List<FLUXLocation> fluxLocations = faCatch.getSpecifiedFLUXLocations();
        if (fluxLocations == null || fluxLocations.isEmpty())
            return faCatchEntity;

        for (FLUXLocation location : fluxLocations) {
            if (location.getRegionalFisheriesManagementOrganizationCode() != null)
                faCatchEntity.setRfmo(location.getRegionalFisheriesManagementOrganizationCode().getValue());

            IDType id = location.getID();
            if (id != null) {
                switch (FluxLocationSchemeId.valueOf(id.getSchemeID())) {
                    case TERRITORY:
                        faCatchEntity.setTerritory(id.getValue());
                        break;
                    case FAO_AREA:
                        faCatchEntity.setFaoArea(id.getValue());
                        break;
                    case ICES_STAT_RECTANGLE:
                        faCatchEntity.setIcesStatRectangle(id.getValue());
                        break;
                    case EFFORT_ZONE:
                        faCatchEntity.setEffortZone(id.getValue());
                        break;
                    case GFCM_GSA:
                        faCatchEntity.setGfcmGsa(id.getValue());
                        break;
                    case GFCM_STAT_RECTANGLE:
                        faCatchEntity.setGfcmStatRectangle(id.getValue());
                        break;
                    default:
                        log.error("Unknown schemeId for FluxLocation." + id.getSchemeID());
                        break;
                }
            }
        }

        return faCatchEntity;
    }

    protected Set<FluxLocationEntity> getFluxLocationEntities(List<FLUXLocation> fluxLocations, FishingActivityEntity fishingActivityEntity) {
        if (fluxLocations == null || fluxLocations.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FluxLocationEntity> fluxLocationEntities = new HashSet<>();
        for (FLUXLocation fluxLocation : fluxLocations) {
            FluxLocationEntity fluxLocationEntity = FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(fluxLocation, FluxLocationCatchTypeEnum.FA_RELATED, fishingActivityEntity, new FluxLocationEntity());
            fluxLocationEntities.add(fluxLocationEntity);
        }
        return fluxLocationEntities;
    }

    protected Set<GearProblemEntity> getGearProblemEntities(List<GearProblem> gearProblems, FishingActivityEntity fishingActivityEntity) {
        if (gearProblems == null || gearProblems.isEmpty()) {
            return Collections.emptySet();
        }
        Set<GearProblemEntity> gearProblemEntities = new HashSet<>();
        for (GearProblem gearProblem : gearProblems) {
            GearProblemEntity gearProblemEntity = GearProblemMapper.INSTANCE.mapToGearProblemEntity(gearProblem);
            gearProblemEntity.setFishingActivity(fishingActivityEntity);
            gearProblemEntities.add(gearProblemEntity);
        }
        return gearProblemEntities;
    }

    protected Set<FluxCharacteristicEntity> getFluxCharacteristicsEntities(List<FLUXCharacteristic> fluxCharacteristics, FishingActivityEntity fishingActivityEntity) {
        if (fluxCharacteristics == null || fluxCharacteristics.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FluxCharacteristicEntity> fluxCharacteristicEntities = new HashSet<>();
        for (FLUXCharacteristic fluxCharacteristic : fluxCharacteristics) {
            FluxCharacteristicEntity fluxCharacteristicEntity = FluxCharacteristicsMapper.INSTANCE.mapToFluxCharEntity(fluxCharacteristic, fishingActivityEntity, new FluxCharacteristicEntity());
            fluxCharacteristicEntities.add(fluxCharacteristicEntity);
        }
        return fluxCharacteristicEntities;
    }

    protected Set<FishingGearEntity> getFishingGearEntities(List<FishingGear> fishingGears, FishingActivityEntity fishingActivityEntity) {
        if (fishingGears == null || fishingGears.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FishingGearEntity> fishingGearEntities = new HashSet<>();
        for (FishingGear fishingGear : fishingGears) {
            FishingGearEntity fishingGearEntity = FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear, fishingActivityEntity, new FishingGearEntity());
            fishingGearEntities.add(fishingGearEntity);
        }
        return fishingGearEntities;
    }

    protected Set<FishingTripEntity> getFishingTripEntities(FishingTrip fishingTrip, FishingActivityEntity fishingActivityEntity) {
        if (fishingTrip == null) {
            return Collections.emptySet();
        }
        return asSet(FishingTripMapper.INSTANCE.mapToFishingTripEntity(fishingTrip, fishingActivityEntity, new FishingTripEntity()));
    }

    protected Set<DelimitedPeriodEntity> getDelimitedPeriodEntities(List<DelimitedPeriod> delimitedPeriods, FishingActivityEntity fishingActivityEntity) {
        if (delimitedPeriods == null || delimitedPeriods.isEmpty()) {
            return Collections.emptySet();
        }
        Set<DelimitedPeriodEntity> delimitedPeriodEntities = new HashSet<>();
        for (DelimitedPeriod delimitedPeriod : delimitedPeriods) {
            DelimitedPeriodEntity delimitedPeriodEntity = DelimitedPeriodMapper.INSTANCE.mapToDelimitedPeriodEntity(delimitedPeriod);
            delimitedPeriodEntity.setFishingActivity(fishingActivityEntity);
            delimitedPeriodEntities.add(delimitedPeriodEntity);
        }
        return delimitedPeriodEntities;
    }

    protected Set<FishingActivityIdentifierEntity> mapToFishingActivityIdentifierEntities(List<IDType> idTypes, FishingActivityEntity fishingActivityEntity) {
        if (idTypes == null || idTypes.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FishingActivityIdentifierEntity> identifierEntities = new HashSet<>();
        for (IDType idType : idTypes) {
            FishingActivityIdentifierEntity identifier = FishingActivityMapper.INSTANCE.mapToFishingActivityIdentifierEntity(idType);
            identifier.setFishingActivity(fishingActivityEntity);
            identifierEntities.add(identifier);
        }
        return identifierEntities;
    }

    protected VesselStorageCharacteristicsEntity getSourceVesselStorageCharacteristics(VesselStorageCharacteristic sourceVesselStorageChar, FishingActivityEntity fishingActivityEntity) {
        if (sourceVesselStorageChar == null) {
            return null;
        }
        return VesselStorageCharacteristicsMapper.INSTANCE.mapToSourceVesselStorageCharEntity(sourceVesselStorageChar, fishingActivityEntity, new VesselStorageCharacteristicsEntity());
    }

    protected VesselStorageCharacteristicsEntity getDestVesselStorageCharacteristics(VesselStorageCharacteristic destVesselStorageChar, FishingActivityEntity fishingActivityEntity) {
        if (destVesselStorageChar == null) {
            return null;
        }
        return VesselStorageCharacteristicsMapper.INSTANCE.mapToDestVesselStorageCharEntity(destVesselStorageChar, fishingActivityEntity, new VesselStorageCharacteristicsEntity());
    }

    public Map<String, String> mapCharacteristics(FishingActivityEntity entity) {
        Map<String, String> map = FluxCharacteristicsMapper.INSTANCE.map(entity.getFluxCharacteristics());
        map.putAll(FlapDocumentMapper.INSTANCE.map(entity.getFlapDocuments()));
        return map;
    }

    protected String getFlagState(FishingActivity fishingActivity) {

        List<VesselTransportMeans> vesselList = fishingActivity.getRelatedVesselTransportMeans();
        if (CollectionUtils.isEmpty(vesselList)) {
            return null;
        }

        VesselTransportMeans vesselTransportMeans = vesselList.get(0);
        if (vesselTransportMeans == null)
            return null;

        return getCountry(vesselTransportMeans.getRegistrationVesselCountry());
    }
}