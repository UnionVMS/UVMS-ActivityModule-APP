/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.service.util;

import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.File;
import java.io.IOException;

/**
 * Created by sanera on 26/10/2016.
 */
public class CreateTestDataXML {

    public static void main(String[] args){

        File file = new File("service/src/test/resources/fa_flux_message.xml");
        try {
            file.createNewFile();

            System.out.println("FILE CREATED");

            JAXBContext jaxbContext = JAXBContext.newInstance(FLUXFAReportMessage.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            FLUXFAReportMessage fluxFAReportMessage= MapperUtil.getFLUXFAReportMessage();
            System.out.println("GET DATA");

            marshaller.marshal(fluxFAReportMessage, file);
            System.out.println("WRITE FILE TO THE SYSTEM");
            marshaller.marshal(fluxFAReportMessage, System.out);
            System.out.println("DONE");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PropertyException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }
}
