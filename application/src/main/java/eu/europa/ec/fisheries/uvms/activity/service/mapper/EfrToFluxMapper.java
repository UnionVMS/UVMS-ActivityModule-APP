package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearRoleEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripKey;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.GearCharacteristicEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.LocationEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.LocationEnum;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.UnitCodeEnum;
import eu.europa.ec.fisheries.uvms.activity.service.util.Utils;
import se.havochvatten.efr.efropenapi.model.ArrivalLocation;
import se.havochvatten.efr.efropenapi.model.CatchGearSettings;
import se.havochvatten.efr.efropenapi.model.CatchSpecies;
import se.havochvatten.efr.efropenapi.model.FishingCatch;
import se.havochvatten.efr.efropenapi.model.FishingReport;
import se.havochvatten.efr.efropenapi.model.PriorNotificationEstimatedCatch;
import se.havochvatten.efr.efropenapi.model.UserSpecifiedLocation;

import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Stateless
public class EfrToFluxMapper {

    private static final String PURPOSE_ORIGINAL = "9"; // From MDR list MDR_FLUX_GP_Purpose
    private static final BigDecimal ZERO_BIG_DECIMAL = new BigDecimal(0);

    // TODO have one document for all fishing activities instead of each one in separate documents?

    // TODO private static class MappingContext that holds references to FishingTripEntity (that there should be only one of),
    // the EFR fishing report, etc? We could pass just this instance around to all the methods.

    // TODO can't have SWE hard coded as flag state in case other countries want to use code?

    // TODO consistency of using null or empty collections

    /**
     * This conversion assumes that the fishing report documents a fully completed fishing trip
     * entered using the EFR app. As such, we make assumptions about values in the FLUX message,
     * such as setting the reason for arrival to "Landing".
     *
     * @param efrFishingReport
     * @return
     */
    public FluxFaReportMessageEntity efrFishingReportToFluxMessageEntity(FishingReport efrFishingReport) {
        FluxFaReportMessageEntity result = new FluxFaReportMessageEntity();

        FishingTripEntity fishingTripEntity = createFishingTripForMessage(efrFishingReport);

        // Chapter 7.1.1 in spec
        result.setFluxReportDocument_Id(efrFishingReport.getFishingReportId().toString());
        result.setFluxReportDocument_IdSchemeId("UUID");
        // result.setFluxReportDocument_ReferencedFaQueryMessageId(); kan nog skippa (min 0 enligt spec)
        // result.setFluxReportDocument_ReferencedFaQueryMessageSchemeId(); kan nog skippa (min 0 enligt spec)
        result.setFluxReportDocument_CreationDatetime(Instant.now()); // TODO eller ta efrFishingReport.getServerCreatedAt()?
        result.setFluxReportDocument_PurposeCode(PURPOSE_ORIGINAL);
        result.setFluxReportDocument_PurposeCodeListId("FLUX_GP_PURPOSE");
        // result.setFluxReportDocument_Purpose(); kan nog skippa (min 0 enligt spec) om vi inte har något bra att säga

        // Antar att det här är owner i FLUXReportDocument
        // Table 3 in spec
        result.setFluxParty_identifier("SWE");
        result.setFluxParty_schemeId("FLUX_GP_PARTY");
        // result.setFluxParty_name();  kan nog skippa (min 0 enligt spec)
        // result.setFluxParty_nameLanguageId();  kan nog skippa (min 0 enligt spec)

        Set<FaReportDocumentEntity> documents = new HashSet<>();
        documents.add(createDepartureDocument(result, fishingTripEntity, efrFishingReport));

        Optional<FaReportDocumentEntity> catchesDocument = createCatchesDocument(result, fishingTripEntity, efrFishingReport);
        if (catchesDocument.isPresent()) {
            documents.add(catchesDocument.get());
        }

        Optional<FaReportDocumentEntity> discardsDocument = createDiscardsDocument(result, fishingTripEntity, efrFishingReport);
        if (discardsDocument.isPresent()) {
            documents.add(discardsDocument.get());
        }

        documents.add(createPriorNotificationOfArrivalDocument(result, fishingTripEntity, efrFishingReport));

        // TODO arrival document, in addition to prior notification of arrival?

        documents.add(createLandingDocument(result, fishingTripEntity, efrFishingReport));

        // Final step: Add all FA Report Documents to result
        result.setFaReportDocuments(documents);

        return result;
    }

    // Chapter 7.1.15.9 in spec
    private FishingTripEntity createFishingTripForMessage(FishingReport efrFishingReport) {
        FishingTripKey fishingTripKey = new FishingTripKey();
        fishingTripKey.setTripId(efrFishingReport.getFishingReportId().toString()); // TODO I guess?
        fishingTripKey.setTripSchemeId("UUID");

        FishingTripEntity result = new FishingTripEntity();
        result.setFishingTripKey(fishingTripKey);

        //result.setTypeCode("???"); We can probably skip this. Has only one valid value in the MDR and that is for joint fishing operations
        //result.setTypeCodeListId("FISHING_TRIP_TYPE");

        //result.setCalculatedTripStartDate(); // TODO we should add this to the EFR fishing report DTO
        result.setCalculatedTripEndDate(efrFishingReport.getPriorNotification() != null ?
                efrFishingReport.getPriorNotification().getEstimatedLandingTime() : null);

        // catches and fishing activities can be set on a FishingTripEntity but why would we?

        return result;
    }

