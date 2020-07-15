package eu.europa.ec.fisheries.ers.service.facatch.evolution;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportDocumentType;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.CatchProgressDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.JfopCatchProgressDTO;

import java.util.Set;

public class JfopCatchProgressHandler extends FopCatchProgressHandler {

    @Override
    public CatchProgressDTO prepareCatchProgressDTO(FishingActivityEntity fishingActivity) {
        if (fishingActivity == null) {
            throw new IllegalArgumentException("Fishing activity is null");
        }

        String reportType = fishingActivity.getFaReportDocument().getTypeCode();
        JfopCatchProgressDTO catchProgressDTO = new JfopCatchProgressDTO(reportType, reportType.equals(FaReportDocumentType.DECLARATION.name()), fishingActivity.getCalculatedStartTime());

        Set<FaCatchEntity> faCatches = fishingActivity.getFaCatchs();
        faCatches.forEach(faCatch -> handleSubtractingCatch(faCatch, faCatches, catchProgressDTO));

        return catchProgressDTO;
    }
}
