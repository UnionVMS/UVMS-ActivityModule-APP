package eu.europa.ec.fisheries.uvms.activity.service.facatch.evolution;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportDocumentType;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CatchEvolutionProgressDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CatchSummaryListDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.SpeciesQuantityDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FaCatchTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

public abstract class CatchEvolutionProgressHandler {

    private static final String PARAM_CUMULATED = "cumulated";
    private static final String PARAM_ONBOARD = "onboard";

    public abstract CatchEvolutionProgressDTO prepareCatchEvolutionProgressDTO(FishingActivityEntity fishingActivity, Map<String, Double> speciesCumulatedWeight);

    protected CatchEvolutionProgressDTO initCatchEvolutionProgressDTO(FishingActivityEntity fishingActivity, FaReportDocumentType faReportDocumentType, Map<String, Double> speciesCumulatedWeight) {
        CatchSummaryListDTO onboard = new CatchSummaryListDTO();
        CatchSummaryListDTO cumulated = new CatchSummaryListDTO();
        Map<String, CatchSummaryListDTO> catchEvolution = new TreeMap<>();
        catchEvolution.put(PARAM_ONBOARD, onboard);
        catchEvolution.put(PARAM_CUMULATED, cumulated);
        CatchEvolutionProgressDTO catchEvolutionProgressDTO = new CatchEvolutionProgressDTO();
        catchEvolutionProgressDTO.setActivityType(fishingActivity.getTypeCode());
        catchEvolutionProgressDTO.setCatchEvolution(catchEvolution);
        catchEvolutionProgressDTO.setReportType(faReportDocumentType.name());

        for (Map.Entry<String, Double> scw : speciesCumulatedWeight.entrySet()) {
            SpeciesQuantityDTO speciesQuantityDTO = new SpeciesQuantityDTO();
            speciesQuantityDTO.setSpeciesCode(scw.getKey());
            Double weight = scw.getValue();
            speciesQuantityDTO.setWeight(weight);
            cumulated.getSpeciesList().add(speciesQuantityDTO);
            cumulated.setTotal(cumulated.getTotal() + weight);
        }

        return catchEvolutionProgressDTO;
    }

    protected void handleOnboardCatch(FaCatchEntity faCatch, CatchEvolutionProgressDTO catchEvolutionProgressDTO) {
        CatchSummaryListDTO onboard = catchEvolutionProgressDTO.getCatchEvolution().get(PARAM_ONBOARD);
        String speciesCode = faCatch.getSpeciesCode();
        boolean exists = false;
        onboard.setTotal(onboard.getTotal() + faCatch.getCalculatedWeightMeasure());

        for (SpeciesQuantityDTO speciesQuantityDTO : onboard.getSpeciesList()) {
            if (speciesQuantityDTO.getSpeciesCode().equalsIgnoreCase(speciesCode)) {
                speciesQuantityDTO.setWeight(speciesQuantityDTO.getWeight() + faCatch.getCalculatedWeightMeasure());
                exists = true;
                break;
            }
        }

        if (!exists) {
            SpeciesQuantityDTO speciesQuantityDTO = new SpeciesQuantityDTO();
            speciesQuantityDTO.setSpeciesCode(speciesCode);
            speciesQuantityDTO.setWeight(faCatch.getCalculatedWeightMeasure());
            onboard.getSpeciesList().add(speciesQuantityDTO);
        }
    }

    protected void handleCumulatedCatchNoDeletion(FaCatchEntity faCatch, CatchEvolutionProgressDTO catchEvolutionProgressDTO, Map<String, Double> speciesCumulatedWeight) {
        CatchSummaryListDTO cumulated = catchEvolutionProgressDTO.getCatchEvolution().get(PARAM_CUMULATED);
        handleCumulatedCatch(faCatch, cumulated, speciesCumulatedWeight, false);
    }

