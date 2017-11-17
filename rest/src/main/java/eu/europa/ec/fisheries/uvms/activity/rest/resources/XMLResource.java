/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */


package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import eu.europa.ec.fisheries.ers.service.FaCatchReportService;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityRequestMapper;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportResponse;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is a resource to call services exposed tru the JMS channel
 */
@Path("/xml")
@Slf4j
public class XMLResource {

    @EJB
    private FaCatchReportService faCatchReportService;

    @POST
    @Produces(value = {MediaType.APPLICATION_XML})
    @Consumes(value = {MediaType.APPLICATION_XML})
    @Path("/faCatchReport")
    public FACatchSummaryReportResponse getFaCatchSummaryReport(FACatchSummaryReportRequest request) throws ServiceException {

        FACatchSummaryReportResponse response = faCatchReportService.getFACatchSummaryReportResponse(FishingActivityRequestMapper.buildFishingActivityQueryFromRequest(request));
        return response;

    }

}