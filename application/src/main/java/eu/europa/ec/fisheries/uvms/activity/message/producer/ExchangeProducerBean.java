/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.message.producer;

import eu.europa.ec.fisheries.schema.exchange.module.v1.EfrReportSaved;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import java.util.UUID;

@Stateless
@LocalBean
public class ExchangeProducerBean extends AbstractProducer {

    @Resource(mappedName = "java:/" + MessageConstants.QUEUE_EXCHANGE_EVENT)
    private Queue destination;

    @Override
    public Destination getDestination() {
        return destination;
    }

    public void sendEfrReportSavedAckToExchange(UUID ackResponseMessageId) throws JMSException {
        if (ackResponseMessageId == null) {
            return;
        }
        EfrReportSaved efrReportSaved = new EfrReportSaved();
        efrReportSaved.setMethod(ExchangeModuleMethod.EFR_REPORT_SAVED);
        efrReportSaved.setRefGuid(ackResponseMessageId.toString());

        String xml = JAXBMarshaller.marshallJaxBObjectToString(efrReportSaved);
        sendMessageToSpecificQueueWithFunction(xml, getDestination(), null, efrReportSaved.getMethod().toString(), null);
    }
}
