package eu.europa.ec.fisheries.ers.service.facatch.evolution;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportDocumentType;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.CatchProgressDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.DisCatchProgressDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FaCatchTypeEnum;

import java.util.Set;

public class DisCatchProgressHandler extends CatchProgressHandler {

    @Override
    public CatchProgressDTO prepareCatchProgressDTO(FishingActivityEntity fishingActivity) {
        if (fishingActivity == null) {
            throw new IllegalArgumentException("Fishing activity is null");
        }

        String reportType = fishingActivity.getFaReportDocument().getTypeCode();
        DisCatchProgressDTO catchProgressDTO = new DisCatchProgressDTO(reportType, reportType.equals(FaReportDocumentType.DECLARATION.name()), fishingActivity.getCalculatedStartTime());


        Set<FaCatchEntity> faCatches = fishingActivity.getFaCatchs();

        faCatches.forEach(faCatch ->{
            if(faCatch.getTypeCode().equals("TAKEN_ONBOARD")){
                faCatch.setTypeCode(FaCatchTypeEnum.TAKEN_ON_BOARD.value());
            }
        });

        faCatches.forEach(faCatch -> handleSubtractingCatch(faCatch, faCatches, catchProgressDTO));

        return catchProgressDTO;
    }
}
