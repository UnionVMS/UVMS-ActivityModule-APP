package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.TransactionalTests;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend.ArrivalLocation;
import eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend.CatchGearSettings;
import eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend.CatchSpecies;
import eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend.FishingCatch;
import eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend.FishingReport;
import eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend.PriorNotificationDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend.PriorNotificationEstimatedCatch;
import eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend.UserSpecifiedLocation;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ModelMapperException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * This test also covers FluxToEfrMapper since we map the fishing reports to FLUX
 * and then back again.
 */
@RunWith(Arquillian.class)
public class EfrToFluxMapperIntegrationTest extends TransactionalTests {

    @Inject
    private EfrToFluxMapper efrToFluxMapper;

    @Inject
    private FluxToEfrMapper fluxToEfrMapper;

    FishingReport originalDefault;
    FishingReport mappedDefault; // The fishing report that has been mapped back from FLUX

    @Before
    public void setUp() {
        originalDefault = createFishingReport();
        FluxFaReportMessageEntity fluxVersion = efrToFluxMapper.efrFishingReportToFluxMessageEntity(originalDefault);
        try {
            mappedDefault = fluxToEfrMapper.fluxMessageEntityToEfrFishingReport(fluxVersion);
        } catch (ModelMapperException e) {
            fail("Failure in test setup, failed to map from FLUX to EFR");
        }
    }

    @Test
    public void fishingReportId() {
        assertEquals(originalDefault.getFishingReportId(), mappedDefault.getFishingReportId());
    }

    @Test
    public void targetSpecies() {
        assertEquals(originalDefault.getTargetSpeciesCode(), mappedDefault.getTargetSpeciesCode());
    }

    @Test
    public void shipCfr() {
        assertEquals(originalDefault.getShipCfr(), mappedDefault.getShipCfr());
    }

    @Test
    public void priorNotification_estimatedLandingTime() {
        PriorNotificationDto originalPriorNotification = originalDefault.getPriorNotification();
        PriorNotificationDto mappedPriorNotification = mappedDefault.getPriorNotification();

        assertEquals(originalPriorNotification.getEstimatedLandingTime(), mappedPriorNotification.getEstimatedLandingTime());
    }

    @Test
    public void priorNotification_userSpecifiedArrivalLocation() {
        ArrivalLocation originalArrivalLocation = originalDefault.getPriorNotification().getArrivalLocation();
        UserSpecifiedLocation originalUserSpecifiedLocation = originalArrivalLocation.getUserSpecifiedLocation();
        ArrivalLocation mappedArrivalLocation = mappedDefault.getPriorNotification().getArrivalLocation();
        UserSpecifiedLocation mappedUserSpecifiedLocation = mappedArrivalLocation.getUserSpecifiedLocation();

        assertNull(mappedArrivalLocation.getPortCode());
        assertEquals(originalUserSpecifiedLocation.getName(), mappedUserSpecifiedLocation.getName());
        assertEquals(originalUserSpecifiedLocation.getLatitude(), mappedUserSpecifiedLocation.getLatitude());
        assertEquals(originalUserSpecifiedLocation.getLongitude(), mappedUserSpecifiedLocation.getLongitude());
    }

    @Test
    public void priorNotification_portArrivalLocation() throws ModelMapperException {
        // Given
        FishingReport minimal = createMinimalFishingReport();

        // When
        FluxFaReportMessageEntity fluxVersion = efrToFluxMapper.efrFishingReportToFluxMessageEntity(minimal);
        FishingReport mappedMinimal = fluxToEfrMapper.fluxMessageEntityToEfrFishingReport(fluxVersion);

        // Then
        ArrivalLocation originalArrivalLocation = minimal.getPriorNotification().getArrivalLocation();
        ArrivalLocation mappedArrivalLocation = mappedMinimal.getPriorNotification().getArrivalLocation();

        assertNull(mappedArrivalLocation.getUserSpecifiedLocation());
        assertEquals(originalArrivalLocation.getPortCode(), mappedArrivalLocation.getPortCode());
    }

