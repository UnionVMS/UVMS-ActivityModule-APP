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

package eu.europa.ec.fisheries.ers.service.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.dao.ActivityConfigurationDao;
import eu.europa.ec.fisheries.ers.fa.dao.FaCatchDao;
import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripIdentifierDao;
import eu.europa.ec.fisheries.ers.fa.dao.VesselIdentifierDao;
import eu.europa.ec.fisheries.ers.fa.entities.ChronologyData;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.service.MdrModuleService;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.CatchSummaryListDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.ChronologyDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.ChronologyTripDTO;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.FishingTripSummaryViewDTO;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.SortKey;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginationDto;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.SneakyThrows;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Created by padhyad on 9/29/2016.
 */
public class FishingTripServiceBeanTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    EntityManager em;

    @Mock
    FaReportDocumentDao faReportDocumentDao;

    @Mock
    FishingActivityDao fishingActivityDao;

    @Mock
    VesselIdentifierDao vesselIdentifiersDao;

    @Mock
    FishingTripIdentifierDao fishingTripIdentifierDao;

    @Mock
    FishingTripDao fishingTripDao;

    @Mock
    FaCatchDao faCatchDao;

    @Mock
    ActivityConfigurationDao activityConfigurationDao;

    @Mock
    ActivityServiceBean activityServiceBean;

    @InjectMocks
    FishingTripServiceBean fishingTripService;

    @Mock
    MdrModuleService mdrModuleService;

    static FishingActivityQuery query;

    @BeforeClass
    public static void setup(){
        query = createRequest();
    }

    private static FishingActivityQuery createRequest(){
        FishingActivityQuery query = new FishingActivityQuery();

        PaginationDto pagination = new PaginationDto();
        pagination.setOffset(0);
        pagination.setPageSize(25);


        Map<SearchFilter, List<String>> myMap = new HashMap<>();
        myMap.put(SearchFilter.PURPOSE,(Arrays.asList(new String[]{"9","1","5","3"})));
        query.setSearchCriteriaMapMultipleValues(myMap);

        query.setShowOnlyLatest(true);

        return query;
    }

    @Test
    @SneakyThrows
    public void getChronologyOfFishingTrip_limitedRecordsFound() {

        //Mock the Dao
        doReturn("XXX-XXXXX-XXXXX").when(vesselIdentifiersDao).getLatestVesselIdByTrip(any(String.class));
        Stream<ChronologyData> previousTrips = getPreviousTrips();
        doReturn(previousTrips).when(fishingTripIdentifierDao).getPreviousTrips(any(String.class), any(Date.class), any(Integer.class));
        Stream<ChronologyData> nextTrips = getNextTrips();
        doReturn(nextTrips).when(fishingTripIdentifierDao).getNextTrips(any(String.class), any(Date.class), any(Integer.class));
        doReturn(Stream.empty()).when(fishingTripIdentifierDao).getPreviousConcurrentTrips(any(String.class), any(String.class), any(Date.class), any(Integer.class));
        doReturn(Stream.empty()).when(fishingTripIdentifierDao).getNextConcurrentTrips(any(String.class), any(String.class), any(Date.class), any(Integer.class));
        doReturn(DateUtils.stringToDate("2019-03-09 14:39:00 +0000")).when(fishingTripIdentifierDao).getSelectedTripStartDate(any(String.class));

        //Trigger
        String selectedTrip = "selected Trip";
        ChronologyTripDTO chronology = fishingTripService.getChronologyOfFishingTrip(selectedTrip, 5);

        //Assert
        assertEquals(selectedTrip, chronology.getSelectedTrip().getTripId());
        List<String> previousIds = getIds(getPreviousTrips());
        assertEquals(previousIds.subList(0, chronology.getPreviousTrips().size()), chronology.getPreviousTrips().stream().map(ChronologyDTO::getTripId).collect(Collectors.toList()));
        List<String> nextIds = getIds(getNextTrips());
        Collections.reverse(nextIds);
        assertEquals(nextIds.subList(3, nextIds.size()), chronology.getNextTrips().stream().map(ChronologyDTO::getTripId).collect(Collectors.toList()));
    }

    @Test
    @SneakyThrows
    public void getChronologyOfFishingTrip_OnlyPrevious() {

        //Mock the Dao
        doReturn("XXX-XXXXX-XXXXX").when(vesselIdentifiersDao).getLatestVesselIdByTrip(any(String.class));
        Stream<ChronologyData> previousTrips = getPreviousTrips();
        doReturn(previousTrips).when(fishingTripIdentifierDao).getPreviousTrips(any(String.class), any(Date.class), any(Integer.class));
        doReturn(Stream.empty()).when(fishingTripIdentifierDao).getNextTrips(any(String.class), any(Date.class), any(Integer.class));
        doReturn(Stream.empty()).when(fishingTripIdentifierDao).getPreviousConcurrentTrips(any(String.class), any(String.class), any(Date.class), any(Integer.class));
        doReturn(Stream.empty()).when(fishingTripIdentifierDao).getNextConcurrentTrips(any(String.class), any(String.class), any(Date.class), any(Integer.class));
        doReturn(DateUtils.stringToDate("2019-03-09 14:39:00 +0000")).when(fishingTripIdentifierDao).getSelectedTripStartDate(any(String.class));

        //Trigger
        String selectedTrip = "selected Trip";
        ChronologyTripDTO chronology = fishingTripService.getChronologyOfFishingTrip(selectedTrip, 5);


        List<String> previousIds = getIds(getPreviousTrips());
        assertEquals(previousIds.subList(0, 4), chronology.getPreviousTrips().stream().map(ChronologyDTO::getTripId).collect(Collectors.toList()));
    }

    @Test
    @SneakyThrows
    public void getChronologyOfFishingTrip_All() {
        //Mock the Dao
        doReturn("XXX-XXXXX-XXXXX").when(vesselIdentifiersDao).getLatestVesselIdByTrip(any(String.class));
        Stream<ChronologyData> previousTrips = getPreviousTrips();
        doReturn(previousTrips).when(fishingTripIdentifierDao).getPreviousTrips(any(String.class), any(Date.class), any(Integer.class));
        Stream<ChronologyData> nextTrips = getNextTrips();
        doReturn(nextTrips).when(fishingTripIdentifierDao).getNextTrips(any(String.class), any(Date.class), any(Integer.class));
        doReturn(Stream.empty()).when(fishingTripIdentifierDao).getPreviousConcurrentTrips(any(String.class), any(String.class), any(Date.class), any(Integer.class));
        doReturn(Stream.empty()).when(fishingTripIdentifierDao).getNextConcurrentTrips(any(String.class), any(String.class), any(Date.class), any(Integer.class));
        doReturn(DateUtils.stringToDate("2019-03-09 14:39:00 +0000")).when(fishingTripIdentifierDao).getSelectedTripStartDate(any(String.class));


        //Trigger
        String selectedTrip = "selected Trip";
        ChronologyTripDTO chronology = fishingTripService.getChronologyOfFishingTrip(selectedTrip, 0);

        List<String> previousIds = getIds(getPreviousTrips());
        assertEquals(previousIds, chronology.getPreviousTrips().stream().map(ChronologyDTO::getTripId).collect(Collectors.toList()));
        List<String> nextIds = getIds(getNextTrips());
        Collections.reverse(nextIds);
        assertEquals(nextIds, chronology.getNextTrips().stream().map(ChronologyDTO::getTripId).collect(Collectors.toList()));
    }

    @Test
    @SneakyThrows
    public void getChronologyOfFishingTrip_OnlyCurrentAndSelected() {
        //Mock the Dao
        doReturn("XXX-XXXXX-XXXXX").when(vesselIdentifiersDao).getLatestVesselIdByTrip(any(String.class));
        Stream<ChronologyData> previousTrips = getPreviousTrips();
        doReturn(previousTrips).when(fishingTripIdentifierDao).getPreviousTrips(any(String.class), any(Date.class), any(Integer.class));
        Stream<ChronologyData> nextTrips = getNextTrips();
        doReturn(nextTrips).when(fishingTripIdentifierDao).getNextTrips(any(String.class), any(Date.class), any(Integer.class));
        doReturn(Stream.empty()).when(fishingTripIdentifierDao).getPreviousConcurrentTrips(any(String.class), any(String.class), any(Date.class), any(Integer.class));
        doReturn(Stream.empty()).when(fishingTripIdentifierDao).getNextConcurrentTrips(any(String.class), any(String.class), any(Date.class), any(Integer.class));
        doReturn(DateUtils.stringToDate("2019-03-09 14:39:00 +0000")).when(fishingTripIdentifierDao).getSelectedTripStartDate(any(String.class));

        //Trigger
        String selectedTrip = "selected Trip";
        ChronologyTripDTO chronology = fishingTripService.getChronologyOfFishingTrip(selectedTrip, 1);

        assertEquals(0, chronology.getPreviousTrips().size());
        assertEquals(0, chronology.getNextTrips().size());
    }

    private List<String> getIds(Stream<ChronologyData> entities) {
        return entities.map(ChronologyData::getTripId).collect(Collectors.toList());
    }

    private List<VesselIdentifierEntity> getVesselIdentifiers() {
        VesselIdentifierEntity vesselIdentifierEntity1 = new VesselIdentifierEntity();
        vesselIdentifierEntity1.setVesselIdentifierId("Vessel Id 1");
        vesselIdentifierEntity1.setVesselIdentifierSchemeId("Vessel Scheme Id 1");

        VesselIdentifierEntity vesselIdentifierEntity2 = new VesselIdentifierEntity();
        vesselIdentifierEntity2.setVesselIdentifierId("Vessel Id 2");

        List<VesselIdentifierEntity> identifierEntities = new ArrayList<>();
        identifierEntities.add(vesselIdentifierEntity1);
        identifierEntities.add(vesselIdentifierEntity2);

        return identifierEntities;
    }

    public Stream<ChronologyData> getPreviousTrips() {
        return Stream.of(
            new ChronologyData("SRC-2020-01-29", "2020-01-29"),
            new ChronologyData("SRC-2020-01-28", "2020-01-28"),
            new ChronologyData("SRC-2020-01-22", "2020-01-22"),
            new ChronologyData("SRC-2020-01-17", "2020-01-17"),
            new ChronologyData("SRC-2020-01-16", "2020-01-16")
        );
    }

    public Stream<ChronologyData> getNextTrips(){
        return Stream.of(
                new ChronologyData("SRC-2020-03-16", "2020-01-16"),
                new ChronologyData("SRC-2020-03-17", "2020-01-17"),
                new ChronologyData("SRC-2020-03-22", "2020-01-22"),
                new ChronologyData("SRC-2020-03-28", "2020-01-28"),
                new ChronologyData("SRC-2020-03-29", "2020-01-29")
        );
    }

    private List<FishingActivityEntity> getFishingActivityEntityList(){
        List<FishingActivityEntity> fishingActivityEntityList = MapperUtil.getFishingActivityEntityList();
        for(FishingActivityEntity fishingActivityEntity: fishingActivityEntityList){
            fishingActivityEntity.setCalculatedStartTime(new Timestamp(new Date().getTime()));
        }
        return fishingActivityEntityList;
    }

    @Test
    @SneakyThrows
    public void testGetFishingTripSummary() throws ServiceException {
        when(fishingTripDao.fetchVesselTransportDetailsForFishingTrip("NOR-TRP-20160517234053706")).thenReturn(MapperUtil.getFishingTripEntity());
        when(fishingActivityDao.getFishingActivityListForFishingTrip("NOR-TRP-20160517234053706", null)).thenReturn(MapperUtil.getFishingActivityEntityList());

        //Trigger
        FishingTripSummaryViewDTO fishingTripSummaryViewDTO = fishingTripService.getFishingTripSummaryAndReports("NOR-TRP-20160517234053706", null);

        Mockito.verify(fishingActivityDao, Mockito.times(1)).getFishingActivityListForFishingTrip(Mockito.any(String.class), Mockito.any(Geometry.class));

        //Verify
        assertEquals(3, fishingTripSummaryViewDTO.getSummary().size());
        assertEquals(3, fishingTripSummaryViewDTO.getActivityReports().size());

    }


    @Test
    @SneakyThrows
    public void testRetrieveFaCatchesForFishingTrip() throws ServiceException {

        when(faCatchDao.findFaCatchesByFishingTrip("NOR-TRP-20160517234053706")).thenReturn(MapperUtil.getFaCaches());
        //Trigger
        Map<String, CatchSummaryListDTO> faCatchesMap = fishingTripService.retrieveFaCatchesForFishingTrip("NOR-TRP-20160517234053706");
        //Verify
        Mockito.verify(faCatchDao, Mockito.times(1)).findFaCatchesByFishingTrip(Mockito.any(String.class));

        assertNotNull(faCatchesMap.get("landed"));
        assertNotNull(faCatchesMap.get("onboard"));
        assertEquals(2, faCatchesMap.get("landed").getSpeciesList().size());
        assertEquals(1, faCatchesMap.get("onboard").getSpeciesList().size());
        assertEquals((Double) 150.2, faCatchesMap.get("landed").getTotal());
        assertEquals((Double) 200.2, faCatchesMap.get("onboard").getTotal());
    }


    @Test
    @SneakyThrows
    public void testGetTripMapDetailsForTripId() throws ServiceException, JsonProcessingException {
        String expected = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiPoint\",\"coordinates\":[[-10,40],[-40,30],[-20,20],[-30,10]]},\"properties\":{}}]}";
        when(faReportDocumentDao.loadReports("NOR-TRP-20160517234053706", "Y")).thenReturn(Arrays.asList(MapperUtil.getFaReportDocumentEntity()));
        //Trigger
        ObjectNode node = fishingTripService.getTripMapDetailsForTripId("NOR-TRP-20160517234053706");
        Mockito.verify(faReportDocumentDao, Mockito.times(1)).loadReports(Mockito.any(String.class), Mockito.any(String.class));

        ObjectMapper objectMapper = new ObjectMapper();
        //Verify
        assertEquals(expected, objectMapper.writeValueAsString(node));

    }

    @Test
    @SneakyThrows
    public void testFilterFishingTrips() throws ServiceException, JsonProcessingException {

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


        when(fishingTripDao.getFishingTripIdsForMatchingFilterCriteria(any(FishingActivityQuery.class),eq(false))).thenReturn(MapperUtil.getFishingTripIdSet());
        when(fishingTripDao.getCountOfFishingTripsForMatchingFilterCriteria(any(FishingActivityQuery.class))).thenReturn(new Integer(2));
        when(fishingActivityDao.getFishingActivityListByQuery(any(FishingActivityQuery.class))).thenReturn(MapperUtil.getFishingActivityEntityList());
        //Trigger
        FishingTripResponse response = fishingTripService.filterFishingTrips(query);

        assertNotNull(response);

    }


    @Test
    @SneakyThrows
    public void testFilterFishingTripsByPERIOD_END_TRIP() throws ServiceException, JsonProcessingException {
        SortKey key = new SortKey();
        key.setReversed(false);
        key.setSortBy(SearchFilter.PERIOD_END_TRIP);
        query.setSorting(key);

        //Trigger
        FishingTripResponse response = fishingTripService.filterFishingTrips(query);
        assertNotNull(response);
    }

    @Test
    @SneakyThrows
    public void testFilterFishingTripsByPERIOD_START() throws ServiceException, JsonProcessingException {

        SortKey key = new SortKey();
        key.setReversed(false);
        key.setSortBy(SearchFilter.PERIOD_START);
        query.setSorting(key);

        //Trigger
        FishingTripResponse response = fishingTripService.filterFishingTrips(query);
        assertNotNull(response);
    }

    @Test
    @SneakyThrows
    public void testFilterFishingTripsByTRIP_ID() throws ServiceException, JsonProcessingException {

        SortKey key = new SortKey();
        key.setReversed(false);
        key.setSortBy(SearchFilter.TRIP_ID);
        query.setSorting(key);

        //Trigger
        FishingTripResponse response = fishingTripService.filterFishingTrips(query);
        assertNotNull(response);
    }

    @Test
    @SneakyThrows
    public void testFilterFishingTripsByFLAG_STATE() throws ServiceException, JsonProcessingException {

        SortKey key = new SortKey();
        key.setReversed(false);
        key.setSortBy(SearchFilter.FLAG_STATE);
        query.setSorting(key);

        //Trigger
        FishingTripResponse response = fishingTripService.filterFishingTrips(query);
        assertNotNull(response);
    }


    @Test
    @SneakyThrows
    public void testBuildFishingTripSearchRespose() throws ServiceException, JsonProcessingException {

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
}
