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
import java.util.Date;
import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
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
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationEvent;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselCountry;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.TextType;

/**
 * TODO create test
 */
public class FaReportEntityToModelMapper {

    FAReportDocument mapToFAReportDocument(FaReportDocumentEntity entity){

        FAReportDocument faReportDocument = new FAReportDocument();

        mapAcceptanceDateTime(faReportDocument, entity.getAcceptedDatetime());

        mapTypeCode(faReportDocument, entity.getTypeCode(), entity.getTypeCodeListId());

        mapFMCMarkerCode(faReportDocument, entity.getFmcMarker(), entity.getFmcMarkerListId());

        mapRelatedFLUXReportDocument(faReportDocument, entity.getFluxReportDocument());

        mapRelatedReportIDs(faReportDocument,entity.getFaReportIdentifiers());

        mapSpecifiedVesselTransportMeans(faReportDocument, entity.getVesselTransportMeans());

        Set<FishingActivityEntity> fishingActivities = entity.getFishingActivities(); // TODO MAP
        FluxFaReportMessageEntity fluxFaReportMessage = entity.getFluxFaReportMessage();// TODO MAP

        return faReportDocument;
    }

    private void mapSpecifiedVesselTransportMeans(FAReportDocument faReportDocument, Set<VesselTransportMeansEntity> entities) {

        VesselTransportMeans vesselTransportMeans = new VesselTransportMeans();

        //vesselTransportMeans.set
        if(isNotEmpty(entities)){

            VesselTransportMeansEntity entity = entities.iterator().next();

            setRoleCode(vesselTransportMeans, entity.getRoleCodeListId(), entity.getRoleCode());
            setNames(vesselTransportMeans, entity.getName());
            setRegistrationVesselCountry(vesselTransportMeans, entity.getCountry(), entity.getCountrySchemeId());
            setRegistrationEvent(vesselTransportMeans, entity.getRegistrationEvent());

            Set<VesselIdentifierEntity> vesselIdentifiers = entity.getVesselIdentifiers(); // TODO MAP
            Set<ContactPartyEntity> contactParty = entity.getContactParty(); // TODO MAP
            FishingActivityEntity fishingActivity = entity.getFishingActivity(); // TODO MAP
            Set<FlapDocumentEntity> flapDocuments = entity.getFlapDocuments();  // TODO MAP

        }

        faReportDocument.setSpecifiedVesselTransportMeans(vesselTransportMeans);

    }

    private void setRegistrationEvent(VesselTransportMeans vesselTransportMeans, RegistrationEventEntity entity) {
        RegistrationEvent registrationEvent = new RegistrationEvent();

        setDateTime(registrationEvent, entity.getOccurrenceDatetime());

        setRelatedRegistrationLocation(registrationEvent, entity.getRegistrationLocation());

        vesselTransportMeans.setSpecifiedRegistrationEvents(singletonList(registrationEvent));
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
