/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
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

    private FaReportDocumentDao faReportDocumentDao;

    @PostConstruct
    public void init() {
        faReportDocumentDao = new FaReportDocumentDao(entityManager);
    }

    @NotNull
    public List<ActivityIDType> getMatchingIds(List<ActivityIDType> activityIDTypes, ActivityTableType activityTableType) {
        if (CollectionUtils.isEmpty(activityIDTypes)) {
            return Collections.emptyList();
        }

        return getActivityIDTypesPresentInDatabase(activityTableType, activityIDTypes);
    }

    @NotNull
    private List<ActivityIDType> getActivityIDTypesPresentInDatabase(ActivityTableType originalActivityTableType, List<ActivityIDType> originalActivityIdTypeList) {
        List<FaReportDocumentEntity> matchingFaReportDocuments = faReportDocumentDao.getMatchingIdentifiers(originalActivityIdTypeList, originalActivityTableType);
        return mapIDsListToActivityIDTypeList(matchingFaReportDocuments);
    }

    private List<ActivityIDType> mapIDsListToActivityIDTypeList(List<FaReportDocumentEntity> identifiersFromDbList) {
        if (CollectionUtils.isEmpty(identifiersFromDbList)) {
            return Collections.emptyList();
        }
        List<ActivityIDType> entityIds = new ArrayList<>();
        for (FaReportDocumentEntity faReportDocumentEntity : identifiersFromDbList) {
            ActivityIDType activityIdType = new ActivityIDType(faReportDocumentEntity.getFluxReportDocument_Id(), faReportDocumentEntity.getFluxReportDocument_IdSchemeId());
            entityIds.add(activityIdType);
        }
        return entityIds;
    }
}
