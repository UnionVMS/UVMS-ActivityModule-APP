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

package eu.europa.ec.fisheries.ers.service.search;

import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by sanera on 12/07/2016.
 * Filter Fishing Activity Reports functionality require Query to be generated Dynamically based on Filters provided.
 * This class provides mapping information to Query Builder.
 */
public class FilterMap {

    public static final String FROM_NAME = "ownerName";
    public static final String FROM_ID = "ownerId";
    public static final String OCCURENCE_START_DATE = "startDate";
    public static final String OCCURENCE_END_DATE = "endDate";
    public static final String VESSEL_IDENTITY_NAME = "vtName";
    public static final String VESSEL_IDENTIFIRE = "vtSchemeId";
    public static final String PURPOSE_CODE = "purposeCode";
    public static final String REPORT_TYPE_CODE = "faReportTypeCode";
    public static final String ACTIVITY_TYPE_CODE = "activityTypecode";
    public static final String AREA_ID = "fluxAreaId";
    public static final String PORT_ID = "fluxPortId";
    public static final String FISHING_GEAR = "fishingGearType";
    public static final String SPECIES_CODE = "speciesCode";
    public static final String QUNTITY_MIN = "minWeight";
    public static final String QUNTITY_MAX = "maxWeight";
    public static final String CONTACT_PERSON_NAME = "agent";
    public static final String VESSEL_TRANSPORT_TABLE_ALIAS = "fa.vesselTransportMeans vt";
    public static final String FA_CATCH_TABLE_ALIAS = " a.faCatchs faCatch ";
    public static final String DELIMITED_PERIOD_TABLE_ALIAS = " a.delimitedPeriods dp ";
    public static final String FLUX_REPORT_DOC_TABLE_ALIAS = " fa.fluxReportDocument flux ";
    public static final String FLUX_PARTY_TABLE_ALIAS = " flux.fluxParty fp  ";
    public static final String MASTER_MAPPING  = " vt.contactParty cparty JOIN FETCH cparty.contactPerson cPerson ";
    public static final String DATASOURCE = "dataSource";
    public static final String FAREPORT_ID = "faReportId";

    private static EnumMap<SearchFilter,FilterDetails> filterMappings = new EnumMap<>(SearchFilter.class);// This contains Table Join and Where condition mapping for each Filter
    private static EnumMap<SearchFilter,String> filterSortMappings = new EnumMap<>(SearchFilter.class); // For Sort criteria, which expression should be used
    private static EnumMap<SearchFilter,String> filterQueryParameterMappings = new EnumMap<>(SearchFilter.class);  // Query parameter mapping
    private static EnumMap<SearchFilter,String> filterSortWhereMappings = new EnumMap<>(SearchFilter.class); // Special case for star and end date sorting


    private FilterMap(){}


    static{
        populateFilterMappings();
        populateFilterQueryParameterMappings();
        populateFilterSortMappings();
        populateFilterSortWhereMappings();
    }


    /**
         Below method stores mapping for each Filter criteria. Mapping will provide information on table joins required for the criteria and Where conditions which needs to be applied for the criteria
     */
    private static void populateFilterMappings(){

        filterMappings.put(SearchFilter.SOURCE,new FilterDetails(" ","fa.source =:"+DATASOURCE));
        filterMappings.put(SearchFilter.FROM_ID,new FilterDetails( " fp.fluxPartyIdentifiers fpi","fpi.fluxPartyIdentifierId =:"+FROM_ID+ " "));
        filterMappings.put(SearchFilter.FROM_NAME,new FilterDetails(FLUX_PARTY_TABLE_ALIAS,"  fp.fluxPartyName=:"+FROM_NAME+" "));

        filterMappings.put(SearchFilter.PERIOD_START,new FilterDetails(DELIMITED_PERIOD_TABLE_ALIAS,"( dp.startDate >= :"+OCCURENCE_START_DATE +"  OR a.occurence  >= :"+OCCURENCE_START_DATE +" )"));
        filterMappings.put(SearchFilter.PERIOD_END,new FilterDetails(DELIMITED_PERIOD_TABLE_ALIAS," dp.endDate <= :"+OCCURENCE_END_DATE));

        filterMappings.put(SearchFilter.VESSEL_NAME,new FilterDetails("fa.vesselTransportMeans vt","vt.name =:"+VESSEL_IDENTITY_NAME));
        filterMappings.put(SearchFilter.VESSEL_IDENTIFIRE,new FilterDetails("vt.vesselIdentifiers vi","vi.vesselIdentifierId =:"+VESSEL_IDENTIFIRE));
        filterMappings.put(SearchFilter.PURPOSE,new FilterDetails(FLUX_REPORT_DOC_TABLE_ALIAS,"flux.purposeCode =:"+PURPOSE_CODE));
        filterMappings.put(SearchFilter.REPORT_TYPE,new FilterDetails(" ","fa.typeCode =:"+REPORT_TYPE_CODE));
        filterMappings.put(SearchFilter.ACTIVITY_TYPE,new FilterDetails(" ","a.typeCode IN (:"+ACTIVITY_TYPE_CODE+")"));
        filterMappings.put(SearchFilter.AREAS,new FilterDetails("a.fluxLocations fluxLoc","( fluxLoc.typeCode IN ('AREA') and fluxLoc.fluxLocationIdentifier =:"+AREA_ID+" )"));
        filterMappings.put(SearchFilter.PORT,new FilterDetails("a.fluxLocations fluxLoc","( fluxLoc.typeCode IN ('LOCATION') and fluxLoc.fluxLocationIdentifier =:"+PORT_ID+" )"));
        filterMappings.put(SearchFilter.GEAR,new FilterDetails("a.fishingGears fg","fg.typeCode =:"+FISHING_GEAR));
        filterMappings.put(SearchFilter.SPECIES,new FilterDetails(FA_CATCH_TABLE_ALIAS+" LEFT JOIN FETCH faCatch.aapProcesses aprocess LEFT JOIN FETCH aprocess.aapProducts aprod ","faCatch.speciesCode =:"+SPECIES_CODE +" OR aprod.speciesCode =:"+SPECIES_CODE));
        filterMappings.put(SearchFilter.QUNTITY_MIN,new FilterDetails(FA_CATCH_TABLE_ALIAS+" LEFT JOIN FETCH faCatch.aapProcesses aprocess LEFT JOIN FETCH aprocess.aapProducts aprod "," (faCatch.calculatedWeightMeasure  BETWEEN :"+QUNTITY_MIN ));
        filterMappings.put(SearchFilter.QUNTITY_MAX,new FilterDetails(" ","  :"+QUNTITY_MAX+") "));
        filterMappings.put(SearchFilter.MASTER,new FilterDetails(" fa.vesselTransportMeans vt JOIN FETCH vt.contactParty cparty JOIN FETCH cparty.contactPerson cPerson","(UPPER(cPerson.title) =:"+CONTACT_PERSON_NAME+" or " +
                "UPPER(cPerson.givenName) =:"+CONTACT_PERSON_NAME+" or UPPER(cPerson.middleName) =:"+CONTACT_PERSON_NAME+" or UPPER(cPerson.familyName) =:"+CONTACT_PERSON_NAME+" " +
                "or UPPER(cPerson.familyNamePrefix) =:"+CONTACT_PERSON_NAME+" or UPPER(cPerson.nameSuffix) =:"+CONTACT_PERSON_NAME+ " or UPPER(cPerson.alias) =:"+CONTACT_PERSON_NAME+")"));
        filterMappings.put(SearchFilter.FA_REPORT_ID,new FilterDetails(" ","fa.id =:"+FAREPORT_ID));

    }

