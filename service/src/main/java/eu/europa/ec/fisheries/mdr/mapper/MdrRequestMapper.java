/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.mapper;

import eu.europa.ec.fisheries.schema.rules.module.v1.RulesModuleMethod;
import eu.europa.ec.fisheries.schema.rules.module.v1.SetFLUXMDRSyncMessageRulesRequest;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import org.joda.time.DateTime;
import un.unece.uncefact.data.standard.unqualifieddatatype._13.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._13.IDType;
import xeu.ec.fisheries.flux_bl.flux_mdr_query._1.BasicAttribute;
import xeu.ec.fisheries.flux_bl.flux_mdr_query._1.MDRQueryType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class MdrRequestMapper {

	/**
	 * This class isn't supposed to have instances.
	 *
	 */
	private MdrRequestMapper(){}

	/**
	 * Creates an MDRQueryType for qurying the FLUX with the acronym and serviceType parameters.
	 * For the moment all the querying is done wityh serviceType set to OBJ_DATA_ALL which means 
	 * that all the available Entity Rows will be extracted.
	 * 
	 * @param acronym
	 * @param serviceType 
	 * @return
	 * @throws ExchangeModelMarshallException 
	 */
	public static String mapMdrQueryTypeToString(String acronym, String serviceType) throws  ExchangeModelMarshallException {
		
		IDType idType = new IDType();
		idType.setValue(acronym);		
		
		CodeType codeType = new CodeType();
		codeType.setValue(serviceType);		
		
		MDRQueryType mdrQueryType = new MDRQueryType();
		mdrQueryType.setObjAcronym(idType);
		mdrQueryType.setServiceType(codeType);	
		
		BasicAttribute reqOb = new BasicAttribute();
		reqOb.setMDRQuery(mdrQueryType);
		XMLGregorianCalendar date = null;
		try {
			date = createXMLDate();
		} catch (DatatypeConfigurationException e) {
			throw new ExchangeModelMarshallException("DatatypeConfigurationException Exception thrown while trying to create XML Calendar."+e.getMessage());
		}
		reqOb.setCreation(date);

		SetFLUXMDRSyncMessageRulesRequest fluxRequestObject = new SetFLUXMDRSyncMessageRulesRequest();
		fluxRequestObject.setRequest(JAXBMarshaller.marshallJaxBObjectToString(reqOb));
		fluxRequestObject.setMethod(RulesModuleMethod.SET_FLUX_MDR_SYNC_REQUEST);
		return JAXBMarshaller.marshallJaxBObjectToString(fluxRequestObject);
	}

	/**
	 * Creates a new XMLGregorianCalendar instance.
	 *
	 * @return
	 * @throws DatatypeConfigurationException
     */
	private static XMLGregorianCalendar createXMLDate() throws DatatypeConfigurationException {
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().toGregorianCalendar());
	}
	
}
