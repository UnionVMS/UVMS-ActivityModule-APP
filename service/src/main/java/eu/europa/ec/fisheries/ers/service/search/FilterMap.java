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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sanera on 12/07/2016.
 */
public class FilterMap {

    public static final String FROM_NAME = "ownerName";
    public static final String OCCURENCE_START_DATE = "startDate";
    public static final String OCCURENCE_END_DATE = "endDate";
    public static final String VESSEL_IDENTITY_NAME = "vtName";
    public static final String PURPOSE_CODE = "purposeCode";
    public static final String REPORT_TYPE_CODE = "faReportTypeCode";
    public static final String ACTIVITY_TYPE_CODE = "activityTypecode";
    public static final String AREA_ID = "fluxAreaId";
    public static final String PORT_ID = "fluxPortId";
    public static final String FISHING_GEAR = "fishingGearType";
    public static final String SPECIES_CODE = "speciesCode";
    public static final String UNIT_QUANTITY = "unitQuantity";
    public static final String CONTACT_PERSON_NAME = "contactPersonName";
    public static final String VESSEL_TRANSPORT_TABLE_ALIAS = "a.faReportDocument.vesselTransportMeans vt";
    public static final String MASTER_MAPPING  = " vt.contactParty cparty JOIN FETCH cparty.contactPerson cPerson ";
    public static final String REPORT_DOCUMENT_TABLE_ALIAS  = " a.faReportDocument fa ";

    private static Map<Filters,FilterDetails> filterMappings = new HashMap<Filters,FilterDetails>();
    private static Map<Filters,String> filterSortMappings = new HashMap<>();


    static{
        _populateFilterMappings();
        _populateFilterSortMappings();
    }

    private static void _populateFilterMappings(){

        filterMappings.put(Filters.FROM,new FilterDetails("a.faReportDocument.fluxReportDocument flux","flux.ownerFluxPartyName =:"+FROM_NAME));
        filterMappings.put(Filters.PERIOD,new FilterDetails("a.delimitedPeriods dp","(( dp.startDate >= :"+OCCURENCE_START_DATE + " and dp.endDate <= :"+OCCURENCE_END_DATE+" ) OR a.occurence BETWEEN :"+OCCURENCE_START_DATE +" and  :"+OCCURENCE_END_DATE+" )"));
        filterMappings.put(Filters.VESSEL_IDENTIFIES,new FilterDetails("a.faReportDocument.vesselTransportMeans vt","vt.name =:"+VESSEL_IDENTITY_NAME));
        filterMappings.put(Filters.PURPOSE,new FilterDetails("a.faReportDocument.fluxReportDocument flux","flux.purposeCode =:"+PURPOSE_CODE));
        filterMappings.put(Filters.REPORT_TYPE,new FilterDetails("a.faReportDocument fa","fa.typeCode =:"+REPORT_TYPE_CODE));
        filterMappings.put(Filters.ACTIVITY_TYPE,new FilterDetails("FishingActivityEntity a","a.typeCode =:"+ACTIVITY_TYPE_CODE));
        filterMappings.put(Filters.AREAS,new FilterDetails("a.fluxLocations fluxLoc","( fluxLoc.typeCode IN ('AREA') and fluxLoc.typeCodeListId =:"+AREA_ID+" )"));
        filterMappings.put(Filters.PORT,new FilterDetails("a.fluxLocations fluxLoc","( fluxLoc.typeCode IN ('LOCATION') and fluxLoc.typeCodeListId =:"+PORT_ID+" )"));
        filterMappings.put(Filters.GEAR,new FilterDetails("a.fishingGears fg","fg.typeCode =:"+FISHING_GEAR));
        filterMappings.put(Filters.SPECIES,new FilterDetails("a.faCatchs faCatch","faCatch.speciesCode =:"+SPECIES_CODE));
        filterMappings.put(Filters.QUNTITIES,new FilterDetails("a.faCatchs faCatch","faCatch.unitQuantity =:"+UNIT_QUANTITY));
        filterMappings.put(Filters.MASTER,new FilterDetails("a.faReportDocument.vesselTransportMeans vt JOIN FETCH vt.contactParty cparty JOIN FETCH cparty.contactPerson cPerson","cPerson.givenName =:"+CONTACT_PERSON_NAME));

    }

    private static void _populateFilterSortMappings(){
        filterSortMappings.put(Filters.PERIOD,"a.occurence");
        filterSortMappings.put(Filters.REPORT_TYPE,"a.faReportDocument.typeCode");
        filterSortMappings.put(Filters.ACTIVITY_TYPE,"a.typeCode");

    }

    public static Map<Filters, FilterDetails> getFilterMappings() {
        return filterMappings;
    }

    public static Map<Filters,String> getFilterSortMappings() {
        return filterSortMappings;
    }
}
