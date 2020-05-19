/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.mapper.view.base;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import eu.europa.ec.fisheries.ers.service.mdrcache.MDRAcronymType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierType;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQuery;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQueryParameter;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

@Slf4j
public class FaQueryFactory {

    public static final String FA_QUERY_TYPE = MDRAcronymType.FA_QUERY_TYPE.name();
    public static final String FA_QUERY_PARAMETER = MDRAcronymType.FA_QUERY_PARAMETER.name();
    public static final String FLUX_GP_PARTY = MDRAcronymType.FLUX_GP_PARTY.name();
    public static final String TRIP = "TRIP";
    public static final String VESSEL = "VESSEL";
    public static final String TRIPID = "TRIPID";
    public static final String VESSELID = "VESSELID";
    public static final String CONSOLIDATED = "CONSOLIDATED";
    public static final String BOOLEAN_VALUE = "BOOLEAN_VALUE";
    public static final String UUID_ = "UUID";

    private FaQueryFactory(){
        super();
    }

    public static FAQuery createFaQueryForVesselId(String submitterFluxParty, List<VesselIdentifierType> vesselIdentifiers, boolean consolidated, XMLGregorianCalendar startDate, XMLGregorianCalendar endDate) {
        FAQuery faq = createFAQuery();
        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();
        delimitedPeriod.setStartDateTime(new DateTimeType(startDate, null));
        delimitedPeriod.setEndDateTime(new DateTimeType(endDate, null));
        faq.setSpecifiedDelimitedPeriod(delimitedPeriod);
        faq.setTypeCode(createCodeType(VESSEL, FA_QUERY_TYPE));
        faq.setSubmitterFLUXParty(new FLUXParty(
                Collections.singletonList(createIDType(submitterFluxParty, FLUX_GP_PARTY)), null));
        List<FAQueryParameter> simpleParams = new ArrayList<>();
        vesselIdentifiers.forEach( identifier ->
            simpleParams.add(
                new FAQueryParameter(
                    createCodeType(VESSELID, FA_QUERY_PARAMETER),
                    null, null,
                    createIDType(identifier.getValue(), identifier.getKey().name())
        )));
        simpleParams.add(
                new FAQueryParameter(
                        createCodeType(CONSOLIDATED, FA_QUERY_PARAMETER),
                        createCodeType(consolidated?"Y":"N", BOOLEAN_VALUE),
                        null, null));
        faq.setSimpleFAQueryParameters(simpleParams);
        return faq;
    }

    public static FAQuery createFaQueryForTrip(String tripId, String sendTo, boolean consolidated) {
        FAQuery faq = new FAQuery();
        faq.setID(new IDType(UUID.randomUUID().toString(), "UUID", null, null, null,
                null, null, null));
        faq.setTypeCode(new CodeType("TRIP", "FA_QUERY_TYPE", null, null, null, null,
                null, null, null, null));
        try {
            final XMLGregorianCalendar currentDate = DateUtils.getCurrentDate();
            faq.setSubmittedDateTime(new DateTimeType(currentDate, null));
        } catch (DatatypeConfigurationException e) {
            log.error("[ERROR] Error while trying to create XMLGregorianCalendar () DateUtils.getCurrentDate()! Going to retry", e);
            return null;
        }
        faq.setSubmitterFLUXParty(new FLUXParty(
                Collections.singletonList(new IDType(sendTo, "FLUX_GP_PARTY", null, null,
                        null, null, null, null)), null));
        faq.setSimpleFAQueryParameters(Arrays.asList(
                new FAQueryParameter(
                        new CodeType("TRIPID", "FA_QUERY_PARAMETER", null, null,
                                null, null, null, null, null, null),
                        null, null,
                        new IDType(tripId, "EU_TRIP_ID", null, null,
                                null, null, null, null)
                ),
                new FAQueryParameter(
                        new CodeType("CONSOLIDATED", "FA_QUERY_PARAMETER", null, null,
                                null, null, null, null, null, null),
                        new CodeType(consolidated?"Y":"N", "BOOLEAN_VALUE", null, null,
                                null, null, null, null, null, null),
                        null, null)));
        return faq;
    }

    public static FAQuery createFAQuery() {
        FAQuery faq = new FAQuery();
        faq.setID(createIDType(UUID.randomUUID().toString(), UUID_));
        try {
            final XMLGregorianCalendar currentDate = DateUtils.getCurrentDate();
            faq.setSubmittedDateTime(new DateTimeType(currentDate, null));
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
        return faq;
    }
    
    public static CodeType createCodeType(String value,
                                          String schemeID) {
        return new CodeType(value, schemeID, null, null, null, null,
                null, null, null, null);
    }

    public static IDType createIDType(String value, String schemeID) {
        return new IDType(value, schemeID, null, null, null,
                null, null, null);
    }
}
