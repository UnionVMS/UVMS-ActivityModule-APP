/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.activity.service.search;

import eu.europa.ec.fisheries.uvms.commons.date.XMLDateUtils;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQuery;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQueryParameter;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
public class FaQueryFactory {

    private FaQueryFactory(){
        super();
    }

    public static FAQuery createFaQueryForTrip(String tripId, String sendTo, boolean consolidated) {
        IDType queryId = new IDType();
        queryId.setValue(UUID.randomUUID().toString());
        queryId.setSchemeID("UUID");

        CodeType queryTypeCode = new CodeType();
        queryTypeCode.setValue("TRIP");
        queryTypeCode.setListID("FA_QUERY_TYPE");

        FAQuery fishingActivityQuery = new FAQuery();
        fishingActivityQuery.setID(queryId);
        fishingActivityQuery.setTypeCode(queryTypeCode);

        try {
            final XMLGregorianCalendar currentDate = XMLDateUtils.getCurrentDate();
            fishingActivityQuery.setSubmittedDateTime(new DateTimeType(currentDate, null));
        } catch (DatatypeConfigurationException e) {
            log.error("[ERROR] Error while trying to create XMLGregorianCalendar () DateUtils.getCurrentDate()! Going to retry", e);
            return null;
        }

        IDType fluxPartyId = new IDType();
        fluxPartyId.setValue(sendTo);
        fluxPartyId.setSchemeID("FLUX_GP_PARTY");

        FLUXParty fluxParty = new FLUXParty(Collections.singletonList(fluxPartyId), null);
        fishingActivityQuery.setSubmitterFLUXParty(fluxParty);

        CodeType tripIdQueryParameter = new CodeType();
        tripIdQueryParameter.setValue("TRIPID");
        tripIdQueryParameter.setListID("FA_QUERY_PARAMETER");

        IDType tripIdQueryParameterValue = new IDType();
        tripIdQueryParameterValue.setValue(tripId);
        tripIdQueryParameterValue.setSchemeID("EU_TRIP_ID");

        CodeType consolidatedQueryParameter = new CodeType();
        consolidatedQueryParameter.setValue("CONSOLIDATED");
        consolidatedQueryParameter.setListID("FA_QUERY_PARAMETER");

        CodeType consolidatedQueryParameterValue = new CodeType();
        consolidatedQueryParameterValue.setValue(consolidated ? "Y" : "N");
        consolidatedQueryParameterValue.setListID("BOOLEAN_VALUE");

        FAQueryParameter faQueryParameter = new FAQueryParameter(tripIdQueryParameter, null, null, tripIdQueryParameterValue);
        FAQueryParameter faQueryParameter1 = new FAQueryParameter(consolidatedQueryParameter, consolidatedQueryParameterValue, null, null);

        List<FAQueryParameter> value1 = Arrays.asList(faQueryParameter, faQueryParameter1);
        fishingActivityQuery.setSimpleFAQueryParameters(value1);
        return fishingActivityQuery;
    }
}
