/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;

@Stateless
@Slf4j
public class ActivityConfigProducerBean extends AbstractProducer implements ConfigMessageProducer {

    @Resource(mappedName =  "java:/" + MessageConstants.QUEUE_CONFIG)
    private Queue destination;

    @Resource(mappedName = "java:/" + MessageConstants.QUEUE_ACTIVITY)
    private Queue replyToQueue;


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendConfigMessage(String textMsg) {
        try {
            return sendModuleMessage(textMsg, replyToQueue);
        } catch (JMSException e) {
            log.error("[ERROR] Error while trying to send message to Config! Check ActivityConfigProducerBeanImpl..");
        }
        return StringUtils.EMPTY;
    }

    @Override
    public Destination getDestination() {
        return destination;
    }
}

