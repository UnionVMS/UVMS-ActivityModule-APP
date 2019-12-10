package eu.europa.ec.fisheries.ers.service.facatch.evolution;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.CatchProgressDTO;
import org.apache.commons.lang3.EnumUtils;

import java.util.*;

public class CatchProgressProcessor {

    private CatchProgressRegistry catchProgressRegistry;

    public CatchProgressProcessor(CatchProgressRegistry catchProgressRegistry) {
        this.catchProgressRegistry = catchProgressRegistry;
    }

    public List<CatchProgressDTO> process(List<FishingActivityEntity> fishingActivities) {
        fishingActivities.sort(new FishingActivityCalculatedDateComparator());
        List<CatchProgressDTO> catchProgressDTOs = new ArrayList<>();

        int orderId = 1;
        for (FishingActivityEntity fishingActivity : fishingActivities) {
            FishingActivityTypeEnum fishingActivityType = EnumUtils.getEnum(FishingActivityTypeEnum.class, fishingActivity.getTypeCode());

            if (fishingActivityType != null && catchProgressRegistry != null && catchProgressRegistry.containsHandler(fishingActivityType)) {
                CatchProgressHandler catchProgressHandler = catchProgressRegistry.findHandler(fishingActivityType);
                CatchProgressDTO catchProgressDTO = catchProgressHandler.prepareCatchProgressDTO(fishingActivity);

                if (catchProgressDTO != null) {
                    catchProgressDTO.setOrderId(orderId++);
                    catchProgressDTOs.add(catchProgressDTO);
                }
            }
        }

        return catchProgressDTOs;
    }
}
