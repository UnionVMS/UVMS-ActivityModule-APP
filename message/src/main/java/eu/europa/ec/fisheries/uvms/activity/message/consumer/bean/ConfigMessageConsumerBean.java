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
package eu.europa.ec.fisheries.uvms.activity.message.consumer.bean;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.activity.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageConsumer;
import eu.europa.ec.fisheries.uvms.message.JMSUtils;

@Stateless
public class ConfigMessageConsumerBean implements ConfigMessageConsumer {

    final static Logger LOG = LoggerFactory.getLogger(ConfigMessageConsumerBean.class);

    private final static long TIMEOUT = 30000;

    private ConnectionFactory connectionFactory;

    private Queue responseActivityQueue;

    @PostConstruct
    private void init() {
    	connectionFactory = JMSUtils.lookupConnectionFactory();
    	responseActivityQueue = JMSUtils.lookupQueue(MessageConstants.ACTIVITY_MESSAGE_QUEUE);
    }

    public <T> T getMessage(String correlationId, Class type) throws ConfigMessageException {
    	if (correlationId == null || correlationId.isEmpty()) {
    		throw new ConfigMessageException("No CorrelationID provided!");
    	}
    	
    	Connection connection=null;
    	try {
    		            
            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);

            T response = (T) session.createConsumer(responseActivityQueue, "JMSCorrelationID='" + correlationId + "'").receive(TIMEOUT);
            
            if (response == null) {
                throw new ConfigMessageException("[ Timeout reached or message null in MobileTerminalMessageConsumer. ]");
            }

            return response;
        } catch (Exception e) {
            LOG.error("[ Error when consuming message. ] {}", e.getMessage());
            throw new ConfigMessageException("Error when retrieving message: " + e.getMessage());
        } finally {
        	JMSUtils.disconnectQueue(connection);
        }
    }

    @Override
    public <T> T getConfigMessage(String correlationId, Class type) throws ConfigMessageException {
        try {
            return getMessage(correlationId, type);
        }
        catch (ConfigMessageException e) {
            LOG.error("[ Error when getting config message. ] {}", e.getMessage());
            throw new ConfigMessageException(e.getMessage());
        }
    }

}