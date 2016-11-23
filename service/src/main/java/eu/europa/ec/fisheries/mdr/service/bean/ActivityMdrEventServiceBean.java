/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.service.bean;

import eu.europa.ec.fisheries.mdr.service.ActivityMdrEventService;
import eu.europa.ec.fisheries.mdr.repository.MdrRepository;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFLUXFMDRSyncMessageEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SetFLUXMDRSyncMessageActivityResponse;
import lombok.extern.slf4j.Slf4j;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.BasicAttribute;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.jms.TextMessage;

/**
 *  Observer class listening to events fired from MessageConsumerBean (Activity).
 *  Specifically to GetFLUXFMDRSyncMessageEvent event type.
 *  The message will contain the MDR Entity to be synchronised (As Flux XML Type at this moment).
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
		log.info("-->> Recieved message from FLUX related to MDR Entity Synchronization.");
		// Extract message from EventMessage Object
		try {
			BasicAttribute responseObject = extractFluxResponseFromEvenetMessage(message);
			mdrRepository.updateMdrEntity(responseObject.getResponse());
		} catch (ActivityModelMarshallException e) {
			log.error("ModelMarshallException while trying to extract response from FLUX message! After unmarshalling BasicAttribute resulted NULL! Nothing to persist!", e);
		}
	}

	/**
	 * ResponseType from Flux Response.
	 * 
	 * @param message
	 * @return ResponseType
	 */
	private BasicAttribute extractFluxResponseFromEvenetMessage(EventMessage message) throws ActivityModelMarshallException {
		TextMessage textMessage = null;
		BasicAttribute respType   = null;
		try {
			textMessage = message.getJmsMessage();
			SetFLUXMDRSyncMessageActivityResponse activityResp = JAXBMarshaller.unmarshallTextMessage(textMessage, SetFLUXMDRSyncMessageActivityResponse.class);
			respType    = JAXBMarshaller.unmarshallTextMessage(activityResp.getRequest(), BasicAttribute.class);
		} catch (ActivityModelMarshallException e) {
			log.error("Error while attempting to Unmarshall Flux Response Object (XML MDR Entity) : \n",e);
			throw e;
		}
		log.info("BasicAttribute : /n"+respType);
		return respType;
	}
}
