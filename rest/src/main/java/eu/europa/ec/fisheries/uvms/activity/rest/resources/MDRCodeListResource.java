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

import eu.europa.ec.fisheries.mdr.domain.codelists.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.repository.MdrRepository;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityFeaturesEnum;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.util.ActivityExceptionInterceptor;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.util.IUserRoleInterceptor;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.dto.SearchRequestDto;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.ejb.EJB;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

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
    public Response findCodeListByAcronymFilterredByFilter(@Context HttpServletRequest request,
                                                            @PathParam("acronym") String acronym,
                                                            @PathParam("offset") Integer offset,
                                                            @PathParam("pageSize") Integer pageSize,
                                                            @QueryParam("sortBy") String sortBy,
                                                            @QueryParam("sortReversed") Boolean isReversed,
                                                            @QueryParam("filter") String filter,
                                                            @QueryParam("searchAttribute") String searchAttribute) {
        log.debug("findCodeListByAcronymFilterredByFilter(acronym={}, offset={}, pageSize={}, sortBy={}, isReversed={}, filter={}, searchAttribute={})", acronym,offset,pageSize,sortBy,isReversed,filter,searchAttribute);
        try {
            List<? extends MasterDataRegistry> mdrList = mdrService.findCodeListItemsByAcronymAndFilter(acronym, offset, pageSize, sortBy, isReversed, filter, searchAttribute);
            int totalCodeItemsCount = mdrService.countCodeListItemsByAcronymAndFilter(acronym, filter, searchAttribute);
            return createSuccessPaginatedResponse(mdrList, totalCodeItemsCount);
        } catch (ServiceException e) {
            log.error("Internal Server Error.", e);
            return createErrorResponse("internal_server_error");
        }
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.MDR_SEARCH_CODE_LIST_ITEMS})
    public Response findCodeListByAcronymFilterredByFilter(@Context HttpServletRequest request,
                                                           SearchRequestDto searchRequest) {
        Response response;

        Map<String, String> criteria = searchRequest.getCriteria();

        if (criteria != null && criteria.size() > 0) {
            String acronym = criteria.get("acronym");
            SearchRequestDto.PaginationDto pagination = searchRequest.getPagination();
            int offset = pagination!=null?pagination.getOffset():0;
            int pageSize = pagination!=null?pagination.getPageSize():Integer.MAX_VALUE;
            SearchRequestDto.SortingDto sorting = searchRequest.getSorting();
            String sortBy = sorting!=null? sorting.getSortBy():null;
            boolean isReversed = sorting!=null? sorting.isReversed():false;

            String filter = criteria.get("filter");
            String searchAttribute = criteria.get("searchAttribute");

            if (acronym == null || StringUtils.isBlank(acronym)) {
                response = createErrorResponse("missing_required_parameter_acronym");
            } else {
                try {
                    response = this.findCodeListByAcronymFilterredByFilter(request, acronym, offset, pageSize, sortBy, isReversed, filter, searchAttribute);
                } catch (NumberFormatException e) {
                    log.error("Internal Server Error.", e);
                    response = createErrorResponse("internal_server_error");
                }
            }
        } else {
            response = createErrorResponse("missing_required_criteria");
        }

        return response;
    }

}
