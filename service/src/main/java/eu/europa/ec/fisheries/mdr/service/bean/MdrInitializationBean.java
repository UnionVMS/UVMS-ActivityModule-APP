/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.service.bean;

import eu.europa.ec.fisheries.mdr.domain.ActivityConfiguration;
import eu.europa.ec.fisheries.mdr.domain.MdrStatus;
import eu.europa.ec.fisheries.mdr.domain.constants.AcronymListState;
import eu.europa.ec.fisheries.mdr.mapper.MasterDataRegistryEntityCacheFactory;
import eu.europa.ec.fisheries.mdr.repository.MdrRepository;
import eu.europa.ec.fisheries.mdr.repository.MdrStatusRepository;
import eu.europa.ec.fisheries.mdr.service.MdrSynchronizationService;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static javax.ejb.ConcurrencyManagementType.BEAN;

/**
 * Created by kovian on 29/07/2016.
 */
@Slf4j
@Singleton
@Startup
@ConcurrencyManagement(BEAN)
@DependsOn(value = {"MdrSynchronizationServiceBean", "MdrStatusRepositoryBean", "MdrRepositoryBean"})
public class MdrInitializationBean {

    @EJB
    private MdrSynchronizationService synchBean;

    @EJB
    private MdrSchedulerServiceBean schedulerBean;

    @EJB
    private MdrStatusRepository mdrStatusRepository;

    @EJB
    private MdrRepository mdrRepository;

    /**
     * Method for start up Jobs (Deploy phase)
     *
     *  1. Initializing the acronyms cache.
     *  2. Setting up the scheduler timer for MDR synchronization at start up.
     *  3. Updating the acronyms status table (with eventually new added entities (acronyms)).
     *
     */
    //@PostConstruct
    public void startUpMdrInitializationProcess(){

        log.info("[START] Starting up ActivityModule Initialization..");
        log.info("Going to : \n\t\t1. Initailize acronymsCache..\n\t\t2. Update MDR status table..\n\t\t3. Set up MDR scheduler..  ");

        // 1. Initializing the acronyms cache;
        log.info("\n\n\t\t1. Initailizing acronymsCache..\n");
        MasterDataRegistryEntityCacheFactory.initialize();

        // 2. Updating the acronyms status table (with eventually new added entities (acronyms)).
        log.info("\n\n\t\t2. Updating MDR status table..\n");
        updateMdrStatusTable();

        // 3. Setting up the scheduler timer for MDR synchronization at start up.
        log.info("\n\n\t\t3. Starting up MDR Synchronization Scheduler Initialization..\n");
        // Get the scheduler config from DB;
        ActivityConfiguration storedMdrSchedulerConfig = mdrRepository.getMdrSchedulerConfiguration();
        if(storedMdrSchedulerConfig != null){
            // Set up new scheduler at start up (deploy phase);
            schedulerBean.setUpScheduler(storedMdrSchedulerConfig.getConfigValue());
        }

        log.info("[END] Finished Starting up ActivityModule Initialization..");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void updateMdrStatusTable() {
        List<String> acronymsFromCache   = null;
        List<String> statusListFromDb = null;
        List<MdrStatus> diffList = new ArrayList<MdrStatus>();

        try {
            acronymsFromCache = MasterDataRegistryEntityCacheFactory.getAcronymsList();
            statusListFromDb  = extractStringListFromAcronymStatusList(mdrStatusRepository.getAllAcronymsStatuses());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Error occurred in updateMdrStatusTable() while attempting to get AcronymsList.",e);
        }

        if(CollectionUtils.isNotEmpty(acronymsFromCache)){
            if(CollectionUtils.isNotEmpty(statusListFromDb)) {
                for (String actualCacheAcronym : acronymsFromCache) {
                    if (!statusListFromDb.contains(actualCacheAcronym)) {
                        diffList.add(createNewAcronymSatus(actualCacheAcronym));
                    }
                }
            } else {
                for (String actualCacheAcronym : acronymsFromCache) {
                    diffList.add(createNewAcronymSatus(actualCacheAcronym));
                }
            }
        }

        if (CollectionUtils.isNotEmpty(diffList)) {
            try {
                mdrStatusRepository.saveAcronymsStatusList(diffList);
            } catch (ServiceException e) {
                log.error("Error occurred while attempting to save the AcronymsStatusList.",e);
            }
        } else {
            log.info("No new Acronyms were found for insertion into the MdrStatus Table..");
        }
    }

    private MdrStatus createNewAcronymSatus(String actualCacheAcronym) {
        return new MdrStatus(actualCacheAcronym, "", null, null, AcronymListState.NEWENTRY, true);
    }

    private List<String> extractStringListFromAcronymStatusList(List<MdrStatus> allAcronymsStatuses) {
        List<String> extractedList = new ArrayList<String>();
        for(MdrStatus status : allAcronymsStatuses){
            extractedList.add(status.getObjectAcronym());
        }
        return extractedList;
    }
}
