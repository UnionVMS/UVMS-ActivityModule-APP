/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.activity.message.consumer.bean;

import eu.europa.ec.fisheries.ers.activity.message.event.ActivityMessageErrorEvent;
import eu.europa.ec.fisheries.ers.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;

/**
 * Created by padhyad on 12/9/2016.
 */
@Stateless
@LocalBean
@Slf4j
public class ActivityErrorMessageServiceBean extends AbstractProducer {

    private static final String MODULE_NAME = "activity";

    public String getModuleName() {
        return MODULE_NAME;
    }

    @Resource(mappedName =  "java:/" + MessageConstants.QUEUE_MODULE_ACTIVITY)
    private Queue destination;

    @Override
    public Destination getDestination() {
        return destination;
    }

    public void sendModuleErrorResponseMessage(@Observes @ActivityMessageErrorEvent EventMessage message) {
    	try {
            sendResponseMessageToSender(message.getJmsMessage(), JAXBMarshaller.marshallJaxBObjectToString(message.getFault()));
            log.info("Sending message back to recipient from Activity Module with correlationId {} on queue: {}", message.getJmsMessage().getJMSMessageID());
        } catch (ActivityModelMarshallException | JMSException  e) {
            log.error("[ Error when returning module activity request. ] {} {}", e);
        }
    }
}
