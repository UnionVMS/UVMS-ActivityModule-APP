/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.fa.dao.FluxReportIdentifierDao;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityIDType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityTableType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityUniquinessList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
@LocalBean
@Transactional
@Slf4j
public class ActivityMatchingIdsService extends BaseActivityBean {

    private FluxReportIdentifierDao fluxReportIdentifierDao;

    @PostConstruct
    public void init() {
        fluxReportIdentifierDao = new FluxReportIdentifierDao(entityManager);
    }

    @NotNull
    public List<ActivityIDType> getMatchingIds(List<ActivityIDType> activityIDTypes, ActivityTableType activityTableType) {
        if (CollectionUtils.isEmpty(activityIDTypes)) {
            return Collections.emptyList();
        }

        return getActivityIDTypesPresentInDatabase(activityTableType, activityIDTypes);
    }

    @NotNull
    public List<ActivityUniquinessList> getMatchingIds(List<ActivityUniquinessList> activityUniquenessLists) {
        if (CollectionUtils.isEmpty(activityUniquenessLists)) {
            return Collections.emptyList();
        }
        List<ActivityUniquinessList> matchingIdsList = new ArrayList<>();

        for (ActivityUniquinessList activityUniquinessList : activityUniquenessLists) {
            ActivityUniquinessList activityNonUniqueIdsList = getActivityNonUniqueIdsList(activityUniquinessList);
            matchingIdsList.add(activityNonUniqueIdsList);
        }
        return matchingIdsList;
    }

    private ActivityUniquinessList getActivityNonUniqueIdsList(ActivityUniquinessList originalUniquenessList) {
        ActivityTableType originalActivityTableType = originalUniquenessList.getActivityTableType();
        List<ActivityIDType> originalActivityIdTypeList = originalUniquenessList.getIds();

        List<ActivityIDType> updatedActivityIdTypeList = getActivityIDTypesPresentInDatabase(originalActivityTableType, originalActivityIdTypeList);

        ActivityUniquinessList updatedUniquenessList = new ActivityUniquinessList();
        updatedUniquenessList.setIds(updatedActivityIdTypeList);
        updatedUniquenessList.setActivityTableType(originalActivityTableType);
        return updatedUniquenessList;
    }

    @NotNull
    private List<ActivityIDType> getActivityIDTypesPresentInDatabase(ActivityTableType originalActivityTableType, List<ActivityIDType> originalActivityIdTypeList) {
        List<FluxReportIdentifierEntity> matchingIdentifiers = fluxReportIdentifierDao.getMatchingIdentifiers(originalActivityIdTypeList, originalActivityTableType);
        return mapIDsListToActivityIDTypeList(matchingIdentifiers);
    }

    private List<ActivityIDType> mapIDsListToActivityIDTypeList(List<FluxReportIdentifierEntity> identifiersFromDbList) {
        if (CollectionUtils.isEmpty(identifiersFromDbList)) {
            return Collections.emptyList();
        }
        List<ActivityIDType> entityIds = new ArrayList<>();
        for (FluxReportIdentifierEntity fishTripIdentifier : identifiersFromDbList) {
            ActivityIDType activityIdType = new ActivityIDType(fishTripIdentifier.getFluxReportIdentifierId(), fishTripIdentifier.getFluxReportIdentifierSchemeId());
            entityIds.add(activityIdType);
        }
        return entityIds;
    }
}