    @Test
    public void priorNotification_catches() {
        Collection<PriorNotificationEstimatedCatch> originalCatches = originalDefault.getPriorNotification().getEstimatedCatches();
        Collection<PriorNotificationEstimatedCatch> mappedCatches = mappedDefault.getPriorNotification().getEstimatedCatches();

        assertEquals(originalCatches.size(), mappedCatches.size());
        PriorNotificationEstimatedCatch asfCatch = mappedCatches.stream().filter(c -> c.getCode().equals("ASF")).findAny().get();
        PriorNotificationEstimatedCatch bobCatch = mappedCatches.stream().filter(c -> c.getCode().equals("BOB")).findAny().get();

        assertEquals(11.11, asfCatch.getWeightInKilos().doubleValue(), 0.00001);
        assertEquals(4, bobCatch.getQuantity().intValue());
    }

    @Test
    public void fishingCatches() {
        List<FishingCatch> originalCatches = originalDefault.getFishingCatches();
        List<FishingCatch> mappedCatches = mappedDefault.getFishingCatches();

        assertEquals(originalCatches.size(), mappedCatches.size());

        // As of writing we have to use the gear code to differentiate between the catches, since we have no persistent UUID or timestamps
        // This works only because this test data has a unique gear code for each catch. This however is unrealistic.
        compareCatches(originalCatches.get(0),
                mappedCatches.stream()
                        .filter(c -> c.getGearCode().equals(originalCatches.get(0).getGearCode()))
                        .findAny()
                        .get());

        compareCatches(originalCatches.get(1),
                mappedCatches.stream()
                        .filter(c -> c.getGearCode().equals(originalCatches.get(1).getGearCode()))
                        .findAny()
                        .get());

        compareCatches(originalCatches.get(2),
                mappedCatches.stream()
                        .filter(c -> c.getGearCode().equals(originalCatches.get(2).getGearCode()))
                        .findAny()
                        .get());
    }

    private void compareCatches(FishingCatch originalCatch, FishingCatch mappedCatch) {
        //assertEquals(originalCatch.getId(), mappedCatch.getId()); I wish!
        assertEquals(originalCatch.getGearCode(), mappedCatch.getGearCode());

        CatchGearSettings originalGear = originalCatch.getCatchGearSettings();
        CatchGearSettings mappedGear = mappedCatch.getCatchGearSettings();

        assertEquals(originalGear.getMeshSizeInMillimeter(), mappedGear.getMeshSizeInMillimeter());
        //assertEquals(originalGear.getQuantity(), mappedGear.getQuantity()); TODO as of now this is not set because we don't know if it is quantity of gear of meters of net in mapping
        assertEquals(originalGear.getDeployedPeriodStartDate(), mappedGear.getDeployedPeriodStartDate());
        assertEquals(originalGear.getDeployedPeriodEndDate(), mappedGear.getDeployedPeriodEndDate());

        UserSpecifiedLocation originalGearLocation = originalGear.getGearLocation();
        UserSpecifiedLocation mappedGearLocation = mappedGear.getGearLocation();
        assertEquals(originalGearLocation.getName(), mappedGearLocation.getName());
        assertEquals(originalGearLocation.getLatitude(), mappedGearLocation.getLatitude());
        assertEquals(originalGearLocation.getLongitude(), mappedGearLocation.getLongitude());

        List<CatchSpecies> originalSpecies = originalCatch.getSpecies();
        List<CatchSpecies> mappedSpecies = mappedCatch.getSpecies();

        // empty catch?
        if (originalSpecies == null) {
            assertNull(mappedSpecies);
            return;
        }

        assertEquals(originalSpecies.size(), mappedSpecies.size());

        for (CatchSpecies originalSpecy : originalSpecies) { // sic
            CatchSpecies mappedSpecy = mappedSpecies.stream()
                    .filter(s -> s.getCode().equals(originalSpecy.getCode()))
                    .findAny()
                    .get();

            assertEquals(originalSpecy.getCode(), mappedSpecy.getCode());
            // assertEquals(originalSpecy.getVariant(), mappedSpecy.getVariant()); I wish
            assertEquals(originalSpecy.getBmsQuantity(), mappedSpecy.getBmsQuantity());
            assertEquals(originalSpecy.getBmsWeightInKg(), mappedSpecy.getBmsWeightInKg());

            assertEquals(originalSpecy.getDimQuantity(), mappedSpecy.getDimQuantity());
            assertEquals(originalSpecy.getDimWeightInKg(), mappedSpecy.getDimWeightInKg());

            assertEquals(originalSpecy.getDisQuantity(), mappedSpecy.getDisQuantity());
            assertEquals(originalSpecy.getDisWeightInKg(), mappedSpecy.getDisWeightInKg());

            assertEquals(originalSpecy.getLscQuantity(), mappedSpecy.getLscQuantity());
            assertEquals(originalSpecy.getLscWeightInKg(), mappedSpecy.getLscWeightInKg());
        }
    }