    // Chapter 7.1.2 in spec
    private FaReportDocumentEntity createBasicDocument(FluxFaReportMessageEntity message, FishingReport efrFishingReport) {
        FaReportDocumentEntity result = new FaReportDocumentEntity();
        // result.setTypeCode(); kan nog skippa (min 0 enligt spec)
        // result.setTypeCodeListId("FLUX_FA_REPORT_TYPE");
        // result.setRelatedFaReportIdentifiers(); Skippar alla related reports
        result.setAcceptedDatetime(Instant.now()); // TODO eller något från efrFishingReport?
        // result.setFmcMarker(); kan nog skippa (min 0 enligt spec)
        // result.setFmcMarkerListId("FLUX_FA_FMC");

        // Table 5 in spec
        result.setFluxReportDocument_Id(UUID.randomUUID().toString()); // Vi har inte ID:n på delar av fiskerapporten i EFR så behöver generera ett här (används som PK i databasen)
        result.setFluxReportDocument_IdSchemeId("UUID");
        // result.setFluxReportDocument_ReferencedFaReportDocumentId(); kan nog skippa
        // result.setFluxReportDocument_ReferencedFaReportDocumentSchemeId(); kan nog skippa
        result.setFluxReportDocument_CreationDatetime(Instant.now());  // TODO eller något från efrFishingReport? "The UTC date time of the creation of this FLUXFAReportDocument"
        result.setFluxReportDocument_PurposeCode(PURPOSE_ORIGINAL);
        result.setFluxReportDocument_PurposeCodeListId("FLUX_GP_PURPOSE");
        // result.setFluxReportDocument_Purpose(); kan nog skippa (min 0 enligt spec) TODO eller skriva något om att det är en färdigställd lax-resa?
        result.setFluxFaReportMessage(message);

        // Table 6 in spec
        // Antar att det här är owner i FLUXReportDocument
        result.setFluxParty_identifier("SWE");
        result.setFluxParty_schemeId("FLUX_GP_PARTY");
        // result.setFluxParty_name();  kan nog skippa (min 0 enligt spec)
        // result.setFluxParty_nameLanguageId();  kan nog skippa (min 0 enligt spec)

        result.setVesselTransportMeans(createVesselTransportMeans(efrFishingReport));

        return result;
    }

    private FishingActivityEntity createBasicFishingActivity(FishingTripEntity fishingTripEntity, FaReportDocumentEntity faReportDocument) {
        FishingActivityEntity result = new FishingActivityEntity();
        result.setFaReportDocument(faReportDocument);

        result.setFishingTrip(fishingTripEntity);
        result.setFlagState("SWE"); // TODO not hardcoded

        return result;
    }

    private Set<VesselTransportMeansEntity> createVesselTransportMeans(FishingReport efrFishingReport) {
        VesselTransportMeansEntity vessel = new VesselTransportMeansEntity();
        vessel.setRoleCode("TODO depends on fishing activity"); // Behövs? "Mandatory for the vessels, other than the reporting vessel, participating to the fishing activity."
        vessel.setRoleCodeListId("FA_VESSEL_ROLE");
        VesselIdentifierEntity vesselIdentifier = new VesselIdentifierEntity();
        vesselIdentifier.setVesselIdentifierId(efrFishingReport.getShipCfr());
        vesselIdentifier.setVesselIdentifierSchemeId("CFR");
        vesselIdentifier.setVesselTransportMeans(vessel);
        // TODO should we pass more identifiers from backend?
        vessel.setVesselIdentifiers(Set.of(vesselIdentifier));
        // vessel.setName(); Skippa antagligen, är min 0 i specen och finns inte i DTOn
        vessel.setCountry("TODO sverige"); // TODO om inte hårdkoda till sverige, hur veta vilket land?
        vessel.setCountrySchemeId("TERRITORY");

        // TODO varifrån ska vi få detta?
        //ContactPartyEntity contactParty = new ContactPartyEntity();
        //vessel.setContactParty(Set.of(contactParty));

        return Set.of(vessel);
    }

    // Chapter 7.1.3 in spec
    private FaReportDocumentEntity createDepartureDocument(FluxFaReportMessageEntity message, FishingTripEntity fishingTripEntity, FishingReport efrFishingReport) {
        FaReportDocumentEntity result = createBasicDocument(message, efrFishingReport);

        FishingActivityEntity fishingActivity = createBasicFishingActivity(fishingTripEntity, result);

        fishingActivity.setTypeCode("DEPARTURE");
        fishingActivity.setTypeCodeListid("FLUX_FA_TYPE");
        //fishingActivity.setOccurence(); TODO get from a future extended efrFishingReport

        if (efrFishingReport.getTargetSpeciesCode() != null) {
            fishingActivity.setSpeciesTargetCode(efrFishingReport.getTargetSpeciesCode());
            fishingActivity.setSpeciesTargetCodeListId("FAO_SPECIES");
        }

        // TODO port of departure is mandatory

        // TODO catches on board on departure?

        // TODO gear on board on departure?
        // we only have gear on catches, no way of knowing what gear is actually on board

        // skip optional reason
        // skip optional fishery type

        result.setFishingActivities(Set.of(fishingActivity));

        return result;
    }

