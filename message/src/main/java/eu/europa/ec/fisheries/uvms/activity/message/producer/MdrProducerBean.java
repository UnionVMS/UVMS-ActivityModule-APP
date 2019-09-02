package eu.europa.ec.fisheries.uvms.activity.message.producer;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.Destination;
import javax.jms.Queue;

/**
 * Created by patilva on 06/04/2017.
 */
@Stateless
@LocalBean
public class MdrProducerBean extends AbstractProducer {

    @Resource(mappedName =  "java:/" + MessageConstants.QUEUE_MDR_EVENT)
    private Queue destination;

    @Override
    public Destination getDestination() {
        return destination;
    }
}
