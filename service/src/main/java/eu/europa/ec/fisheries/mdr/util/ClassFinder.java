package eu.europa.ec.fisheries.mdr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import eu.europa.ec.fisheries.mdr.domain.MasterDataRegistry;

public final class ClassFinder {

	private static final String MDR_DOMAIN_PACKAGE_WOUT_DOT = "eu.europa.ec.fisheries.mdr.domain";

	public static List<Class<? extends MasterDataRegistry>> extractEntityInstancesFromPackage(){		
		Reflections reflections = new Reflections(MDR_DOMAIN_PACKAGE_WOUT_DOT);
		Set<Class<? extends MasterDataRegistry>> subTypes = reflections.getSubTypesOf(MasterDataRegistry.class);		
		return new ArrayList<Class<? extends MasterDataRegistry>>(subTypes);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}