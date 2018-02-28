/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.bean;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.europa.ec.fisheries.ers.fa.dao.FluxReportIdentifierDao;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityIDType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleMethod;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityUniquinessList;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

/**
 * Created by kovian on 12/07/2017.
 */
@Stateless
@LocalBean
@Transactional
@Slf4j
public class ActivityMatchingIdsServiceBean extends BaseActivityBean {

    private FluxReportIdentifierDao fluxRepIdentDao;

    @PostConstruct
    public void init() {
        initEntityManager();
        EntityManager entityManager = getEntityManager();
        fluxRepIdentDao = new FluxReportIdentifierDao(entityManager);
    }


    public GetNonUniqueIdsResponse getMatchingIdsResponse(List<ActivityUniquinessList> activityUniquinessLists) {
        if(CollectionUtils.isEmpty(activityUniquinessLists)){
            return null;
        }
        GetNonUniqueIdsResponse response = new GetNonUniqueIdsResponse();
        List<ActivityUniquinessList> activityUniquinessResponseLists = new ArrayList<>();
        response.setMethod(ActivityModuleMethod.GET_NON_UNIQUE_IDS);
        response.setActivityUniquinessLists(activityUniquinessResponseLists);
        for(ActivityUniquinessList actUniqueReq : activityUniquinessLists){
            activityUniquinessResponseLists.add(getActivityNonUniqueIdsList(actUniqueReq));
        }
        return response;
    }

    private ActivityUniquinessList getActivityNonUniqueIdsList(ActivityUniquinessList actRequestUniqueReq) {
        ActivityUniquinessList respUniqList = new ActivityUniquinessList();
        respUniqList.setActivityTableType(actRequestUniqueReq.getActivityTableType());
        List<ActivityIDType> ids = actRequestUniqueReq.getIds();
        respUniqList.setIds(mapIDsListToActivityIDTypeList(fluxRepIdentDao.getMatchingIdentifiers(ids, actRequestUniqueReq.getActivityTableType())));
        return respUniqList;
    }

    private List<ActivityIDType> mapIDsListToActivityIDTypeList(List<FluxReportIdentifierEntity> identifiersFromDbList) {
        if(CollectionUtils.isEmpty(identifiersFromDbList)){
            return Collections.emptyList();
        }
        List<ActivityIDType> entityIds = new ArrayList<>();
        for(FluxReportIdentifierEntity fishTripIdentifier : identifiersFromDbList){
            entityIds.add(new ActivityIDType(fishTripIdentifier.getFluxReportIdentifierId(),fishTripIdentifier.getFluxReportIdentifierSchemeId()));
        }
        return entityIds;
    }
}
