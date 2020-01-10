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


package eu.europa.ec.fisheries.uvms.activity.fa.utils;

import eu.europa.ec.fisheries.uvms.spatial.model.constants.USMSpatial;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaType;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class UsmUtils {

    private UsmUtils() {}

    public static List<AreaIdentifierType> convertDataSetToAreaId(List<Dataset> datasets) {
        List<AreaIdentifierType> areaRestrictions = new ArrayList<>(datasets.size());
        for (Dataset dataset : datasets) {
            int lastIndexDelimiter = dataset.getDiscriminator().lastIndexOf(USMSpatial.DELIMITER);

            if (lastIndexDelimiter > -1 )  {
                AreaIdentifierType areaIdentifierType = new AreaIdentifierType();
                AreaType areaType = AreaType.valueOf(dataset.getDiscriminator().substring(0, lastIndexDelimiter));
                String areaId = dataset.getDiscriminator().substring(lastIndexDelimiter + 1);

                if (areaType!= null && StringUtils.isNotBlank(areaId)) {
                    areaIdentifierType.setAreaType(areaType);
                    areaIdentifierType.setId(areaId);
                    areaRestrictions.add(areaIdentifierType);
                }
            }
        }
        return areaRestrictions;
    }
}
