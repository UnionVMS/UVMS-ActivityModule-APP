/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.service.mapper;

import static java.util.Collections.singletonList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyRoleEntity;
import eu.europa.ec.fisheries.ers.fa.entities.ContactPersonEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FlapDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxPartyIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearProblemEntity;
import eu.europa.ec.fisheries.ers.fa.entities.RegistrationEventEntity;
import eu.europa.ec.fisheries.ers.fa.entities.RegistrationLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.StructuredAddressEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselStorageCharCodeEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselStorageCharacteristicsEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactPerson;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingActivity;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationEvent;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.StructuredAddress;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselCountry;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselStorageCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.QuantityType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.TextType;

/**
 * TODO create test
 */
public class FLUXFAReportMessageEntityToFLUXFAReportMessageMapper {

    public FLUXFAReportMessage mapToFLUXFAReportMessage(List<FaReportDocumentEntity> faReportMessageEntity){
        FLUXFAReportMessage target = new FLUXFAReportMessage();
        if (CollectionUtils.isNotEmpty(faReportMessageEntity)){
            FluxFaReportMessageEntity source = faReportMessageEntity.iterator().next().getFluxFaReportMessage();
            mapFLUXReportDocument(target, source);
            mapFAReportDocuments(target, source.getFaReportDocuments());
        }
        return target;
    }

    private void mapFAReportDocuments(FLUXFAReportMessage target, Set<FaReportDocumentEntity> faReportDocuments) {
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
        mapTypeCode(target, source.getTypeCode(), source.getTypeCodeListId());
        mapFMCMarkerCode(target, source.getFmcMarker(), source.getFmcMarkerListId());
        mapRelatedFLUXReportDocument(target, source.getFluxReportDocument());
        mapRelatedReportIDs(target,source.getFaReportIdentifiers());
        mapSpecifiedVesselTransportMeans(target, source.getVesselTransportMeans());
        mapFishingActivities(target, source.getFishingActivities());
    }

    private void mapFLUXReportDocument(FLUXFAReportMessage fluxfaReportMessage, FluxFaReportMessageEntity fluxFaReportMessageEntity) {
        if (ObjectUtils.allNotNull(fluxfaReportMessage, fluxfaReportMessage)){
            FluxReportDocumentEntity fluxReportDocumentEntity = fluxFaReportMessageEntity.getFluxReportDocument();
            if (fluxReportDocumentEntity != null){
                FLUXReportDocument fluxReportDocument = new FLUXReportDocument();
                mapTypeCode(fluxReportDocument, fluxReportDocumentEntity.getPurposeCode(), fluxReportDocumentEntity.getPurposeCodeListId());
                mapCreationDateTime(fluxReportDocument, fluxReportDocumentEntity.getCreationDatetime());
                mapReferencedID(fluxReportDocument, fluxReportDocumentEntity.getReferenceId(), fluxReportDocumentEntity.getReferenceSchemeId());
                mapTextType(fluxReportDocument, fluxReportDocumentEntity.getPurpose());
                mapOwnerFLUXParty(fluxReportDocument, fluxReportDocumentEntity.getFluxParty());
                mapIDs(fluxReportDocument, fluxReportDocumentEntity.getFluxReportIdentifiers());
                fluxfaReportMessage.setFLUXReportDocument(fluxReportDocument);
            }
        }
    }

    private void mapFishingActivities(FAReportDocument faReportDocument, Set<FishingActivityEntity> fishingActivityEntities) {

        if (CollectionUtils.isNotEmpty(fishingActivityEntities)){

            List<FishingActivity> fishingActivityList = new ArrayList<>();

            for (FishingActivityEntity source : fishingActivityEntities) {

                FishingActivity target = new FishingActivity();
                mapTypeCode(target, source);
                mapReasonCode(target, source);
                mapOperationsQuantity(target, source);
                mapFisheryTypeCode(target, source);
                mapSpeciesTargetCode(target, source);
                mapFishingDurationMeasure(target, source);
                mapVesselRelatedActivityCode(target, source);
                mapOccurrenceDateTime(target, source.getOccurence());
                mapSourceVesselStorageCharacteristic(target, source.getSourceVesselCharId());
                mapDestinationVesselStorageCharacteristic(target, source.getDestVesselCharId());

                mapFluxLocations(target, source.getFluxLocations());

                source.getFluxCharacteristics(); // TODO MAP
                source.getFishingGears(); // TODO MAP
                source.getFlagState(); // TODO MAP
                source.getFluxLocations(); // TODO MAP
                //target.getSpecifiedFACatches() // TODO map

                fishingActivityList.add(target);
            }

            faReportDocument.setSpecifiedFishingActivities(fishingActivityList);
        }
    }

