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
import eu.europa.ec.fisheries.mdr.service.MdrSchedulerService;
import eu.europa.ec.fisheries.mdr.service.MdrSynchronizationService;
import eu.europa.ec.fisheries.mdr.util.GenericOperationOutcome;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityFeaturesEnum;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
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

	private static final String ERROR_GETTING_AVAIL_MDR = "An error occured while trying to get MDR available Acronyms List. The List is actually Empty! Have to reinitialize Activity Module!";
	private static final String ERROR_MANUAL_MDR_SYNC   = "An error occured while trying to manually update the MDR List.";

	@EJB
	private MdrSynchronizationService syncBean;

	@EJB
	private MdrSchedulerService schedulerService;

	@EJB
	private MdrStatusRepository mdrStatusBean;

	/**
	 * Requests synchronization of all "updatable" code lists.
	 * Only those acronyms that are "schedulable" will be affected;
	 *
	 * @return
	 */
	@GET
	@Path("/sync/all")
	@Produces(value = {MediaType.APPLICATION_JSON})
	public Response synchronizeAllAcronyms(@Context HttpServletRequest request) {
		if (request.isUserInRole(ActivityFeaturesEnum.UPDATE_MDR_CODE_LISTS.toString())) {
			log.info("Starting MDR Synchronization...");
			GenericOperationOutcome outcome = syncBean.manualStartMdrSynchronization();
			log.info("Finished MDR Synchronization with error flag : "+outcome.getStatus().toString());
			if(!outcome.isOK()){
				return createErrorResponse(ERROR_MANUAL_MDR_SYNC);
			}
			return createSuccessResponse();
		}
		return createAccessForbiddenResponse("User not allowed to request MDR code lists update");
	}

	/**
	 * Requests synchronization of all code lists, specified as an array of acronyms.
	 * All the list regardless of "schedulability" will be updated.
	 *
	 * @param request
	 * @param acronymsToSynch
     * @return response
     */
	@POST
	@Path("/sync/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(value = {MediaType.APPLICATION_JSON})
	public Response synchronizeListOfAcronyms(@Context HttpServletRequest request, Collection<String> acronymsToSynch) {
		if (request.isUserInRole(ActivityFeaturesEnum.UPDATE_MDR_CODE_LISTS.toString())) {
			log.info("Starting MDR Synchronization...");
			GenericOperationOutcome outcome = syncBean.updateMdrEntities((List<String>) acronymsToSynch);
			log.info("Finished MDR Synchronization with error flag : " + outcome.getStatus().toString());
			if (!outcome.isOK()) {
				return createErrorResponse(ERROR_MANUAL_MDR_SYNC);
			}
			return createSuccessResponse();
		}
		return createAccessForbiddenResponse("User not allowed to request a List of MDR code lists update");
	}


	//TODO to be removed after having the ERS-MDR communication sorted
	@GET
	@Path("/flux")
	@Produces(value = {MediaType.APPLICATION_JSON})
	public Response receiveFluxMessage(@Context HttpServletRequest request) {
		if (request.isUserInRole(ActivityFeaturesEnum.UPDATE_MDR_CODE_LISTS.toString())){
			log.info("Starting MDR Synchronization");
			syncBean.sendMockedMessageToERSMDRQueue();
			log.info("Finished MDR Synchronization");
			return createSuccessResponse();
		}
		return createAccessForbiddenResponse("User not allowed to use this mocked up function");
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
		}
		return createAccessForbiddenResponse("User not allowed to list all MDR code lists details.");
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
	 * @param request
	 * @return response
     */
	@GET
	@Path("/scheduler/config")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSchedulerConfiguration(@Context HttpServletRequest request) {
		if (request.isUserInRole(ActivityFeaturesEnum.CONFIGURE_MDR_SCHEDULER.toString())) {
			return createSuccessResponse(schedulerService.getActualSchedulerConfiguration());
		}
		return createAccessForbiddenResponse("User not allowed to get MDR scheduler configuration");
	}

	/**
	 * Saves the given cronConfigStr to the ActivityConfiguration Table.
	 *
	 * @param request
	 * @param cronConfigStr
	 */
	@PUT
	@Path("/scheduler/config/update")
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveSchedulerConfiguration(@Context HttpServletRequest request, String cronConfigStr) {
		if (request.isUserInRole(ActivityFeaturesEnum.CONFIGURE_MDR_SCHEDULER.toString())) {
			schedulerService.reconfigureScheduler(cronConfigStr);
			return createSuccessResponse();
		}
		return createAccessForbiddenResponse("User not allowed to modify MDR scheduler");
	}

	/**
	 * Updates the schedulable flag for a given acronym. This flag definies whether the acronym could be automatically updated, or not.
	 *
	 * @param request
	 * @param acronym
	 * @param schedulable
     * @return response
     */
	@PUT
	@Path("/status/schedulable/update/{acronym}/{schedulable}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(value = {MediaType.APPLICATION_JSON})
	public Response changeSchedulableForAcronym(@Context HttpServletRequest request, @PathParam("acronym") String acronym, @PathParam("schedulable") Boolean schedulable) {
		if (request.isUserInRole(ActivityFeaturesEnum.CODE_LISTS_ENABLE_DISABLE_SCHEDULED_UPDATE.toString())) {
			log.info("Changing schedulable for acronym : ", acronym);
			mdrStatusBean.updateSchedulableForAcronym(acronym, schedulable);
			return createSuccessResponse();
		}
		return createAccessForbiddenResponse("User not allowed to request MDR codeList schedulability change!");
	}
}
