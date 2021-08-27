/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2020.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.service.bean.asset.gateway;


import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.types.*;

import java.util.Date;
import java.util.List;

public interface ActivityAssetGateway {

    List<Asset> getAssetListByQuery(AssetListQuery assetListQuery);

    List<BatchAssetListResponseElement> getAssetListBatch(List<AssetListQuery> assetListQuery);

    List<Asset> getAssetGroup(List<AssetGroup> assetGroupQuery);

    Asset getAssetFromAssetGuidAndDate(String assetGuid, Date occurrenceDate);

    Asset getAssetFromAssetHistId(String assetHistGuid);

    Asset getAssetByIdentifierPrecedence(AssetListCriteria assetListCriteria);

}
