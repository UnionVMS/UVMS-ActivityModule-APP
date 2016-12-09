/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.message.producer;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;


import eu.europa.ec.fisheries.uvms.mdr.message.exception.ActivityMessageException;
import eu.europa.ec.fisheries.uvms.mdr.message.constants.MessageConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractMessageProducer {

	    @Resource(lookup = MessageConstants.CONNECTION_FACTORY)
	    private ConnectionFactory connectionFactory;

	    /**
	     *
	     * Send a "fire and forget" message to a recipient
	     *
	     * @param toQueue The destinsation of the response
	     * @param textMessag The actual message as a String representation of an XML
	     * @param deliveryMode The delivery mode to use
	     * @param defultPriority The priority for this message
	     * @param timeToLive The message's lifetime (in milliseconds)
	     * @return
	     * @throws MovementMessageException
	     */
	    protected String sendMessage(Destination toQueue, Destination responseQueue, String textMessag, int deliveryMode, int defultPriority, long timeToLive) throws ActivityMessageException {
	        return sendMessage(toQueue, responseQueue, textMessag, null, deliveryMode, defultPriority, timeToLive);
	    }

	    /**
	     * Sends a response message to a reciever. The corralationId is the
	     * JMSMessage id provided in the message this metod responds to.
	     *
	     * @param responseQueue The destinsation of the response
	     * @param textMessage The actual message as a String representation of an
	     * XML
	     * @param correlationId The correlationId to set on the message that is
	     * returned
	     * @return The JMSMessage id of the sent message
	     * @throws MovementMessageException
	     */
	    protected String sendMessage(Destination responseQueue, String textMessage, String correlationId) throws ActivityMessageException {
	        return sendMessage(responseQueue, null, textMessage, correlationId, null, null, null);
	    }

	    /**
	     *
	     * Sends a JS message to a recipient and sets teh expected reponse queue
	     *
	     * @param toQueue The destinsation of the message
	     * @param replyQueue The destination that shis message should respond to
	     * when arriving at the toQueue
	     * @param textMessage The actual message as a String representation of an
	     * XML
	     * @return The JMSMessage id of the sent message
	     * @throws MovementMessageException
	     */
	    protected String sendMessage(Destination toQueue, Destination replyQueue, String textMessage) throws ActivityMessageException {
	        return sendMessage(toQueue, replyQueue, textMessage, null, null, null, null);
	    }

	    /**
	     *
	     * Sends a message to a JMS destination
	     *
	     * @param toQueue The destinsation of the message
	     * @param replyQueue The destination that shis message should respond to
	     * when arriving at the toQueue
	     * @param textMessage The actual message as a String representation of an
	     * XML
	     * @param correlationId The correlationId to set on the message that is
	     * returned
	     * @param deliveryMode The delivery mode to use
	     * @param defaultPriority The priority for this message
	     * @param timetoLive The message's lifetime (in milliseconds)
	     * @return The JMSMessage id of the sent message
	     * @throws MovementMessageException
	     */
	    private String sendMessage(Destination toQueue, Destination replyQueue, String textMessage, String correlationId, Integer deliveryMode, Integer defaultPriority, Long timetoLive) throws ActivityMessageException {

	        try {

	            log.info("[ Sending message to recipient on queue ] {}", toQueue);

	            Connection connection = createConnection();
	            Session session = getSession(connection);

	            TextMessage message = session.createTextMessage();
	            message.setText(textMessage);
	            message.setJMSReplyTo(replyQueue);
	            message.setJMSDestination(toQueue);
	            message.setJMSCorrelationID(correlationId);

	            if (deliveryMode != null && defaultPriority != null && timetoLive != null) {
	                getProducer(session, toQueue).send(message, deliveryMode, defaultPriority, timetoLive);
	            } else {
	                getProducer(session, toQueue).send(message);
	            }

	            session.close();

	            if (connection != null) {
	                connection.stop();
	                connection.close();
	            }

	            return message.getJMSMessageID();
	        } catch (JMSException ex) {
	            throw new ActivityMessageException("Error when sending message or closing JMS queue", ex);
	        }
	    }

	    /**
	     * Gets the session that will be used to send a JMS message
	     *
	     * @param connection The created connection that will be used for the
	     * session
	     * @return The newly created session
	     * @throws JMSException
	     */
	    protected Session getSession(Connection connection) throws JMSException {
	        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	        connection.start();
	        return session;
	    }

	    /**
	     * Creates a connection from the connection factory provided by the
	     * application server.
	     *
	     * @return The newly created connection
	     * @throws JMSException
	     */
	    protected Connection createConnection() throws JMSException {
	        return connectionFactory.createConnection();
	    }

	    private javax.jms.MessageProducer getProducer(Session session, Destination destination) throws JMSException {
	        javax.jms.MessageProducer producer = session.createProducer(destination);
	        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	        producer.setTimeToLive(60000L);
	        return producer;
	    }
}