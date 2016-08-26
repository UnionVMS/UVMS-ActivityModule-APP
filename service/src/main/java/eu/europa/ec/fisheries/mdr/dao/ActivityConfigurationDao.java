/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.dao;

import eu.europa.ec.fisheries.mdr.domain.ActivityConfiguration;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by kovian on 17/08/2016.
 */
@Slf4j
public class ActivityConfigurationDao extends AbstractDAO<ActivityConfiguration> {

    private EntityManager em;
    
    private static final String SELECT_FROM_MDRCONFIG_WHERE_NAME_EQ = "from ActivityConfiguration where configName = ";
    private static final String SCHEDULER_CONFIG_NAME = "MDR_SCHED_CONFIG_NAME";

    public ActivityConfigurationDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    public List<ActivityConfiguration> findAllConfigurations() throws ServiceException {
        return findAllEntity(ActivityConfiguration.class);
    }

    public ActivityConfiguration findConfiguration(String configName){
    	ActivityConfiguration configEntry      = null;
        List<ActivityConfiguration> configList = null;
        try {
            configList = findEntityByHqlQuery(ActivityConfiguration.class, SELECT_FROM_MDRCONFIG_WHERE_NAME_EQ + "'"+configName+"'");
            if(CollectionUtils.isNotEmpty(configList)){
                configEntry =  configList.get(0);
            } else {
                log.error("No configuration found in the db regarding {} ", configName);
            }
        } catch (ServiceException | NullPointerException e) {
            log.error("Error while trying to get Configuration for configName : ", configName, e);
        }
        return configEntry;
    }
    
    public ActivityConfiguration getMdrSchedulerConfiguration(){
    	return findConfiguration(SCHEDULER_CONFIG_NAME);
    }
    
    public void changeMdrSchedulerConfiguration(String newCronExpression) throws ServiceException{
    	ActivityConfiguration newConfig = getMdrSchedulerConfiguration();
        if(newConfig != null){
            newConfig.setConfigValue(newCronExpression);
        } else {
            ActivityConfiguration newToSaveConfig = new ActivityConfiguration(SCHEDULER_CONFIG_NAME, newCronExpression);
            saveOrUpdateEntity(newToSaveConfig);
        }

    }

}