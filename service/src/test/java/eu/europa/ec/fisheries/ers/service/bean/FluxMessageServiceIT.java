/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import un.unece.uncefact.data.standard.fluxfareportmessage._1.FLUXFAReportMessage;

import javax.ejb.EJB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * Created by padhyad on 5/19/2016.
 */
@RunWith(Arquillian.class)
public class FluxMessageServiceIT extends BaseActivityArquillianTest {

    @EJB
    private FluxMessageService fluxResponseMessageService;

    @Test
    public void shouldSaveFaReportDocument() throws Exception {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("fa_flux_message.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(FLUXFAReportMessage.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        FLUXFAReportMessage fluxfaReportMessage = (FLUXFAReportMessage) jaxbUnmarshaller.unmarshal(is);
        fluxResponseMessageService.saveFishingActivityReportDocuments(fluxfaReportMessage.getFAReportDocuments(), FaReportSourceEnum.FLUX);
    }
}