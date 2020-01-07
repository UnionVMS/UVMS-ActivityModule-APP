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
import com.google.common.collect.Sets;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearRoleEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxPartyEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxPartyIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.GearCharacteristicEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.RegistrationEventEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.RegistrationLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FluxLocationEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.GearDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.PositionDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.RelatedReportDto;
import eu.europa.ec.fisheries.uvms.activity.service.util.Utils;
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
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
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
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class BaseMapper {

    protected static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtils.DATE_TIME_UI_FORMAT, Locale.ROOT).withZone(ZoneId.of("UTC"));

    public static Set<FluxLocationDto> mapFromFluxLocation(Set<FluxLocationEntity> fLocEntities) {
        Set<FluxLocationDto> locationDtos = FluxLocationMapper.INSTANCE.mapEntityToFluxLocationDto(fLocEntities);
        return locationDtos != null ? locationDtos : Sets.newHashSet();
    }

    public static Set<FluxLocationDto> mapFromFluxLocation(Set<FluxLocationEntity> fLocEntities, final FluxLocationEnum typeCode) {
        Set<FluxLocationEntity> filtered = fLocEntities.stream()
                .filter(p -> typeCode.name().equals(p.getTypeCode()))
                .collect(Collectors.toSet());
        return mapFromFluxLocation(filtered);
    }

    public static FishingTripEntity mapToFishingTripEntity(FishingTrip fishingTrip) {
        return FishingTripEntity.create(fishingTrip);
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
        if (entity == null || entity.getFaReportDocument() == null || StringUtils.isEmpty(entity.getFaReportDocument().getFluxReportDocument_Id())) {
            return false;
        }

        return entity.getFaReportDocument().getFluxReportDocument_ReferenceId() != null &&
                entity.getFaReportDocument().getFluxReportDocument_ReferenceId().length() != 0;
    }

    private static String getIdType(IDType idType) {
        return (idType == null) ? null : idType.getValue();
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

    protected Map<String, String> getReportIdMap(String fluxReportIdentifierId, String fluxReportIdentifierSchemeId) {
        Map<String, String> recordMap = new HashMap<>();
        recordMap.put(fluxReportIdentifierId, fluxReportIdentifierSchemeId);
        return recordMap;
    }

/*
    protected Map<String, String> getReportIdMapOLD(Collection<FluxReportIdentifierEntity> identifiers) {
        Map<String, String> recordMap = new HashMap<>();
        for (FluxReportIdentifierEntity identifier : identifiers) {
            recordMap.put(identifier.getFluxReportIdentifierId(), identifier.getFluxReportIdentifierSchemeId());
        }
        return recordMap;
    }
*/

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
        String quantityWithUnit = quantityOnly + charac.getValueMeasureUnitCode();
        switch (charac.getTypeCode()) {
            case GearCharacteristicConstants.GEAR_CHARAC_TYPE_CODE_ME:
                gearDto.setMeshSize(quantityWithUnit);
                break;
            case GearCharacteristicConstants.GEAR_CHARAC_TYPE_CODE_GM:
                gearDto.setLengthWidth(quantityWithUnit);
                break;
            case GearCharacteristicConstants.GEAR_CHARAC_TYPE_CODE_GN:
                gearDto.setNumberOfGears(Integer.parseInt(quantityOnly));
                break;
            case GearCharacteristicConstants.GEAR_CHARAC_TYPE_CODE_HE:
                gearDto.setHeight(quantityWithUnit);
                break;
            case GearCharacteristicConstants.GEAR_CHARAC_TYPE_CODE_NI:
                gearDto.setNrOfLines(quantityWithUnit);
                break;
            case GearCharacteristicConstants.GEAR_CHARAC_TYPE_CODE_NN:
                gearDto.setNrOfNets(quantityWithUnit);
                break;
            case GearCharacteristicConstants.GEAR_CHARAC_TYPE_CODE_NL:
                gearDto.setNominalLengthOfNet(quantityWithUnit);
                break;
            case GearCharacteristicConstants.GEAR_CHARAC_TYPE_CODE_QG:
                if (!Objects.equals(charac.getValueQuantityCode(), GearCharacteristicConstants.GEAR_CHARAC_Q_CODE_C62)) {
                    gearDto.setQuantity(quantityWithUnit);
                }
                break;
            case GearCharacteristicConstants.GEAR_CHARAC_TYPE_CODE_GD:
                String description = charac.getDescription();
                if (StringUtils.isNoneEmpty(description)) {
                    gearDto.setDescription(charac.getDescription());
                } else {
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
        if (value == null) {
            return null;
        }
        return value.toGregorianCalendar().toInstant();
    }

    @Named("singleIDTypeValue")
    protected String singleIDTypeValue(List<IDType> idTypes) {
        return getObjectPropertyFromListOfObjectsWithMaxOneItem(idTypes, IDType::getValue);
    }

    @Named("singleIDTypeSchemeID")
    protected String singleIDTypeSchemeID(List<IDType> idTypes) {
        return getObjectPropertyFromListOfObjectsWithMaxOneItem(idTypes, IDType::getSchemeID);
    }

    @Named("OnlyInvokedByCustomMappers")
    protected final <O, P> P getObjectPropertyFromListOfObjectsWithMaxOneItem(List<O> listOfO, Function<O, P> getPfromOFunction) {
        if (CollectionUtils.isEmpty(listOfO)) {
            return null;
        }
        if (listOfO.size() > 1) {
            String values = listOfO.stream().map(getPfromOFunction).map(Object::toString).collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Failed to map list of type " + listOfO.getClass().getTypeName() + " since there are more than one value. Values: " + values);
        }
        return getPfromOFunction.apply(listOfO.get(0));
    }

    protected Set<FaReportIdentifierEntity> mapRelatedReportIDs(FAReportDocument faReportDocument) {
        Set<FaReportIdentifierEntity> faReportIdentifiers = new HashSet<>();

        List<IDType> relatedReportIDs = faReportDocument.getRelatedReportIDs();
        for (IDType relatedReportID : relatedReportIDs) {
            FaReportIdentifierEntity faReportIdentifierEntity = new FaReportIdentifierEntity();
            faReportIdentifierEntity.setFaReportIdentifierId(relatedReportID.getValue());
            faReportIdentifierEntity.setFaReportIdentifierSchemeId(relatedReportID.getSchemeID());

            faReportIdentifiers.add(faReportIdentifierEntity);
        }

        return faReportIdentifiers;
    }

    protected List<RelatedReportDto> mapToRelatedReportDtoList(Set<FaReportIdentifierEntity> faReportIdentifierEntities) {
        List<RelatedReportDto> relatedReportDtos = new ArrayList<>();
        for (FaReportIdentifierEntity faReportIdentifierEntity : faReportIdentifierEntities) {
            RelatedReportDto relatedReportDto = new RelatedReportDto();
            relatedReportDto.setId(faReportIdentifierEntity.getFaReportIdentifierId());
            relatedReportDto.setSchemeId(faReportIdentifierEntity.getFaReportIdentifierSchemeId());
            relatedReportDtos.add(relatedReportDto);
        }

        return relatedReportDtos;
    }

    protected FLUXReportDocument mapToFluxReportDocument(FaReportDocumentEntity faReportDocumentEntity) {
        FLUXReportDocument fluxReportDocument = new FLUXReportDocument();

        Instant fluxReportDocument_creationDatetime = faReportDocumentEntity.getFluxReportDocument_CreationDatetime();
        DateTimeType creationDateTime = new DateTimeType();
        XMLGregorianCalendar xmlGregorianCalendar = instantToXMLGregorianCalendarUTC(fluxReportDocument_creationDatetime);
        creationDateTime.setDateTime(xmlGregorianCalendar);

        String fluxReportDocument_id = faReportDocumentEntity.getFluxReportDocument_Id();
        String fluxReportDocument_idSchemeId = faReportDocumentEntity.getFluxReportDocument_IdSchemeId();

        IDType idType = new IDType();
        idType.setValue(fluxReportDocument_id);
        idType.setSchemeID(fluxReportDocument_idSchemeId);
        ArrayList<IDType> idTypes = Lists.newArrayList(idType);

        FluxPartyEntity fluxReportDocument_fluxParty = faReportDocumentEntity.getFluxReportDocument_FluxParty();
        FLUXParty fluxParty = mapToFluxParty(fluxReportDocument_fluxParty);

        String fluxReportDocument_purpose = faReportDocumentEntity.getFluxReportDocument_Purpose();
        TextType purpose = new TextType();
        purpose.setValue(fluxReportDocument_purpose);

        String fluxReportDocument_purposeCode = faReportDocumentEntity.getFluxReportDocument_PurposeCode();
        String fluxReportDocument_purposeCodeListId = faReportDocumentEntity.getFluxReportDocument_PurposeCodeListId();
        CodeType purposeCode = new CodeType();
        purposeCode.setValue(fluxReportDocument_purposeCode);
        purposeCode.setListID(fluxReportDocument_purposeCodeListId);

        String fluxReportDocument_referenceId = faReportDocumentEntity.getFluxReportDocument_ReferenceId();
        String fluxReportDocument_referenceIdSchemeId = faReportDocumentEntity.getFluxReportDocument_ReferenceIdSchemeId();

        IDType referenceId = new IDType();
        referenceId.setValue(fluxReportDocument_referenceId);
        referenceId.setSchemeID(fluxReportDocument_referenceIdSchemeId);

        fluxReportDocument.setCreationDateTime(creationDateTime);
        fluxReportDocument.setIDS(idTypes);
        fluxReportDocument.setOwnerFLUXParty(fluxParty);
        fluxReportDocument.setPurpose(purpose);
        fluxReportDocument.setPurposeCode(purposeCode);
        fluxReportDocument.setReferencedID(referenceId);

        return fluxReportDocument;
    }

    private FLUXParty mapToFluxParty(FluxPartyEntity fluxPartyEntity) {
        String fluxPartyNameValue = fluxPartyEntity.getFluxPartyName();
        String nameLanguageId = fluxPartyEntity.getNameLanguageId();

        TextType fluxPartyName = new TextType();
        fluxPartyName.setValue(fluxPartyNameValue);
        fluxPartyName.setLanguageID(nameLanguageId);
        ArrayList<TextType> fluxPartyNameList = Lists.newArrayList(fluxPartyName);

        List<IDType> fluxPartyIdList = new ArrayList<>();

        Set<FluxPartyIdentifierEntity> fluxPartyIdentifiers = fluxPartyEntity.getFluxPartyIdentifiers();
        for (FluxPartyIdentifierEntity fluxPartyIdentifierEntity : fluxPartyIdentifiers) {
            String fluxPartyIdentifierId = fluxPartyIdentifierEntity.getFluxPartyIdentifierId();
            String fluxPartyIdentifierSchemeId = fluxPartyIdentifierEntity.getFluxPartyIdentifierSchemeId();

            IDType fluxPartyIdType = new IDType();
            fluxPartyIdType.setValue(fluxPartyIdentifierId);
            fluxPartyIdType.setSchemeID(fluxPartyIdentifierSchemeId);

            fluxPartyIdList.add(fluxPartyIdType);
        }

        FLUXParty ownerFluxParty = new FLUXParty();
        ownerFluxParty.setNames(fluxPartyNameList);
        ownerFluxParty.setIDS(fluxPartyIdList);

        return ownerFluxParty;
    }
}
