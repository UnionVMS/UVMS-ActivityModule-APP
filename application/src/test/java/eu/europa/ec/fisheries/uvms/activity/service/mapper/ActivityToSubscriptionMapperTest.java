/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.service.mapper.SubscriptionMapper;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.wsdl.subscription.module.CriteriaType;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubCriteriaType;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataRequest;
import eu.europa.ec.fisheries.wsdl.subscription.module.ValueType;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import un.unece.uncefact.data.standard.fluxfaquerymessage._3.FLUXFAQueryMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQuery;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQueryParameter;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ActivityToSubscriptionMapperTest {

    private FLUXFAQueryMessage fluxfaQueryMessage = new FLUXFAQueryMessage();

    private Date parseToUTCDate(String value) {
        LocalDateTime localDateTime = LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DateUtils.DATE_TIME_XML_FORMAT));
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime.toLocalDate(), localDateTime.toLocalTime(), ZoneId.of("UTC"));
        return Date.from(zonedDateTime.toInstant());
    }

    @Before
    @SneakyThrows
    public void before(){
        IDType idType = new IDType();
        idType.setSchemeID("FLUX_GP_PARTY");
        idType.setValue("BEL");
        FLUXParty fluxParty = new FLUXParty();
        fluxParty.setIDS(Collections.singletonList(idType));

        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();

        DateTimeType startDateTime = new DateTimeType();

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(parseToUTCDate("2016-07-01T11:14:00Z"));
        XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        startDateTime.setDateTime(xmlDate);
        delimitedPeriod.setStartDateTime(startDateTime);

        DateTimeType endDateTime = new DateTimeType();

        GregorianCalendar cal2 = new GregorianCalendar();
        cal2.setTime(parseToUTCDate("2017-07-01T02:00:00Z"));
        XMLGregorianCalendar xmlDate2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal2);
        endDateTime.setDateTime(xmlDate2);
        delimitedPeriod.setEndDateTime(endDateTime);

        FAQueryParameter faQueryParameter4 = new FAQueryParameter();
        CodeType codeType4 = new CodeType();
        codeType4.setValue("CONSOLIDATED");

        CodeType valueCode = new CodeType();
        valueCode.setListID("BOOLEAN_VALUE");
        valueCode.setValue("Y");
        faQueryParameter4.setTypeCode(codeType4);
        faQueryParameter4.setValueCode(valueCode);

        List<FAQueryParameter> faQueryParameters = new ArrayList<>();
        faQueryParameters.add(faQueryParameter4);

        FAQuery faQuery = new FAQuery();
        faQuery.setSpecifiedDelimitedPeriod(delimitedPeriod);
        faQuery.setSimpleFAQueryParameters(faQueryParameters);
        faQuery.setSubmitterFLUXParty(fluxParty);

        fluxfaQueryMessage.setFAQuery(faQuery);
    }

    @Test
    public void testMapToSubscriptionDataRequest(){

        SubscriptionDataRequest request = SubscriptionMapper.mapToSubscriptionDataRequest(fluxfaQueryMessage.getFAQuery());

        assertEquals(CriteriaType.SENDER, request.getQuery().getCriteria().get(0).getCriteria());
        assertEquals(CriteriaType.VESSEL, request.getQuery().getCriteria().get(1).getCriteria());
        assertEquals(CriteriaType.VALIDITY_PERIOD, request.getQuery().getCriteria().get(2).getCriteria());

        assertEquals(SubCriteriaType.ORGANISATION, request.getQuery().getCriteria().get(0).getSubCriteria());
        assertEquals(SubCriteriaType.START_DATE, request.getQuery().getCriteria().get(2).getSubCriteria());
        assertEquals(SubCriteriaType.END_DATE, request.getQuery().getCriteria().get(3).getSubCriteria());

        assertEquals(ValueType.SCHEME_ID, request.getQuery().getCriteria().get(0).getValueType());
        assertEquals(ValueType.BOOLEAN_VALUE, request.getQuery().getCriteria().get(1).getValueType());
        assertEquals(ValueType.YYYY_MM_DD_T_HH_MM_SS_SSSZ, request.getQuery().getCriteria().get(2).getValueType());
        assertEquals(ValueType.YYYY_MM_DD_T_HH_MM_SS_SSSZ, request.getQuery().getCriteria().get(3).getValueType());

        assertEquals("BEL", request.getQuery().getCriteria().get(0).getValue());
        assertEquals("Y", request.getQuery().getCriteria().get(1).getValue());
       // assertEquals("2016-07-01T11:14:00.000+02:00", request.getQuery().getCriteria().get(2).getValue());
       // assertEquals("2017-07-01T02:00:00.000+02:00", request.getQuery().getCriteria().get(3).getValue());

       // DateUtils.parseToUTCDate("2016-07-01T11:14:00.000+02:00", request.getQuery().getCriteria().get(2).getValueType().value());
       // DateUtils.parseToUTCDate("2017-07-01T02:00:00.000+02:00", request.getQuery().getCriteria().get(3).getValueType().value());

    }
}
