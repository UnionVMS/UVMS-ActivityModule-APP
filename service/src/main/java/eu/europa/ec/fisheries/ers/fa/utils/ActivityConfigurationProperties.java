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