    private Optional<FaReportDocumentEntity> createCatchesDocument(FluxFaReportMessageEntity message, FishingTripEntity fishingTripEntity, FishingReport efrFishingReport) {
        FaReportDocumentEntity result = createBasicDocument(message, efrFishingReport);

        result.setTypeCode("DECLARATION");
        result.setTypeCodeListId("FLUX_FA_REPORT_TYPE");

        Set<FishingActivityEntity> fishingActivities = new HashSet<>();
        for (FishingCatch efrCatch : Utils.safeIterable(efrFishingReport.getFishingCatches())) {
            fishingActivities.add(createCatchFishingOperation(result, fishingTripEntity, efrFishingReport, efrCatch));
        }
        fishingActivities.remove(null);

        if (fishingActivities.isEmpty()) {
            // No fishing activities, no catches document
            return Optional.empty();
        }

        result.setFishingActivities(fishingActivities);

        return Optional.of(result);
    }

    // Chapter 7.1.5 in spec
    private FishingActivityEntity createCatchFishingOperation(FaReportDocumentEntity parent, FishingTripEntity fishingTripEntity, FishingReport efrFishingReport, FishingCatch efrCatch) {
        // Currently we map all catches to a "gear retrieval" fishing operation (it has a GEAR_RETRIEVAL sub-activity)
        // Also we make an assumption that no catch is left in the gear, it is all taken aboard (discarded catch is handled elsewhere, only LCS catch here)
        Instant occurrence = efrCatch.getClientCreatedAt();
        UserSpecifiedLocation gearLocation = null;
        if (efrCatch.getCatchGearSettings() != null) {
             gearLocation = efrCatch.getCatchGearSettings().getGearLocation();
        }
        // TODO mappingexception if we don't have a location?

        FishingActivityEntity result = createBasicFishingActivity(fishingTripEntity, parent);

        result.setTypeCode("FISHING_OPERATION");
        result.setTypeCodeListid("FLUX_FA_TYPE");
        result.setOccurence(occurrence); // TODO not actually mandatory since we have it on the sub-activity
        result.setCalculatedStartTime(occurrence); // TODO not sure if both are needed
        result.setSpeciesTargetCode(efrFishingReport.getTargetSpeciesCode()); // TODO can set, but is it completely redundant?
        result.setSpeciesTargetCodeListId("FAO_SPECIES");

        result.setFishingGears(createGearForCatchFishingOperation(efrCatch, result));

        result.setFaCatchs(createCatchesForCatchFishingOperation(efrCatch));

        // Chapter 7.1.5.1 in spec - Gear retrieval sub activity
        FishingActivityEntity subActivity = createBasicFishingActivity(fishingTripEntity, parent);
        subActivity.setTypeCode("GEAR_RETRIEVAL");
        subActivity.setTypeCodeListid("FLUX_FA_TYPE");
        subActivity.setOccurence(occurrence);
        if (gearLocation != null) {
            subActivity.setLatitude(latLongDmsToDd(gearLocation.getLatitude()));
            subActivity.setLongitude(latLongDmsToDd(gearLocation.getLongitude()));
            LocationEntity subActivityLocation = new LocationEntity();
            subActivityLocation.setTypeCode(LocationEnum.POSITION);
            subActivityLocation.setTypeCodeListId("FLUX_LOCATION_TYPE");
            subActivityLocation.setName(gearLocation.getName());
            subActivity.setLocations(Set.of(subActivityLocation));
        }
        // Based on the spec and looking at existing FLUX messages we can skip the rest in the subactivity
        result.setRelatedFishingActivity(subActivity);

        return result;
    }

    private Set<FaCatchEntity> createCatchesForCatchFishingOperation(FishingCatch efrCatch) {
        if (efrCatch.isEmptyCatch() || efrCatch.getSpecies() == null) {
            return null;
        }

        Set<FaCatchEntity> result = new HashSet<>();
        for (CatchSpecies species : efrCatch.getSpecies()) {
            if (hasFishSizeClass(species, "LSC")) {
                // Chapter 7.1.15.6 in spec
                FaCatchEntity fluxCatch = new FaCatchEntity();
                fluxCatch.setTypeCode("ONBOARD");
                fluxCatch.setTypeCodeListId("FA_CATCH_TYPE");
                fluxCatch.setSpeciesCode(species.getCode());
                fluxCatch.setSpeciesCodeListid("FAO_SPECIES");
                fluxCatch.setSizeDistributionClassCode("LSC"); // Chapter 7.1.15.17 in spec
                fluxCatch.setSizeDistributionClassCodeListId("FISH_SIZE_CLASS");
                setCatchQuantity(fluxCatch, species, "LSC");
                setCatchWeightInKg(fluxCatch, species, "LSC");
                // skip weighing means code, seems to be a BFT (blue finned tuna) thing only
                // skip size distribution category, seems to be a BFT thing only
                // skip location stuff, set in the sub activity
                // skip destination stuff, seems to be a BFT thing only
                // skip gear, that is set on the fishing activity
                // skip applied AAP processes, we don't have any support for that yet in the data coming from EFR
                result.add(fluxCatch);
            }
        }

        return result;
    }

