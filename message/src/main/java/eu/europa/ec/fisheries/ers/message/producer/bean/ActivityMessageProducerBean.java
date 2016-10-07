/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.message.producer.bean;

import eu.europa.ec.fisheries.ers.message.exception.ActivityMessageException;
import eu.europa.ec.fisheries.ers.message.producer.AbstractMessageProducer;
import eu.europa.ec.fisheries.ers.message.producer.ActivityMessageProducer;
import eu.europa.ec.fisheries.uvms.activity.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.activity.message.constants.ModuleQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.*;

@Slf4j
@Stateless
public class ActivityMessageProducerBean extends AbstractMessageProducer implements ActivityMessageProducer {
	
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

    @Resource(mappedName = MessageConstants.ASSET_MODULE_QUEUE)
    private Queue assetsQueue;

    @Resource(mappedName = MessageConstants.ACTIVITY_MESSAGE_QUEUE)
    private Queue activitySyncQueue;

	
	/**
	 * Sends a message to Exchange Queue.
	 * 
	 * @param  text (to be sent to the queue)
	 * @return messageID
	 */
    @Override
	public String sendRulesModuleMessage(String text) throws ActivityMessageException {
		log.info("Sending Request to Exchange module.");		
		String messageID = StringUtils.EMPTY;
		try {
			messageID = sendModuleMessage(text, ModuleQueue.RULES);
		} catch (ActivityMessageException e) {
			log.error("Error sending message to Exchange Module.",e);
            throw e;
		}
		return messageID;
	}
	
    /**
     * Sends a message to a given Queue.
     *
     * @param  text (the message to be sent)
     * @param  queue
     * @return JMSMessageID
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendModuleMessage(String text, ModuleQueue queue) throws ActivityMessageException {
        try {
            Session session     = getNewSession();
            TextMessage message = session.createTextMessage();
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
            log.error("[ Error when sending data source message. ] {}", e);
            throw new ActivityMessageException(e.getMessage());
        } finally {
            disconnectQueue();
        }
    }

    /**
     * Sends a message to Assets Module Queue
     *
     * @param text
     * @return
     * @throws ActivityMessageException
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendAssetsModuleSynchronousMessage(String text) throws ActivityMessageException {
        try {
            Session session     = getNewSession();
            TextMessage message = session.createTextMessage();
            message.setJMSReplyTo(activitySyncQueue);
            message.setText(text);
            getProducer(session, assetsQueue).send(message);
            return message.getJMSMessageID();
        } catch (Exception e) {
            log.error("[ Error when sending data source message. ] {}", e);
            throw new ActivityMessageException(e.getMessage());
        } finally {
            disconnectQueue();
        }
    }
	
    /**
     * Sends a message to the recipient of the message (once a response is recieved)
     * 
     * @param requestMessage
     * @param returnMessage
     * @return void
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendMessageBackToRecipient(TextMessage requestMessage, String returnMessage) throws ActivityMessageException {
        try {
            sendMessage(requestMessage.getJMSReplyTo(), returnMessage, requestMessage.getJMSMessageID());
        } catch (Exception e) {
            log.error("[ Error when sending message. ] {}", e);
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
                 return connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
             } catch (JMSException | NullPointerException ex) {
                 log.error("Error when opening connection to JMS broker", ex);
                 throw new JMSException(ex.getMessage());
             }
        }
        return connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
    }

    /**
     * Disconnects from the queue this Producer is connected to.
     *
     */
    private void disconnectQueue() {
        try {
            if(connection != null){
                connection.stop();
                connection.close();
            }
            log.debug("Succesfully disconnected from FLUX BRIDGE Remote queue.");
        } catch (JMSException e) {
            log.error("[ Error when stopping or closing JMS queue ] {}", e);
        }
    }
}