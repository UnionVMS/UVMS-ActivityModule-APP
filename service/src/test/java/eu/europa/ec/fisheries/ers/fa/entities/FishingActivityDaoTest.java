/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.service.search.Filters;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.ListCriteria;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class FishingActivityDaoTest extends BaseErsFaDaoTest{

private FishingActivityDao dao=new FishingActivityDao(em);

 @Before   
  public void prepare(){
      Operation operation = sequenceOf(DELETE_ALL,INSERT_ERS_FLUX_REPORT_DOCUMENT_DATA,INSERT_ERS_VESSEL_TRANSPORT_MEANS_DATA,INSERT_ERS_FA_REPORT_DOCUMENT_DATA,INSERT_ERS_FISHING_ACTIVITY_DATA,
              INSERT_ERS_SIZE_DISTRIBUTION_DATA,INSERT_ERS_FA_CATCH_DATA,INSERT_ERS_FISHING_TRIP_DATA,INSERT_ERS_FISHING_TRIP_IDENTIFIER_DATA);
      DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), operation);
      dbSetupTracker.launchIfNecessary(dbSetup);
  }



	@Test
    @SneakyThrows
   public void testFindEntityById() throws Exception {

      dbSetupTracker.skipNextLaunch();
      FishingActivityEntity entity=dao.findEntityById(FishingActivityEntity.class, 1);
      assertNotNull(entity);

  }

    @Test
    @SneakyThrows
    public void testSearchEntityByQuery() throws Exception {

        dbSetupTracker.skipNextLaunch();
        FishingActivityQuery query = new FishingActivityQuery();
        List<ListCriteria> list = new ArrayList<ListCriteria>();


        list.add(new ListCriteria(Filters.VESSEL_IDENTIFIES, "vesselGroup1" ));
        list.add(new ListCriteria(Filters.PURPOSE, "PURPOSE"));
        list.add(new ListCriteria(Filters.MASTER, "MARK") );
        list.add(new ListCriteria(Filters.REPORT_TYPE, "DOC_TYPE_1"));
        list.add(new ListCriteria(Filters.REPORT_TYPE, "DOC_TYPE_1"));
        list.add(new ListCriteria(Filters.ACTIVITY_TYPE,"FISHING_OPERATION"));
        list.add(new ListCriteria(Filters.AREAS, "FLUX_LOCATION_ID"));
        list.add(new ListCriteria(Filters.GEAR, "GEAR_TYPE"));
        list.add(new ListCriteria(Filters.SPECIES, "beagle1"));
        list.add(new ListCriteria(Filters.QUNTITIES, "111"));
        list.add(new ListCriteria(Filters.PERIOD, "2014-05-27 07:47:31"));
        list.add(new ListCriteria(Filters.FROM, "flux1"));

        query.setSearchCriteria(list);
        // query.setPagination( new Pagination(1,2));

      //  List<FishingActivityEntity> finishingActivityList=dao.getFishingActivityListByQuery(query);

       // assertNotNull(finishingActivityList);

    }

    @Test
    @SneakyThrows
    public void testGetFishingActivityList() throws Exception {

        dbSetupTracker.skipNextLaunch();
        List<FishingActivityEntity> finishingActivityList=dao.getFishingActivityList();
        assertNotNull(finishingActivityList);
        assertEquals(0,finishingActivityList.size());

    }

    @Test
    @SneakyThrows
    public void testGetFishingActivityListForFishingTrip() throws Exception {

        dbSetupTracker.skipNextLaunch();
        List<FishingActivityEntity> finishingActivityList=dao.getFishingActivityListForFishingTrip("NOR-TRP-20160517234053706",null);
        assertNotNull(finishingActivityList);
        assertNotEquals(0,finishingActivityList.size());

    }
}