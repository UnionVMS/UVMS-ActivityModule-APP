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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.FaCatchDao;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.FishingTripDao;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.VesselTransportMeansDao;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.activity.service.MdrModuleService;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.CatchSummaryListDTO;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.service.search.SortKey;
import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import lombok.SneakyThrows;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FishingTripServiceBeanTest {

    @Mock
    private FaReportDocumentDao faReportDocumentDao;

    @Mock
    private FishingActivityDao fishingActivityDao;

    @Mock
    private FishingTripDao fishingTripDao;

    @Mock
    private FaCatchDao faCatchDao;

    @Mock
    private ActivityServiceBean activityServiceBean;

    @InjectMocks
    private FishingTripServiceBean fishingTripService;

    @Mock
    private MdrModuleService mdrModuleService;

    @Mock
    private AssetModuleServiceBean assetModule;

    @Mock
    private VesselTransportMeansDao vesselTransportMeansDao;

    @Test
    @SneakyThrows
    public void retrieveFaCatchesForFishingTrip() {

        when(faCatchDao.findFaCatchesByFishingTrip("NOR-TRP-20160517234053706")).thenReturn(MapperUtil.getFaCaches());
        //Trigger
        Map<String, CatchSummaryListDTO> faCatchesMap = fishingTripService.retrieveFaCatchesForFishingTrip("NOR-TRP-20160517234053706");
        //Verify
        Mockito.verify(faCatchDao, times(1)).findFaCatchesByFishingTrip(Mockito.any(String.class));

        assertNotNull(faCatchesMap.get("landed"));
        assertNotNull(faCatchesMap.get("onboard"));
        assertEquals(2, faCatchesMap.get("landed").getSpeciesList().size());
        assertEquals(1, faCatchesMap.get("onboard").getSpeciesList().size());
        assertEquals((Double) 150.2, faCatchesMap.get("landed").getTotal());
        assertEquals((Double) 200.2, faCatchesMap.get("onboard").getTotal());
    }


    @Test
    @SneakyThrows
    public void getTripMapDetailsForTripId() {
        String expected = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiPoint\",\"coordinates\":[[-10,40],[-40,30],[-20,20],[-30,10]]},\"properties\":{}}]}";
        when(faReportDocumentDao.loadReports("NOR-TRP-20160517234053706", "Y")).thenReturn(Arrays.asList(MapperUtil.getFaReportDocumentEntity()));
        //Trigger
        ObjectNode node = fishingTripService.getTripMapDetailsForTripId("NOR-TRP-20160517234053706");
        Mockito.verify(faReportDocumentDao, times(1)).loadReports(Mockito.any(String.class), Mockito.any(String.class));

        ObjectMapper objectMapper = new ObjectMapper();
        //Verify
        assertEquals(expected, objectMapper.writeValueAsString(node));

    }

    @Test
    @SneakyThrows
    public void filterFishingTrips() {

        Map<SearchFilter, String> searchMap = new HashMap<>();
        searchMap.put(SearchFilter.REPORT_TYPE, "NOTIFICATION");
        searchMap.put(SearchFilter.PERIOD_START, "2012-05-27T07:47:31");
        searchMap.put(SearchFilter.PERIOD_END, "2017-05-27T07:47:31");


        Map<SearchFilter, List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        List<String> activityTypeValues = new ArrayList<>();
        activityTypeValues.add("FISHING_OPERATION");
        activityTypeValues.add("DEPARTURE");
        searchCriteriaMapMultiVal.put(SearchFilter.ACTIVITY_TYPE, activityTypeValues);

        FishingActivityQuery query = new FishingActivityQuery();
        query.setSearchCriteriaMap(searchMap);
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultiVal);


        when(fishingTripDao.getFishingTripIdsForMatchingFilterCriteria(any(FishingActivityQuery.class))).thenReturn(MapperUtil.getFishingTripIdSet());
        when(fishingTripDao.getCountOfFishingTripsForMatchingFilterCriteria(any(FishingActivityQuery.class))).thenReturn(2);
        when(fishingActivityDao.getFishingActivityListByQuery(any(FishingActivityQuery.class))).thenReturn(MapperUtil.getFishingActivityEntityList());
        //Trigger
        FishingTripResponse response = fishingTripService.filterFishingTrips(query);

        assertNotNull(response);

    }

    @Test
    @SneakyThrows
    public void buildFishingTripSearchResponse() {

        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new EnumMap<>(SearchFilter.class);
        searchCriteriaMap.put(SearchFilter.TRIP_ID, "NOR-TRP-20160517234053706");
        searchCriteriaMap.put(SearchFilter.FISHING_TRIP_SCHEME_ID, "EU_TRIP_ID");
        query.setSearchCriteriaMap(searchCriteriaMap);
        SortKey sortKey = new SortKey();
        sortKey.setSortBy(SearchFilter.PERIOD_START);
        sortKey.setReversed(false);
        query.setSorting(sortKey);


        when(fishingActivityDao.getFishingActivityListByQuery(any(FishingActivityQuery.class))).thenReturn(MapperUtil.getFishingActivityEntityList());
        //Trigger
        FishingTripResponse response = fishingTripService.buildFishingTripSearchRespose(MapperUtil.getFishingTripIdSet(), false);

        assertNotNull(response);

    }

    @Test
    @SneakyThrows
    public void getVesselDetailsForFishingTrip() {
        // Given
        Map<String, List<String>> returnMap = new HashMap<>();
        returnMap.put("code", new ArrayList<>());
        VesselTransportMeansEntity vesselTransportMeansEntity = new VesselTransportMeansEntity();
        vesselTransportMeansEntity.setName("MyFirstVessel");
        when(vesselTransportMeansDao.findLatestVesselByTripId("NOR-TRP-20160517234053706")).thenReturn(vesselTransportMeansEntity);
        when(mdrModuleService.getAcronymFromMdr("FLUX_VESSEL_ID_TYPE", "*", new ArrayList<>(), 9999999, "code")).thenReturn(returnMap);

        // When
        VesselDetailsDTO vesselDetailsDTO = fishingTripService.getVesselDetailsForFishingTrip("NOR-TRP-20160517234053706");

        // Then
        Mockito.verify(vesselTransportMeansDao, times(1)).findLatestVesselByTripId("NOR-TRP-20160517234053706");
        assertEquals("MyFirstVessel", vesselDetailsDTO.getName());
    }
}