    private Set<FishingGearEntity> createGearForCatchFishingOperation(FishingCatch efrCatch, FishingActivityEntity fishingActivity) {
        FishingGearEntity gear = new FishingGearEntity();
        gear.setFishingActivity(fishingActivity);
        gear.setTypeCode(efrCatch.getGearCode());
        gear.setTypeCodeListId("GEAR_TYPE");
        FishingGearRoleEntity gearRole = new FishingGearRoleEntity();
        gearRole.setFishingGear(gear);
        gearRole.setRoleCode("DEPLOYED");
        gearRole.setRoleCodeListId("FA_GEAR_ROLE");
        gear.setFishingGearRole(Set.of(gearRole));

        CatchGearSettings catchGearSettings = efrCatch.getCatchGearSettings();
        if (catchGearSettings != null) {
            Set<GearCharacteristicEntity> characteristics = new HashSet<>();
            if (catchGearSettings.getQuantity() != null) {
                // TODO we need more data from EFR to determine whether quantity is meters of net or number of gear
            }
            if (catchGearSettings.getMeshSizeInMillimeter() != null) {
                GearCharacteristicEntity characteristic = new GearCharacteristicEntity();
                characteristic.setTypeCode(GearCharacteristicConstants.GEAR_CHARAC_TYPE_CODE_ME); // ME = Mesh size
                characteristic.setTypeCodeListId("FA_GEAR_CHARACTERISTIC");
                characteristic.setValueMeasure(catchGearSettings.getMeshSizeInMillimeter().doubleValue());
                characteristic.setValueQuantityCode(UnitCodeEnum.MMT.getUnit());
                characteristics.add(characteristic);
            }
            if (!characteristics.isEmpty()) {
                gear.setGearCharacteristics(characteristics);
            }
        }
        return Set.of(gear);
    }


    private Optional<FaReportDocumentEntity> createDiscardsDocument(FluxFaReportMessageEntity message, FishingTripEntity fishingTripEntity, FishingReport efrFishingReport) {
        FaReportDocumentEntity result = createBasicDocument(message, efrFishingReport);

        result.setTypeCode("DECLARATION");
        result.setTypeCodeListId("FLUX_FA_REPORT_TYPE");

        Set<FishingActivityEntity> fishingActivities = new HashSet<>();

        for (FishingCatch efrCatch : Utils.safeIterable(efrFishingReport.getFishingCatches())) {
            fishingActivities.add(createDiscardFishingOperation(result, fishingTripEntity, efrFishingReport, efrCatch));
        }
        fishingActivities.remove(null); // likely that at least one of the EFR catches was not a discard

        if (fishingActivities.isEmpty()) {
            // no fishing activities = no document
            return Optional.empty();
        }

        result.setFishingActivities(fishingActivities);

        return Optional.of(result);
    }

    // Chapter 7.1.7 in spec
    private FishingActivityEntity createDiscardFishingOperation(FaReportDocumentEntity parent, FishingTripEntity fishingTripEntity, FishingReport efrFishingReport, FishingCatch efrCatch) {
        // Here we go through all catches and look for discards
        Instant occurrence = efrCatch.getClientCreatedAt();
        UserSpecifiedLocation gearLocation = null;
        if (efrCatch.getCatchGearSettings() != null) {
            gearLocation = efrCatch.getCatchGearSettings().getGearLocation();
        }
        // TODO mappingexception if we don't have a location?

        FishingActivityEntity result = createBasicFishingActivity(fishingTripEntity, parent);

        result.setTypeCode("DISCARD");
        result.setTypeCodeListid("FLUX_FA_TYPE");
        result.setOccurence(occurrence);
        result.setCalculatedStartTime(occurrence); // TODO not sure if both are needed
        result.setSpeciesTargetCode(efrFishingReport.getTargetSpeciesCode()); // TODO can set, but is it completely redundant?
        result.setSpeciesTargetCodeListId("FAO_SPECIES");

        // TODO man kan sätta result.setReasonCode(). Enligt spec är den min 0, men man kanske vill sätta den ändå:
        // "reason for discard". Dock är det ju då tyvärr på FIshingActivity-nivå, så då blir det en fiskeaktivitet
        // för varje BMS, DIS etc värde som finns i en catch->species. Det kan bli jävligt bökigt...

        Set<FaCatchEntity> discardCatches = new HashSet<>();
        for (CatchSpecies efrSpecies : Utils.safeIterable(efrCatch.getSpecies())) {
            discardCatches.add(createDiscardedFishingOperationCatch(efrSpecies, "BMS"));
            discardCatches.add(createDiscardedFishingOperationCatch(efrSpecies, "DIM"));
            discardCatches.add(createDiscardedFishingOperationCatch(efrSpecies, "DIS"));
            discardCatches.add(createDiscardedFishingOperationCatch(efrSpecies, "ROV"));
        }
        discardCatches.remove(null); // very likely that at least one of the discards are not present, remove that null element

        if (discardCatches.isEmpty()) {
            // no discard catches = no discard fishing activity
            return null;
        }

        result.setFaCatchs(discardCatches);

        if (gearLocation != null) {
            result.setLatitude(latLongDmsToDd(gearLocation.getLatitude()));
            result.setLongitude(latLongDmsToDd(gearLocation.getLongitude()));

            LocationEntity location = new LocationEntity();
            location.setName(gearLocation.getName());
            location.setNameLanguageId("SWE"); // TODO not hardcoded
            location.setTypeCode(LocationEnum.POSITION);
            location.setTypeCodeListId("FLUX_LOCATION_TYPE");
            result.setLocations(Set.of(location));
        }

        // skip FLUX characteristics

        return result;
    }

