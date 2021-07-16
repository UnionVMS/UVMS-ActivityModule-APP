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

package eu.europa.ec.fisheries.ers.fa.utils;

import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;

/**
 * Created by padhyad on 10/12/2016.
 */
public enum VesselTypeAssetQueryEnum {

    CFR("CFR", ConfigSearchField.CFR),
    IRCS("IRCS", ConfigSearchField.IRCS),
    EXT_MARK("EXT_MARK", ConfigSearchField.EXTERNAL_MARKING),
    FLAG_STATE("FLAG_STATE", ConfigSearchField.FLAG_STATE),
    NAME("NAME", ConfigSearchField.NAME),
    ICCAT("ICCAT", ConfigSearchField.ICCAT),
    UVI("UVI", ConfigSearchField.UVI);

    private String type;

    private ConfigSearchField configSearchField;

    VesselTypeAssetQueryEnum(String type, ConfigSearchField configSearchField) {
        this.type = type;
        this.configSearchField = configSearchField;
    }

    public static VesselTypeAssetQueryEnum getVesselTypeAssetQueryEnum(String type) {
        for (VesselTypeAssetQueryEnum vesselTypeAssetQueryEnum : VesselTypeAssetQueryEnum.values()) {
            if (vesselTypeAssetQueryEnum.type.equalsIgnoreCase(type)) {
                return vesselTypeAssetQueryEnum;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public ConfigSearchField getConfigSearchField() {
        return configSearchField;
    }
}
