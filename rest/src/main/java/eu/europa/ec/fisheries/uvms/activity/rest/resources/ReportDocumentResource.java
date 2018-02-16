/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.uvms.commons.rest.resource.UnionVMSResource;
import lombok.extern.slf4j.Slf4j;

@Path("/report")
@Slf4j
@Stateless
public class ReportDocumentResource extends UnionVMSResource {

    @EJB
    private FishingTripService fishingTripService;

    @GET
    @Path("/{tripID}")
    @Produces(value = {MediaType.APPLICATION_XML})
    public Object test(@PathParam("tripID") String tripID){
        return fishingTripService.getFaReportDocumentsForTrip(tripID);

    }
}
