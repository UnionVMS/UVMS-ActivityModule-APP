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

import eu.europa.ec.fisheries.ers.fa.dao.FishingTripDao;
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
import java.util.List;

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
        cronologyTripDTO.setCurrentTrip(getCurrentTrip(tripId, latestVesselIdentifiers));
        cronologyTripDTO.setSelectedTrip(tripId);
        return cronologyTripDTO;
    }

    private String getCurrentTrip(String tripId, List<VesselIdentifierEntity> vesselIdentifiers) {
        String currentTrip = null;
        if (vesselIdentifiers != null && !vesselIdentifiers.isEmpty()) {
            for (VesselIdentifierEntity vesselIdentifier : vesselIdentifiers) {
                FishingTripIdentifierEntity identifierEntity = fishingTripIdentifierDao.getCurrentTrip(vesselIdentifier.getVesselIdentifierId(),
                        vesselIdentifier.getVesselIdentifierSchemeId());
                currentTrip = identifierEntity != null ? identifierEntity.getTripId() : null;
                break;
            }
        }
        return currentTrip;
    }
}
