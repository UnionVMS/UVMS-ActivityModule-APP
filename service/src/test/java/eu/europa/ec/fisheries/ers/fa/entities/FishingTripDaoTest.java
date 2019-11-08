/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.fa.entities;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.dao.FishingTripDao;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.FishingTripId;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by sanera on 06/09/2016.
 */
public class FishingTripDaoTest extends BaseErsFaDaoTest {

    private FishingTripDao dao = new FishingTripDao(em);

    @Before
    public void prepare() {
        super.prepare();
    }

    @Test
    @SneakyThrows
    public void testGetFishingTripsForMatchingFilterCriteria() throws Exception {

        dbSetupTracker.skipNextLaunch();

        Map<SearchFilter,String> searchCriteriaMap = new HashMap<>();

        Map<SearchFilter,List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        List<String> activityTypeValues=new ArrayList<>();
        activityTypeValues.add("FISHING_OPERATION");
        activityTypeValues.add("DEPARTURE");
        activityTypeValues.add("ARRIVAL");

        FishingActivityQuery query = new FishingActivityQuery();
        query.setSearchCriteriaMap(searchCriteriaMap);
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);

        searchCriteriaMapMultiVal.put(SearchFilter.ACTIVITY_TYPE, activityTypeValues);
        List<FishingTripEntity> list= dao.getFishingTripsForMatchingFilterCriteria(query);

        System.out.println("done:" + list.size());

