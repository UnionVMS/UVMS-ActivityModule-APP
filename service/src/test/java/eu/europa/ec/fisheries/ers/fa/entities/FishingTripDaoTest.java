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

import eu.europa.ec.fisheries.ers.fa.dao.FishingTripDao;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

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
    public void testFetchVesselTransportDetailsForFishingTrip() throws Exception {

        dbSetupTracker.skipNextLaunch();
        FishingTripEntity fishingTripEntity = dao.fetchVesselTransportDetailsForFishingTrip("NOR-TRP-20160517234053706");
        assertNotNull(fishingTripEntity);
        assertNotNull(fishingTripEntity.getFaCatch());
        assertNotNull(fishingTripEntity.getFishingActivity());
    }

    @Test
    @SneakyThrows
    public void testGetFishingTripsForMatchingFilterCriteria() throws Exception {

        dbSetupTracker.skipNextLaunch();

        Map<SearchFilter,String> searchCriteriaMap = new HashMap<>();


  //      searchCriteriaMap.put(Filters.PERIOD_START, "2012-05-27T07:47:31");
   //     searchCriteriaMap.put(Filters.PERIOD_END, "2015-05-27T07:47:31");

     //   searchCriteriaMap.put(Filters.PURPOSE, "9");
  //      searchCriteriaMap.put(SearchFilter.REPORT_TYPE, "DECLARATION");
     //   searchCriteriaMap.put(SearchFilter.GEAR, "GEAR_TYPE");
    //    searchCriteriaMap.put(Filters.ACTIVITY_TYPE, "DEPARTURE");

//        searchCriteriaMap.put(Filters.SPECIES, "PLE");
 //       searchCriteriaMap.put(Filters.MASTER, "MARK");
   //     searchCriteriaMap.put(Filters.AREAS, "27.4.b");
     //   searchCriteriaMap.put(Filters.PORT, "GBR");

       // searchCriteriaMap.put(Filters.QUNTITY_MIN, "0");
      //  searchCriteriaMap.put(Filters.QUNTITY_MAX, "25");
        Map<SearchFilter,List<String>> searchCriteriaMapMultiVal = new HashMap<>();
        List<String> activityTypeValues=new ArrayList<>();
        activityTypeValues.add("FISHING_OPERATION");
        activityTypeValues.add("DEPARTURE");
        searchCriteriaMapMultiVal.put(SearchFilter.ACTIVITY_TYPE, activityTypeValues);
        List<FishingTripEntity> list= dao.getFishingTripsForMatchingFilterCriteria(searchCriteriaMap,searchCriteriaMapMultiVal);

        System.out.println("done:" + list.size());

        assertNotNull(list);

    }

}