    protected void handleCumulatedCatchWithDeletion(FaCatchEntity faCatch, CatchEvolutionProgressDTO catchEvolutionProgressDTO, Map<String, Double> speciesCumulatedWeight, List<FaCatchTypeEnum> catchTypesToDelete) {
        CatchSummaryListDTO cumulated = catchEvolutionProgressDTO.getCatchEvolution().get(PARAM_CUMULATED);

        for (FaCatchTypeEnum faCatchType : catchTypesToDelete) {
            FaCatchTypeEnum faCatchTypeEnum = EnumUtils.getEnum(FaCatchTypeEnum.class, faCatch.getTypeCode());

            if (faCatchTypeEnum != null && faCatchTypeEnum == faCatchType) {
                handleCumulatedCatch(faCatch, cumulated, speciesCumulatedWeight, true);
            }
        }
    }

    protected void handleCumulatedCatch(FaCatchEntity faCatch, CatchSummaryListDTO cumulated, Map<String, Double> speciesCumulatedWeight, boolean delete) {
        String speciesCode = faCatch.getSpeciesCode();
        cumulated.setTotal(delete ? cumulated.getTotal() - faCatch.getCalculatedWeightMeasure() : cumulated.getTotal() + faCatch.getCalculatedWeightMeasure());

        Optional<SpeciesQuantityDTO> previousAccumulationForThisSpeciesOptional = cumulated.getSpeciesList().stream()
                .filter(dto -> speciesCode.equalsIgnoreCase(dto.getSpeciesCode()))
                .findAny();

        if (previousAccumulationForThisSpeciesOptional.isPresent()) {
            SpeciesQuantityDTO previousAccumulationForThisSpecies = previousAccumulationForThisSpeciesOptional.get();
            double weight = delete ? previousAccumulationForThisSpecies.getWeight() - faCatch.getCalculatedWeightMeasure() : previousAccumulationForThisSpecies.getWeight() + faCatch.getCalculatedWeightMeasure();
            previousAccumulationForThisSpecies.setWeight(weight);

            if (speciesCumulatedWeight.containsKey(speciesCode)) {
                double existingCumulatedWeight = speciesCumulatedWeight.get(speciesCode);
                double newCumulatedWeight = delete ? existingCumulatedWeight - faCatch.getCalculatedWeightMeasure() : existingCumulatedWeight + faCatch.getCalculatedWeightMeasure();
                speciesCumulatedWeight.put(speciesCode, newCumulatedWeight);
            } else {
                speciesCumulatedWeight.put(speciesCode, weight);
            }
        } else {
            SpeciesQuantityDTO speciesQuantityDTO = new SpeciesQuantityDTO();
            speciesQuantityDTO.setSpeciesCode(speciesCode);
            double weight;
            if (speciesCumulatedWeight.containsKey(speciesCode)) {
                weight = delete ? speciesCumulatedWeight.get(speciesCode) - faCatch.getCalculatedWeightMeasure() : speciesCumulatedWeight.get(speciesCode) + faCatch.getCalculatedWeightMeasure();
            } else {
                weight = faCatch.getCalculatedWeightMeasure();
            }
            speciesCumulatedWeight.put(speciesCode, weight);
            speciesQuantityDTO.setWeight(weight);
            cumulated.getSpeciesList().add(speciesQuantityDTO);
        }
    }

    protected boolean isFaCatchTypePresent(Set<FaCatchEntity> faCatches, FaCatchTypeEnum faCatchType) {
        if (CollectionUtils.isEmpty(faCatches) || faCatchType == null) {
            return false;
        }

        for (FaCatchEntity faCatch : faCatches) {
            FaCatchTypeEnum faCatchTypeTemp = EnumUtils.getEnum(FaCatchTypeEnum.class, faCatch.getTypeCode());

            if (faCatchTypeTemp != null && faCatchTypeTemp == faCatchType) {
                return true;
            }
        }

        return false;
    }
}
