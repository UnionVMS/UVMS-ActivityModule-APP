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

import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityFeaturesEnum;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.util.ActivityExceptionInterceptor;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.util.IUserRoleInterceptor;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;

import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by padhyad on 8/22/2016.
 */
@Path("/config")
public class PreferenceConfigResource extends UnionVMSResource {

    @GET
    @Path("/admin")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.MANAGE_ADMIN_CONFIGURATIONS})
    public Response getAdminConfig(@Context HttpServletRequest request,
                                   @Context HttpServletResponse response) {
        return createSuccessResponse();
    }

    @POST
    @Path("/admin")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.MANAGE_ADMIN_CONFIGURATIONS})
    public Response saveAdminConfig(@Context HttpServletRequest request,
                                    @Context HttpServletResponse response) {
        return createSuccessResponse();
    }

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    public Response getUserConfig(@Context HttpServletRequest request,
                                  @Context HttpServletResponse response) {
        return createSuccessResponse();
    }

    @POST
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    public Response saveUserConfig(@Context HttpServletRequest request,
                                  @Context HttpServletResponse response) {
        return createSuccessResponse();
    }

    @POST
    @Path("/user/reset")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    public Response resetUserConfig(@Context HttpServletRequest request,
                                    @Context HttpServletResponse response) {
        return createSuccessResponse();
    }


}
