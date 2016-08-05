/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import eu.europa.ec.fisheries.ers.service.bean.ActivityService;
import eu.europa.ec.fisheries.ers.service.bean.FluxMessageService;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.constants.ErrorCodes;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import un.unece.uncefact.data.standard.fluxfareportmessage._1.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FishingActivity;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
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
    private final static Logger LOG = LoggerFactory.getLogger(FishingActivityResource.class);

    @EJB
    private FluxMessageService fluxResponseMessageService;


    @EJB
    private ActivityService activityService;

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


    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/listByQuery")
    public Response listActivityReportsByQuery(@Context HttpServletRequest request,
                                               @Context HttpServletResponse response, FishingActivityQuery fishingActivityQuery) {
        Response responseMethod;
        if( request.isUserInRole("LIST_ACTIVITY_REPORTS")){
            LOG.info("Query Received to search Fishing Activity Reports. "+fishingActivityQuery);

            if(fishingActivityQuery==null)
                return  createErrorResponse("Query to find list is null.");

            List<FishingActivityReportDTO> dtoList= null;
            try {
                dtoList = activityService.getFishingActivityListByQuery(fishingActivityQuery);
                responseMethod = createSuccessResponse(dtoList);
                LOG.info("successful");
            } catch (ServiceException e) {
                LOG.error("Exception while trying to get Fishing Activity Report list.",e);
                responseMethod = createErrorResponse("Exception while trying to get Fishing Activity Report list.");
            }
        }else{
            responseMethod= createErrorResponse(ErrorCodes.NOT_AUTHORIZED);
        }
        return responseMethod;
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listActivityReports(@Context HttpServletRequest request,
                                        @Context HttpServletResponse response) {
        Response responseMethod;
        if( request.isUserInRole("LIST_ACTIVITY_REPORTS")) {
            LOG.info("listActivityReports ");
            List<FishingActivityReportDTO> dtoList = activityService.getFishingActivityList();
            responseMethod = createSuccessResponse(dtoList);
            LOG.info("successful");
        }else{
            responseMethod= createErrorResponse(ErrorCodes.NOT_AUTHORIZED);
        }
        return responseMethod;
    }
}
