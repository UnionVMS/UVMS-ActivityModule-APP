/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.service.util;

import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;

import java.util.Arrays;
import java.util.*;
import java.util.HashSet;

/**
 * Created by sanera on 31/08/2016.
 */
public class ActivityDataUtil {

    public static FluxReportDocumentEntity getFluxReportDocumentEntity(String fluxDocumentID, String referenceID,  Date creationDateTime, String purposeCode, String purposeCodeListId,String purpose,String ownerFluxPartyId,String ownerFluxPartyName ){
        FluxReportDocumentEntity fluxReportDocumentEntity = new FluxReportDocumentEntity();

        FluxReportIdentifierEntity entity = new FluxReportIdentifierEntity();
        entity.setFluxReportIdentifierId(fluxDocumentID);
        fluxReportDocumentEntity.setFluxReportIdentifiers(new HashSet<>(Arrays.asList(entity)));
        fluxReportDocumentEntity.setCreationDatetime(creationDateTime);

        FluxPartyEntity fluxPartyEntity = new FluxPartyEntity();
        fluxPartyEntity.setFluxPartyName(ownerFluxPartyName);
        FluxPartyIdentifierEntity fluxPartyIdentifierEntity = new FluxPartyIdentifierEntity();
        fluxPartyIdentifierEntity.setFluxPartyIdentifierId(ownerFluxPartyId);
        fluxReportDocumentEntity.setFluxParty(fluxPartyEntity);

        fluxReportDocumentEntity.setPurpose(purpose);
        fluxReportDocumentEntity.setPurposeCode(purposeCode);
        fluxReportDocumentEntity.setPurposeCodeListId(purposeCodeListId);
        fluxReportDocumentEntity.setReferenceId(referenceID);
        return fluxReportDocumentEntity;
    }

    public static RegistrationLocationEntity getRegistrationLocationEntity(String description, String regionCode, String regionCodeListId, String name, String typeCode, String typeCodeListId, String locationCountryId, String locationCountrySchemeId){
        RegistrationLocationEntity registrationLocationEntity = new RegistrationLocationEntity();
        registrationLocationEntity.setDescription(description);
        registrationLocationEntity.setRegionCode(regionCode);
        registrationLocationEntity.setRegionCodeListId(regionCodeListId);
        registrationLocationEntity.setName(name);
        registrationLocationEntity.setTypeCode(typeCode);
        registrationLocationEntity.setTypeCodeListId(typeCodeListId);
        registrationLocationEntity.setLocationCountryId(locationCountryId);
        registrationLocationEntity.setLocationCountrySchemeId(locationCountrySchemeId);
        return registrationLocationEntity;
    }

    public static RegistrationEventEntity getRegistrationEventEntity(String description, Date OccurrenceDatetime, RegistrationLocationEntity registrationLocation ){
        RegistrationEventEntity registrationEventEntity = new RegistrationEventEntity();
        registrationEventEntity.setDescription(description);
        registrationEventEntity.setOccurrenceDatetime(OccurrenceDatetime);
        registrationEventEntity.setRegistrationLocation(registrationLocation);
        return registrationEventEntity;
    }


    public static VesselTransportMeansEntity getVesselTransportMeansEntity(String roleCode, String roleCodeListId, String name,RegistrationEventEntity registrationEventEntity ){
        VesselTransportMeansEntity vesselTransportMeansEntity = new VesselTransportMeansEntity();
        vesselTransportMeansEntity.setRoleCode(roleCode);
        vesselTransportMeansEntity.setRoleCodeListId(roleCodeListId);
        vesselTransportMeansEntity.setName(name);
        vesselTransportMeansEntity.setRegistrationEvent(registrationEventEntity);
        return vesselTransportMeansEntity;
    }

    public static FaReportDocumentEntity getFaReportDocumentEntity(String typeCode, String typeCodeListId, Date  acceptedDatetime, FluxReportDocumentEntity fluxReportDocumentEntity,VesselTransportMeansEntity vesselTransportMeansEntity,String status ){
        FaReportDocumentEntity faReportDocumentEntity = new FaReportDocumentEntity();
        faReportDocumentEntity.setTypeCode(typeCode);
        faReportDocumentEntity.setTypeCodeListId(typeCodeListId);
        faReportDocumentEntity.setAcceptedDatetime(acceptedDatetime);
        faReportDocumentEntity.setFluxReportDocument(fluxReportDocumentEntity);
        faReportDocumentEntity.setVesselTransportMeans(vesselTransportMeansEntity);
        faReportDocumentEntity.setStatus(status);
        return faReportDocumentEntity;
    }

    public static FishingActivityEntity getFishingActivityEntity(String typeCode, String typeCodeListId, Date  occurence, String reasonCode,String reasonCodeListId,FaReportDocumentEntity faReportDocumentEntity,FishingActivityEntity relatedfishingActivityEntity ){
        FishingActivityEntity fishingActivityEntity = new FishingActivityEntity();
        fishingActivityEntity.setTypeCode(typeCode);
        fishingActivityEntity.setTypeCodeListid(typeCodeListId);
        fishingActivityEntity.setOccurence(occurence);
        fishingActivityEntity.setReasonCode(reasonCode);
        fishingActivityEntity.setReasonCodeListId(reasonCodeListId);
        fishingActivityEntity.setFaReportDocument(faReportDocumentEntity);
        fishingActivityEntity.setRelatedFishingActivity(relatedfishingActivityEntity);
        return fishingActivityEntity;
    }


