/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import eu.europa.ec.fisheries.mdr.domain.MdrStatus;
import eu.europa.ec.fisheries.mdr.repository.MdrStatusRepository;
import eu.europa.ec.fisheries.mdr.service.MdrSynchronizationService;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityFeaturesEnum;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;

@Path("/mdr")
@Slf4j
@Stateless
public class MdrSynchronizationResource extends UnionVMSResource {

    private static final String ERROR_GETTING_AVAIL_MDR = "An error occured while trying to get MDR available Acronyms List.";
    private static final String ERROR_MANUAL_MDR_SYNC   = "An error occured while trying to manually update the MDR List.";

	@EJB
	private MdrSynchronizationService syncBean;

	@EJB
	private MdrStatusRepository mdrStatusBean;

    /**
     * Requests synchronization of all "updatable" code lists.
     *
     * @return
     */
	@GET
	@Path("/sync")
	@Produces(value = {MediaType.APPLICATION_JSON})
	public Response synchronizeNow(@Context HttpServletRequest request) {
		if (request.isUserInRole(ActivityFeaturesEnum.UPDATE_MDR_CODE_LISTS.toString())) {
			log.info("Starting MDR Synchronization...");
			boolean withErrors = syncBean.manualStartMdrSynchronization();
			log.info("Finished MDR Synchronization with error flag : "+withErrors);
			if(withErrors){
				return createErrorResponse(ERROR_MANUAL_MDR_SYNC);
			}
			return createSuccessResponse();
		} else {
			return createAccessForbiddenResponse("User not allowed to request MDR code lists update");
		}
	}

	/**
	 * Requests synchronization of all code lists, specified as an array of acronyms.
	 *
	 * @return
	 */
	@POST
	@Path("/sync")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(value = {MediaType.APPLICATION_JSON})
	public Response synchronizeNow(@Context HttpServletRequest request, @Context HttpServletResponse response, Collection<String> acronymsToSynch) {
		if (request.isUserInRole(ActivityFeaturesEnum.UPDATE_MDR_CODE_LISTS.toString())) {
			log.info("Starting MDR Synchronization...");
			boolean withErrors = syncBean.updateMdrEntities((List<String>) acronymsToSynch);
			log.info("Finished MDR Synchronization with error flag : " + withErrors);
			if (withErrors) {
				return createErrorResponse(ERROR_MANUAL_MDR_SYNC);
			}
			return createSuccessResponse();
		} else {
			return createAccessForbiddenResponse("User not allowed to request MDR code lists update");
		}
	}


	@GET
	@Path("/flux")
	@Produces(value = {MediaType.APPLICATION_JSON})
	public Response receiveFluxMessage() {
		log.info("Starting MDR Synchronization");
		syncBean.sendMockedMessageToERSMDRQueue();
		log.info("Finished MDR Synchronization");
		return createSuccessResponse();
	}

    /**
	 * Returns an array with all the available MDR code lists and their details (last successful update, last update attempt datetime, status, etc.).
	 * The details do not contain the individual MDR code lists content.
     *
     * @return createSuccessResponse(acronymsList)
     */
	@GET
	@Path("/acronyms/details")
	@Produces(value = {MediaType.APPLICATION_JSON})
	public Response getAvailableMdrAcronymsStatuses(@Context HttpServletRequest request) {
		log.debug("[START] getAvailableMdrAcronymsDetails ");
		if (request.isUserInRole(ActivityFeaturesEnum.LIST_MDR_CODE_LISTS.toString())) {
			List<MdrStatus> acronymsList = mdrStatusBean.getAllAcronymsStatuses();
			if (CollectionUtils.isEmpty(acronymsList)) {
				return createErrorResponse(ERROR_GETTING_AVAIL_MDR);
			} else {
				log.debug("{} MDR code lists returned.", acronymsList.size());
				return createSuccessResponse(acronymsList);
			}
		} else {
			return createAccessForbiddenResponse("User not allowed to list all MDR code lists details.");
		}
	}

	/**
	 * Resource for obtaining all the available MDR acronyms (aka acronymsList)
	 *
	 * @return createSuccessResponse(acronymsList)
	 */
	@GET
	@Path("/acronyms")
	@Produces(value = {MediaType.APPLICATION_JSON})
	public Response getAvailableMdrAcronyms() {
		List<String> acronymsList =  syncBean.getAvailableMdrAcronyms();
		if(CollectionUtils.isEmpty(acronymsList)){
			return createErrorResponse(ERROR_GETTING_AVAIL_MDR);
		}
		return createSuccessResponse(acronymsList);
	}

	/**
	 * Gets the actual stored scheduler configuration. 
	 * 
	 * @return
	 */
	@GET
	@Path("/schedulerConfig")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSchedulerConfiguration() {
		return createSuccessResponse(syncBean.getActualSchedulerConfiguration());
	}

	/**
	 * Saves the given cronConfigStr to the ActivityConfiguration Table.
	 * 
	 * @param request
	 * @param cronConfigStr
	 */
	@PUT
	@Path("/schedulerConfig")
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveSchedulerConfiguration(@Context HttpServletRequest request, String cronConfigStr) {
		if (request.isUserInRole(ActivityFeaturesEnum.CONFIGURE_MDR_SCHEDULER.toString())) {
			syncBean.reconfigureScheduler(cronConfigStr);
			return createSuccessResponse();
		} else {
			return createAccessForbiddenResponse("User not allowed to modify MDR scheduler");
		}
	}
}
