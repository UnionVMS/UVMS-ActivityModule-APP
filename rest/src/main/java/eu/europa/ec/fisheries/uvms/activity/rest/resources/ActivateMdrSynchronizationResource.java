package eu.europa.ec.fisheries.uvms.activity.rest.resources;

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
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import lombok.extern.slf4j.Slf4j;

@Path("/mdr")
@Slf4j
@Stateless
public class ActivateMdrSynchronizationResource extends UnionVMSResource {

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
}
