package eu.europa.ec.fisheries.mdr.service.bean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;

import eu.europa.ec.fisheries.mdr.service.ActivityMdrEventService;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFLUXFMDRSyncMessageEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import lombok.extern.slf4j.Slf4j;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.ResponseType;

/**
 * @author kovian
 * 
 *  Observer class listening to events fired from MessageConsumerBean.
 *  Specifically to GetFLUXFMDRSyncMessageEvent event type.
 *  The message will contain the MDR Entity to be synchronised.
 *  
 *  Using the MdrRepository the Entity in question will be stored in the Cache DB.
 *
 */
@Stateless
@Slf4j
public class ActivityMdrEventServiceBean implements ActivityMdrEventService {
	
	@EJB
	private MdrRepository mdrRepository;
	
	@Override
	public void recievedSyncMdrEntityMessage(@Observes @GetFLUXFMDRSyncMessageEvent EventMessage message){
		
		log.info("Recieved message from FLUX related to MDR Entity Synchronization.");
		
		// Extract message from EventMessage Object
		ResponseType responseObject = extractFluxResponseFromEvenetMessage(message);
		mdrRepository.updateMdrEntity(responseObject);
		
		
	}

	/**
	 * ResponseType from Flux Response.
	 * 
	 * @param message
	 * @return ResponseType
	 */
	private ResponseType extractFluxResponseFromEvenetMessage(EventMessage message) {
		// TODO : Implement this part
		return new ResponseType();
	}
}
