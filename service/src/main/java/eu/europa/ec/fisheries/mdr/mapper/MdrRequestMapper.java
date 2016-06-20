package eu.europa.ec.fisheries.mdr.mapper;



import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFLUXMDRSyncMessageRequest;
import eu.europa.ec.fisheries.uvms.exception.ModelMarshallException;
import un.unece.uncefact.data.standard.unqualifieddatatype._13.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._13.IDType;
import xeu.ec.fisheries.flux_bl.flux_mdr_query._1.BasicAttribute;
import xeu.ec.fisheries.flux_bl.flux_mdr_query._1.MDRQueryType;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;

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
	public static String mapMDRQueryTypeToString(String acronym, String serviceType) throws ModelMarshallException, ExchangeModelMarshallException {
		
		IDType idType = new IDType();
		idType.setValue(acronym);		
		
		CodeType codeType = new CodeType();
		codeType.setValue(serviceType);		
		
		MDRQueryType mdrQueryType = new MDRQueryType();
		mdrQueryType.setObjAcronym(idType);
		mdrQueryType.setServiceType(codeType);	
		
		BasicAttribute reqOb = new BasicAttribute();
		reqOb.setMDRQuery(mdrQueryType);
		
		SetFLUXMDRSyncMessageRequest fluxRequestObject = new SetFLUXMDRSyncMessageRequest();
		fluxRequestObject.setRequest(JAXBMarshaller.marshallJaxBObjectToString(reqOb));
		fluxRequestObject.setMethod(ExchangeModuleMethod.SET_MDR_SYNC_MESSAGE);
		return JAXBMarshaller.marshallJaxBObjectToString(fluxRequestObject);
	}
	
}
