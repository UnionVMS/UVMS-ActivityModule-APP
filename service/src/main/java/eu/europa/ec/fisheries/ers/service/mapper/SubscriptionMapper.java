/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.service.mapper;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;
import eu.europa.ec.fisheries.wsdl.subscription.module.*;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQuery;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQueryParameter;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;
import static eu.europa.ec.fisheries.wsdl.subscription.module.CriteriaType.SENDER;
import static eu.europa.ec.fisheries.wsdl.subscription.module.CriteriaType.VALIDITY_PERIOD;
import static eu.europa.ec.fisheries.wsdl.subscription.module.SubCriteriaType.END_DATE;
import static eu.europa.ec.fisheries.wsdl.subscription.module.SubCriteriaType.ORGANISATION;
import static eu.europa.ec.fisheries.wsdl.subscription.module.SubCriteriaType.START_DATE;
import static eu.europa.ec.fisheries.wsdl.subscription.module.ValueType.SCHEME_ID;
import static eu.europa.ec.fisheries.wsdl.subscription.module.ValueType.YYYY_MM_DD_T_HH_MM_SS_SSSZ;

@Slf4j
public class SubscriptionMapper {

    private SubscriptionMapper(){}

    public static SubscriptionDataRequest mapToSubscriptionDataRequest(FLUXFAReportMessage fluxfaReportMessage) {
        SubscriptionDataRequest request = new SubscriptionDataRequest();
        request.setMethod(SubscriptionModuleMethod.MODULE_ACCESS_PERMISSION_REQUEST);

        SubscriptionDataQuery subscriptionDataQuery = new SubscriptionDataQuery();
        subscriptionDataQuery.setMessageType(MessageType.FLUX_FA_REPORT_MESSAGE);

        List<SubscriptionDataCriteria> criteria = subscriptionDataQuery.getCriteria();

        DateTimeType creationDateTime = fluxfaReportMessage.getFLUXReportDocument().getCreationDateTime();

        criteria.addAll(mapDelimitedPeriodToSubscriptionCriteria(creationDateTime));

        // TODO implement mapping
        request.setQuery(subscriptionDataQuery);
        return request;
    }

    public static SubscriptionDataRequest mapToSubscriptionDataRequest(FAQuery faQuery) {
        SubscriptionDataRequest request = new SubscriptionDataRequest();
        request.setMethod(SubscriptionModuleMethod.MODULE_ACCESS_PERMISSION_REQUEST);
        SubscriptionDataQuery query = new SubscriptionDataQuery();
        query.setMessageType(MessageType.FLUX_FA_QUERY_MESSAGE);
        query.getCriteria().addAll(mapFluxPartyToSenderSubscriptionCriteria(faQuery.getSubmitterFLUXParty()));
        query.getCriteria().addAll(mapFAQueryParametersToSubscriptionCriteria(faQuery.getSimpleFAQueryParameters()));
        query.getCriteria().addAll(mapDelimitedPeriodToSubscriptionCriteria(faQuery.getSpecifiedDelimitedPeriod()));
        request.setQuery(query);
        return request;
    }

    private static List<SubscriptionDataCriteria> mapFluxPartyToSenderSubscriptionCriteria(FLUXParty party){
        List<SubscriptionDataCriteria> dataCriteriaList = new ArrayList<>();
        List<IDType> organisationIds = party.getIDS();
        for (IDType organisationId: organisationIds){
            if ("FLUX_GP_PARTY".equals(organisationId.getSchemeID())){
                String value = organisationId.getValue();
                dataCriteriaList.add(createCriteria(SENDER, ORGANISATION, SCHEME_ID, value));
            }
        }
        return dataCriteriaList;
    }

    private static List<SubscriptionDataCriteria> mapDelimitedPeriodToSubscriptionCriteria(DateTimeType creationDateTime) {
        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();
        delimitedPeriod.setStartDateTime(creationDateTime);
        return mapDelimitedPeriodToSubscriptionCriteria(delimitedPeriod);
    }

    private static List<SubscriptionDataCriteria> mapDelimitedPeriodToSubscriptionCriteria(DelimitedPeriod period){
        List<SubscriptionDataCriteria> dataCriteriaList = new ArrayList<>();
        if (period != null) {
            DateTimeType startDateTime = period.getStartDateTime();
            DateTimeType endDateTime = period.getEndDateTime();

            if (startDateTime != null){
                XMLGregorianCalendar dateTime = startDateTime.getDateTime();
                if (dateTime != null){
                    SubscriptionDataCriteria startDateTimeCriteria =
                            createCriteria(VALIDITY_PERIOD, START_DATE, YYYY_MM_DD_T_HH_MM_SS_SSSZ, dateTime.toString());
                    dataCriteriaList.add(startDateTimeCriteria);
                }
            }

            if (endDateTime != null){
                XMLGregorianCalendar dateTime = endDateTime.getDateTime();
                if (dateTime != null){
                    SubscriptionDataCriteria endDateTimeCriteria =
                            createCriteria(VALIDITY_PERIOD, END_DATE, YYYY_MM_DD_T_HH_MM_SS_SSSZ, dateTime.toString());
                    dataCriteriaList.add(endDateTimeCriteria);
                }
            }
        }
        return dataCriteriaList;
    }

    private static SubscriptionDataCriteria createCriteria(CriteriaType criteriaType, SubCriteriaType subCriteriaType, ValueType valueType, String value){
        SubscriptionDataCriteria criteria = new SubscriptionDataCriteria();
        criteria.setCriteria(criteriaType);
        criteria.setSubCriteria(subCriteriaType);
        criteria.setValueType(valueType);
        criteria.setValue(value);
        return criteria;
    }

    private static List<SubscriptionDataCriteria> mapFAQueryParametersToSubscriptionCriteria(List<FAQueryParameter> faQueryParameters){
        List<SubscriptionDataCriteria> dataCriteriaList = new ArrayList<>();
        for (FAQueryParameter faQueryParameter : faQueryParameters){
            SubscriptionDataCriteria criteria = new SubscriptionDataCriteria();
            criteria.setCriteria(CriteriaType.VESSEL);
            CodeType faQueryParameterTypeCode = faQueryParameter.getTypeCode();
            if (faQueryParameterTypeCode != null){
                try {
                    SubCriteriaType subCriteriaType = SubCriteriaType.valueOf(faQueryParameterTypeCode.getValue());
                    criteria.setSubCriteria(subCriteriaType);
                }
                catch (IllegalArgumentException e){
                    log.warn(e.getMessage(), e);
                }
            }
            IDType valueID = faQueryParameter.getValueID();
            if (valueID != null){

                try {
                    ValueType valueType = ValueType.valueOf(faQueryParameter.getValueID().getSchemeID());
                    criteria.setValueType(valueType);
                    criteria.setValue(faQueryParameter.getValueID().getValue());
                }
                catch (IllegalArgumentException e){
                    log.warn(e.getMessage(), e);
                }
            }
            CodeType valueCode = faQueryParameter.getValueCode();
            if (valueCode != null){

                try {
                    criteria.setValueType(ValueType.valueOf(faQueryParameter.getValueCode().getListID()));
                    criteria.setValue(faQueryParameter.getValueCode().getValue());
                }
                catch (IllegalArgumentException e){
                    log.warn(e.getMessage(), e);
                }
            }
            dataCriteriaList.add(criteria);
        }
        return dataCriteriaList;
    }

}
