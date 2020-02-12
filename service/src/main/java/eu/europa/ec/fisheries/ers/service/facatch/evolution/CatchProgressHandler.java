package eu.europa.ec.fisheries.ers.service.facatch.evolution;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.CatchProgressDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.DetailedCatchProgressDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.SimpleCatchProgressDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FaCatchTypeEnum;
import org.apache.commons.lang3.EnumUtils;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;


public abstract class CatchProgressHandler {

    private static final Set<FaCatchTypeEnum> ADDED_CATCH_TYPES = Collections.unmodifiableSet(EnumSet.of(FaCatchTypeEnum.ONBOARD, FaCatchTypeEnum.KEPT_IN_NET, FaCatchTypeEnum.BY_CATCH, FaCatchTypeEnum.LOADED));
    private static final Set<FaCatchTypeEnum> SUBTRACTED_CATCH_TYPES = Collections.unmodifiableSet(EnumSet.of(FaCatchTypeEnum.DEMINIMIS, FaCatchTypeEnum.DISCARDED, FaCatchTypeEnum.UNLOADED));

    public abstract CatchProgressDTO prepareCatchProgressDTO(FishingActivityEntity fishingActivity);

    protected void handleSimpleCatch(FaCatchEntity faCatch, SimpleCatchProgressDTO catchProgressDTO) {
        catchProgressDTO.updateTotal(faCatch.getSpeciesCode(), faCatch.getCalculatedWeightMeasure());
    }

    protected void handleSubtractingCatch(FaCatchEntity faCatch, Set<FaCatchEntity> faCatches, SimpleCatchProgressDTO catchProgressDTO) {
        FaCatchTypeEnum faCatchType = EnumUtils.getEnum(FaCatchTypeEnum.class, faCatch.getTypeCode());
        if(ADDED_CATCH_TYPES.contains(faCatchType)){
            catchProgressDTO.updateTotal(faCatch.getSpeciesCode(), faCatch.getCalculatedWeightMeasure());
        } else if (faCatchType == FaCatchTypeEnum.TAKEN_ONBOARD && !isFaCatchTypePresent(faCatches, FaCatchTypeEnum.ONBOARD)){
            catchProgressDTO.updateTotal(faCatch.getSpeciesCode(), faCatch.getCalculatedWeightMeasure());
        } else if(SUBTRACTED_CATCH_TYPES.contains(faCatchType) && isFaCatchTypePresent(faCatches, FaCatchTypeEnum.TAKEN_ONBOARD) && !isFaCatchTypePresent(faCatches, FaCatchTypeEnum.ONBOARD)){
            catchProgressDTO.updateTotal(faCatch.getSpeciesCode(), -faCatch.getCalculatedWeightMeasure());
        }
    }

    protected void handleDetailedCatch(FaCatchEntity faCatch, DetailedCatchProgressDTO catchProgressDTO) {
        FaCatchTypeEnum faCatchType = EnumUtils.getEnum(FaCatchTypeEnum.class, faCatch.getTypeCode());
        switch (faCatchType){
            case ONBOARD:
                catchProgressDTO.addOnboard(faCatch.getSpeciesCode(), faCatch.getCalculatedWeightMeasure());
                break;
            case LOADED:
                catchProgressDTO.addLoaded(faCatch.getSpeciesCode(), faCatch.getCalculatedWeightMeasure());
                break;
            case UNLOADED:
                catchProgressDTO.addUnloaded(faCatch.getSpeciesCode(), faCatch.getCalculatedWeightMeasure());
                break;
        }
    }

    private boolean isFaCatchTypePresent(Set<FaCatchEntity> faCatches, FaCatchTypeEnum faCatchType) {
        return faCatches.stream().map(faCatch -> EnumUtils.getEnum(FaCatchTypeEnum.class, faCatch.getTypeCode())).anyMatch(faCatchType::equals);
    }
}