    private FaCatchEntity createDiscardedFishingOperationCatch(CatchSpecies efrCatchSpecies, String fishSizeClass) {
        if (!hasFishSizeClass(efrCatchSpecies, fishSizeClass)) {
            return null;
        }

        FaCatchEntity result = new FaCatchEntity();
        result.setTypeCode(fishSizeClass.equals("DIM") ? "DEMINIMIS" : "DISCARD");
        result.setTypeCodeListId("FA_CATCH_TYPE");
        result.setSpeciesCode(efrCatchSpecies.getCode());
        result.setSpeciesCodeListid("FAO_SPECIES");
        result.setSizeDistributionClassCode(fishSizeClass);
        result.setSizeDistributionClassCodeListId("FISH_SIZE_CLASS");
        setCatchQuantity(result, efrCatchSpecies, fishSizeClass);
        setCatchWeightInKg(result, efrCatchSpecies, fishSizeClass);

        // location set in parent fishing activity
        // gear not set at all for discards

        return result;
    }

    // Chapter 7.1.10 in spec
    private FaReportDocumentEntity createPriorNotificationOfArrivalDocument(FluxFaReportMessageEntity message, FishingTripEntity fishingTripEntity, FishingReport efrFishingReport) {
        FaReportDocumentEntity result = createBasicDocument(message, efrFishingReport);

        FishingActivityEntity fishingActivity = createBasicFishingActivity(fishingTripEntity, result);

        fishingActivity.setTypeCode("ARRIVAL"); // According to spec, this type code is the same for arrival and prior notification of arrival
        fishingActivity.setTypeCodeListid("FLUX_FA_TYPE");
        fishingActivity.setOccurence(efrFishingReport.getPriorNotification().getEstimatedLandingTime());
        fishingActivity.setReasonCode("LAN"); // LAN = Landing
        fishingActivity.setReasonCodeListId("FA_REASON_ARRIVAL");

        LocationEntity landingLocation = createLandingLocation(efrFishingReport);
        if (landingLocation != null) {
            fishingActivity.setLocations(Set.of(landingLocation));

            UserSpecifiedLocation userSpecifiedLocation = getUserSpecifiedLocation(efrFishingReport);
            if (userSpecifiedLocation != null) {
                fishingActivity.setLatitude(latLongDmsToDd(userSpecifiedLocation.getLatitude()));
                fishingActivity.setLongitude(latLongDmsToDd(userSpecifiedLocation.getLongitude()));
            }
        }
        // TODO else what? min 1 in spec

        fishingActivity.setFaCatchs(getCatchesForPriorNotificationOfArrival(efrFishingReport));

        result.setFishingActivities(Set.of(fishingActivity));

        return result;
    }

