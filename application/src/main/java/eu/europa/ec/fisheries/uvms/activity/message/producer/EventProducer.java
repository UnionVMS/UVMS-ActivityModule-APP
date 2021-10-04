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

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Topic;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FishingActivityUtilsMapper;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractTopicProducer;

@RequestScoped
public class EventProducer extends AbstractTopicProducer {

    private static final Logger LOG = LoggerFactory.getLogger(EventProducer.class);

    private Jsonb jsonb = JsonbBuilder.create();

    @Resource(mappedName = "java:/" + MessageConstants.EVENT_STREAM_TOPIC)
    private Topic destination;

    @Inject
    FishingActivityUtilsMapper fishingActivityUtilsMapper;

    @Override
    public Destination getDestination() {
        return destination;
    }

    public void sendActivityEvent(FluxFaReportMessageEntity reportMessage) {
        try {
            for (FaReportDocumentEntity faDocument : reportMessage.getFaReportDocuments()) {
                for (FishingActivityEntity activity : faDocument.getFishingActivities()) {
                    String message = jsonb.toJson(fishingActivityUtilsMapper.mapToFishingActivityReportDTO(activity));
                    sendMessageToEventStream(message, "Activity", null, null);
                }
            }
        } catch (JMSException e) {
            LOG.error("Could not send Activity event", e);
        }
    }
}