    /**
            For Sort by start date and End date, it needs special treatment. We need to use subQuery to make sure We pick up only first Start or End date from the list of dates.
            Below method helps that special case
     */
    private static void populateFilterSortWhereMappings(){
        filterSortWhereMappings.put(SearchFilter.PERIOD_START,"dp1.startDate");
        filterSortWhereMappings.put(SearchFilter.PERIOD_END,"dp1.endDate");
    }


    // below method provides mapping which shoulbe used in order by clause. This will achieve sorting for the criteria
    private static void populateFilterSortMappings(){
        filterSortMappings.put(SearchFilter.PERIOD_START,"dp.startDate");
        filterSortMappings.put(SearchFilter.PERIOD_END,"dp.endDate");
        filterSortMappings.put(SearchFilter.REPORT_TYPE,"fa.typeCode");
        filterSortMappings.put(SearchFilter.SOURCE,"fa.source");
        filterSortMappings.put(SearchFilter.ACTIVITY_TYPE,"a.typeCode");
        filterSortMappings.put(SearchFilter.OCCURRENCE,"a.occurence");
        filterSortMappings.put(SearchFilter.PURPOSE,"flux.purposeCode");
        filterSortMappings.put(SearchFilter.FROM_NAME,"fp.fluxPartyName");

    }


    /**
     To put values in Query, Query Builder needs to know name used in query to be mapped to value.  Put that mapping here
     */
    private static void populateFilterQueryParameterMappings(){

        filterQueryParameterMappings.put(SearchFilter.SOURCE,DATASOURCE);
        filterQueryParameterMappings.put(SearchFilter.FROM_ID,FROM_ID);
        filterQueryParameterMappings.put(SearchFilter.FROM_NAME,FROM_NAME);
        filterQueryParameterMappings.put(SearchFilter.PERIOD_START,OCCURENCE_START_DATE );
        filterQueryParameterMappings.put(SearchFilter.PERIOD_END,OCCURENCE_END_DATE );
        filterQueryParameterMappings.put(SearchFilter.VESSEL_NAME,VESSEL_IDENTITY_NAME);
        filterQueryParameterMappings.put(SearchFilter.VESSEL_IDENTIFIRE,VESSEL_IDENTIFIRE);
        filterQueryParameterMappings.put(SearchFilter.PURPOSE,PURPOSE_CODE);
        filterQueryParameterMappings.put(SearchFilter.REPORT_TYPE,REPORT_TYPE_CODE);
        filterQueryParameterMappings.put(SearchFilter.ACTIVITY_TYPE,ACTIVITY_TYPE_CODE);
        filterQueryParameterMappings.put(SearchFilter.AREAS,AREA_ID);
        filterQueryParameterMappings.put(SearchFilter.PORT,PORT_ID);
        filterQueryParameterMappings.put(SearchFilter.GEAR,FISHING_GEAR);
        filterQueryParameterMappings.put(SearchFilter.SPECIES,SPECIES_CODE);
        filterQueryParameterMappings.put(SearchFilter.QUNTITY_MIN,QUNTITY_MIN);
        filterQueryParameterMappings.put(SearchFilter.QUNTITY_MAX,QUNTITY_MAX);
        filterQueryParameterMappings.put(SearchFilter.MASTER,CONTACT_PERSON_NAME);
        filterQueryParameterMappings.put(SearchFilter.FA_REPORT_ID,FAREPORT_ID);

    }


    public static Map<SearchFilter, FilterDetails> getFilterMappings() {
        return filterMappings;
    }


    public static Map<SearchFilter,String> getFilterSortMappings() {
        return filterSortMappings;
    }

    public static Map<SearchFilter,String> getFilterSortWhereMappings() {
        return filterSortWhereMappings;
    }

    public static Map<SearchFilter,String> getFilterQueryParameterMappings() {
        return filterQueryParameterMappings;
    }
}
