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

import eu.europa.ec.fisheries.ers.service.search.*;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by sanera on 07/10/2016.
 */
public class SearchQueryBuilderTest extends BaseErsFaDaoTest {

    @Before
    public void prepare() {
        super.prepare();
    }


    @Test
    @SneakyThrows
    public void testCreateSQL() {

        FishingActivityQuery query = new FishingActivityQuery();
        Map<Filters,String> searchCriteriaMap = new HashMap<>();

        searchCriteriaMap.put(Filters.FROM_ID, "OWNER1");
        searchCriteriaMap.put(Filters.FROM_NAME, "OWNER_NAME1");
        searchCriteriaMap.put(Filters.PERIOD_START, "2012-05-27 07:47:31");
        searchCriteriaMap.put(Filters.PERIOD_END, "2015-05-27 07:47:31");
        searchCriteriaMap.put(Filters.VESSEL_NAME, "vessel1");
        searchCriteriaMap.put(Filters.VESSEL_IDENTIFIRE, "CFR123");
        searchCriteriaMap.put(Filters.PURPOSE, "9");
        searchCriteriaMap.put(Filters.REPORT_TYPE, "DECLARATION");
        searchCriteriaMap.put(Filters.GEAR, "GEAR_TYPE");
        searchCriteriaMap.put(Filters.ACTIVITY_TYPE, "DEPARTURE");
        searchCriteriaMap.put(Filters.SPECIES, "PLE");
        searchCriteriaMap.put(Filters.MASTER, "MARK");
        searchCriteriaMap.put(Filters.AREAS, "27.4.b");
        searchCriteriaMap.put(Filters.PORT, "GBR");
        searchCriteriaMap.put(Filters.QUNTITY_MIN, "0");
        searchCriteriaMap.put(Filters.QUNTITY_MAX, "25");
        searchCriteriaMap.put(Filters.WEIGHT_MEASURE, "TNE");
        searchCriteriaMap.put(Filters.SOURCE, "FLUX");

        query.setSortKey(new SortKey(Filters.PURPOSE, SortOrder.ASC));
        query.setSearchCriteriaMap(searchCriteriaMap);
        query.setPagination( new Pagination(1,2));
        query.setSortKey(new SortKey(Filters.FROM_NAME, SortOrder.ASC));

        StringBuilder sql= SearchQueryBuilder.createSQL(query);
        System.out.println("done:" + sql);
        assertNotNull(sql);

    }
}
