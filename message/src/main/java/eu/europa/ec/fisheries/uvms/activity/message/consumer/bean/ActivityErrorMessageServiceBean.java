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

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.jms.JMSException;

import eu.europa.ec.fisheries.uvms.activity.message.event.ActivityMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import lombok.extern.slf4j.Slf4j;

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

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_MODULE_ACTIVITY;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendModuleErrorResponseMessage(@Observes @ActivityMessageErrorEvent EventMessage message) {
    	try {
            sendModuleResponseMessage(message.getJmsMessage(), JAXBMarshaller.marshallJaxBObjectToString(message.getFault()));
            log.info("Sending message back to recipient from Activity Module with correlationId {} on queue: {}", message.getJmsMessage().getJMSMessageID());
        } catch (ActivityModelMarshallException | JMSException | MessageException e) {
            log.error("[ Error when returning module activity request. ] {} {}", e);
        }
    }
}