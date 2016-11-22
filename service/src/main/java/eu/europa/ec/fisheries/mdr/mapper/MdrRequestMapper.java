/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.mapper;

import eu.europa.ec.fisheries.mdr.exception.ActivityMappingException;
import eu.europa.ec.fisheries.schema.rules.module.v1.RulesModuleMethod;
import eu.europa.ec.fisheries.schema.rules.module.v1.SetFLUXMDRSyncMessageRulesRequest;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import org.joda.time.DateTime;
import un.unece.uncefact.data.standard.query.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

public class MdrRequestMapper {

	public static final String DOUBLE_DASH = "--";
	public static final String EN = "EN";
	public static final String UUID = "UUID";
	public static final String FLUX_MDR_QUERY_TYPE = "FLUX_MDR_QUERY_TYPE";
	public static final String GRC = "GRC";
	public static final String INDEX = "INDEX";

	/**
	 * This class isn't supposed to have instances.
	 *
	 */
	private MdrRequestMapper(){}

	/**
	 * Creates an FLUXMDRQueryMessage for qurying the FLUX TL with the acronym and serviceType parameters.
	 * (and other needed ones)
	 * For the moment all the querying is done with serviceType set to OBJ_DATA_ALL which means
	 * that all the available Entity Rows will be extracted.
	 * 
	 * @param acronym
	 * @param serviceType 
	 * @return
	 * @throws ExchangeModelMarshallException 
	 */
	public static String mapMdrQueryTypeToString(String acronym, String serviceType) throws ActivityMappingException {

		SetFLUXMDRSyncMessageRulesRequest fluxRequestObject = new SetFLUXMDRSyncMessageRulesRequest();
		FLUXMDRQueryMessage mdrQueryMsg                     = new FLUXMDRQueryMessage();
		MDRQueryType mdrQuery                               = new MDRQueryType();

		// Contractual language code
		CodeType languageCode = new CodeType();
		languageCode.setValue(EN);
		mdrQuery.setContractualLanguageCode(languageCode);

		// Unique message ID
		IDType messageID = new IDType();
		messageID.setSchemeID(UUID);
		messageID.setValue(createBusinessUUID(acronym, serviceType));
		mdrQuery.setID(messageID);

		// Service type (TypeCode);
		CodeType requestServName = new CodeType();
		requestServName.setValue(serviceType);
		requestServName.setListID(FLUX_MDR_QUERY_TYPE);
		mdrQuery.setTypeCode(requestServName);

		// Acronym value (SubjectMDRQueryIdentity);
		MDRQueryIdentityType subjectQueryType = new MDRQueryIdentityType();
		IDType idType = new IDType();
		idType.setValue(acronym);
		idType.setSchemeID(INDEX);
		subjectQueryType.setID(idType);
		/*try {
			subjectQueryType.setValidityDelimitedPeriod(createValidityDelimitedPeriod());
		} catch (DatatypeConfigurationException e) {
			throw new ActivityMappingException(e);
		}*/
		mdrQuery.setSubjectMDRQueryIdentity(subjectQueryType);

		// Submiter Flux party
		FLUXPartyType fluxParty = new FLUXPartyType();
		List<IDType> countryIds = new ArrayList<>();
		IDType contryId = new IDType();
		contryId.setValue(GRC);
		countryIds.add(contryId);
		fluxParty.setIDS(countryIds);
		mdrQuery.setSubmitterFLUXParty(fluxParty);

		String fluxStrReq;
		try {
			// Submitted DateTime
			mdrQuery.setSubmittedDateTime(createSubmitedDate());
			mdrQueryMsg.setMDRQuery(mdrQuery);
			fluxRequestObject.setRequest(JAXBMarshaller.marshallJaxBObjectToString(mdrQueryMsg));
			fluxRequestObject.setMethod(RulesModuleMethod.SET_FLUX_MDR_SYNC_REQUEST);
			fluxStrReq =  JAXBMarshaller.marshallJaxBObjectToString(fluxRequestObject);
		} catch (ExchangeModelMarshallException | DatatypeConfigurationException e) {
			throw new ActivityMappingException(e);
		}
		return fluxStrReq;
	}

	private static DelimitedPeriodType createValidityDelimitedPeriod() throws DatatypeConfigurationException {
		XMLGregorianCalendar startDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime("2001-01-01").toGregorianCalendar());
		XMLGregorianCalendar endDate   = DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().toGregorianCalendar());
		return new DelimitedPeriodType(new DateTimeType(startDate, null), new DateTimeType(endDate, null), null);
	}


	/**
	 * Creates a new DateTimeType instance to be used as a createSubmitedDate.
	 *
	 * @return new DateTimeType(date, dateTimeString);
	 * @throws DatatypeConfigurationException
	 */
	private static DateTimeType createSubmitedDate() throws DatatypeConfigurationException {
		XMLGregorianCalendar date   = DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().toGregorianCalendar());
		/*DateTimeType.DateTimeString dateTimeString = new DateTimeType.DateTimeString();
		dateTimeString.setValue(date.toString());
		dateTimeString.setFormat(dateTimeString.getFormat());*/
		return new DateTimeType(date, null);
	}


	/**
	 *  BUSINESS_UUID has a prefix, a date-time combination and a serial - thus it is semi unique
	 * @return String
	 * @param acronym
	 */
	private static String createBusinessUUID(String acronym, String serviceType){
		final String uuid = java.util.UUID.randomUUID().toString();
		StringBuilder sb = new StringBuilder(acronym).append(DOUBLE_DASH).append(serviceType).append(DOUBLE_DASH).append(uuid);
		return sb.toString();
	}

	public static String mapMdrQueryTypeToStringForINDEXServiceType(String serviceType) throws ActivityMappingException {
		return mapMdrQueryTypeToString(INDEX, serviceType);
	}
}
