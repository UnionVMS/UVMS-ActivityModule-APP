/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.service.bean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.europa.ec.fisheries.mdr.service.MdrSynchronizationService;
import eu.europa.ec.fisheries.uvms.rest.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;

@Path("/mdr")
@Slf4j
@Stateless
public class ActivateMdrSynchronizationResource {

	@EJB
	private MdrSynchronizationService syncBean;

	@GET
	@Path("/sync")
	@Produces(value = {MediaType.APPLICATION_JSON})
	public Response activateMdrSynchronization() {
		log.info("Starting MDR Synchronization");
		syncBean.manualStartMdrSynchronization();
		log.info("Finished MDR Synchronization");
		return createSuccessResponse();
	}
	
	@GET
	@Path("/flux")
	@Produces(value = {MediaType.APPLICATION_JSON})
	public Response recieveFluxMessage() {
		log.info("Starting MDR Synchronization");
		syncBean.sendMockedMessageToERSMDRQueue();
		log.info("Finished MDR Synchronization");
		return createSuccessResponse();
	}
	
	private Response createSuccessResponse() {
		ResponseDto responseDTO = new ResponseDto(HttpServletResponse.SC_OK);
		Response response = Response.status(HttpServletResponse.SC_OK).entity(responseDTO).build();
		return response;
	}
}