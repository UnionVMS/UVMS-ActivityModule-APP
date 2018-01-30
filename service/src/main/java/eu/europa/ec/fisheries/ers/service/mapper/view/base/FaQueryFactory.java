/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.mapper.view.base;

import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQuery;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQueryParameter;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

@Slf4j
public class FaQueryFactory {

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
}
