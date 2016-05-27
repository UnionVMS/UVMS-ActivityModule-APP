package eu.europa.ec.fisheries.mdr.mapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MasterDataRegistryEntityCacheFactory {

	private static Map<String, Object> acronymsCache;
	private static MasterDataRegistryEntityCacheFactory instance;
	private static final String METHOD_ACRONYM   = "getAcronym";
	private static final String ENTITIES_PACKAGE = "eu.europa.ec.fisheries.mdr.domain";

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
			if(acronymsCache == null){
				initializeCache();
			}
			return (acronymsCache.get(entityAcronym)).getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			throw new NullPointerException();
		}
	}
	
	private static void initializeCache() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		List<Class<?>> classes = ClassFinder.find(ENTITIES_PACKAGE);
		acronymsCache = new HashMap<>();
		for (Class<?> aClass : classes) {
			if(!Modifier.isAbstract(aClass.getModifiers())){
				String classAcronym     = (String) aClass.getMethod(METHOD_ACRONYM).invoke(aClass.newInstance());
				Object classReference =  aClass.newInstance();
				acronymsCache.put(classAcronym, classReference);
				log.info("Creating cache instance for : " + aClass.getCanonicalName());
			}
		}
	}

	private final static class ClassFinder {

		private final static char DOT 				  = '.';
		private final static char SLASH 			  = '/';
		private final static String CLASS_SUFFIX 	  = ".class";
		private final static String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the given '%s' package exists?";

		public final static List<Class<?>> find(final String scannedPackage) {
			final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			final String scannedPath = scannedPackage.replace(DOT, SLASH);
			final Enumeration<URL> resources;
			try {
				resources = classLoader.getResources(scannedPath);
			} catch (IOException e) {
				throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage), e);
			}
			final List<Class<?>> classes = new LinkedList<Class<?>>();
			while (resources.hasMoreElements()) {
				final File file = new File(resources.nextElement().getFile());
				classes.addAll(find(file, scannedPackage));
			}
			return classes;
		}

		private final static List<Class<?>> find(final File file, final String scannedPackage) {
			final List<Class<?>> classes = new LinkedList<Class<?>>();
			if (file.isDirectory()) {
				for (File nestedFile : file.listFiles()) {
					classes.addAll(find(nestedFile, scannedPackage));
				}
				// File names with the $1, $2 holds the anonymous inner classes,
				// we are not interested on them.
			} else if (file.getName().endsWith(CLASS_SUFFIX) && !file.getName().contains("$")) {

				final int beginIndex = 0;
				final int endIndex = file.getName().length() - CLASS_SUFFIX.length();
				final String className = file.getName().substring(beginIndex, endIndex);
				try {
					final String resource = scannedPackage + DOT + className;
					classes.add(Class.forName(resource));
				} catch (ClassNotFoundException ignore) {
				}
			}
			return classes;
		}

	}

}