    private static FishingReport createFishingReport() {
        FishingReport result = new FishingReport();

        result.setFishingReportId(UUID.randomUUID());
        result.setClientCreatedAt(xMinutesAgo(60));
        result.setClientUpdatedAt(xMinutesAgo(55));
        result.setServerCreatedAt(xMinutesAgo(60));
        result.setServerUpdatedAt(xMinutesAgo(55));

        ArrayList<FishingCatch> catches = new ArrayList<>();
        result.setFishingCatches(catches);

        FishingCatch catch0 = new FishingCatch();
        catch0.setId(UUID.randomUUID());
        catch0.setEmptyCatch(false);
        catch0.setGearCode("my_gear_0");
        catch0.setClientCreatedAt(xMinutesAgo(40));
        catch0.setClientUpdatedAt(xMinutesAgo(40));
        catch0.setServerCreatedAt(xMinutesAgo(40));
        catch0.setServerUpdatedAt(xMinutesAgo(40));
        CatchGearSettings catchGearSettings0 = new CatchGearSettings();
        catchGearSettings0.setMeshSizeInMillimeter(5);
        catchGearSettings0.setGearLocation(createUserSpecifiedLocation("gear_loc_0", "1 2 3", "22 0 1"));
        catch0.setCatchGearSettings(catchGearSettings0);
        ArrayList<CatchSpecies> catch0species = new ArrayList<>();
        catch0.setSpecies(catch0species);
        catches.add(catch0);

        CatchSpecies catch0Species0 = new CatchSpecies();
        catch0Species0.setCode("SAL");
        catch0Species0.setVariant("DEFAULT");
        catch0Species0.setLscQuantity(10);
        catch0species.add(catch0Species0);

        FishingCatch catch1 = new FishingCatch();
        catch1.setId(UUID.randomUUID());
        catch1.setEmptyCatch(true);
        catch1.setGearCode("my_gear_1");
        catch1.setClientCreatedAt(xMinutesAgo(30));
        catch1.setClientUpdatedAt(xMinutesAgo(30));
        catch1.setServerCreatedAt(xMinutesAgo(30));
        catch1.setServerUpdatedAt(xMinutesAgo(30));
        CatchGearSettings catchGearSettings1 = new CatchGearSettings();
        catchGearSettings1.setMeshSizeInMillimeter(10);
        catchGearSettings1.setGearLocation(createUserSpecifiedLocation("gear_loc_1", "-1 2 3", "-22 0 1"));
        catch1.setCatchGearSettings(catchGearSettings1);
        catches.add(catch1);

        FishingCatch catch2 = new FishingCatch();
        catch2.setId(UUID.randomUUID());
        catch2.setEmptyCatch(false);
        catch2.setGearCode("my_gear_2");
        catch2.setClientCreatedAt(xMinutesAgo(20));
        catch2.setClientUpdatedAt(xMinutesAgo(20));
        catch2.setServerCreatedAt(xMinutesAgo(20));
        catch2.setServerUpdatedAt(xMinutesAgo(20));
        CatchGearSettings catchGearSettings2 = new CatchGearSettings();
        catchGearSettings2.setQuantity(3);
        catchGearSettings2.setGearLocation(createUserSpecifiedLocation("gear_loc_2", "80 59 59", "0 59 59"));
        catch2.setCatchGearSettings(catchGearSettings2);
        ArrayList<CatchSpecies> catch2species = new ArrayList<>();
        catch2.setSpecies(catch2species);
        catches.add(catch2);

        CatchSpecies cath2Species0 = new CatchSpecies();
        cath2Species0.setCode("DIS");
        cath2Species0.setVariant("DEFAULT");
        cath2Species0.setDisQuantity(20_000);
        catch2species.add(cath2Species0);

        CatchSpecies cath2Species1 = new CatchSpecies();
        cath2Species1.setCode("BMS");
        cath2Species1.setVariant("DEFAULT");
        cath2Species1.setBmsQuantity(30_000);
        catch2species.add(cath2Species1);

        CatchSpecies cath2Species2 = new CatchSpecies();
        cath2Species2.setCode("ROV");
        cath2Species2.setVariant("DEFAULT");
        cath2Species2.setRovWeightInKg(10.12);
        catch2species.add(cath2Species2);

        PriorNotificationDto priorNotification = new PriorNotificationDto();
        priorNotification.setEstimatedLandingTime(Instant.now());
        ArrayList<PriorNotificationEstimatedCatch> estimatedCatches = new ArrayList<>();
        priorNotification.setEstimatedCatches(estimatedCatches);

        PriorNotificationEstimatedCatch estimatedCatch0 = new PriorNotificationEstimatedCatch();
        estimatedCatch0.setCode("BOB");
        estimatedCatch0.setQuantity(4);
        estimatedCatches.add(estimatedCatch0);

        PriorNotificationEstimatedCatch estimatedCatch1 = new PriorNotificationEstimatedCatch();
        estimatedCatch1.setCode("ASF");
        estimatedCatch1.setWeightInKilos(11.11);
        estimatedCatches.add(estimatedCatch1);

        ArrivalLocation arrivalLocation = new ArrivalLocation();
        //arrivalLocation.setPortCode("hemmabäst");
        priorNotification.setArrivalLocation(arrivalLocation);
        arrivalLocation.setUserSpecifiedLocation(createUserSpecifiedLocation("en_punkt", "11 22 33", "-123 0 0"));

        result.setPriorNotification(priorNotification);
        result.setTargetSpeciesCode("SAL");
        result.setShipCfr("myship");

        return result;
    }

