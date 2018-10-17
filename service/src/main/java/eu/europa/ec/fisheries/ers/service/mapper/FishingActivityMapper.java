/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

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
import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxPartyIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearProblemEntity;
import eu.europa.ec.fisheries.ers.fa.entities.SizeDistributionClassCodeEntity;
import eu.europa.ec.fisheries.ers.fa.entities.SizeDistributionEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselStorageCharacteristicsEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationEnum;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationSchemeId;
import eu.europa.ec.fisheries.ers.service.dto.DelimitedPeriodDTO;
import eu.europa.ec.fisheries.ers.service.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.ers.service.dto.FluxReportIdentifierDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.ReportDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingActivitySummary;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselContactPartyType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierType;
import eu.europa.ec.fisheries.uvms.commons.date.XMLDateUtils;
import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.GeometryMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProcess;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProduct;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FACatch;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingActivity;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingGear;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearProblem;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.SizeDistribution;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselStorageCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;


@Mapper(uses = {FishingActivityIdentifierMapper.class, FaCatchMapper.class, DelimitedPeriodMapper.class, XMLDateUtils.class,
        FishingGearMapper.class, GearProblemMapper.class, FishingTripMapper.class,
        FluxCharacteristicsMapper.class, FaReportDocumentMapper.class, GeometryMapper.class, FlapDocumentMapper.class},
        imports = {FaReportStatusType.class}
)
@Slf4j
public abstract class FishingActivityMapper extends BaseMapper {