        assertNotNull(list);

    }

    @Test
    @SneakyThrows
    public void testGetFishingTripIdsForMatchingFilterCriteria() throws Exception {

        dbSetupTracker.skipNextLaunch();

        Map<SearchFilter,String> searchCriteriaMap = new HashMap<>();
        searchCriteriaMap.put(SearchFilter.ACTIVITY_TYPE, "DEPARTURE");


        Map<SearchFilter,List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        List<String> purposeCodeValues=new ArrayList<>();
        purposeCodeValues.add("9");
        purposeCodeValues.add("1");
        purposeCodeValues.add("5");
        purposeCodeValues.add("3");


        FishingActivityQuery query = new FishingActivityQuery();
        query.setSearchCriteriaMap(searchCriteriaMap);
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);

        searchCriteriaMapMultiVal.put(SearchFilter.PURPOSE, purposeCodeValues);
        Set<FishingTripId> fishingTripIdSet= dao.getFishingTripIdsForMatchingFilterCriteria(query);
        FishingTripId fishingTripId = new FishingTripId("NOR-TRP-20160517234053706","EU_TRIP_ID");

        assertEquals(true,fishingTripIdSet.contains(fishingTripId));
        assertNotNull(fishingTripIdSet);

    }

    @Test
    @SneakyThrows
    public void testGetFishingTripIdsForMatchingFilterCriteria_allCommonFilters() throws Exception {

        dbSetupTracker.skipNextLaunch();

        Map<SearchFilter,String> searchCriteriaMap = new HashMap<>();
        searchCriteriaMap.put(SearchFilter.ACTIVITY_TYPE, "DEPARTURE");
        searchCriteriaMap.put(SearchFilter.SOURCE, "FLUX");
        searchCriteriaMap.put(SearchFilter.OWNER, "OWNER1");
        searchCriteriaMap.put(SearchFilter.FROM, "OWNER1");
        searchCriteriaMap.put(SearchFilter.PERIOD_START, "2012-05-27T07:47:31");
        searchCriteriaMap.put(SearchFilter.PERIOD_END, "2018-05-27T07:47:31");
        searchCriteriaMap.put(SearchFilter.REPORT_TYPE, "DECLARATION");
        searchCriteriaMap.put(SearchFilter.AREAS, "J");
        searchCriteriaMap.put(SearchFilter.GEAR, "GEAR_TYPE");
        searchCriteriaMap.put(SearchFilter.SPECIES, "BFT");
        searchCriteriaMap.put(SearchFilter.QUANTITY_MIN, "0");
        searchCriteriaMap.put(SearchFilter.QUANTITY_MAX, "50");
        searchCriteriaMap.put(SearchFilter.MASTER, "MARK");

        Map<SearchFilter,List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        List<String> purposeCodeValues=new ArrayList<>();
        purposeCodeValues.add("9");
        purposeCodeValues.add("1");
        purposeCodeValues.add("5");
        purposeCodeValues.add("3");


        FishingActivityQuery query = new FishingActivityQuery();
        query.setSearchCriteriaMap(searchCriteriaMap);
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);

        searchCriteriaMapMultiVal.put(SearchFilter.PURPOSE, purposeCodeValues);
        Set<FishingTripId> fishingTripIdSet= dao.getFishingTripIdsForMatchingFilterCriteria(query);
        FishingTripId fishingTripId = new FishingTripId("NOR-TRP-20160517234053706","EU_TRIP_ID");

        assertEquals(true,fishingTripIdSet.contains(fishingTripId));
        assertNotNull(fishingTripIdSet);

    }


    @Test
    @SneakyThrows
    public void testGetFishingTripIdsForMatchingFilterCriteria_noFilters() throws Exception {

        dbSetupTracker.skipNextLaunch();

        Map<SearchFilter,String> searchCriteriaMap = new HashMap<>();

        Map<SearchFilter,List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        List<String> purposeCodeValues=new ArrayList<>();
        purposeCodeValues.add("9");
        purposeCodeValues.add("1");
        purposeCodeValues.add("5");
        purposeCodeValues.add("3");


        FishingActivityQuery query = new FishingActivityQuery();
        query.setSearchCriteriaMap(searchCriteriaMap);
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);

        searchCriteriaMapMultiVal.put(SearchFilter.PURPOSE, purposeCodeValues);
        Set<FishingTripId> fishingTripIdSet= dao.getFishingTripIdsForMatchingFilterCriteria(query);
        FishingTripId fishingTripId = new FishingTripId("NOR-TRP-20160517234053706","EU_TRIP_ID");

        assertEquals(true,fishingTripIdSet.contains(fishingTripId));
        assertNotNull(fishingTripIdSet);

    }

    @Test
    @SneakyThrows
    public void testGetCountOfFishingTripsForMatchingFilterCriteria() throws Exception {

        dbSetupTracker.skipNextLaunch();

        Map<SearchFilter,String> searchCriteriaMap = new HashMap<>();
        searchCriteriaMap.put(SearchFilter.ACTIVITY_TYPE, "DEPARTURE");
        searchCriteriaMap.put(SearchFilter.SOURCE, "FLUX");
        searchCriteriaMap.put(SearchFilter.OWNER, "OWNER1");
        searchCriteriaMap.put(SearchFilter.FROM, "OWNER1");
        searchCriteriaMap.put(SearchFilter.PERIOD_START, "2012-05-27T07:47:31");
        searchCriteriaMap.put(SearchFilter.PERIOD_END, "2018-05-27T07:47:31");
        searchCriteriaMap.put(SearchFilter.REPORT_TYPE, "DECLARATION");
        searchCriteriaMap.put(SearchFilter.AREAS, "J");
        searchCriteriaMap.put(SearchFilter.GEAR, "GEAR_TYPE");
        searchCriteriaMap.put(SearchFilter.SPECIES, "BFT");
        searchCriteriaMap.put(SearchFilter.QUANTITY_MIN, "0");
        searchCriteriaMap.put(SearchFilter.QUANTITY_MAX, "50");
        searchCriteriaMap.put(SearchFilter.MASTER, "MARK");

        Map<SearchFilter,List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        List<String> purposeCodeValues=new ArrayList<>();
        purposeCodeValues.add("9");
        purposeCodeValues.add("1");
        purposeCodeValues.add("5");
        purposeCodeValues.add("3");


        FishingActivityQuery query = new FishingActivityQuery();
        query.setSearchCriteriaMap(searchCriteriaMap);
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);

        searchCriteriaMapMultiVal.put(SearchFilter.PURPOSE, purposeCodeValues);
        Integer fishingTripCount= dao.getCountOfFishingTripsForMatchingFilterCriteria(query);
        System.out.println("Count : "+fishingTripCount);

        assertEquals(true,fishingTripCount >= 1);

    }
}
