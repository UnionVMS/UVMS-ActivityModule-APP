/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Arrival;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Departure;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Landing;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.NotificationOfArrival;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.ers.service.FluxMessageService;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.ActivityViewEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityFeaturesEnum;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.util.ActivityExceptionInterceptor;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.util.IUserRoleInterceptor;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import eu.europa.ec.fisheries.uvms.rest.security.bean.USMService;
import eu.europa.ec.fisheries.uvms.spatial.model.constants.USMSpatial;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;
import lombok.extern.slf4j.Slf4j;

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

    @GET
    @Path("/arrival/{activityId}/")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(Arrival.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getActivityArrivalView(@Context HttpServletRequest request,
                                           @Context HttpServletResponse response,
                                           @HeaderParam("scopeName") String scopeName,
                                           @HeaderParam("roleName") String roleName,
                                           @PathParam("activityId") String activityId) throws ServiceException {
        return createActivityView(scopeName, roleName, activityId, request, ActivityViewEnum.ARRIVAL);
    }


    @GET
    @Path("/landing/{activityId}/")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(Landing.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getActivityLandingView(@Context HttpServletRequest request,
                                           @Context HttpServletResponse response,
                                           @HeaderParam("scopeName") String scopeName,
                                           @HeaderParam("roleName") String roleName,
                                           @PathParam("activityId") String activityId) throws ServiceException {
        return createActivityView(scopeName, roleName, activityId, request, ActivityViewEnum.LANDING);
    }

    @GET
    @Path("/discard/{activityId}/")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(Landing.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getDiscardAtSeaView(@Context HttpServletRequest request,
                                        @Context HttpServletResponse response,
                                        @HeaderParam("scopeName") String scopeName,
                                        @HeaderParam("roleName") String roleName,
                                        @PathParam("activityId") String activityId) throws ServiceException {
        return createActivityView(scopeName, roleName, activityId, request, ActivityViewEnum.DISCARD_AT_SEA);
    }

    @GET
    @Path("/departure/{activityId}/")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(Departure.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getActivityDepartureView(@Context HttpServletRequest request,
                                             @Context HttpServletResponse response,
                                             @HeaderParam("scopeName") String scopeName,
                                             @HeaderParam("roleName") String roleName,
                                             @PathParam("activityId") String activityId) throws ServiceException {
        return createActivityView(scopeName, roleName, activityId, request, ActivityViewEnum.DEPARTURE);
    }

    @GET
    @Path("/notification/{activityId}/")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(NotificationOfArrival.class)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getActivityNotificationOfArrivalView(@Context HttpServletRequest request,
                                           @Context HttpServletResponse response,
                                           @HeaderParam("scopeName") String scopeName,
                                           @HeaderParam("roleName") String roleName,
                                           @PathParam("activityId") String activityId) throws ServiceException {
        return createActivityView(scopeName, roleName, activityId, request, ActivityViewEnum.PRIOR_NOTIFICATION_OF_ARRIVAL);
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
    private Response createActivityView(String scopeName, String roleName, String activityId, HttpServletRequest request, ActivityViewEnum view) throws ServiceException {
        String username        = request.getRemoteUser();
        List<Dataset> datasets = usmService.getDatasetsPerCategory(USMSpatial.USM_DATASET_CATEGORY, username, USMSpatial.APPLICATION_NAME, roleName, scopeName);
        return createSuccessResponse(activityService.getFishingActivityForView(activityId, datasets, view));
    }
}
