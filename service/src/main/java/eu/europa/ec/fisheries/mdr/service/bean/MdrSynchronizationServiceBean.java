package eu.europa.ec.fisheries.mdr.service.bean;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import javax.xml.rpc.ServiceException;

import eu.europa.ec.fisheries.ers.message.producer.MdrMessageProducer;
import eu.europa.ec.fisheries.mdr.mapper.MasterDataRegistryEntityCacheFactory;
import eu.europa.ec.fisheries.mdr.mapper.MdrRequestMapper;
import eu.europa.ec.fisheries.mdr.service.MdrSynchronizationService;
import eu.europa.ec.fisheries.uvms.exception.ModelMarshallException;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.ResponseType;
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
     * Scheduled Job for the extraction of all MDR Entities from FLUX and storing them
     * in our DB (Cache) providing a faster access to MDR Entities. (Synchronisation of MDR)
     * 
     * @throws InterruptedException
     * @throws JAXBException
     * @throws MessageException
     * @throws JMSException
     * @throws ServiceException
     */
    //@Schedule(minute="*", hour="*", persistent=false, info="AUTO_TIMER_0")
	/*@Schedule(hour="*", minute="*", persistent=false)
    public void atSchedule() throws InterruptedException, JAXBException, MessageException, JMSException, ServiceException {    	
    	extractAcronymsAndUpdateMdr();    	
    } */

	
    /**
     *  Manually startable Job for synchronisation of the MDR Entities.
     */
    public void manualStartMdrSynchronization(){
    	extractAcronymsAndUpdateMdr();
    }
	    
    private void extractAcronymsAndUpdateMdr() {
    	
		// Get all the acronyms from the acronyms cache
    	List<String> acronymsList = extractMdrAcronyms();
    	
    	// For each Acronym get the response from FLUX and store it in the "Cache DB" deleting the previous entries.   	
    	for(String actualAcronym : acronymsList){
    		log.info("Preparing Request Object for "+actualAcronym+" and sending message to Exchange queue.");
    		
    		// Create request object and send message to exchange module queue and wait for response
    		String strReqObj = null;
    		try {
				strReqObj = MdrRequestMapper.mapMDRQueryTypeToString(actualAcronym, OBJ_DATA_ALL);
			} catch (ModelMarshallException e) {
				log.error("Error while trying to map MDRQueryType.");
				e.printStackTrace();
			}
    		
    		String messageId = producer.sendExchangeModuleMessage(strReqObj);
    		// consumer.getMessage(correlationID);
    		
    		// Saving the received Entity Rows to the Cache DB
        	ResponseType response = null;
        	mdrRepository.updateMdrEntity(response);
    	}
	}


	private List<String> extractMdrAcronyms() {
		List<String> acronymsList = null;
    	try {
			acronymsList = MasterDataRegistryEntityCacheFactory.getAcronymsList();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			log.error("Error while trying to get the acronyms List from cache.");
			e.printStackTrace();
		}
		return acronymsList;
	}
}
