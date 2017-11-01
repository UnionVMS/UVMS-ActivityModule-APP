/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.ers.service.FluxMessageService;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityViewDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.FishingOperation;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.ActivityViewEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityFeaturesEnum;
import eu.europa.ec.fisheries.uvms.activity.rest.ActivityExceptionInterceptor;
import eu.europa.ec.fisheries.uvms.activity.rest.IUserRoleInterceptor;
import eu.europa.ec.fisheries.uvms.commons.rest.resource.UnionVMSResource;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.security.bean.USMService;
import eu.europa.ec.fisheries.uvms.spatial.model.constants.USMSpatial;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.AreaEntry;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Arrival;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Departure;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.GearShotAndRetrieval;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Landing;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.NotificationOfArrival;

/**
 * Created by kovian on 08/02/2017.
 */
@Path("/fa/views")
@Slf4j
@Stateless
public class FishingActivityViewsResource extends UnionVMSResource {

    @EJB
    private FluxMessageService fluxResponseMessageService;

    @EJB
    private ActivityService activityService;

    @EJB
    private USMService usmService;

    @POST
    @Path("/arrival")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @JsonView(Arrival.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getActivityArrivalView(@Context HttpServletRequest request,
                                           @Context HttpServletResponse response,
                                           @HeaderParam("scopeName") String scopeName,
                                           @HeaderParam("roleName") String roleName,
                                           ActivityViewDto activityViewDto) throws ServiceException {
        return createActivityView(scopeName, roleName, activityViewDto.getActivityId(), activityViewDto.getTripId(), request, ActivityViewEnum.ARRIVAL);
    }


    @POST
    @Path("/landing")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @JsonView(Landing.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getActivityLandingView(@Context HttpServletRequest request,
                                           @Context HttpServletResponse response,
                                           @HeaderParam("scopeName") String scopeName,
                                           @HeaderParam("roleName") String roleName,
                                           ActivityViewDto activityViewDto) throws ServiceException {
        return createActivityView(scopeName, roleName, activityViewDto.getActivityId(), activityViewDto.getTripId(), request, ActivityViewEnum.LANDING);
    }

    @POST
    @Path("/discard")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @JsonView(Landing.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getDiscardAtSeaView(@Context HttpServletRequest request,
                                        @Context HttpServletResponse response,
                                        @HeaderParam("scopeName") String scopeName,
                                        @HeaderParam("roleName") String roleName,
                                        ActivityViewDto activityViewDto) throws ServiceException {
        return createActivityView(scopeName, roleName, activityViewDto.getActivityId(), activityViewDto.getTripId(), request, ActivityViewEnum.DISCARD_AT_SEA);
    }

    @POST
    @Path("/departure")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @JsonView(Departure.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getActivityDepartureView(@Context HttpServletRequest request,
                                             @Context HttpServletResponse response,
                                             @HeaderParam("scopeName") String scopeName,
                                             @HeaderParam("roleName") String roleName,
                                             ActivityViewDto activityViewDto) throws ServiceException {
        return createActivityView(scopeName, roleName, activityViewDto.getActivityId(), activityViewDto.getTripId(), request, ActivityViewEnum.DEPARTURE);
    }

    @POST
    @Path("/notification")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @JsonView(NotificationOfArrival.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getActivityNotificationOfArrivalView(@Context HttpServletRequest request,
                                                         @Context HttpServletResponse response,
                                                         @HeaderParam("scopeName") String scopeName,
                                                         @HeaderParam("roleName") String roleName,
                                                         ActivityViewDto activityViewDto) throws ServiceException {
        return createActivityView(scopeName, roleName, activityViewDto.getActivityId(), activityViewDto.getTripId(), request, ActivityViewEnum.PRIOR_NOTIFICATION_OF_ARRIVAL);
    }


    @POST
    @Path("/areaEntry")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @JsonView(AreaEntry.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getActivityAreaEntryView(@Context HttpServletRequest request,
                                             @Context HttpServletResponse response,
                                             @HeaderParam("scopeName") String scopeName,
                                             @HeaderParam("roleName") String roleName,
                                             ActivityViewDto activityViewDto) throws ServiceException {
        return createActivityView(scopeName, roleName, activityViewDto.getActivityId(), activityViewDto.getTripId(), request, ActivityViewEnum.AREA_ENTRY);
    }


    @POST
    @Path("/areaExit")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @JsonView(FishingActivityView.AreaExit.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getActivityAreaExitView(@Context HttpServletRequest request,
                                            @Context HttpServletResponse response,
                                            @HeaderParam("scopeName") String scopeName,
                                            @HeaderParam("roleName") String roleName,
                                            ActivityViewDto activityViewDto) throws ServiceException {
        return createActivityView(scopeName, roleName, activityViewDto.getActivityId(), activityViewDto.getTripId(), request, ActivityViewEnum.AREA_EXIT);
    }


    @POST
    @Path("/gearshot")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @JsonView(GearShotAndRetrieval.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getGearShotAndRetrievalView(@Context HttpServletRequest request,
                                                @Context HttpServletResponse response,
                                                @HeaderParam("scopeName") String scopeName,
                                                @HeaderParam("roleName") String roleName,
                                                ActivityViewDto activityViewDto) throws ServiceException {
        return createActivityView(scopeName, roleName, activityViewDto.getActivityId(), activityViewDto.getTripId(), request, ActivityViewEnum.GEAR_SHOT_RETRIEVAL);
    }


    @POST
    @Path("/transhipment")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @JsonView(FishingActivityView.Transhipment.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getTranshipmentView(@Context HttpServletRequest request,
                                        @Context HttpServletResponse response,
                                        @HeaderParam("scopeName") String scopeName,
                                        @HeaderParam("roleName") String roleName,
                                        ActivityViewDto activityViewDto) throws ServiceException {
        return createActivityView(scopeName, roleName, activityViewDto.getActivityId(), activityViewDto.getTripId(), request, ActivityViewEnum.TRANSSHIPMENT);
    }

    @POST
    @Path("/fishingoperation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @JsonView(FishingOperation.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getFishingOperationView(@Context HttpServletRequest request,
                                            @Context HttpServletResponse response,
                                            @HeaderParam("scopeName") String scopeName,
                                            @HeaderParam("roleName") String roleName,
                                            ActivityViewDto activityViewDto) throws ServiceException {
        return createActivityView(scopeName, roleName, activityViewDto.getActivityId(), activityViewDto.getTripId(), request, ActivityViewEnum.FISHING_OPERATION);
    }


    @POST
    @Path("/relocation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @JsonView(FishingActivityView.Relocation.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getRelocationView(@Context HttpServletRequest request,
                                      @Context HttpServletResponse response,
                                      @HeaderParam("scopeName") String scopeName,
                                      @HeaderParam("roleName") String roleName,
                                      ActivityViewDto activityViewDto) throws ServiceException {
        return createActivityView(scopeName, roleName, activityViewDto.getActivityId(), activityViewDto.getTripId(), request, ActivityViewEnum.RELOCATION);
    }

    @POST
    @Path("/jointfishingoperation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @JsonView(FishingActivityView.JointFishingOperation.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getJointFishingOperationView(@Context HttpServletRequest request,
                                                 @Context HttpServletResponse response,
                                                 @HeaderParam("scopeName") String scopeName,
                                                 @HeaderParam("roleName") String roleName,
                                                 ActivityViewDto activityViewDto) throws ServiceException {
        return createActivityView(scopeName, roleName, activityViewDto.getActivityId(), activityViewDto.getTripId(), request, ActivityViewEnum.JOINT_FISHING_OPERATIONS);
    }


    /**
     * Depending on the view creates the DTO to return to the front-end.
     *
     * @param scopeName
     * @param roleName
     * @param activityId
     * @param request
     * @return View DTO
     * @throws ServiceException
     */
    private Response createActivityView(String scopeName, String roleName, Integer activityId, String tripId, HttpServletRequest request, ActivityViewEnum view) throws ServiceException {
        String username = request.getRemoteUser();
        List<Dataset> datasets = usmService.getDatasetsPerCategory(USMSpatial.USM_DATASET_CATEGORY, username, USMSpatial.APPLICATION_NAME, roleName, scopeName);
        return createSuccessResponse(activityService.getFishingActivityForView(activityId, tripId, datasets, view));
    }
}
