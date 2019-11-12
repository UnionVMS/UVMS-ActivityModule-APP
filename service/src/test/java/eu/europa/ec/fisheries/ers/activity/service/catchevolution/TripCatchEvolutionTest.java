package eu.europa.ec.fisheries.ers.activity.service.catchevolution;

import eu.europa.ec.fisheries.ers.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.activity.fa.utils.FaReportDocumentType;
import eu.europa.ec.fisheries.ers.activity.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.ers.activity.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.ers.activity.service.dto.fishingtrip.CatchEvolutionProgressDTO;
import eu.europa.ec.fisheries.ers.activity.service.dto.fishingtrip.SpeciesQuantityDTO;
import eu.europa.ec.fisheries.ers.activity.service.facatch.evolution.CatchEvolutionProgressHandler;
import eu.europa.ec.fisheries.ers.activity.service.facatch.evolution.FishingActivityCalculatedDateComparator;
import eu.europa.ec.fisheries.ers.activity.service.util.ActivityDataUtil;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FaCatchTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TripCatchEvolutionTest extends CatchEvolutionProgressHandler {

    private Instant parseToUTCDate(String value) {
        LocalDateTime localDateTime = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return localDateTime.toInstant(ZoneOffset.UTC);
    }
    
    @Override
    public CatchEvolutionProgressDTO prepareCatchEvolutionProgressDTO(FishingActivityEntity fishingActivity, Map<String, Double> speciesCumulatedWeight) {
        return null;
    }

    @Test
    public void testFishingActivityCalculatedDateComparator() {
        FishingActivityEntity fishingActivityEntity1 = new FishingActivityEntity();
        fishingActivityEntity1.setCalculatedStartTime(Instant.ofEpochMilli(1000002));
        FishingActivityEntity fishingActivityEntity2 = new FishingActivityEntity();
        fishingActivityEntity2.setCalculatedStartTime(Instant.ofEpochMilli(1000000));
        FishingActivityEntity fishingActivityEntity3 = new FishingActivityEntity();
        fishingActivityEntity3.setCalculatedStartTime(Instant.ofEpochMilli(1000001));
        FishingActivityEntity fishingActivityEntity4 = new FishingActivityEntity();
        fishingActivityEntity4.setCalculatedStartTime(null);

        List<FishingActivityEntity> fishingActivities = Arrays.asList(fishingActivityEntity1, fishingActivityEntity2, fishingActivityEntity3, fishingActivityEntity4);

        Collections.sort(fishingActivities, new FishingActivityCalculatedDateComparator());

        Assert.assertEquals(fishingActivities.get(0).getCalculatedStartTime(), fishingActivityEntity2.getCalculatedStartTime());
        Assert.assertEquals(fishingActivities.get(2).getCalculatedStartTime(), fishingActivityEntity1.getCalculatedStartTime());
        Assert.assertEquals(fishingActivities.get(3).getCalculatedStartTime(), null);
    }

    @Test
    public void testInitCatchEvolutionProgressDTO() {
        FishingActivityEntity fishingActivityEntity = ActivityDataUtil.getFishingActivityEntity("DEPARTURE", "FLUX_FA_TYPE",
                parseToUTCDate("2014-05-27 07:47:31"), "FISHING", "FIS", null, null);

        CatchEvolutionProgressDTO catchEvolutionProgressDTO = initCatchEvolutionProgressDTO(fishingActivityEntity, FaReportDocumentType.DECLARATION, new HashMap<String, Double>());

        assertTrue(catchEvolutionProgressDTO.getCatchEvolution().containsKey("onboard"));
        assertTrue(catchEvolutionProgressDTO.getCatchEvolution().containsKey("cumulated"));
        assertTrue(catchEvolutionProgressDTO.getReportType().equals(FaReportDocumentType.DECLARATION.name()));
    }

    @Test
    public void testHandleOnboardCatch() {
        FaReportDocumentEntity faReportDocumentEntity = ActivityDataUtil.getFaReportDocumentEntity(FaReportDocumentType.DECLARATION.name(), "FLUX_FA_REPORT_TYPE",
                parseToUTCDate("2016-06-27 07:47:31"), null,
                null, FaReportStatusType.NEW);
        FishingActivityEntity fishingActivityEntity = ActivityDataUtil.getFishingActivityEntity(FishingActivityTypeEnum.FISHING_OPERATION.name(), "FLUX_FA_TYPE",
                parseToUTCDate("2014-05-27 07:47:31"), "FISHING", "FIS", faReportDocumentEntity, null);
        FaCatchEntity faCatchEntity = ActivityDataUtil.getFaCatchEntity(fishingActivityEntity, "LOADED", "FA_CATCH_TYPE", "COD", "FAO_SPECIES",
                11112D, 11112.0D, "KGM", "BFT", "WEIGHT_MEANS", null);
        faCatchEntity.setCalculatedWeightMeasure(11112D);
        CatchEvolutionProgressDTO catchEvolutionProgressDTO = initCatchEvolutionProgressDTO(fishingActivityEntity, FaReportDocumentType.DECLARATION, new HashMap<String, Double>());

        handleOnboardCatch(faCatchEntity, catchEvolutionProgressDTO);

        assertTrue(!catchEvolutionProgressDTO.getCatchEvolution().get("onboard").getSpeciesList().isEmpty());
        assertEquals(catchEvolutionProgressDTO.getCatchEvolution().get("onboard").getSpeciesList().get(0).getSpeciesCode(), "COD");
        assertTrue(catchEvolutionProgressDTO.getCatchEvolution().get("onboard").getSpeciesList().get(0).getWeight() == 11112D);
    }

    @Test
    public void testHandleCumulatedCatchNoDeletion() {
        FaReportDocumentEntity faReportDocumentEntity = ActivityDataUtil.getFaReportDocumentEntity(FaReportDocumentType.DECLARATION.name(), "FLUX_FA_REPORT_TYPE",
                parseToUTCDate("2016-06-27 07:47:31"), null,
                null, FaReportStatusType.NEW);
        FishingActivityEntity fishingActivityEntity = ActivityDataUtil.getFishingActivityEntity(FishingActivityTypeEnum.FISHING_OPERATION.name(), "FLUX_FA_TYPE",
                parseToUTCDate("2014-05-27 07:47:31"), "FISHING", "FIS", faReportDocumentEntity, null);
        FaCatchEntity faCatchEntity = ActivityDataUtil.getFaCatchEntity(fishingActivityEntity, "LOADED", "FA_CATCH_TYPE", "COD", "FAO_SPECIES",
                11112D, 11112.0D, "KGM", "BFT", "WEIGHT_MEANS", null);
        faCatchEntity.setCalculatedWeightMeasure(11112D);
        CatchEvolutionProgressDTO catchEvolutionProgressDTO = initCatchEvolutionProgressDTO(fishingActivityEntity, FaReportDocumentType.DECLARATION, new HashMap<String, Double>());

        handleCumulatedCatchNoDeletion(faCatchEntity, catchEvolutionProgressDTO, new HashMap<String, Double>());

        assertTrue(!catchEvolutionProgressDTO.getCatchEvolution().get("cumulated").getSpeciesList().isEmpty());
        assertEquals(catchEvolutionProgressDTO.getCatchEvolution().get("cumulated").getSpeciesList().get(0).getSpeciesCode(), "COD");
        assertTrue(catchEvolutionProgressDTO.getCatchEvolution().get("cumulated").getSpeciesList().get(0).getWeight() == 11112D);
    }

    @Test
    public void testHandleCumulatedCatchWithDeletion() {
        FaReportDocumentEntity faReportDocumentEntity = ActivityDataUtil.getFaReportDocumentEntity(FaReportDocumentType.DECLARATION.name(), "FLUX_FA_REPORT_TYPE",
                parseToUTCDate("2016-06-27 07:47:31"), null,
                null, FaReportStatusType.NEW);
        FishingActivityEntity fishingActivityEntity = ActivityDataUtil.getFishingActivityEntity(FishingActivityTypeEnum.DISCARD.name(), "FLUX_FA_TYPE",
                parseToUTCDate("2014-05-27 07:47:31"), "FISHING", "FIS", faReportDocumentEntity, null);
        FaCatchEntity faCatchEntity = ActivityDataUtil.getFaCatchEntity(fishingActivityEntity, "DEMINIMIS", "FA_CATCH_TYPE", "COD", "FAO_SPECIES",
                11112D, 11112.0D, "KGM", "BFT", "WEIGHT_MEANS", null);
        faCatchEntity.setCalculatedWeightMeasure(11112D);
        CatchEvolutionProgressDTO catchEvolutionProgressDTO = initCatchEvolutionProgressDTO(fishingActivityEntity, FaReportDocumentType.DECLARATION, new HashMap<String, Double>());

        SpeciesQuantityDTO speciesQuantityDTO = new SpeciesQuantityDTO();
        speciesQuantityDTO.setSpeciesCode("COD");
        speciesQuantityDTO.setWeight(1000D);

        catchEvolutionProgressDTO.getCatchEvolution().get("cumulated").addSpecieAndQuantity("COD", 11112D, StringUtils.EMPTY);
        catchEvolutionProgressDTO.getCatchEvolution().get("onboard").addSpecieAndQuantity("COD", 11112D, StringUtils.EMPTY);
        catchEvolutionProgressDTO.getCatchEvolution().get("onboard").addSpecieAndQuantity("HKE", 11112D, StringUtils.EMPTY);

        handleCumulatedCatchWithDeletion(faCatchEntity, catchEvolutionProgressDTO, new HashMap<String, Double>(), Arrays.asList(FaCatchTypeEnum.DEMINIMIS));

        assertTrue(!catchEvolutionProgressDTO.getCatchEvolution().get("cumulated").getSpeciesList().isEmpty());
        assertEquals(catchEvolutionProgressDTO.getCatchEvolution().get("cumulated").getSpeciesList().get(0).getSpeciesCode(), "COD");
        assertTrue(catchEvolutionProgressDTO.getCatchEvolution().get("cumulated").getSpeciesList().get(0).getWeight() == 0);
    }

}