    public static final FishingActivityMapper INSTANCE = Mappers.getMapper(FishingActivityMapper.class);
    public static final String LOCATION_AREA = FluxLocationEnum.AREA.toString();
    public static final String LOCATION_PORT = FluxLocationEnum.LOCATION.toString();

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
            @Mapping(target = "operationsQuantity", source = "fishingActivity.operationsQuantity"),
            @Mapping(target = "fishingDurationMeasure", source = "fishingActivity.fishingDurationMeasure.value"),
            @Mapping(target = "fishingDurationMeasureCode", source = "fishingActivity.fishingDurationMeasure.unitCode"),
            @Mapping(target = "fishingDurationMeasureUnitCodeListVersionID", source = "fishingActivity.fishingDurationMeasure.unitCodeListVersionID"),
            @Mapping(target = "faReportDocument", expression = "java(faReportDocumentEntity)"),// FIXME
            @Mapping(target = "sourceVesselCharId", expression = "java(getSourceVesselStorageCharacteristics(fishingActivity.getSourceVesselStorageCharacteristic(), fishingActivityEntity))"),
            @Mapping(target = "destVesselCharId", expression = "java(getDestVesselStorageCharacteristics(fishingActivity.getDestinationVesselStorageCharacteristic(), fishingActivityEntity))"),
            @Mapping(target = "fishingActivityIdentifiers", expression = "java(mapToFishingActivityIdentifierEntities(fishingActivity.getIDS(), fishingActivityEntity))"),
            @Mapping(target = "delimitedPeriods", expression = "java(getDelimitedPeriodEntities(fishingActivity.getSpecifiedDelimitedPeriods(), fishingActivityEntity))"),
            @Mapping(target = "fishingTrips", expression = "java(BaseMapper.mapToFishingTripEntitySet(fishingActivity.getSpecifiedFishingTrip(), fishingActivityEntity))"),
            @Mapping(target = "fishingGears", expression = "java(getFishingGearEntities(fishingActivity.getSpecifiedFishingGears(), fishingActivityEntity))"),
            @Mapping(target = "fluxCharacteristics", ignore = true),
            @Mapping(target = "gearProblems", expression = "java(getGearProblemEntities(fishingActivity.getSpecifiedGearProblems(), fishingActivityEntity))"),
            @Mapping(target = "fluxLocations", expression = "java(getFluxLocationEntities(fishingActivity.getRelatedFLUXLocations(), fishingActivityEntity))"),
            @Mapping(target = "faCatchs", expression = "java(mapToFaCatchEntities(fishingActivity.getSpecifiedFACatches(), fishingActivityEntity))"),
            @Mapping(target = "vesselTransportMeans", expression = "java(getVesselTransportMeansEntity(fishingActivity, faReportDocumentEntity, fishingActivityEntity))"),
            @Mapping(target = "allRelatedFishingActivities", expression = "java(getAllRelatedFishingActivities(fishingActivity.getRelatedFishingActivities(), faReportDocumentEntity, fishingActivityEntity))"),
            @Mapping(target = "flagState", expression = "java(getFlagState(fishingActivity))"),
            @Mapping(target = "calculatedStartTime", expression = "java(getCalculatedStartTime(fishingActivity))"), // FIXME
            @Mapping(target = "latest", constant = "true"),
            @Mapping(target = "flapDocuments", ignore = true)
    })
    public abstract FishingActivityEntity mapToFishingActivityEntity(FishingActivity fishingActivity, FaReportDocumentEntity faReportDocumentEntity, @MappingTarget FishingActivityEntity fishingActivityEntity);// FIXME

    @Mappings({
            @Mapping(target = "fishingActivityId", source = "id"),
            @Mapping(target = "uniqueFAReportId", expression = "java(getUniqueId(entity))"),
            @Mapping(target = "faReportID", source = "faReportDocument.id"),
            @Mapping(target = "fromId", expression = "java(getFromId(entity))"),
            @Mapping(target = "fromName", source = "faReportDocument.fluxReportDocument.fluxParty.fluxPartyName"),
            @Mapping(target = "vesselTransportMeansName", expression = "java(getFaReportDocVesselTransportMeans(entity).getName())"),
            @Mapping(target = "purposeCode", expression = "java(FaReportStatusType.valueOf(entity.getFaReportDocument().getStatus()).getPurposeCode().toString())"),
            @Mapping(target = "FAReportType", source = "faReportDocument.typeCode"),
            @Mapping(source = "typeCode", target = "activityType"),
            @Mapping(target = "areas", expression = "java(getAreasForFishingActivity(entity))"),
            @Mapping(target = "port", expression = "java(getPortsForFishingActivity(entity))"),
            @Mapping(target = "fishingGear", expression = "java(getFishingGears(entity))"),
            @Mapping(target = "speciesCode", expression = "java(getSpeciesCode(entity))"),
            @Mapping(target = "quantity", expression = "java(getQuantity(entity))"),
            @Mapping(target = "fluxLocations", expression = "java(null)"),
            @Mapping(target = "fishingGears", expression = "java(null)"),
            @Mapping(target = "fluxCharacteristics", expression = "java(null)"),
            @Mapping(target = "delimitedPeriod", expression = "java(null)"),
            @Mapping(target = "dataSource", source = "faReportDocument.source"),
            @Mapping(target = "vesselIdTypes", expression = "java(getVesselIdType(entity))"),
            @Mapping(target = "startDate", source = "calculatedStartTime"),
            @Mapping(target = "endDate", expression = "java(getEndDate(entity))"),
            @Mapping(target = "hasCorrection", expression = "java(getCorrection(entity))"),
            @Mapping(target = "fluxReportReferenceId", source = "faReportDocument.fluxReportDocument.referenceId"),
            @Mapping(target = "fluxReportReferenceSchemeId", source = "faReportDocument.fluxReportDocument.referenceSchemeId"),
            @Mapping(target = "cancelingReportID", source = "canceledBy"),
            @Mapping(target = "deletingReportID", source = "deletedBy")
    })
    public abstract FishingActivityReportDTO mapToFishingActivityReportDTO(FishingActivityEntity entity);

    @Mappings({
            @Mapping(target = "activityType", source = "typeCode"),
            @Mapping(target = "activityId", source = "id"),
            @Mapping(target = "geometry", source = "wkt"),
            @Mapping(target = "acceptedDateTime", source = "faReportDocument.acceptedDatetime"),
            @Mapping(target = "dataSource", source = "faReportDocument.source"),
            @Mapping(target = "reportType", source = "faReportDocument.typeCode"),
            @Mapping(target = "purposeCode", source = "faReportDocument.fluxReportDocument.purposeCode"),
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
            @Mapping(target = "faReferenceID", source = "faReportDocument.fluxReportDocument.referenceId"),
            @Mapping(target = "faReferenceSchemeID", source = "faReportDocument.fluxReportDocument.referenceSchemeId"),
            @Mapping(target = "faUniqueReportID", expression = "java(getUniqueFaReportId(entity))"),
            @Mapping(target = "faUniqueReportSchemeID", expression = "java(getUniqueFaReportSchemeId(entity))"),// FIXME entity method
            @Mapping(target = "reason", source = "reasonCode"),
            @Mapping(target = "purposeCode", source = "faReportDocument.fluxReportDocument.purposeCode"),
            @Mapping(target = "faReportDocumentType", source = "faReportDocument.typeCode"),
            @Mapping(target = "faReportAcceptedDateTime", source = "faReportDocument.acceptedDatetime"),
            @Mapping(target = "correction", expression = "java(getCorrection(entity))"), // FIXME entity method
            @Mapping(target = "delimitedPeriod", expression = "java(getDelimitedPeriodDTOList(entity))"),
            @Mapping(target = "fluxLocations", ignore = true),
            @Mapping(target = "faReportID", source = "faReportDocument.id")
    })
    public abstract ReportDTO mapToReportDTO(FishingActivityEntity entity);

    @Mappings({
            @Mapping(target = "type", source = "typeCode"),
            @Mapping(target = "occurrence", source = "occurence"),
            @Mapping(target = "identifiers", source = "fishingActivityIdentifiers"),
    })
    public abstract ActivityDetailsDto mapFishingActivityEntityToActivityDetailsDto(FishingActivityEntity entity);

    @Mappings({
            @Mapping(target = "faIdentifierId", source = "value"),
            @Mapping(target = "faIdentifierSchemeId", source = "schemeID")
    })
    protected abstract FishingActivityIdentifierEntity mapToFishingActivityIdentifierEntity(IDType idType);

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

    protected Date getCalculatedStartTime(FishingActivity fishingActivity) {
        if (fishingActivity == null) {
            return null;
        }
        DateTimeType dateTimeType;
        DateTimeType occurrenceDateTime = fishingActivity.getOccurrenceDateTime();
        if (occurrenceDateTime != null && occurrenceDateTime.getDateTime() != null) {
            return XMLDateUtils.xmlGregorianCalendarToDate(occurrenceDateTime.getDateTime());
        }
        if (CollectionUtils.isNotEmpty(fishingActivity.getSpecifiedDelimitedPeriods())) {
            List<DelimitedPeriod> delimitedPeriodEntities = fishingActivity.getSpecifiedDelimitedPeriods();
            dateTimeType = delimitedPeriodEntities.iterator().next().getStartDateTime();
            if (dateTimeType != null && dateTimeType.getDateTime() != null) {
                return XMLDateUtils.xmlGregorianCalendarToDate(dateTimeType.getDateTime());
            }
        }
        // We reached till this point of code means FishingActivity has neither occurrence date or startDate for DelimitedPeriod.
        // In such cases, we need to check if its subactivities have date mentioned.
        // If yes, then take the first subactivity occurrence date and consider it as start date for parent fishing activity
        List<FishingActivity> relatedFishingActivities = fishingActivity.getRelatedFishingActivities();
        if (CollectionUtils.isNotEmpty(relatedFishingActivities)) {
            for (FishingActivity subFishingActivity : relatedFishingActivities) {
                if (subFishingActivity.getOccurrenceDateTime() != null || (CollectionUtils.isNotEmpty(fishingActivity.getSpecifiedDelimitedPeriods()) &&
                        fishingActivity.getSpecifiedDelimitedPeriods().iterator().next().getStartDateTime() != null)) {
                    dateTimeType = subFishingActivity.getOccurrenceDateTime();
                    if (dateTimeType == null) {
                        dateTimeType = fishingActivity.getSpecifiedDelimitedPeriods().iterator().next().getStartDateTime();
                    }
                    if (dateTimeType != null && dateTimeType.getDateTime() != null) {
                        return XMLDateUtils.xmlGregorianCalendarToDate(dateTimeType.getDateTime());
                    }
                }
            }
        }
        return null;
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
        if (entity == null || entity.getFaReportDocument() == null || entity.getFaReportDocument().getFluxReportDocument() == null) {
            return Collections.emptyList();
        }
        List<FluxReportIdentifierDTO> identifierDTOs = new ArrayList<>();
        Set<FluxReportIdentifierEntity> identifiers = entity.getFaReportDocument().getFluxReportDocument().getFluxReportIdentifiers();
        for (FluxReportIdentifierEntity fluxReportIdentifierEntity : identifiers) {
            identifierDTOs.add(FluxReportIdentifierMapper.INSTANCE.mapToFluxReportIdentifierDTO(fluxReportIdentifierEntity));
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
        Set<String> fromIdList = new HashSet<>();
        Set<FluxPartyIdentifierEntity> idSet = entity.getFaReportDocument().getFluxReportDocument().getFluxParty().getFluxPartyIdentifiers();
        for (FluxPartyIdentifierEntity fluxPartyIdentifierEntity : idSet) {
            fromIdList.add(fluxPartyIdentifierEntity.getFluxPartyIdentifierId());
        }
        fromIdList.remove(null);
        return new ArrayList<>(fromIdList);
    }

    protected Map<String, String> getVesselIdType(FishingActivityEntity entity) {
        if (entity == null || entity.getFaReportDocument() == null || entity.getFaReportDocument().getVesselTransportMeans() == null
                || CollectionUtils.isEmpty(entity.getFaReportDocument().getVesselTransportMeans()) || CollectionUtils.isEmpty(entity.getFaReportDocument().getVesselTransportMeans().iterator().next().getVesselIdentifiers())) {
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
        if (entity == null || entity.getFaReportDocument() == null || entity.getFaReportDocument().getVesselTransportMeans() == null
                || CollectionUtils.isEmpty(entity.getFaReportDocument().getVesselTransportMeans()) || CollectionUtils.isEmpty(entity.getFaReportDocument().getVesselTransportMeans().iterator().next().getVesselIdentifiers())) {
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

    /**
     * This method will calculate sum of all the weights of FACatches
     *
     * @param entity
     * @return
     */
    protected Double getQuantity(FishingActivityEntity entity) {
        if (entity == null || entity.getFaCatchs() == null) {
            return new Double(0);
        }

        Double quantity = new Double(0);
        Set<FaCatchEntity> faCatchList = entity.getFaCatchs();

        for (FaCatchEntity faCatch : faCatchList) {
            if (faCatch.getCalculatedWeightMeasure() != null)
                quantity = quantity + faCatch.getCalculatedWeightMeasure();

            getQuantityFromAapProduct(faCatch, quantity);
        }

        return quantity;
    }

    protected void getQuantityFromAapProduct(FaCatchEntity faCatch, Double quantity) {
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
            if (LOCATION_AREA.equalsIgnoreCase(location.getTypeCode())) {
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
            if (LOCATION_PORT.equalsIgnoreCase(location.getTypeCode())) {
                ports.add(location.getFluxLocationIdentifier());
            }
        }
        ports.remove(null);
        return new ArrayList<>(ports);
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

    protected Set<FaCatchEntity> mapToFaCatchEntities(List<FACatch> faCatches, FishingActivityEntity fishingActivityEntity) {
        if (faCatches == null || faCatches.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FaCatchEntity> faCatchEntities = new HashSet<>();
        for (FACatch faCatch : faCatches) {
            FaCatchEntity faCatchEntity = FaCatchMapper.INSTANCE.mapToFaCatchEntity(faCatch);
            List<AAPProcess> appliedAAPProcesses = faCatch.getAppliedAAPProcesses();
            if (CollectionUtils.isNotEmpty(appliedAAPProcesses)) {
                for (AAPProcess aapProcess : appliedAAPProcesses) {
                    AapProcessEntity aapProcessEntity = AapProcessMapper.INSTANCE.mapToAapProcessEntity(aapProcess);
                    List<AAPProduct> resultAAPProducts = aapProcess.getResultAAPProducts();
                    if (CollectionUtils.isNotEmpty(resultAAPProducts)) {
                        for (AAPProduct aapProduct : resultAAPProducts) {
                            aapProcessEntity.addAapProducts(AapProductMapper.INSTANCE.mapToAapProductEntity(aapProduct));
                        }
                    }
                    List<CodeType> codeTypeList = aapProcess.getTypeCodes();
                    if (CollectionUtils.isNotEmpty(codeTypeList)) {
                        for (CodeType codeType : codeTypeList) {
                            aapProcessEntity.addProcessCode(AapProcessCodeMapper.INSTANCE.mapToAapProcessCodeEntity(codeType));
                        }
                    }
                    faCatchEntity.addAAPProcess(aapProcessEntity);
                }
            }

            SizeDistribution specifiedSizeDistribution = faCatch.getSpecifiedSizeDistribution();
            if (specifiedSizeDistribution != null) {
                SizeDistributionEntity sizeDistributionEntity = SizeDistributionMapper.INSTANCE.mapToSizeDistributionEntity(specifiedSizeDistribution);
                sizeDistributionEntity.setFaCatch(faCatchEntity);
                List<CodeType> classCodes = specifiedSizeDistribution.getClassCodes();
                if (CollectionUtils.isNotEmpty(classCodes)) {
                    for (CodeType classCode : classCodes) {
                        SizeDistributionClassCodeEntity sizeDistributionClassCodeEntity = SizeDistributionMapper.INSTANCE.mapToSizeDistributionClassCodeEntity(classCode);
                        sizeDistributionEntity.addSizeDistribution(sizeDistributionClassCodeEntity);
                    }

                }
                faCatchEntity.setSizeDistribution(sizeDistributionEntity);
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
            if (location.getRegionalFisheriesManagementOrganizationCode() != null) {
                faCatchEntity.setRfmo(location.getRegionalFisheriesManagementOrganizationCode().getValue());
            }
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
            fluxLocationEntity.setFluxLocationType(FluxLocationCatchTypeEnum.FA_RELATED.getType());
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

    protected Set<FishingGearEntity> getFishingGearEntities(List<FishingGear> fishingGears, FishingActivityEntity fishingActivityEntity) {
        if (fishingGears == null || fishingGears.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FishingGearEntity> fishingGearEntities = new HashSet<>();
        for (FishingGear fishingGear : fishingGears) {
            FishingGearEntity fishingGearEntity = FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear);
            fishingGearEntity.setFishingActivity(fishingActivityEntity);
            fishingGearEntities.add(fishingGearEntity);
        }
        return fishingGearEntities;
    }

    protected String getFishingTripId(FishingActivityEntity fishingActivityEntity) {
        if (fishingActivityEntity == null || CollectionUtils.isEmpty(fishingActivityEntity.getFishingTrips())) {
            return null;
        }
        Set<FishingTripEntity> fishingTripEntities = fishingActivityEntity.getFishingTrips();
        FishingTripEntity fishingTripEntity = fishingTripEntities.iterator().next();
        if (CollectionUtils.isEmpty(fishingTripEntity.getFishingTripIdentifiers()))
            return null;
        return fishingTripEntity.getFishingTripIdentifiers().iterator().next().getTripId();
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
                && (entity.getFaReportDocument().getFluxReportDocument() != null)
                && (!entity.getFaReportDocument().getFluxReportDocument().getFluxReportIdentifiers().isEmpty())) {
            return entity.getFaReportDocument().getFluxReportDocument().getFluxReportIdentifiers().iterator().next().getFluxReportIdentifierId();
        }
        return null;
    }

    protected String getLandingCountryId(FishingActivityEntity entity) {
        if (entity.getFluxLocations() != null) {
            for (FluxLocationEntity fluxLocation : entity.getFluxLocations()) {
                if (FluxLocationCatchTypeEnum.FA_RELATED.getType().equals(fluxLocation.getFluxLocationType())) {
                    return fluxLocation.getCountryId();
                }
            }
        }
        return null;
    }

}