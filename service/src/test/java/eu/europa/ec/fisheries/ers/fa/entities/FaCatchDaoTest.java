/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import eu.europa.ec.fisheries.ers.fa.dao.FaCatchDao;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

public class FaCatchDaoTest extends BaseErsFaDaoTest {


    private FaCatchDao dao = new FaCatchDao(em);

    @Before
    @SneakyThrows
    public void prepare() {
        super.prepare();
    }


    @Test
    @SneakyThrows
    public void testFindEntityById() throws Exception {
        dbSetupTracker.skipNextLaunch();
        FaCatchEntity entity = dao.findEntityById(FaCatchEntity.class, 1);
        assertNotNull(entity);
    }

    @Test
    @SneakyThrows
    public void testFindCatchesByTripId() throws Exception {
        dbSetupTracker.skipNextLaunch();
        List<Object[]> entity = dao.findFaCatchesByFishingTrip("NOR-TRP-20160517234053706");
        assertNotNull(entity);
    }

    //@Test
    public void testGetCatchSummary() throws Exception {

        dbSetupTracker.skipNextLaunch();
     /*  List<Object[]> list = dao.getCatchSummary();
        for(Object[] objArr :list){
            System.out.println(Arrays.toString(objArr));
        }*/
        List<FaCatchSummaryCustomEntity> list = dao.getCatchSummary();
        for(FaCatchSummaryCustomEntity objArr :list){
            System.out.println(objArr);
        }
        assertNotNull(list);

    }

    @Test
    public void testGetFACatchSummaryReportString() throws Exception {

        dbSetupTracker.skipNextLaunch();

        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();

        List<GroupCriteria> groupByFields = new ArrayList<>();
        groupByFields.add(GroupCriteria.DATE);
        groupByFields.add(GroupCriteria.SIZE_CLASS);
        groupByFields.add(GroupCriteria.SPECIES);
        groupByFields.add(GroupCriteria.AREA);
        query.setGroupByFields(groupByFields);

        searchCriteriaMap.put(SearchFilter.SOURCE, "FLUX");

        query.setSearchCriteriaMap(searchCriteriaMap);

        StringBuilder sqlGenerated = dao.getFACatchSummaryReportString(query);
        System.out.println("sqlGenerated:" + sqlGenerated);
        assertNotNull(sqlGenerated);

    }


}