/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.ContactPartyRoleEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.RegistrationEventEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.RegistrationLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselStorageCharCodeEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselStorageCharacteristicsEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.service.util.Utils;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactPerson;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FACatch;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingActivity;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationEvent;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselCountry;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselStorageCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.QuantityType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.TextType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.singletonList;

@Slf4j
public class ActivityEntityToModelMapper extends BaseMapper {

    public static final ActivityEntityToModelMapper INSTANCE = new ActivityEntityToModelMapper();
  
    private ActivityEntityToModelMapper(){

    }

    public FLUXFAReportMessage mapToFLUXFAReportMessage(List<FaReportDocumentEntity> faReportMessageEntity){
        FLUXFAReportMessage target = new FLUXFAReportMessage();

        FLUXReportDocument fluxReportDocument = new FLUXReportDocument();
        CodeType codeType = new CodeType();
        codeType.setValue("9");
        codeType.setListID("FLUX_GP_PURPOSE");
        fluxReportDocument.setPurposeCode(codeType);

        IDType idType = new IDType();
        idType.setSchemeID("UUID");
        idType.setValue(UUID.randomUUID().toString());

        fluxReportDocument.setIDS(Collections.singletonList(idType));

        DateTimeType dateTimeType = new DateTimeType();

        try {
            XMLGregorianCalendar currentDate = DateUtils.getCurrentDate();
            dateTimeType.setDateTime(currentDate);
        } catch (DatatypeConfigurationException e) {
            log.warn(e.getMessage(), e);
        }
        fluxReportDocument.setCreationDateTime(dateTimeType);

        TextType textType = new TextType();
        textType.setValue("LOAD_REPORTS");
        fluxReportDocument.setPurpose(textType);

        FLUXParty party = new FLUXParty();
        IDType idType1 = new IDType();
        idType1.setSchemeID("FLUX_GP_PARTY");
        idType1.setValue("TODO_SET_NODE_ALIAS");
        party.setIDS(Collections.singletonList(idType1));
        fluxReportDocument.setOwnerFLUXParty(party);

        target.setFLUXReportDocument(fluxReportDocument);
        if (CollectionUtils.isNotEmpty(faReportMessageEntity)){
            mapFAReportDocuments(target, faReportMessageEntity);
        }
        return target;
    }

    private void mapFAReportDocuments(FLUXFAReportMessage target, List<FaReportDocumentEntity> faReportDocuments) {
        List<FAReportDocument> faReportDocumentList = new ArrayList<>();
        for (FaReportDocumentEntity faReportDocumentEntity : faReportDocuments){
            FAReportDocument faReportDocument = new FAReportDocument();
            mapFAReportDocument(faReportDocument, faReportDocumentEntity);
            faReportDocumentList.add(faReportDocument);
        }
        target.setFAReportDocuments(faReportDocumentList);
    }

    private void mapFAReportDocument(FAReportDocument target, FaReportDocumentEntity source) {
        mapAcceptanceDateTime(target, source.getAcceptedDatetime());
        mapPurposeCode(target, source.getTypeCode(), source.getTypeCodeListId());
        mapFMCMarkerCode(target, source.getFmcMarker(), source.getFmcMarkerListId());
        target.setRelatedFLUXReportDocument(FluxReportDocumentMapper.INSTANCE.mapToFluxReportDocument(source.getFluxReportDocument()));
        mapRelatedReportIDs(target, source.getFaReportIdentifiers());
        mapSpecifiedVesselTransportMeans(target, source.getVesselTransportMeans());
        mapFishingActivities(target, source.getFishingActivities());
    }

    private void mapFishingActivities(FAReportDocument faReportDocument, Set<FishingActivityEntity> fishingActivityEntities) {
        List<FishingActivity> fishingActivityList = new ArrayList<>();

        for (FishingActivityEntity source : fishingActivityEntities) {
            FishingActivity fishingActivity = mapFishingActivity(source);
            fishingActivity.setRelatedFishingActivities(new ArrayList<>());

            for (FishingActivityEntity relatedFishingActivity : source.getAllRelatedFishingActivities()) {
                FishingActivity relatedActivity = mapFishingActivity(relatedFishingActivity);
                fishingActivity.getRelatedFishingActivities().add(relatedActivity);
            }

            fishingActivityList.add(fishingActivity);
        }

        faReportDocument.setSpecifiedFishingActivities(fishingActivityList);
    }

