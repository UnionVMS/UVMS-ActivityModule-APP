/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.SortKey;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginationDto;
import lombok.SneakyThrows;

public class FishingActivityDaoTest extends BaseErsFaDaoTest {

    private FishingActivityDao dao = new FishingActivityDao(em);

    @Before
    public void prepare() {
        super.prepare();
    }

    @Test
    @SneakyThrows
    public void testFindEntityById() throws Exception {
        dbSetupTracker.skipNextLaunch();
        FishingActivityEntity entity = dao.findEntityById(FishingActivityEntity.class, 1);
        assertNotNull(entity);
    }

    @Test
    @SneakyThrows
    public void testGetFishingActivityForTrip(){
        dbSetupTracker.skipNextLaunch();

        String tripId = "NOR-TRP-20160517234053706";
        String tripSchemeId = "EU_TRIP_ID";
        String activityTypeCode = "DEPARTURE";
        List<String> purposes = Arrays.asList("1", "3", "5", "9");

        List<FishingActivityEntity> fishingActivityForTrip = dao.getFishingActivityForTrip(tripId, tripSchemeId,
                activityTypeCode, purposes);

        assertNotNull(fishingActivityForTrip);
        assertTrue(!fishingActivityForTrip.isEmpty());
        assertTrue(fishingActivityForTrip.size() == 1);
    }

    @Test
    @SneakyThrows
    public void testGetFishingActivityListByQuery() throws Exception {

        dbSetupTracker.skipNextLaunch();
        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();

        Map<SearchFilter, List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        List<String> activityTypeValues = new ArrayList<>();
        activityTypeValues.add("FISHING_OPERATION");
        activityTypeValues.add("DEPARTURE");
        searchCriteriaMapMultiVal.put(SearchFilter.ACTIVITY_TYPE, activityTypeValues);
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);

        searchCriteriaMap.put(SearchFilter.OWNER, "OWNER1");
        searchCriteriaMap.put(SearchFilter.PERIOD_START, "2012-05-27T07:47:31");
        searchCriteriaMap.put(SearchFilter.PERIOD_END, "2015-05-27T07:47:31");
        searchCriteriaMap.put(SearchFilter.VESSEL_NAME, "vessel1");
        searchCriteriaMap.put(SearchFilter.VESSEL_IDENTIFIRE, "CFR123");
        searchCriteriaMap.put(SearchFilter.REPORT_TYPE, "DECLARATION");
        searchCriteriaMap.put(SearchFilter.GEAR, "GEAR_TYPE");
        searchCriteriaMap.put(SearchFilter.SPECIES, "PLE");
        searchCriteriaMap.put(SearchFilter.MASTER, "MARK");
        searchCriteriaMap.put(SearchFilter.AREAS, "27.4.b");
       // searchCriteriaMap.put(SearchFilter.PORT, "GBR");
        searchCriteriaMap.put(SearchFilter.QUANTITY_MIN, "0");
        searchCriteriaMap.put(SearchFilter.QUANTITY_MAX, "25");

        query.setSearchCriteriaMap(searchCriteriaMap);
        PaginationDto pagination = new PaginationDto();
        pagination.setPageSize(2);
        pagination.setOffset(1);
        query.setPagination(pagination);

        SortKey sortingDto = new SortKey();
        sortingDto.setSortBy(SearchFilter.OCCURRENCE);
        sortingDto.setReversed(false);
        query.setSorting(sortingDto);

        List<FishingActivityEntity> finishingActivityList = dao.getFishingActivityListByQuery(query);
        System.out.println("done:" + finishingActivityList.size());

