/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.FluxMessageService;
import eu.europa.ec.fisheries.ers.service.dto.FilterFishingActivityReportResultDTO;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityFeaturesEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripResponse;
import eu.europa.ec.fisheries.uvms.activity.rest.ActivityExceptionInterceptor;
import eu.europa.ec.fisheries.uvms.activity.rest.IUserRoleInterceptor;
import eu.europa.ec.fisheries.uvms.commons.rest.resource.UnionVMSResource;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.security.bean.USMService;
import eu.europa.ec.fisheries.uvms.spatial.model.constants.USMSpatial;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
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

    @EJB
    private ActivityService activityService;

    @EJB
    private FishingTripService fishingTripService;


    @EJB
    private USMService usmService;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/save")
    public Response saveFaReportDocument() throws ServiceException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("fa_flux_message_cedric_data.xml");
        JAXBContext jaxbContext;
        FLUXFAReportMessage fluxfaReportMessage;
        try {
            jaxbContext = JAXBContext.newInstance(FLUXFAReportMessage.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            fluxfaReportMessage = (FLUXFAReportMessage) jaxbUnmarshaller.unmarshal(is);
        } catch (JAXBException e) {
            log.error("Error occured during Unmorshalling of the FLUXFAReportMessage", e);
           throw new ServiceException(e.getMessage());
        }
        fluxResponseMessageService.saveFishingActivityReportDocuments(fluxfaReportMessage, FaReportSourceEnum.FLUX);
        return createSuccessResponse();
    }


    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/commChannel")
    public Response getCommunicationChannel() throws ServiceException {

        return createSuccessResponse(FaReportSourceEnum.values());
    }


    @POST
    @Path("/list")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response listActivityReportsByQuery(@Context HttpServletRequest request,
                                               @HeaderParam("scopeName") String scopeName,
                                               @HeaderParam("roleName") String roleName,
                                               FishingActivityQuery fishingActivityQuery) throws ServiceException {

        log.info("Query Received to search Fishing Activity Reports. " + fishingActivityQuery);
        if (fishingActivityQuery == null) {
            return createErrorResponse("Query to find list is null.");
        }
        String username = request.getRemoteUser();
        List<Dataset> datasets = usmService.getDatasetsPerCategory(USMSpatial.USM_DATASET_CATEGORY, username, USMSpatial.APPLICATION_NAME, roleName, scopeName);
        log.info("Successful retrieved");
        FilterFishingActivityReportResultDTO resultDTO = activityService.getFishingActivityListByQuery(fishingActivityQuery, datasets);
        return createSuccessPaginatedResponse(resultDTO.getResultList(), resultDTO.getTotalCountOfRecords());
    }

    @POST
    @Path("/listTrips")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response listFishingTripsByQuery(@Context HttpServletRequest request,
                                               @HeaderParam("scopeName") String scopeName,
                                               @HeaderParam("roleName") String roleName,
                                               FishingActivityQuery fishingActivityQuery) throws ServiceException {

        log.info("Query Received to search Fishing Activity Reports. " + fishingActivityQuery);
        if (fishingActivityQuery == null) {
            return createErrorResponse("Query to find list is null.");
        }
        String username = request.getRemoteUser();
        List<Dataset> datasets = usmService.getDatasetsPerCategory(USMSpatial.USM_DATASET_CATEGORY, username, USMSpatial.APPLICATION_NAME, roleName, scopeName);
        log.info("Successful retrieved");
        FishingTripResponse fishingTripIdsForFilter = fishingTripService.filterFishingTrips(fishingActivityQuery);
        return createSuccessResponse(fishingTripIdsForFilter);
    }

    @GET
    @Path("/history/{referenceId}/{schemeId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getAllCorrections(@Context HttpServletRequest request,
                                      @Context HttpServletResponse response,
                                      @PathParam("referenceId") String referenceId,
                                      @PathParam("schemeId") String schemeId) throws ServiceException {

        return createSuccessResponse(activityService.getFaReportCorrections(referenceId, schemeId));
    }


    @GET
    @Path("/previous/{activityId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getPreviousFishingActivity(@Context HttpServletRequest request,
                                      @Context HttpServletResponse response,
                                      @PathParam("activityId") String activityId) throws ServiceException {
        int converstedActivityId=0;
        log.info("Received ActivityId from frontEnd as: " + activityId);
        if(activityId !=null){

            converstedActivityId=Integer.parseInt(activityId);
        }
        return createSuccessResponse(activityService.getPreviousFishingActivity(converstedActivityId));
    }

    @GET
    @Path("/next/{activityId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getNextFishingActivity(@Context HttpServletRequest request,
                                               @Context HttpServletResponse response,
                                               @PathParam("activityId") String activityId) throws ServiceException {
        int converstedActivityId=0;
        log.info("Received ActivityId from frontEnd as: " + activityId);
        if(activityId !=null){

            converstedActivityId=Integer.parseInt(activityId);
        }
        return createSuccessResponse(activityService.getNextFishingActivity(converstedActivityId));
    }

}
