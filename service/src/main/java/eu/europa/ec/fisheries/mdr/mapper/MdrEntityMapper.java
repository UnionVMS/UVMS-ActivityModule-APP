/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.mapper;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import eu.europa.ec.fisheries.mdr.domain.MasterDataRegistry;
import lombok.extern.slf4j.Slf4j;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.CodeElementType;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.ResponseType;

/**
 * Mapper class that can be used to map entities from MDR
 **/
@Slf4j
public class MdrEntityMapper {
	
	private static MasterDataRegistryEntityCacheFactory mdrEntityFactory = MasterDataRegistryEntityCacheFactory.getInstance();
	
	// This class isn't supposed to have instances.
	private MdrEntityMapper(){}
	
	
	/**
	 *  Entry point for mapping Entities that extend MasterDataType
	 * 
	 * @param response
	 * @return
	 */
	public static List<MasterDataRegistry> mappJAXBObjectToMasterDataType(ResponseType response){
		return mapJaxbToMDRType(response.getMDRCodeList().getCodeElements(), response.getMDRCodeList().getObjAcronym().getValue());
	}

	/**
	 * Common-Method for mapping Entities that extend the MasterDataRegistry.
	 * 
	 * @param  codeElementsTypeList
	 * @param  acronym
	 * @return entityList
	 */
	private static List<MasterDataRegistry> mapJaxbToMDRType(List<CodeElementType> codeElements,
			String acronym) {
		List<MasterDataRegistry> entityList = new ArrayList<MasterDataRegistry>();
		for(CodeElementType actualJaxbElement : codeElements){
			MasterDataRegistry entity = null;
			try {
				entity = (MasterDataRegistry) mdrEntityFactory.getNewInstanceForEntity(acronym);
				entity.populateFromJAXBFields(actualJaxbElement);
			} catch (NullPointerException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				log.error("Exception while attempting to map JAXBObject to MDR Entity. (MdrEntityMapper class)");
				e.printStackTrace();
			}	
			entityList.add(entity);
		}
		return entityList;

	}

	
}