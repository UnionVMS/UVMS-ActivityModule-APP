package eu.europa.ec.fisheries.ers.service.catchprogress;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.CatchProgressDTO;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class CatchProgressHandlerUtil {

    static FishingActivityEntity makeFishingActivityEntity(String typeCode, String faReportDocTypeCode, FaCatchEntity... faCatches) {
        FishingActivityEntity fishingActivity = new FishingActivityEntity();
        fishingActivity.setTypeCode(typeCode);
        FaReportDocumentEntity faReportDocument = new FaReportDocumentEntity();
        faReportDocument.setTypeCode(faReportDocTypeCode);
        fishingActivity.setFaReportDocument(faReportDocument);

        Set<FaCatchEntity> faCatchesSet = new HashSet<>(Arrays.asList(faCatches));
        fishingActivity.setFaCatchs(faCatchesSet);

        return fishingActivity;
    }

    static FaCatchEntity makeFaCatch(String typeCode, String speciesCode, Double calculatedWeightMeasure) {
        FaCatchEntity result = new FaCatchEntity();
        result.setTypeCode(typeCode);
        result.setSpeciesCode(speciesCode);
        result.setCalculatedWeightMeasure(calculatedWeightMeasure);
        return result;
    }

    static void assertCatchProgressDTO(CatchProgressDTO result, String activityType, String reportType, boolean affectsCumulative) {
        assertNotNull(result);
        assertEquals(activityType, result.getActivityType());
        assertEquals(reportType, result.getReportType());
        assertEquals(affectsCumulative, result.isAffectsCumulative());
    }

    static void assertSpeciesNumber(Map<String, Double> speciesMap, int speciesListSize) {
        assertNotNull(speciesMap);
        assertEquals(speciesListSize, speciesMap.keySet().size());
    }

    static void assertSpeciesQuantity(Map<String, Double> speciesMap, String species, double weight) {
        assertEquals(Double.valueOf(weight), speciesMap.get(species));
    }
}
