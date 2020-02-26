package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.dao.FluxLocationDao;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.*;
import eu.europa.ec.fisheries.uvms.activity.service.util.Utils;
import eu.europa.ec.fisheries.uvms.commons.date.XMLDateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.*;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FishingActivityMapper {

    @Inject
    FluxLocationMapper locationMapper;
    
    @Inject
    AapProcessMapper aapProcessMapper;

    @Inject
    AapProductMapper aapProductMapper;

    @Inject
    AapProcessCodeMapper aapProcessCodeMapper;

    @Inject
    VesselTransportMeansMapper vesselTransportMeansMapper;

    @Inject
    GearProblemMapper gearProblemMapper;

    @Inject
    VesselStorageCharacteristicsMapper vesselStorageCharacteristicsMapper;

    @Inject
    FaCatchMapper faCatchMapper;

    @PersistenceContext(unitName = "activityPUpostgres")
    EntityManager em;

    public FishingActivityEntity mapToFishingActivityEntity(FishingActivity fishingActivity, FaReportDocumentEntity faReportDocumentEntity) {
        FishingActivityEntity entity = new FishingActivityEntity();
        entity.setLatest(true);
        entity.setCalculatedEndTime(getCalculatedEndTime(fishingActivity));
        entity.setCalculatedStartTime(getCalculatedStartTime(fishingActivity));
        entity.setFlagState(getFlagState(fishingActivity));
        entity.setAllRelatedFishingActivities(getAllRelatedFishingActivities(fishingActivity.getRelatedFishingActivities(), faReportDocumentEntity, entity));
        entity.setVesselTransportMeans(getVesselTransportMeansEntity(fishingActivity, faReportDocumentEntity, entity));
        entity.setFaCatchs(mapToFaCatchEntities(fishingActivity.getSpecifiedFACatches(), entity));
        entity.setFluxLocations(getFluxLocationEntities(fishingActivity.getRelatedFLUXLocations(), entity));
        entity.setGearProblems(getGearProblemEntities(fishingActivity.getSpecifiedGearProblems(), entity));
        entity.setFishingTrip(BaseMapper.mapToFishingTripEntity(fishingActivity.getSpecifiedFishingTrip()));
        entity.setSourceVesselCharId(getSourceVesselStorageCharacteristics(fishingActivity.getSourceVesselStorageCharacteristic(), entity));
        entity.setDestVesselCharId(getDestVesselStorageCharacteristics(fishingActivity.getDestinationVesselStorageCharacteristic(), entity));
        entity.setFishingDurationMeasure(getDouble(fishingActivity.getFishingDurationMeasure()));
        entity.setFishingDurationMeasureCode(fishingActivity.getFishingDurationMeasure() != null ? fishingActivity.getFishingDurationMeasure().getUnitCode() : null);
        entity.setFishingDurationMeasureUnitCodeListVersionID(fishingActivity.getFishingDurationMeasure() != null ? fishingActivity.getFishingDurationMeasure().getUnitCodeListVersionID() : null);
        entity.setTypeCode(fishingActivity.getTypeCode().getValue());
        entity.setTypeCodeListid(getCodeTypeListId(fishingActivity.getTypeCode()));
        entity.setOccurence(fishingActivity.getOccurrenceDateTime() != null ? fishingActivity.getOccurrenceDateTime().getDateTime().toGregorianCalendar().toInstant() : null);
        entity.setReasonCode(fishingActivity.getReasonCode() != null ? fishingActivity.getReasonCode().getValue() : null);
        entity.setReasonCodeListId(getCodeTypeListId(fishingActivity.getReasonCode()));
        entity.setVesselActivityCode(getCodeType(fishingActivity.getVesselRelatedActivityCode()));
        entity.setVesselActivityCodeListId(getCodeTypeListId(fishingActivity.getVesselRelatedActivityCode()));
        entity.setFisheryTypeCode(getCodeType(fishingActivity.getFisheryTypeCode()));
        entity.setFisheryTypeCodeListId(getCodeTypeListId(fishingActivity.getFisheryTypeCode()));
        entity.setSpeciesTargetCode(getCodeType(fishingActivity.getSpeciesTargetCode()));
        entity.setSpeciesTargetCodeListId(getCodeTypeListId(fishingActivity.getSpeciesTargetCode()));

        if(fishingActivity.getOperationsQuantity() != null) {
            QuantityType quantityType = new QuantityType();
            quantityType.setUnitCode(fishingActivity.getOperationsQuantity().getUnitCode());
            quantityType.setUnitCodeListID(fishingActivity.getOperationsQuantity().getUnitCodeListID());
            quantityType.setValue(getDouble(fishingActivity.getOperationsQuantity()));
            entity.setOperationsQuantity(quantityType);
        }

        entity.setFaReportDocument(faReportDocumentEntity);

        return entity;
    }

    private Double getDouble(un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType measureType) {
        if(measureType != null) {
            if(measureType.getValue() != null) {
                return measureType.getValue().doubleValue();
            }
        }
        return null;
    }
    private Double getDouble(un.unece.uncefact.data.standard.unqualifieddatatype._20.QuantityType quantityType) {
        if(quantityType != null) {
            if(quantityType.getValue() != null) {
                return quantityType.getValue().doubleValue();
            }
        }
        return null;
    }

    private String getIdType(IDType idType) {
        return (idType == null) ? null : idType.getValue();
    }
    private String getCodeTypeListId(CodeType codeType) {
        return (codeType == null) ? null : codeType.getListID();
    }
    private String getCodeType(CodeType codeType) {
        return (codeType == null) ? null : codeType.getValue();
    }
    private String getCountry(VesselCountry country) {
        if (country == null) {
            return null;
        }
        return getIdType(country.getID());
    }


    protected Set<VesselTransportMeansEntity> getVesselTransportMeansEntity(FishingActivity fishingActivity, FaReportDocumentEntity faReportDocumentEntity, FishingActivityEntity fishingActivityEntity) {
        List<VesselTransportMeans> vesselList = fishingActivity.getRelatedVesselTransportMeans();
        if (vesselList == null || vesselList.isEmpty()) {
            return null;
        }
        Set<VesselTransportMeansEntity> vesselTransportMeansEntities = new HashSet<>();
        for (VesselTransportMeans vesselTransportMeans : vesselList) {
            VesselTransportMeansEntity vesselTransportMeansEntity = vesselTransportMeansMapper.mapToVesselTransportMeansEntity(vesselTransportMeans);
            vesselTransportMeansEntity.setFaReportDocument(faReportDocumentEntity);
            vesselTransportMeansEntity.setFishingActivity(fishingActivityEntity);
            vesselTransportMeansEntities.add(vesselTransportMeansEntity);
        }
        return vesselTransportMeansEntities;
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

        if (fishingActivity.getSpecifiedDelimitedPeriods().size() > 1) {
            throw new IllegalArgumentException("Received more than one DelimitedPeriod in FishingActivity");
        }

        Optional<Instant> endTimeFromDelimitedTimePeriod = getEndTimeFromDelimitedTimePeriod(fishingActivity);
        if (endTimeFromDelimitedTimePeriod.isPresent()) {
            return endTimeFromDelimitedTimePeriod.get();
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

        if (fishingActivity.getOccurrenceDateTime() != null) {
            return XMLDateUtils.xmlGregorianCalendarToDate(fishingActivity.getOccurrenceDateTime().getDateTime()).toInstant();
        }

        return null;
    }

    private Optional<Instant> getEndTimeFromDelimitedTimePeriod(FishingActivity fishingActivity) {
        List<DelimitedPeriod> specifiedDelimitedPeriods = fishingActivity.getSpecifiedDelimitedPeriods();
        if (specifiedDelimitedPeriods.isEmpty()) {
            return Optional.empty();
        }

        DateTimeType occurrenceDateTime = fishingActivity.getOccurrenceDateTime();
        DelimitedPeriod delimitedPeriod = specifiedDelimitedPeriods.get(0);
        Instant endDate = DelimitedPeriodMapper.getEndDate(delimitedPeriod);
        if (endDate != null) {
            return Optional.of(endDate);
        } else {
            Duration duration = DelimitedPeriodMapper.getDuration(delimitedPeriod);
            if (duration != null && occurrenceDateTime != null) {
                Instant instant = XMLDateUtils.xmlGregorianCalendarToDate(occurrenceDateTime.getDateTime()).toInstant();
                return Optional.of(instant.plus(duration));
            }
        }

        return Optional.empty();
    }

    private void mapAapProcesses(FACatch faCatch, FaCatchEntity faCatchEntity) {
        for (AAPProcess aapProcess : Utils.safeIterable(faCatch.getAppliedAAPProcesses())) {
            AapProcessEntity aapProcessEntity = aapProcessMapper.mapToAapProcessEntity(aapProcess);
            for (AAPProduct aapProduct : Utils.safeIterable(aapProcess.getResultAAPProducts())) {
                aapProcessEntity.addAapProducts(aapProductMapper.mapToAapProductEntity(aapProduct));
            }

            for (CodeType codeType : Utils.safeIterable(aapProcess.getTypeCodes())) {
                aapProcessEntity.addProcessCode(aapProcessCodeMapper.mapToAapProcessCodeEntity(codeType));
            }
            faCatchEntity.addAAPProcess(aapProcessEntity);
        }
    }

    private void mapSizeDistribution(FACatch faCatch, FaCatchEntity faCatchEntity) {
        SizeDistribution specifiedSizeDistribution = faCatch.getSpecifiedSizeDistribution();
        if (specifiedSizeDistribution == null) {
            return;
        }

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

    protected Set<FishingActivityEntity> getAllRelatedFishingActivities(List<FishingActivity> fishingActivity, FaReportDocumentEntity faReportDocumentEntity, FishingActivityEntity parentFishingActivity) {
        Set<FishingActivityEntity> relatedFishingActivityEntities = new HashSet<>();

        if (fishingActivity == null || fishingActivity.isEmpty()) {
            return relatedFishingActivityEntities;
        }
        for (FishingActivity relatedFishingActivity : fishingActivity) {
            FishingActivityEntity relatedFishingActivityEntity = mapToFishingActivityEntity(relatedFishingActivity, faReportDocumentEntity);
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
            FaCatchEntity faCatchEntity = faCatchMapper.mapToFaCatchEntity(faCatch);
            mapAapProcesses(faCatch, faCatchEntity);
            mapSizeDistribution(faCatch, faCatchEntity);
            faCatchEntity.setFishingActivity(fishingActivityEntity);
            faCatchEntity.setGearTypeCode(extractFishingGearTypeCode(faCatchEntity.getFishingGears()));
            faCatchEntity.setPresentation(extractPresentation(faCatchEntity.getAapProcesses()));
            faCatchEntities.add(faCatchEntity);
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

    protected Set<FluxLocationEntity> getFluxLocationEntities(List<FLUXLocation> fluxLocations, FishingActivityEntity fishingActivityEntity) {
        if (fluxLocations == null || fluxLocations.isEmpty()) {
            return Collections.emptySet();
        }
        FluxLocationDao fluxLocationDao = new FluxLocationDao(em);
        Set<FluxLocationEntity> fluxLocationEntities = new HashSet<>();
        for (FLUXLocation fluxLocation : fluxLocations) {
            if(fluxLocation.getTypeCode() != null && fluxLocation.getTypeCode().getValue().equals("POSITION")) {
                FLUXGeographicalCoordinate coordinate = fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate();
                if(coordinate != null) {
                    fishingActivityEntity.setLongitude(coordinate.getLongitudeMeasure().getValue().doubleValue());
                    fishingActivityEntity.setLatitude(coordinate.getLatitudeMeasure().getValue().doubleValue());
                    if(coordinate.getAltitudeMeasure() != null){
                        fishingActivityEntity.setAltitude(coordinate.getAltitudeMeasure().getValue().doubleValue());
                    }
                }
            } else {
                FluxLocationEntity fluxLocationEntity = fluxLocationDao.findLocation(fluxLocation.getID());
                if(fluxLocationEntity == null) {
                    fluxLocationEntity = locationMapper.mapToFluxLocationEntity(fluxLocation);
                    em.persist(fluxLocationEntity);
                }
                fluxLocationEntities.add(fluxLocationEntity);
            }
        }
        return fluxLocationEntities;
    }

    protected Set<GearProblemEntity> getGearProblemEntities(List<GearProblem> gearProblems, FishingActivityEntity fishingActivityEntity) {
        if (gearProblems == null || gearProblems.isEmpty()) {
            return Collections.emptySet();
        }
        Set<GearProblemEntity> gearProblemEntities = new HashSet<>();
        for (GearProblem gearProblem : gearProblems) {
            GearProblemEntity gearProblemEntity = gearProblemMapper.mapToGearProblemEntity(gearProblem);
            gearProblemEntity.setFishingActivity(fishingActivityEntity);
            gearProblemEntities.add(gearProblemEntity);
        }
        return gearProblemEntities;
    }

    protected VesselStorageCharacteristicsEntity getSourceVesselStorageCharacteristics(VesselStorageCharacteristic sourceVesselStorageChar, FishingActivityEntity fishingActivityEntity) {
        if (sourceVesselStorageChar == null) {
            return null;
        }
        VesselStorageCharacteristicsEntity vesselStorageCharacteristicsEntity =
                vesselStorageCharacteristicsMapper.mapToDestVesselStorageCharEntity(sourceVesselStorageChar);
        vesselStorageCharacteristicsEntity.setFishingActivitiesForSourceVesselCharId(fishingActivityEntity);
        return vesselStorageCharacteristicsEntity;
    }

    protected VesselStorageCharacteristicsEntity getDestVesselStorageCharacteristics(VesselStorageCharacteristic destVesselStorageChar, FishingActivityEntity fishingActivityEntity) {
        if (destVesselStorageChar == null) {
            return null;
        }
        VesselStorageCharacteristicsEntity vesselStorageCharacteristicsEntity =
                vesselStorageCharacteristicsMapper.mapToDestVesselStorageCharEntity(destVesselStorageChar);
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

}
