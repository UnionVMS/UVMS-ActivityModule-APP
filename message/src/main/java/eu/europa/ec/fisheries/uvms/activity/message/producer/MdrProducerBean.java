package eu.europa.ec.fisheries.uvms.activity.message.producer;

import eu.europa.ec.fisheries.uvms.message.AbstractProducer;
import eu.europa.ec.fisheries.uvms.message.MessageConstants;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.Destination;

/**
 * Created by patilva on 06/04/2017.
 */
@Stateless
@Local
public class MdrProducerBean extends AbstractProducer {

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_MDR_EVENT;
    }
}