    private FishingActivity mapFishingActivity(FishingActivityEntity source) {
        FishingActivity target = new FishingActivity();
        mapPurposeCode(target, source);
        mapReasonCode(target, source);
        mapOperationsQuantity(target, source);
        mapFisheryTypeCode(target, source);
        mapSpeciesTargetCode(target, source);
        mapFishingDurationMeasure(target, source);
        mapVesselRelatedActivityCode(target, source);
        mapOccurrenceDateTime(target, source.getOccurence());
        mapSourceVesselStorageCharacteristic(target, source.getSourceVesselCharId());
        mapDestinationVesselStorageCharacteristic(target, source.getDestVesselCharId());

        target.setRelatedFLUXLocations(FluxLocationMapper.INSTANCE.mapToFluxLocationList(source.getFluxLocations()));

        Set<FluxCharacteristicEntity> fluxCharacteristics = source.getFluxCharacteristics();
        if (CollectionUtils.isNotEmpty(fluxCharacteristics)) {
            List<FLUXCharacteristic> fluxCharacteristicList = new ArrayList<>();
            for (FluxCharacteristicEntity fluxCharacteristicEntity : fluxCharacteristics) {
                FLUXCharacteristic fLUXCharacteristic = FluxCharacteristicsMapper.INSTANCE.mapToFLUXCharacteristic(fluxCharacteristicEntity);
                FluxLocationEntity fluxLocation = fluxCharacteristicEntity.getFluxLocation();
                FLUXLocation location = FluxLocationMapper.INSTANCE.mapToFluxLocation(fluxLocation);
                if (fluxLocation != null) {
                    fLUXCharacteristic.setSpecifiedFLUXLocations(Collections.singletonList(location));
                }
                fluxCharacteristicList.add(fLUXCharacteristic);
            }
            target.setSpecifiedFLUXCharacteristics(fluxCharacteristicList);
        }

        target.setSpecifiedDelimitedPeriods(DelimitedPeriodMapper.INSTANCE.mapToDelimitedPeriodList(source.getDelimitedPeriods()));

        FishingTripEntity fishingTripEntity = source.getFishingTrip();
        if (fishingTripEntity != null) {
            target.setSpecifiedFishingTrip(fishingTripEntity.convert());
        }

        target.setSpecifiedFishingGears(FishingGearMapper.INSTANCE.mapToFishingGearList(source.getFishingGears()));

        Set<FaCatchEntity> faCatchs = source.getFaCatchs();

        if (CollectionUtils.isNotEmpty(faCatchs)) {
            List<FACatch> faCatchList = new ArrayList<>();
            for (FaCatchEntity faCatchEntity : faCatchs) {
                FACatch faCatch = FaCatchMapper.INSTANCE.mapToFaCatch(faCatchEntity);
                Set<FluxLocationEntity> fluxLocations = faCatchEntity.getFluxLocations();
                List<FLUXLocation> specified = new ArrayList<>();
                List<FLUXLocation> destination = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(fluxLocations)) {
                    for (FluxLocationEntity fluxLocation : Utils.safeIterable(fluxLocations)) {
                        if (FluxLocationCatchTypeEnum.FA_CATCH_SPECIFIED.getType().equals(fluxLocation.getFluxLocationType())) {
                            specified.add(FluxLocationMapper.INSTANCE.mapToFluxLocation(fluxLocation));
                        }
                        if (FluxLocationCatchTypeEnum.FA_CATCH_DESTINATION.getType().equals(fluxLocation.getFluxLocationType())) {
                            destination.add(FluxLocationMapper.INSTANCE.mapToFluxLocation(fluxLocation));
                        }
                    }
                    faCatch.setSpecifiedFLUXLocations(specified);
                    faCatch.setDestinationFLUXLocations(destination);
                }

                FishingTripEntity catchFishingTrip = faCatchEntity.getFishingTrip();
                if (catchFishingTrip != null) {
                    faCatch.getRelatedFishingTrips().add(catchFishingTrip.convert());
                }
                faCatchList.add(faCatch);
            }
            target.setSpecifiedFACatches(faCatchList);
        }

        mapRelatedVesselTransportMeans(target, source.getVesselTransportMeans());

        target.setSpecifiedFLAPDocuments(FlapDocumentMapper.INSTANCE.mapToFlapDocumentList(source.getFlapDocuments()));

