/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.mapper.subscription;

import static org.junit.Assert.assertEquals;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import eu.europa.ec.fisheries.ers.service.mapper.SubscriptionMapper;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.wsdl.subscription.module.CriteriaType;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubCriteriaType;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataRequest;
import eu.europa.ec.fisheries.wsdl.subscription.module.ValueType;
import lombok.SneakyThrows;
import org.joda.time.DateTime;
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

public class ActivityToSubscriptionMapperTest {

    private FLUXFAQueryMessage fluxfaQueryMessage = new FLUXFAQueryMessage();

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
        DateTime dateTime = DateUtils.XML_FORMATTER.parseDateTime("2016-07-01T11:14:00Z");
        cal.setTime(dateTime.toDate());
        XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        startDateTime.setDateTime(xmlDate);
        delimitedPeriod.setStartDateTime(startDateTime);

        DateTimeType endDateTime = new DateTimeType();

        GregorianCalendar cal2 = new GregorianCalendar();
        DateTime dateTime2 = DateUtils.XML_FORMATTER.parseDateTime("2016-07-01T11:14:00Z");
        cal2.setTime(dateTime2.toDate());
        XMLGregorianCalendar xmlDate2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal2);
        endDateTime.setDateTime(xmlDate2);
        delimitedPeriod.setEndDateTime(endDateTime);

        FAQueryParameter faQueryParameter = new FAQueryParameter();
        CodeType codeType = new CodeType();
        codeType.setValue(SubCriteriaType.VESSELID.value());
        faQueryParameter.setTypeCode(codeType);
        IDType idType0 = new IDType();
        idType0.setValue("PD2438");
        idType0.setSchemeID("IRCS");
        faQueryParameter.setValueID(idType0);

        FAQueryParameter faQueryParameter2 = new FAQueryParameter();
        CodeType codeType2 = new CodeType();
        codeType2.setValue(SubCriteriaType.VESSELID.value());
        faQueryParameter2.setTypeCode(codeType2);
        IDType idType2 = new IDType();
        idType2.setValue("SVN123456789");
        idType2.setSchemeID("CFR");
        faQueryParameter2.setValueID(idType2);

        FAQueryParameter faQueryParameter3 = new FAQueryParameter();
        CodeType codeType3 = new CodeType();
        codeType3.setValue(SubCriteriaType.TRIPID.value());
        faQueryParameter3.setTypeCode(codeType3);
        IDType idType3 = new IDType();
        idType3.setValue("FRA-TRP-2016122102030");
        idType3.setSchemeID("EU_TRIP_ID");
        faQueryParameter3.setValueID(idType3);

        FAQueryParameter faQueryParameter4 = new FAQueryParameter();
        CodeType codeType4 = new CodeType();
        codeType4.setValue("CONSOLIDATED");

        CodeType valueCode = new CodeType();
        valueCode.setListID("BOOLEAN_VALUE");
        valueCode.setValue("Y");
        faQueryParameter4.setTypeCode(codeType4);
        faQueryParameter4.setValueCode(valueCode);

        List<FAQueryParameter> faQueryParameters = new ArrayList<>();
        faQueryParameters.add(faQueryParameter);
        faQueryParameters.add(faQueryParameter2);
        faQueryParameters.add(faQueryParameter3);
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
        assertEquals(CriteriaType.VESSEL, request.getQuery().getCriteria().get(2).getCriteria());
        assertEquals(CriteriaType.VESSEL, request.getQuery().getCriteria().get(3).getCriteria());
        assertEquals(CriteriaType.VESSEL, request.getQuery().getCriteria().get(4).getCriteria());
        assertEquals(CriteriaType.VALIDITY_PERIOD, request.getQuery().getCriteria().get(5).getCriteria());
        assertEquals(CriteriaType.VALIDITY_PERIOD, request.getQuery().getCriteria().get(6).getCriteria());

        assertEquals(SubCriteriaType.ORGANISATION, request.getQuery().getCriteria().get(0).getSubCriteria());
        assertEquals(SubCriteriaType.VESSELID, request.getQuery().getCriteria().get(1).getSubCriteria());
        assertEquals(SubCriteriaType.VESSELID, request.getQuery().getCriteria().get(2).getSubCriteria());
        assertEquals(SubCriteriaType.TRIPID, request.getQuery().getCriteria().get(3).getSubCriteria());
        assertEquals(SubCriteriaType.CONSOLIDATED, request.getQuery().getCriteria().get(4).getSubCriteria());
        assertEquals(SubCriteriaType.START_DATE, request.getQuery().getCriteria().get(5).getSubCriteria());
        assertEquals(SubCriteriaType.END_DATE, request.getQuery().getCriteria().get(6).getSubCriteria());

        assertEquals(ValueType.SCHEME_ID, request.getQuery().getCriteria().get(0).getValueType());
        assertEquals(ValueType.IRCS, request.getQuery().getCriteria().get(1).getValueType());
        assertEquals(ValueType.CFR, request.getQuery().getCriteria().get(2).getValueType());
        assertEquals(ValueType.EU_TRIP_ID, request.getQuery().getCriteria().get(3).getValueType());
        assertEquals(ValueType.BOOLEAN_VALUE, request.getQuery().getCriteria().get(4).getValueType());
        assertEquals(ValueType.YYYY_MM_DD_T_HH_MM_SS_SSSZ, request.getQuery().getCriteria().get(5).getValueType());
        assertEquals(ValueType.YYYY_MM_DD_T_HH_MM_SS_SSSZ, request.getQuery().getCriteria().get(6).getValueType());

        assertEquals("BEL", request.getQuery().getCriteria().get(0).getValue());
        assertEquals("PD2438", request.getQuery().getCriteria().get(1).getValue());
        assertEquals("SVN123456789", request.getQuery().getCriteria().get(2).getValue());
        assertEquals("FRA-TRP-2016122102030", request.getQuery().getCriteria().get(3).getValue());
        assertEquals("Y", request.getQuery().getCriteria().get(4).getValue());
        //assertEquals("2016-07-01T02:00:00.000+02:00", request.getQuery().getCriteria().get(5).getValue());
        //assertEquals("2017-07-01T02:00:00.000+02:00", request.getQuery().getCriteria().get(6).getValue());

        DateUtils.parseToUTCDate("2016-07-01T02:00:00.000+02:00", request.getQuery().getCriteria().get(5).getValueType().value());
        DateUtils.parseToUTCDate("2017-07-01T02:00:00.000+02:00", request.getQuery().getCriteria().get(6).getValueType().value());

    }
}
