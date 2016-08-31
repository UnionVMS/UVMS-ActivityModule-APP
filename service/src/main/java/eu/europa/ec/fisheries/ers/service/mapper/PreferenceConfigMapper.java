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

package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.model.dto.config.ActivityConfigDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.config.FaReportConfigDTO;

/**
 * Created by padhyad on 8/24/2016.
 */
public class PreferenceConfigMapper {

    public static final PreferenceConfigMapper INSTANCE;

    static {
        INSTANCE = new PreferenceConfigMapper();
    }

    public ActivityConfigDTO mergeUserPreference(ActivityConfigDTO target, ActivityConfigDTO source) {
        target.setFaReportConfig(mergeFaReportConfig(target.getFaReportConfig(), source.getFaReportConfig()));
        return target;
    }

    public ActivityConfigDTO resetUserPreference(ActivityConfigDTO target, ActivityConfigDTO source) {
        target.setFaReportConfig(resetFaReportConfig(target.getFaReportConfig(), source.getFaReportConfig()));
        return target;
    }

    private FaReportConfigDTO resetFaReportConfig(FaReportConfigDTO target, FaReportConfigDTO source) {
        if (source == null || target == null) {
            return target;
        }
        target.setAapProcess((source.getAapProcess() != null) ? null : target.getAapProcess());
        target.setAapProduct((source.getAapProduct() != null) ? null : target.getAapProduct());
        target.setAapStock((source.getAapStock() != null) ? null : target.getAapStock());
        target.setContactParty((source.getContactParty() != null) ? null : target.getContactParty());
        target.setContactPerson((source.getContactPerson() != null) ? null : target.getContactPerson());
        target.setDelimitedPeriod((source.getDelimitedPeriod() != null) ? null : target.getDelimitedPeriod());
        target.setFaCatches((source.getFaCatches() != null) ? null : target.getFaCatches());
        target.setFaReport((source.getFaReport() != null) ? null : target.getFaReport());
        target.setFishingActivity((source.getFishingActivity() != null) ? null : target.getFishingActivity());
        target.setFishingGears((source.getFishingGears() != null) ? null : target.getFishingGears());
        target.setFishingTrip((source.getFishingTrip() != null) ? null : target.getFishingTrip());
        target.setFluxCharacteristics((source.getFluxCharacteristics() != null) ? null : target.getFluxCharacteristics());
        target.setFluxLocations((source.getFluxLocations() != null) ? null : target.getFluxLocations());
        target.setGearCharacteristics((source.getGearCharacteristics() != null) ? null : target.getGearCharacteristics());
        target.setGearProblems((source.getGearProblems() != null) ? null : target.getGearProblems());
        target.setStructuredAddress((source.getStructuredAddress() != null) ? null : target.getStructuredAddress());
        target.setVessel((source.getVessel() != null) ? null : target.getVessel());
        return target;
    }

    private FaReportConfigDTO mergeFaReportConfig(FaReportConfigDTO target, FaReportConfigDTO source) {

        if (target == null) {
            return source;
        }

        if (source == null) {
            return target;
        }

        target.setAapProcess((source.getAapProcess() == null || source.getAapProcess().isEmpty()) ? target.getAapProcess() : source.getAapProcess());
        target.setAapProduct((source.getAapProduct() == null || source.getAapProduct().isEmpty()) ? target.getAapProduct() : source.getAapProduct());
        target.setAapStock((source.getAapStock() == null || source.getAapStock().isEmpty()) ? target.getAapStock() : source.getAapStock());
        target.setContactParty((source.getContactParty() == null || source.getContactParty().isEmpty()) ? target.getContactParty() : source.getContactParty());
        target.setContactPerson((source.getContactPerson() == null || source.getContactPerson().isEmpty()) ? target.getContactPerson() : source.getContactPerson());
        target.setDelimitedPeriod((source.getDelimitedPeriod() == null || source.getDelimitedPeriod().isEmpty()) ? target.getDelimitedPeriod() : source.getDelimitedPeriod());
        target.setFaCatches((source.getFaCatches() == null || source.getFaCatches().isEmpty()) ? target.getFaCatches() : source.getFaCatches());
        target.setFaReport((source.getFaReport() == null || source.getFaReport().isEmpty()) ? target.getFaReport() : source.getFaReport());
        target.setFishingActivity((source.getFishingActivity() == null || source.getFishingActivity().isEmpty()) ? target.getFishingActivity() : source.getFishingActivity());
        target.setFishingGears((source.getFishingGears() == null || source.getFishingGears().isEmpty()) ? target.getFishingGears() : source.getFishingGears());
        target.setFishingTrip((source.getFishingTrip() == null || source.getFishingTrip().isEmpty()) ? target.getFishingTrip() : source.getFishingTrip());
        target.setFluxCharacteristics((source.getFluxCharacteristics() == null || source.getFluxCharacteristics().isEmpty()) ? target.getFluxCharacteristics() : source.getFluxCharacteristics());
        target.setFluxLocations((source.getFluxLocations() == null || source.getFluxLocations().isEmpty()) ? target.getFluxLocations() : source.getFluxLocations());
        target.setGearCharacteristics((source.getGearCharacteristics() == null || source.getGearCharacteristics().isEmpty()) ? target.getGearCharacteristics() : source.getGearCharacteristics());
        target.setGearProblems((source.getGearProblems() == null || source.getGearProblems().isEmpty()) ? target.getGearProblems() : source.getGearProblems());
        target.setStructuredAddress((source.getStructuredAddress() == null || source.getStructuredAddress().isEmpty()) ? target.getStructuredAddress() : source.getStructuredAddress());
        target.setVessel((source.getVessel() == null || source.getVessel().isEmpty()) ? target.getVessel() : source.getVessel());

        return target;
    }
}