    private Set<FaCatchEntity> getCatchesForPriorNotificationOfArrival(FishingReport efrFishingReport) {
        if (efrFishingReport.getPriorNotification() == null || efrFishingReport.getPriorNotification().getEstimatedCatches() == null) {
            return null;
        }

        Set<FaCatchEntity> result = new HashSet<>();
        for (PriorNotificationEstimatedCatch efrCatch : efrFishingReport.getPriorNotification().getEstimatedCatches()) {
            FaCatchEntity fluxCatch = new FaCatchEntity();
            fluxCatch.setTypeCode("ONBOARD");
            fluxCatch.setTypeCodeListId("FA_CATCH_TYPE");
            fluxCatch.setSpeciesCode(efrCatch.getCode()); // TODO double check that this is in fact in FAO format
            fluxCatch.setSpeciesCodeListid("FAO_SPECIES");

            fluxCatch.setUnitQuantity(efrCatch.getQuantity() != null ? efrCatch.getQuantity().doubleValue() : null);
            fluxCatch.setUnitQuantityCode("C62");
            // fluxCatch.setCalculatedUnitQuantity(); TODO set this as well?

            BigDecimal lscWeightInKg = efrCatch.getWeightInKilos();
            if (lscWeightInKg != null) {
                // Spec says to round to two decimals
                double roundedLscWeight = lscWeightInKg.setScale(2, RoundingMode.HALF_UP).doubleValue();
                fluxCatch.setWeightMeasure(roundedLscWeight);
                fluxCatch.setWeightMeasureUnitCode("KGM");
                //fluxCatch.setCalculatedWeightMeasure(); TODO set this as well?
                //fluxCatch.setWeighingMeansCode("The code specifying the means of weighing for this FA catch. "); TODO set?
                //fluxCatch.setWeighingMeansCodeListId("WEIGHT_MEANS");
            }

            // chapter 7.1.15.17 in spec
            // TODO keep? We set these in landing but should we also have them for prior notification?
            fluxCatch.setSizeDistributionClassCode("LSC"); // spec says "Use value "LSC" for legally sized fish. Use value "BMS" for fish below minimum conservation reference size"
            fluxCatch.setSizeDistributionClassCodeListId("FISH_SIZE_CLASS");
            //fluxCatch.setSizeDistributionCategoryCode("TODO spec "says A code specifying the size distribution of the AAP product"; // TODO?
            //fluxCatch.setSizeDistributionCategoryCodeListId("FA_BFT_SIZE_CATEGORY");

            // Skip locations, data not available
            // skip related fishing trip
            // skip gear, data not available
            // skip AAP, data not available

            result.add(fluxCatch);
        }

        return result;
    }

    // chapter 7.1.12 in spec
    private FaReportDocumentEntity createLandingDocument(FluxFaReportMessageEntity message, FishingTripEntity fishingTripEntity, FishingReport efrFishingReport) {
        FaReportDocumentEntity result = createBasicDocument(message, efrFishingReport);

        FishingActivityEntity fishingActivity = createBasicFishingActivity(fishingTripEntity, result);

        fishingActivity.setTypeCode("LANDING");
        fishingActivity.setTypeCodeListid("FLUX_FA_TYPE");

        // Spec:en nämner Identification men den är optional. Vet inte ens vilket fält på entiteten som motsvarar det

        // TODO chansar på att detta är Specified Delimited_Period men vad fan ska de sättas till?
        //fishingActivity.setCalculatedStartTime();
        //fishingActivity.setCalculatedEndTime();

        // SpecifiedFishing_Trip ska sättas i createBasicFishingActivity()

        setPositionFromPriorNotification(fishingActivity, efrFishingReport);
        LocationEntity landingLocation = createLandingLocation(efrFishingReport);
        if (landingLocation != null) {
            fishingActivity.setLocations(Set.of(landingLocation));

            UserSpecifiedLocation userSpecifiedLocation = getUserSpecifiedLocation(efrFishingReport);
            if (userSpecifiedLocation != null) {
                fishingActivity.setLatitude(latLongDmsToDd(userSpecifiedLocation.getLatitude()));
                fishingActivity.setLongitude(latLongDmsToDd(userSpecifiedLocation.getLongitude()));
            }
        }
        // TODO else what? min 1 in spec

        fishingActivity.setFaCatchs(getUnloadedCatchesForLanding(fishingActivity, efrFishingReport));

        // Skip specified FLUX characteristics

        result.setFishingActivities(Set.of(fishingActivity));

        return result;
    }

    private void setPositionFromPriorNotification(FishingActivityEntity fishingActivity, FishingReport efrFishingReport) {
        if (efrFishingReport.getPriorNotification() == null ||
                efrFishingReport.getPriorNotification().getArrivalLocation() == null ||
                efrFishingReport.getPriorNotification().getArrivalLocation().getUserSpecifiedLocation() == null) {
            return;
        }

        UserSpecifiedLocation userSpecifiedLocation = efrFishingReport.getPriorNotification().getArrivalLocation().getUserSpecifiedLocation();
        fishingActivity.setLatitude(latLongDmsToDd(userSpecifiedLocation.getLatitude()));
        fishingActivity.setLongitude(latLongDmsToDd(userSpecifiedLocation.getLongitude()));
    }

    // Chapter 7.1.15.10 in spec
    private LocationEntity createLandingLocation(FishingReport efrFishingReport) {
        if (efrFishingReport.getPriorNotification() == null) {
            return null;
        }

        if (efrFishingReport.getPriorNotification().getArrivalLocation() == null) {
            return null;
        }

        ArrivalLocation arrivalLocation = efrFishingReport.getPriorNotification().getArrivalLocation();

        LocationEntity result = new LocationEntity();
        result.setTypeCodeListId("FLUX_LOCATION_TYPE");

        // Skip "Country": Min 0 in specification
        // Skip "Identification": Min 0 in specification
        // Skip "Regional Fisheries Management Organisation": Min 0 in specification
        // Skip "Physical Structured Address": Min 0 in specification and cannot be set on model
        // Skip "Applicable FLUX Characteristic": Min 0 in specification and cannot be set on model

        if (arrivalLocation.getPortCode() != null) {
            result.setTypeCode(LocationEnum.LOCATION);
            result.setFluxLocationIdentifier(arrivalLocation.getPortCode()); // TODO correct?
            result.setFluxLocationIdentifierSchemeId("LOCATION");
        } else if (arrivalLocation.getUserSpecifiedLocation() != null) {
            UserSpecifiedLocation userSpecifiedLocation = arrivalLocation.getUserSpecifiedLocation();

            result.setTypeCode(LocationEnum.POSITION);

            if (userSpecifiedLocation.getName() != null) {
                result.setName(userSpecifiedLocation.getName());
                result.setNameLanguageId("SWE");
            }

            // Lat and long set in the parent fishing activity

        } else {
            // for some reason, arrival location without either port or user specified location
            return null;
        }

        return result;
    }

