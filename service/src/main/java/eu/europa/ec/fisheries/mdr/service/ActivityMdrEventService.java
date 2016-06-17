package eu.europa.ec.fisheries.mdr.service;

import javax.ejb.Local;
import javax.enterprise.event.Observes;

import eu.europa.ec.fisheries.uvms.activity.message.event.GetFLUXFMDRSyncMessageEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;

@Local
public interface ActivityMdrEventService {

	void recievedSyncMdrEntityMessage(@Observes @GetFLUXFMDRSyncMessageEvent EventMessage message);

}
