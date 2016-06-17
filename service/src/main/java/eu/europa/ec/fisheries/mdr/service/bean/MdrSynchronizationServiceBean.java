package eu.europa.ec.fisheries.mdr.service.bean;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.apache.commons.collections.CollectionUtils;

import eu.europa.ec.fisheries.ers.message.producer.MdrMessageProducer;
import eu.europa.ec.fisheries.mdr.mapper.MasterDataRegistryEntityCacheFactory;
import eu.europa.ec.fisheries.mdr.mapper.MdrRequestMapper;
import eu.europa.ec.fisheries.mdr.service.MdrSynchronizationService;
import eu.europa.ec.fisheries.uvms.exception.ModelMarshallException;
import lombok.extern.slf4j.Slf4j;

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
	 * Manually startable Job for synchronisation of the MDR Entities.
	 */
	public void manualStartMdrSynchronization() {
		extractAcronymsAndUpdateMdr();
	}

	private void extractAcronymsAndUpdateMdr() {

		// Get all the acronyms from the acronyms cache
		log.info("Exctracting the available acronyms.");
		List<String> acronymsList = null;
		try {
			acronymsList = extractMdrAcronyms();
		} catch (Exception e1) {
			log.error("Couldn't extract Entity Acronyms. The following Exception was thrown : \n\n" + e1.getMessage());
		}

		// For each Acronym send a request object towards Exchange module.
		if (CollectionUtils.isNotEmpty(acronymsList)) {
			for (String actualAcronym : acronymsList) {
				log.info("Preparing Request Object for " + actualAcronym + " and sending message to Exchange queue.");

				// Create request object and send message to exchange module
				String strReqObj = null;
				try {
					strReqObj = MdrRequestMapper.mapMDRQueryTypeToString(actualAcronym, OBJ_DATA_ALL);
				} catch (ModelMarshallException e) {
					log.error("Error while trying to map MDRQueryType.");
					e.printStackTrace();
				}
				producer.sendExchangeModuleMessage(strReqObj);
			}
		}
	}

	private List<String> extractMdrAcronyms() throws Exception {
		List<String> acronymsList = null;
		try {
			acronymsList = MasterDataRegistryEntityCacheFactory.getAcronymsList();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new Exception(e);
		}
		if (!CollectionUtils.isEmpty(acronymsList)) {
			log.info("Acronyms exctracted. There were found [" + acronymsList.size()
					+ "] acronyms in the MDR domain package.");
		}
		return acronymsList;
	}
}
