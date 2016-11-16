/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
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

import eu.europa.ec.fisheries.mdr.repository.MdrRepository;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityFeaturesEnum;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.util.ActivityExceptionInterceptor;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.util.IUserRoleInterceptor;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by georgige on 8/22/2016.
 */
@Slf4j
@Path("/cl")
public class MDRCodeListResource extends UnionVMSResource {

    @EJB
    private MdrRepository mdrService;

    @GET
    @Path("/{acronym}/{offset}/{pageSize}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.MDR_SEARCH_CODE_LIST_ITEMS})
    public Response findCodeListByAcronymFilterredByFilter(@PathParam("acronym") String acronym,
                                                            @PathParam("offset") Integer offset,
                                                            @PathParam("pageSize") Integer pageSize,
                                                            @QueryParam("sortBy") String sortBy,
                                                            @QueryParam("sortReversed") Boolean isReversed,
                                                            @QueryParam("filter") String filter,
                                                            @QueryParam("searchAttribute") String searchAttribute) {
        log.debug("findCodeListByAcronymFilterredByFilter(acronym={}, offset={}, pageSize={}, sortBy={}, isReversed={}, filter={}, searchAttribute={})", acronym,offset,pageSize,sortBy,isReversed,filter,searchAttribute);
        try {
            return createSuccessResponse(mdrService.findCodeListItemsByAcronymAndFilter(acronym, offset, pageSize, sortBy, isReversed, filter, searchAttribute));
        } catch (ServiceException e) {
            log.error("Internal Server Error.", e);
            return createErrorResponse("internal_server_error");
        }
    }
}
