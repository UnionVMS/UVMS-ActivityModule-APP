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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.dao.*;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.message.producer.ActivityMessageProducer;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.CatchSummaryListDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.CronologyTripDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.FishingTripSummaryViewDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.SneakyThrows;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.persistence.EntityManager;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * Created by padhyad on 9/29/2016.
 */
public class FishingTripServiceBeanTest {

    @Mock
    EntityManager em;

    @Mock
    ActivityMessageProducer producer;

    @Mock
    AssetsMessageConsumerBean consumer;

    @Mock
    FaReportDocumentDao faReportDocumentDao;

    @Mock
    FishingActivityDao fishingActivityDao;

    @Mock
    VesselIdentifiersDao vesselIdentifiersDao;

    @Mock
    FishingTripIdentifierDao fishingTripIdentifierDao;

    @Mock
    FishingTripDao fishingTripDao;

    @Mock
    FaCatchDao faCatchDao;

    @InjectMocks
    FishingTripServiceBean fishingTripService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    @SneakyThrows
    public void getCronologyOfFishingTrip_limitedRecordsFound() {

        //Mock the Dao
        doReturn(getVesselIdentifiers()).when(vesselIdentifiersDao).getLatestVesselIdByTrip(any(String.class));
        FishingTripIdentifierEntity fishingTripIdentifierEntity = getCurrentTrip();
        doReturn(fishingTripIdentifierEntity).when(fishingTripIdentifierDao).getCurrentTrip(any(String.class), any(String.class));
        List<FishingTripIdentifierEntity> previousTrips = getPreviousTrips(5);
        doReturn(previousTrips).when(fishingTripIdentifierDao).getPreviousTrips(any(String.class), any(String.class), any(String.class), any(Integer.class));
        List<FishingTripIdentifierEntity> nextTrips = getNextTrips(5);
        doReturn(nextTrips).when(fishingTripIdentifierDao).getNextTrips(any(String.class), any(String.class), any(String.class), any(Integer.class));

        //Trigger
        String selectedTrip = "selected Trip";
        CronologyTripDTO cronology = fishingTripService.getCronologyOfFishingTrip(selectedTrip, 5);

        //Assert
        assertEquals(selectedTrip, cronology.getSelectedTrip());
        assertEquals(fishingTripIdentifierEntity.getTripId(), cronology.getCurrentTrip());
        List<String> previousIds = getIds(previousTrips);
        assertEquals(previousIds.subList(3, previousIds.size()), cronology.getPreviousTrips());
        List<String> nextIds = getIds(nextTrips);
        assertEquals(nextIds.subList(0, 2), cronology.getNextTrips());
    }

    @Test
    @SneakyThrows
    public void getCronologyOfFishingTrip_OnlyPrevious() {
        //Mock the Dao
        doReturn(getVesselIdentifiers()).when(vesselIdentifiersDao).getLatestVesselIdByTrip(any(String.class));
        FishingTripIdentifierEntity fishingTripIdentifierEntity = getCurrentTrip();
        doReturn(fishingTripIdentifierEntity).when(fishingTripIdentifierDao).getCurrentTrip(any(String.class), any(String.class));
        List<FishingTripIdentifierEntity> previousTrips = getPreviousTrips(2);
        doReturn(previousTrips).when(fishingTripIdentifierDao).getPreviousTrips(any(String.class), any(String.class), any(String.class), any(Integer.class));
        List<FishingTripIdentifierEntity> nextTrips = getNextTrips(2);
        doReturn(nextTrips).when(fishingTripIdentifierDao).getNextTrips(any(String.class), any(String.class), any(String.class), any(Integer.class));

        //Trigger
        String selectedTrip = "selected Trip";
        CronologyTripDTO cronology = fishingTripService.getCronologyOfFishingTrip(selectedTrip, 2);

        assertEquals(selectedTrip, cronology.getSelectedTrip());
        assertEquals(fishingTripIdentifierEntity.getTripId(), cronology.getCurrentTrip());
        List<String> previousIds = getIds(previousTrips);
        assertEquals(previousIds.subList(1, previousIds.size()), cronology.getPreviousTrips());
    }


