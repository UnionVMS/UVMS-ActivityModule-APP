package eu.europa.ec.fisheries.ers.service.facatch.evolution;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportDocumentType;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.CatchEvolutionProgressDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FaCatchTypeEnum;
import org.apache.commons.lang3.EnumUtils;

import java.util.Arrays;
import java.util.Map;

public class FOPCatchEvolutionProgressHandler extends CatchEvolutionProgressHandler {
    @Override
    public CatchEvolutionProgressDTO prepareCatchEvolutionProgressDTO(FishingActivityEntity fishingActivity, Map<String, Double> speciesCumulatedWeight) {
        FaReportDocumentType faReportDocumentType = EnumUtils.getEnum(FaReportDocumentType.class, fishingActivity.getFaReportDocument().getTypeCode());

        if (faReportDocumentType != null && faReportDocumentType != FaReportDocumentType.DECLARATION) {
            return null;
        }

        CatchEvolutionProgressDTO catchEvolutionProgressDTO = initCatchEvolutionProgressDTO(fishingActivity, faReportDocumentType, speciesCumulatedWeight);

        for (FaCatchEntity faCatch : fishingActivity.getFaCatchs()) {
            FaCatchTypeEnum faCatchType = EnumUtils.getEnum(FaCatchTypeEnum.class, faCatch.getTypeCode());

            if (faCatchType == FaCatchTypeEnum.ONBOARD || faCatchType == FaCatchTypeEnum.KEPT_IN_NET || faCatchType == FaCatchTypeEnum.BY_CATCH) {
                handleOnboardCatch(faCatch, catchEvolutionProgressDTO);
                handleCumulatedCatchNoDeletion(faCatch, catchEvolutionProgressDTO, speciesCumulatedWeight);
            } else if (faCatchType == FaCatchTypeEnum.TAKEN_ON_BOARD && !isFaCatchTypePresent(fishingActivity.getFaCatchs(), FaCatchTypeEnum.TAKEN_ON_BOARD)) {
                handleOnboardCatch(faCatch, catchEvolutionProgressDTO);
                handleCumulatedCatchWithDeletion(faCatch, catchEvolutionProgressDTO, speciesCumulatedWeight, Arrays.asList(FaCatchTypeEnum.DEMINIMIS, FaCatchTypeEnum.DISCARDED));
            }
        }

        return catchEvolutionProgressDTO;
    }


}
