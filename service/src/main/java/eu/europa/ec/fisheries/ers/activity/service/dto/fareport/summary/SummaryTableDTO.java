
/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package eu.europa.ec.fisheries.ers.activity.service.dto.fareport.summary;

import eu.europa.ec.fisheries.uvms.activity.model.schemas.FaCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishSizeClassEnum;

import java.util.Map;

/**
 * Created by sanera on 27/01/2017.
 */
public class SummaryTableDTO {


    private Map<FishSizeClassEnum,Object> summaryFishSize;
     private Map<FaCatchTypeEnum,Object> summaryFaCatchType;

    public Map<FishSizeClassEnum, Object> getSummaryFishSize() {
        return summaryFishSize;
    }

    public void setSummaryFishSize(Map<FishSizeClassEnum, Object> summaryFishSize) {
        this.summaryFishSize = summaryFishSize;
    }

    public Map<FaCatchTypeEnum, Object> getSummaryFaCatchType() {
        return summaryFaCatchType;
    }

    public void setSummaryFaCatchType(Map<FaCatchTypeEnum, Object> summaryFaCatchType) {
        this.summaryFaCatchType = summaryFaCatchType;
    }

    @Override
    public String toString() {
        return "SummaryTable{" +
                "summaryFishSize=" + summaryFishSize +
                ", summaryFaCatchType=" + summaryFaCatchType +
                '}';
    }
}
