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

package eu.europa.ec.fisheries.uvms.activity.message.consumer.bean;

import static eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants.QUEUE_MODULE_ACTIVITY;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;

import eu.europa.ec.fisheries.uvms.activity.message.event.ActivityMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;

/**
 * Created by padhyad on 12/9/2016.
 */
@Stateless
@LocalBean
@Slf4j
public class ActivityMessageServiceBean extends AbstractProducer {

    private static final String MODULE_NAME = "activity";

    public String getModuleName() {
        return MODULE_NAME;
    }

    @Override
    public String getDestinationName() {
        return QUEUE_MODULE_ACTIVITY;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendModuleErrorResponseMessage(@Observes @ActivityMessageErrorEvent EventMessage message) {
    	Connection connection=null;
    	try {
    		            
            connection = getConnectionFactory().createConnection();
            final Session session = JMSUtils.connectToQueue(connection);
            log.info("Sending message back to recipient from Activity Module with correlationId {} on queue: {}", message.getJmsMessage().getJMSMessageID());
            String data = JAXBMarshaller.marshallJaxBObjectToString(message.getFault());
            TextMessage response = session.createTextMessage(data);
            response.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
            session.createProducer(message.getJmsMessage().getJMSReplyTo()).send(response);
        } catch (ActivityModelMarshallException | JMSException e) {
            log.error("Error when returning module activity request", e);
            log.error("[ Error when returning module activity request. ] {} {}", e.getMessage(), e.getStackTrace());
        } finally {
        	JMSUtils.disconnectQueue(connection);
        }
    }
}