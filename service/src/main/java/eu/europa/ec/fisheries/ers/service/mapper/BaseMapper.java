/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import static com.google.common.collect.Sets.newHashSet;
import static org.mockito.internal.util.collections.Sets.newSet;

import javax.validation.constraints.NotNull;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearRoleEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearCharacteristicEntity;
import eu.europa.ec.fisheries.ers.fa.entities.RegistrationEventEntity;
import eu.europa.ec.fisheries.ers.fa.entities.RegistrationLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.SizeDistributionClassCodeEntity;
import eu.europa.ec.fisheries.ers.fa.entities.SizeDistributionEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationEnum;
import eu.europa.ec.fisheries.ers.fa.utils.UnitCodeEnum;
import eu.europa.ec.fisheries.ers.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.ers.service.dto.DelimitedPeriodDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.ers.service.dto.view.PositionDto;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.commons.date.XMLDateUtils;
import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.commons.geometry.utils.GeometryUtils;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingTrip;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselCountry;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.TextType;

@Slf4j
@NoArgsConstructor
public class BaseMapper {

    public static Set<FluxLocationDto> mapFromFluxLocation(Set<FluxLocationEntity> fLocEntities) {
        Set<FluxLocationDto> locationDtos = FluxLocationMapper.INSTANCE.mapEntityToFluxLocationDto(fLocEntities);
        if (locationDtos != null) {
            return locationDtos;
        }
        return Sets.newHashSet();
    }

    public static Set<FluxLocationDto> mapFromFluxLocation(Set<FluxLocationEntity> fLocEntities, final FluxLocationCatchTypeEnum typeCode) {
        Iterable<FluxLocationEntity> filtered = Iterables.filter(fLocEntities, new Predicate<FluxLocationEntity>() {
            @Override
            public boolean apply(FluxLocationEntity p) {
                return typeCode.name().equals(p.getFluxLocationType());
            }
        });
        return mapFromFluxLocation(newHashSet(filtered.iterator()));
    }

    public static Set<FluxLocationDto> mapFromFluxLocation(Set<FluxLocationEntity> fLocEntities, final FluxLocationEnum typeCode) {
        Iterable<FluxLocationEntity> filtered = Iterables.filter(fLocEntities, new Predicate<FluxLocationEntity>() {
            @Override
            public boolean apply(FluxLocationEntity p) {
                return typeCode.name().equals(p.getTypeCode());
            }
        });
        return mapFromFluxLocation(newHashSet(filtered.iterator()));
    }

    public static Set<FishingTripEntity> mapToFishingTripEntitySet(FishingTrip fishingTrip, FishingActivityEntity fishingActivityEntity) {
        if (fishingTrip == null) {
            return Collections.emptySet();
        }
        FishingTripEntity fishingTripEntity = mapToFishingTripEntity(fishingTrip);
        fishingTripEntity.setFishingActivity(fishingActivityEntity);
        return new HashSet<>(Collections.singletonList(fishingTripEntity));
    }

    public static Set<FishingTripEntity> mapToFishingTripEntitySet(List<FishingTrip> fishingTrips, FaCatchEntity faCatchEntity) {
        if (fishingTrips == null || fishingTrips.isEmpty()) {
            return Collections.emptySet();
        }
        Set<FishingTripEntity> fishingTripEntities = new HashSet<>();
        for (FishingTrip fishingTrip : fishingTrips) {
            FishingTripEntity fishingTripEntity = mapToFishingTripEntity(fishingTrip);

            fishingTripEntity.setFaCatch(faCatchEntity);
            fishingTripEntities.add(fishingTripEntity);
        }
        return fishingTripEntities;
    }

    private static FishingTripEntity mapToFishingTripEntity(FishingTrip fishingTrip) {
        FishingTripEntity fishingTripEntity = FishingTripMapper.INSTANCE.mapToFishingTripEntity(fishingTrip);
        List<IDType> ids = fishingTrip.getIDS();
        if (CollectionUtils.isNotEmpty(ids)){
            for (IDType idType : ids){
                fishingTripEntity.addFishingTripIdentifiers(FishingTripIdentifierMapper.INSTANCE.mapToFishingTripIdentifier(idType));
            }
        }
        List<DelimitedPeriod> specifiedDelimitedPeriods = fishingTrip.getSpecifiedDelimitedPeriods();
        if (CollectionUtils.isNotEmpty(specifiedDelimitedPeriods)){
            for (DelimitedPeriod delimitedPeriod : specifiedDelimitedPeriods){
                fishingTripEntity.addDelimitedPeriods(DelimitedPeriodMapper.INSTANCE.mapToDelimitedPeriodEntity(delimitedPeriod));
            }
        }
        return fishingTripEntity;
    }

