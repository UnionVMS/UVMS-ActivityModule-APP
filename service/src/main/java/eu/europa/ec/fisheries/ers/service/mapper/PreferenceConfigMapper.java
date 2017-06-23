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

import eu.europa.ec.fisheries.ers.service.dto.config.ActivityConfigDTO;
import eu.europa.ec.fisheries.ers.service.dto.config.FishingActivityConfigDTO;

/**
 * Created by padhyad on 8/24/2016.
 */
public class PreferenceConfigMapper {

    public static final PreferenceConfigMapper INSTANCE;

    static {
        INSTANCE = new PreferenceConfigMapper();
    }

    public ActivityConfigDTO mergeUserPreference(ActivityConfigDTO target, ActivityConfigDTO source) {
        target.setFishingActivityConfig(mergeFaReportConfig(target.getFishingActivityConfig(), source.getFishingActivityConfig()));
        return target;
    }

    public ActivityConfigDTO resetUserPreference(ActivityConfigDTO target, ActivityConfigDTO source) {
        target.setFishingActivityConfig(resetFaReportConfig(target.getFishingActivityConfig(), source.getFishingActivityConfig()));
        return target;
    }

    private FishingActivityConfigDTO resetFaReportConfig(FishingActivityConfigDTO target, FishingActivityConfigDTO source) {
        if (source == null || target == null) {
            return target;
        }
        target.setSummaryReport((source.getSummaryReport() != null) ? null : target.getSummaryReport());
        return target;
    }

    private FishingActivityConfigDTO mergeFaReportConfig(FishingActivityConfigDTO target, FishingActivityConfigDTO source) {

        if (target == null) {
            return source;
        }

        if (source == null) {
            return target;
        }

        target.setSummaryReport((source.getSummaryReport() == null) ? target.getSummaryReport() : source.getSummaryReport());
        return target;
    }
}