        assertNotNull(finishingActivityList);
    }

    @Test
    @SneakyThrows
    public void testGetFishingActivityListByQuery_GetByFaReportID() throws Exception {

        dbSetupTracker.skipNextLaunch();
        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();
        Map<SearchFilter, List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        List<String> activityTypeValues = new ArrayList<>();
        activityTypeValues.add("FISHING_OPERATION");
        activityTypeValues.add("DEPARTURE");
        searchCriteriaMapMultiVal.put(SearchFilter.ACTIVITY_TYPE, activityTypeValues);

        List<String> purposeCodeValues = new ArrayList<>();
        purposeCodeValues.add("5");
        purposeCodeValues.add("9");

        searchCriteriaMapMultiVal.put(SearchFilter.PURPOSE, purposeCodeValues);
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);

        searchCriteriaMap.put(SearchFilter.OWNER, "OWNER1");
        searchCriteriaMap.put(SearchFilter.PERIOD_START, "2012-05-27T07:47:31");
        searchCriteriaMap.put(SearchFilter.PERIOD_END, "2015-05-27T07:47:31");
        searchCriteriaMap.put(SearchFilter.VESSEL_NAME, "vessel1");
        searchCriteriaMap.put(SearchFilter.VESSEL_IDENTIFIRE, "CFR123");
        searchCriteriaMap.put(SearchFilter.REPORT_TYPE, "DECLARATION");
        searchCriteriaMap.put(SearchFilter.GEAR, "GEAR_TYPE");
        searchCriteriaMap.put(SearchFilter.SPECIES, "PLE");
        searchCriteriaMap.put(SearchFilter.MASTER, "MARK");
        searchCriteriaMap.put(SearchFilter.AREAS, "27.4.b");
     //   searchCriteriaMap.put(SearchFilter.PORT, "GBR");
        searchCriteriaMap.put(SearchFilter.QUANTITY_MIN, "0");
        searchCriteriaMap.put(SearchFilter.QUANTITY_MAX, "25");
        searchCriteriaMap.put(SearchFilter.FA_REPORT_ID, "1");

        query.setSearchCriteriaMap(searchCriteriaMap);

        PaginationDto pagination = new PaginationDto();
        pagination.setPageSize(2);
        pagination.setOffset(1);
        query.setPagination(pagination);

        SortKey sortingDto = new SortKey();
        sortingDto.setSortBy(SearchFilter.OCCURRENCE);
        sortingDto.setReversed(false);
        query.setSorting(sortingDto);
        List<FishingActivityEntity> finishingActivityList = dao.getFishingActivityListByQuery(query);
        assertNotNull(finishingActivityList);
    }

    @Test
    @SneakyThrows
    public void testGetCountForFishingActivityListByQuery() throws Exception {

        dbSetupTracker.skipNextLaunch();
        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();

        Map<SearchFilter, List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        List<String> activityTypeValues = new ArrayList<>();
        activityTypeValues.add("FISHING_OPERATION");
        activityTypeValues.add("DEPARTURE");
        searchCriteriaMapMultiVal.put(SearchFilter.ACTIVITY_TYPE, activityTypeValues);

        List<String> purposeCodeValues = new ArrayList<>();
        purposeCodeValues.add("5");
        purposeCodeValues.add("9");
        searchCriteriaMapMultiVal.put(SearchFilter.PURPOSE, purposeCodeValues);

        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);

        searchCriteriaMap.put(SearchFilter.OWNER, "OWNER1");

        searchCriteriaMap.put(SearchFilter.PERIOD_START, "2012-05-27T07:47:31");
        searchCriteriaMap.put(SearchFilter.PERIOD_END, "2015-05-27T07:47:31");
        searchCriteriaMap.put(SearchFilter.VESSEL_NAME, "vessel1");
        searchCriteriaMap.put(SearchFilter.VESSEL_IDENTIFIRE, "CFR123");

        searchCriteriaMap.put(SearchFilter.REPORT_TYPE, "DECLARATION");
        searchCriteriaMap.put(SearchFilter.GEAR, "GEAR_TYPE");

        searchCriteriaMap.put(SearchFilter.SPECIES, "PLE");
        searchCriteriaMap.put(SearchFilter.MASTER, "MARK");
        searchCriteriaMap.put(SearchFilter.AREAS, "27.4.b");

        searchCriteriaMap.put(SearchFilter.QUANTITY_MIN, "0");
        searchCriteriaMap.put(SearchFilter.QUANTITY_MAX, "25");

        searchCriteriaMap.put(SearchFilter.SOURCE, "FLUX");

        SortKey sortingDto = new SortKey();
        sortingDto.setSortBy(SearchFilter.PURPOSE);
        sortingDto.setReversed(false);
        query.setSorting(sortingDto);

        query.setSearchCriteriaMap(searchCriteriaMap);
        PaginationDto pagination = new PaginationDto();
        pagination.setPageSize(2);
        pagination.setOffset(1);
        query.setPagination(pagination);

        SortKey sortingDto2 = new SortKey();
        sortingDto2.setSortBy(SearchFilter.OCCURRENCE);
        sortingDto2.setReversed(false);
        query.setSorting(sortingDto2);

        int size = dao.getCountForFishingActivityListByQuery(query);

        System.out.println("done:" + size);
     //   assertNotEquals(0, size);

    }

    @Test
    @SneakyThrows
    public void testGetFishingActivityListForFishingTrip() throws Exception {
        dbSetupTracker.skipNextLaunch();
        List<FishingActivityEntity> finishingActivityList = dao.getFishingActivityListForFishingTrip("NOR-TRP-20160517234053706", null);
        assertNotNull(finishingActivityList);
        assertNotEquals(0, finishingActivityList.size());
    }

}