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

package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.service.ModuleService;
import eu.europa.ec.fisheries.ers.service.MovementModuleService;
import eu.europa.ec.fisheries.schema.movement.search.v1.*;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.MovementProducerBean;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.uvms.movement.model.exception.ModelMapperException;
import eu.europa.ec.fisheries.uvms.movement.model.mapper.MovementModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.movement.model.mapper.MovementModuleResponseMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by padhyad on 10/11/2016.
 */
@Stateless
@Transactional
@Slf4j
public class MovementModuleServiceBean extends ModuleService implements MovementModuleService {

    @EJB
    private MovementProducerBean movementProducer;

    @EJB
    private ActivityConsumerBean activityConsumer;

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<MovementType> getMovement(List<String> vesselIds, Date startDate, Date endDate) throws ServiceException {

        try {
            MovementQuery movementQuery = new MovementQuery();
            addListCriteria(vesselIds, movementQuery);
            addRangeCriteria(startDate, endDate, movementQuery);
            movementQuery.setExcludeFirstAndLastSegment(true);

            String request = MovementModuleRequestMapper.mapToGetMovementMapByQueryRequest(movementQuery);
            String moduleMessage = movementProducer.sendModuleMessage(request, activityConsumer.getDestination());
            TextMessage response = activityConsumer.getMessage(moduleMessage, TextMessage.class);
            if (response != null && !isUserFault(response)) {
                List<MovementMapResponseType> mapResponseTypes = MovementModuleResponseMapper.mapToMovementMapResponse(response);
                List<MovementType> movements = new ArrayList<>();
                for (MovementMapResponseType movementMap : mapResponseTypes) {
                    movements.addAll(movementMap.getMovements());
                }
                return movements;
            } else {
                throw new ServiceException("FAILED TO GET DATA FROM MOVEMENT");
            }
        } catch (ServiceException | MessageException | JMSException | ModelMapperException e) {
            log.error("Exception in communication with movements", e);
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private void addRangeCriteria(Date startDate, Date endDate, MovementQuery movementQuery) {
        RangeCriteria rangeCriteria = new RangeCriteria();
        rangeCriteria.setKey(RangeKeyType.DATE);
        rangeCriteria.setFrom(DateFormatUtils.format(DateUtils.addDays(startDate, -1), "yyyy-MM-dd HH:mm:ss Z"));
        rangeCriteria.setTo(DateFormatUtils.format(DateUtils.addDays(endDate, 1), "yyyy-MM-dd HH:mm:ss Z"));
        movementQuery.getMovementRangeSearchCriteria().add(rangeCriteria);
    }

    private void addListCriteria(List<String> vesselIds, MovementQuery movementQuery) {
        for (String vesselId : vesselIds) {
            ListCriteria listCriteria = new ListCriteria();
            listCriteria.setKey(SearchKey.CONNECT_ID);
            listCriteria.setValue(vesselId);
            movementQuery.getMovementSearchCriteria().add(listCriteria);
        }
    }
}
