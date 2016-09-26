/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.service.bean;

import com.google.common.collect.ImmutableMap;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripIdentifierDao;
import eu.europa.ec.fisheries.ers.fa.dao.VesselIdentifiersDao;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.service.*;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.CronologyTripDTO;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by padhyad on 9/22/2016.
 */
@Stateless
@Local(FishingTripService.class)
@Transactional
@Slf4j
public class FishingTripServiceBean implements FishingTripService {

    @PersistenceContext(unitName = "activityPU")
    private EntityManager em;

    private VesselIdentifiersDao vesselIdentifiersDao;
    private FishingTripIdentifierDao fishingTripIdentifierDao;

    private static final String PREVIOUS = "PREVIOUS";
    private static final String NEXT = "NEXT";

    @PostConstruct
    public void init() {
        fishingTripIdentifierDao = new FishingTripIdentifierDao(em);
        vesselIdentifiersDao = new VesselIdentifiersDao(em);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public CronologyTripDTO getCronologyOfFishingTrip(String tripId, Integer count) {
        List<VesselIdentifierEntity> latestVesselIdentifiers = vesselIdentifiersDao.getLatestVesselIdByTrip(tripId); // Find the latest Vessel for the Trip for finding the trip of that vessel
        CronologyTripDTO cronologyTripDTO = new CronologyTripDTO();
        cronologyTripDTO.setCurrentTrip(getCurrentTrip(latestVesselIdentifiers));
        cronologyTripDTO.setSelectedTrip(tripId);

        List<String> previousTrips = new ArrayList<>(getPreviousTrips(tripId, count, latestVesselIdentifiers));
        List<String> nextTrips = new ArrayList<>(getNextTrips(tripId, count, latestVesselIdentifiers));

        Map<String, Integer> countMap = calculateTripCounts(count, previousTrips.size(), nextTrips.size());
        log.info("Number of previous record to find : " + countMap.get(PREVIOUS));
        log.info("Number of next record to find : " + countMap.get(NEXT));

        cronologyTripDTO.setPreviousTrips(previousTrips.subList(previousTrips.size() - countMap.get(PREVIOUS), previousTrips.size()));
        cronologyTripDTO.setNextTrips(nextTrips.subList(0, countMap.get(NEXT)));

        return cronologyTripDTO;
    }

    private Map<String, Integer> calculateTripCounts(Integer count, Integer previousTripCount, Integer nextTripCount) {

        Integer previous = 0;
        Integer next = 0;

        if (count == 0) {
            log.info("All the previous and next result will be returned");
            previous = previousTripCount;
            next = nextTripCount;
        } else if (count != 1) {
            log.info("Count the number of previous and next result based on count received");
            count = count - 1;
            if (count % 2 == 0) {
                previous = count/2;
                next = count/2;
            } else if (count % 2 == 1) {
                previous = count/2 + 1;
                next = count/2;
            }
            if (previous > previousTripCount) {
                previous = previousTripCount;
                next = count - previous;
            }
            if (next > nextTripCount) {
                next = nextTripCount;
                previous = count - next;
                if (previous > previousTripCount) {
                    previous = previousTripCount;
                }
            }
        }
        return ImmutableMap.<String, Integer> builder().put(PREVIOUS, previous).put(NEXT, next).build();
    }

    private String getCurrentTrip(List<VesselIdentifierEntity> vesselIdentifiers) {
        String currentTrip = null;
        if (vesselIdentifiers != null && !vesselIdentifiers.isEmpty()) {
            for (VesselIdentifierEntity vesselIdentifier : vesselIdentifiers) {
                FishingTripIdentifierEntity identifierEntity = fishingTripIdentifierDao.getCurrentTrip(vesselIdentifier.getVesselIdentifierId(),
                        vesselIdentifier.getVesselIdentifierSchemeId());
                currentTrip = identifierEntity != null ? identifierEntity.getTripId() : null;
                break;
            }
        }
        log.info("Current Trip : " + currentTrip);
        return currentTrip;
    }

    private Set<String> getPreviousTrips(String tripId, Integer limit, List<VesselIdentifierEntity> vesselIdentifiers) {
        Set<String> tripIds = new LinkedHashSet<>();
        if (vesselIdentifiers != null && !vesselIdentifiers.isEmpty()) {
            for (VesselIdentifierEntity vesselIdentifier : vesselIdentifiers) {
                List<FishingTripIdentifierEntity> identifierEntities = fishingTripIdentifierDao.getPreviousTrips(vesselIdentifier.getVesselIdentifierId(),
                        vesselIdentifier.getVesselIdentifierSchemeId(), tripId, limit);
                for (FishingTripIdentifierEntity identifiers : identifierEntities) {
                    tripIds.add(identifiers.getTripId());
                }
            }
        }
        log.info("Previous Trips : " + tripIds);
        return tripIds;
    }

    private Set<String> getNextTrips(String tripId, Integer limit, List<VesselIdentifierEntity> vesselIdentifiers) {
        Set<String> tripIds = new LinkedHashSet<>();
        if (vesselIdentifiers != null && !vesselIdentifiers.isEmpty()) {
            for (VesselIdentifierEntity vesselIdentifier : vesselIdentifiers) {
                List<FishingTripIdentifierEntity> identifierEntities = fishingTripIdentifierDao.getNextTrips(vesselIdentifier.getVesselIdentifierId(),
                        vesselIdentifier.getVesselIdentifierSchemeId(), tripId, limit);
                for (FishingTripIdentifierEntity identifiers : identifierEntities) {
                    tripIds.add(identifiers.getTripId());
                }
            }
        }
        log.info("Next Trips : " + tripIds);
        return tripIds;
    }
}
