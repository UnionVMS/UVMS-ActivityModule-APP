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
import eu.europa.ec.fisheries.schema.movement.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementQuery;
import eu.europa.ec.fisheries.schema.movement.search.v1.RangeCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.RangeKeyType;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKey;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.MovementProducerBean;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.movement.model.mapper.MovementModuleRequestMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Transactional
@Slf4j
public class MovementModuleServiceBean extends ModuleService implements MovementModuleService {

    private static final long ONE_DAY_IN_MILLIS = 24L * 60L * 60L * 1000L;
    private static final String RANGE_CRITERIA_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

    @EJB
    private MovementProducerBean movementProducer;

    @EJB
    private ActivityConsumerBean activityConsumer;

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<MovementType> getMovement(List<String> vesselIds, Instant startDate, Instant endDate) throws ServiceException {
        try {
            MovementQuery movementQuery = new MovementQuery();
            addListCriteria(vesselIds, movementQuery);
            addRangeCriteria(startDate, endDate, movementQuery);
            movementQuery.setExcludeFirstAndLastSegment(true);

            String request = MovementModuleRequestMapper.mapToGetMovementMapByQueryRequest(movementQuery);
            String moduleMessage = movementProducer.sendModuleMessage(request, activityConsumer.getDestination());
            TextMessage response = activityConsumer.getMessage(moduleMessage, TextMessage.class);
            //TODO: Implement this call against new Movement Module!
            /*
            if (response != null && isNotUserFault(response)) {
                List<MovementMapResponseType> mapResponseTypes = MovementModuleResponseMapper.mapToMovementMapResponse(response);
                List<MovementType> movements = new ArrayList<>();
                for (MovementMapResponseType movementMap : mapResponseTypes) {
                    movements.addAll(movementMap.getMovements());
                }
                return movements;
            } else {
                throw new ServiceException("FAILED TO GET DATA FROM MOVEMENT");
            }
            */

            return new ArrayList<>();

        } catch (JMSException e) {
            log.error("Exception in communication with movements", e);
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private void addRangeCriteria(Instant startDate, Instant endDate, MovementQuery movementQuery) {
        RangeCriteria rangeCriteria = new RangeCriteria();
        rangeCriteria.setKey(RangeKeyType.DATE);
        rangeCriteria.setFrom(DateFormatUtils.format(startDate.toEpochMilli() - ONE_DAY_IN_MILLIS, RANGE_CRITERIA_DATE_FORMAT));
        rangeCriteria.setTo(DateFormatUtils.format(endDate.toEpochMilli() + ONE_DAY_IN_MILLIS, RANGE_CRITERIA_DATE_FORMAT));
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
