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

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by sanera on 12/07/2016.
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
    public static final String FLUX_REPORT_DOC_TABLE_ALIAS = " fa.fluxReportDocument flux JOIN FETCH flux.fluxParty fp  ";
    public static final String MASTER_MAPPING  = " vt.contactParty cparty JOIN FETCH cparty.contactPerson cPerson ";
    public static final String DATASOURCE = "dataSource";

    private static EnumMap<Filters,FilterDetails> filterMappings = new EnumMap<>(Filters.class);
    private static EnumMap<Filters,String> filterSortMappings = new EnumMap<>(Filters.class);
    private static EnumMap<Filters,String> filterQueryParameterMappings = new EnumMap<>(Filters.class);


    private FilterMap(){}


    static{
        populateFilterMappings();
        populateFilterQueryParameterMappings();
        populateFilterSortMappings();
    }


    private static void populateFilterMappings(){

        filterMappings.put(Filters.SOURCE,new FilterDetails(" ","fa.source =:"+DATASOURCE));
        filterMappings.put(Filters.FROM_ID,new FilterDetails(FLUX_REPORT_DOC_TABLE_ALIAS + " JOIN FETCH fp.fluxPartyIdentifiers fpi","fpi.fluxPartyIdentifierId =:"+FROM_ID+ " "));
        filterMappings.put(Filters.FROM_NAME,new FilterDetails(FLUX_REPORT_DOC_TABLE_ALIAS," fp.fluxPartyName=:"+FROM_NAME+" "));

        filterMappings.put(Filters.PERIOD_START,new FilterDetails(DELIMITED_PERIOD_TABLE_ALIAS,"( dp.startDate >= :"+OCCURENCE_START_DATE +"  OR a.occurence  >= :"+OCCURENCE_START_DATE +" )"));
        filterMappings.put(Filters.PERIOD_END,new FilterDetails(DELIMITED_PERIOD_TABLE_ALIAS," dp.endDate <= :"+OCCURENCE_END_DATE));

        filterMappings.put(Filters.VESSEL_NAME,new FilterDetails("fa.vesselTransportMeans vt","vt.name =:"+VESSEL_IDENTITY_NAME));
        filterMappings.put(Filters.VESSEL_IDENTIFIRE,new FilterDetails("vt.vesselIdentifiers vi","vi.vesselIdentifierSchemeId =:"+VESSEL_IDENTIFIRE));
        filterMappings.put(Filters.PURPOSE,new FilterDetails("fa.fluxReportDocument flux","flux.purposeCode =:"+PURPOSE_CODE));
        filterMappings.put(Filters.REPORT_TYPE,new FilterDetails(" ","fa.typeCode =:"+REPORT_TYPE_CODE));
        filterMappings.put(Filters.ACTIVITY_TYPE,new FilterDetails(" ","a.typeCode =:"+ACTIVITY_TYPE_CODE));
        filterMappings.put(Filters.AREAS,new FilterDetails("a.fluxLocations fluxLoc","( fluxLoc.typeCode IN ('AREA') and fluxLoc.fluxLocationIdentifier =:"+AREA_ID+" )"));
        filterMappings.put(Filters.PORT,new FilterDetails("a.fluxLocations fluxLoc","( fluxLoc.typeCode IN ('LOCATION') and fluxLoc.fluxLocationIdentifier =:"+PORT_ID+" )"));
        filterMappings.put(Filters.GEAR,new FilterDetails("a.fishingGears fg","fg.typeCode =:"+FISHING_GEAR));
        filterMappings.put(Filters.SPECIES,new FilterDetails("a.faCatchs faCatch JOIN FETCH faCatch.aapProcesses aprocess JOIN FETCH aprocess.aapProducts aprod","faCatch.speciesCode =:"+SPECIES_CODE +" OR aprod.speciesCode =:"+SPECIES_CODE));
        filterMappings.put(Filters.QUNTITY_MIN,new FilterDetails("a.faCatchs faCatch JOIN FETCH faCatch.aapProcesses aprocess JOIN FETCH aprocess.aapProducts aprod"," (faCatch.weightMeasure  BETWEEN :"+QUNTITY_MIN ));
        filterMappings.put(Filters.QUNTITY_MAX,new FilterDetails(" ","  :"+QUNTITY_MAX+") "));
        filterMappings.put(Filters.MASTER,new FilterDetails(" fa.vesselTransportMeans vt JOIN FETCH vt.contactParty cparty JOIN FETCH cparty.contactPerson cPerson","(UPPER(cPerson.title) =:"+CONTACT_PERSON_NAME+" or " +
                "UPPER(cPerson.givenName) =:"+CONTACT_PERSON_NAME+" or UPPER(cPerson.middleName) =:"+CONTACT_PERSON_NAME+" or UPPER(cPerson.familyName) =:"+CONTACT_PERSON_NAME+" " +
                "or UPPER(cPerson.familyNamePrefix) =:"+CONTACT_PERSON_NAME+" or UPPER(cPerson.nameSuffix) =:"+CONTACT_PERSON_NAME+ " or UPPER(cPerson.alias) =:"+CONTACT_PERSON_NAME+")"));

    }

    private static void populateFilterSortMappings(){
        filterSortMappings.put(Filters.PERIOD_START,"dp.startDate");
        filterSortMappings.put(Filters.PERIOD_END,"dp.endDate");
        filterSortMappings.put(Filters.REPORT_TYPE,"fa.typeCode");
        filterSortMappings.put(Filters.SOURCE,"fa.source");
        filterSortMappings.put(Filters.ACTIVITY_TYPE,"a.typeCode");
        filterSortMappings.put(Filters.PURPOSE,"flux.purposeCode");
        filterSortMappings.put(Filters.FROM_NAME,"flux.ownerFluxPartyName");

    }

    private static void populateFilterQueryParameterMappings(){

        filterQueryParameterMappings.put(Filters.SOURCE,DATASOURCE);
        filterQueryParameterMappings.put(Filters.FROM_ID,FROM_ID);
        filterQueryParameterMappings.put(Filters.FROM_NAME,FROM_NAME);
        filterQueryParameterMappings.put(Filters.PERIOD_START,OCCURENCE_START_DATE );
        filterQueryParameterMappings.put(Filters.PERIOD_END,OCCURENCE_END_DATE );
        filterQueryParameterMappings.put(Filters.VESSEL_NAME,VESSEL_IDENTITY_NAME);
        filterQueryParameterMappings.put(Filters.VESSEL_IDENTIFIRE,VESSEL_IDENTIFIRE);
        filterQueryParameterMappings.put(Filters.PURPOSE,PURPOSE_CODE);
        filterQueryParameterMappings.put(Filters.REPORT_TYPE,REPORT_TYPE_CODE);
        filterQueryParameterMappings.put(Filters.ACTIVITY_TYPE,ACTIVITY_TYPE_CODE);
        filterQueryParameterMappings.put(Filters.AREAS,AREA_ID);
        filterQueryParameterMappings.put(Filters.PORT,PORT_ID);
        filterQueryParameterMappings.put(Filters.GEAR,FISHING_GEAR);
        filterQueryParameterMappings.put(Filters.SPECIES,SPECIES_CODE);
        filterQueryParameterMappings.put(Filters.QUNTITY_MIN,QUNTITY_MIN);
        filterQueryParameterMappings.put(Filters.QUNTITY_MAX,QUNTITY_MAX);
        filterQueryParameterMappings.put(Filters.MASTER,CONTACT_PERSON_NAME);

    }


    public static Map<Filters, FilterDetails> getFilterMappings() {
        return filterMappings;
    }

    public static Map<Filters,String> getFilterSortMappings() {
        return filterSortMappings;
    }

    public static Map<Filters,String> getFilterQueryParameterMappings() {
        return filterQueryParameterMappings;
    }
}