    @Test
    @SneakyThrows
    public void getCronologyOfFishingTrip_All() {
        //Mock the Dao
        doReturn(getVesselIdentifiers()).when(vesselIdentifiersDao).getLatestVesselIdByTrip(any(String.class));
        FishingTripIdentifierEntity fishingTripIdentifierEntity = getCurrentTrip();
        doReturn(fishingTripIdentifierEntity).when(fishingTripIdentifierDao).getCurrentTrip(any(String.class), any(String.class));
        List<FishingTripIdentifierEntity> previousTrips = getPreviousTrips(10);
        doReturn(previousTrips).when(fishingTripIdentifierDao).getPreviousTrips(any(String.class), any(String.class), any(String.class), any(Integer.class));
        List<FishingTripIdentifierEntity> nextTrips = getNextTrips(10);
        doReturn(nextTrips).when(fishingTripIdentifierDao).getNextTrips(any(String.class), any(String.class), any(String.class), any(Integer.class));

        //Trigger
        String selectedTrip = "selected Trip";
        CronologyTripDTO cronology = fishingTripService.getCronologyOfFishingTrip(selectedTrip, 0);

        assertEquals(selectedTrip, cronology.getSelectedTrip());
        assertEquals(fishingTripIdentifierEntity.getTripId(), cronology.getCurrentTrip());
        List<String> previousIds = getIds(previousTrips);
        assertEquals(previousIds, cronology.getPreviousTrips());
        List<String> nextIds = getIds(nextTrips);
        assertEquals(nextIds, cronology.getNextTrips());
    }

    @Test
    @SneakyThrows
    public void getCronologyOfFishingTrip_OnlyCurrentAndSelected() {
        //Mock the Dao
        doReturn(getVesselIdentifiers()).when(vesselIdentifiersDao).getLatestVesselIdByTrip(any(String.class));
        FishingTripIdentifierEntity fishingTripIdentifierEntity = getCurrentTrip();
        doReturn(fishingTripIdentifierEntity).when(fishingTripIdentifierDao).getCurrentTrip(any(String.class), any(String.class));
        List<FishingTripIdentifierEntity> previousTrips = getPreviousTrips(5);
        doReturn(previousTrips).when(fishingTripIdentifierDao).getPreviousTrips(any(String.class), any(String.class), any(String.class), any(Integer.class));
        List<FishingTripIdentifierEntity> nextTrips = getNextTrips(5);
        doReturn(nextTrips).when(fishingTripIdentifierDao).getNextTrips(any(String.class), any(String.class), any(String.class), any(Integer.class));

        //Trigger
        String selectedTrip = "selected Trip";
        CronologyTripDTO cronology = fishingTripService.getCronologyOfFishingTrip(selectedTrip, 1);

        assertEquals(selectedTrip, cronology.getSelectedTrip());
        assertEquals(fishingTripIdentifierEntity.getTripId(), cronology.getCurrentTrip());

        assertEquals(0, cronology.getPreviousTrips().size());
        assertEquals(0, cronology.getNextTrips().size());
    }