    /**
     * Chapter 7.1.15.6 in spec.
     * These catches are taking from the "root" of the EFR fishing report, not the prior notification sub-part.
     * Also, we assume that all catches in the fishing report are being unloaded.
     */
    private Set<FaCatchEntity> getUnloadedCatchesForLanding(FishingActivityEntity fishingActivityEntity, FishingReport efrFishingReport) {
        List<FishingCatch> catches = efrFishingReport.getFishingCatches();
        if (catches == null || catches.isEmpty()) {
            return null; // TODO or empty set?
        }

        Set<FaCatchEntity> result = new HashSet<>();

        for (FishingCatch efrCatch : catches) {
            if (efrCatch.isEmptyCatch() || efrCatch.getSpecies() == null) {
                // empty catches are not part of unloaded catches
                continue;
            }

            for (CatchSpecies species : efrCatch.getSpecies()) {
                FaCatchEntity fluxCatch = new FaCatchEntity();
                fluxCatch.setTypeCode("UNLOADED"); // We assume that all catches in the fishing report are unloaded
                fluxCatch.setTypeCodeListId("FA_CATCH_TYPE");
                fluxCatch.setSpeciesCode(species.getCode()); // TODO double check that this is in fact in FAO format
                fluxCatch.setSpeciesCodeListid("FAO_SPECIES");

                setCatchQuantity(fluxCatch, species, "LCS");
                setCatchWeightInKg(fluxCatch, species, "LCS");

                // Skip locations, lat and long. Spec says min 0, and "Mandatory if location not specified in the FishingActivity entity[...]", which we do

                // chapter 7.1.15.17 in spec
                fluxCatch.setSizeDistributionClassCode("LSC"); // spec says "Use value "LSC" for legally sized fish. Use value "BMS" for fish below minimum conservation reference size"
                fluxCatch.setSizeDistributionClassCodeListId("FISH_SIZE_CLASS");
                //fluxCatch.setSizeDistributionCategoryCode("TODO spec "says A code specifying the size distribution of the AAP product"; // TODO?
                //fluxCatch.setSizeDistributionCategoryCodeListId("FA_BFT_SIZE_CATEGORY");

                // skip related fishing trip

                setGearDataOnFluxCatch(fluxCatch, efrCatch, fishingActivityEntity);

                // TODO skip ResultAAP_Product for now, since we have no processed catches yet: "Mandatory when declaring processed catches at landing or transhipment" says spec

                result.add(fluxCatch);
            }
        }

        return result;
    }

    // chapter 7.1.15.14
    private void setGearDataOnFluxCatch(FaCatchEntity fluxCatch, FishingCatch efrCatch, FishingActivityEntity fishingActivityEntity) {
        if (efrCatch.getGearCode() == null) {
            return;
        }

        fluxCatch.setGearTypeCode(efrCatch.getGearCode()); // TODO why this AND the following object?

        FishingGearEntity gearEntity = new FishingGearEntity();
        gearEntity.setFaCatch(fluxCatch);
        gearEntity.setFishingActivity(fishingActivityEntity);

        gearEntity.setTypeCode(efrCatch.getGearCode());
        gearEntity.setTypeCodeListId("GEAR_TYPE");


        //gearEntity.setFishingGearRole(); Skip. Min 0 in spec, and has specific values to use in fishing operations, departure and arrival

        // Chapter 7.1.15.15
        if (efrCatch.getCatchGearSettings() != null) {
            CatchGearSettings catchGearSettings = efrCatch.getCatchGearSettings();
            Set<GearCharacteristicEntity> gearCharacteristics = new HashSet<>();

            if (catchGearSettings.getMeshSizeInMillimeter() != null) {
                GearCharacteristicEntity meshSize = new GearCharacteristicEntity();
                meshSize.setTypeCode(GearCharacteristicConstants.GEAR_CHARAC_TYPE_CODE_ME); // ME = Mesh size
                meshSize.setTypeCodeListId("FA_GEAR_CHARACTERISTIC");
                meshSize.setValueMeasure(catchGearSettings.getMeshSizeInMillimeter().doubleValue());
                meshSize.setValueMeasureUnitCode("MMT");
                gearCharacteristics.add(meshSize);
            }
            if (catchGearSettings.getQuantity() != null) {
                GearCharacteristicEntity quantity = new GearCharacteristicEntity();
                quantity.setTypeCode(GearCharacteristicConstants.GEAR_CHARAC_TYPE_CODE_QG); // QG = Quantity of gear on board
                quantity.setTypeCodeListId("FA_GEAR_CHARACTERISTIC");
                quantity.setValueQuantity(catchGearSettings.getQuantity().doubleValue());
                quantity.setValueQuantityCode(GearCharacteristicConstants.GEAR_CHARAC_Q_CODE_C62);
                gearCharacteristics.add(quantity);
            }

            if (!gearCharacteristics.isEmpty()) {
                gearEntity.setGearCharacteristics(gearCharacteristics);
            }
        }

        fluxCatch.setFishingGears(Set.of(gearEntity));
    }

