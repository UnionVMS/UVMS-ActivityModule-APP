package eu.europa.ec.fisheries.ers.service.facatch.evolution;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress.CatchProgressDTO;
import org.apache.commons.lang3.EnumUtils;

import java.util.*;
import java.util.stream.Collectors;

public class CatchProgressProcessor {

    public static final String[] ALTERING_PURPOSE_CODES = {"1", "5", "3"};
    
    private CatchProgressRegistry catchProgressRegistry;

    public CatchProgressProcessor(CatchProgressRegistry catchProgressRegistry) {
        this.catchProgressRegistry = catchProgressRegistry;
    }

    public List<CatchProgressDTO> process(List<FishingActivityEntity> fishingActivities) {
        fishingActivities = filterAlteredReports(fishingActivities);
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
    
    
    
    public List<FishingActivityEntity> filterAlteredReports(List<FishingActivityEntity> activities) {
        List<String> alteringPurposeCodes = Arrays.asList(ALTERING_PURPOSE_CODES);
        List<String> idsToBeRemoved = activities.stream()
                                                .filter(fa -> alteringPurposeCodes.contains(getPurposeCode(fa)))
                                                .map(this::getReferenceId)
                                                .collect(Collectors.toList());
        return activities.stream().filter(fa -> !activityReportContainsIdForRemoval(fa, idsToBeRemoved)).collect(Collectors.toList());
    }
    
    public String getPurposeCode(FishingActivityEntity fishingActivityEntity) {
        if( fishingActivityEntity.getFaReportDocument() != null && fishingActivityEntity.getFaReportDocument().getFluxReportDocument() != null) {
            return fishingActivityEntity.getFaReportDocument().getFluxReportDocument().getPurposeCode();
        }
        return "";
    }
    
    public String getReferenceId(FishingActivityEntity fishingActivityEntity) {
        if( fishingActivityEntity.getFaReportDocument() != null
                && fishingActivityEntity.getFaReportDocument().getFluxReportDocument() != null
                && fishingActivityEntity.getFaReportDocument().getFluxReportDocument().getReferenceId() != null) {
            return fishingActivityEntity.getFaReportDocument().getFluxReportDocument().getReferenceId();
        }
        return null;
    }
    
    public boolean activityReportContainsIdForRemoval(FishingActivityEntity fishingActivityEntity, List<String> idsToBeRemoved) {
        if( fishingActivityEntity.getFaReportDocument() != null
                && fishingActivityEntity.getFaReportDocument().getFluxReportDocument() != null
                && fishingActivityEntity.getFaReportDocument().getFluxReportDocument().getFluxReportIdentifiers() != null) {
            String id = fishingActivityEntity.getFaReportDocument().getFluxReportDocument().getFluxReportIdentifiers().stream().findFirst().map(FluxReportIdentifierEntity::getFluxReportIdentifierId).orElse(null);
            return idsToBeRemoved.contains(id);
        }
        return false;
    }
}
