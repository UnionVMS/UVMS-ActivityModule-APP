/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.ers.service.bean;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBException;

import eu.europa.ec.fisheries.ers.service.exception.ActivityModuleException;
import eu.europa.ec.fisheries.ers.service.mapper.SubscriptionMapper;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.SubscriptionProducerBean;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataRequest;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionPermissionResponse;
import eu.europa.fisheries.uvms.subscription.model.mapper.SubscriptionModuleResponseMapper;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQuery;

@Stateless
@Transactional
@Slf4j
@LocalBean
public class ActivitySubscriptionPermissionChecker {

    @EJB
    private SubscriptionProducerBean subscriptionProducerBean;

    @EJB
    private ActivityConsumerBean activityConsumerBean;

    public SubscriptionPermissionResponse checkPermissionForFaQuery(FAQuery faQuery) throws ActivityModuleException {
        SubscriptionDataRequest subscriptionDataRequest = SubscriptionMapper.mapToSubscriptionDataRequest(faQuery);
        String subscrPermissionRequest;
        try {
            subscrPermissionRequest = JAXBUtils.marshallJaxBObjectToString(subscriptionDataRequest);
        } catch (JAXBException e) {
            throw new ActivityModuleException("JAXBException while trying to unmarshall to SubscriptionDataRequest from Subscription Module!", e);
        }
        return getPermissionFromSubscription(subscrPermissionRequest, activityConsumerBean.getDestination());
    }

    public SubscriptionPermissionResponse checkPermissionForFaReport(FLUXFAReportMessage faReport) throws ActivityModuleException {
        SubscriptionDataRequest subscriptionDataRequest = SubscriptionMapper.mapToSubscriptionDataRequest(faReport);
        String subscrPermissionRequest;
        try {
            subscrPermissionRequest = JAXBUtils.marshallJaxBObjectToString(subscriptionDataRequest);
        } catch (JAXBException e) {
            throw new ActivityModuleException("JAXBException while trying to unmarshall to SubscriptionDataRequest from Subscription Module!", e);
        }
        return getPermissionFromSubscription(subscrPermissionRequest, activityConsumerBean.getDestination());
    }

    public SubscriptionPermissionResponse getPermissionFromSubscription(String subscrPermissionRequest, Destination activityReplyToQueue) throws ActivityModuleException {
        try {
            String corrID = subscriptionProducerBean.sendModuleMessage(subscrPermissionRequest, activityReplyToQueue);
            TextMessage message = activityConsumerBean.getMessage(corrID, TextMessage.class);
            return SubscriptionModuleResponseMapper.mapToSubscriptionPermissionResponse(message.getText());
        } catch (JMSException | JAXBException e) {
            throw new ActivityModuleException("Error while trying to check permissions from Subscription Module!", e);
        }

    }

}
