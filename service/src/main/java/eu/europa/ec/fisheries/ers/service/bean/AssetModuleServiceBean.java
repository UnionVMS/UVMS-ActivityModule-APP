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

import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.VesselTypeAssetQueryEnum;
import eu.europa.ec.fisheries.ers.service.AssetModuleService;
import eu.europa.ec.fisheries.ers.service.MdrModuleService;
import eu.europa.ec.fisheries.ers.service.ModuleService;
import eu.europa.ec.fisheries.ers.service.bean.asset.gateway.ActivityAssetGateway;
import eu.europa.ec.fisheries.ers.service.mdrcache.MDRAcronymType;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField;
import eu.europa.ec.fisheries.wsdl.asset.module.FindVesselIdsByAssetHistGuidResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.swing.*;
import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Stateless
@Transactional
@Slf4j
public class AssetModuleServiceBean extends ModuleService implements AssetModuleService {


    @Inject
    private ActivityAssetGateway activityAssetGateway;

    @EJB
    private MdrModuleService mdrModuleServiceBean;

    @Override
    public List<Asset> getAssetListResponse(AssetListQuery assetListQuery) {
           return activityAssetGateway.getAssetListByQuery(assetListQuery);
    }

    @Override
    public List<BatchAssetListResponseElement> getAssetListResponseBatch(List<AssetListQuery> assetListQuery) {
           return activityAssetGateway.getAssetListBatch(assetListQuery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAssetGuids(Collection<VesselIdentifierEntity> vesselIdentifiers) {

        List<Asset> assetListByQuery = activityAssetGateway.getAssetListByQuery(createAssetListQuery(vesselIdentifiers));
        return getGuidsFromAssets(assetListByQuery);
    }

    @Override
    public List<Asset> getAssetsHavingAtLeastOneIdentifier(Collection<VesselIdentifierEntity> vesselIdentifiers) {
        return activityAssetGateway.getAssetListByQuery(createAssetListQuery(vesselIdentifiers));
    }

    @Override
    public List<Asset> getAssetsHavingAtLeastOneIdentifier(List<IDType> idTypes) {
        return activityAssetGateway.getAssetListByQuery(createAssetListQuery(idTypes));
    }

    @Override
    public List<String> getAssetGuids(String vesselSearchStr, String vesselGroupSearch) {
        List<String> guidsFromVesselSearchStr = null;
        List<String> guidsFromVesselGroup     = null;

        // Get the list of guids from assets if vesselSearchStr is provided
        if (StringUtils.isNotEmpty(vesselSearchStr)) {
            List<Asset> assetListQuery = activityAssetGateway.getAssetListByQuery(createAssetListQuery(vesselSearchStr));
            guidsFromVesselSearchStr = getGuidsFromAssets(assetListQuery);
        }

        // Get the list of guids from assets if vesselGroupSearchName is provided
        // If the list of guids is not empty then we have to provide this on the query also
        if(StringUtils.isNotEmpty(vesselGroupSearch)){
            List<Asset> assetGroup = activityAssetGateway.getAssetGroup(createAssetGroupQuery(vesselGroupSearch));
            guidsFromVesselGroup = getGuidsFromAssets(assetGroup);
        }

        return joinResults(guidsFromVesselSearchStr, guidsFromVesselGroup);
    }

    @NotNull
    protected List<String> getGuidsFromAssets(List<Asset> assets) {
        List<String> assetGuids = new ArrayList<>();
        for (Asset asset : assets) {
            assetGuids.add(asset.getAssetId().getGuid());
        }
        return assetGuids;
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

            //Fix for UNIONVMS-5102
            //if(!queryEnum.equals(VesselTypeAssetQueryEnum.UVI) && queryEnum.getConfigSearchField() != null){
                AssetListCriteriaPair criteriaPair = new AssetListCriteriaPair();
                criteriaPair.setKey(queryEnum.getConfigSearchField());
                criteriaPair.setValue(vesselToSearchFor);
                assetListCriteria.getCriterias().add(criteriaPair);
            //}
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

    private AssetListQuery createAssetListQuery(List<IDType> vesselsIdentifiers) {
        AssetListQuery assetListQuery = new AssetListQuery();
        //Set asset list criteria
        AssetListCriteria assetListCriteria = new AssetListCriteria();
        for (IDType identifier : vesselsIdentifiers) {
            VesselTypeAssetQueryEnum queryEnum = VesselTypeAssetQueryEnum.getVesselTypeAssetQueryEnum(identifier.getSchemeID());
            if(queryEnum != null && queryEnum.getConfigSearchField() != null && StringUtils.isNotEmpty(identifier.getValue())){
                AssetListCriteriaPair criteriaPair = new AssetListCriteriaPair();
                criteriaPair.setKey(queryEnum.getConfigSearchField());
                criteriaPair.setValue(identifier.getValue());
                assetListCriteria.getCriterias().add(criteriaPair);
            } else {
                log.warn("For Identifier : '"+identifier.getValue() + "' it was not found the counterpart in the VesselTypeAssetQueryEnum.");
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

    @Override
    public String getAssetHistoryGuid(String assetGuid, Date occurrenceDate) throws ServiceException {
        try {
            Asset assetFromAssetGuidAndDate = activityAssetGateway.getAssetFromAssetGuidAndDate(assetGuid, occurrenceDate);
            if(assetFromAssetGuidAndDate == null || assetFromAssetGuidAndDate.getEventHistory() == null){
                return null;
            }
            return assetFromAssetGuidAndDate.getEventHistory().getEventId();
        } catch (Exception e) {
            log.error("Error while trying to get Asset List..");
            throw new ServiceException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public VesselIdentifiersHolder getAssetVesselIdentifiersByAssetHistoryGuid(String assetHistoryGuid)  {
            Asset assetFromAssetHistId = activityAssetGateway.getAssetFromAssetHistId(assetHistoryGuid);
            FindVesselIdsByAssetHistGuidResponse findVesselIdsByAssetHistGuidResponse = AssetModuleResponseMapper.createFindVesselIdsByAssetHistGuidResponseObject(assetFromAssetHistId);
            return findVesselIdsByAssetHistGuidResponse.getIdentifiers();
    }


    @Override
    public Asset getAssetGuidByIdentifierPrecedence(VesselTransportMeansEntity vesselTransport, FaReportDocumentEntity faReportDocumentEntity) throws  ServiceException{
        Collection<VesselIdentifierEntity> vesselIdentifiers = vesselTransport.getVesselIdentifiers();
        List<AssetListCriteriaPair> criteriaPairs =
                vesselIdentifiers.stream()
                        .map(entry -> {
                            AssetListCriteriaPair criteriaPair = new AssetListCriteriaPair();
                            criteriaPair.setKey(VesselTypeAssetQueryEnum.getVesselTypeAssetQueryEnum(entry.getVesselIdentifierSchemeId()).getConfigSearchField());
                            criteriaPair.setValue(entry.getVesselIdentifierId());
                            return criteriaPair;
                        })
                        .collect(Collectors.toList());

        //Check for cfr
        AssetListCriteriaPair cfrPair =
                criteriaPairs.stream().filter(p->ConfigSearchField.CFR == p.getKey()).findAny().orElse(null);
        if (cfrPair != null) {
            //If a CFR is in the message, it should be for an EU country
            Map<String, List<String>> memberStates = mdrModuleServiceBean.getAcronymFromMdr(MDRAcronymType.MEMBER_STATE.name(), "code", "startDate","endDate");
            String country = memberStates.get("code").stream().filter(s->s.equals(vesselTransport.getCountry())).findAny().orElse(null);
            if (country != null) {
                int indexOfCountry = memberStates.get("code").indexOf(country);
                Date startDate = DateUtils.parseToUTCDate(memberStates.get("startDate").get(indexOfCountry), DateUtils.DATE_TIME_UI_FORMAT_MS);
                Date endDate = DateUtils.parseToUTCDate(memberStates.get("endDate").get(indexOfCountry), DateUtils.DATE_TIME_UI_FORMAT_MS);
                if (!(faReportDocumentEntity.getFluxReportDocument().getCreationDatetime().compareTo(startDate) >= 0 &&
                        faReportDocumentEntity.getFluxReportDocument().getCreationDatetime().compareTo(endDate) <= 0)) {
                    //remove cfr from criteria
                    criteriaPairs.remove(cfrPair);
                }
            } else {
                criteriaPairs.remove(cfrPair);
            }
        }

        try {
            AssetListCriteria assetListCriteria = new AssetListCriteria();
            assetListCriteria.getCriterias().addAll(criteriaPairs);

            //Add the flagstate criterion
            AssetListCriteriaPair flagStatePair = new AssetListCriteriaPair();
            flagStatePair.setKey(ConfigSearchField.FLAG_STATE);
            flagStatePair.setValue(vesselTransport.getCountry());
            assetListCriteria.getCriterias().add(flagStatePair);

            //Add the creation date criterion
            AssetListCriteriaPair datePair = new AssetListCriteriaPair();
            datePair.setKey(ConfigSearchField.DATE);
            datePair.setValue(DateUtils.dateToString(faReportDocumentEntity.getFluxReportDocument().getCreationDatetime()));
            assetListCriteria.getCriterias().add(datePair);


            return activityAssetGateway.getAssetByIdentifierPrecedence(assetListCriteria);
        } catch (Exception e) {
            log.error("Error while trying to get Asset List..");
            throw new ServiceException(e.getMessage(), e.getCause());
        }
    }

}
