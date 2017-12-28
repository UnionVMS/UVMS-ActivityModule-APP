/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.service.mapper.subscription;

import static eu.europa.ec.fisheries.wsdl.subscription.module.CriteriaType.SENDER;
import static eu.europa.ec.fisheries.wsdl.subscription.module.CriteriaType.VALIDITY_PERIOD;
import static eu.europa.ec.fisheries.wsdl.subscription.module.CriteriaType.VESSEL;
import static eu.europa.ec.fisheries.wsdl.subscription.module.SubCriteriaType.END_DATE_TIME;
import static eu.europa.ec.fisheries.wsdl.subscription.module.SubCriteriaType.ORGANISATION;
import static eu.europa.ec.fisheries.wsdl.subscription.module.SubCriteriaType.START_DATE_TIME;
import static eu.europa.ec.fisheries.wsdl.subscription.module.ValueType.SCHEME_ID;
import static eu.europa.ec.fisheries.wsdl.subscription.module.ValueType.YYYY_MM_DD_T_HH_MM_SS_SSSZ;

import java.util.ArrayList;
import java.util.List;

import eu.europa.ec.fisheries.wsdl.subscription.module.CriteriaType;
import eu.europa.ec.fisheries.wsdl.subscription.module.MessageType;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubCriteriaType;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataCriteria;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataQuery;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataRequest;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionMethod;
import eu.europa.ec.fisheries.wsdl.subscription.module.ValueType;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQuery;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQueryParameter;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

public class SubscriptionMapper {

    private SubscriptionMapper(){

    }

    public static SubscriptionDataRequest mapToSubscriptionDataRequest(FAQuery faQuery) {
        SubscriptionDataRequest request = new SubscriptionDataRequest();
        request.setMethod(SubscriptionMethod.SUBSCRIPTION_DATA);
        SubscriptionDataQuery query = new SubscriptionDataQuery();
        query.setMessageType(MessageType.FLUX_FA_QUERY_MESSAGE);
        query.getCriteria().addAll(mapFluxPartyToSenderSubscriptionCriteria(faQuery.getSubmitterFLUXParty()));
        query.getCriteria().addAll(mapFAQueryParametersToSubscriptionCriteria(faQuery.getSimpleFAQueryParameters()));
        query.getCriteria().addAll(mapDelimitedPeriodToFaQuerySubscriptionCriteria(faQuery.getSpecifiedDelimitedPeriod()));
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

    private static List<SubscriptionDataCriteria> mapDelimitedPeriodToFaQuerySubscriptionCriteria(DelimitedPeriod period){

        List<SubscriptionDataCriteria> dataCriteriaList = new ArrayList<>();

        SubscriptionDataCriteria startDateTimeCriteria =
                createCriteria(VALIDITY_PERIOD, START_DATE_TIME, YYYY_MM_DD_T_HH_MM_SS_SSSZ, period.getStartDateTime().getDateTime().toString());
        dataCriteriaList.add(startDateTimeCriteria);

        SubscriptionDataCriteria endDateTimeCriteria =
                createCriteria(VALIDITY_PERIOD, END_DATE_TIME, YYYY_MM_DD_T_HH_MM_SS_SSSZ, period.getEndDateTime().getDateTime().toString());
        dataCriteriaList.add(endDateTimeCriteria);

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

            criteria.setCriteria(VESSEL);

            CodeType faQueryParameterTypeCode = faQueryParameter.getTypeCode();

            criteria.setSubCriteria(SubCriteriaType.valueOf(faQueryParameterTypeCode.getValue()));

            IDType valueID = faQueryParameter.getValueID();
            if (valueID != null){
                criteria.setValueType(ValueType.valueOf(faQueryParameter.getValueID().getSchemeID()));
                criteria.setValue(faQueryParameter.getValueID().getValue());

            }

            CodeType valueCode = faQueryParameter.getValueCode();
            if (valueCode != null){
                criteria.setValueType(ValueType.valueOf(faQueryParameter.getValueCode().getListID()));
                criteria.setValue(faQueryParameter.getValueCode().getValue());

            }
            dataCriteriaList.add(criteria);
        }
        return dataCriteriaList;
    }

}
