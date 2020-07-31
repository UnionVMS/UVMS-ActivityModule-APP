/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityEventQueueConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.ExchangeProducerBean;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.*;
import javax.transaction.Transactional;

@Stateless
@LocalBean
@Slf4j
@Transactional
public class ExchangeServiceBean {

    @EJB
    private ExchangeProducerBean exchangeProducerBean;

    @EJB
    private ActivityEventQueueConsumerBean eventQueueConsumerBean;

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public void updateExchangeMessage(String exchangeLogGuid, Exception exception) {
       try {
           String statusMsg = ExchangeModuleRequestMapper.createUpdateLogStatusRequest(exchangeLogGuid, exception);
           log.debug("Message to exchange to update status : {}", statusMsg);
           exchangeProducerBean.sendModuleMessage(statusMsg, eventQueueConsumerBean.getDestination());
        } catch (ExchangeModelMarshallException | MessageException e) {
            log.error("Could not update message status to technical business error with exchangeLogGuid "+exchangeLogGuid, e);
        }
    }
}