    public static DelimitedPeriodDTO calculateFishingTime(Set<DelimitedPeriodEntity> periodEntities) {
        BigDecimal fishingTime = BigDecimal.ZERO;
        Date startDate = null;
        Date endDate = null;
        String unitCode = null;
        for (DelimitedPeriodEntity period : periodEntities) {
            Double calcDur = period.getCalculatedDuration();
            Date start = period.getStartDate();
            Date end = period.getEndDate();
            if (startDate == null || start.before(startDate)) {
                startDate = start;
            }
            if (endDate == null || end.after(endDate)) {
                endDate = end;
            }
            if (calcDur != null) {
                fishingTime = fishingTime.add(new BigDecimal(calcDur));
            }
            unitCode = unitCode == null ? periodEntities.size() > 1 ? UnitCodeEnum.MIN.getUnit() : period.getDurationMeasure().getUnitCode() : unitCode;
        }

        DelimitedPeriodDTO build = DelimitedPeriodDTO.builder()
                .duration(fishingTime.doubleValue()).endDate(endDate).startDate(startDate).unitCode(unitCode).build();

        if (Math.abs(BigDecimal.ZERO.doubleValue() - build.getDuration()) < 0.00000001) {
            build.setDuration(null);
        }
        return build;
    }

    public static Set<SizeDistributionClassCodeEntity> mapToSizeDistributionClassCodes(List<CodeType> codeTypes, SizeDistributionEntity sizeDistributionEntity) {
        if (codeTypes == null || codeTypes.isEmpty()) {
            Collections.emptySet();
        }
        Set<SizeDistributionClassCodeEntity> classCodes = new HashSet<>();
        for (CodeType codeType : codeTypes) {
            SizeDistributionClassCodeEntity entity = SizeDistributionMapper.INSTANCE.mapToSizeDistributionClassCodeEntity(codeType);
            entity.setSizeDistribution(sizeDistributionEntity);
            classCodes.add(entity);
        }
        return classCodes;
    }

    public static RegistrationLocationEntity mapToRegistrationLocationEntity(RegistrationLocation registrationLocation, RegistrationEventEntity registrationEventEntity) {
        if (registrationLocation == null) {
            return null;
        }
        RegistrationLocationEntity registrationLocationEntity = RegistrationLocationMapper.INSTANCE.mapToRegistrationLocationEntity(registrationLocation);
        registrationLocationEntity.setRegistrationEvent(registrationEventEntity);
        return registrationLocationEntity;
    }

    public static Set<FishingGearRoleEntity> mapToFishingGears(List<CodeType> codeTypes, FishingGearEntity fishingGearEntity) {
        if (CollectionUtils.isEmpty(codeTypes)) {
            Collections.emptySet();
        }
        Set<FishingGearRoleEntity> fishingGearRoles = newSet();
        for (CodeType codeType : codeTypes) {
            FishingGearRoleEntity gearRole = FishingGearMapper.INSTANCE.mapToFishingGearRoleEntity(codeType);
            gearRole.setFishingGear(fishingGearEntity);
            fishingGearRoles.add(gearRole);
        }
        return fishingGearRoles;
    }

    public static Set<GearCharacteristicEntity> getGearCharacteristicEntities(List<GearCharacteristic> gearCharacteristics, FishingGearEntity fishingGearEntity) {
        if (CollectionUtils.isEmpty(gearCharacteristics)) {
            return Collections.emptySet();
        }
        Set<GearCharacteristicEntity> gearCharacteristicEntities = newSet();
        for (GearCharacteristic gearCharacteristic : gearCharacteristics) {
            GearCharacteristicEntity gearCharacteristicEntity = GearCharacteristicsMapper.INSTANCE.mapToGearCharacteristicEntity(gearCharacteristic, fishingGearEntity, new GearCharacteristicEntity());
            gearCharacteristicEntities.add(gearCharacteristicEntity);
        }
        return gearCharacteristicEntities;
    }

