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
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.AssetProducerBean;
import eu.europa.ec.fisheries.uvms.asset.client.AssetClient;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetQuery;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Transactional
@Slf4j
public class AssetModuleServiceBean extends ModuleService implements AssetModuleService {

    @EJB
    private AssetProducerBean assetProducer;

    @EJB
    private ActivityConsumerBean activityConsumer;

    @EJB
    private AssetClient assetClient;

    public AssetModuleServiceBean() {
    }

    @Inject
    public AssetModuleServiceBean(AssetProducerBean assetProducerBean, ActivityConsumerBean activityConsumerBean, AssetClient assetClient) {
        this.assetProducer = assetProducerBean;
        this.activityConsumer = activityConsumerBean;
        this.assetClient = assetClient;
    }

    @Override
    public List<Asset> getAssetListResponse(AssetListQuery assetListQuery) throws ServiceException {

        //TODO: Implement call to new Asset Module!
        /*
        try {

            String assetsRequest = AssetModuleRequestMapper.createAssetListModuleRequest(assetListQuery);
            String correlationID = assetProducer.sendModuleMessage(assetsRequest, activityConsumer.getDestination());
            TextMessage response = activityConsumer.getMessage(correlationID, TextMessage.class);
            assetList = AssetModuleResponseMapper.mapToAssetListFromResponse(response, correlationID);

        } catch (AssetModelMapperException | MessageException e) {
            log.error("Error while trying to send message to Assets module.", e);
            throw new ServiceException(e.getMessage(), e.getCause());
        }
        */

        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAssetGuids(Collection<VesselIdentifierEntity> vesselIdentifiers) {
        AssetQuery assetQuery = new AssetQuery();

        Map<VesselTypeAssetQueryEnum, List<String>> identifierMap = new HashMap<>();

        for (VesselIdentifierEntity vesselIdentifier : vesselIdentifiers) {
            VesselTypeAssetQueryEnum queryEnum = VesselTypeAssetQueryEnum.getVesselTypeAssetQueryEnum(vesselIdentifier.getVesselIdentifierSchemeId());
            if (queryEnum != null && queryEnum.getConfigSearchField() != null && StringUtils.isNotEmpty(vesselIdentifier.getVesselIdentifierId())) {
                List<String> queryEnumValues = identifierMap.get(queryEnum);
                if (queryEnumValues == null) {
                    queryEnumValues = new ArrayList<>();
                    identifierMap.put(queryEnum, queryEnumValues);
                }
                queryEnumValues.add(vesselIdentifier.getVesselIdentifierId());
            }
        }

        Set<VesselTypeAssetQueryEnum> vesselTypeAssetQueryEnums = identifierMap.keySet();
        for (VesselTypeAssetQueryEnum vesselTypeAssetQueryEnum : vesselTypeAssetQueryEnums) {
            switch (vesselTypeAssetQueryEnum) {
                case CFR:
                    assetQuery.setCfr(identifierMap.get(vesselTypeAssetQueryEnum));
                    break;
                case IRCS:
                    assetQuery.setIrcs(identifierMap.get(vesselTypeAssetQueryEnum));
                    break;
                case EXT_MARK:
                    assetQuery.setExternalMarking(identifierMap.get(vesselTypeAssetQueryEnum));
                    break;
                case FLAG_STATE:
                    assetQuery.setFlagState(identifierMap.get(vesselTypeAssetQueryEnum));
                    break;
                case NAME:
                    assetQuery.setName(identifierMap.get(vesselTypeAssetQueryEnum));
                    break;
                case ICCAT:
                    assetQuery.setIccat(identifierMap.get(vesselTypeAssetQueryEnum));
                    break;
                case UVI:
                    assetQuery.setUvi(identifierMap.get(vesselTypeAssetQueryEnum));
                    break;
            }
        }

        List<AssetDTO> assetList = assetClient.getAssetList(assetQuery);

        return assetList
                .stream()
                .map(AssetDTO::getId)
                .map(UUID::toString)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAssetGuids(String vesselSearchStr, String vesselGroupSearch) throws ServiceException {
        return new ArrayList<>();
        /*
        List<String> guidsFromVesselSearchStr = null;
        List<String> guidsFromVesselGroup     = null;
        String request;

        // Get the list of guids from assets if vesselSearchStr is provided
        if (StringUtils.isNotEmpty(vesselSearchStr)) {
            try {
                request = AssetModuleRequestMapper.createAssetListModuleRequest(createAssetListQuery(vesselSearchStr));
            } catch (AssetModelMapperException e) {
                log.error("Error while trying to map the request for assets Module.", e);
                throw new ServiceException(e.getMessage(), e.getCause());
            }
            guidsFromVesselSearchStr = getGuidsFromAssets(request);
        }

        // Get the list of guids from assets if vesselGroupSearchName is provided
        // If the list of guids is not empty then we have to provide this on the query also
        if(StringUtils.isNotEmpty(vesselGroupSearch)){
            try {
                request = AssetModuleRequestMapper.createAssetListModuleRequest(createAssetGroupQuery(vesselGroupSearch));
            } catch (AssetModelMapperException e) {
                log.error("Error while trying to map the request for assets Module.", e);
                throw new ServiceException(e.getMessage(), e.getCause());
            }
            guidsFromVesselGroup = getGuidsFromAssets(request);
        }

        return joinResults(guidsFromVesselSearchStr, guidsFromVesselGroup);

         */
    }

    @NotNull
    protected List<String> getGuidsFromAssets(String request) throws ServiceException {
        return new ArrayList<>();
        /*
        try {
            String correlationId = assetProducer.sendModuleMessage(request, activityConsumer.getDestination());
            TextMessage response = activityConsumer.getMessage(correlationId, TextMessage.class);
            if (response != null && isNotUserFault(response)) {
                List<Asset> assets = AssetModuleResponseMapper.mapToAssetListFromResponse(response, correlationId);
                List<String> assetGuids = new ArrayList<>();
                for (Asset asset : assets) {
                    assetGuids.add(asset.getAssetId().getGuid());
                }
                return assetGuids;
            } else {
                throw new ServiceException("FAILED TO GET DATA FROM ASSET");
            }
        } catch (ServiceException | MessageException | AssetModelMapperException e) {
            log.error("Exception in communication with movements", e);
            throw new ServiceException(e.getMessage(), e);
        }

         */
    }

    private List<String> joinResults(List<String> guidsFromVesselSearchStr, List<String> guidsFromVesselGroup) {
        Set<String> resultingList = new HashSet<>();
        if(CollectionUtils.isNotEmpty(guidsFromVesselSearchStr)){
            resultingList.addAll(guidsFromVesselSearchStr);
        }
        if(CollectionUtils.isNotEmpty(guidsFromVesselGroup)){
            resultingList.addAll(guidsFromVesselGroup);
        }
        return new ArrayList<>(resultingList);
    }

    @NotNull
    private List<AssetGroup> createAssetGroupQuery(String vesselGroupSearch) {
        List<AssetGroup> assetGroupList = new ArrayList<>();
        AssetGroup assGroup = new AssetGroup();
        assGroup.getSearchFields().addAll(createAssetGroupSearchFileds(vesselGroupSearch));
        assGroup.setGuid(vesselGroupSearch);
        assetGroupList.add(assGroup);
        return assetGroupList;
    }

    private List<AssetGroupSearchField> createAssetGroupSearchFileds(String vesselGroupSearchName) {
        List<AssetGroupSearchField> assetGroupSearchFieldList = new ArrayList<>();
        AssetGroupSearchField assetGroupGuidField = new AssetGroupSearchField();
        assetGroupGuidField.setKey(ConfigSearchField.GUID);
        assetGroupGuidField.setValue(vesselGroupSearchName);
        assetGroupSearchFieldList.add(assetGroupGuidField);
        return assetGroupSearchFieldList;
    }

    private AssetListQuery createAssetListQuery(String vesselToSearchFor) {
        AssetListQuery assetListQuery       = new AssetListQuery();
        AssetListCriteria assetListCriteria = new AssetListCriteria();
        for (VesselTypeAssetQueryEnum queryEnum : VesselTypeAssetQueryEnum.values()) {
            // Little hack since in the asset module this value corrisponds to a number one,
            // if it fails to parse it than it will throw!
            if(!queryEnum.equals(VesselTypeAssetQueryEnum.UVI) && queryEnum.getConfigSearchField() != null){
                AssetListCriteriaPair criteriaPair = new AssetListCriteriaPair();
                criteriaPair.setKey(queryEnum.getConfigSearchField());
                criteriaPair.setValue(vesselToSearchFor);
                assetListCriteria.getCriterias().add(criteriaPair);
            }
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

    private AssetListQuery createAssetListQuery(Collection<VesselIdentifierEntity> vesselIdentifiers) {
        AssetListQuery assetListQuery = new AssetListQuery();
        //Set asset list criteria
        AssetListCriteria assetListCriteria = new AssetListCriteria();
        for (VesselIdentifierEntity identifier : vesselIdentifiers) {
            VesselTypeAssetQueryEnum queryEnum = VesselTypeAssetQueryEnum.getVesselTypeAssetQueryEnum(identifier.getVesselIdentifierSchemeId());
            if(queryEnum != null && queryEnum.getConfigSearchField() != null && StringUtils.isNotEmpty(identifier.getVesselIdentifierId())){
                AssetListCriteriaPair criteriaPair = new AssetListCriteriaPair();
                criteriaPair.setKey(queryEnum.getConfigSearchField());
                criteriaPair.setValue(identifier.getVesselIdentifierId());
                assetListCriteria.getCriterias().add(criteriaPair);
            } else {
                log.warn("For Identifier : '"+identifier.getVesselIdentifierSchemeId()+"' it was not found the counterpart in the VesselTypeAssetQueryEnum.");
            }
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