    private static FishingReport createMinimalFishingReport() {
        FishingReport result = new FishingReport();

        result.setFishingReportId(UUID.randomUUID());
        result.setClientCreatedAt(xMinutesAgo(60));
        result.setClientUpdatedAt(xMinutesAgo(55));
        result.setServerCreatedAt(xMinutesAgo(60));
        result.setServerUpdatedAt(xMinutesAgo(55));

        PriorNotificationDto priorNotification = new PriorNotificationDto();
        priorNotification.setEstimatedLandingTime(Instant.now());

        ArrivalLocation arrivalLocation = new ArrivalLocation();
        arrivalLocation.setPortCode("hemmabäst");
        priorNotification.setArrivalLocation(arrivalLocation);

        result.setPriorNotification(priorNotification);
        result.setTargetSpeciesCode("XXX");
        result.setShipCfr("FuryOfTheEmperor");

        return result;
    }

    private static UserSpecifiedLocation createUserSpecifiedLocation(String name, String lat, String lon) {
        UserSpecifiedLocation result = new UserSpecifiedLocation();
        result.setName(name);
        result.setLatitude(lat);
        result.setLongitude(lon);
        return result;
    }

    private static Instant xMinutesAgo(int x) {
        return Instant.now().minus(x, ChronoUnit.MINUTES);
    }
}