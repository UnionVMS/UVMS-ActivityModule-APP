/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import eu.europa.ec.fisheries.ers.service.bean.FluxMessageService;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.fluxfareportmessage._1.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FishingActivity;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by padhyad on 7/6/2016.
 */
@Path("/fa")
@Slf4j
@Stateless
public class FishingActivityResource extends UnionVMSResource {

    @EJB
    private FluxMessageService fluxResponseMessageService;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/save")
    public Response saveFaReportDocument() throws Exception {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("fa_flux_message.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(FLUXFAReportMessage.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        FLUXFAReportMessage fluxfaReportMessage = (FLUXFAReportMessage) jaxbUnmarshaller.unmarshal(is);

        for (FAReportDocument faReportDocument : fluxfaReportMessage.getFAReportDocuments()) {
            CodeType purposeCode = new CodeType();
            purposeCode.setValue("5");
            purposeCode.setListID("Test scheme Id");
            faReportDocument.getRelatedFLUXReportDocument().setPurposeCode(purposeCode);
        }

        fluxResponseMessageService.saveFishingActivityReportDocuments(fluxfaReportMessage.getFAReportDocuments());

        List<FAReportDocument> faReportDocumentList = fluxfaReportMessage.getFAReportDocuments();
        for (FAReportDocument faReportDocument : faReportDocumentList) {
            IDType id = faReportDocument.getRelatedFLUXReportDocument().getIDS().get(0);
            faReportDocument.getRelatedFLUXReportDocument().setReferencedID(id);

            IDType newId = new IDType();
            newId.setValue("New Id 1");
            newId.setSchemeID("New scheme Id 1");
            faReportDocument.getRelatedFLUXReportDocument().setIDS(Arrays.asList(newId));

            CodeType purposeCode = new CodeType();
            purposeCode.setValue("5");
            purposeCode.setListID("Test scheme Id");
            faReportDocument.getRelatedFLUXReportDocument().setPurposeCode(purposeCode);

            for (FishingActivity fishingActivity : faReportDocument.getSpecifiedFishingActivities()) {
                fishingActivity.setRelatedFishingActivities(null);
            }
        }
        fluxResponseMessageService.saveFishingActivityReportDocuments(faReportDocumentList);
        return createSuccessResponse();
    }
}