    public static SizeDistributionEntity getSizeDistributionEntity(String classCode, String classCodeListId, String  categoryCode, String categoryCodeListId ){
        SizeDistributionEntity sizeDistributionEntity = new SizeDistributionEntity();

        SizeDistributionClassCodeEntity entity = new SizeDistributionClassCodeEntity();
        entity.setClassCode(classCode);
        entity.setClassCodeListId(classCodeListId);
        sizeDistributionEntity.setSizeDistributionClassCode(new HashSet<>(Arrays.asList(entity)));

        sizeDistributionEntity.setCategoryCode(categoryCode);
        sizeDistributionEntity.setCategoryCodeListId(categoryCodeListId);
        return sizeDistributionEntity;
    }

    public static FaCatchEntity getFaCatchEntity(FishingActivityEntity fishingActivityEntity,String typeCode, String typeCodeListId, String  speciesCode, String speciesCodeListid,Long unitQuantity,Double weightMeasure,String weightMeasureUnitCode,
             String weighingMeansCode,String weighingMeansCodeListId,SizeDistributionEntity sizeDistributionEntity  ){
        FaCatchEntity faCatchEntity = new FaCatchEntity();
        faCatchEntity.setFishingActivity(fishingActivityEntity);
        faCatchEntity.setTypeCode(typeCode);
        faCatchEntity.setTypeCodeListId(typeCodeListId);
        faCatchEntity.setSpeciesCode(speciesCode);
        faCatchEntity.setSpeciesCodeListid(speciesCodeListid);
        faCatchEntity.setUnitQuantity(unitQuantity);
        faCatchEntity.setWeightMeasure(weightMeasure);
        faCatchEntity.setWeightMeasureUnitCode(weightMeasureUnitCode);
        faCatchEntity.setWeighingMeansCode(weighingMeansCode);
        faCatchEntity.setSizeDistribution(sizeDistributionEntity);
        faCatchEntity.setWeighingMeansCodeListId(weighingMeansCodeListId);
        return faCatchEntity;
    }


    public static FishingTripEntity getFishingTripEntity(String typeCode, String typeCodeListId, FaCatchEntity  faCatchEntity, FishingActivityEntity fishingActivityEntity ){
        FishingTripEntity fishingTripEntity = new FishingTripEntity();
        fishingTripEntity.setTypeCode(typeCode);
        fishingTripEntity.setTypeCodeListId(typeCodeListId);
        fishingTripEntity.setFaCatch(faCatchEntity);
        fishingTripEntity.setFishingActivity(fishingActivityEntity);
        return fishingTripEntity;
    }


    public static FishingTripIdentifierEntity getFishingTripIdentifierEntity(FishingTripEntity fishingTripEntity, String tripId, String tripSchemeId ){
        FishingTripIdentifierEntity fishingTripIdentifierEntity = new FishingTripIdentifierEntity();
        fishingTripIdentifierEntity.setFishingTrip(fishingTripEntity);
        fishingTripIdentifierEntity.setTripId(tripId);
        fishingTripIdentifierEntity.setTripSchemeId(tripSchemeId);
        return fishingTripIdentifierEntity;
    }

    public static ContactPartyEntity getContactPartyEntity(String title, String givenName, String middleName, String familyName, String familyNamePrefix, String nameSuffix, String gender, String alias) {
        ContactPartyEntity contPartEntity = new ContactPartyEntity();
        ContactPersonEntity contactPersonEntity = new ContactPersonEntity();
        contactPersonEntity.setTitle(title);
        contactPersonEntity.setGivenName(givenName);
        contactPersonEntity.setMiddleName(middleName);
        contactPersonEntity.setFamilyName(familyName);
        contactPersonEntity.setFamilyNamePrefix(familyNamePrefix);
        contactPersonEntity.setNameSuffix(nameSuffix);
        contactPersonEntity.setGender(gender);
        contactPersonEntity.setAlias(alias);
        contPartEntity.setContactPerson(contactPersonEntity);
        return contPartEntity;
    }

    public static Set<ContactPartyRoleEntity> getContactPartyRole(String roleCode, String roleID, ContactPartyEntity contPartEntity) {
        Set<ContactPartyRoleEntity> rolesList = new HashSet<>();
        ContactPartyRoleEntity entity_1 = new ContactPartyRoleEntity();
        entity_1.setContactParty(contPartEntity);
        entity_1.setRoleCode(roleCode);
        entity_1.setRoleCodeListId(roleID);
        rolesList.add(entity_1);
       return rolesList;
    }

    public static ListAssetResponse getListAssetResponse(){
        ListAssetResponse listResponse = new ListAssetResponse();
        Asset asset = new Asset();
        asset.setCfr("UPDATED_CFR");
        asset.setImo("UPDATED_IMO");
        asset.setIrcs("UPDATED_IRCS");
        listResponse.setCurrentPage(1);
        listResponse.setTotalNumberOfPages(1);
        listResponse.getAsset().add(asset);
        return listResponse;
    }
}
