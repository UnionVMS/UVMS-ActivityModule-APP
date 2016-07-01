package eu.europa.ec.fisheries.mdr.mapper;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;

import eu.europa.ec.fisheries.schema.rules.module.v1.RulesModuleMethod;
import eu.europa.ec.fisheries.schema.rules.module.v1.SetFLUXMDRSyncMessageRequest;
import eu.europa.ec.fisheries.uvms.exception.ModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import un.unece.uncefact.data.standard.unqualifieddatatype._13.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._13.IDType;
import xeu.ec.fisheries.flux_bl.flux_mdr_query._1.BasicAttribute;
import xeu.ec.fisheries.flux_bl.flux_mdr_query._1.MDRQueryType;

public class MdrRequestMapper {

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
	public static String createNewMDRQueryTypeAndMapItToString(String acronym, String serviceType) throws ModelMarshallException, ExchangeModelMarshallException {
		
		IDType idType = new IDType();
		idType.setValue(acronym);		
		
		CodeType codeType = new CodeType();
		codeType.setValue(serviceType);		
		
		MDRQueryType mdrQueryType = new MDRQueryType();
		mdrQueryType.setObjAcronym(idType);
		mdrQueryType.setServiceType(codeType);	
		
		BasicAttribute reqOb = new BasicAttribute();
		reqOb.setMDRQuery(mdrQueryType);
		reqOb.setCreation(createXMLDate());

		SetFLUXMDRSyncMessageRequest fluxRequestObject = new SetFLUXMDRSyncMessageRequest();
		fluxRequestObject.setRequest(JAXBMarshaller.marshallJaxBObjectToString(reqOb));
		fluxRequestObject.setMethod(RulesModuleMethod.SET_FLUX_MDR_SYNC_REQUEST);
		return JAXBMarshaller.marshallJaxBObjectToString(fluxRequestObject);
	}

	private static XMLGregorianCalendar createXMLDate() {
		XMLGregorianCalendar xgc = null;
		try {
			xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().toGregorianCalendar());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return xgc;
	}
	
}
