package eu.europa.ec.fisheries.mdr.mapper;

import eu.europa.ec.fisheries.uvms.exception.ModelMarshallException;
import un.unece.uncefact.data.standard.unqualifieddatatype._13.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._13.IDType;
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
	 */
	public static String mapMDRQueryTypeToString(String acronym, String serviceType) throws ModelMarshallException {
		IDType idType = new IDType();
		idType.setValue(acronym);		
		CodeType codeType = new CodeType();
		codeType.setValue(serviceType);		
		MDRQueryType request = new MDRQueryType();
		request.setObjAcronym(idType);
		request.setServiceType(codeType);				
		return JAXBMarshaller.marshallJaxBObjectToString(request);
	}
	
}
