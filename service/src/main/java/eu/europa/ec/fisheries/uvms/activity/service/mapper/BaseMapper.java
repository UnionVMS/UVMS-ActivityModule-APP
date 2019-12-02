/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import com.google.common.collect.Sets;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearRoleEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.GearCharacteristicEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.RegistrationEventEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.RegistrationLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FluxLocationEnum;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.UnitCodeEnum;
import eu.europa.ec.fisheries.uvms.activity.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.GearDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.PositionDto;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.view.base.ViewConstants;
import eu.europa.ec.fisheries.uvms.activity.service.util.Utils;
import eu.europa.ec.fisheries.uvms.activity.service.dto.DelimitedPeriodDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.commons.geometry.utils.GeometryUtils;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Geometry;
import org.mapstruct.Named;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingTrip;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselCountry;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.TextType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class BaseMapper {

    protected static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtils.DATE_TIME_UI_FORMAT, Locale.ROOT).withZone(ZoneId.of("UTC"));

    public static Set<FluxLocationDto> mapFromFluxLocation(Set<FluxLocationEntity> fLocEntities) {
        Set<FluxLocationDto> locationDtos = FluxLocationMapper.INSTANCE.mapEntityToFluxLocationDto(fLocEntities);
        return locationDtos != null ? locationDtos : Sets.newHashSet();
    }

    public static Set<FluxLocationDto> mapFromFluxLocation(Set<FluxLocationEntity> fLocEntities, final FluxLocationCatchTypeEnum typeCode) {
        Set<FluxLocationEntity> filtered = fLocEntities.stream()
                .filter(p -> typeCode.name().equals(p.getFluxLocationType()))
                .collect(Collectors.toSet());
        return mapFromFluxLocation(filtered);
    }

    public static Set<FluxLocationDto> mapFromFluxLocation(Set<FluxLocationEntity> fLocEntities, final FluxLocationEnum typeCode) {
        Set<FluxLocationEntity> filtered = fLocEntities.stream()
                .filter(p -> typeCode.name().equals(p.getTypeCode()))
                .collect(Collectors.toSet());
        return mapFromFluxLocation(filtered);
    }

    public static FishingTripEntity mapToFishingTripEntity(FishingTrip fishingTrip) {
        FishingTripEntity fishingTripEntity = FishingTripMapper.INSTANCE.mapToFishingTripEntity(fishingTrip);
        List<IDType> ids = fishingTrip.getIDS();
        // TODO: WTAF, should be one and only one?
        IDType idType = ids.get(0);
        fishingTripEntity.setFishingTripIdentifier(FishingTripIdentifierMapper.INSTANCE.mapToFishingTripIdentifier(idType));

        List<DelimitedPeriod> specifiedDelimitedPeriods = fishingTrip.getSpecifiedDelimitedPeriods();
        for (DelimitedPeriod delimitedPeriod : Utils.safeIterable(specifiedDelimitedPeriods)) {
            fishingTripEntity.addDelimitedPeriods(DelimitedPeriodMapper.INSTANCE.mapToDelimitedPeriodEntity(delimitedPeriod));
        }

        return fishingTripEntity;
    }

    protected static DelimitedPeriodDTO calculateFishingTime(Set<DelimitedPeriodEntity> periodEntities) {
        BigDecimal fishingTime = BigDecimal.ZERO;
        Instant startInstant = null;
        Instant endInstant = null;
        String unitCode = null;
        for (DelimitedPeriodEntity period : periodEntities) {
            Double calcDur = period.getCalculatedDuration();
            Instant start = period.getStartDate();
            Instant end = period.getEndDate();

            if (startInstant == null || start.isBefore(startInstant)) {
                startInstant = start;
            }
            if (endInstant == null || end.isAfter(endInstant)) {
                endInstant = end;
            }
            if (calcDur != null) {
                fishingTime = fishingTime.add(new BigDecimal(calcDur));
            }
            unitCode = unitCode == null ? periodEntities.size() > 1 ? UnitCodeEnum.MIN.getUnit() : period.getDurationMeasure().getUnitCode() : unitCode;
        }

        Date startDate = startInstant != null ? Date.from(startInstant) : null;
        Date endDate = endInstant != null ? Date.from(endInstant) : null;

        DelimitedPeriodDTO build = DelimitedPeriodDTO.builder()
                .duration(fishingTime.doubleValue()).endDate(endDate).startDate(startDate).unitCode(unitCode).build();

        if (Math.abs(BigDecimal.ZERO.doubleValue() - build.getDuration()) < 0.00000001) {
            build.setDuration(null);
        }
        return build;
    }

    public static RegistrationLocationEntity mapToRegistrationLocationEntity(RegistrationLocation registrationLocation, RegistrationEventEntity registrationEventEntity) {
        if (registrationLocation == null) {
            return null;
        }
        RegistrationLocationEntity registrationLocationEntity = RegistrationLocationMapper.INSTANCE.mapToRegistrationLocationEntity(registrationLocation);
        registrationLocationEntity.setRegistrationEvent(registrationEventEntity);
        return registrationLocationEntity;
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
        for (AssetIdentifierDto identifierDto : Utils.safeIterable(identifierDtoSet)) {
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
        return criteriaList;
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

    protected PositionDto extractPositionDtoFromFishingActivity(FishingActivityEntity faEntity) {
        if (faEntity == null) {
            return null;
        }
        PositionDto positionDto = new PositionDto();
        positionDto.setOccurence(faEntity.getOccurrenceAsDate().orElse(null));
        if (CollectionUtils.isNotEmpty(faEntity.getFluxLocations())) {
            FluxLocationEntity locationEntity = extractFLUXPosition(faEntity.getFluxLocations());
            if (locationEntity != null && locationEntity.getGeom() != null) {
                positionDto.setGeometry(GeometryMapper.INSTANCE.geometryToWkt(locationEntity.getGeom()).getValue());
            }
        }
        return positionDto;
    }

    @Named("instantToDate")
    protected Date instantToDate(Instant value) {
        if (value == null) {
            return null;
        }

        return Date.from(value);
    }

    @Named("instantToDateUtilsStringFormat")
    protected String instantToDateUtilsStringFormat(Instant value) {
        if (value == null) {
            return null;
        }

        return dateTimeFormatter.format(value);
    }

    protected void fillRoleAndCharacteristics(GearDto gearDto, FishingGearEntity gearEntity) {
        Set<FishingGearRoleEntity> fishingGearRole = gearEntity.getFishingGearRole();
        if (CollectionUtils.isNotEmpty(fishingGearRole)) {
            FishingGearRoleEntity role = fishingGearRole.iterator().next();
            gearDto.setRole(role.getRoleCode());
        }
        Set<GearCharacteristicEntity> gearCharacteristics = gearEntity.getGearCharacteristics();
        for (GearCharacteristicEntity charac : Utils.safeIterable(gearCharacteristics)) {
            fillCharacteristicField(charac, gearDto);
        }
    }

    private void fillCharacteristicField(GearCharacteristicEntity charac, GearDto gearDto) {
        String quantityOnly = charac.getValueMeasure() != null ? charac.getValueMeasure().toString() : StringUtils.EMPTY;
        String quantityWithUnit = new StringBuilder(quantityOnly).append(charac.getValueMeasureUnitCode()).toString();
        switch (charac.getTypeCode()) {
            case ViewConstants.GEAR_CHARAC_TYPE_CODE_ME:
                gearDto.setMeshSize(quantityWithUnit);
                break;
            case ViewConstants.GEAR_CHARAC_TYPE_CODE_GM:
                gearDto.setLengthWidth(quantityWithUnit);
                break;
            case ViewConstants.GEAR_CHARAC_TYPE_CODE_GN:
                gearDto.setNumberOfGears(Integer.parseInt(quantityOnly));
                break;
            case ViewConstants.GEAR_CHARAC_TYPE_CODE_HE:
                gearDto.setHeight(quantityWithUnit);
                break;
            case ViewConstants.GEAR_CHARAC_TYPE_CODE_NI:
                gearDto.setNrOfLines(quantityWithUnit);
                break;
            case ViewConstants.GEAR_CHARAC_TYPE_CODE_NN:
                gearDto.setNrOfNets(quantityWithUnit);
                break;
            case ViewConstants.GEAR_CHARAC_TYPE_CODE_NL:
                gearDto.setNominalLengthOfNet(quantityWithUnit);
                break;
            case ViewConstants.GEAR_CHARAC_TYPE_CODE_QG:
                if (!Objects.equals(charac.getValueQuantityCode(), ViewConstants.GEAR_CHARAC_Q_CODE_C62)) {
                    gearDto.setQuantity(quantityWithUnit);
                }
                break;
            case ViewConstants.GEAR_CHARAC_TYPE_CODE_GD:
                String description = charac.getDescription();
                if (StringUtils.isNoneEmpty(description)){
                    gearDto.setDescription(charac.getDescription());
                }
                else {
                    gearDto.setDescription(charac.getValueText());
                }
                break;
            default:
                break;
        }
    }

    /**
     * Converts an Instant to an XMLGregorianCalendar that uses the UTC time zone.
     * The precision is seconds, not milliseconds. This is done in order to preserve backwards compatibility of the format for outgoing FLUX messages.
     * Otherwise, the timestamp in the FLUX XML will contain milliseconds, which it hasn't previously.
     * XMLDateUtils.dateToXmlGregorian() (which this method is intended to replace) also doesn't have millisecond precision.
     *
     * @param instant Instant to convert
     * @return XMLGregorianCalendar set to the same epoch second with UTC time zone
     */
    @Named("instantToXMLGregorianCalendarUTC")
    protected XMLGregorianCalendar instantToXMLGregorianCalendarUTC(Instant instant) {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.ROOT);
        calendar.setTimeInMillis(instant.toEpochMilli());
        try {
            XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
            // no millisecond precision, see Javadoc
            xmlGregorianCalendar.setFractionalSecond(null);
            return xmlGregorianCalendar;
        } catch (DatatypeConfigurationException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Named("instantToDateTimeTypeUTC")
    protected DateTimeType instantToDateTimeTypeUTC(Instant value) {
        if (value == null) {
            return null;
        }

        XMLGregorianCalendar xmlGregorianCalendar = instantToXMLGregorianCalendarUTC(value);
        DateTimeType result = new DateTimeType();
        result.setDateTime(xmlGregorianCalendar);
        return result;
    }

    @Named("dateTimeTypeToInstant")
    protected Instant dateTimeTypeToInstant(DateTimeType value) {
        if (value == null) {
            return null;
        }

        XMLGregorianCalendar dateTime = value.getDateTime();
        if (dateTime == null) {
            return null;
        }

        return dateTime.toGregorianCalendar().toInstant();
    }

    @Named("xmlGregorianCalendarToInstant")
    protected Instant xmlGregorianCalendarToInstant(XMLGregorianCalendar value) {
        if (value ==  null) {
            return null;
        }

        return value.toGregorianCalendar().toInstant();
    }
}
