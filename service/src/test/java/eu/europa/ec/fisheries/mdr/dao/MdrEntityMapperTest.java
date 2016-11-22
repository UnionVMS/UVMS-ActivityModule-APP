/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.dao;

import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.mapper.MdrEntityMapper;
import eu.europa.ec.fisheries.mdr.mapper.MdrRequestMapper;
import eu.europa.ec.fisheries.mdr.util.ClassFinder;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import un.unece.uncefact.data.standard.response.FLUXMDRReturnMessage;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;
import un.unece.uncefact.data.standard.response.MDRDataSetType;
import un.unece.uncefact.data.standard.response.TextType;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class MdrEntityMapperTest {

	FLUXMDRReturnMessage responseType;
	String entityName;
	
	@Before
	public void prepare(){
		entityName = "ACTION_TYPE";
		responseType   = mockJaxbCodeElementType();
		
	}
	
	@Test
	@SneakyThrows
	public void testLoadingOfTheClassesInTheCache() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		List<MasterDataRegistry> mdrEntity = MdrEntityMapper.mapJAXBObjectToMasterDataType(responseType);
		assertNotNull(mdrEntity);
		assertEquals(2, mdrEntity.size());
	}
	
	
	@Test
	@SneakyThrows
	public void testMdrEntityJaxBMarshalling(){
		String strReqObj = MdrRequestMapper.mapMdrQueryTypeToString(entityName, "ALL");
		assertNotNull(strReqObj);
	}
	
	@Test
	@SneakyThrows
	public void testJarFilesExtractor(){	
		List<Class<? extends MasterDataRegistry>> classList = ClassFinder.extractEntityInstancesFromPackage();		
		System.out.println("DONE"+classList);
	}

	private FLUXMDRReturnMessage mockJaxbCodeElementType() {

		FLUXMDRReturnMessage response       = new FLUXMDRReturnMessage();
		MDRDataSetType mdrListType = new MDRDataSetType();

		MDRDataNodeType codeElemType_1 = new MDRDataNodeType();

		MDRDataNodeType codeElemType_2 = new MDRDataNodeType();
		
		TextType acronym = new TextType();
		acronym.setValue(entityName);
		
		mdrListType.setContainedMDRDataNodes(Arrays.asList(codeElemType_1, codeElemType_2));
		mdrListType.setName(acronym);
		
		response.setMDRDataSet(mdrListType);
		
		return response;
	}

	@Test
	@SneakyThrows
	public void testCreateRequestForMDRAcronym(){
		String theReqStr = MdrRequestMapper.mapMdrQueryTypeToString("ACTION_TYPE", "OBJ_DESC");
		System.out.println("YES");
	}
	
}
