package eu.europa.ec.fisheries.ers.activity.service.facatch.evolution;

import eu.europa.ec.fisheries.ers.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.activity.fa.utils.FaReportDocumentType;
import eu.europa.ec.fisheries.ers.activity.service.dto.fishingtrip.CatchEvolutionProgressDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FaCatchTypeEnum;
import org.apache.commons.lang3.EnumUtils;

import java.util.Map;

public class LANCatchEvolutionProgressHandler extends CatchEvolutionProgressHandler {
    @Override
    public CatchEvolutionProgressDTO prepareCatchEvolutionProgressDTO(FishingActivityEntity fishingActivity, Map<String, Double> speciesCumulatedWeight) {
        FaReportDocumentType faReportDocumentType = EnumUtils.getEnum(FaReportDocumentType.class, fishingActivity.getFaReportDocument().getTypeCode());

        if (faReportDocumentType != null && faReportDocumentType != FaReportDocumentType.DECLARATION) {
            return null;
        }

        CatchEvolutionProgressDTO catchEvolutionProgressDTO = initCatchEvolutionProgressDTO(fishingActivity, faReportDocumentType, speciesCumulatedWeight);

        for (FaCatchEntity faCatch : fishingActivity.getFaCatchs()) {
            FaCatchTypeEnum faCatchType = EnumUtils.getEnum(FaCatchTypeEnum.class, faCatch.getTypeCode());

            if (faCatchType == FaCatchTypeEnum.UNLOADED) {
                handleOnboardCatch(faCatch, catchEvolutionProgressDTO);
            }
        }

        return catchEvolutionProgressDTO;
    }
}
