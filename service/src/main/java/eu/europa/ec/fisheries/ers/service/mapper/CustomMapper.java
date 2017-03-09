package eu.europa.ec.fisheries.ers.service.mapper;


import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyRoleEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomMapper {

    public static List<String> getRoles(Set<ContactPartyRoleEntity> contactPartyRoles){
        List<String> roles = new ArrayList<>();
        for(ContactPartyRoleEntity roleEntity : contactPartyRoles){
            roles.add(roleEntity.getRoleCode());
        }
        return roles;
    }

    public static FishingTripEntity getSpecifiedFishingTrip(FishingActivityEntity activityEntity) {
        FishingTripEntity fishingTripEntity = null;
        Set<FishingTripEntity> fishingTrips = activityEntity.getFishingTrips();
        if (!CollectionUtils.isEmpty(fishingTrips)) {
            fishingTripEntity = activityEntity.getFishingTrips().iterator().next();
        }
        return fishingTripEntity;
    }

    public static FluxReportDocumentEntity getFluxReportDocument(FishingActivityEntity activityEntity) {
        FaReportDocumentEntity faReportDocument = activityEntity.getFaReportDocument();
        return faReportDocument != null ? faReportDocument.getFluxReportDocument() : null;
    }

    public static Set<FluxLocationEntity> getRelatedFluxLocations(FishingActivityEntity activityEntity) {
        FishingTripEntity specifiedFishingTrip = getSpecifiedFishingTrip(activityEntity);
        Set<FluxLocationEntity> relatedFluxLocations = new HashSet<>();
        if (specifiedFishingTrip != null){
            relatedFluxLocations = getRelatedFluxLocations(specifiedFishingTrip);
        }
        return relatedFluxLocations;
    }

    public static Set<FluxLocationEntity> getRelatedFluxLocations(FishingTripEntity tripEntity) {
        Set<FluxLocationEntity> fluxLocations = new HashSet<>();
        FishingActivityEntity fishingActivity = tripEntity.getFishingActivity();
        if (fishingActivity != null){
            fluxLocations = fishingActivity.getFluxLocations();
        }
        return fluxLocations;
    }
}
