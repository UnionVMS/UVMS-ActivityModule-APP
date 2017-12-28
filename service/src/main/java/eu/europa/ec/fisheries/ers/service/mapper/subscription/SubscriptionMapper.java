/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.service.mapper.subscription.mapper;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
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
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

/**
 * TODO create test
 */
public class SubscriptionMapper {

    private List<SubscriptionDataCriteria> mapPartyToSender(FLUXParty party){

        List<SubscriptionDataCriteria> dataCriteriaList = new ArrayList<>();

        List<IDType> organisationIds = party.getIDS();

        for (IDType organisationId: organisationIds){

            String value = organisationId.getValue();
            SubscriptionDataCriteria criteria = new SubscriptionDataCriteria();
            criteria.setSubCriteria(SubCriteriaType.ORGANISATION);
            criteria.setValueType(ValueType.SCHEME_ID);
            criteria.setValue(value);
            dataCriteriaList.add(criteria);
        }

        return dataCriteriaList;
    }

    public static String mapToGetSubscriptionDataRequest(FAQuery faQuery) throws JAXBException {

        DelimitedPeriod specifiedDelimitedPeriod = faQuery.getSpecifiedDelimitedPeriod();
        IDType id = faQuery.getID();
        List<FAQueryParameter> simpleFAQueryParameters = faQuery.getSimpleFAQueryParameters();
        DateTimeType submittedDateTime = faQuery.getSubmittedDateTime();
        FLUXParty submitterFLUXParty = faQuery.getSubmitterFLUXParty();
        CodeType typeCode = faQuery.getTypeCode();




        //    SubscriptionDataCriteria criteria = new SubscriptionDataCriteria();
        //    criteria.setCriteria(CriteriaType.FLUX_FA_QUERY_MESSAGE);
        //    criteria.setSubCriteria(SubCriteriaType.SPECIFIED_DELIMITED_PERIOD);
        // Object from = parameters.get("from");
        //   criteria.setValueType(ValueType.YYYY_MM_DDTHH_MM_SS);
        //criteria.setValue(from);
        //criteria.
        // query.getCriteria().addAll();

        //equest.setQuery();



        SubscriptionDataRequest request = new SubscriptionDataRequest();

        request.setMethod(SubscriptionMethod.SUBSCRIPTION_DATA);

         SubscriptionDataQuery query = new SubscriptionDataQuery();


        List<SubscriptionDataCriteria> criteriaList = new ArrayList<>();





        return JAXBUtils.marshallJaxBObjectToString(request);
    }
}