    private static boolean hasFishSizeClass(CatchSpecies species, String fishSizeClass) {
        switch (fishSizeClass) {
            case "BMS":
                return (species.getBmsQuantity() != null && species.getBmsQuantity() > 0) ||
                       (species.getBmsWeightInKg() != null && species.getBmsWeightInKg().compareTo(ZERO_BIG_DECIMAL) > 0);
            case "DIM":
                return (species.getDimQuantity() != null && species.getDimQuantity() > 0) ||
                       (species.getDimWeightInKg() != null && species.getDimWeightInKg().compareTo(ZERO_BIG_DECIMAL) > 0);
            case "DIS":
                return (species.getDisQuantity() != null && species.getDisQuantity() > 0) ||
                       (species.getDisWeightInKg() != null && species.getDisWeightInKg().compareTo(ZERO_BIG_DECIMAL) > 0);
            case "LSC":
                return (species.getLscQuantity() != null && species.getLscQuantity() > 0) ||
                       (species.getLscWeightInKg() != null && species.getLscWeightInKg().compareTo(ZERO_BIG_DECIMAL) > 0);
            case "ROV":
                return (species.getRovQuantity() != null && species.getRovQuantity() > 0) ||
                       (species.getRovWeightInKg() != null && species.getRovWeightInKg().compareTo(ZERO_BIG_DECIMAL) > 0);
            default:
                return false;
        }
    }

    private static void setCatchQuantity(FaCatchEntity fluxCatch, CatchSpecies species, String fishSizeClass) {
        Integer efrQuantity;
        switch (fishSizeClass) {
            case "BMS":
                efrQuantity = species.getBmsQuantity();
                break;
            case "DIM":
                efrQuantity = species.getDimQuantity();
                break;
            case "DIS":
                efrQuantity = species.getDisQuantity();
                break;
            case "LSC":
                efrQuantity = species.getLscQuantity();
                break;
            case "ROV":
                efrQuantity = species.getRovQuantity();
                break;
            default:
                return;
        }

        if (efrQuantity != null) {
            fluxCatch.setUnitQuantity(efrQuantity.doubleValue());
            fluxCatch.setUnitQuantityCode("C62");
            // fluxCatch.setCalculatedUnitQuantity(); TODO set this as well?
        }
    }

    private static void setCatchWeightInKg(FaCatchEntity fluxCatch, CatchSpecies species, String fishSizeClass) {
        BigDecimal efrWeight;

        switch (fishSizeClass) {
            case "BMS":
                efrWeight = species.getBmsWeightInKg();
                break;
            case "DIM":
                efrWeight = species.getDimWeightInKg();
                break;
            case "DIS":
                efrWeight = species.getDisWeightInKg();
                break;
            case "LSC":
                efrWeight = species.getLscWeightInKg();
                break;
            case "ROV":
                efrWeight = species.getRovWeightInKg();
                break;
            default:
                return;
        }

        if (efrWeight != null) {
            double roundedWeight = efrWeight
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            fluxCatch.setWeightMeasure(roundedWeight);
            fluxCatch.setWeightMeasureUnitCode("KGM");
            //fluxCatch.setCalculatedWeightMeasure(); TODO set this as well?

        }
    }

    /**
     * @param latOrLong Latitude or longitude in degrees-minutes-seconds format
     * @return The same but in decimal degree format
     */
    private static Double latLongDmsToDd(String latOrLong) {
        if (latOrLong == null) {
            return null;
        }

        String[] dms = latOrLong.split(" ");
        double degrees = Double.parseDouble(dms[0]);
        double minutes = Double.parseDouble(dms[1]);
        double seconds = Double.parseDouble(dms[2]);
        String direction = dms.length == 4 ? dms[3] : null;

        double signum = 1.0;
        if (degrees < 0.0) {
            signum = -1.0;
        } else if (direction != null) {
            if (direction.equals("S") || direction.equals("W")) {
                signum = -1.0;
            }
        }

        double convertedDegrees = signum * (Math.abs(degrees) + (minutes / 60.0) + (seconds / 3600.0));

        // round to four decimals
        return BigDecimal.valueOf(convertedDegrees)
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private static UserSpecifiedLocation getUserSpecifiedLocation(FishingReport efrFishingReport) {
        if (efrFishingReport.getPriorNotification() != null &&
            efrFishingReport.getPriorNotification().getArrivalLocation() != null) {
            return efrFishingReport.getPriorNotification().getArrivalLocation().getUserSpecifiedLocation();
        }

        return null;
    }
}
