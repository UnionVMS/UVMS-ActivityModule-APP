/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2020.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.service.bean.asset.gateway.impl;

import eu.europa.ec.fisheries.ers.service.bean.asset.gateway.ActivityAssetGateway;
import eu.europa.ec.fisheries.uvms.asset.rest.client.AssetClient;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.BatchAssetListResponseElement;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@ApplicationScoped
public class ActivityAssetGatewayImpl implements ActivityAssetGateway {

    @Inject
    private AssetClient assetClient;


    @Override
    public List<Asset> getAssetListByQuery(AssetListQuery assetListQuery) {
        try {
            ListAssetResponse assetListByQuery = assetClient.getAssetListByQuery(assetListQuery);
            return assetListByQuery == null ? new ArrayList<>():assetListByQuery.getAsset();
        } catch (Exception e) {
            log.error("Error in communication with asset: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<BatchAssetListResponseElement> getAssetListBatch(List<AssetListQuery> assetListQuery) {
        try {
            return assetClient.getAssetListBatch(assetListQuery);
        } catch (Exception e) {
            log.error("Error in communication with asset: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Asset> getAssetGroup(List<AssetGroup> assetGroupQuery) {
        return assetClient.getAssetGroup(assetGroupQuery);
    }

    @Override
    public Asset getAssetFromAssetGuidAndDate(String assetGuid, Date occurrenceDate) {
        return assetClient.getAssetFromAssetGuidAndDate(assetGuid, occurrenceDate);
    }

    @Override
    public Asset getAssetFromAssetHistId(String assetHistGuid) {
        return assetClient.getAssetFromAssetHistGuid(assetHistGuid);
    }
}


