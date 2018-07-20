package eu.europa.ec.fisheries.uvms.activity.message.producer;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Created by patilva on 06/04/2017.
 */
@Stateless
@LocalBean
public class MdrProducerBean extends AbstractProducer {

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_MDR_EVENT;
    }
}
