/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.mapper;
import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.ActivityCacheInitException;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.response.FLUXMDRReturnMessage;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper class that can be used to map entities from MDR
 **/
@Slf4j
public class MdrEntityMapper {
	
	private static MasterDataRegistryEntityCacheFactory mdrEntityFactory = MasterDataRegistryEntityCacheFactory.getInstance();
	
	// This class isn't supposed to have instances (singleton).
	private MdrEntityMapper(){}
	
	
	/**
	 *  Entry point for mapping Entities that extend MasterDataType
	 * 
	 * @param response
	 * @return
	 */
	public static List<MasterDataRegistry> mapJAXBObjectToMasterDataType(FLUXMDRReturnMessage response){
		return mapJaxbToMDRType(response.getMDRDataSet().getContainedMDRDataNodes(), response.getMDRDataSet().getName().getValue());
	}

	/**
	 * Common-Method for mapping Entities that extend the MasterDataRegistry.
	 * 
	 * @param  codeElements
	 * @param  acronym
	 *
	 * @return entityList
	 */
	private static List<MasterDataRegistry> mapJaxbToMDRType(List<MDRDataNodeType> codeElements, String acronym) {
		List<MasterDataRegistry> entityList = new ArrayList<>();
		for(MDRDataNodeType actualJaxbElement : codeElements){
			MasterDataRegistry entity = null;
			try {
				entity = mdrEntityFactory.getNewInstanceForEntity(acronym);
				entity.populateFromJAXBFields(actualJaxbElement);
			} catch (NullPointerException | SecurityException e) {
				log.error("Exception while attempting to map JAXBObject to MDR Entity. (MdrEntityMapper class)", e);
			} catch (FieldNotMappedException e) {
				log.error("Exception while attempting to map field to MDR Entity. (MdrEntityMapper class)", e);
			} catch (ActivityCacheInitException e) {
				log.error("Exception while attempting to call mdrEntityFactory.getNewInstanceForEntity(acronym).", e);
			}
			entityList.add(entity);
		}
		return entityList;

	}

	
}