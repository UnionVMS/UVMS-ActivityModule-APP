/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.util;

import eu.europa.ec.fisheries.mdr.domain.MasterDataRegistry;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ClassFinder {

	private static final String MDR_DOMAIN_PACKAGE_WOUT_DOT = "eu.europa.ec.fisheries.mdr.domain";

	private ClassFinder(){}

	public static List<Class<? extends MasterDataRegistry>> extractEntityInstancesFromPackage(){		
		Reflections reflections = new Reflections(MDR_DOMAIN_PACKAGE_WOUT_DOT);
		Set<Class<? extends MasterDataRegistry>> subTypes = reflections.getSubTypesOf(MasterDataRegistry.class);		
		return new ArrayList<Class<? extends MasterDataRegistry>>(subTypes);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}