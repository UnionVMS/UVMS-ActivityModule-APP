/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.AapProcessCodeEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.AapProcessEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.AapProductEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.GearProblemEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselStorageCharacteristicsEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FluxLocationEnum;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FluxLocationSchemeId;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingActivitySummary;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselContactPartyType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierType;
import eu.europa.ec.fisheries.uvms.activity.service.dto.DelimitedPeriodDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FluxReportIdentifierDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.ReportDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.uvms.activity.service.util.Utils;
import eu.europa.ec.fisheries.uvms.commons.date.XMLDateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProcess;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProduct;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FACatch;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingActivity;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearProblem;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.SizeDistribution;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselStorageCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(imports = {FaReportStatusType.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Slf4j
public abstract class FishingActivityMapper extends BaseMapper {

    public static final FishingActivityMapper INSTANCE = Mappers.getMapper(FishingActivityMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "fishingActivity.typeCode.value"),
            @Mapping(target = "typeCodeListid", expression = "java(getCodeTypeListId(fishingActivity.getTypeCode()))"),
            @Mapping(target = "occurence", source = "fishingActivity.occurrenceDateTime", qualifiedByName = "dateTimeTypeToInstant"),
            @Mapping(target = "reasonCode", source = "fishingActivity.reasonCode.value"),
            @Mapping(target = "reasonCodeListId", expression = "java(getCodeTypeListId(fishingActivity.getReasonCode()))"),
            @Mapping(target = "vesselActivityCode", expression = "java(getCodeType(fishingActivity.getVesselRelatedActivityCode()))"),
            @Mapping(target = "vesselActivityCodeListId", expression = "java(getCodeTypeListId(fishingActivity.getVesselRelatedActivityCode()))"),
            @Mapping(target = "fisheryTypeCode", expression = "java(getCodeType(fishingActivity.getFisheryTypeCode()))"),
            @Mapping(target = "fisheryTypeCodeListId", expression = "java(getCodeTypeListId(fishingActivity.getFisheryTypeCode()))"),
            @Mapping(target = "speciesTargetCode", expression = "java(getCodeType(fishingActivity.getSpeciesTargetCode()))"),
            @Mapping(target = "speciesTargetCodeListId", expression = "java(getCodeTypeListId(fishingActivity.getSpeciesTargetCode()))"),
            @Mapping(target = "operationsQuantity", source = "fishingActivity.operationsQuantity"),
            @Mapping(target = "fishingDurationMeasure", source = "fishingActivity.fishingDurationMeasure.value"),
            @Mapping(target = "fishingDurationMeasureCode", source = "fishingActivity.fishingDurationMeasure.unitCode"),
            @Mapping(target = "fishingDurationMeasureUnitCodeListVersionID", source = "fishingActivity.fishingDurationMeasure.unitCodeListVersionID"),
            @Mapping(target = "faReportDocument", source = "faReportDocumentEntity"),
            @Mapping(target = "sourceVesselCharId", expression = "java(getSourceVesselStorageCharacteristics(fishingActivity.getSourceVesselStorageCharacteristic(), fishingActivityEntity))"),
            @Mapping(target = "destVesselCharId", expression = "java(getDestVesselStorageCharacteristics(fishingActivity.getDestinationVesselStorageCharacteristic(), fishingActivityEntity))"),
            @Mapping(target = "fishingTrip", expression = "java(BaseMapper.mapToFishingTripEntity(fishingActivity.getSpecifiedFishingTrip()))"),
            @Mapping(target = "fishingGears", ignore = true),
            @Mapping(target = "fluxCharacteristics", ignore = true),
            @Mapping(target = "gearProblems", expression = "java(getGearProblemEntities(fishingActivity.getSpecifiedGearProblems(), fishingActivityEntity))"),
            @Mapping(target = "fluxLocations", expression = "java(getFluxLocationEntities(fishingActivity.getRelatedFLUXLocations(), fishingActivityEntity))"),
            @Mapping(target = "faCatchs", expression = "java(mapToFaCatchEntities(fishingActivity.getSpecifiedFACatches(), fishingActivityEntity))"),
            @Mapping(target = "vesselTransportMeans", expression = "java(getVesselTransportMeansEntity(fishingActivity, faReportDocumentEntity, fishingActivityEntity))"),
            @Mapping(target = "allRelatedFishingActivities", expression = "java(getAllRelatedFishingActivities(fishingActivity.getRelatedFishingActivities(), faReportDocumentEntity, fishingActivityEntity))"),
            @Mapping(target = "flagState", expression = "java(getFlagState(fishingActivity))"),
            @Mapping(target = "calculatedStartTime", expression = "java(getCalculatedStartTime(fishingActivity))"),
            @Mapping(target = "calculatedEndTime", expression = "java(getCalculatedEndTime(fishingActivity))"),
            @Mapping(target = "latest", constant = "true"),
            @Mapping(target = "flapDocuments", ignore = true)
    })
    public abstract FishingActivityEntity mapToFishingActivityEntity(FishingActivity fishingActivity, FaReportDocumentEntity faReportDocumentEntity);

    @Mappings({
            @Mapping(target = "fishingActivityId", source = "id"),
            @Mapping(target = "uniqueFAReportId", expression = "java(getUniqueId(entity))"),
            @Mapping(target = "faReportID", source = "faReportDocument.id"),
            @Mapping(target = "fromId", expression = "java(getFromId(entity))"),
            @Mapping(target = "fromName", source = "faReportDocument.fluxParty_name"),
            @Mapping(target = "vesselTransportMeansName", expression = "java(getFaReportDocVesselTransportMeans(entity).getName())"),
            @Mapping(target = "purposeCode", expression = "java(FaReportStatusType.valueOf(entity.getFaReportDocument().getStatus()).getPurposeCode().toString())"),
            @Mapping(target = "faReportType", source = "faReportDocument.typeCode"),
            @Mapping(source = "typeCode", target = "activityType"),
            @Mapping(target = "areas", expression = "java(getAreasForFishingActivity(entity))"),
            @Mapping(target = "port", expression = "java(getPortsForFishingActivity(entity))"),
            @Mapping(target = "fishingGear", expression = "java(getFishingGears(entity))"),
            @Mapping(target = "speciesCode", expression = "java(getSpeciesCode(entity))"),
            @Mapping(target = "quantity", expression = "java(getQuantity(entity))"),
            @Mapping(target = "fluxLocations", expression = "java(null)"),
            @Mapping(target = "fishingGears", expression = "java(null)"),
            @Mapping(target = "fluxCharacteristics", expression = "java(null)"),
            @Mapping(target = "dataSource", source = "faReportDocument.source"),
            @Mapping(target = "vesselIdTypes", expression = "java(getVesselIdType(entity))"),
            @Mapping(target = "startDate", source = "calculatedStartTime", qualifiedByName = "instantToDate"),
            @Mapping(target = "endDate", expression = "java(getEndDate(entity))"),
            @Mapping(target = "hasCorrection", expression = "java(getCorrection(entity))"),
            @Mapping(target = "fluxReportReferenceId", source = "faReportDocument.fluxReportDocument_ReferenceId"),
            @Mapping(target = "fluxReportReferenceSchemeId", source = "faReportDocument.fluxReportDocument_ReferenceIdSchemeId"),
            @Mapping(target = "cancelingReportID", source = "canceledBy"),
            @Mapping(target = "deletingReportID", source = "deletedBy"),
            @Mapping(target = "occurence", source = "occurence", qualifiedByName = "instantToDate")
    })
    public abstract FishingActivityReportDTO mapToFishingActivityReportDTO(FishingActivityEntity entity);

    @Mappings({
            @Mapping(target = "activityType", source = "typeCode"),
            @Mapping(target = "activityId", source = "id"),
            @Mapping(target = "faReportID", source = "faReportDocument.id"),
            @Mapping(target = "geometry", source = "wkt"),
            @Mapping(target = "acceptedDateTime", source = "faReportDocument.acceptedDatetime", qualifiedByName = "dateTimeTypeToInstant"),
            @Mapping(target = "dataSource", source = "faReportDocument.source"),
            @Mapping(target = "reportType", source = "faReportDocument.typeCode"),
            @Mapping(target = "purposeCode", source = "faReportDocument.fluxReportDocument_PurposeCode"),
            @Mapping(target = "vesselName", expression = "java(getFaReportDocVesselTransportMeans(entity).getName())"),
            @Mapping(target = "vesselGuid", expression = "java(getFaReportDocVesselTransportMeans(entity).getGuid())"),
            @Mapping(target = "gears", expression = "java(getFishingGearTypeCodeList(entity))"),
            @Mapping(target = "species", expression = "java(getSpeciesCode(entity))"),
            @Mapping(target = "areas", expression = "java(getAreasForFishingActivity(entity))"),
            @Mapping(target = "ports", expression = "java(getPortsForFishingActivity(entity))"),
            @Mapping(target = "vesselIdentifiers", expression = "java(getVesselIdentifierTypeList(entity))"),
            @Mapping(target = "isCorrection", expression = "java(getCorrection(entity))"),
            @Mapping(target = "tripId", expression = "java(getFishingTripId(entity))"),
            @Mapping(target = "flagState", expression = "java(getFlagState(entity))"),
            @Mapping(target = "landingReferencedID", expression = "java(getFluxReportIdentifierId(entity))"),
            @Mapping(target = "landingState", expression = "java(getLandingCountryId(entity))"),
            @Mapping(target = "occurence", source = "occurence", qualifiedByName = "instantToXMLGregorianCalendar")
    })
    public abstract FishingActivitySummary mapToFishingActivitySummary(FishingActivityEntity entity);

    @Mappings({
            @Mapping(target = "role", expression = "java(getRole(entity))"),
            @Mapping(target = "title", source = "contactPerson.title"),
            @Mapping(target = "givenName", source = "contactPerson.givenName"),
            @Mapping(target = "middleName", source = "contactPerson.middleName"),
            @Mapping(target = "familyName", source = "contactPerson.familyName"),
            @Mapping(target = "familyNamePrefix", source = "contactPerson.familyNamePrefix"),
            @Mapping(target = "nameSuffix", source = "contactPerson.nameSuffix"),
            @Mapping(target = "gender", source = "contactPerson.gender"),
            @Mapping(target = "alias", source = "contactPerson.alias")
    })
    public abstract VesselContactPartyType mapToVesselContactParty(ContactPartyEntity entity);

    @Mappings({
            @Mapping(target = "fishingActivityId", source = "id"),
            @Mapping(target = "activityType", source = "typeCode"),
            @Mapping(target = "geometry", source = "wkt"),
            @Mapping(target = "faReferenceID", source = "faReportDocument.fluxReportDocument_ReferenceId"),
            @Mapping(target = "faReferenceSchemeID", source = "faReportDocument.fluxReportDocument_ReferenceIdSchemeId"),
            @Mapping(target = "faUniqueReportID", expression = "java(getUniqueFaReportId(entity))"),
            @Mapping(target = "faUniqueReportSchemeID", expression = "java(getUniqueFaReportSchemeId(entity))"),// FIXME entity method
            @Mapping(target = "reason", source = "reasonCode"),
            @Mapping(target = "purposeCode", source = "faReportDocument.fluxReportDocument_PurposeCode"),
            @Mapping(target = "faReportDocumentType", source = "faReportDocument.typeCode"),
            @Mapping(target = "faReportAcceptedDateTime", source = "faReportDocument.acceptedDatetime", qualifiedByName = "instantToDate"),
            @Mapping(target = "correction", expression = "java(getCorrection(entity))"), // FIXME entity method
            @Mapping(target = "delimitedPeriod", expression = "java(getDelimitedPeriodDTOList(entity))"),
            @Mapping(target = "fluxLocations", ignore = true),
            @Mapping(target = "faReportID", source = "faReportDocument.id"),
            @Mapping(target = "occurence", source = "occurence", qualifiedByName = "instantToDate")
    })
    public abstract ReportDTO mapToReportDTO(FishingActivityEntity entity);

    @Mappings({
            @Mapping(target = "type", source = "typeCode"),
            @Mapping(target = "occurrence", source = "entity.occurence", qualifiedByName = "instantToDate"),
            @Mapping(target = "nrOfOperation", source = "operationsQuantity.value"),
            @Mapping(target = "reason", source = "reasonCode"),
            @Mapping(target = "vesselActivity", source = "vesselActivityCode")
    })
    public abstract ActivityDetailsDto mapFishingActivityEntityToActivityDetailsDto(FishingActivityEntity entity);

    protected Set<VesselTransportMeansEntity> getVesselTransportMeansEntity(FishingActivity fishingActivity, FaReportDocumentEntity faReportDocumentEntity, FishingActivityEntity fishingActivityEntity) {
        List<VesselTransportMeans> vesselList = fishingActivity.getRelatedVesselTransportMeans();
        if (vesselList == null || vesselList.isEmpty()) {
            return null;
        }
        Set<VesselTransportMeansEntity> vesselTransportMeansEntities = new HashSet<>();
        for (VesselTransportMeans vesselTransportMeans : vesselList) {
            VesselTransportMeansEntity vesselTransportMeansEntity = VesselTransportMeansMapper.INSTANCE.mapToVesselTransportMeansEntity(vesselTransportMeans);
            vesselTransportMeansEntity.setFaReportDocument(faReportDocumentEntity);
            vesselTransportMeansEntity.setFishingActivity(fishingActivityEntity);
            vesselTransportMeansEntities.add(vesselTransportMeansEntity);
        }
        return vesselTransportMeansEntities;
    }

    protected VesselTransportMeansEntity getFaReportDocVesselTransportMeans(FishingActivityEntity entity) {
        if (entity == null || entity.getFaReportDocument() == null || CollectionUtils.isEmpty(entity.getFaReportDocument().getVesselTransportMeans())) {
            return new VesselTransportMeansEntity();
        }
        return entity.getFaReportDocument().getVesselTransportMeans().iterator().next();
    }

    protected List<String> getFishingGearTypeCodeList(FishingActivityEntity entity) {
        if (entity == null || entity.getFishingGears() == null || entity.getFishingGears().isEmpty()) {
            return Collections.emptyList();
        }
        List<String> fishingGearTypecodeList = new ArrayList<>();
        for (FishingGearEntity fishingGear : entity.getFishingGears()) {
            if (!isItDupicateOrNull(fishingGear.getTypeCode(), fishingGearTypecodeList))
                fishingGearTypecodeList.add(fishingGear.getTypeCode());
        }
        return fishingGearTypecodeList;
    }

    protected Instant getCalculatedStartTime(FishingActivity fishingActivity) {
        if (fishingActivity == null) {
            return null;
        }
        Instant startTimeInstant = null;

        DateTimeType occurrenceDateTime = fishingActivity.getOccurrenceDateTime();
        if (occurrenceDateTime != null && occurrenceDateTime.getDateTime() != null) {
            startTimeInstant = XMLDateUtils.xmlGregorianCalendarToDate(occurrenceDateTime.getDateTime()).toInstant();
        }

        List<DelimitedPeriod> specifiedDelimitedPeriods = fishingActivity.getSpecifiedDelimitedPeriods();
        if (specifiedDelimitedPeriods.size() > 1) {
            throw new IllegalArgumentException("Received more than one DelimitedPeriod in FishingActivity");
        }

        if (!specifiedDelimitedPeriods.isEmpty()) {
            DelimitedPeriod delimitedPeriod = specifiedDelimitedPeriods.get(0);
            Instant startDate = DelimitedPeriodMapper.getStartDate(delimitedPeriod);
            if (startDate != null) {
                if (startTimeInstant == null || startDate.isBefore(startTimeInstant)) {
                    return startDate;
                }
            }
        }

        List<FishingActivity> relatedFishingActivities = fishingActivity.getRelatedFishingActivities();
        for (FishingActivity relatedFishingActivity : relatedFishingActivities) {
            Instant calculatedStartTime = getCalculatedStartTime(relatedFishingActivity);
            if (startTimeInstant == null ||
                    calculatedStartTime != null && calculatedStartTime.isBefore(startTimeInstant)) {
                startTimeInstant = calculatedStartTime;
            }
        }

        return startTimeInstant;
    }

    protected Instant getCalculatedEndTime(FishingActivity fishingActivity) {
        if (fishingActivity == null) {
            return null;
        }
        DateTimeType occurrenceDateTime = fishingActivity.getOccurrenceDateTime();

        List<DelimitedPeriod> specifiedDelimitedPeriods = fishingActivity.getSpecifiedDelimitedPeriods();
        if (specifiedDelimitedPeriods.size() > 1) {
            throw new IllegalArgumentException("Received more than one DelimitedPeriod in FishingActivity");
        }

        if (!specifiedDelimitedPeriods.isEmpty()) {
            DelimitedPeriod delimitedPeriod = specifiedDelimitedPeriods.get(0);
            Instant endDate = DelimitedPeriodMapper.getEndDate(delimitedPeriod);
            if (endDate != null) {
                return endDate;
            } else {
                Duration duration = DelimitedPeriodMapper.getDuration(delimitedPeriod);
                if (duration != null && occurrenceDateTime != null) {
                    Instant instant = XMLDateUtils.xmlGregorianCalendarToDate(occurrenceDateTime.getDateTime()).toInstant();
                    return instant.plus(duration);
                }
            }
        }

        Instant relatedFishingActivityEndDate = null;
        List<FishingActivity> relatedFishingActivities = fishingActivity.getRelatedFishingActivities();
        for (FishingActivity relatedFishingActivity : relatedFishingActivities) {
            Instant calculatedEndTime = getCalculatedEndTime(relatedFishingActivity);
            if (relatedFishingActivityEndDate == null ||
                    calculatedEndTime != null && calculatedEndTime.isAfter(relatedFishingActivityEndDate)) {
                relatedFishingActivityEndDate = calculatedEndTime;
            }
        }

        if (relatedFishingActivityEndDate != null) {
            return relatedFishingActivityEndDate;
        }

        if (occurrenceDateTime != null) {
            return XMLDateUtils.xmlGregorianCalendarToDate(occurrenceDateTime.getDateTime()).toInstant();
        }

        return null;
    }

    protected Date getEndDate(FishingActivityEntity entity) {
        if (entity == null) {
            return null;
        }

        Instant calculatedEndTime = entity.getCalculatedEndTime();
        if (calculatedEndTime != null) {
            return Date.from(calculatedEndTime);
        }

        return null;
    }

    protected List<DelimitedPeriodDTO> getDelimitedPeriodDTOList(FishingActivityEntity entity) {
        if (entity == null) {
            return Collections.emptyList();
        }

        Instant calculatedStartTime = entity.getCalculatedStartTime();
        Instant calculatedEndTime = entity.getCalculatedEndTime();

        DelimitedPeriodDTO delimitedPeriodDTO = new DelimitedPeriodDTO();

        if (calculatedStartTime != null) {
            delimitedPeriodDTO.setStartDate(Date.from(calculatedStartTime));
        }

        if (calculatedEndTime != null) {
            delimitedPeriodDTO.setEndDate(Date.from(calculatedEndTime));
        }

        if (calculatedStartTime != null && calculatedEndTime != null) {
            Duration between = Duration.between(calculatedStartTime, calculatedEndTime);
            long durationInMinutes = between.toMinutes();
            delimitedPeriodDTO.setDuration((double) durationInMinutes);
            delimitedPeriodDTO.setUnitCode("MIN");
        }

        return Lists.newArrayList(delimitedPeriodDTO);
    }

    protected String getUniqueFaReportId(FishingActivityEntity entity) {
        List<FluxReportIdentifierDTO> fluxReportIdentifierDTOs = getUniqueId(entity);
        if (CollectionUtils.isEmpty(fluxReportIdentifierDTOs)) {
            return null;
        }
        // for EU implementation we are expecting only single value for the FLUXReportIdentifier per FLUXReportDocument as per implementation guide.
        FluxReportIdentifierDTO fluxReportIdentifierDTO = fluxReportIdentifierDTOs.get(0);
        return fluxReportIdentifierDTO.getFluxReportId();
    }

    protected String getUniqueFaReportSchemeId(FishingActivityEntity entity) {
        List<FluxReportIdentifierDTO> fluxReportIdentifierDTOs = getUniqueId(entity);
        if (CollectionUtils.isEmpty(fluxReportIdentifierDTOs)) {
            return null;
        }
        // for EU implementation we are expecting only single value for the FLUXReportIdentifier per FLUXReportDocument as per implementation guide.
        FluxReportIdentifierDTO fluxReportIdentifierDTO = fluxReportIdentifierDTOs.get(0);
        return fluxReportIdentifierDTO.getFluxReportSchemeId();
    }

    protected List<FluxReportIdentifierDTO> getUniqueId(FishingActivityEntity entity) {
        if (entity == null || entity.getFaReportDocument() == null || StringUtils.isEmpty(entity.getFaReportDocument().getFluxReportDocument_Id())) {
            return Collections.emptyList();
        }

        FluxReportIdentifierDTO fluxReportIdentifierDTO = new FluxReportIdentifierDTO();
        fluxReportIdentifierDTO.setFluxReportId(entity.getFaReportDocument().getFluxReportDocument_Id());
        fluxReportIdentifierDTO.setFluxReportSchemeId(entity.getFaReportDocument().getFluxReportDocument_IdSchemeId());

        List<FluxReportIdentifierDTO> identifierDTOs = new ArrayList<>();
        identifierDTOs.add(fluxReportIdentifierDTO);

        return identifierDTOs;
    }

    protected List<String> getFromId(FishingActivityEntity entity) {
        if (entity == null ||
                entity.getFaReportDocument() == null ||
                entity.getFaReportDocument().getFluxParty_identifier() == null) {
            return Collections.emptyList();
        }

        return Lists.newArrayList(entity.getFaReportDocument().getFluxParty_identifier());
    }

    protected Map<String, String> getVesselIdType(FishingActivityEntity entity) {
        if (entity == null ||
                entity.getFaReportDocument() == null ||
                entity.getFaReportDocument().getVesselTransportMeans() == null ||
                CollectionUtils.isEmpty(entity.getFaReportDocument().getVesselTransportMeans()) ||
                CollectionUtils.isEmpty(entity.getFaReportDocument().getVesselTransportMeans().iterator().next().getVesselIdentifiers())) {
            return Collections.emptyMap();
        }
        Map<String, String> vesselTransportIdList = new HashMap<>();
        /*
            We can assume that FaReportDocument will always have only one VesselTransportMeans.One to many relation is artificially created
         */
        Set<VesselIdentifierEntity> identifierList = entity.getFaReportDocument().getVesselTransportMeans().iterator().next().getVesselIdentifiers();

        for (VesselIdentifierEntity identity : identifierList) {
            vesselTransportIdList.put(identity.getVesselIdentifierSchemeId(), identity.getVesselIdentifierId());
        }
        return vesselTransportIdList;
    }

    protected List<VesselIdentifierType> getVesselIdentifierTypeList(FishingActivityEntity entity) {
        if (entity == null ||
                entity.getFaReportDocument() == null ||
                entity.getFaReportDocument().getVesselTransportMeans() == null ||
                CollectionUtils.isEmpty(entity.getFaReportDocument().getVesselTransportMeans()) ||
                CollectionUtils.isEmpty(entity.getFaReportDocument().getVesselTransportMeans().iterator().next().getVesselIdentifiers())) {
            return Collections.emptyList();
        }
        List<VesselIdentifierType> identifiers = new ArrayList<>();
        Set<VesselIdentifierEntity> identifierList = entity.getFaReportDocument().getVesselTransportMeans().iterator().next().getVesselIdentifiers();
        for (VesselIdentifierEntity identity : identifierList) {
            identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.valueOf(identity.getVesselIdentifierSchemeId()), identity.getVesselIdentifierId()));
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
        speciesCode.remove(null);
        return new ArrayList<>(speciesCode);
    }

    private void getSpeciesCodeFromAapProduct(FaCatchEntity faCatch, HashSet<String> speciesCode) {
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

    /**
     * This method will calculate sum of all the weights of FACatches
     *
     * @param entity
     */
    protected Double getQuantity(FishingActivityEntity entity) {
        if (entity == null || entity.getFaCatchs() == null) {
            return 0d;
        }

        Double quantity = 0d;
        Set<FaCatchEntity> faCatchList = entity.getFaCatchs();

        for (FaCatchEntity faCatch : faCatchList) {
            if (faCatch.getCalculatedWeightMeasure() != null)
                quantity = quantity + faCatch.getCalculatedWeightMeasure();

            getQuantityFromAapProduct(faCatch, quantity);
        }

        return quantity;
    }

    private void getQuantityFromAapProduct(FaCatchEntity faCatch, Double quantity) {
        if (faCatch.getAapProcesses() == null)
            return;

        for (AapProcessEntity aapProcessEntity : faCatch.getAapProcesses()) {
            Set<AapProductEntity> aapProductList = aapProcessEntity.getAapProducts();
            if (aapProductList != null) {
                for (AapProductEntity aapProduct : aapProductList) {
                    if (aapProduct.getWeightMeasure() != null)
                        quantity = quantity + aapProduct.getWeightMeasure();
                }
            }
        }
    }

    protected Set<String> getFishingGears(FishingActivityEntity entity) {
        if (entity == null || entity.getFishingGears() == null) {
            return Collections.emptySet();
        }
        Set<String> gears = new HashSet<>();
        Set<FishingGearEntity> gearList = entity.getFishingGears();

        for (FishingGearEntity gear : gearList) {
            gears.add(gear.getTypeCode());
        }
        gears.remove(null);
        return gears;
    }

    protected List<String> getAreasForFishingActivity(FishingActivityEntity entity) {
        if (entity == null || entity.getFluxLocations() == null) {
            return Collections.emptyList();
        }
        Set<String> areas = new HashSet<>();
        Set<FluxLocationEntity> fluxLocations = entity.getFluxLocations();

        for (FluxLocationEntity location : fluxLocations) {
            if (FluxLocationEnum.AREA.toString().equalsIgnoreCase(location.getTypeCode())) {
                areas.add(location.getFluxLocationIdentifier());
            }
        }
        areas.remove(null);
        return new ArrayList<>(areas);
    }

    protected List<String> getPortsForFishingActivity(FishingActivityEntity entity) {
        if (entity == null || entity.getFluxLocations() == null) {
            return Collections.emptyList();
        }
        Set<String> ports = new HashSet<>();
        Set<FluxLocationEntity> fluxLocations = entity.getFluxLocations();
        for (FluxLocationEntity location : fluxLocations) {
            if (FluxLocationEnum.LOCATION.toString().equalsIgnoreCase(location.getTypeCode())) {
                ports.add(location.getFluxLocationIdentifier());
            }
        }
        ports.remove(null);
        return new ArrayList<>(ports);
    }

    protected Set<FishingActivityEntity> getAllRelatedFishingActivities(List<FishingActivity> fishingActivity, FaReportDocumentEntity faReportDocumentEntity, FishingActivityEntity parentFishingActivity) {
        Set<FishingActivityEntity> relatedFishingActivityEntities = new HashSet<>();

        if (fishingActivity == null || fishingActivity.isEmpty()) {
            return relatedFishingActivityEntities;
        }
        for (FishingActivity relatedFishingActivity : fishingActivity) {
            FishingActivityEntity relatedFishingActivityEntity = FishingActivityMapper.INSTANCE.mapToFishingActivityEntity(relatedFishingActivity, faReportDocumentEntity);
            relatedFishingActivityEntity.setRelatedFishingActivity(parentFishingActivity);
            relatedFishingActivityEntities.add(relatedFishingActivityEntity);
        }
        return relatedFishingActivityEntities;
    }

    protected Set<FaCatchEntity> mapToFaCatchEntities(List<FACatch> faCatches, FishingActivityEntity fishingActivityEntity) {
        if (faCatches == null || faCatches.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FaCatchEntity> faCatchEntities = new HashSet<>();
        for (FACatch faCatch : faCatches) {
            FaCatchEntity faCatchEntity = FaCatchMapper.INSTANCE.mapToFaCatchEntity(faCatch);

            for (AAPProcess aapProcess : Utils.safeIterable(faCatch.getAppliedAAPProcesses())) {
                AapProcessEntity aapProcessEntity = AapProcessMapper.INSTANCE.mapToAapProcessEntity(aapProcess);
                for (AAPProduct aapProduct : Utils.safeIterable(aapProcess.getResultAAPProducts())) {
                    aapProcessEntity.addAapProducts(AapProductMapper.INSTANCE.mapToAapProductEntity(aapProduct));
                }

                for (CodeType codeType : Utils.safeIterable(aapProcess.getTypeCodes())) {
                    aapProcessEntity.addProcessCode(AapProcessCodeMapper.INSTANCE.mapToAapProcessCodeEntity(codeType));
                }
                faCatchEntity.addAAPProcess(aapProcessEntity);
            }

            SizeDistribution specifiedSizeDistribution = faCatch.getSpecifiedSizeDistribution();
            if (specifiedSizeDistribution != null) {
                if (specifiedSizeDistribution.getCategoryCode() != null) {
                    faCatchEntity.setSizeDistributionCategoryCode(specifiedSizeDistribution.getCategoryCode().getValue());
                    faCatchEntity.setSizeDistributionCategoryCodeListId(specifiedSizeDistribution.getCategoryCode().getListID());
                }

                List<CodeType> classCodes = specifiedSizeDistribution.getClassCodes();
                if (!CollectionUtils.isEmpty(classCodes)) {
                    if (classCodes.size() > 1) {
                        String values = classCodes.stream().map(CodeType::getValue).collect(Collectors.joining(", "));
                        throw new IllegalArgumentException("Failed to map FACatch.specifiedSizeDistribution, more than one SizeDistributionType.ClassCode found. Values: " + values);
                    }
                    faCatchEntity.setSizeDistributionClassCode(classCodes.get(0).getValue());
                    faCatchEntity.setSizeDistributionClassCodeListId(classCodes.get(0).getListID());
                }
            }

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
        if (CollectionUtils.isNotEmpty(fishingGears)) {
            FishingGearEntity fishingGearEntity = fishingGears.iterator().next();
            return fishingGearEntity.getTypeCode();
        }
        return null;
    }

    private FaCatchEntity mapFluxLocationSchemeIds(FACatch faCatch, FaCatchEntity faCatchEntity) {
        List<FLUXLocation> fluxLocations = faCatch.getSpecifiedFLUXLocations();
        if (fluxLocations == null || fluxLocations.isEmpty()) {
            return faCatchEntity;
        }
        for (FLUXLocation location : fluxLocations) {
            IDType id = location.getID();
            if (id != null) {
                FluxLocationSchemeId fluxLocationSchemeId = null;
                try {
                    fluxLocationSchemeId = FluxLocationSchemeId.valueOf(id.getSchemeID());
                } catch (IllegalArgumentException e) {
                    log.warn("Unknown schemeId for FluxLocation." + id.getSchemeID());
                }
                if (fluxLocationSchemeId != null) {
                    switch (fluxLocationSchemeId) {
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
                            log.warn("Unknown schemeId for FluxLocation." + id.getSchemeID());
                            break;
                    }
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
            FluxLocationEntity fluxLocationEntity = FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(fluxLocation);
            fluxLocationEntity.setFluxLocationCatchTypeMapperInfo(FluxLocationCatchTypeEnum.FA_RELATED);
            fluxLocationEntity.setFishingActivity(fishingActivityEntity);
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

    protected String getFishingTripId(FishingActivityEntity fishingActivityEntity) {
        if (fishingActivityEntity == null) {
            return null;
        }
        if (fishingActivityEntity.getFishingTrip() == null) {
            return null;
        }

        return fishingActivityEntity.getFishingTrip().getFishingTripKey().getTripId();
    }

    protected VesselStorageCharacteristicsEntity getSourceVesselStorageCharacteristics(VesselStorageCharacteristic sourceVesselStorageChar, FishingActivityEntity fishingActivityEntity) {
        if (sourceVesselStorageChar == null) {
            return null;
        }
        VesselStorageCharacteristicsEntity vesselStorageCharacteristicsEntity =
                VesselStorageCharacteristicsMapper.INSTANCE.mapToDestVesselStorageCharEntity(sourceVesselStorageChar);
        vesselStorageCharacteristicsEntity.setFishingActivitiesForSourceVesselCharId(fishingActivityEntity);
        return vesselStorageCharacteristicsEntity;
    }

    protected VesselStorageCharacteristicsEntity getDestVesselStorageCharacteristics(VesselStorageCharacteristic destVesselStorageChar, FishingActivityEntity fishingActivityEntity) {
        if (destVesselStorageChar == null) {
            return null;
        }
        VesselStorageCharacteristicsEntity vesselStorageCharacteristicsEntity =
                VesselStorageCharacteristicsMapper.INSTANCE.mapToDestVesselStorageCharEntity(destVesselStorageChar);
        vesselStorageCharacteristicsEntity.setFishingActivitiesForDestVesselCharId(fishingActivityEntity);
        return vesselStorageCharacteristicsEntity;
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

    protected String getFlagState(FishingActivityEntity fishingActivity) {

        Set<VesselTransportMeansEntity> vesselList = fishingActivity.getVesselTransportMeans();
        if (CollectionUtils.isEmpty(vesselList)) {
            return null;
        }

        return vesselList.iterator().next().getCountry();
    }

    private boolean isItDupicateOrNull(String valueTocheck, List<String> listTobeCheckedAgainst) {
        if (valueTocheck == null)
            return true;
        if (CollectionUtils.isNotEmpty(listTobeCheckedAgainst) && listTobeCheckedAgainst.contains(valueTocheck))
            return true;
        return false;
    }

    protected String getRole(ContactPartyEntity entity) {
        if ((entity.getContactPartyRole() != null)
                && (!entity.getContactPartyRole().isEmpty())) {
            return entity.getContactPartyRole().iterator().next().getRoleCode();
        }
        return null;
    }

    protected String getFluxReportIdentifierId(FishingActivityEntity entity) {
        if ((entity.getFaReportDocument() != null)
                && (StringUtils.isNotEmpty(entity.getFaReportDocument().getFluxReportDocument_Id()))) {
            return entity.getFaReportDocument().getFluxReportDocument_Id();
        }
        return null;
    }

    protected String getLandingCountryId(FishingActivityEntity entity) {
        if (entity.getFluxLocations() != null) {
            for (FluxLocationEntity fluxLocation : entity.getFluxLocations()) {
                if (FluxLocationCatchTypeEnum.FA_RELATED.equals(fluxLocation.getFluxLocationCatchTypeMapperInfo())) {
                    return fluxLocation.getCountryId();
                }
            }
        }
        return null;
    }
}