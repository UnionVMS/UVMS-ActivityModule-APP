/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.mapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import eu.europa.ec.fisheries.mdr.domain.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.util.ClassFinder;
import lombok.extern.slf4j.Slf4j;

/***
 * This class is designed to perform a scanning of the MDR Entities Package and build a cache to furtherly 
 * be used to construct request objects to send request to FLUX.
 *
 */
@Slf4j
public class MasterDataRegistryEntityCacheFactory {

	private static Map<String, Object> acronymsCache;
	private static List<String> acronymsList;
	
	private static MasterDataRegistryEntityCacheFactory instance;
	private static final String METHOD_ACRONYM   = "getAcronym";
	private static final String ENTITIES_PACKAGE = "eu.europa.ec.fisheries.mdr.domain";
	
	@PostConstruct
	private void initializeClass(){
		instance = new MasterDataRegistryEntityCacheFactory();
		try {
			log.info("Initializing MasterDataRegistryEntityCacheFactory class.");
			initializeCache();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.error("Failed to initiate MasterDataRegistryEntityCacheFactory class.");
			e.printStackTrace();
		}
		
	}

	public static MasterDataRegistryEntityCacheFactory getInstance(){
		if(instance == null){
			instance = new MasterDataRegistryEntityCacheFactory();
			try {
				initializeCache();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e1) {
				e1.printStackTrace();
			}
			try {
				MasterDataRegistryEntityCacheFactory.initializeCache();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	public Object getNewInstanceForEntity(String entityAcronym) throws NullPointerException, InvocationTargetException, NoSuchMethodException, SecurityException {
		try {
			if(MapUtils.isEmpty(acronymsCache)){
				initializeCache();
			}
			return acronymsCache.get(entityAcronym).getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			throw new NullPointerException();
		}
	}
	
	private static void initializeCache() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		List<Class<? extends MasterDataRegistry>> entitiesList = null;
		
		try {
			entitiesList = ClassFinder.extractEntityInstancesFromPackage();
		} catch(Exception ex){
			log.error("Couldn't extract entities from package "+ENTITIES_PACKAGE+" \n The following exception was thrown : \n"+ex.getMessage());
		}
		
		acronymsCache = new HashMap<>();
		acronymsList  = new ArrayList<String>();
		for (Class<?> aClass : entitiesList) {
			if(!Modifier.isAbstract(aClass.getModifiers())){
				String classAcronym     = (String) aClass.getMethod(METHOD_ACRONYM).invoke(aClass.newInstance());
				Object classReference =  aClass.newInstance();
				acronymsCache.put(classAcronym, classReference);
				acronymsList.add(classAcronym);
				log.info("Creating cache instance for : " + aClass.getCanonicalName());
			}
		}
	}
	
	/**
	 * Returns the List of all available Acronyms fro MDR.
	 * 
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static List<String> getAcronymsList() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {		
		if(CollectionUtils.isEmpty(acronymsList)){
			initializeCache();
		}		
		return acronymsList;
	}

	

}