    private void mapFluxLocations(FishingActivity target, Set<FluxLocationEntity> fluxLocationEntities) {
        List<FLUXLocation> fluxLocations = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(fluxLocationEntities)){
            for (FluxLocationEntity source : fluxLocationEntities) {
                FLUXLocation location = new FLUXLocation();
                mapFLUXLocation(location, source);
                fluxLocations.add(location);
            }
        }
        target.setRelatedFLUXLocations(fluxLocations);
    }

    private void mapFLUXLocation(FLUXLocation target, FluxLocationEntity source) {
        if (ObjectUtils.allNotNull(target, source)){

            Double latitude = source.getLatitude();
            Double longitude = source.getLongitude();
            Double altitude = source.getAltitude();
            String fluxLocationType = source.getFluxLocationType();
            String fluxLocationIdentifier = source.getFluxLocationIdentifier();
            GearProblemEntity gearProblem = source.getGearProblem();
            String typeCode = source.getTypeCode();
            Set<StructuredAddressEntity> structuredAddresses = source.getStructuredAddresses();
            String countryId = source.getCountryId();


        }
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
        if (ObjectUtils.allNotNull(target, source)) {
            CodeType codeType = new CodeType();
            codeType.setValue(source.getVesselActivityCode());
            codeType.setValue(source.getVesselActivityCodeListId());
            target.setSpeciesTargetCode(codeType);
        }
    }

    private void mapFishingDurationMeasure(FishingActivity target, FishingActivityEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            MeasureType measureType = new MeasureType();
            Double fishingDurationMeasure = source.getFishingDurationMeasure();
            if (fishingDurationMeasure != null){
                measureType.setValue(new BigDecimal(fishingDurationMeasure));
            }
            measureType.setUnitCode(source.getFishingDurationMeasureCode());
            target.setFishingDurationMeasure(measureType);
        }
    }

    private void mapSpeciesTargetCode(FishingActivity target, FishingActivityEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            CodeType codeType = new CodeType();
            codeType.setValue(source.getSpeciesTargetCode());
            codeType.setValue(source.getSpeciesTargetCodeListId());
            target.setSpeciesTargetCode(codeType);
        }
    }

    private void mapFisheryTypeCode(FishingActivity target, FishingActivityEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            CodeType codeType = new CodeType();
            codeType.setValue(source.getFisheryTypeCode());
            codeType.setListID(source.getFisheryTypeCodeListId());
            target.setFisheryTypeCode(codeType);
        }
    }

    private void mapOperationsQuantity(FishingActivity target, FishingActivityEntity source) {
        if (ObjectUtils.allNotNull(target, target)){
            QuantityType quantityType = new QuantityType();
            Double operationQuantity = source.getOperationQuantity();
            if (operationQuantity != null){
                quantityType.setValue(new BigDecimal(operationQuantity));
            }
            quantityType.setUnitCode(source.getOperationQuantityCode());
            target.setOperationsQuantity(quantityType);
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

    private void mapOccurrenceDateTime(FishingActivity target, Date source) {
        if (ObjectUtils.allNotNull(target, source)){
            DateTimeType dateTimeType = new DateTimeType();
            dateTimeType.setDateTime(DateUtils.dateToXmlGregorian(source));
            target.setOccurrenceDateTime(dateTimeType);
        }
    }

    private void mapTypeCode(FishingActivity target, FishingActivityEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            CodeType codeType = new CodeType();
            codeType.setValue(source.getTypeCode());
            codeType.setListID(source.getTypeCodeListid());
            target.setTypeCode(codeType);
        }
    }

    private void mapRelatedFLUXReportDocument(FAReportDocument target, FluxReportDocumentEntity source) {
        if (ObjectUtils.allNotNull(target, source)){
            FLUXReportDocument fluxReportDocument = new FLUXReportDocument();
            mapTypeCode(fluxReportDocument, source.getPurposeCode(), source.getPurposeCodeListId());
            mapCreationDateTime(fluxReportDocument, source.getCreationDatetime());
            mapReferencedID(fluxReportDocument, source.getReferenceId(), source.getReferenceSchemeId());
            mapTextType(fluxReportDocument, source.getPurpose());
            mapOwnerFLUXParty(fluxReportDocument, source.getFluxParty());
            mapIDs(fluxReportDocument, source.getFluxReportIdentifiers());
            target.setRelatedFLUXReportDocument(fluxReportDocument);
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
            target.setSpecifiedVesselTransportMeans(vesselTransportMeans);
        }
    }

    private void mapGrantedFLAPDocuments(FishingActivity vesselTransportMeans, Set<FlapDocumentEntity> flapDocumentEntities) { // TODO map

    }

    private void mapSpecifiedContactParties(VesselTransportMeans target, Set<ContactPartyEntity> contactPartyEntities) {
        ArrayList<ContactParty> contactParties = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(contactPartyEntities)) {
            for (ContactPartyEntity source : contactPartyEntities){
                ContactParty contactParty = new ContactParty();
                mapContactPerson(contactParty, source.getContactPerson());
                mapRoles(contactParty, source.getContactPartyRole());
                mapStructuredAddress(contactParty, source.getStructuredAddresses());//TODO map
                contactParties.add(contactParty);
            }
        }
        target.setSpecifiedContactParties(contactParties);
    }

    private void mapStructuredAddress(ContactParty target, Set<StructuredAddressEntity> structuredAddressEntities) {

        List<StructuredAddress> structuredAddressList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(structuredAddressEntities)){
            for (StructuredAddressEntity source : structuredAddressEntities) {
                StructuredAddress structuredAddress = new StructuredAddress();
                //structuredAddress.setPostalArea(new TextType(source.getPostcode(), source.getP)); // FIXME not saved in DB
                structuredAddress.setBlockName(new TextType(source.getBlockName(), null, null));
                structuredAddress.setBuildingName(new TextType(source.getBuildingName(), null,null));
                //structuredAddress.setBuildingNumber(new TextType(source.get(), null,null)); // FIXME not saved in DB
                structuredAddress.setCityName(new TextType(source.getCityName(), null,null));
                structuredAddress.setCitySubDivisionName(new TextType(source.getCitySubdivisionName(), null,null));
                //structuredAddress.setFloorIdentification(new TextType(source.get(), null,null)); // FIXME not saved in DB
                structuredAddress.setCountryName(new TextType(source.getCountryName(), null,null));

                CodeType codeType = new CodeType();
                codeType.setValue(source.getPostcode());
                structuredAddress.setPostcodeCode(codeType);

                IDType idType = new IDType();
                idType.setValue(source.getCountry());
                structuredAddress.setCountryID(idType);

                structuredAddressList.add(structuredAddress);
            }
        }

        target.setSpecifiedStructuredAddresses(structuredAddressList);
    }

    private void mapRoles(ContactParty target, Set<ContactPartyRoleEntity> contactPartyRoleEntities) {
        List<CodeType> codeTypeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(contactPartyRoleEntities)){
            for (ContactPartyRoleEntity source : contactPartyRoleEntities){
                CodeType codeType = new CodeType();
                codeType.setValue(source.getRoleCode());
                codeType.setListID(source.getRoleCodeListId());
                codeTypeList.add(codeType);
            }
        }
        target.setRoleCodes(codeTypeList);
    }

    private void mapContactPerson(ContactParty target, ContactPersonEntity contactPersonEntity) {
        if (ObjectUtils.allNotNull(target, contactPersonEntity)){
            contactPersonEntity.getGender(); //TODO
            ContactPerson source = new ContactPerson();
            source.setAlias(new TextType(contactPersonEntity.getAlias(), null, null));
            source.setGivenName(new TextType(contactPersonEntity.getGivenName(), null, null));
            source.setFamilyName(new TextType(contactPersonEntity.getFamilyName(), null, null));
            source.setFamilyNamePrefix(new TextType(contactPersonEntity.getFamilyNamePrefix(), null, null));
            source.setMiddleName(new TextType(contactPersonEntity.getMiddleName(), null, null));
            source.setTitle(new TextType(contactPersonEntity.getTitle(), null, null));
            source.setNameSuffix(new TextType(contactPersonEntity.getNameSuffix(), null, null));
            target.setSpecifiedContactPersons(Collections.singletonList(source));
        }
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

    private void mapIDs(FLUXReportDocument target, Set<FluxReportIdentifierEntity> fluxReportIdentifiers) {
        if (CollectionUtils.isNotEmpty(fluxReportIdentifiers)){
            List<IDType> idTypeList = new ArrayList<>();
            for (FluxReportIdentifierEntity source : fluxReportIdentifiers){
                IDType idType = new IDType();
                String fluxReportIdentifierId = source.getFluxReportIdentifierId();
                String fluxReportIdentifierSchemeId = source.getFluxReportIdentifierSchemeId();
                idType.setSchemeID(fluxReportIdentifierSchemeId);
                idType.setValue(fluxReportIdentifierId);
                idTypeList.add(idType);
            }
            target.setIDS(idTypeList);
        }
    }

    private void mapRegistrationEvent(VesselTransportMeans target, RegistrationEventEntity source) {
        if (ObjectUtils.allNotNull(target, source)){
            RegistrationEvent registrationEvent = new RegistrationEvent();
            mapOccurrenceDateTime(registrationEvent, source.getOccurrenceDatetime());
            mapRelatedRegistrationLocation(registrationEvent, source.getRegistrationLocation());
            target.setSpecifiedRegistrationEvents(singletonList(registrationEvent));
        }
    }

    private void mapRelatedRegistrationLocation(RegistrationEvent target, RegistrationLocationEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            RegistrationLocation registrationLocation = new RegistrationLocation();
            setDescriptions(registrationLocation, source);
            mapCountryID(registrationLocation, source);
            setGeopoliticalRegionCode(registrationLocation, source);
            mapNames(registrationLocation, source);
            mapTypeCode(registrationLocation, source);
            target.setRelatedRegistrationLocation(registrationLocation);
        }
    }

    private void mapTypeCode(RegistrationLocation target, RegistrationLocationEntity source) {
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

    private void mapOccurrenceDateTime(RegistrationEvent target, Date source) {
        if (ObjectUtils.allNotNull(target, source)){
            DateTimeType dateTimeType = new DateTimeType();
            dateTimeType.setDateTime(DateUtils.dateToXmlGregorian(source));
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

    private void mapOwnerFLUXParty(FLUXReportDocument target, FluxPartyEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            FLUXParty fluxParty = new FLUXParty();
            String fluxPartyName = source.getFluxPartyName();
            String nameLanguageId = source.getNameLanguageId();
            mapNamesAndLanguageId(fluxParty, fluxPartyName, nameLanguageId);
            Set<FluxPartyIdentifierEntity> fluxPartyIdentifiers = source.getFluxPartyIdentifiers();
            mapFluxPartyIdentifiers(fluxParty, fluxPartyIdentifiers);
            target.setOwnerFLUXParty(fluxParty);
        }
    }

    private void mapFluxPartyIdentifiers(FLUXParty target, Set<FluxPartyIdentifierEntity> fluxPartyIdentifierEntities) {
        if (CollectionUtils.isNotEmpty(fluxPartyIdentifierEntities)){
            List<IDType> idTypeList = new ArrayList<>();
            for (FluxPartyIdentifierEntity source : fluxPartyIdentifierEntities){
                IDType idType = new IDType();
                String fluxPartyIdentifierId = source.getFluxPartyIdentifierId();
                String fluxPartyIdentifierSchemeId = source.getFluxPartyIdentifierSchemeId();
                idType.setSchemeID(fluxPartyIdentifierSchemeId);
                idType.setValue(fluxPartyIdentifierId);
                idTypeList.add(idType);
            }
            target.setIDS(idTypeList);
        }
    }

    private void mapNamesAndLanguageId(FLUXParty target, String fluxPartyName, String nameLanguageId) {
        if (ObjectUtils.allNotNull(target)) {
            TextType textType = new TextType();
            textType.setValue(fluxPartyName);
            textType.setLanguageID(nameLanguageId);
            target.setNames(singletonList(textType));
        }
    }

    private void mapTextType(FLUXReportDocument target, String purposeValue) {
        if (ObjectUtils.allNotNull(target)) {
            TextType textType = new TextType();
            textType.setValue(purposeValue);
            target.setPurpose(textType);
        }
    }

    private void mapReferencedID(FLUXReportDocument target, String referenceId, String referenceSchemeId) {
        if (ObjectUtils.allNotNull(target)) {
            IDType idType = new IDType();
            idType.setValue(referenceId);
            idType.setSchemeID(referenceSchemeId);
            target.setReferencedID(idType);
        }
    }

    private void mapCreationDateTime(FLUXReportDocument target, Date source) {
        if (ObjectUtils.allNotNull(target, source)) {
            DateTimeType dateTimeType = new DateTimeType();
            dateTimeType.setDateTime(DateUtils.dateToXmlGregorian(source));
            target.setCreationDateTime(dateTimeType);
        }
    }

    private void mapFMCMarkerCode(FAReportDocument target, String fmcMarkerValue, String fmcMarkerListId) {
        if (ObjectUtils.allNotNull(target)) {
            CodeType codeType = new CodeType();
            codeType.setValue(fmcMarkerValue);
            codeType.setListID(fmcMarkerListId);
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

    private void mapTypeCode(FLUXReportDocument target, String typeCode, String listId) {
        if (ObjectUtils.allNotNull(target)) {
            CodeType codeType = new CodeType();
            codeType.setValue(typeCode);
            codeType.setListID(listId);
            target.setTypeCode(codeType);
        }
    }

    private void mapTypeCode(FAReportDocument target, String typeCode, String listId) {
        if (ObjectUtils.allNotNull(target)) {
            CodeType codeType = new CodeType();
            codeType.setValue(typeCode);
            codeType.setListID(listId);
            target.setTypeCode(codeType);
        }
    }

    private void mapAcceptanceDateTime(FAReportDocument target, Date source) {
        if (ObjectUtils.allNotNull(target, source)) {
            DateTimeType dateTimeType = new DateTimeType();
            dateTimeType.setDateTime(DateUtils.dateToXmlGregorian(source));
            target.setAcceptanceDateTime(dateTimeType);
        }
    }
}
