/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.mdr.cachefactory;

import eu.europa.ec.fisheries.mdr.domain.codelists.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.mapper.MasterDataRegistryEntityCacheFactory;
import lombok.SneakyThrows;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kovian on 24/11/2016.
 */
@Ignore
public class MdrCacheFactoryTest {

    String filesPath              = "C:\\GIT Repository\\activity-trunk\\db-liquibase\\LIQUIBASE\\postgres\\schema\\tables";
    String includeDeclarationInit = "<include file=\"\\postgres\\schema\\tables\\";

    @Test
    @SneakyThrows
    public void testCacheInitAndPrintClassesForPersistenceXML(){
        MasterDataRegistryEntityCacheFactory.initialize();
        List<String> acronyms = MasterDataRegistryEntityCacheFactory.getAcronymsList();
        System.out.println("\nWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
        System.out.println("\nWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
        for(String acronym : acronyms){
            MasterDataRegistry mdrEntity = MasterDataRegistryEntityCacheFactory.getInstance().getNewInstanceForEntity(acronym);
            System.out.println("<class>"+mdrEntity.getClass().getCanonicalName().toString()+"</class>");
        }
        System.out.println("\nWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
        System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW\n\n\n\n");
    }

    @Test
    @SneakyThrows
    public void testEnlistFileEntriesInConsole(){
        String includeDeclarationEnd  = "\"/>";
        String limitToFilesThatContainThisString = "mdr_";
        List<String> fileNamesList = getFilesListFromPath(filesPath, limitToFilesThatContainThisString);
        for(String fileName : fileNamesList){
            System.out.println(includeDeclarationInit+fileName+includeDeclarationEnd);
        }
    }


    public static List<String> getFilesListFromPath(String filesPath, String limitToFileNamesContaining) {
        File folder = new File(filesPath);
        File[] listOfFiles = folder.listFiles();
        List<String> filesList = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(limitToFileNamesContaining)) {
                filesList.add(listOfFiles[i].getName());
            }
        }
        return filesList;
    }
}
