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

package eu.europa.ec.fisheries.ers.fa.utils;

import eu.europa.ec.fisheries.mdr.domain.ActivityConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sanera on 28/11/2016.
 * This class will store all the Activity project configurations.
 * Map in this class should be updated only once when the application is deployed.
 * Configurations should be read from this class and not from the database.
 */

public class ActivityConfigurationProperties {

    public static String LIMIT_FISHING_TRIPS ="LIMIT_FISHING_TRIPS";
    private static Map<String,String> configurations = new HashMap<>();

    private ActivityConfigurationProperties(){

    }

    // Load Map with database values. Ideally this method should be called only once at the application deployment time
    // Or whenever any value is changed in the database, call this method to reload the values.
    public static void updateConfigurationsFromDb(List<ActivityConfiguration> configList){

        for(ActivityConfiguration config:configList){
            configurations.put(config.getConfigName(),config.getConfigValue());
        }
    }

    public static String getValue(String key){
        return configurations.get(key);
    }
}
