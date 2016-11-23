/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.
This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.mdr.service.bean;

import eu.europa.ec.fisheries.ers.message.exception.ActivityMessageException;
import eu.europa.ec.fisheries.ers.message.producer.ActivityMessageProducer;
import eu.europa.ec.fisheries.mdr.domain.MdrCodeListStatus;
import eu.europa.ec.fisheries.mdr.domain.constants.AcronymListState;
import eu.europa.ec.fisheries.mdr.exception.ActivityCacheInitException;
import eu.europa.ec.fisheries.mdr.exception.ActivityMappingException;
import eu.europa.ec.fisheries.mdr.mapper.MasterDataRegistryEntityCacheFactory;
import eu.europa.ec.fisheries.mdr.mapper.MdrRequestMapper;
import eu.europa.ec.fisheries.mdr.repository.MdrRepository;
import eu.europa.ec.fisheries.mdr.repository.MdrStatusRepository;
import eu.europa.ec.fisheries.mdr.service.MdrSynchronizationService;
import eu.europa.ec.fisheries.mdr.util.GenericOperationOutcome;
import eu.europa.ec.fisheries.mdr.util.OperationOutcome;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author kovian
 *         <p>
 *         EJB that provides the MDR Synchronization Functionality.
 *         1. Methods for synchronizing the MDR lists
 *         2. Method for getting the actual state of the MDR codeLists
 */
@Slf4j
@Stateless
public class MdrSynchronizationServiceBean implements MdrSynchronizationService {

    @EJB
    private MdrRepository mdrRepository;

    @EJB
    private MdrStatusRepository statusRepository;

    @EJB
    private ActivityMessageProducer producer;

    private static final String OBJ_DATA_ALL = "OBJ_DATA_ALL";
    private static final String OBJ_DESC = "OBJ_DESC";
    private static final String INDEX = "INDEX";

    /**
     * Manually startable Job for the MDR Entities synchronising.
     * It will check if the acronym is schedulable before sending a request.
     */
    @Override
    public GenericOperationOutcome manualStartMdrSynchronization() {
        log.info("\n\t\t--->>> STARTING MDR SYNCHRONIZATION \n");
        return extractAcronymsAndUpdateMdr();
    }

    /**
     * Extracts all the available acronyms and for each of those that are updatable
     * sends an update request message to the next module (that will propagate it to - other modules which will propagate it until the - flux node).
     *
     * @return errorContainer
     */
    @Override
    public GenericOperationOutcome extractAcronymsAndUpdateMdr() {
        List<String> updatableAcronyms = extractUpdatableAcronyms(getAvailableMdrAcronyms());
        GenericOperationOutcome errorContainer = updateMdrEntities(updatableAcronyms);
        log.info("\n\t\t---> SYNCHRONIZATION OF MDR ENTITIES FINISHED!\n\n");
        return errorContainer;
    }


    /**
     * Extract a sublist containing all the acronyms that are updatable by the scheduler (schedulable);
     *
     * @param availableAcronyms
     * @return matchList
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private List<String> extractUpdatableAcronyms(List<String> availableAcronyms) {
        List<String> statusListFromDb = extractAcronymsListFromAcronymStatusList(statusRepository.getAllUpdatableAcronymsStatuses());
        List<String> matchList = new ArrayList<>();
        for (String actualCacheAcronym : availableAcronyms) {
            if (statusListFromDb.contains(actualCacheAcronym)) {
                matchList.add(actualCacheAcronym);
            }
        }
        return matchList;
    }


    /**
     * Extracts the objectAcronyms from the list of MdrCodeListStatus objects;
     *
     * @param allUpdatableAcronymsStatuses
     * @return acronymsStrList
     */
    private List<String> extractAcronymsListFromAcronymStatusList(List<MdrCodeListStatus> allUpdatableAcronymsStatuses) {
        List<String> acronymsStrList = new ArrayList<>();
        for (MdrCodeListStatus actStatus : allUpdatableAcronymsStatuses) {
            acronymsStrList.add(actStatus.getObjectAcronym());
        }
        return acronymsStrList;
    }

