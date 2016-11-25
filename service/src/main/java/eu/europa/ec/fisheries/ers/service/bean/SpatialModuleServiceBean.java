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
import eu.europa.ec.fisheries.ers.service.SpatialModuleService;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.SpatialProducerBean;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.uvms.spatial.model.exception.SpatialModelMapperException;
import eu.europa.ec.fisheries.uvms.spatial.model.mapper.SpatialModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.spatial.model.mapper.SpatialModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.FilterAreasSpatialRS;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.TextMessage;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by padhyad on 10/11/2016.
 */
@Stateless
@Transactional
@Slf4j
public class SpatialModuleServiceBean extends ModuleService implements SpatialModuleService {

    @EJB
    private SpatialProducerBean spatialProducer;

    @EJB
    private ActivityConsumerBean activityConsumer;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFilteredAreaGeom(Collection<AreaIdentifierType> areaIdentifiers) throws ServiceException {
        try {
            String request = SpatialModuleRequestMapper.mapToFilterAreaSpatialRequest(new ArrayList<>(areaIdentifiers), new ArrayList<AreaIdentifierType>());
            String correlationId = spatialProducer.sendModuleMessage(request, activityConsumer.getDestination());
            TextMessage message = activityConsumer.getMessage(correlationId, TextMessage.class);
            if (message != null && !isUserFault(message)) {
                FilterAreasSpatialRS response = SpatialModuleResponseMapper.mapToFilterAreasSpatialRSFromResponse(message, correlationId);
                return response.getGeometry();
            } else {
                throw new ServiceException("FAILED TO GET DATA FROM SPATIAL");
            }
        } catch (ServiceException | MessageException | SpatialModelMapperException e) {
            log.error("Exception in communication with spatial while retrieving filtered area", e);
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
