/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyRoleEntity;
import eu.europa.ec.fisheries.ers.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearRoleEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxPartyEntity;
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
import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.commons.geometry.utils.GeometryUtils;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselCountry;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.QuantityType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.TextType;

import javax.validation.constraints.NotNull;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

import static com.google.common.collect.Sets.newHashSet;
import static org.mockito.internal.util.collections.Sets.newSet;

/**
 * TODO create test
 */
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

    public static DelimitedPeriodDTO calculateFishingTime(Set<DelimitedPeriodEntity> periodEntities) {

        BigDecimal fishingTime = BigDecimal.ZERO;
        Date startDate = null;
        Date endDate = null;

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
        }

        DelimitedPeriodDTO build = DelimitedPeriodDTO.builder()
                .duration(fishingTime.doubleValue()).endDate(endDate).startDate(startDate).build();

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

    public static FluxPartyEntity getFluxPartyEntity(FLUXParty fluxParty, FluxReportDocumentEntity fluxReportDocumentEntity) {
        if (fluxParty == null) {
            return null;
        }
        FluxPartyEntity fluxPartyEntity = FluxPartyMapper.INSTANCE.mapToFluxPartyEntity(fluxParty);
        fluxPartyEntity.setFluxReportDocument(fluxReportDocumentEntity);
        return fluxPartyEntity;
    }

    public static Set<FluxReportIdentifierEntity> mapToFluxReportIdentifierEntities(List<IDType> idTypes, FluxReportDocumentEntity fluxReportDocumentEntity) {
        if (idTypes == null || idTypes.isEmpty()) {
            Collections.emptySet();
        }
        Set<FluxReportIdentifierEntity> identifiers = newSet();
        for (IDType idType : idTypes) {
            FluxReportIdentifierEntity entity = FluxReportDocumentMapper.INSTANCE.mapToFluxReportIdentifierEntity(idType);
            entity.setFluxReportDocument(fluxReportDocumentEntity);
            identifiers.add(entity);
        }
        return identifiers;
    }

    public static FishingTripEntity getSpecifiedFishingTrip(FishingActivityEntity activityEntity) {
        FishingTripEntity fishingTripEntity = null;
        Set<FishingTripEntity> fishingTrips = activityEntity.getFishingTrips();
        if (!CollectionUtils.isEmpty(fishingTrips)) {
            fishingTripEntity = activityEntity.getFishingTrips().iterator().next();
        }
        return fishingTripEntity;
    }


    public static Set<FluxLocationEntity> getRelatedFluxLocations(FishingTripEntity tripEntity) {
        Set<FluxLocationEntity> fluxLocations = newSet();
        FishingActivityEntity fishingActivity = tripEntity.getFishingActivity();
        if (fishingActivity != null) {
            fluxLocations = fishingActivity.getFluxLocations();
        }
        return fluxLocations;
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

    public static List<String> getRoles(Set<ContactPartyRoleEntity> contactPartyRoles) {
        List<String> roles = new ArrayList<>();
        for (ContactPartyRoleEntity roleEntity : contactPartyRoles) {
            roles.add(roleEntity.getRoleCode());
        }
        return roles;
    }

    public static FluxReportDocumentEntity getFluxReportDocument(FishingActivityEntity activityEntity) {
        FaReportDocumentEntity faReportDocument = activityEntity.getFaReportDocument();
        return faReportDocument != null ? faReportDocument.getFluxReportDocument() : null;
    }

    public static Set<FluxLocationEntity> getRelatedFluxLocations(FishingActivityEntity activityEntity) {
        FishingTripEntity specifiedFishingTrip = getSpecifiedFishingTrip(activityEntity);
        Set<FluxLocationEntity> relatedFluxLocations = new HashSet<>();
        if (specifiedFishingTrip != null) {
            relatedFluxLocations = getRelatedFluxLocations(specifiedFishingTrip);
        }
        return relatedFluxLocations;
    }

    protected String getIdType(IDType idType) {
        return (idType == null) ? null : idType.getValue();
    }

    protected String getIdTypeSchemaId(IDType idType) {
        return (idType == null) ? null : idType.getSchemeID();
    }

    protected String getCodeType(CodeType codeType) {
        return (codeType == null) ? null : codeType.getValue();
    }

    protected String getCodeTypeListId(CodeType codeType) {
        return (codeType == null) ? null : codeType.getListID();
    }

    protected Double getCalculatedQuantity(QuantityType quantityType) {
        if (quantityType == null) {
            return null;
        }
        UnitCodeEnum unitCodeEnum = UnitCodeEnum.getUnitCode(quantityType.getUnitCode());
        if (unitCodeEnum != null) {
            BigDecimal quantity = quantityType.getValue();
            BigDecimal result = quantity.multiply(new BigDecimal(unitCodeEnum.getConversionFactor()));
            return result.doubleValue();
        } else {
            return null;
        }
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
        try {
            if (dateTime != null) {
                if (dateTime.getDateTime() != null) {
                    value = dateTime.getDateTime().toGregorianCalendar().getTime();
                } else if (dateTime.getDateTimeString() != null) {
                    DateFormat df = new SimpleDateFormat(dateTime.getDateTimeString().getFormat());
                    value = df.parse(dateTime.getDateTimeString().getValue());
                }
            }
        } catch (java.text.ParseException e) {
            log.error(e.getMessage(), e);
        }
        return value;
    }

    /**
     * This method will return fishing trip start and end time
     * fishingActivityType =FishingActivityTypeEnum.DEPARTURE = method will return fishing trip start time
     * fishingActivityType =FishingActivityTypeEnum.ARRIVAL = method will return fishing trip end time
     *
     * @param fishingTripList
     * @param fishingActivityType
     * @return
     */
    public Date getFishingTripDateTime(List<FishingTripEntity> fishingTripList, String fishingActivityType) {
        if (CollectionUtils.isEmpty(fishingTripList) || fishingActivityType == null) {
            return null;
        }

        for (FishingTripEntity fishingTripEntity : fishingTripList) {
            FishingActivityEntity fishingActivity = fishingTripEntity.getFishingActivity();
            if (fishingActivity != null && fishingActivityType.equals(fishingActivity.getTypeCode()) && fishingActivity.getCalculatedStartTime() != null) {
                return fishingActivity.getCalculatedStartTime();
            }
        }
        return null;
    }


    /**
     * This method will return fishing trip start and end time
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