    private List<String> getIds(List<FishingTripIdentifierEntity> entities) {
        List<String> ids = new ArrayList<>();
        for (FishingTripIdentifierEntity trip : entities) {
            ids.add(trip.getTripId());
        }
        return ids;
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

    public FishingTripIdentifierEntity getCurrentTrip() {
        FishingTripIdentifierEntity identifier = new FishingTripIdentifierEntity();
        identifier.setTripId("Current Trip");
        identifier.setTripSchemeId("Current Trip scheme Id");
        return identifier;
    }

    public List<FishingTripIdentifierEntity> getPreviousTrips(int numberOfRecord) {
        List<FishingTripIdentifierEntity> previousTrips = new ArrayList<>();
        for (int i = 1 ; i <= numberOfRecord ; i++) {
            FishingTripIdentifierEntity identifier = new FishingTripIdentifierEntity();
            identifier.setTripId("Previous Trip " + i);
            identifier.setTripSchemeId("Previous Scheme " + i);
            previousTrips.add(identifier);
        }
        return previousTrips;
    }

    public List<FishingTripIdentifierEntity> getNextTrips(int numberOfRecord) {
        List<FishingTripIdentifierEntity> nextTrips = new ArrayList<>();
        for (int i = 1 ; i <= numberOfRecord ; i++) {
            FishingTripIdentifierEntity identifier = new FishingTripIdentifierEntity();
            identifier.setTripId("Next Trip " + i);
            identifier.setTripSchemeId("Next Scheme " + i);
            nextTrips.add(identifier);
        }
        return nextTrips;
    }


    @Test
    @SneakyThrows
    public void testGetFishingTripSummary() throws ServiceException {
        when(fishingTripDao.fetchVesselTransportDetailsForFishingTrip("NOR-TRP-20160517234053706")).thenReturn(MapperUtil.getFishingTripEntity());
        when(fishingActivityDao.getFishingActivityListForFishingTrip("NOR-TRP-20160517234053706", null)).thenReturn(MapperUtil.getFishingActivityEntityList());

        //Trigger
        FishingTripSummaryViewDTO fishingTripSummaryViewDTO=fishingTripService.getFishingTripSummaryAndReports("NOR-TRP-20160517234053706", null);

        Mockito.verify(fishingActivityDao, Mockito.times(1)).getFishingActivityListForFishingTrip(Mockito.any(String.class), Mockito.any(Geometry.class));

        //Verify
        assertEquals(3, fishingTripSummaryViewDTO.getSummary().size());
        assertEquals(3, fishingTripSummaryViewDTO.getActivityReports().size());

    }


    @Test
    @SneakyThrows
    public void testRetreiveCachesByTripId() throws ServiceException {

        when(faCatchDao.findFaCatchesByFishingTrip("NOR-TRP-20160517234053706")).thenReturn(MapperUtil.getFaCaches());
        //Trigger
        Map<String, CatchSummaryListDTO> faCatchesMap =fishingTripService.retrieveFaCatchesForFishingTrip("NOR-TRP-20160517234053706");
        //Verify
        Mockito.verify(faCatchDao, Mockito.times(1)).findFaCatchesByFishingTrip(Mockito.any(String.class));

        assertNotNull(faCatchesMap.get("landed"));
        assertNotNull(faCatchesMap.get("onboard"));
        assertEquals(2, faCatchesMap.get("landed").getSpeciesList().size());
        assertEquals(2, faCatchesMap.get("onboard").getSpeciesList().size());
        assertEquals((Double) 150.2, faCatchesMap.get("landed").getTotal());
        assertEquals((Double) 200.2, faCatchesMap.get("onboard").getTotal());
    }


    @Test
    @SneakyThrows
    public void testGetTripMapDetailsForTripId() throws ServiceException, JsonProcessingException {
        String expected="{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiPoint\",\"coordinates\":[[-10,40],[-40,30],[-20,20],[-30,10]]},\"properties\":{}}]}" ;
        when(faReportDocumentDao.getLatestFaReportDocumentsForTrip("NOR-TRP-20160517234053706")).thenReturn(Arrays.asList(MapperUtil.getFaReportDocumentEntity()));
        //Trigger
        ObjectNode node = fishingTripService.getTripMapDetailsForTripId("NOR-TRP-20160517234053706");
        Mockito.verify(faReportDocumentDao, Mockito.times(1)).getLatestFaReportDocumentsForTrip(Mockito.any(String.class));

        ObjectMapper objectMapper = new ObjectMapper();
        //Verify
        assertEquals(expected,objectMapper.writeValueAsString(node));

    }

  //  @Test
    @SneakyThrows
    public void testGetFishingTripIdsForFilter() throws ServiceException, JsonProcessingException {

        Map<SearchFilter,String> searchMap=new HashMap<>();
        searchMap.put(SearchFilter.REPORT_TYPE,"NOTIFICATION");

        Map<SearchFilter,List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        List<String> activityTypeValues=new ArrayList<>();
        activityTypeValues.add("FISHING_OPERATION");
        activityTypeValues.add("DEPARTURE");
        searchCriteriaMapMultiVal.put(SearchFilter.ACTIVITY_TYPE, activityTypeValues);
        when(fishingTripDao.getFishingTripsForMatchingFilterCriteria(searchMap,searchCriteriaMapMultiVal)).thenReturn(Arrays.asList(MapperUtil.getFishingTripEntity()));
        //Trigger
        FishingTripResponse response = fishingTripService.getFishingTripIdsForFilter(searchMap,searchCriteriaMapMultiVal);
        Mockito.verify(fishingTripDao, Mockito.times(1)).getFishingTripsForMatchingFilterCriteria(Mockito.any(Map.class),Mockito.any(Map.class));
        System.out.println("response:"+response);
        assertNotNull(response);
     //   assertNotEquals(0,response.getFishingTripIds().size());

    }

}
