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

package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import eu.europa.ec.fisheries.ers.service.bean.ActivityService;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.FishingTripSummaryViewDTO;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by sanera on 04/08/2016.
 */
@Path("/fishingTrip")
@Slf4j
@Stateless

public class FishingTripResource extends UnionVMSResource {

    private final static Logger LOG = LoggerFactory.getLogger(FishingTripResource.class);

    @Context
    private UriInfo context;

    @EJB
    private ActivityService activityService;

    @GET
    @Path("/summary/{fishingTripId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFishingTripSummary(@Context HttpServletRequest request,
                                        @Context HttpServletResponse response,
                                        @PathParam("fishingTripId") String fishingTripId) {
        Response responseMethod;
        LOG.info("Fishing Trip summary from fishing trip:"+fishingTripId);
        FishingTripSummaryViewDTO fishingTripSummaryViewDTO= null;
        try {
             fishingTripSummaryViewDTO = activityService.getFishingTripSummary(fishingTripId);
        } catch (ServiceException e) {
            LOG.error("Exception while trying to get Fishing trip summary For Fishing Trip:"+fishingTripId,e);
            responseMethod = createErrorResponse("Exception while trying to get Fishing trip summary For Fishing Trip.");
        }

        LOG.info("successful");
        responseMethod =createSuccessResponse(fishingTripSummaryViewDTO);
        return responseMethod;
    }
}
