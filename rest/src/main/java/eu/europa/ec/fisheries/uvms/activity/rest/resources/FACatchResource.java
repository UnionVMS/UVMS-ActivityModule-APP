/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityFeaturesEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportResponse;
import eu.europa.ec.fisheries.uvms.activity.rest.ActivityExceptionInterceptor;
import eu.europa.ec.fisheries.uvms.activity.rest.IUserRoleInterceptor;
import eu.europa.ec.fisheries.uvms.activity.service.FaCatchReportService;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.commons.rest.resource.UnionVMSResource;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/catch")
@Slf4j
@Stateless
public class FACatchResource extends UnionVMSResource {

    @Context
    private UriInfo context;

    @EJB
    private FaCatchReportService reportService;

    @POST
    @Path("/summary")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getFACatchSummaryReport(FishingActivityQuery fishingActivityQuery) throws ServiceException {

        log.debug("Query Received to getFACatchSummaryReport. " + fishingActivityQuery);
        if (fishingActivityQuery == null) {
            return createErrorResponse("Query to find list is null.");
        }
        FACatchSummaryReportResponse faCatchSummaryReportResponse = reportService.getFACatchSummaryReportResponse(fishingActivityQuery);
        log.debug("Successfully processed");
        return createSuccessResponse(faCatchSummaryReportResponse);
    }

    @GET
    @Path("/details/{fishingTripId}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getFACatchSummaryDetails(@PathParam("fishingTripId") String tripId) throws ServiceException {
        log.debug("getFACatchSummaryDetails: " + tripId);
        return createSuccessResponse( reportService.getCatchDetailsScreen(tripId));
    }
}


