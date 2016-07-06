/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.message.producer.bean;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang.StringUtils;

import eu.europa.ec.fisheries.ers.message.exception.ActivityMessageException;
import eu.europa.ec.fisheries.ers.message.producer.AbstractMessageProducer;
import eu.europa.ec.fisheries.ers.message.producer.MdrMessageProducer;
import eu.europa.ec.fisheries.uvms.activity.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.activity.message.constants.ModuleQueue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
public class MdrMessageProducerBean extends AbstractMessageProducer implements MdrMessageProducer {
	
    @Resource(lookup = MessageConstants.CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    private Connection connection;

	@Resource(mappedName = MessageConstants.ACTIVITY_MESSAGE_IN_QUEUE)
	private Queue responseQueue;

	@Resource(mappedName = MessageConstants.EXCHANGE_MODULE_QUEUE)
	private Queue exchangeQueue;
	
	@Resource(mappedName = MessageConstants.ERS_MDR_QUEUE)
	private Queue ersMdrQueue;
	
	@Resource(mappedName = MessageConstants.RULES_EVENT_QUEUE)
	private Queue rulesQueue;

	
	/**
	 * Sends a message to Exchange Queue.
	 * 
	 * @param  text (to be sent to the queue)
	 * @return messageID
	 */
	public String sendRulesModuleMessage(String text){
		log.info("Sending Request to Exchange module.");		
		String messageID = StringUtils.EMPTY;
		try {
			messageID = sendModuleMessage(text, ModuleQueue.RULES);
		} catch (ActivityMessageException e) {
			log.error("Error sending message to Exchange Module.");
			e.printStackTrace();
		}
		return messageID;
	}
	
    /**
     * Sends a message to a given Queue.
     * 
     * @param  String text (the message to be sent)
     * @param  ModuleQueue queue
     * @return JMSMessageID
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendModuleMessage(String text, ModuleQueue queue) throws ActivityMessageException {
        try {
            Session session     = getNewSession();
            TextMessage message = session.createTextMessage();
            //message.setJMSReplyTo(responseQueue);
            message.setText(text);
            switch (queue) {
            	case RULES:
            		getProducer(session, rulesQueue).send(message);
            		break;
                case EXCHANGE:
                    getProducer(session, exchangeQueue).send(message);
                    break;
                case ERSMDRPLUGINQUEUE:
                    getProducer(session, ersMdrQueue).send(message);
                    break;
                default:
                    throw new ActivityMessageException("Queue not defined or implemented");
            }

            return message.getJMSMessageID();
        } catch (Exception e) {
            log.error("[ Error when sending data source message. ] {}", e.getMessage());
            throw new ActivityMessageException(e.getMessage());
        }
    }
	
	
    /**
     * Sends a message to the recipient of the message (once a response is recieved)
     * 
     * @param TextMessage requestMessage
     * @param String returnMessage
     * @return void
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendMessageBackToRecipient(TextMessage requestMessage, String returnMessage) throws ActivityMessageException {
        try {
            sendMessage(requestMessage.getJMSReplyTo(), returnMessage, requestMessage.getJMSMessageID());
        } catch (Exception e) {
            log.error("[ Error when sending message. ] {}", e.getMessage());
            throw new ActivityMessageException("[ Error when sending message. ]", e);
        }
    }
	
    /**
     * Creates a MessageProducer for the given destination;
     * 
     * @param session
     * @param destination
     * @return MessageProducer
     * @throws JMSException
     */
    private MessageProducer getProducer(Session session, Destination destination) throws JMSException {
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.setTimeToLive(60000L);
        return producer;
    }
    
    /**
     * Creates a new JMS Session and returns it;
     * 
     * @return Session
     * @throws JMSException
     */
    private Session getNewSession() throws JMSException {
        if (connection == null) {
        	 log.debug("Open connection to JMS broker");
             try {
                 connection = connectionFactory.createConnection();
                 connection.start();
             } catch (JMSException ex) {
                 log.error("Error when open connection to JMS broker");
             }
        }
        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        return session;
    }
}