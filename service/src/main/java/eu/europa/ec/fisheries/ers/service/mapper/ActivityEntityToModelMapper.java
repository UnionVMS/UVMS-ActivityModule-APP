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
import java.util.UUID;

import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyRoleEntity;
import eu.europa.ec.fisheries.ers.fa.entities.ContactPersonEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FlapDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxCharacteristicEntity;
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
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactPerson;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLAPDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXCharacteristic;
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
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IndicatorType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.QuantityType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.TextType;

/**
 * TODO create test
 */
public class ActivityEntityToModelMapper {

    @SneakyThrows
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
        dateTimeType.setDateTime(DateUtils.getCurrentDate());
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
        mapRelatedFLUXReportDocument(target, source.getFluxReportDocument());
        mapRelatedReportIDs(target,source.getFaReportIdentifiers());
        mapSpecifiedVesselTransportMeans(target, source.getVesselTransportMeans());
        mapFishingActivities(target, source.getFishingActivities());
    }

    private void mapFishingActivities(FAReportDocument faReportDocument, Set<FishingActivityEntity> fishingActivityEntities) {

        if (CollectionUtils.isNotEmpty(fishingActivityEntities)){

            List<FishingActivity> fishingActivityList = new ArrayList<>();

            for (FishingActivityEntity source : fishingActivityEntities) {

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
                mapFluxLocations(target, source.getFluxLocations());
                mapSpecifiedFLUXCharacteristics(target, source.getFluxCharacteristics());


                source.getFishingGears(); // TODO MAP
                source.getFlagState(); // TODO MAP
                source.getFluxLocations(); // TODO MAP
                //target.getSpecifiedFACatches() // TODO map

                fishingActivityList.add(target);
            }

            faReportDocument.setSpecifiedFishingActivities(fishingActivityList);
        }
    }

    private void mapSpecifiedFLUXCharacteristics(FishingActivity fishingActivity, Set<FluxCharacteristicEntity> fluxCharacteristics) {
        if (CollectionUtils.isNotEmpty(fluxCharacteristics)){
            List<FLUXCharacteristic> fluxCharacteristicList = new ArrayList<>();
            for (FluxCharacteristicEntity source : fluxCharacteristics) {
                FLUXCharacteristic target = new FLUXCharacteristic();
                mapPurposeCode(target, source);
                mapValueCode(target, source);
                mapValueDateTime(target, source);
                mapValueIndicator(target, source);
                mapValueQuantity(target, source);
                mapValueMeasure(target, source);
                mapDescriptions(target, source);
                mapValues(target, source);
                fluxCharacteristicList.add(target);
            }
            fishingActivity.setSpecifiedFLUXCharacteristics(fluxCharacteristicList);
        }
    }

    private void mapValues(FLUXCharacteristic target, FluxCharacteristicEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            String valueText = source.getValueText();
            if (StringUtils.isNotEmpty(valueText)) {
                TextType textType = new TextType();
                textType.setValue(valueText);
                target.setValues(Collections.singletonList(textType));
            }
        }
    }

    private void mapDescriptions(FLUXCharacteristic target, FluxCharacteristicEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            String description = source.getDescription();
            String valueLanguageId = source.getValueLanguageId();
            if (StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(valueLanguageId)){
                TextType textType = new TextType();
                if (StringUtils.isNotEmpty(description)){
                    textType.setValue(description);
                }
                if (StringUtils.isNotEmpty(valueLanguageId)){
                    textType.setLanguageID(source.getValueLanguageId());
                }
                target.setDescriptions(Collections.singletonList(textType));
            }
        }
    }

    private void mapValueMeasure(FLUXCharacteristic target, FluxCharacteristicEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            Double valueMeasure = source.getValueMeasure();
            String valueMeasureUnitCode = source.getValueMeasureUnitCode();

            if (valueMeasure != null || StringUtils.isNotEmpty(valueMeasureUnitCode)){
                MeasureType measureType = new MeasureType();
                if (valueMeasure != null){
                    measureType.setValue(new BigDecimal(valueMeasure));
                }
                if (StringUtils.isNotEmpty(valueMeasureUnitCode)){
                    measureType.setUnitCode(valueMeasureUnitCode);
                }
                target.setValueMeasure(measureType);
            }

        }
    }

    private void mapValueQuantity(FLUXCharacteristic target, FluxCharacteristicEntity source) {
        if (ObjectUtils.allNotNull(target, source)){
            Double valueQuantity = source.getValueQuantity();
            String valueQuantityCode = source.getValueQuantityCode();
            if (StringUtils.isNotEmpty(valueQuantityCode) ||  valueQuantity != null){
                QuantityType quantityType = new QuantityType();
                if (StringUtils.isNotEmpty(valueQuantityCode)){
                    quantityType.setUnitCode(valueQuantityCode);
                }
                if (valueQuantity != null){
                    quantityType.setValue(new BigDecimal(valueQuantity));
                }
                target.setValueQuantity(quantityType);
            }
        }
    }

    private void mapValueIndicator(FLUXCharacteristic target, FluxCharacteristicEntity source) {
        if (ObjectUtils.allNotNull(target, source)){
            String valueIndicator = source.getValueIndicator();
            if (StringUtils.isNotEmpty(valueIndicator)){
                IndicatorType type = new IndicatorType();
                IndicatorType.IndicatorString indicatorString = new IndicatorType.IndicatorString();
                indicatorString.setValue(valueIndicator);
                type.setIndicatorString(indicatorString);
                target.setValueIndicator(type);
            }
        }
    }

    private void mapValueDateTime(FLUXCharacteristic target, FluxCharacteristicEntity source) {
        if (ObjectUtils.allNotNull(target, source)){
            Date valueDateTime = source.getValueDateTime();
            if (valueDateTime != null){
                DateTimeType dateTimeType = new DateTimeType();
                dateTimeType.setDateTime(DateUtils.dateToXmlGregorian(valueDateTime));
                target.setValueDateTime(dateTimeType);
            }
        }
    }

    private void mapValueCode(FLUXCharacteristic target, FluxCharacteristicEntity source) {
        if (ObjectUtils.allNotNull(target, source)){
            if (source.getValueCode() != null){
                CodeType codeType = new CodeType();
                codeType.setValue(source.getValueCode());
                target.setTypeCode(codeType);
            }
        }
    }

    private void mapFluxLocations(FishingActivity target, Set<FluxLocationEntity> fluxLocationEntities) {
        if (CollectionUtils.isNotEmpty(fluxLocationEntities)){
            List<FLUXLocation> fluxLocations = new ArrayList<>();
            for (FluxLocationEntity source : fluxLocationEntities) {
                FLUXLocation location = new FLUXLocation();
                mapFLUXLocation(location, source);
                fluxLocations.add(location);
            }
            target.setRelatedFLUXLocations(fluxLocations);
        }
    }

    private void mapFLUXLocation(FLUXLocation target, FluxLocationEntity source) {

            setTypeCode(target, source);

            Double latitude = source.getLatitude();
            Double longitude = source.getLongitude();
            Double altitude = source.getAltitude();
            String fluxLocationIdentifier = source.getFluxLocationIdentifier();
            GearProblemEntity gearProblem = source.getGearProblem();
            Set<StructuredAddressEntity> structuredAddresses = source.getStructuredAddresses();
            String countryId = source.getCountryId();

    }

    private void setTypeCode(FLUXLocation target, FluxLocationEntity source) {
        if (ObjectUtils.allNotNull(target, source)){
            String fluxLocationType = source.getFluxLocationType();
            //String typeCode = source.getF();
           // if(StringUtils.isNotEmpty(fluxLocationType) || StringUtils.isNotEmpty(typeCode)){
                CodeType codeType = new CodeType();

                if (StringUtils.isNotEmpty(fluxLocationType)){
                   // codeType.setValue();
                    //codeType.se}

               // target.setTypeCode();
        }

    }}

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

    private void mapPurposeCode(FLUXCharacteristic target, FluxCharacteristicEntity source) {
        if (ObjectUtils.allNotNull(target, source)) {
            if (source.getTypeCode() != null){
                CodeType codeType = new CodeType();
                codeType.setValue(source.getTypeCode());
                target.setTypeCode(codeType);
            }
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

    private void mapRelatedFLUXReportDocument(FAReportDocument target, FluxReportDocumentEntity source) {
        if (ObjectUtils.allNotNull(target, source)){
            FLUXReportDocument fluxReportDocument = new FLUXReportDocument();
           // mapTypeCode(fluxReportDocument, source.getPurposeCode(), source.getPurposeCodeListId());

            mapPurposeCode(fluxReportDocument, source.getPurposeCode(), source.getPurposeCodeListId());
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
            mapGrantedFLAPDocuments(vesselTransportMeans, source.getFlapDocuments());
            target.setSpecifiedVesselTransportMeans(vesselTransportMeans);
        }
    }

    private void mapGrantedFLAPDocuments(VesselTransportMeans vesselTransportMeans, Set<FlapDocumentEntity> flapDocumentEntities) {
        if (ObjectUtils.allNotNull(vesselTransportMeans) && CollectionUtils.isNotEmpty(flapDocumentEntities)){
            List<FLAPDocument> flapDocuments = new ArrayList<>();
            for (FlapDocumentEntity entity : flapDocumentEntities){
                String flapDocumentId = entity.getFlapDocumentId();
                String flapDocumentSchemeId = entity.getFlapDocumentSchemeId();
                FLAPDocument flapDocument = new FLAPDocument();
                IDType idType = new IDType();
                idType.setSchemeID(flapDocumentSchemeId);
                idType.setValue(flapDocumentId);
                flapDocument.setID(idType);
                flapDocuments.add(flapDocument);
            }
            vesselTransportMeans.setGrantedFLAPDocuments(flapDocuments);
        }
    }

    private void mapSpecifiedContactParties(VesselTransportMeans target, Set<ContactPartyEntity> contactPartyEntities) {
        ArrayList<ContactParty> contactParties = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(contactPartyEntities)) {
            for (ContactPartyEntity source : contactPartyEntities){
                ContactParty contactParty = new ContactParty();
                mapContactPerson(contactParty, source.getContactPerson());
                mapRoles(contactParty, source.getContactPartyRole());
                mapStructuredAddress(contactParty, source.getStructuredAddresses());
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

                structuredAddress.setPostalArea(new TextType(source.getPostalAreaValue(), source.getPostalAreaLanguageID(), source.getPostalAreaLanguageLocaleID()));
                structuredAddress.setBlockName(new TextType(source.getBlockName(), null, null));
                structuredAddress.setBuildingName(new TextType(source.getBuildingName(), null,null));

                if (StringUtils.isNotEmpty(source.getStreetName())){
                    structuredAddress.setStreetName(new TextType(source.getStreetName(), null,null));
                }

                if (StringUtils.isNotEmpty(source.getCountrySubdivisionName())){
                    structuredAddress.setCountrySubDivisionName(new TextType(source.getCountrySubdivisionName(), null,null));
                }

                if (StringUtils.isNotEmpty(source.getPostOfficeBox())){
                    structuredAddress.setPostOfficeBox(new TextType(source.getPostOfficeBox(), null,null));
                }

                if (StringUtils.isNotEmpty(source.getBuildingNumber())){
                    structuredAddress.setBuildingNumber(new TextType(source.getBuildingNumber(), null,null));
                }

                if (StringUtils.isNotEmpty(source.getPlotId())){
                    structuredAddress.setPlotIdentification(new TextType(source.getPlotId(), null,null));
                }

                //structuredAddress.setBuildingNumber(new TextType(source.get(), null,null)); // FIXME not saved in DB
                structuredAddress.setCityName(new TextType(source.getCityName(), null,null));
                structuredAddress.setCitySubDivisionName(new TextType(source.getCitySubdivisionName(), null,null));
                //structuredAddress.setFloorIdentification(new TextType(source.get(), null,null)); // FIXME not saved in DB
                structuredAddress.setCountryName(new TextType(source.getCountryName(), null,null));
                mapCountryID(structuredAddress, source.getCountryIDValue(),  source.getCountryIDSchemeID());
                mapPostcode(structuredAddress, source.getPostcode(), source.getPostcodeListID());

                structuredAddressList.add(structuredAddress);
            }
        }

        target.setSpecifiedStructuredAddresses(structuredAddressList);
    }

    private void mapCountryID(StructuredAddress structuredAddress, String countryIDValue, String countryIDSchemeID) {
        if (ObjectUtils.allNotNull(structuredAddress) && (StringUtils.isNotEmpty(countryIDValue) || StringUtils.isNotEmpty(countryIDSchemeID))){
            IDType idType = new IDType();
            if (StringUtils.isNotEmpty(countryIDSchemeID)){
                idType.setSchemeID(countryIDSchemeID);
            }
            if (StringUtils.isNotEmpty(countryIDValue)){
                idType.setValue(countryIDValue);
            }
            structuredAddress.setCountryID(idType);
        }
    }

    private void mapPostcode(StructuredAddress structuredAddress, String postcode, String postcodeListID) {
        if (ObjectUtils.allNotNull(structuredAddress) && (StringUtils.isNotEmpty(postcode) || StringUtils.isNotEmpty(postcodeListID))){
            CodeType codeType = new CodeType();
            if (StringUtils.isNotEmpty(postcode)){
                codeType.setValue(postcode);
            }
            if (StringUtils.isNotEmpty(postcodeListID)){
                codeType.setListID(postcodeListID);
            }
            structuredAddress.setPostcodeCode(codeType);
        }
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

    private void mapPurposeCode(FLUXReportDocument target, String typeCode, String listId) {
        if (ObjectUtils.allNotNull(target)) {
            CodeType codeType = new CodeType();
            codeType.setValue(typeCode);
            codeType.setListID(listId);
            target.setPurposeCode(codeType);
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

    private void mapAcceptanceDateTime(FAReportDocument target, Date source) {
        if (ObjectUtils.allNotNull(target, source)) {
            DateTimeType dateTimeType = new DateTimeType();
            dateTimeType.setDateTime(DateUtils.dateToXmlGregorian(source));
            target.setAcceptanceDateTime(dateTimeType);
        }
    }
}
