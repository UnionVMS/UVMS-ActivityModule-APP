package eu.europa.ec.fisheries.ers.service.facatch.evolution;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.ArrCatchProgressDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.CatchProgressDTO;

public class ArrCatchProgressHandler extends CatchProgressHandler {

    @Override
    public CatchProgressDTO prepareCatchProgressDTO(FishingActivityEntity fishingActivity) {
        if (fishingActivity == null) {
            throw new IllegalArgumentException("Fishing activity is null");
        }

        String reportType = fishingActivity.getFaReportDocument().getTypeCode();
        ArrCatchProgressDTO catchProgressDTO = new ArrCatchProgressDTO(reportType, false, fishingActivity.getCalculatedStartTime());

        fishingActivity.getFaCatchs().forEach(faCatch -> handleDetailedCatch(faCatch, catchProgressDTO));

        return catchProgressDTO;
    }
}
