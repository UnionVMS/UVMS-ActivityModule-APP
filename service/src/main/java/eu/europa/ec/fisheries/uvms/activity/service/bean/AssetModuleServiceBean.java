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

package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.VesselTypeAssetQueryEnum;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.AssetProducerBean;
import eu.europa.ec.fisheries.uvms.activity.service.AssetModuleService;
import eu.europa.ec.fisheries.uvms.activity.service.ModuleService;
import eu.europa.ec.fisheries.uvms.asset.client.AssetClient;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetQuery;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    public List<AssetDTO> getAssets(AssetQuery assetQuery) throws ServiceException {
        final boolean dynamic = false;
        List<AssetDTO> assetList = assetClient.getAssetList(assetQuery, dynamic);
        return assetList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAssetGuids(Collection<VesselIdentifierEntity> vesselIdentifiers) {
        Map<VesselTypeAssetQueryEnum, List<String>> identifierTypeToValuesMap = new HashMap<>();

        for (VesselIdentifierEntity vesselIdentifier : vesselIdentifiers) {
            VesselTypeAssetQueryEnum queryEnum = VesselTypeAssetQueryEnum.getVesselTypeAssetQueryEnum(vesselIdentifier.getVesselIdentifierSchemeId());
            if (queryEnum != null && queryEnum.getConfigSearchField() != null && StringUtils.isNotEmpty(vesselIdentifier.getVesselIdentifierId())) {
                List<String> queryEnumValues = identifierTypeToValuesMap.get(queryEnum);
                if (queryEnumValues == null) {
                    queryEnumValues = new ArrayList<>();
                    identifierTypeToValuesMap.put(queryEnum, queryEnumValues);
                }
                queryEnumValues.add(vesselIdentifier.getVesselIdentifierId());
            }
        }

        AssetQuery assetQuery = deriveAssetQuery(identifierTypeToValuesMap);
        final boolean dynamic = false;
        List<AssetDTO> assetList = assetClient.getAssetList(assetQuery, dynamic);
        final List<String> guids = extractGuidsFromAssets(assetList);
        return guids;
    }

    @NotNull
    private AssetQuery deriveAssetQuery(Map<VesselTypeAssetQueryEnum, List<String>> identifierMap) {
        AssetQuery assetQuery = new AssetQuery();
        identifierMap.forEach((VesselTypeAssetQueryEnum vesselTypeAssetQueryEnum, List<String> vesselIdentifierStrings) -> {
            switch (vesselTypeAssetQueryEnum) {
                case CFR:
                    assetQuery.setCfr(vesselIdentifierStrings);
                    break;
                case IRCS:
                    assetQuery.setIrcs(vesselIdentifierStrings);
                    break;
                case EXT_MARK:
                    assetQuery.setExternalMarking(vesselIdentifierStrings);
                    break;
                case FLAG_STATE:
                    assetQuery.setFlagState(vesselIdentifierStrings);
                    break;
                case NAME:
                    assetQuery.setName(vesselIdentifierStrings);
                    break;
                case ICCAT:
                    assetQuery.setIccat(vesselIdentifierStrings);
                    break;
                case UVI:
                    assetQuery.setUvi(vesselIdentifierStrings);
                    break;
            }
        });
        return assetQuery;
    }

    @Override
    public List<String> getAssetGuids(String vesselIdsSearchStr, String vesselGroupGuidSearchStr) throws ServiceException {
        List<String> resultingGuids = new ArrayList<>();

        // Get the list of guids from assets if vesselIdsSearchStr is provided
        if (StringUtils.isNotEmpty(vesselIdsSearchStr)) {
            Map<VesselTypeAssetQueryEnum, List<String>> identifierTypeToValuesMap = createMultipleIdTypesQueryMap(vesselIdsSearchStr);
            AssetQuery assetQuery = deriveAssetQuery(identifierTypeToValuesMap);
            boolean dynamic = false;
            List<AssetDTO> assetList = assetClient.getAssetList(assetQuery, dynamic);
            resultingGuids.addAll(extractGuidsFromAssets(assetList));
        }

        // Get the list of guids from assets if vesselGroupSearchName is provided
        if (StringUtils.isNotEmpty(vesselGroupGuidSearchStr)) {
            UUID vesselGroupGuidSearch = null;
            try {
                vesselGroupGuidSearch = UUID.fromString(vesselGroupGuidSearchStr);
            } catch (IllegalArgumentException iae) {
                throw new ServiceException("Provided group id, " + vesselGroupGuidSearchStr + ", was not a valid UUID", iae);
            }
            List<AssetDTO> assetList = assetClient.getAssetsByGroupIds(Arrays.asList(vesselGroupGuidSearch));
            resultingGuids.addAll(extractGuidsFromAssets(assetList));
        }

        return resultingGuids;

    }

    private List<String> extractGuidsFromAssets(List<AssetDTO> assetList) {
        return assetList
                .stream()
                .map(AssetDTO::getId)
                .map(UUID::toString)
                .collect(Collectors.toList());
    }

    private Map<VesselTypeAssetQueryEnum, List<String>> createMultipleIdTypesQueryMap(String vesselIdentifierValueToSearchFor) {
        Map<VesselTypeAssetQueryEnum, List<String>> identifierTypeToValuesMap = new HashMap<>();
        for (VesselTypeAssetQueryEnum queryEnum : VesselTypeAssetQueryEnum.values()) {
            if (queryEnum.getConfigSearchField() != null) {
                identifierTypeToValuesMap.put(queryEnum, Arrays.asList(vesselIdentifierValueToSearchFor));
            }
        }
        return identifierTypeToValuesMap;
    }
}
