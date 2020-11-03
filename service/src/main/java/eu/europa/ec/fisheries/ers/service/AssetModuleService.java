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

package eu.europa.ec.fisheries.ers.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.BatchAssetListResponseElement;
import eu.europa.ec.fisheries.wsdl.asset.types.VesselIdentifiersHolder;

public interface AssetModuleService {

    /**
     * Service to Get Asset GUIDs by Vessel Identifiers
     *
     * @param vesselIdentifiers vessel identifiers from Activity
     * @return asset GUIDs
     * @throws ServiceException
     */
    List<String> getAssetGuids(Collection<VesselIdentifierEntity> vesselIdentifiers) throws ServiceException;

    List<String> getAssetGuids(String vesselSearchStr, String vesselGroupSearchName) throws ServiceException;

    List<Asset> getAssetListResponse(AssetListQuery assetListQuery) throws ServiceException;

    List<BatchAssetListResponseElement> getAssetListResponseBatch(List<AssetListQuery> assetListQuery) throws ServiceException;

    String getAssetHistoryGuid(String assetGuid, Date occurrenceDate) throws ServiceException;

    VesselIdentifiersHolder getAssetVesselIdentifiersByAssetHistoryGuid(String assetHistoryGuid);
}
