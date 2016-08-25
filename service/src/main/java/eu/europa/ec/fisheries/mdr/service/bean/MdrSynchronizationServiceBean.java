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
import eu.europa.ec.fisheries.ers.message.producer.MdrMessageProducer;
import eu.europa.ec.fisheries.mdr.domain.MdrStatus;
import eu.europa.ec.fisheries.mdr.mapper.MasterDataRegistryEntityCacheFactory;
import eu.europa.ec.fisheries.mdr.mapper.MdrRequestMapper;
import eu.europa.ec.fisheries.mdr.repository.MdrRepository;
import eu.europa.ec.fisheries.mdr.repository.MdrStatusRepository;
import eu.europa.ec.fisheries.mdr.service.MdrSynchronizationService;
import eu.europa.ec.fisheries.uvms.activity.message.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.exception.ModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.unqualifieddatatype._13.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._13.NameType;
import un.unece.uncefact.data.standard.unqualifieddatatype._13.TextType;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.*;

import javax.ejb.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author kovian
 *
 * EJB that provides the MDR Synchronization Functionality.
 *  1. Methods for synchronizing the MDR lists
 *  2. Method for getting the actual state of the MDR codeLists
 */
@Slf4j
@Stateless
public class MdrSynchronizationServiceBean implements MdrSynchronizationService {

	@EJB
	private MdrRepository mdrRepository;

	@EJB
	private MdrStatusRepository statusRepository;

	@EJB
	private MdrMessageProducer producer;

	private static final String OBJ_DATA_ALL = "OBJ_DATA_ALL";

	/**
	 * Manually startable Job for the MDR Entities synchronising.
	 */
	public boolean manualStartMdrSynchronization() {
		log.info("\n\t\t--->>> STARTING MDR SYNCHRONIZATION \n");
		return extractAcronymsAndUpdateMdr();
	}

	/**
	 * Extracts all the available acronyms and for each of those that are updatable
	 * sends an update request message to the next module (that will propagate it to - other modules which will propagate it until the - flux node).
	 */
	@Override
	public boolean extractAcronymsAndUpdateMdr() {
		List<String> updatableAcronyms = extractUpdatableAcronyms(getAvailableMdrAcronyms());
		boolean error = updateMdrEntities(updatableAcronyms);
		log.info("\n\t\t---> SYNCHRONIZATION OF MDR ENTITIES FINISHED!\n\n");
		return error;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private List<String> extractUpdatableAcronyms(List<String> availableAcronyms) {

		List<String> statusListFromDb =  extractAcronymsListFromAcronymStatusList(statusRepository.getAllUpdatableAcronymsStatuses());
		List<String> matchList = new ArrayList<String>();

		if(CollectionUtils.isNotEmpty(availableAcronyms)){
			if(CollectionUtils.isNotEmpty(statusListFromDb)) {
				for (String actualCacheAcronym : availableAcronyms) {
					if (statusListFromDb.contains(actualCacheAcronym)) {
						matchList.add(actualCacheAcronym);
					}
				}
			}
		}
		return matchList;
	}

	private List<String> extractAcronymsListFromAcronymStatusList(List<MdrStatus> allUpdatableAcronymsStatuses) {
		List<String> updatableList = new ArrayList<String>();
		for(MdrStatus actStatus : allUpdatableAcronymsStatuses){
			updatableList.add(actStatus.getObjectAcronym());
		}
		return updatableList;
	}

	/**
	 * Updates the given list of mdr entities.
	 * The list given as input contains the acronyms.
	 *
	 * @param acronymsList
	 * @return error
     */
	@Override
	public boolean updateMdrEntities(List<String> acronymsList) {
		// For each Acronym send a request object towards Exchange module.
		boolean error = false;
		if (CollectionUtils.isNotEmpty(acronymsList)) {
			for (String actualAcronym : acronymsList) {
				log.info("Preparing Request Object for " + actualAcronym + " and sending message to Exchange queue.");
				// Create request object and send message to exchange module
				String strReqObj = null;
				try {
					strReqObj = MdrRequestMapper.mapMdrQueryTypeToString(actualAcronym, OBJ_DATA_ALL);
				} catch (ExchangeModelMarshallException | ModelMarshallException e) {
					log.error("Error while trying to map MDRQueryType."+e.getMessage());
					error = true;
				}
				producer.sendRulesModuleMessage(strReqObj);
				log.info("Synchronization Request Sent for Entity : "+actualAcronym);
			}
		} else {
			error = true;
		}
		return error;
	}

	/**
	 *
	 * Method that extracts all the available acronyms.
	 *
	 * @return acronymsList
     */
	@Override
	public List<String> getAvailableMdrAcronyms() {
		List<String> acronymsList = null;
		try {
			acronymsList = extractMdrAcronyms();
			log.info("\n---> Exctracted : "+acronymsList.size()+" acronyms!\n");
		} catch (Exception exC) {
			log.error("Couldn't extract Entity Acronyms. The following Exception was thrown : \n" + exC.getMessage());
		}
		return acronymsList;
	}

	/**
	 * Extracts all the available acronyms. (from the domain package - reflection)
	 * 
	 * @return acronymsList
	 * @throws Exception
	 */
	private List<String> extractMdrAcronyms() throws Exception {
		List<String> acronymsList = null;
		try {
			// Get all the acronyms from the acronyms cache
			log.info("Exctracting the available acronyms.");
			acronymsList = MasterDataRegistryEntityCacheFactory.getAcronymsList();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new Exception(e);
		}
		if (!CollectionUtils.isEmpty(acronymsList)) {
			log.info("Acronyms exctracted. \nThere were found [ " + acronymsList.size()+ " ] acronyms in the MDR domain package.");
		}
		return acronymsList;
	}

	@Override
	public void sendMockedMessageToERSMDRQueue() {
		
		try {
			producer.sendModuleMessage(mockAndMarshallResponse(), ModuleQueue.ERSMDRPLUGINQUEUE);
		} catch (ActivityMessageException | ExchangeModelMarshallException e) {
			log.error(e.getMessage());
		}
	}



	private String mockAndMarshallResponse() throws ExchangeModelMarshallException {

		ResponseType respType       = new ResponseType();
		
		IDType objAcronym = new IDType();
		objAcronym.setValue("ACTION_TYPE");
		
		FieldType field_1= new FieldType();
		field_1.setFieldName(new NameType("DESCRIPTION", "EN"));
		TextType value = new TextType();
		value.setLanguageID("EN");
		value.setValue("English description of Action Type Entity.");
		field_1.setFieldValue(value);
		
		FieldType field_2= new FieldType();
		field_2.setFieldName(new NameType("CODE", "EN"));
		TextType valu = new TextType();
		valu.setLanguageID("EN");
		valu.setValue("ACT_TYPE");
		field_2.setFieldValue(valu);
		
		CodeElementType entity_1 = new CodeElementType();
		entity_1.setFields(Arrays.asList(field_1, field_2));
		
		CodeElementType entity_2 = new CodeElementType();
		entity_2.setFields(Arrays.asList(field_1, field_2));
		
		List<CodeElementType> entitiesList = new ArrayList<CodeElementType>();
		entitiesList.addAll(Arrays.asList(entity_1, entity_2));

		MDRCodeListType mdrCodeListType = new MDRCodeListType(objAcronym, null, null, null, null, null, entitiesList);
		
		respType.setMDRCodeList(mdrCodeListType);
		
		BasicAttribute respRootType = new BasicAttribute();
		respRootType.setResponse(respType);

		return JAXBMarshaller.marshallJaxBObjectToString(respRootType);
	}
}