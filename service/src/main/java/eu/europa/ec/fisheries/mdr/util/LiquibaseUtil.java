/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.mdr.util;

import org.apache.commons.compress.utils.Charsets;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kovian on 24/11/2016.
 */
public class LiquibaseUtil {

    static String splitter     = "<changeSet ";
    static String changeLogFilePath = "C:\\GIT Repository\\activity-trunk\\db-liquibase\\LIQUIBASE\\postgres\\changelog\\generatedChangelog.xml";
    static String locationForGeneratedXmls = "C:\\newLiquibaseScripts\\";
    static String filePrefix   = "";
    static String fileSuffix   = ".xml";
    private static int sequence = 0;

    public static void createXMLLiquibaseEntries() throws IOException {

        String filecontentStr = readFile(changeLogFilePath, Charsets.UTF_8);

        List<String> filesContents = Arrays.asList(filecontentStr.split(splitter));
        filesContents = fixFileContents(filesContents, splitter);
        System.out.println("\n\n I Found : " + filesContents.size() + " Change Sets in location : " + changeLogFilePath);

        for (String content : filesContents) {
            String fileName = getFileName(filePrefix, fileSuffix, content, locationForGeneratedXmls);
            String filePath = new StringBuilder(locationForGeneratedXmls).append(filePrefix).append(fileName).append(fileSuffix).toString();
            String header   = createHeader(fileName);
            String footer   = createFooter(fileName);
            String finalContent = addHeaderAndFooter(content, header, footer);
            setSequence(sequence++);
            createNewFile(filePath, finalContent);
        }
    }

    private static String createFooter(String fileName) {
        String footer = "\t\n" +
                "\t<changeSet author=\"kovian\" id=\"76817789687171-"+sequence+"\" dbms=\"postgresql\">\n" +
                "\t\t<addDefaultValue \n" +
                "\t\t\t\tcolumnDataType=\"BIGINT\"\n" +
                "\t\t\t\tcolumnName=\"id\"\n" +
                "\t\t\t\tdefaultValueSequenceNext=\""+fileName+"_seq\"\n" +
                "\t\t\t\ttableName=\""+fileName+"\"/>\n" +
                "\t</changeSet>\t\n" +
                "\t\n" +
                "</databaseChangeLog>";
        return footer;
    }

    private static String createHeader(String fileName) {
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<databaseChangeLog xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\" \n" +
                "\t\t\t\t   xmlns:ext=\"http://www.liquibase.org/xml/ns/dbchangelog-ext\" \n" +
                "\t\t\t\t   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                "\t\t\t\t   xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog " +
                "http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.3.xsd http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd\">\n" +
                "       \n" +
                "    <changeSet author=\"kovian\" id=\"1890672105152481"+sequence+"\">\n" +
                "\t  <createSequence cycle=\"false\" incrementBy=\"1\" maxValue=\"9223372036854775807\" minValue=\"1\"\n" +
                "\t\t\t\t\t  sequenceName=\""+fileName+"_seq\" startValue=\"1\"/>\n" +
                "    </changeSet>  \n";
        return header;
    }

    private static String addHeaderAndFooter(String content, String header, String footer) {
        return new StringBuilder(header).append(content).append(footer).toString();
    }

    private static List<String> fixFileContents(List<String> filesContents, String splitter) {
        List<String> fixedContents = new ArrayList<>();
        for (String content : filesContents) {
            if (content.contains("tableName=")) {
                String contentR = splitter + content;
                fixedContents.add(contentR);
            }
        }
        return fixedContents;
    }

    private static String getFileName(String filePrefix, String fileSuffix, String content, String newLocation) {
        String cutStr = content.substring(content.indexOf("tableName=\""), content.indexOf("tableName=\"") + 100);
        String fileName = cutStr.substring(cutStr.indexOf("\"") + 1, cutStr.indexOf("\"", cutStr.indexOf("\"") + 1));
        return fileName;
    }


    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private static void createNewFile(String filePath, String content) {
        System.out.println("\n Creating file : " + filePath);
        File fileEntry = new File(filePath);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileEntry), Charsets.UTF_8))) {
            writer.write(content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int getSequence() {
        return sequence;
    }
    public static void setSequence(int sequence1) {
        sequence = sequence1;
    }

}