        source.getFlagState(); // TODO MAP

        return target;
    }

    private void mapDestinationVesselStorageCharacteristic(FishingActivity target, VesselStorageCharacteristicsEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            VesselStorageCharacteristic vesselStorageCharacteristic = new VesselStorageCharacteristic();
            mapID(vesselStorageCharacteristic, source);
            mapVesselStorageCharCodes(vesselStorageCharacteristic, source.getVesselStorageCharCode());
            target.setDestinationVesselStorageCharacteristic(vesselStorageCharacteristic);
        }
    }

    private void mapSourceVesselStorageCharacteristic(FishingActivity target, VesselStorageCharacteristicsEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            VesselStorageCharacteristic vesselStorageCharacteristic = new VesselStorageCharacteristic();
            mapID(vesselStorageCharacteristic, source);
            mapVesselStorageCharCodes(vesselStorageCharacteristic, source.getVesselStorageCharCode());
            target.setSourceVesselStorageCharacteristic(vesselStorageCharacteristic);
        }
    }

    private void mapVesselStorageCharCodes(VesselStorageCharacteristic target, Set<VesselStorageCharCodeEntity> vesselStorageCharCode) {
        if (CollectionUtils.isNotEmpty(vesselStorageCharCode) && target != null){
            List<CodeType> codeTypeList = new ArrayList<>();
            for (VesselStorageCharCodeEntity source : vesselStorageCharCode) {
                CodeType codeType = new CodeType();
                codeType.setValue(source.getVesselTypeCode());
                codeType.setListID(source.getVesselTypeCodeListId());
                codeTypeList.add(codeType);
            }
            target.setTypeCodes(codeTypeList);
        }
    }

    private void mapID(VesselStorageCharacteristic target, VesselStorageCharacteristicsEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            IDType idType = new IDType();
            idType.setSchemeID(source.getVesselSchemaId());
            idType.setValue(source.getVesselId());
            target.setID(idType);
        }
    }

    private void mapVesselRelatedActivityCode(FishingActivity target, FishingActivityEntity source) {
        if (ObjectUtils.allNotNull(target, source) && (StringUtils.isNotEmpty(source.getVesselActivityCode()) || StringUtils.isNotEmpty(source.getVesselActivityCodeListId()))) {
            CodeType codeType = new CodeType();

            if (StringUtils.isNotEmpty(source.getVesselActivityCode())){
                codeType.setValue(source.getVesselActivityCode());
            }
            if (StringUtils.isNotEmpty(source.getVesselActivityCodeListId())){
                codeType.setListID(source.getVesselActivityCodeListId());
            }
            target.setVesselRelatedActivityCode(codeType);
        }
    }

    private void mapFishingDurationMeasure(FishingActivity target, FishingActivityEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            MeasureType measureType = new MeasureType();
            Double fishingDurationMeasure = source.getFishingDurationMeasure();
            if (fishingDurationMeasure != null){
                measureType.setValue(new BigDecimal(fishingDurationMeasure));
            }
            if (StringUtils.isNotEmpty(source.getFishingDurationMeasureCode())){
                measureType.setUnitCode(source.getFishingDurationMeasureCode());
            }
            if (StringUtils.isNotEmpty(source.getFishingDurationMeasureUnitCodeListVersionID())){
                measureType.setUnitCodeListVersionID(source.getFishingDurationMeasureUnitCodeListVersionID());
            }
            target.setFishingDurationMeasure(measureType);
        }
    }

    private void mapSpeciesTargetCode(FishingActivity target, FishingActivityEntity source) {
        if (ObjectUtils.allNotNull(target, source) && (StringUtils.isNotEmpty(source.getSpeciesTargetCode()) || StringUtils.isNotEmpty(source.getSpeciesTargetCodeListId()))) {
            CodeType codeType = new CodeType();

            if (StringUtils.isNotEmpty(source.getSpeciesTargetCode())){
                codeType.setValue(source.getSpeciesTargetCode());
            }
            if (StringUtils.isNotEmpty(source.getSpeciesTargetCodeListId())){
                codeType.setListID(source.getSpeciesTargetCodeListId());
            }
            target.setSpeciesTargetCode(codeType);
        }
    }

    private void mapFisheryTypeCode(FishingActivity target, FishingActivityEntity source) {
        if (ObjectUtils.allNotNull(target, source) && (StringUtils.isNotEmpty(source.getFisheryTypeCode()) || StringUtils.isNotEmpty(source.getFisheryTypeCodeListId()))) {
            CodeType codeType = new CodeType();
            if (StringUtils.isNotEmpty(source.getFisheryTypeCode())){
                codeType.setValue(source.getFisheryTypeCode());
            }
            if (StringUtils.isNotEmpty(source.getFisheryTypeCodeListId())){
                codeType.setListID(source.getFisheryTypeCodeListId());
            }
            target.setFisheryTypeCode(codeType);
        }
    }

    private void mapOperationsQuantity(FishingActivity target, FishingActivityEntity source) {
        if (ObjectUtils.allNotNull(target, target)){
            QuantityType quantityType = new QuantityType();
            if (source.getOperationsQuantity() != null){
                Double operationQuantity = source.getOperationsQuantity().getValue();
                if (operationQuantity != null){
                    quantityType.setValue(new BigDecimal(operationQuantity));
                }
                String unitCode = source.getOperationsQuantity().getUnitCode();
                if (StringUtils.isNotEmpty(unitCode)){
                    quantityType.setUnitCode(unitCode);
                }
                String unitCodeListID = source.getOperationsQuantity().getUnitCodeListID();
                if (StringUtils.isNotEmpty(unitCodeListID)){
                    quantityType.setUnitCodeListID(unitCodeListID);
                }
                target.setOperationsQuantity(quantityType);
            }
        }
    }

    private void mapReasonCode(FishingActivity target, FishingActivityEntity source) {
        if (ObjectUtils.allNotNull(target, target)){
            CodeType codeType = new CodeType();
            codeType.setValue(source.getReasonCode());
            codeType.setListID(source.getReasonCodeListId());
            target.setReasonCode(codeType);
        }
    }

    private void mapOccurrenceDateTime(FishingActivity target, Instant source) {
        if (ObjectUtils.allNotNull(target, source)) {
            DateTimeType dateTimeType = instantToDateTimeTypeUTC(source);
            target.setOccurrenceDateTime(dateTimeType);
        }
    }

    private void mapPurposeCode(FishingActivity target, FishingActivityEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            CodeType codeType = new CodeType();
            codeType.setValue(source.getTypeCode());
            codeType.setListID(source.getTypeCodeListid());
            target.setTypeCode(codeType);
        }
    }

    private void mapRelatedVesselTransportMeans(FishingActivity target, Set<VesselTransportMeansEntity> entities) {
        if (CollectionUtils.isNotEmpty(entities) && target != null){
            List<VesselTransportMeans> vesselTransportMeansList = new ArrayList<>();
            for (VesselTransportMeansEntity source : entities) {
                VesselTransportMeans vesselTransportMeans = new VesselTransportMeans();
                setRoleCode(vesselTransportMeans, source.getRoleCodeListId(), source.getRoleCode());
                mapNames(vesselTransportMeans, source.getName());
                mapRegistrationVesselCountry(vesselTransportMeans, source.getCountry(), source.getCountrySchemeId());
                mapRegistrationEvent(vesselTransportMeans, source.getRegistrationEvent());
                mapIDs(vesselTransportMeans, source.getVesselIdentifiers());
                mapSpecifiedContactParties(vesselTransportMeans, source.getContactParty());
                vesselTransportMeans.setGrantedFLAPDocuments(FlapDocumentMapper.INSTANCE.mapToFlapDocumentList(source.getFlapDocuments()));
                vesselTransportMeansList.add(vesselTransportMeans);
            }
            target.setRelatedVesselTransportMeans(vesselTransportMeansList);
        }
    }

    private void mapSpecifiedVesselTransportMeans(FAReportDocument target, Set<VesselTransportMeansEntity> entities) {
        if (CollectionUtils.isNotEmpty(entities) && target != null){
            VesselTransportMeans vesselTransportMeans = new VesselTransportMeans();
            VesselTransportMeansEntity source = entities.iterator().next();
            setRoleCode(vesselTransportMeans, source.getRoleCodeListId(), source.getRoleCode());
            mapNames(vesselTransportMeans, source.getName());
            mapRegistrationVesselCountry(vesselTransportMeans, source.getCountry(), source.getCountrySchemeId());
            mapRegistrationEvent(vesselTransportMeans, source.getRegistrationEvent());
            mapIDs(vesselTransportMeans, source.getVesselIdentifiers());
            mapSpecifiedContactParties(vesselTransportMeans, source.getContactParty());

            vesselTransportMeans.setGrantedFLAPDocuments(FlapDocumentMapper.INSTANCE.mapToFlapDocumentList(source.getFlapDocuments()));
            target.setSpecifiedVesselTransportMeans(vesselTransportMeans);
        }
    }

    private void mapSpecifiedContactParties(VesselTransportMeans target, Set<ContactPartyEntity> contactPartyEntities) {
        ArrayList<ContactParty> contactParties = new ArrayList<>();
        for (ContactPartyEntity source : Utils.safeIterable(contactPartyEntities)) {
            ContactParty contactParty = new ContactParty();

            ContactPerson contactPerson = ContactPersonMapper.INSTANCE.mapToContactPerson(source.getContactPerson());

            if (contactPerson != null){
                contactParty.setSpecifiedContactPersons(Collections.singletonList(contactPerson)); // TODO @Greg mapstruct add non-iterable type
            }
            mapRoles(contactParty, source.getContactPartyRole());

            contactParty.setSpecifiedStructuredAddresses(StructuredAddressMapper.INSTANCE.mapToStructuredAddressList(source.getStructuredAddresses()));
            contactParties.add(contactParty);
        }
        target.setSpecifiedContactParties(contactParties);
    }

    private void mapRoles(ContactParty target, Set<ContactPartyRoleEntity> contactPartyRoleEntities) {
        List<CodeType> codeTypeList = new ArrayList<>();
        for (ContactPartyRoleEntity source : Utils.safeIterable(contactPartyRoleEntities)) {
            CodeType codeType = new CodeType();
            codeType.setValue(source.getRoleCode());
            codeType.setListID(source.getRoleCodeListId());
            codeTypeList.add(codeType);
        }
        target.setRoleCodes(codeTypeList);
    }

    private void mapIDs(VesselTransportMeans vesselTransportMeans, Set<VesselIdentifierEntity> vesselIdentifiers) {
        if (CollectionUtils.isNotEmpty(vesselIdentifiers)){
            List<IDType> idTypeList = new ArrayList<>();
            for (VesselIdentifierEntity vesselIdentifierEntity : vesselIdentifiers){
                IDType idType = new IDType();
                idType.setValue( vesselIdentifierEntity.getVesselIdentifierId());
                idType.setSchemeID(vesselIdentifierEntity.getVesselIdentifierSchemeId());
                idTypeList.add(idType);
            }
            vesselTransportMeans.setIDS(idTypeList);
        }
    }

    private void mapRegistrationEvent(VesselTransportMeans target, RegistrationEventEntity source) {
        if (ObjectUtils.allNotNull(target, source)){
            RegistrationEvent registrationEvent = new RegistrationEvent();
            mapOccurrenceDateTime(registrationEvent, source.getOccurrenceDatetime());
            mapDescription(registrationEvent, source.getDescription(), source.getDescLanguageId());
            mapRelatedRegistrationLocation(registrationEvent, source.getRegistrationLocation());
            target.setSpecifiedRegistrationEvents(singletonList(registrationEvent));
        }
    }

    private void mapDescription(RegistrationEvent target, String description, String descLanguageId) {
        if (ObjectUtils.allNotNull(target) && StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(descLanguageId)) {
            TextType textType = new TextType();
            if (StringUtils.isNotEmpty(description)){
                textType.setValue(description);
            }
            if (StringUtils.isNotEmpty(descLanguageId)){
                textType.setLanguageID(descLanguageId);
            }
            target.setDescriptions(Collections.singletonList(textType));
        }
    }

    private void mapRelatedRegistrationLocation(RegistrationEvent target, RegistrationLocationEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            RegistrationLocation registrationLocation = new RegistrationLocation();
            setDescriptions(registrationLocation, source);
            mapCountryID(registrationLocation, source);
            setGeopoliticalRegionCode(registrationLocation, source);
            mapNames(registrationLocation, source);
            mapPurposeCode(registrationLocation, source);
            target.setRelatedRegistrationLocation(registrationLocation);
        }
    }

    private void mapPurposeCode(RegistrationLocation target, RegistrationLocationEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            CodeType codeType = new CodeType();
            codeType.setValue(source.getTypeCode());
            codeType.setListID(source.getTypeCodeListId());
            target.setTypeCode(codeType);
        }
    }

    private void mapNames(RegistrationLocation target, RegistrationLocationEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            TextType textType = new TextType();
            textType.setValue(source.getName());
            textType.setLanguageID(source.getNameLanguageId());
            target.setNames(singletonList(textType));
        }
    }

    private void setDescriptions(RegistrationLocation target,  RegistrationLocationEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            TextType textType = new TextType();
            textType.setValue(source.getDescription());
            textType.setLanguageID(source.getDescLanguageId());
            target.setDescriptions(singletonList(textType));
        }
    }

    private void setGeopoliticalRegionCode(RegistrationLocation target, RegistrationLocationEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            CodeType codeType = new CodeType();
            codeType.setListID(source.getRegionCodeListId());
            codeType.setValue(source.getRegionCode());
            target.setGeopoliticalRegionCode(codeType);
        }
    }

    private void mapCountryID(RegistrationLocation target, RegistrationLocationEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            IDType idType = new IDType();
            idType.setSchemeID(source.getLocationCountrySchemeId());
            idType.setValue(source.getLocationCountryId());
            target.setCountryID(idType);
        }
    }

    private void mapOccurrenceDateTime(RegistrationEvent target, Instant source) {
        if (ObjectUtils.allNotNull(target, source)) {
            DateTimeType dateTimeType = instantToDateTimeTypeUTC(source);
            target.setOccurrenceDateTime(dateTimeType);
        }
    }

    private void mapRegistrationVesselCountry(VesselTransportMeans target, String country, String schemeID) {
        if (ObjectUtils.allNotNull(target)) {
            VesselCountry vesselCountry = new VesselCountry();
            IDType idType = new IDType();
            idType.setValue(country);
            idType.setSchemeID(schemeID);
            vesselCountry.setID(idType);
            target.setRegistrationVesselCountry(vesselCountry);
        }
    }

    private void mapNames(VesselTransportMeans target, String name) {
        if (ObjectUtils.allNotNull(target)) {
            TextType textType = new TextType();
            textType.setValue(name);
            target.setNames(singletonList(textType));
        }
    }

    private void mapRelatedReportIDs(FAReportDocument target, Set<FaReportIdentifierEntity> faReportIdentifiers) {
        if (CollectionUtils.isNotEmpty(faReportIdentifiers)){
            List<IDType> idTypeList = new ArrayList<>();
            for (FaReportIdentifierEntity source : faReportIdentifiers){
                IDType idType = new IDType();
                String faReportIdentifierId = source.getFaReportIdentifierId();
                String faReportIdentifierSchemeId = source.getFaReportIdentifierSchemeId();
                idType.setSchemeID(faReportIdentifierSchemeId);
                idType.setValue(faReportIdentifierId);
                idTypeList.add(idType);
            }
            target.setRelatedReportIDs(idTypeList);
        }
    }

    private void mapFMCMarkerCode(FAReportDocument target, String fmcMarkerValue, String fmcMarkerListId) {
        if (ObjectUtils.allNotNull(target) && (StringUtils.isNotEmpty(fmcMarkerValue) || StringUtils.isNotEmpty(fmcMarkerListId))) {
            CodeType codeType = new CodeType();

            if (StringUtils.isNotEmpty(fmcMarkerValue)){
                codeType.setValue(fmcMarkerValue);
            }
            if (StringUtils.isNotEmpty(fmcMarkerListId)){
                codeType.setListID(fmcMarkerListId);
            }
            target.setFMCMarkerCode(codeType);
        }
    }

    private void setRoleCode(VesselTransportMeans target, String roleCodeListId, String roleCodeListValue) {
        if (ObjectUtils.allNotNull(target)) {
            CodeType codeType = new CodeType();
            codeType.setValue(roleCodeListValue);
            codeType.setListID(roleCodeListId);
            target.setRoleCode(codeType);
        }
    }

    private void mapPurposeCode(FAReportDocument target, String typeCode, String listId) {
        if (ObjectUtils.allNotNull(target)) {
            CodeType codeType = new CodeType();
            codeType.setValue(typeCode);
            codeType.setListID(listId);
            target.setTypeCode(codeType);
        }
    }

    private void mapAcceptanceDateTime(FAReportDocument target, Instant source) {
        if (ObjectUtils.allNotNull(target, source)) {
            DateTimeType dateTimeType = instantToDateTimeTypeUTC(source);
            target.setAcceptanceDateTime(dateTimeType);
        }
    }
}