    public static List<AssetListCriteriaPair> mapToAssetListCriteriaPairList(Set<AssetIdentifierDto> identifierDtoSet) {
        List<AssetListCriteriaPair> criteriaList = new ArrayList<>();
        for (AssetIdentifierDto identifierDto : identifierDtoSet) {
            AssetListCriteriaPair criteriaPair = new AssetListCriteriaPair();
            VesselIdentifierSchemeIdEnum identifierSchemeId = identifierDto.getIdentifierSchemeId();
            ConfigSearchField key = VesselIdentifierMapper.INSTANCE.map(identifierSchemeId);
            String identifierId = identifierDto.getFaIdentifierId();
            if (key != null && identifierId != null) {
                criteriaPair.setKey(key);
                criteriaPair.setValue(identifierId);
                criteriaList.add(criteriaPair);
            }
        }
        return criteriaList;
    }

    public static List<AssetListCriteriaPair> mapMdrCodeListToAssetListCriteriaPairList(Set<AssetIdentifierDto> identifierDtoSet, List<String> vesselIdentifierSchemeList) {
        List<AssetListCriteriaPair> criteriaList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(identifierDtoSet)) {
            for (AssetIdentifierDto identifierDto : identifierDtoSet) {
                VesselIdentifierSchemeIdEnum identifierSchemeId = identifierDto.getIdentifierSchemeId();
                ConfigSearchField keyFromDto = VesselIdentifierMapper.INSTANCE.map(identifierSchemeId);
                if (null != identifierSchemeId && null != keyFromDto && vesselIdentifierSchemeList.contains(keyFromDto.name())) {
                    String identifierId = identifierDto.getFaIdentifierId();
                    AssetListCriteriaPair criteriaPair = new AssetListCriteriaPair();
                    criteriaPair.setKey(ConfigSearchField.fromValue(identifierSchemeId.name()));
                    criteriaPair.setValue(identifierId);
                    criteriaList.add(criteriaPair);
                }
            }
        }
        return criteriaList;
    }

    public static Double getCalculatedMeasure(MeasureType measureType) {
        if (measureType == null) {
            return null;
        }
        UnitCodeEnum unitCodeEnum = UnitCodeEnum.getUnitCode(measureType.getUnitCode());
        if (unitCodeEnum != null) {
            BigDecimal measuredValue = measureType.getValue();
            BigDecimal result = measuredValue.multiply(new BigDecimal(unitCodeEnum.getConversionFactor()));
            return result.doubleValue();
        } else {
            return null;
        }
    }

    public static String getLanguageIdFromList(List<TextType> textTypes) {
        if (CollectionUtils.isEmpty(textTypes)) {
            return null;
        }
        return textTypes.get(0).getLanguageID();
    }

    public static String getTextFromList(List<TextType> textTypes) {
        if (CollectionUtils.isEmpty(textTypes)) {
            return null;
        }
        return textTypes.get(0).getValue();
    }


    public static boolean getCorrection(FishingActivityEntity entity) {
        if (entity == null || entity.getFaReportDocument() == null || entity.getFaReportDocument().getFluxReportDocument() == null) {
            return false;
        }

        FluxReportDocumentEntity fluxReportDocument = entity.getFaReportDocument().getFluxReportDocument();
        return fluxReportDocument.getReferenceId() != null && fluxReportDocument.getReferenceId().length() != 0;
    }

    protected static XMLGregorianCalendar convertToXMLGregorianCalendar(Date dateTime, boolean includeTimeZone) {
        XMLGregorianCalendar calendar = null;
        try {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(dateTime.getTime());
            calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
            if (!includeTimeZone) {
                calendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED); //If we do not want timeZone to be included, set this
            }
        } catch (DatatypeConfigurationException e) {
            log.error(e.getMessage(), e);
        }
        return calendar;
    }

    public static FluxReportDocumentEntity getFluxReportDocument(FishingActivityEntity activityEntity) {
        FaReportDocumentEntity faReportDocument = activityEntity.getFaReportDocument();
        return faReportDocument != null ? faReportDocument.getFluxReportDocument() : null;
    }

    public static String getIdType(IDType idType) {
        return (idType == null) ? null : idType.getValue();
    }

    public static String getIdTypeSchemaId(IDType idType) {
        return (idType == null) ? null : idType.getSchemeID();
    }

    public static String getCodeType(CodeType codeType) {
        return (codeType == null) ? null : codeType.getValue();
    }

    public static String getCodeTypeListId(CodeType codeType) {
        return (codeType == null) ? null : codeType.getListID();
    }

    protected String getCountry(VesselCountry country) {
        if (country == null) {
            return null;
        }
        return getIdType(country.getID());
    }

    protected Map<String, String> getReportIdMap(Collection<FluxReportIdentifierEntity> identifiers) {
        Map<String, String> recordMap = new HashMap<>();
        for (FluxReportIdentifierEntity identifier : identifiers) {
            recordMap.put(identifier.getFluxReportIdentifierId(), identifier.getFluxReportIdentifierSchemeId());
        }
        return recordMap;
    }

    protected FishingActivityEntity extractSubFishingActivity(Set<FishingActivityEntity> fishingActivityList, FishingActivityTypeEnum faTypeToExtract) {
        if (CollectionUtils.isEmpty(fishingActivityList)) {
            return null;
        }
        for (FishingActivityEntity fishingActivityEntity : fishingActivityList) {
            if (faTypeToExtract.toString().equalsIgnoreCase(fishingActivityEntity.getTypeCode())) {
                return fishingActivityEntity;
            }
        }
        return null;
    }

    protected FluxLocationEntity extractFLUXPosition(Set<FluxLocationEntity> fluxLocationEntityList) {
        if (CollectionUtils.isEmpty(fluxLocationEntityList)) {
            return null;
        }
        for (FluxLocationEntity locationEntity : fluxLocationEntityList) {
            if (FluxLocationEnum.POSITION.toString().equalsIgnoreCase(locationEntity.getTypeCode())) {
                return locationEntity;
            }
        }
        return null;
    }

    protected String extractGeometryWkt(Double longitude, Double latitude) {
        Geometry geom = GeometryUtils.createPoint(longitude, latitude);
        return GeometryMapper.INSTANCE.geometryToWkt(geom).getValue();
    }

    @NotNull
    protected PositionDto extractPositionDtoFromFishingActivity(FishingActivityEntity faEntity) {
        if (faEntity == null) {
            return null;
        }
        PositionDto positionDto = new PositionDto();
        positionDto.setOccurence(faEntity.getOccurence());
        if (CollectionUtils.isNotEmpty(faEntity.getFluxLocations())) {
            FluxLocationEntity locationEntity = extractFLUXPosition(faEntity.getFluxLocations());
            if (locationEntity != null && locationEntity.getGeom() != null) {
                positionDto.setGeometry(GeometryMapper.INSTANCE.geometryToWkt(locationEntity.getGeom()).getValue());
            }
        }
        return positionDto;
    }

    protected Date convertToDate(DateTimeType dateTime) {
        Date value = null;
        if (dateTime != null){
            value = XMLDateUtils.xmlGregorianCalendarToDate(dateTime.getDateTime());
        }
        return value;
    }

    /**
     * return fishing trip start and end time
     * fishingActivityType =FishingActivityTypeEnum.DEPARTURE = method will return fishing trip start time
     * fishingActivityType =FishingActivityTypeEnum.ARRIVAL = method will return fishing trip end time
     *
     * @param fishingActivities
     * @param fishingActivityType
     * @return
     */
    public Date getFishingTripDateTimeFromFishingActivities(List<FishingActivityEntity> fishingActivities, String fishingActivityType) {
        if (CollectionUtils.isEmpty(fishingActivities) || fishingActivityType == null) {
            return null;
        }
        for (FishingActivityEntity fishingActivityEntity : fishingActivities) {
            if (fishingActivityEntity != null && fishingActivityType.equals(fishingActivityEntity.getTypeCode()) && fishingActivityEntity.getCalculatedStartTime() != null) {
                return fishingActivityEntity.getCalculatedStartTime();
            }
        }
        return null;
    }

}