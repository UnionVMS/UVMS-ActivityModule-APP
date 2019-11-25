/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.fa.dao;

import com.google.common.collect.Sets;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.BaseErsFaDaoTest;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.service.search.SortKey;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginationDto;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FishingActivityDaoTest extends BaseErsFaDaoTest {

    private FishingActivityDao dao = new FishingActivityDao(em);

    @Before
    public void prepare() {
        super.prepare();

        dbSetupTracker.skipNextLaunch();
    }

    @Test
    @SneakyThrows
    public void findEntityById() {
        // Given

        // When
        FishingActivityEntity entity = dao.findEntityById(FishingActivityEntity.class, 1);

        // Then
        assertEquals(1, entity.getId());
    }

    @Test
    @SneakyThrows
    public void getFishingActivityForTrip() {
        // Given
        String tripId = "NOR-TRP-20160517234053706";
        String tripSchemeId = "EU_TRIP_ID";
        String activityTypeCode = "DEPARTURE";
        List<String> purposes = Arrays.asList("1", "3", "5", "9");

        // When
        List<FishingActivityEntity> fishingActivityForTrip = dao.getFishingActivityForTrip(tripId, tripSchemeId,
                activityTypeCode, purposes);

        // Then
        assertEquals(1, fishingActivityForTrip.size());

        FishingActivityEntity fishingActivityEntity = fishingActivityForTrip.get(0);
        assertEquals(1, fishingActivityEntity.getId());

        FishingTripEntity fishingTripEntity = fishingActivityEntity.getFishingTrip();
        FishingTripIdentifierEntity fishingTripIdentifier = fishingTripEntity.getFishingTripIdentifier();
        assertEquals("NOR-TRP-20160517234053706", fishingTripIdentifier.getTripId());
    }

    @Test
    @SneakyThrows
    public void getFishingActivityListByQuery_byMultiValueCriteriaMap() {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();

        Map<SearchFilter, List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        List<String> activityTypeValues = new ArrayList<>();
        activityTypeValues.add("FISHING_OPERATION");
        activityTypeValues.add("DEPARTURE");
        searchCriteriaMapMultiVal.put(SearchFilter.ACTIVITY_TYPE, activityTypeValues);
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);

        // When
        List<FishingActivityEntity> result = dao.getFishingActivityListByQuery(query);

        // Then
        assertIds(result, 4, 5, 6, 7, 8);

        for (int i = 0; i < 5; i++) {
            assertEquals("FISHING_OPERATION", result.get(i).getTypeCode());
        }
    }

    @Test
    @SneakyThrows
    public void getFishingActivityListByQuery_byReportType() {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();

        searchCriteriaMap.put(SearchFilter.REPORT_TYPE, "DECLARATION");
        query.setSearchCriteriaMap(searchCriteriaMap);

        // When
        List<FishingActivityEntity> result = dao.getFishingActivityListByQuery(query);

        // Then
        assertIds(result, 2, 3, 4, 6);
    }

    @Test
    @SneakyThrows
    public void getFishingActivityListByQuery_byPort() {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();

        searchCriteriaMap.put(SearchFilter.PORT, "GBR");
        query.setSearchCriteriaMap(searchCriteriaMap);

        // When
        List<FishingActivityEntity> result = dao.getFishingActivityListByQuery(query);

        // Then
        assertIds(result, 6, 7);
    }

    /**
     * The test that only has REPORT_TYPE in the search criteria map returns the activities 2, 3, 4 and 6.
     * The test that only has PORT in the search criteria map returns the activities 6 and 7.
     * So this test has both REPORT_TYPE and PORT in the search criteria map, and thus should only get
     * the activity 6 as a result.
     */
    @Test
    @SneakyThrows
    public void getFishingActivityListByQuery_byReportTypeAndPort() {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();

        searchCriteriaMap.put(SearchFilter.REPORT_TYPE, "DECLARATION");
        searchCriteriaMap.put(SearchFilter.PORT, "GBR");

        query.setSearchCriteriaMap(searchCriteriaMap);

        // When
        List<FishingActivityEntity> result = dao.getFishingActivityListByQuery(query);

        // Then
        assertIds(result, 6);
    }

    @Test
    @SneakyThrows
    public void getFishingActivityListByQuery_noResult() {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();

        searchCriteriaMap.put(SearchFilter.PURPOSE, "Plundering the Spanish treasure fleet");
        query.setSearchCriteriaMap(searchCriteriaMap);

        // When
        List<FishingActivityEntity> result = dao.getFishingActivityListByQuery(query);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @SneakyThrows
    public void getFishingActivityListByQuery_GetByFaReportID() {
        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();
        searchCriteriaMap.put(SearchFilter.FA_REPORT_ID, "1");

        query.setSearchCriteriaMap(searchCriteriaMap);

        List<FishingActivityEntity> finishingActivityList = dao.getFishingActivityListByQuery(query);
        assertEquals(1, finishingActivityList.size());

        FishingActivityEntity fishingActivityEntity = finishingActivityList.get(0);
        assertEquals(1, fishingActivityEntity.getFaReportDocument().getId());
    }

    @Test
    @SneakyThrows
    public void getCountForFishingActivityListByQuery_twoCountsForPort() {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();

        searchCriteriaMap.put(SearchFilter.PORT, "GBR");
        query.setSearchCriteriaMap(searchCriteriaMap);

        // When
        int count = dao.getCountForFishingActivityListByQuery(query);

        // Then
        assertEquals(2, count);
    }

    @Test
    @SneakyThrows
    public void getCountForFishingActivityListByQuery_zeroHits() {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();

        searchCriteriaMap.put(SearchFilter.PORT, "The Grey Havens");
        query.setSearchCriteriaMap(searchCriteriaMap);

        // When
        int count = dao.getCountForFishingActivityListByQuery(query);

        // Then
        assertEquals(0, count);
    }

    @Test
    @SneakyThrows
    public void getFishingActivityListForFishingTrip() {
        // Given

        // When
        List<FishingActivityEntity> result = dao.getFishingActivityListForFishingTrip("NOR-TRP-20160517234053706", null);

        // Then
        assertIds(result, 1, 2, 3);
    }

    @Test
    @SneakyThrows
    public void getFishingActivityListByQuery_byReportType_paginate() {
        // Given
        FishingActivityQuery page0Query = new FishingActivityQuery();
        FishingActivityQuery page1Query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();

        searchCriteriaMap.put(SearchFilter.REPORT_TYPE, "DECLARATION");
        page0Query.setSearchCriteriaMap(searchCriteriaMap);
        page1Query.setSearchCriteriaMap(searchCriteriaMap);

        PaginationDto page0Pagination = new PaginationDto();
        PaginationDto page1Pagination = new PaginationDto();
        page0Pagination.setPageSize(2);
        page1Pagination.setPageSize(2);
        page0Pagination.setOffset(0);
        page1Pagination.setOffset(1); // note: offset is 1 item, not 1 page
        page0Query.setPagination(page0Pagination);
        page1Query.setPagination(page1Pagination);

        // make sorting deterministic
        SortKey sortingDto = new SortKey();
        sortingDto.setSortBy(SearchFilter.OCCURRENCE);
        sortingDto.setReversed(false);
        page0Query.setSorting(sortingDto);
        page1Query.setSorting(sortingDto);

        // When
        List<FishingActivityEntity> page0Result = dao.getFishingActivityListByQuery(page0Query);
        List<FishingActivityEntity> page1Result = dao.getFishingActivityListByQuery(page1Query);

        // Then
        assertIds(page0Result, 6, 4);
        assertIds(page1Result, 4, 3); // ID 4 again, since offset is 1 item, not 1 page
    }

    private void assertIds(Collection<FishingActivityEntity> activities, Integer ... expectedIdsAsArray) {
        Set<Integer> expectedIds = Sets.newHashSet(expectedIdsAsArray);
        Set<Integer> actualIds = activities.stream()
                .map(FishingActivityEntity::getId)
                .collect(Collectors.toSet());

        assertEquals(expectedIds, actualIds);
    }
}
