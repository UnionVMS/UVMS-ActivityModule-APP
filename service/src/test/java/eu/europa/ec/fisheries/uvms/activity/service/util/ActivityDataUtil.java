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

package eu.europa.ec.fisheries.uvms.activity.service.util;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.ContactPartyRoleEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.ContactPersonEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxPartyEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxPartyIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.RegistrationEventEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.SizeDistributionClassCodeEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.SizeDistributionEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sanera on 31/08/2016.
 */
public class ActivityDataUtil {

    private static List<Object[]> faCatches;
    private static List<FaCatchEntity> faCatchesEntities;

    public static FluxReportDocumentEntity getFluxReportDocumentEntity(String fluxDocumentID, String referenceID, Instant creationDateTime, String purposeCode, String purposeCodeListId, String purpose, String ownerFluxPartyId, String ownerFluxPartyName) {
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

    public static VesselTransportMeansEntity getVesselTransportMeansEntity(String roleCode, String roleCodeListId, String name, RegistrationEventEntity registrationEventEntity) {
        VesselTransportMeansEntity vesselTransportMeansEntity = new VesselTransportMeansEntity();
        vesselTransportMeansEntity.setRoleCode(roleCode);
        vesselTransportMeansEntity.setRoleCodeListId(roleCodeListId);
        vesselTransportMeansEntity.setName(name);
        vesselTransportMeansEntity.setRegistrationEvent(registrationEventEntity);
        return vesselTransportMeansEntity;
    }

    public static FaReportDocumentEntity getFaReportDocumentEntity(String typeCode, String typeCodeListId, Instant acceptedDatetime, FluxReportDocumentEntity fluxReportDocumentEntity, VesselTransportMeansEntity vesselTransportMeansEntity, FaReportStatusType status) {
        FaReportDocumentEntity faReportDocumentEntity = new FaReportDocumentEntity();
        faReportDocumentEntity.setTypeCode(typeCode);
        faReportDocumentEntity.setTypeCodeListId(typeCodeListId);
        faReportDocumentEntity.setAcceptedDatetime(acceptedDatetime);
        faReportDocumentEntity.setFluxReportDocument(fluxReportDocumentEntity);
        faReportDocumentEntity.setVesselTransportMeans(new HashSet<>(Arrays.asList(vesselTransportMeansEntity)));
        faReportDocumentEntity.setStatus(status.name());
        return faReportDocumentEntity;
    }

    public static FishingActivityEntity getFishingActivityEntity(String typeCode, String typeCodeListId, Instant occurence, String reasonCode, String reasonCodeListId, FaReportDocumentEntity faReportDocumentEntity, FishingActivityEntity relatedfishingActivityEntity) {
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


    public static SizeDistributionEntity getSizeDistributionEntity(String classCode, String classCodeListId, String categoryCode, String categoryCodeListId) {
        SizeDistributionEntity sizeDistributionEntity = new SizeDistributionEntity();

        SizeDistributionClassCodeEntity entity = new SizeDistributionClassCodeEntity();
        entity.setClassCode(classCode);
        entity.setClassCodeListId(classCodeListId);
        sizeDistributionEntity.setSizeDistributionClassCodeEntities(new HashSet<>(Arrays.asList(entity)));

        sizeDistributionEntity.setCategoryCode(categoryCode);
        sizeDistributionEntity.setCategoryCodeListId(categoryCodeListId);
        return sizeDistributionEntity;
    }

    public static FaCatchEntity getFaCatchEntity(FishingActivityEntity fishingActivityEntity, String typeCode, String typeCodeListId, String speciesCode, String speciesCodeListid, Double unitQuantity, Double weightMeasure, String weightMeasureUnitCode,
                                                 String weighingMeansCode, String weighingMeansCodeListId, SizeDistributionEntity sizeDistributionEntity) {
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


    public static FishingTripEntity getFishingTripEntity(String typeCode, String typeCodeListId, FaCatchEntity faCatchEntity, FishingActivityEntity fishingActivityEntity) {
        FishingTripEntity fishingTripEntity = new FishingTripEntity();
        fishingTripEntity.setTypeCode(typeCode);
        fishingTripEntity.setTypeCodeListId(typeCodeListId);
        fishingTripEntity.getCatchEntities().add(faCatchEntity);
        fishingTripEntity.getFishingActivities().add(fishingActivityEntity);
        return fishingTripEntity;
    }


    public static FishingTripIdentifierEntity getFishingTripIdentifierEntity(FishingTripEntity fishingTripEntity, String tripId, String tripSchemeId) {
        FishingTripIdentifierEntity fishingTripIdentifierEntity = new FishingTripIdentifierEntity();
        //fishingTripIdentifierEntity.setFishingTrip(fishingTripEntity);
        fishingTripIdentifierEntity.setTripId(tripId);
        fishingTripIdentifierEntity.setTripSchemeId(tripSchemeId);
        fishingTripIdentifierEntity.setCalculatedTripEndDate(Instant.parse("2016-01-12T00:00:00Z"));
        fishingTripIdentifierEntity.setCalculatedTripStartDate(Instant.parse("2013-01-12T00:00:00Z"));
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

    public static Set<VesselIdentifierEntity> getVesselIdentifiers(VesselTransportMeansEntity vesselTransportMeansEntity1, String ident_, String scheme_) {
        Set<VesselIdentifierEntity> identifiers = new HashSet<>();
        VesselIdentifierEntity identifier = new VesselIdentifierEntity();
        identifier.setVesselTransportMeans(vesselTransportMeansEntity1);
        identifier.setVesselIdentifierId(ident_);
        identifier.setVesselIdentifierSchemeId(scheme_);
        identifiers.add(identifier);
        return identifiers;
    }

    public static List<FaCatchEntity> getFaCatchesEntities() {
        List<FaCatchEntity> faCatchEntities = new ArrayList<>();
        FluxReportDocumentEntity fluxReportDocumentEntity1=   ActivityDataUtil.getFluxReportDocumentEntity("FLUX_REPORT_DOCUMENT1",null, DateUtils.parseToUTCDate("2016-06-27 07:47:31","yyyy-MM-dd HH:mm:ss"),
                "PURPOSE", "PURPOSE_CODE_LIST",null, "OWNER_FLUX_ID1","flux1");
        VesselTransportMeansEntity vesselTransportMeansEntity1= ActivityDataUtil.getVesselTransportMeansEntity("PAIR_FISHING_PARTNER", "FA_VESSEL_ROLE", "vesselGroup1", null);
        vesselTransportMeansEntity1.setVesselIdentifiers(ActivityDataUtil.getVesselIdentifiers(vesselTransportMeansEntity1, "IDENT_1", "CFR"));
        FaReportDocumentEntity faReportDocumentEntity1=  ActivityDataUtil.getFaReportDocumentEntity("Declaration" , "FLUX_FA_REPORT_TYPE", DateUtils.parseToUTCDate("2016-06-27 07:47:31","yyyy-MM-dd HH:mm:ss"), fluxReportDocumentEntity1,
                vesselTransportMeansEntity1, FaReportStatusType.NEW);
        FishingActivityEntity fishingActivityEntity1 = ActivityDataUtil.getFishingActivityEntity("DEPARTURE", "FLUX_FA_TYPE", DateUtils.parseToUTCDate("2014-05-27 07:47:31", "yyyy-MM-dd HH:mm:ss"), "FISHING", "FIS", faReportDocumentEntity1, null);
        SizeDistributionEntity sizeDistributionEntity = ActivityDataUtil.getSizeDistributionEntity("LSC", "FISH_SIZE_CLASS", "BFT", "FA_BFT_SIZE_CATEGORY");

        FaCatchEntity faCatchEntity1 = ActivityDataUtil.getFaCatchEntity(fishingActivityEntity1, "LOADED", "FA_CATCH_TYPE", "COD", "FAO_SPECIES",
                11112D, 11112.0D, "KGM", "BFT", "WEIGHT_MEANS", sizeDistributionEntity);
        FaCatchEntity faCatchEntity2 = ActivityDataUtil.getFaCatchEntity(fishingActivityEntity1, "ONBOARD", "FA_CATCH_TYPE", "HKE", "FAO_SPECIES",
                11112D, 11112.0D, "KGM", "BFT", "WEIGHT_MEANS", sizeDistributionEntity);
        FaCatchEntity faCatchEntity3 = ActivityDataUtil.getFaCatchEntity(fishingActivityEntity1, "UNLOADED", "FA_CATCH_TYPE", "HAD", "FAO_SPECIES",
                11112D, 11112.0D, "KGM", "BFT", "WEIGHT_MEANS", sizeDistributionEntity);
        FaCatchEntity faCatchEntity4 = ActivityDataUtil.getFaCatchEntity(fishingActivityEntity1, "DEMINIMIS", "FA_CATCH_TYPE", "POK", "FAO_SPECIES",
                11112D, 11112.0D, "KGM", "BFT", "WEIGHT_MEANS", sizeDistributionEntity);

        faCatchEntities.add(faCatchEntity1);
        faCatchEntities.add(faCatchEntity2);
        faCatchEntities.add(faCatchEntity3);
        faCatchEntities.add(faCatchEntity4);

        return faCatchEntities;
    }
}
