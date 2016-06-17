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
	public Response getAreaTypes() {
		log.info("Starting MDR Synchronization");
		syncBean.manualStartMdrSynchronization();
		log.info("Finished MDR Synchronization");
		return createSuccessResponse();
	}
	
	private Response createSuccessResponse() {
		ResponseDto responseDTO = new ResponseDto(HttpServletResponse.SC_OK);
		Response response = Response.status(HttpServletResponse.SC_OK).entity(responseDTO).build();
		return response;
	}
}
