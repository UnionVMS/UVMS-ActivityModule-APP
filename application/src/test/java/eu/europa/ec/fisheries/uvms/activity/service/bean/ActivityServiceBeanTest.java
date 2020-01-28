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
package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FilterFishingActivityReportResultDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginationDto;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActivityServiceBeanTest {

    @InjectMocks
    private ActivityServiceBean activityService;

    @Mock
    private FishingActivityDao fishingActivityDao;

    @Mock
    private FaReportDocumentDao faReportDocumentDao;

    @Test
    public void getNextFishingActivity() {
        // Given
        int fishingActivityId = 5;
        int expectedNextFishingActivityId = 18;

        Instant now = Instant.now();

        FishingTripEntity fishingTrip = new FishingTripEntity();

        FishingActivityEntity first = new FishingActivityEntity();
        first.setId(expectedNextFishingActivityId);
        first.setOccurence(now.plus(5, ChronoUnit.MINUTES));

        FishingActivityEntity second = new FishingActivityEntity();
        second.setId(2);
        second.setOccurence(now.minus(1, ChronoUnit.MINUTES));

        FishingActivityEntity third = new FishingActivityEntity();
        third.setId(fishingActivityId);
        third.setOccurence(now);

        FishingActivityEntity fourth = new FishingActivityEntity();
        fourth.setId(10);
        fourth.setOccurence(now.plus(55, ChronoUnit.MINUTES));

        FishingActivityEntity fifth = new FishingActivityEntity();
        fifth.setId(8);
        fifth.setOccurence(now.minus(20, ChronoUnit.MINUTES));

        fishingTrip.getFishingActivities().add(first);
        fishingTrip.getFishingActivities().add(second);
        fishingTrip.getFishingActivities().add(third);
        fishingTrip.getFishingActivities().add(fourth);
        fishingTrip.getFishingActivities().add(fifth);

        third.setFishingTrip(fishingTrip);

        when(fishingActivityDao.getFishingActivityById(fishingActivityId, null)).thenReturn(third);

        // When
        int nextFishingActivity = activityService.getNextFishingActivity(fishingActivityId);

        // Then
        assertEquals(expectedNextFishingActivityId, nextFishingActivity);
    }

    @Test
    public void getNextFishingActivity_noNextActivity() {
        // Given
        int fishingActivityId = 5;

        FishingTripEntity fishingTrip = new FishingTripEntity();

        FishingActivityEntity fishingActivity = new FishingActivityEntity();

        fishingTrip.getFishingActivities().add(fishingActivity);

        fishingActivity.setFishingTrip(fishingTrip);

        when(fishingActivityDao.getFishingActivityById(fishingActivityId, null)).thenReturn(fishingActivity);

        // When
        int nextFishingActivity = activityService.getNextFishingActivity(fishingActivityId);

        // Then
        assertEquals(-1, nextFishingActivity);
    }

    @Test
    public void getNextFishingActivity_noActivity() {
        // Given
        int fishingActivityId = 5;

        when(fishingActivityDao.getFishingActivityById(fishingActivityId, null)).thenReturn(null);

        // When
        int nextFishingActivity = activityService.getNextFishingActivity(fishingActivityId);

        // Then
        assertEquals(-1, nextFishingActivity);
    }

    @Test
    public void getNextFishingActivity_noFishingTrip() {
        // Given
        int fishingActivityId = 5;

        FishingActivityEntity fishingActivity = new FishingActivityEntity();

        fishingActivity.setFishingTrip(null);

        when(fishingActivityDao.getFishingActivityById(fishingActivityId, null)).thenReturn(fishingActivity);

        // When
        int nextFishingActivity = activityService.getNextFishingActivity(fishingActivityId);

        // Then
        assertEquals(-1, nextFishingActivity);
    }

    @Test
    public void getPreviousFishingActivity() {
        // Given
        int fishingActivityId = 5;
        int expectedPreviousFishingActivityId = 18;

        Instant now = Instant.now();

        FishingTripEntity fishingTrip = new FishingTripEntity();

        FishingActivityEntity first = new FishingActivityEntity();
        first.setId(4);
        first.setOccurence(now.plus(5, ChronoUnit.MINUTES));

        FishingActivityEntity second = new FishingActivityEntity();
        second.setId(2);
        second.setOccurence(now.minus(21, ChronoUnit.MINUTES));

        FishingActivityEntity third = new FishingActivityEntity();
        third.setId(fishingActivityId);
        third.setOccurence(now);

        FishingActivityEntity fourth = new FishingActivityEntity();
        fourth.setId(10);
        fourth.setOccurence(now.plus(55, ChronoUnit.MINUTES));

        FishingActivityEntity fifth = new FishingActivityEntity();
        fifth.setId(expectedPreviousFishingActivityId);
        fifth.setOccurence(now.minus(20, ChronoUnit.MINUTES));

        fishingTrip.getFishingActivities().add(first);
        fishingTrip.getFishingActivities().add(second);
        fishingTrip.getFishingActivities().add(third);
        fishingTrip.getFishingActivities().add(fourth);
        fishingTrip.getFishingActivities().add(fifth);

        third.setFishingTrip(fishingTrip);

        when(fishingActivityDao.getFishingActivityById(fishingActivityId, null)).thenReturn(third);

        // When
        int previousFishingActivity = activityService.getPreviousFishingActivity(fishingActivityId);

        // Then
        assertEquals(expectedPreviousFishingActivityId, previousFishingActivity);
    }

    @Test
    public void getPreviousFishingActivity_noPreviousActivity() {
        // Given
        int fishingActivityId = 5;

        FishingTripEntity fishingTrip = new FishingTripEntity();

        FishingActivityEntity fishingActivity = new FishingActivityEntity();

        fishingTrip.getFishingActivities().add(fishingActivity);

        fishingActivity.setFishingTrip(fishingTrip);

        when(fishingActivityDao.getFishingActivityById(fishingActivityId, null)).thenReturn(fishingActivity);

        // When
        int previousFishingActivity = activityService.getPreviousFishingActivity(fishingActivityId);

        // Then
        assertEquals(-1, previousFishingActivity);
    }

    @Test
    public void getPreviousFishingActivity_noActivity() {
        // Given
        int fishingActivityId = 5;

        when(fishingActivityDao.getFishingActivityById(fishingActivityId, null)).thenReturn(null);

        // When
        int previousFishingActivity = activityService.getPreviousFishingActivity(fishingActivityId);

        // Then
        assertEquals(-1, previousFishingActivity);
    }

    @Test
    public void getPreviousFishingActivity_noFishingTrip() {
        // Given
        int fishingActivityId = 5;

        FishingTripEntity fishingTrip = new FishingTripEntity();

        FishingActivityEntity fishingActivity = new FishingActivityEntity();

        fishingTrip.getFishingActivities().add(fishingActivity);

        fishingActivity.setFishingTrip(null);

        when(fishingActivityDao.getFishingActivityById(fishingActivityId, null)).thenReturn(fishingActivity);

        // When
        int previousFishingActivity = activityService.getPreviousFishingActivity(fishingActivityId);

        // Then
        assertEquals(-1, previousFishingActivity);
    }

    @Test
    @SneakyThrows
    public void testGetFaReportCorrections() {

        //Mock
        final FaReportDocumentEntity faReportDocumentEntity = MapperUtil.getFaReportDocumentEntity();
        List<FaReportDocumentEntity> faRepsArraResp = new ArrayList<FaReportDocumentEntity>(){{
            add(faReportDocumentEntity);
        }};
        Mockito.doReturn(faReportDocumentEntity).when(faReportDocumentDao).findFaReportByIdAndScheme(Mockito.any(String.class), Mockito.any(String.class));
        Mockito.doReturn(faRepsArraResp).when(faReportDocumentDao).getHistoryOfFaReport(Mockito.any(FaReportDocumentEntity.class));

        //Trigger
        List<FaReportCorrectionDTO> faReportCorrectionDTOList = activityService.getFaReportHistory("TEST ID", "SCHEME ID");

        //Verify
        Mockito.verify(faReportDocumentDao, Mockito.times(1)).findFaReportByIdAndScheme("TEST ID", "SCHEME ID");

        //Test
        FaReportCorrectionDTO faReportCorrectionDTO = faReportCorrectionDTOList.get(0);
        assertEquals(faReportDocumentEntity.getStatus(), faReportCorrectionDTO.getCorrectionType());
        assertEquals(faReportDocumentEntity.getFluxReportDocument_CreationDatetime().toEpochMilli(), faReportCorrectionDTO.getCreationDate().toInstant().toEpochMilli());
        assertEquals(faReportDocumentEntity.getAcceptedDatetime().toEpochMilli(), faReportCorrectionDTO.getAcceptedDate().toInstant().toEpochMilli());
        assertEquals(faReportDocumentEntity.getFluxReportDocument_Id(),
                faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getKey());
        assertEquals(faReportDocumentEntity.getFluxReportDocument_IdSchemeId(),
                faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getValue());
        assertEquals(faReportDocumentEntity.getFluxParty_name(), faReportCorrectionDTO.getOwnerFluxPartyName());
    }

    @Test
    @SneakyThrows
    public void getFishingActivityListByQuery() {
        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter,String> searchCriteriaMap = new HashMap<>();
        searchCriteriaMap.put(SearchFilter.OWNER, "OWNER1");
        Map<SearchFilter,List<String>> searchCriteriaMapMultipleValue = new HashMap<>();
        List<String> purposeCodeList= new ArrayList<>();
        purposeCodeList.add("9");
        searchCriteriaMapMultipleValue.put(SearchFilter.PURPOSE,purposeCodeList);
        PaginationDto pagination =new PaginationDto();
        pagination.setPageSize(4);
        pagination.setOffset(1);
        query.setPagination(pagination);
        query.setSearchCriteriaMap(searchCriteriaMap);
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultipleValue);

        when(fishingActivityDao.getFishingActivityListByQuery(query)).thenReturn(MapperUtil.getFishingActivityEntityList());
        //Trigger
        FilterFishingActivityReportResultDTO filterFishingActivityReportResultDTO= activityService.getFishingActivityListByQuery(query, null);
        Mockito.verify(fishingActivityDao, Mockito.times(1)).getFishingActivityListByQuery(Mockito.any(FishingActivityQuery.class));
        //Verify
        assertNotNull(filterFishingActivityReportResultDTO);
        assertNotNull(filterFishingActivityReportResultDTO.getResultList());
    }


    @Test
    @SneakyThrows
    public void getFishingActivityListByQuery_emptyResultSet() {

        FishingActivityQuery query = new FishingActivityQuery();

        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();
        searchCriteriaMap.put(SearchFilter.OWNER, "OWNER1");

        Map<SearchFilter,List<String>> searchCriteriaMapMultipleValue = new HashMap<>();
        List<String> purposeCodeList = new ArrayList<>();
        purposeCodeList.add("9");
        searchCriteriaMapMultipleValue.put(SearchFilter.PURPOSE,purposeCodeList);

        PaginationDto pagination = new PaginationDto();
        pagination.setPageSize(4);
        pagination.setOffset(1);
        query.setPagination(pagination);
        query.setSearchCriteriaMap(searchCriteriaMap);
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultipleValue);

        when(fishingActivityDao.getFishingActivityListByQuery(query)).thenReturn(new ArrayList<>());

        //Trigger
        FilterFishingActivityReportResultDTO filterFishingActivityReportResultDTO= activityService.getFishingActivityListByQuery(query, new ArrayList<>());

        Mockito.verify(fishingActivityDao, Mockito.times(1)).getFishingActivityListByQuery(Mockito.any(FishingActivityQuery.class));

        //Verify
        assertNotNull(filterFishingActivityReportResultDTO);
    }
}




































