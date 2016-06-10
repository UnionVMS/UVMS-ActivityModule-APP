package eu.europa.ec.fisheries.ers.service;

import eu.europa.ec.fisheries.uvms.activity.message.event.GetFLUXFAReportMessageEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;

import javax.ejb.Local;
import javax.enterprise.event.Observes;

/**
 * Created by sanera on 07/06/2016.
 */
@Local
public interface EventService {
    public void GetFLUXFAReportMessage(@Observes @GetFLUXFAReportMessageEvent EventMessage message);
}
