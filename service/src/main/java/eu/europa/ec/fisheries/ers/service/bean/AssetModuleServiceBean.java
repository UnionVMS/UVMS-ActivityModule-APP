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

import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.utils.VesselTypeAssetQueryEnum;
import eu.europa.ec.fisheries.ers.service.AssetModuleService;
import eu.europa.ec.fisheries.ers.service.ModuleService;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.AssetProducerBean;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.TextMessage;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by padhyad on 10/12/2016.
 */
@Stateless
@Transactional
@Slf4j
public class AssetModuleServiceBean extends ModuleService implements AssetModuleService {

    @EJB
    private AssetProducerBean assetProducer;

    @EJB
    private ActivityConsumerBean activityConsumer;

    @Override
    public List<String> getAssetGuids(Collection<VesselIdentifierEntity> vesselIdentifiers) throws ServiceException {
        try {
            String request = AssetModuleRequestMapper.createAssetListModuleRequest(createAssetListQuery(vesselIdentifiers));
            String correlationId = assetProducer.sendModuleMessage(request, activityConsumer.getDestination());
            TextMessage response = activityConsumer.getMessage(correlationId, TextMessage.class);
            if (response != null && !isUserFault(response)) {
                List<Asset> assets = AssetModuleResponseMapper.mapToAssetListFromResponse(response, correlationId);
                List<String> assetGuids = new ArrayList<>();
                for (Asset asset : assets) {
                    assetGuids.add(asset.getAssetId().getGuid());
                }
                return assetGuids;
            } else {
                throw new ServiceException("FAILED TO GET DATA FROM ASSET");
            }
        }  catch (ServiceException | MessageException | AssetModelMapperException e) {
            log.error("Exception in communication with movements", e);
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private AssetListQuery createAssetListQuery(Collection<VesselIdentifierEntity> vesselIdentifiers) {
        AssetListQuery assetListQuery = new AssetListQuery();

        //Set asset list criteria
        AssetListCriteria assetListCriteria = new AssetListCriteria();
        for (VesselIdentifierEntity identifier : vesselIdentifiers) {
            VesselTypeAssetQueryEnum queryEnum = VesselTypeAssetQueryEnum.getVesselTypeAssetQueryEnum(identifier.getVesselIdentifierSchemeId());
            AssetListCriteriaPair criteriaPair = new AssetListCriteriaPair();
            criteriaPair.setKey(queryEnum.getConfigSearchField());
            criteriaPair.setValue(identifier.getVesselIdentifierId());
            assetListCriteria.getCriterias().add(criteriaPair);
        }

        assetListCriteria.setIsDynamic(false); // DO not know why
        assetListQuery.setAssetSearchCriteria(assetListCriteria);

        // Set asset pagination
        AssetListPagination pagination = new AssetListPagination();
        pagination.setPage(1);
        pagination.setListSize(1000);
        assetListQuery.setPagination(pagination);

        return assetListQuery;
    }
}
