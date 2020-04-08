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
import eu.europa.ec.fisheries.uvms.activity.service.AssetModuleService;
import eu.europa.ec.fisheries.uvms.asset.client.AssetClient;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.search.SearchBranch;
import eu.europa.ec.fisheries.uvms.asset.client.model.search.SearchFields;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Stateless
@Transactional
@Slf4j
public class AssetModuleServiceBean implements AssetModuleService {

    @EJB
    private AssetClient assetClient;

    @Inject
    public AssetModuleServiceBean(AssetClient assetClient) {
        this.assetClient = assetClient;
    }

    @Override
    public List<AssetDTO> getAssets(SearchBranch query) {
        return assetClient.getAssetList(query);
    }

    @Override
    public List<String> getAssetGuids(Collection<VesselIdentifierEntity> vesselIdentifiers) {
        SearchBranch query = new SearchBranch();
        query.setLogicalAnd(false);

        for (VesselIdentifierEntity vesselIdentifier : vesselIdentifiers) {
            SearchFields searchField = EnumUtils.getEnumIgnoreCase(SearchFields.class, vesselIdentifier.getVesselIdentifierSchemeId());
            if (searchField != null && StringUtils.isNotEmpty(vesselIdentifier.getVesselIdentifierId())) {
                query.addNewSearchLeaf(searchField, vesselIdentifier.getVesselIdentifierId());
            }
        }

        List<AssetDTO> assetList = assetClient.getAssetList(query);
        return extractGuidsFromAssets(assetList);
    }

    @Override
    public List<String> getAssetGuids(String vesselIdsSearchStr, String vesselGroupGuidSearchStr) throws ServiceException {
        List<String> resultingGuids = new ArrayList<>();

        // Get the list of guids from assets if vesselIdsSearchStr is provided
        if (StringUtils.isNotEmpty(vesselIdsSearchStr)) {
            SearchBranch query = createMultipleIdTypesSearchBranch(vesselIdsSearchStr);
            List<AssetDTO> assetList = assetClient.getAssetList(query);
            resultingGuids.addAll(extractGuidsFromAssets(assetList));
        }

        // Get the list of guids from assets if vesselGroupSearchName is provided
        if (StringUtils.isNotEmpty(vesselGroupGuidSearchStr)) {
            UUID vesselGroupGuidSearch;
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

    private SearchBranch createMultipleIdTypesSearchBranch(String vesselIdentifierValueToSearchFor) {
        SearchBranch result = new SearchBranch();
        result.setLogicalAnd(false);
        result.addNewSearchLeaf(SearchFields.CFR, vesselIdentifierValueToSearchFor);
        result.addNewSearchLeaf(SearchFields.IRCS, vesselIdentifierValueToSearchFor);
        result.addNewSearchLeaf(SearchFields.EXTERNAL_MARKING, vesselIdentifierValueToSearchFor);
        result.addNewSearchLeaf(SearchFields.FLAG_STATE, vesselIdentifierValueToSearchFor);
        result.addNewSearchLeaf(SearchFields.NAME, vesselIdentifierValueToSearchFor);
        result.addNewSearchLeaf(SearchFields.ICCAT, vesselIdentifierValueToSearchFor);
        result.addNewSearchLeaf(SearchFields.UVI, vesselIdentifierValueToSearchFor);

        return result;
    }
}
