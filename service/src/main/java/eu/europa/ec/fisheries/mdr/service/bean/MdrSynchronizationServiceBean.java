/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.service.bean;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.apache.commons.collections.CollectionUtils;

import eu.europa.ec.fisheries.ers.message.exception.ActivityMessageException;
import eu.europa.ec.fisheries.ers.message.producer.MdrMessageProducer;
import eu.europa.ec.fisheries.mdr.mapper.MasterDataRegistryEntityCacheFactory;
import eu.europa.ec.fisheries.mdr.mapper.MdrRequestMapper;
import eu.europa.ec.fisheries.mdr.service.MdrSynchronizationService;
import eu.europa.ec.fisheries.uvms.activity.message.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.exception.ModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.unqualifieddatatype._13.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._13.NameType;
import un.unece.uncefact.data.standard.unqualifieddatatype._13.TextType;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.BasicAttribute;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.CodeElementType;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.MDRCodeListType;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.ResponseType;

/***
 * EJB that provides the MDR Synchronization Functionality.
 *
 */
@Slf4j
@Singleton
public class MdrSynchronizationServiceBean implements MdrSynchronizationService {

	@EJB
	private MdrRepository mdrRepository;

	@EJB
	private MdrMessageProducer producer;

	private static final String OBJ_DATA_ALL = "OBJ_DATA_ALL";

	/**
	 * Scheduled Job for the extraction of all MDR Entities from FLUX and
	 * storing them in our DB (Cache) providing a faster access to MDR Entities.
	 * (Synchronisation of MDR)
	 * 
	 * @throws InterruptedException
	 * @throws JAXBException
	 * @throws MessageException
	 * @throws JMSException
	 * @throws ServiceException
	 */
	// @Schedule(minute="*", hour="*", persistent=false, info="AUTO_TIMER_0")
	/*
	 * @Schedule(hour="*", minute="*", persistent=false) public void
	 * atSchedule() throws InterruptedException, JAXBException,
	 * MessageException, JMSException, ServiceException {
	 * extractAcronymsAndUpdateMdr(); }
	 */

	/**
	 * Manually startable Job for the MDR Entities synchronising.
	 */
	public void manualStartMdrSynchronization() {	
		log.info("\n\t\t--->>> SYNCHRONIZATION OF MDR ENTITIES INITIALIZED \n");
		extractAcronymsAndUpdateMdr();
	}

	private void extractAcronymsAndUpdateMdr() {
		List<String> acronymsList = null;
		try {
			acronymsList = extractMdrAcronyms();
		} catch (Exception exC) {
			log.error("Couldn't extract Entity Acronyms. The following Exception was thrown : \n" + exC.getMessage());
		}

		// For each Acronym send a request object towards Exchange module.
		if (CollectionUtils.isNotEmpty(acronymsList)) {
			for (String actualAcronym : acronymsList) {
				log.info("Preparing Request Object for " + actualAcronym + " and sending message to Exchange queue.");

				// Create request object and send message to exchange module
				String strReqObj = null;
				try {
					strReqObj = MdrRequestMapper.createNewMDRQueryTypeAndMapItToString(actualAcronym, OBJ_DATA_ALL);
				} catch (ExchangeModelMarshallException | ModelMarshallException e) {
					log.error("Error while trying to map MDRQueryType.");
					e.printStackTrace();
				}				 
				producer.sendRulesModuleMessage(strReqObj);
				log.info("Synchronization Request Sent for Entity : "+actualAcronym);
			}
		}
		log.info("\n\t\t--->>> SYNCHRONIZATION OF MDR ENTITIES FINISHED <<<---\n\n");
	}

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