    /**
     * Updates the given list of mdr entities.
     * The list given as input contains the acronyms.
     * For each acronym a request to flux will be sent and the status (Status Table)
     * will be set to RUNNING or FAILED depending on the outcome of the operation.
     *
     * @param acronymsList
     * @return errorContainer
     */
    @Override
    public GenericOperationOutcome updateMdrEntities(List<String> acronymsList) {
        // For each Acronym send a request object towards Exchange module.
        GenericOperationOutcome errorContainer = new GenericOperationOutcome();
        List<String> existingAcronymsList;
        try {
            existingAcronymsList = MasterDataRegistryEntityCacheFactory.getAcronymsList();
        } catch (ActivityCacheInitException e) {
            log.error("Error while trying to get acronymsList from cache", e);
            return new GenericOperationOutcome(OperationOutcome.NOK, "Error while trying to get acronymsList from cache");
        }

        for (String actualAcronym : acronymsList) {
            log.info("Preparing Request Object for " + actualAcronym + " and sending message to Exchange queue.");
            // Create request object and send message to exchange module
            if (existingAcronymsList.contains(actualAcronym)) {// Acronym exists
                String strReqObj;
                try {
                    strReqObj = MdrRequestMapper.mapMdrQueryTypeToString(actualAcronym, OBJ_DATA_ALL);
                    producer.sendRulesModuleMessage(strReqObj);
                    statusRepository.updateStatusAttemptForAcronym(actualAcronym, AcronymListState.RUNNING, DateUtils.nowUTC().toDate());
                    log.info("Synchronization Request Sent for Entity : " + actualAcronym);
                } catch (ActivityMappingException e) {
                    log.error("Error while trying to map MDRQueryType for acronym {}", actualAcronym, e);
                    errorContainer.addMessage("Error while trying to map MDRQueryType for acronym {} " + actualAcronym);
                    statusRepository.updateStatusAttemptForAcronym(actualAcronym, AcronymListState.FAILED, DateUtils.nowUTC().toDate());
                } catch (ActivityMessageException e) {
                    log.error("Error while trying to send message from Activity to Rules module.", e);
                    errorContainer.addMessage("Error while trying to send message from Activity to Rules module for acronym {} " + actualAcronym);
                    statusRepository.updateStatusAttemptForAcronym(actualAcronym, AcronymListState.FAILED, DateUtils.nowUTC().toDate());
                }
                errorContainer.setIncludedObject(statusRepository.getAllAcronymsStatuses());
            } else {// Acronym does not exist
                log.debug("Couldn't find the acronym'" + actualAcronym + "' in the cachedAcronymsList! Request for said acronym won't be sent to flux!");
                errorContainer.addMessage("The following acronym doesn't exist in the cacheFactory : " + actualAcronym);
            }
        }
        return errorContainer;
    }


    @Override
    public void sendRequestForMdrCodelistsStructures(Collection<String> acronymsList) {
        try {
            List<String> missingAcronyms = java.util.Arrays.asList("FA_BAIT_TYPE", "FA_BFT_SIZE_CATEGORY", "FA_BR", "FA_CATCH_TYPE", "FA_CHARACTERISTIC", "FA_FISHERY", "FA_GEAR_CHARACTERISTIC",
                    "FA_GEAR_PROBLEM", "FA_GEAR_RECOVERY", "FA_GEAR_ROLE", "FA_QUERY_TYPE", "FA_QUERY_PARAMETER", "FA_REASON_ARRIVAL", "FA_REASON_DEPARTURE", "FA_REASON_ENTRY", "FA_REASON_DISCARD", "FA_VESSEL_ROLE",
                    "FARM", "FISH_PRESERVATION", "FISHING_TRIP_TYPE", "FLAP_ID_TYPE", "FLUX_FA_FMC", "FLUX_FA_REPORT_TYPE", "FLUX_FA_TYPE", "FLUX_LOCATION_CHARACTERISTIC", "FLUX_LOCATION_TYPE", "GFCM_GSA",
                    "GFCM_STAT_RECTANGLE", "ICES_STAT_RECTANGLE", "VESSEL_STORAGE_TYPE", "WEIGHT_MEANS");

            for(String actAcron : missingAcronyms){
                String strReqObj = MdrRequestMapper.mapMdrQueryTypeToString(actAcron, OBJ_DESC);
                producer.sendRulesModuleMessage(strReqObj);
            }

        } catch (ActivityMappingException e) {
            log.error("Error while trying to map MDRQueryType for acronym {}", acronymsList, e);
        } catch (ActivityMessageException e) {
            log.error("Error while trying to send message from Activity to Rules module.", e);
        }
    }

    @Override
    public void sendRequestForMdrCodelistsIndex() {
        try {
            String strReqObj = MdrRequestMapper.mapMdrQueryTypeToStringForINDEXServiceType(INDEX);
            producer.sendRulesModuleMessage(strReqObj);
            log.info("Synchronization Request Sent for INDEX ServiceType");
        } catch (ActivityMappingException e) {
            log.error("Error while trying to map MDRQueryType for acronym {}", e);
        } catch (ActivityMessageException e) {
            log.error("Error while trying to send message from Activity to Rules module.", e);
        }
    }

    /**
     * Method that extracts all the available acronyms.
     *
     * @return acronymsList
     */
    @Override
    public List<String> getAvailableMdrAcronyms() {
        List<String> acronymsList = new ArrayList<>();
        try {
            acronymsList = MasterDataRegistryEntityCacheFactory.getAcronymsList();
            if (!CollectionUtils.isEmpty(acronymsList)) {
                log.info("Acronyms exctracted. \nThere were found [ " + acronymsList.size() + " ] acronyms in the MDR domain package.");
            }
            log.info("\n---> Exctracted : " + acronymsList.size() + " acronyms!\n");
        } catch (ActivityCacheInitException exC) {
            log.error("Couldn't extract Entity Acronyms. The following Exception was thrown : \n", exC);
        }
        return acronymsList;
    }

}