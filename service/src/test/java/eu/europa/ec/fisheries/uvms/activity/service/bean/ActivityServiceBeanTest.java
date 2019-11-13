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
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingActivityForTripIds;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetFishingActivitiesForTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.activity.service.SpatialModuleService;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FilterFishingActivityReportResultDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginationDto;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActivityServiceBeanTest {

    @InjectMocks
    private ActivityServiceBean activityService;

    @Mock
    private FishingActivityDao fishingActivityDao;

    @Mock
    private FaReportDocumentDao faReportDocumentDao;

    @Mock
    private SpatialModuleService spatialModule;

    @Test
    @SneakyThrows
    public void testGetFaReportCorrections() {

        //Mock
        final FaReportDocumentEntity faReportDocumentEntity = MapperUtil.getFaReportDocumentEntity();
        List<FaReportDocumentEntity> faRepsArraResp = new ArrayList<FaReportDocumentEntity>(){{
            add(faReportDocumentEntity);
        }};
        Mockito.doReturn(faReportDocumentEntity).when(faReportDocumentDao).findFaReportByIdAndScheme(Mockito.any(String.class), Mockito.any(String.class));
        Mockito.doReturn(faRepsArraResp).when(faReportDocumentDao).getHistoryOfFaReport(Mockito.any(FaReportDocumentEntity.class), anyListOf(FaReportDocumentEntity.class));

        //Trigger
        List<FaReportCorrectionDTO> faReportCorrectionDTOList = activityService.getFaReportHistory("TEST ID", "SCHEME ID");

        //Verify
        Mockito.verify(faReportDocumentDao, Mockito.times(1)).findFaReportByIdAndScheme("TEST ID", "SCHEME ID");

        //Test
        FaReportCorrectionDTO faReportCorrectionDTO = faReportCorrectionDTOList.get(0);
        assertEquals(faReportDocumentEntity.getStatus(), faReportCorrectionDTO.getCorrectionType());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getCreationDatetime(), faReportCorrectionDTO.getCreationDate().toInstant());
        assertEquals(faReportDocumentEntity.getAcceptedDatetime(), faReportCorrectionDTO.getAcceptedDate().toInstant());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getFluxReportIdentifiers().iterator().next().getFluxReportIdentifierId(),
                faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getKey());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getFluxReportIdentifiers().iterator().next().getFluxReportIdentifierSchemeId(),
                faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getValue());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getFluxParty().getFluxPartyName(), faReportCorrectionDTO.getOwnerFluxPartyName());
    }


    @Test
    @SneakyThrows
    public void getFishingActivityListByQuery() {
        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter,String> searchCriteriaMap = new HashMap<>();
        searchCriteriaMap.put(SearchFilter.OWNER, "OWNER1");
        List<AreaIdentifierType> areaIdentifierTypes =new ArrayList<>();
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

        when(spatialModule.getFilteredAreaGeom(areaIdentifierTypes)).thenReturn("('MULTIPOINT (10 40, 40 30, 20 20, 30 10)')");
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
        List<AreaIdentifierType> areaIdentifierTypes =new ArrayList<>();
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

        when(spatialModule.getFilteredAreaGeom(areaIdentifierTypes)).thenReturn("('MULTIPOINT (10 40, 40 30, 20 20, 30 10)')");

        when(fishingActivityDao.getFishingActivityListByQuery(query)).thenReturn(new ArrayList<FishingActivityEntity>());

        //Trigger
        FilterFishingActivityReportResultDTO filterFishingActivityReportResultDTO= activityService.getFishingActivityListByQuery(query, new ArrayList<Dataset>());

        Mockito.verify(fishingActivityDao, Mockito.times(1)).getFishingActivityListByQuery(Mockito.any(FishingActivityQuery.class));
        //Verify
         assertNotNull(filterFishingActivityReportResultDTO);
    }

    @Test
    @SneakyThrows
    public void testGetFaAndTripIdsResponse(){
        List<String> flRepPurpCodes = new ArrayList<String>(){{add("1"); add("9");}};
        when(fishingActivityDao.getFishingActivityForTrip(any(String.class), any(String.class), any(String.class), anyListOf(String.class))).thenReturn(mockFishActEntities());

        FishingActivityForTripIds fishingActivityForTripIds = new FishingActivityForTripIds("faTypeCode", "tripId", "tripSchemeId", flRepPurpCodes);
        List<FishingActivityForTripIds> fishingActivityForTripIdsList = Collections.singletonList(fishingActivityForTripIds);
        GetFishingActivitiesForTripResponse response = activityService.getFaAndTripIdsFromTripIds(fishingActivityForTripIdsList);

        assertNotNull(response);
        assertTrue(CollectionUtils.isNotEmpty(response.getFaWithIdentifiers()));
    }

    private List<FishingActivityEntity> mockFishActEntities() {
        final FishingActivityEntity fishAct = new FishingActivityEntity();

        FishingActivityIdentifierEntity ident = new FishingActivityIdentifierEntity();
        ident.setFaIdentifierId("faId");
        ident.setFaIdentifierSchemeId("faSchemeId");
        Set<FishingActivityIdentifierEntity> fishIdentList = new HashSet<>();
        fishIdentList.add(ident);

        Set<FishingTripEntity> fishTrips = new HashSet<>();
        FishingTripEntity fishTrip = new FishingTripEntity();

        Set<FishingTripIdentifierEntity> fishingTripIdentifiers = new HashSet<>();
        FishingTripIdentifierEntity tripident = new FishingTripIdentifierEntity();
        tripident.setTripId("tripId");
        tripident.setTripSchemeId("tripSchemeId");
        fishingTripIdentifiers.add(tripident);

        fishTrips.add(fishTrip);

        fishTrip.setTypeCode("someTripCode");
        fishTrip.setFishingActivity(fishAct);
        fishTrip.setFishingTripIdentifiers(fishingTripIdentifiers);

        fishAct.setFishingActivityIdentifiers(fishIdentList);

        fishAct.setTypeCode("faTypeCode");

        fishAct.setFishingTrips(fishTrips);

        return new ArrayList<FishingActivityEntity>(){{add(fishAct);}};
    }
}




































