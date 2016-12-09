package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.uvms.mdr.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.message.AbstractConsumer;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.Destination;

/**
 * Created by kovian on 15/09/2016.
 */

@Stateless
@Local
public class AssetsMessageConsumerBean extends AbstractConsumer {

    @Resource(mappedName = MessageConstants.ACTIVITY_MESSAGE_QUEUE)
    private Destination destination;

    @Override
    public Destination getDestination() {
        return destination;
    }
}