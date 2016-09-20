/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.VesselDetailsTripDTO;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetListModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetModuleMethod;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import org.apache.commons.lang.StringUtils;

import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kovian on 15/09/2016.
 */
public class AssetsRequestMapper {

    public static String mapToAssetsRequest(VesselDetailsTripDTO vesselDetailsTripDTO) throws ModelMarshallException {
        AssetListModuleRequest assetsModuleRequest = new AssetListModuleRequest();
        AssetListQuery assetsQuery                 = new AssetListQuery();
        AssetListCriteria assetsListCrit           = new AssetListCriteria();
        AssetListPagination pagination             = new AssetListPagination();
        pagination.setListSize(1);
        pagination.setPage(1);
        assetsQuery.setPagination(pagination);
        assetsListCrit.getCriterias().addAll(getCriteriaListXML(vesselDetailsTripDTO));
        assetsListCrit.setIsDynamic(false);
        assetsQuery.setAssetSearchCriteria(assetsListCrit);
        assetsModuleRequest.setMethod(AssetModuleMethod.ASSET_LIST);
        assetsModuleRequest.setQuery(assetsQuery);
        return JAXBMarshaller.marshallJaxBObjectToString(assetsModuleRequest);
    }

    private static List<AssetListCriteriaPair> getCriteriaListXML(VesselDetailsTripDTO vesselDetailsTripDTO) {
        List<AssetListCriteriaPair> criteriaList = new ArrayList<>();
        if(StringUtils.isNotEmpty(vesselDetailsTripDTO.getCfr())){
            criteriaList.add(createCriteriaElement(ConfigSearchField.CFR, vesselDetailsTripDTO.getCfr()));
        }
        if(StringUtils.isNotEmpty(vesselDetailsTripDTO.getExMark())){
            criteriaList.add(createCriteriaElement(ConfigSearchField.EXTERNAL_MARKING, vesselDetailsTripDTO.getExMark()));
        }
        if(StringUtils.isNotEmpty(vesselDetailsTripDTO.getFlagState())){
            criteriaList.add(createCriteriaElement(ConfigSearchField.FLAG_STATE, vesselDetailsTripDTO.getFlagState()));
        }
        if(StringUtils.isNotEmpty(vesselDetailsTripDTO.getIrcs())){
            criteriaList.add(createCriteriaElement(ConfigSearchField.IRCS, vesselDetailsTripDTO.getIrcs()));
        }
        if(StringUtils.isNotEmpty(vesselDetailsTripDTO.getName())){
            criteriaList.add(createCriteriaElement(ConfigSearchField.NAME, vesselDetailsTripDTO.getName()));
        }
        if(StringUtils.isNotEmpty(vesselDetailsTripDTO.getUvi())){
            criteriaList.add(createCriteriaElement(ConfigSearchField.IMO, vesselDetailsTripDTO.getUvi()));
        }
        // TODO: Missing fields from ConfigSearchField enum
        /*
        if(StringUtils.isNotEmpty(vesselDetailsTripDTO.getGfcm())){
            criteriaList.add(createCriteriaElement(ConfigSearchField.PRODUCER_NAME, vesselDetailsTripDTO.getGfcm()));
        }
        if(StringUtils.isNotEmpty(vesselDetailsTripDTO.getIccat())){
            criteriaList.add(createCriteriaElement(ConfigSearchField.PRODUCER_NAME, vesselDetailsTripDTO.getIccat()));
        }*/
        return criteriaList;
    }

    private static AssetListCriteriaPair createCriteriaElement(ConfigSearchField field, String value) {
        AssetListCriteriaPair assetListCrit = new AssetListCriteriaPair();
        assetListCrit.setKey(field);
        assetListCrit.setValue(value);
        return assetListCrit;
    }

    public static void mapAssetsResponseToVesselDetailsTripDTO(TextMessage respMessage,
                                                               VesselDetailsTripDTO vesselDetailsTripDTO) throws ModelMarshallException {

        ListAssetResponse listResp = JAXBMarshaller.unmarshallTextMessage(respMessage, ListAssetResponse.class);
        if(listResp.getTotalNumberOfPages() == 0){
            return;
        }

        Asset asset = listResp.getAsset().get(0);

        if(!StringUtils.equalsIgnoreCase(vesselDetailsTripDTO.getCfr(), asset.getCfr())){
            vesselDetailsTripDTO.setCfr(asset.getCfr());
            vesselDetailsTripDTO.setCfrEnriched(true);
        }
        if(!StringUtils.equalsIgnoreCase(vesselDetailsTripDTO.getExMark(), asset.getExternalMarking())){
            vesselDetailsTripDTO.setExMark(asset.getExternalMarking());
            vesselDetailsTripDTO.setExMarkEnriched(true);
        }
        if(!StringUtils.equalsIgnoreCase(vesselDetailsTripDTO.getFlagState(), asset.getCountryCode())){ // TODO:Check if this is the right value
            vesselDetailsTripDTO.setFlagState(asset.getCountryCode());
            vesselDetailsTripDTO.setFlagStateEnriched(true);
        }
        if(!StringUtils.equalsIgnoreCase(vesselDetailsTripDTO.getIrcs(), asset.getIrcs())){
            vesselDetailsTripDTO.setIrcs(asset.getIrcs());
            vesselDetailsTripDTO.setIrcsEnriched(true);
        }
        if(!StringUtils.equalsIgnoreCase(vesselDetailsTripDTO.getName(), asset.getName())){
            vesselDetailsTripDTO.setName(asset.getName());
            vesselDetailsTripDTO.setNameEnriched(true);
        }
        if(!StringUtils.equalsIgnoreCase(vesselDetailsTripDTO.getUvi(), asset.getImo())){
            vesselDetailsTripDTO.setUvi(asset.getImo());
            vesselDetailsTripDTO.setUviEnriched(true);
        }

        /*
        if(!StringUtils.equalsIgnoreCase(vesselDetailsTripDTO.getGfcm(), asset.get)){
            vesselDetailsTripDTO.set;
        }
        if(!StringUtils.equalsIgnoreCase(vesselDetailsTripDTO.getIccat(), asset.get)){
            vesselDetailsTripDTO.set;
        }*/

    }
}
