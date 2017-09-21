/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.activity.message.producer;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.activity.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.config.constants.ConfigConstants;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import eu.europa.ec.fisheries.uvms.message.JMSUtils;

@Stateless
public class ConfigMessageProducerBean implements ConfigMessageProducer {

	private Queue responseQueue;
	private Queue configQueue;
	private ConnectionFactory connectionFactory;

	final static Logger LOG = LoggerFactory.getLogger(ConfigMessageProducerBean.class);

	@PostConstruct
	public void init() {
    	connectionFactory = JMSUtils.lookupConnectionFactory();
		configQueue = JMSUtils.lookupQueue(ConfigConstants.CONFIG_MESSAGE_IN_QUEUE);
		responseQueue = JMSUtils.lookupQueue(MessageConstants.ACTIVITY_MESSAGE_QUEUE);
	}


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String sendModuleMessage(String text, Queue queue) throws ConfigMessageException {
		Connection connection = null;
		try {
			connection = connectionFactory.createConnection();
			final Session session = JMSUtils.connectToQueue(connection);

			TextMessage message = session.createTextMessage();
			message.setJMSReplyTo(responseQueue);
			message.setText(text);

			getProducer(session, queue).send(message);
			
			return message.getJMSMessageID();
		} catch (Exception e) {
			LOG.error("[ Error when sending data source message. ] {}", e.getMessage());
			throw new ConfigMessageException(e.getMessage());
		} finally {
			JMSUtils.disconnectQueue(connection);
		}
	}

	@Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String sendConfigMessage(String text) throws ConfigMessageException {
		try {
			return sendModuleMessage(text, configQueue);
		} catch (ConfigMessageException e) {
			LOG.error("[ Error when sending config message. ] {}", e.getMessage());
			throw new ConfigMessageException(e.getMessage());
		}
	}

	private javax.jms.MessageProducer getProducer(Session session, Destination destination) throws JMSException {
		javax.jms.MessageProducer producer = session.createProducer(destination);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		producer.setTimeToLive(60000L);
		return producer;
	}
}