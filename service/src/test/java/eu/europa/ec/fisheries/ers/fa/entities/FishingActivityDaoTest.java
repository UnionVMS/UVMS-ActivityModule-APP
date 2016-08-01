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
import eu.europa.ec.fisheries.ers.service.search.*;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static junit.framework.TestCase.assertNotNull;

@Ignore
public class FishingActivityDaoTest extends BaseErsFaDaoTest{

private FishingActivityDao dao=new FishingActivityDao(em);

 @Before   
  public void prepare(){
      Operation operation = sequenceOf(DELETE_ALL, INSERT_ERS_FISHING_ACTIVITY_DATA);
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


        list.add(new ListCriteria(Filters.VESSEL_IDENTIFIES, Arrays.asList(new SearchValue(FilterMap.VESSEL_IDENTITY_NAME,"vesselGroup1") )));
        list.add(new ListCriteria(Filters.PURPOSE, Arrays.asList(new SearchValue(FilterMap.PURPOSE_CODE,"PURPOSE") )));
        list.add(new ListCriteria(Filters.MASTER, Arrays.asList(new SearchValue(FilterMap.CONTACT_PERSON_NAME,"MARK") )));

        list.add(new ListCriteria(Filters.REPORT_TYPE, Arrays.asList(new SearchValue(FilterMap.REPORT_TYPE_CODE,"DOC_TYPE_1") )));

        list.add(new ListCriteria(Filters.REPORT_TYPE, Arrays.asList(new SearchValue(FilterMap.REPORT_TYPE_CODE,"DOC_TYPE_1") )));
        list.add(new ListCriteria(Filters.ACTIVITY_TYPE, Arrays.asList(new SearchValue(FilterMap.ACTIVITY_TYPE_CODE,"FISHING_OPERATION") )));
        list.add(new ListCriteria(Filters.AREAS, Arrays.asList(new SearchValue(FilterMap.AREA_ID,"FLUX_LOCATION_ID") )));
        list.add(new ListCriteria(Filters.GEAR, Arrays.asList(new SearchValue(FilterMap.FISHING_GEAR,"GEAR_TYPE") )));
        list.add(new ListCriteria(Filters.SPECIES, Arrays.asList(new SearchValue(FilterMap.SPECIES_CODE,"beagle1") )));
        list.add(new ListCriteria(Filters.QUNTITIES, Arrays.asList(new SearchValue(FilterMap.UNIT_QUANTITY,"111") )));
        list.add(new ListCriteria(Filters.PERIOD, Arrays.asList(new SearchValue(FilterMap.OCCURENCE_START_DATE,"2014-05-27 07:47:31"),new SearchValue(FilterMap.OCCURENCE_END_DATE,"2015-05-27 07:47:31") )));
        list.add(new ListCriteria(Filters.FROM, Arrays.asList(new SearchValue(FilterMap.FROM_NAME,"flux1") )));

        query.setSearchCriteria(list);
        // query.setPagination( new Pagination(1,2));

        List<FishingActivityEntity> finishingActivityList=dao.getFishingActivityListByQuery(query);

        assertNotNull(finishingActivityList);

    }

    @Test
    @SneakyThrows
    public void testGetFishingActivityList() throws Exception {

        dbSetupTracker.skipNextLaunch();

        List<FishingActivityEntity> finishingActivityList=dao.getFishingActivityList();
        assertNotNull(finishingActivityList);

    }
}