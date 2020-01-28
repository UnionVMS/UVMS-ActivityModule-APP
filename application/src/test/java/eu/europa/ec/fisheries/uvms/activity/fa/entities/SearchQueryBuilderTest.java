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

package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import eu.europa.ec.fisheries.uvms.activity.service.search.builder.FishingActivitySearchBuilder;
import eu.europa.ec.fisheries.uvms.activity.service.search.builder.SearchQueryBuilder;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.service.search.SortKey;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class SearchQueryBuilderTest {

    @Test
    public void createSQL() throws ServiceException {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();

        searchCriteriaMap.put(SearchFilter.ACTIVITY_TYPE, "DEPARTURE");

        query.setSearchCriteriaMap(searchCriteriaMap);
        query.setPageSize(2);
        query.setOffset(1);
        SearchQueryBuilder search = new FishingActivitySearchBuilder();

        // When
        StringBuilder sql = search.createSQL(query);

        // Then
        assertEquals("SELECT DISTINCT a from FishingActivityEntity a LEFT JOIN FETCH a.faReportDocument fa  WHERE a.typeCode IN (:activityTypecode) and a.relatedFishingActivity IS NULL  ORDER BY fa.acceptedDatetime ASC ", sql.toString());
    }

    @Test
    public void createSQL_DateSorting() throws ServiceException {
        // Given
        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();

        searchCriteriaMap.put(SearchFilter.OWNER, "OWNER1");
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
        searchCriteriaMap.put(SearchFilter.QUANTITY_MIN, "0");
        searchCriteriaMap.put(SearchFilter.QUANTITY_MAX, "25");
        searchCriteriaMap.put(SearchFilter.WEIGHT_MEASURE, "TNE");
        searchCriteriaMap.put(SearchFilter.SOURCE, "FLUX");

        SortKey sortingDto = new SortKey();
        sortingDto.setSortBy(SearchFilter.PERIOD_START);
        sortingDto.setReversed(false);
        query.setSorting(sortingDto);

        query.setSearchCriteriaMap(searchCriteriaMap);
        query.setPageSize(2);
        query.setOffset(1);

        SortKey sortingDto2 = new SortKey();
        sortingDto2.setReversed(false);
        query.setSorting(sortingDto);
        query.setSorting(sortingDto2);
        SearchQueryBuilder search = new FishingActivitySearchBuilder();

        // When
        StringBuilder sql = search.createSQL(query);

        String expected =
                "SELECT DISTINCT a from FishingActivityEntity a " +
                "LEFT JOIN FETCH a.faReportDocument fa  " +
                "JOIN FETCH  fa.fluxReportDocument_FluxParty fp   " +
                "JOIN FETCH  fp.fluxPartyIdentifiers fpi  " +
                "LEFT  JOIN FETCH  a.delimitedPeriods dp   " +
                "JOIN FETCH fa.vesselTransportMeans vt " +
                "JOIN vt.vesselIdentifiers vi  RIGHT  " +
                "JOIN a.fluxLocations fluxLoc  " +
                "JOIN FETCH  a.fishingGears fg   " +
                "RIGHT  JOIN  a.faCatchs faCatch  " +
                "LEFT JOIN   faCatch.aapProcesses aprocess  " +
                "LEFT JOIN   aprocess.aapProducts aprod   " +
                "JOIN FETCH  a.faCatchs faCatch  " +
                "LEFT JOIN FETCH  faCatch.aapProcesses aprocess  " +
                "LEFT JOIN FETCH  aprocess.aapProducts aprod   " +
                "JOIN FETCH vt.contactParty cparty " +
                "JOIN FETCH  cparty.contactPerson cPerson  " +
                "WHERE fa.source =:dataSource " +
                "AND fpi.fluxPartyIdentifierId =:ownerId  " +
                "AND    a.calculatedStartTime  >= :startDate  " +
                "AND  (dp.endDate <= :endDate OR  a.calculatedStartTime <= :endDate) " +
                "AND vi.vesselIdentifierId IN (:vtSchemeId) " +
                "AND vt.name IN (:vtName) " +
                "AND fa.fluxReportDocument_PurposeCode IN (:purposeCode) " +
                "AND fa.typeCode IN (:faReportTypeCode) " +
                "AND ( fluxLoc.typeCode IN ('AREA') and fluxLoc.fluxLocationIdentifier =:fluxAreaId ) " +
                "AND  (fluxLoc.typeCode IN ('LOCATION') and fluxLoc.fluxLocationIdentifier =:fluxPortId ) " +
                "AND fg.typeCode IN (:fishingGearType) " +
                "AND ( faCatch.speciesCode IN (:speciesCode)  OR aprod.speciesCode IN (:speciesCode)) " +
                "AND  (  (faCatch.calculatedWeightMeasure  BETWEEN :minWeight AND   :maxWeight)  OR (aprod.calculatedWeightMeasure BETWEEN :minWeight AND :maxWeight) )  " +
                "AND (" +
                    "UPPER(cPerson.title) IN (:agent)  " +
                    "or UPPER(cPerson.givenName) IN (:agent)  " +
                    "or UPPER(cPerson.middleName) IN (:agent)  " +
                    "or UPPER(cPerson.familyName) IN (:agent)  " +
                    "or UPPER(cPerson.familyNamePrefix) IN (:agent)  " +
                    "or UPPER(cPerson.nameSuffix) IN (:agent)  " +
                    "or UPPER(cPerson.alias) IN (:agent) ) " +
                "and a.relatedFishingActivity IS NULL  ORDER BY fa.acceptedDatetime ASC ";

        // Then
        assertEquals(expected, sql.toString());
    }
}
