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

    public static String FROM_NAME = "ownerName";
    public static String OCCURENCE_START_DATE = "startDate";
    public static String OCCURENCE_END_DATE = "endDate";
    public static String VESSEL_IDENTITY_NAME = "vtName";
    public static String PURPOSE_CODE = "purposeCode";
    public static String REPORT_TYPE_CODE = "faReportTypeCode";
    public static String ACTIVITY_TYPE_CODE = "activityTypecode";
    public static String AREA_ID = "fluxAreaId";
    public static String PORT_ID = "fluxPortId";
    public static String FISHING_GEAR = "fishingGearType";
    public static String SPECIES_CODE = "speciesCode";
    public static String UNIT_QUANTITY = "unitQuantity";
    public static String CONTACT_PERSON_NAME = "contactPersonName";
    public static String VESSEL_TRANSPORT_TABLE_ALIAS = "a.faReportDocument.vesselTransportMeans vt";
    public static String MASTER_MAPPING  = " vt.contactParty cparty JOIN FETCH cparty.contactPerson cPerson ";
    public static String REPORT_DOCUMENT_TABLE_ALIAS  = " a.faReportDocument fa ";

    private static Map<SearchKey,FilterDetails> filterMappings = new HashMap<SearchKey,FilterDetails>();

    static{
        filterMappings.put(SearchKey.FROM,new FilterDetails("a.faReportDocument.fluxReportDocument flux","flux.ownerFluxPartyName =:"+FROM_NAME));
        filterMappings.put(SearchKey.PERIOD,new FilterDetails("a.delimitedPeriods dp","(( dp.startDate >= :"+OCCURENCE_START_DATE + " and dp.endDate <= :"+OCCURENCE_END_DATE+" ) OR a.occurence BETWEEN :"+OCCURENCE_START_DATE +" and  :"+OCCURENCE_END_DATE+" )"));
        filterMappings.put(SearchKey.VESSEL_IDENTIFIES,new FilterDetails("a.faReportDocument.vesselTransportMeans vt","vt.name =:"+VESSEL_IDENTITY_NAME));
        filterMappings.put(SearchKey.PURPOSE,new FilterDetails("a.faReportDocument.fluxReportDocument flux","flux.purposeCode =:"+PURPOSE_CODE));
        filterMappings.put(SearchKey.REPORT_TYPE,new FilterDetails("a.faReportDocument fa","fa.typeCode =:"+REPORT_TYPE_CODE));
        filterMappings.put(SearchKey.ACTIVITY_TYPE,new FilterDetails("FishingActivityEntity a","a.typeCode =:"+ACTIVITY_TYPE_CODE));
        filterMappings.put(SearchKey.AREAS,new FilterDetails("a.fluxLocations fluxLoc","( fluxLoc.typeCode IN ('AREA') and fluxLoc.fluxLocationIdentifier =:"+AREA_ID+" )"));
        filterMappings.put(SearchKey.PORT,new FilterDetails("a.fluxLocations fluxLoc","( fluxLoc.typeCode IN ('LOCATION') and fluxLoc.fluxLocationIdentifier =:"+PORT_ID+" )"));
        filterMappings.put(SearchKey.GEAR,new FilterDetails("a.fishingGears fg","fg.typeCode =:"+FISHING_GEAR));
        filterMappings.put(SearchKey.SPECIES,new FilterDetails("a.faCatchs faCatch","faCatch.speciesCode =:"+SPECIES_CODE));
        filterMappings.put(SearchKey.QUNTITIES,new FilterDetails("a.faCatchs faCatch","faCatch.unitQuantity =:"+UNIT_QUANTITY));
        filterMappings.put(SearchKey.MASTER,new FilterDetails("a.faReportDocument.vesselTransportMeans vt JOIN FETCH vt.contactParty cparty JOIN FETCH cparty.contactPerson cPerson","cPerson.givenName =:"+CONTACT_PERSON_NAME));

    }

    public static Map<SearchKey, FilterDetails> getFilterMappings() {
        return filterMappings;
    }
}
