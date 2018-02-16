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
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

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
import eu.europa.ec.fisheries.ers.fa.entities.FluxPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxPartyIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.RegistrationEventEntity;
import eu.europa.ec.fisheries.ers.fa.entities.RegistrationLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.StructuredAddressEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactPerson;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationEvent;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.StructuredAddress;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselCountry;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.TextType;

/**
 * TODO create test
 */
public class FLUXFAReportMessageEntityToFLUXFAReportMessageMapper {

    public FLUXFAReportMessage mapToFluxFaReportMessageEntity(FluxFaReportMessageEntity fluxFaReportMessageEntity){

        FLUXFAReportMessage fluxfaReportMessage = new FLUXFAReportMessage();

        Set<FaReportDocumentEntity> faReportDocuments = fluxFaReportMessageEntity.getFaReportDocuments();

        List<FAReportDocument> faReportDocumentList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(faReportDocuments)){

            for (FaReportDocumentEntity faReportDocumentEntity : faReportDocuments){
                FAReportDocument faReportDocument = mapToFluxFaReportMessageEntity(faReportDocumentEntity);
                faReportDocumentList.add(faReportDocument);
            }
        }

        fluxfaReportMessage.setFAReportDocuments(faReportDocumentList);

        return fluxfaReportMessage;
    }

    private FAReportDocument mapToFluxFaReportMessageEntity(FaReportDocumentEntity faReportDocumentEntity) {

        FAReportDocument faReportDocument = new FAReportDocument();

        mapAcceptanceDateTime(faReportDocument, faReportDocumentEntity.getAcceptedDatetime());

        mapTypeCode(faReportDocument, faReportDocumentEntity.getTypeCode(), faReportDocumentEntity.getTypeCodeListId());

        mapFMCMarkerCode(faReportDocument, faReportDocumentEntity.getFmcMarker(), faReportDocumentEntity.getFmcMarkerListId());

        mapRelatedFLUXReportDocument(faReportDocument, faReportDocumentEntity.getFluxReportDocument());

        mapRelatedReportIDs(faReportDocument,faReportDocumentEntity.getFaReportIdentifiers());

        mapSpecifiedVesselTransportMeans(faReportDocument, faReportDocumentEntity.getVesselTransportMeans());

        //Set<FishingActivityEntity> fishingActivities = faReportDocumentEntity.getFishingActivities(); // TODO MAP
        //FluxFaReportMessageEntity fluxFaReportMessage = faReportDocumentEntity.getFluxFaReportMessage();// TODO MAP

        return faReportDocument;
    }

        private void mapSpecifiedVesselTransportMeans(FAReportDocument faReportDocument, Set<VesselTransportMeansEntity> entities) {

        VesselTransportMeans vesselTransportMeans = new VesselTransportMeans();

        if (isNotEmpty(entities)){

            VesselTransportMeansEntity transportMeansEntity = entities.iterator().next();

            setRoleCode(vesselTransportMeans, transportMeansEntity.getRoleCodeListId(), transportMeansEntity.getRoleCode());
            setNames(vesselTransportMeans, transportMeansEntity.getName());
            setRegistrationVesselCountry(vesselTransportMeans, transportMeansEntity.getCountry(), transportMeansEntity.getCountrySchemeId());
            setRegistrationEvent(vesselTransportMeans, transportMeansEntity.getRegistrationEvent());
            setIDs(vesselTransportMeans, transportMeansEntity.getVesselIdentifiers());

            setSpecifiedContactParties(vesselTransportMeans, transportMeansEntity.getContactParty());
            FishingActivityEntity fishingActivity = transportMeansEntity.getFishingActivity(); // TODO MAP
            Set<FlapDocumentEntity> flapDocuments = transportMeansEntity.getFlapDocuments();  // TODO MAP

        }

        faReportDocument.setSpecifiedVesselTransportMeans(vesselTransportMeans);

    }

    private void setSpecifiedContactParties(VesselTransportMeans vesselTransportMeans, Set<ContactPartyEntity> contactPartyEntities) {
        ArrayList<ContactParty> contactParties = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(contactPartyEntities)) {
            for (ContactPartyEntity contactPartyEntity : contactPartyEntities){
                ContactParty contactParty = new ContactParty();
                setContactPerson(contactParty, contactPartyEntity.getContactPerson());
                setRoles(contactParty, contactPartyEntity.getContactPartyRole());
                setStructuredAddress(contactParty, contactPartyEntity.getStructuredAddresses());//TODO map
                contactParties.add(contactParty);
            }
        }
        vesselTransportMeans.setSpecifiedContactParties(contactParties);
    }

    private void setStructuredAddress(ContactParty contactParty, Set<StructuredAddressEntity> structuredAddressEntities) {

        List<StructuredAddress> structuredAddressList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(structuredAddressEntities)){
            for (StructuredAddressEntity structuredAddressEntity : structuredAddressEntities) {
                StructuredAddress structuredAddress = new StructuredAddress();
                //structuredAddress.setPostalArea(new TextType(structuredAddressEntity.getPostcode(), structuredAddressEntity.getP)); // FIXME not saved in DB
                structuredAddress.setBlockName(new TextType(structuredAddressEntity.getBlockName(), null, null));
                structuredAddress.setBuildingName(new TextType(structuredAddressEntity.getBuildingName(), null,null));
                //structuredAddress.setBuildingNumber(new TextType(structuredAddressEntity.get(), null,null)); // FIXME not saved in DB
                structuredAddress.setCityName(new TextType(structuredAddressEntity.getCityName(), null,null));
                structuredAddress.setCitySubDivisionName(new TextType(structuredAddressEntity.getCitySubdivisionName(), null,null));
                //structuredAddress.setFloorIdentification(new TextType(structuredAddressEntity.get(), null,null)); // FIXME not saved in DB
                structuredAddress.setCountryName(new TextType(structuredAddressEntity.getCountryName(), null,null));

                CodeType codeType = new CodeType();
                codeType.setValue(structuredAddressEntity.getPostcode());
                structuredAddress.setPostcodeCode(codeType);

                IDType idType = new IDType();
                idType.setValue(structuredAddressEntity.getCountry());
                structuredAddress.setCountryID(idType);

                structuredAddressList.add(structuredAddress);
            }
        }

        contactParty.setSpecifiedStructuredAddresses(structuredAddressList);
    }

    private void setRoles(ContactParty contactParty, Set<ContactPartyRoleEntity> contactPartyRoleEntities) {
        List<CodeType> codeTypeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(contactPartyRoleEntities)){
            for (ContactPartyRoleEntity contactPartyRoleEntity : contactPartyRoleEntities){
                CodeType codeType = new CodeType();
                codeType.setValue(contactPartyRoleEntity.getRoleCode());
                codeType.setListID(contactPartyRoleEntity.getRoleCodeListId());
                codeTypeList.add(codeType);
            }
        }
        contactParty.setRoleCodes(codeTypeList);
    }

    private void setContactPerson(ContactParty contactParty, ContactPersonEntity contactPersonEntity) {
        if (ObjectUtils.allNotNull(contactParty, contactPersonEntity)){
            contactPersonEntity.getGender(); //TODO
            ContactPerson contactPerson = new ContactPerson();

            contactPerson.setAlias(new TextType(contactPersonEntity.getAlias(), null, null));
            contactPerson.setGivenName(new TextType(contactPersonEntity.getGivenName(), null, null));
            contactPerson.setFamilyName(new TextType(contactPersonEntity.getFamilyName(), null, null));
            contactPerson.setFamilyNamePrefix(new TextType(contactPersonEntity.getFamilyNamePrefix(), null, null));
            contactPerson.setMiddleName(new TextType(contactPersonEntity.getMiddleName(), null, null));
            contactPerson.setTitle(new TextType(contactPersonEntity.getTitle(), null, null));
            contactPerson.setNameSuffix(new TextType(contactPersonEntity.getNameSuffix(), null, null));
            contactParty.setSpecifiedContactPersons(Collections.singletonList(contactPerson));
        }
    }

    private void setIDs(VesselTransportMeans vesselTransportMeans, Set<VesselIdentifierEntity> vesselIdentifiers) {
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

    private void setRegistrationEvent(VesselTransportMeans vesselTransportMeans, RegistrationEventEntity registrationEventEntity) {
        if (ObjectUtils.allNotNull(vesselTransportMeans, registrationEventEntity)){
            RegistrationEvent registrationEvent = new RegistrationEvent();
            setDateTime(registrationEvent, registrationEventEntity.getOccurrenceDatetime());
            setRelatedRegistrationLocation(registrationEvent, registrationEventEntity.getRegistrationLocation());
            vesselTransportMeans.setSpecifiedRegistrationEvents(singletonList(registrationEvent));
        }
    }

    private void setRelatedRegistrationLocation(RegistrationEvent registrationEvent, RegistrationLocationEntity entity) {
        RegistrationLocation registrationLocation = new RegistrationLocation();
        setDescriptions(registrationLocation, entity);
        setCountryID(registrationLocation, entity);
        setGeopoliticalRegionCode(registrationLocation, entity);
        setNames(registrationLocation, entity);
        setTypeCode(registrationLocation, entity);
        registrationEvent.setRelatedRegistrationLocation(registrationLocation);
    }

    private void setTypeCode(RegistrationLocation registrationLocation, RegistrationLocationEntity entity) {
        CodeType codeType = new CodeType();
        codeType.setValue(entity.getTypeCode());
        codeType.setListID(entity.getTypeCodeListId());
        registrationLocation.setTypeCode(codeType);
    }

    private void setNames(RegistrationLocation registrationLocation, RegistrationLocationEntity entity) {
        TextType textType = new TextType();
        textType.setValue(entity.getName());
        textType.setLanguageID(entity.getNameLanguageId());
        registrationLocation.setNames(singletonList(textType));
    }

    private void setDescriptions(RegistrationLocation registrationLocation,  RegistrationLocationEntity entity) {
        TextType textType = new TextType();
        textType.setValue(entity.getDescription());
        textType.setLanguageID(entity.getDescLanguageId());
        registrationLocation.setDescriptions(singletonList(textType));
    }

    private void setGeopoliticalRegionCode(RegistrationLocation registrationLocation, RegistrationLocationEntity entity) {
        CodeType codeType = new CodeType();
        codeType.setListID(entity.getRegionCodeListId());
        codeType.setValue(entity.getRegionCode());
        registrationLocation.setGeopoliticalRegionCode(codeType);
    }

    private void setCountryID(RegistrationLocation registrationLocation, RegistrationLocationEntity entity) {
        IDType idType = new IDType();
        idType.setSchemeID(entity.getLocationCountrySchemeId());
        idType.setValue(entity.getLocationCountryId());
        registrationLocation.setCountryID(idType);
    }

    private void setDateTime(RegistrationEvent registrationEvent, Date occurrenceDatetime) {
        DateTimeType dateTimeType = new DateTimeType();
        if (occurrenceDatetime != null){
            dateTimeType.setDateTime(DateUtils.dateToXmlGregorian(occurrenceDatetime));
        }
        registrationEvent.setOccurrenceDateTime(dateTimeType);
    }

    private void setRegistrationVesselCountry(VesselTransportMeans vesselTransportMeans, String country, String schemeID) {
        VesselCountry vesselCountry = new VesselCountry();
        IDType idType = new IDType();
        idType.setValue(country);
        idType.setSchemeID(schemeID);
        vesselCountry.setID(idType);
        vesselTransportMeans.setRegistrationVesselCountry(vesselCountry);
    }

    private void setNames(VesselTransportMeans vesselTransportMeans, String name) {
        TextType textType = new TextType();
        textType.setValue(name);
        vesselTransportMeans.setNames(singletonList(textType));
    }

    private void mapRelatedReportIDs(FAReportDocument faReportDocument, Set<FaReportIdentifierEntity> faReportIdentifiers) {
        if (isNotEmpty(faReportIdentifiers)){
            List<IDType> idTypeList = new ArrayList<>();
            for (FaReportIdentifierEntity entity : faReportIdentifiers){
                IDType idType = new IDType();
                String faReportIdentifierId = entity.getFaReportIdentifierId();
                String faReportIdentifierSchemeId = entity.getFaReportIdentifierSchemeId();
                idType.setSchemeID(faReportIdentifierSchemeId);
                idType.setValue(faReportIdentifierId);
                idTypeList.add(idType);
            }
            faReportDocument.setRelatedReportIDs(idTypeList);
        }
    }

    private void mapRelatedFLUXReportDocument(FAReportDocument faReportDocument, FluxReportDocumentEntity entity) {

        FLUXReportDocument fluxReportDocument = new FLUXReportDocument();

        mapTypeCode(fluxReportDocument, entity.getPurposeCode(), entity.getPurposeCodeListId());

        mapCreationDateTime(fluxReportDocument, entity.getCreationDatetime());

        mapReferencedID(fluxReportDocument, entity.getReferenceId(), entity.getReferenceSchemeId());

        mapTextType(fluxReportDocument, entity.getPurpose());

        mapOwnerFLUXParty(fluxReportDocument, entity.getFluxParty());

        mapIDS(fluxReportDocument, entity.getFluxReportIdentifiers());

        faReportDocument.setRelatedFLUXReportDocument(fluxReportDocument);
    }

    private void mapIDS(FLUXReportDocument fluxReportDocument, Set<FluxReportIdentifierEntity> fluxReportIdentifiers) {
        if (isNotEmpty(fluxReportIdentifiers)){
            List<IDType> idTypeList = new ArrayList<>();
            for (FluxReportIdentifierEntity entity : fluxReportIdentifiers){
                IDType idType = new IDType();
                String fluxReportIdentifierId = entity.getFluxReportIdentifierId();
                String fluxReportIdentifierSchemeId = entity.getFluxReportIdentifierSchemeId();
                idType.setSchemeID(fluxReportIdentifierSchemeId);
                idType.setValue(fluxReportIdentifierId);
                idTypeList.add(idType);
            }
            fluxReportDocument.setIDS(idTypeList);
        }
    }

    private void mapOwnerFLUXParty(FLUXReportDocument fluxReportDocument, FluxPartyEntity entity) {
        FLUXParty fluxParty = new FLUXParty();
        if (entity != null){
            String fluxPartyName = entity.getFluxPartyName();
            String nameLanguageId = entity.getNameLanguageId();
            mapNamesAndLanguageId(fluxParty, fluxPartyName, nameLanguageId);
            Set<FluxPartyIdentifierEntity> fluxPartyIdentifiers = entity.getFluxPartyIdentifiers();
            mapFluxPartyIdentifiers(fluxParty, fluxPartyIdentifiers);
        }
        fluxReportDocument.setOwnerFLUXParty(fluxParty);
    }

    private void mapFluxPartyIdentifiers(FLUXParty fluxParty, Set<FluxPartyIdentifierEntity> fluxPartyIdentifierEntities) {
        if (isNotEmpty(fluxPartyIdentifierEntities)){
            List<IDType> idTypeList = new ArrayList<>();
            for (FluxPartyIdentifierEntity entity : fluxPartyIdentifierEntities){
                IDType idType = new IDType();
                String fluxPartyIdentifierId = entity.getFluxPartyIdentifierId();
                String fluxPartyIdentifierSchemeId = entity.getFluxPartyIdentifierSchemeId();
                idType.setSchemeID(fluxPartyIdentifierSchemeId);
                idType.setValue(fluxPartyIdentifierId);
                idTypeList.add(idType);
            }
            fluxParty.setIDS(idTypeList);
        }
    }

    private void mapNamesAndLanguageId(FLUXParty fluxParty, String fluxPartyName, String nameLanguageId) {
        TextType textType = new TextType();
        textType.setValue(fluxPartyName);
        textType.setLanguageID(nameLanguageId);
        fluxParty.setNames(singletonList(textType));
    }

    private void mapTextType(FLUXReportDocument fluxReportDocument, String purposeValue) {
        TextType textType = new TextType();
        textType.setValue(purposeValue);
        fluxReportDocument.setPurpose(textType);
    }

    private void mapReferencedID(FLUXReportDocument fluxReportDocument, String referenceId, String referenceSchemeId) {
        IDType idType = new IDType();
        idType.setValue(referenceId);
        idType.setSchemeID(referenceSchemeId);
        fluxReportDocument.setReferencedID(idType);
    }

    private void mapCreationDateTime(FLUXReportDocument fluxReportDocument, Date creationDatetime) {
        DateTimeType dateTimeType = new DateTimeType();
        dateTimeType.setDateTime(DateUtils.dateToXmlGregorian(creationDatetime));
        fluxReportDocument.setCreationDateTime(dateTimeType);
    }

    private void mapFMCMarkerCode(FAReportDocument faReportDocument, String fmcMarkerValue, String fmcMarkerListId) {
        CodeType codeType = new CodeType();
        codeType.setValue(fmcMarkerValue);
        codeType.setListID(fmcMarkerListId);
        faReportDocument.setFMCMarkerCode(codeType);
    }

    private void setRoleCode(VesselTransportMeans vesselTransportMeans, String roleCodeListId, String roleCodeListValue) {
        CodeType codeType = new CodeType();
        codeType.setValue(roleCodeListValue);
        codeType.setListID(roleCodeListId);
        vesselTransportMeans.setRoleCode(codeType);
    }

    private void mapTypeCode(FLUXReportDocument fluxReportDocument, String typeCode, String listId) {
        CodeType codeType = new CodeType();
        codeType.setValue(typeCode);
        codeType.setListID(listId);
        fluxReportDocument.setTypeCode(codeType);
    }

    private void mapTypeCode(FAReportDocument faReportDocument, String typeCode, String listId) {
        CodeType codeType = new CodeType();
        codeType.setValue(typeCode);
        codeType.setListID(listId);
        faReportDocument.setTypeCode(codeType);
    }

    private void mapAcceptanceDateTime(FAReportDocument faReportDocument, Date acceptedDatetime) {
        if(acceptedDatetime != null){
            DateTimeType dateTimeType = new DateTimeType();
            dateTimeType.setDateTime(DateUtils.dateToXmlGregorian(acceptedDatetime));
            faReportDocument.setAcceptanceDateTime(dateTimeType);
        }
    }
}
