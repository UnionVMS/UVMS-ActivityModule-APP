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

import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.SortKey;
import eu.europa.ec.fisheries.ers.service.search.builder.FishingActivitySearchBuilder;
import eu.europa.ec.fisheries.ers.service.search.builder.SearchQueryBuilder;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.dto.PaginationDto;
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
    public void testCreateSQL() throws ServiceException {

        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter,String> searchCriteriaMap = new HashMap<>();

      /*  searchCriteriaMap.put(SearchFilter.FROM_ID, "OWNER1");
        searchCriteriaMap.put(SearchFilter.PERIOD_START, "2012-05-27 07:47:31");
        searchCriteriaMap.put(SearchFilter.PERIOD_END, "2015-05-27 07:47:31");
        searchCriteriaMap.put(SearchFilter.VESSEL_NAME, "vessel1");
        searchCriteriaMap.put(SearchFilter.VESSEL_IDENTIFIRE, "CFR123");
        searchCriteriaMap.put(SearchFilter.PURPOSE, "9");
        searchCriteriaMap.put(SearchFilter.REPORT_TYPE, "DECLARATION");
        searchCriteriaMap.put(SearchFilter.GEAR, "GEAR_TYPE");*/
        searchCriteriaMap.put(SearchFilter.ACTIVITY_TYPE, "DEPARTURE");
    /*    searchCriteriaMap.put(SearchFilter.SPECIES, "PLE");
        searchCriteriaMap.put(SearchFilter.MASTER, "MARK");
        searchCriteriaMap.put(SearchFilter.AREAS, "27.4.b");
        searchCriteriaMap.put(SearchFilter.PORT, "GBR");
        searchCriteriaMap.put(SearchFilter.QUNTITY_MIN, "0");
        searchCriteriaMap.put(SearchFilter.QUNTITY_MAX, "25");
        searchCriteriaMap.put(SearchFilter.WEIGHT_MEASURE, "TNE");
        searchCriteriaMap.put(SearchFilter.SOURCE, "FLUX");*/

   //     query.setSortKey(new SortKey(SearchFilter.PURPOSE, SortOrder.ASC));
        query.setSearchCriteriaMap(searchCriteriaMap);
        PaginationDto pagination =new PaginationDto();
        pagination.setPageSize(2);
        pagination.setOffset(1);
        query.setPagination( pagination);
        SearchQueryBuilder search= new FishingActivitySearchBuilder();
        StringBuilder sql= search.createSQL(query);
        System.out.println("done:" + sql);
        assertNotNull(sql);

    }

    @Test
    @SneakyThrows
    public void testCreateSQL_DateSorting() throws ServiceException {

        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter,String> searchCriteriaMap = new HashMap<>();

        searchCriteriaMap.put(SearchFilter.FROM_ID, "OWNER1");
        searchCriteriaMap.put(SearchFilter.PERIOD_START, "2012-05-27 07:47:31");
        searchCriteriaMap.put(SearchFilter.PERIOD_END, "2015-05-27 07:47:31");
        searchCriteriaMap.put(SearchFilter.VESSEL_NAME, "vessel1");
        searchCriteriaMap.put(SearchFilter.VESSEL_IDENTIFIRE, "CFR123");
        searchCriteriaMap.put(SearchFilter.PURPOSE, "9");
        searchCriteriaMap.put(SearchFilter.REPORT_TYPE, "DECLARATION");
        searchCriteriaMap.put(SearchFilter.GEAR, "GEAR_TYPE");
       // searchCriteriaMap.put(SearchFilter.ACTIVITY_TYPE, "DEPARTURE");
        searchCriteriaMap.put(SearchFilter.SPECIES, "PLE");
        searchCriteriaMap.put(SearchFilter.MASTER, "MARK");
        searchCriteriaMap.put(SearchFilter.AREAS, "27.4.b");
        searchCriteriaMap.put(SearchFilter.PORT, "GBR");
        searchCriteriaMap.put(SearchFilter.QUNTITY_MIN, "0");
        searchCriteriaMap.put(SearchFilter.QUNTITY_MAX, "25");
        searchCriteriaMap.put(SearchFilter.WEIGHT_MEASURE, "TNE");
        searchCriteriaMap.put(SearchFilter.SOURCE, "FLUX");

        SortKey sortingDto = new SortKey();
        sortingDto.setSortBy(SearchFilter.PERIOD_START);
        sortingDto.setReversed(false);
        query.setSorting(sortingDto);

        query.setSearchCriteriaMap(searchCriteriaMap);
        PaginationDto pagination =new PaginationDto();
        pagination.setPageSize(2);
        pagination.setOffset(1);
        query.setPagination( pagination);

        SortKey sortingDto2 = new SortKey();
        sortingDto2.setReversed(false);
        query.setSorting(sortingDto);
        query.setSorting(sortingDto2);
        SearchQueryBuilder search= new FishingActivitySearchBuilder();
        StringBuilder sql= search.createSQL(query);

        System.out.println("done:" + sql);
        assertNotNull(sql);

    }
}
