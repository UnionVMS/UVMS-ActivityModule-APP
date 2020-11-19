/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.bean;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.service.AssetModuleService;
import eu.europa.ec.fisheries.ers.service.FaQueryService;
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.mapper.ActivityEntityToModelMapper;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ForwardQueryToSubscriptionRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.QueryToSubscription;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubCriteriaType;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataCriteria;
import io.jsonwebtoken.lang.Collections;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQuery;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQueryParameter;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Stateless
@Slf4j
public class FAQueryServiceBean implements FaQueryService {
    
    public static final String VESSELID = "VESSELID";
    public static final String TRIPID = "TRIPID";
    public static final String CONSOLIDATED = "CONSOLIDATED";
    private static final String FLUX_LOCAL_NATION_CODE = "flux_local_nation_code";

    @PersistenceContext(unitName = "activityPUpostgres")
    private EntityManager postgres;

    @PersistenceContext(unitName = "activityPUoracle")
    private EntityManager oracle;

    @Inject
    AssetModuleService assetModuleService;

    @Inject
    FishingTripService fishingTripService;

    private EntityManager em;
    private FaReportDocumentDao FAReportDAO;

    @Inject
    ParameterService parameterService;

    private String localNodeName;

    @PostConstruct
    public void init() {

        String dbDialect = System.getProperty("db.dialect");
        if ("oracle".equalsIgnoreCase(dbDialect)) {
            em = oracle;
        } else {
            em = postgres;
        }
        FAReportDAO = new FaReportDocumentDao(em);
        try {
            localNodeName = parameterService.getParamValueById(FLUX_LOCAL_NATION_CODE);
        } catch (ConfigServiceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FLUXFAReportMessage getReportsByCriteria(List<SubscriptionDataCriteria> subscriptionDataCriteria) {

        if (CollectionUtils.isNotEmpty(subscriptionDataCriteria)){

            String consolidated = "N";
            String tripID = null;
            String vesselId = null;
            String schemeId = null;
            String endDate = null;
            String startDate = null;

            for (SubscriptionDataCriteria dataCriteria : subscriptionDataCriteria){

                SubCriteriaType subCriteria = dataCriteria.getSubCriteria();
                String valueType = dataCriteria.getValueType().value();
                String value = dataCriteria.getValue();

                if (subCriteria == SubCriteriaType.END_DATE) {
                    endDate = value;
                }

                else if (subCriteria == SubCriteriaType.START_DATE) {
                    startDate = value;
                }

            }

            List<FaReportDocumentEntity> faReportDocumentsForTrip = FAReportDAO.loadReports(tripID, consolidated, vesselId, schemeId, startDate, endDate);
            return ActivityEntityToModelMapper.INSTANCE.mapToFLUXFAReportMessage(faReportDocumentsForTrip, localNodeName);

        }

        return null;
    }

    @Override
    public FLUXFAReportMessage getReportsByCriteria(FAQuery faQuery) {
        Criteria criteria = buildCriteria(faQuery);

        List<FaReportDocumentEntity> faReportDocumentsForTrip = FAReportDAO.loadReports(criteria.tripID, criteria.consolidated, criteria.vesselId, criteria.schemeId, criteria.startDate, criteria.endDate);
        return ActivityEntityToModelMapper.INSTANCE.mapToFLUXFAReportMessage(faReportDocumentsForTrip, localNodeName);
    }

    @Override
    public ForwardQueryToSubscriptionRequest getForwardQueryToSubscriptionRequestByFAQuery(FAQuery faQuery) throws ServiceException {
        ForwardQueryToSubscriptionRequest forwardQueryToSubscriptionRequest = new ForwardQueryToSubscriptionRequest();
        QueryToSubscription queryToSubscription = new QueryToSubscription();

        Criteria criteria = buildCriteria(faQuery);
        queryToSubscription.setConsolidated(criteria.getConsolidated());
        queryToSubscription.setEndDate(criteria.getEndDate());
        queryToSubscription.setStartDate(criteria.getStartDate());
        queryToSubscription.setTripID(criteria.getTripID());
        if (criteria.getTripID() != null) {
            criteria.setVesselId(getVesselIdByTripId(criteria.getTripID()));
        }
        queryToSubscription.setVesselId(criteria.getVesselId());
        List<String> assetHistGuids = Lists.newArrayList();
        for (String assetGuid: assetModuleService.getAssetGuids(criteria.getVesselId(),null)){
            assetHistGuids.add(assetModuleService.getAssetHistoryGuid(assetGuid, new Date()));
        }

        queryToSubscription.setAssetHistGuids(assetHistGuids);
        forwardQueryToSubscriptionRequest.setQueryToSubscription(queryToSubscription);

        return forwardQueryToSubscriptionRequest;
    }

    private Criteria buildCriteria(FAQuery faQuery){
        Criteria criteria = new Criteria();
        faQuery.getSimpleFAQueryParameters().forEach(param -> addCriterion(param, criteria));
        if (faQuery.getSpecifiedDelimitedPeriod() != null && faQuery.getSpecifiedDelimitedPeriod().getStartDateTime() != null) {
            criteria.setStartDate(faQuery.getSpecifiedDelimitedPeriod().getStartDateTime().getDateTime().toString());
        }
        if (faQuery.getSpecifiedDelimitedPeriod() != null && faQuery.getSpecifiedDelimitedPeriod().getEndDateTime() != null) {
            criteria.setEndDate(faQuery.getSpecifiedDelimitedPeriod().getEndDateTime().getDateTime().toString());
        }
        return criteria;
    }

    private void addCriterion(FAQueryParameter faQueryParameter, Criteria criteria) {
        if(VESSELID.equals(faQueryParameter.getTypeCode().getValue())) {
            criteria.setSchemeId(faQueryParameter.getValueID().getSchemeID());
            criteria.setVesselId(faQueryParameter.getValueID().getValue());
        } else if (TRIPID.equals(faQueryParameter.getTypeCode().getValue())) {
            criteria.setTripID(faQueryParameter.getValueID().getValue());
        } else if (CONSOLIDATED.equals(faQueryParameter.getTypeCode().getValue())) {
            criteria.setConsolidated(faQueryParameter.getValueCode().getValue());
        }
    }

    private String getVesselIdByTripId(String tripId) throws ServiceException {
        AtomicReference<String> vesselId = new AtomicReference<>();
        Optional<VesselTransportMeansEntity> vesselTransportMeansEntityOptional = fishingTripService.
                getVesselTransportMeansEntityByFishingTripId(tripId);
        if (vesselTransportMeansEntityOptional.isPresent()) {
            VesselTransportMeansEntity vesselTransportMeansEntity = vesselTransportMeansEntityOptional.get();
            if (CollectionUtils.isNotEmpty(vesselTransportMeansEntity.getVesselIdentifiers())) {
                vesselTransportMeansEntity.getVesselIdentifiers().forEach(vesselIdentifier -> {
                    vesselId.set(vesselIdentifier.getVesselIdentifierId());
                });
            }
        }
        return vesselId.get();
    }
    
    @Data
    @NoArgsConstructor
    public class Criteria {
        String startDate;
        String endDate;
        String tripID;
        String vesselId;
        String schemeId;
        String consolidated;
    }
}
