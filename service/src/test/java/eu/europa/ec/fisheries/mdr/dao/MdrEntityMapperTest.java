package eu.europa.ec.fisheries.mdr.dao;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.europa.ec.fisheries.mdr.domain.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.mapper.MdrEntityMapper;
import un.unece.uncefact.data.standard.unqualifieddatatype._13.IDType;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.CodeElementType;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.MDRCodeListType;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.ResponseType;


public class MdrEntityMapperTest {

	ResponseType responseType;
	String entityName;
	
	@Before
	public void prepare(){
		entityName = "ACTION_TYPE";
		responseType   = mockJaxbCodeElementType();
		
	}
	
	@Test
	public void testLoadingOfTheClassesInTheCache() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		List<MasterDataRegistry> mdrEntity = MdrEntityMapper.mappJAXBObjectToMasterDataType(responseType);
		assertNotNull(mdrEntity);
		assertEquals(2, mdrEntity.size());
	}

	private ResponseType mockJaxbCodeElementType() {
		
		ResponseType response       = new ResponseType();		
		MDRCodeListType mdrListType = new MDRCodeListType();
		
		CodeElementType codeElemType_1 = new CodeElementType();
		codeElemType_1.setValidityPeriod(null);
		
		CodeElementType codeElemType_2 = new CodeElementType();
		codeElemType_2.setValidityPeriod(null);
		
		IDType acronym = new IDType();
		acronym.setValue(entityName);
		
		mdrListType.setCodeElements(Arrays.asList(codeElemType_1, codeElemType_2));
		mdrListType.setObjAcronym(acronym);
		
		response.setMDRCodeList(mdrListType);
		
		return response;
	}
	
}
