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

import eu.europa.ec.fisheries.ers.service.PreferenceConfigService;
import eu.europa.ec.fisheries.uvms.activity.model.dto.config.ActivityConfigDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityFeaturesEnum;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.util.ActivityExceptionInterceptor;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.util.IUserRoleInterceptor;
import eu.europa.ec.fisheries.uvms.constants.AuthConstants;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import eu.europa.ec.fisheries.uvms.rest.security.bean.USMService;

import javax.ejb.EJB;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by padhyad on 8/22/2016.
 */
@Path("/config")
public class PreferenceConfigResource extends UnionVMSResource {

    private static final String DEFAULT_CONFIG = "DEFAULT_CONFIG";
    private static final String USER_CONFIG = "USER_CONFIG";

    @EJB
    private USMService usmService;

    @EJB
    private PreferenceConfigService preferenceConfigService;

    @GET
    @Path("/admin")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.MANAGE_ADMIN_CONFIGURATIONS})
    public Response getAdminConfig(@Context HttpServletRequest request,
                                   @Context HttpServletResponse response,
                                   @HeaderParam(AuthConstants.HTTP_HEADER_SCOPE_NAME) String scopeName,
                                   @HeaderParam(AuthConstants.HTTP_HEADER_ROLE_NAME) String roleName) throws ServiceException {

        String applicationName = request.getServletContext().getInitParameter("usmApplication");
        String adminConfig = usmService.getOptionDefaultValue(DEFAULT_CONFIG, applicationName);
        return createSuccessResponse(preferenceConfigService.getAdminConfig(adminConfig));
    }

    @POST
    @Path("/admin")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.MANAGE_ADMIN_CONFIGURATIONS})
    public Response saveAdminConfig(@Context HttpServletRequest request,
                                    @Context HttpServletResponse response,
                                    @HeaderParam(AuthConstants.HTTP_HEADER_SCOPE_NAME) String scopeName,
                                    @HeaderParam(AuthConstants.HTTP_HEADER_ROLE_NAME) String roleName,
                                    ActivityConfigDTO activityConfigDTO) throws ServiceException {
        String applicationName = request.getServletContext().getInitParameter("usmApplication");
        String adminJson = preferenceConfigService.saveAdminConfig(activityConfigDTO);
        usmService.setOptionDefaultValue(DEFAULT_CONFIG, adminJson, applicationName);
        return createSuccessResponse();
    }

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    public Response getUserConfig(@Context HttpServletRequest request,
                                  @Context HttpServletResponse response,
                                  @HeaderParam(AuthConstants.HTTP_HEADER_SCOPE_NAME) String scopeName,
                                  @HeaderParam(AuthConstants.HTTP_HEADER_ROLE_NAME) String roleName) throws ServiceException {
        String applicationName = request.getServletContext().getInitParameter("usmApplication");
        String username = request.getRemoteUser();
        String adminConfig = usmService.getOptionDefaultValue(DEFAULT_CONFIG, applicationName);
        String userConfig = usmService.getUserPreference(USER_CONFIG, username, applicationName, roleName, scopeName);
        return createSuccessResponse(preferenceConfigService.getUserConfig(userConfig, adminConfig));
    }

    @POST
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    public Response saveUserConfig(@Context HttpServletRequest request,
                                   @Context HttpServletResponse response,
                                   @HeaderParam(AuthConstants.HTTP_HEADER_SCOPE_NAME) String scopeName,
                                   @HeaderParam(AuthConstants.HTTP_HEADER_ROLE_NAME) String roleName,
                                   ActivityConfigDTO activityConfigDTO) throws ServiceException {
        String applicationName = request.getServletContext().getInitParameter("usmApplication");
        String username = request.getRemoteUser();
        String userConfig = usmService.getUserPreference(USER_CONFIG, username, applicationName, roleName, scopeName);
        String updatedConfig = preferenceConfigService.saveUserConfig(activityConfigDTO, userConfig);
        usmService.putUserPreference(USER_CONFIG, updatedConfig, applicationName, scopeName, roleName, username);
        return createSuccessResponse();
    }

    @POST
    @Path("/user/reset")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    public Response resetUserConfig(@Context HttpServletRequest request,
                                    @Context HttpServletResponse response,
                                    @HeaderParam(AuthConstants.HTTP_HEADER_SCOPE_NAME) String scopeName,
                                    @HeaderParam(AuthConstants.HTTP_HEADER_ROLE_NAME) String roleName,
                                    ActivityConfigDTO activityConfigDTO) throws ServiceException {
        String applicationName = request.getServletContext().getInitParameter("usmApplication");
        String username = request.getRemoteUser();
        String userConfig = usmService.getUserPreference(USER_CONFIG, username, applicationName, roleName, scopeName);
        String updatedConfig = preferenceConfigService.resetUserConfig(activityConfigDTO, userConfig);
        usmService.putUserPreference(USER_CONFIG, updatedConfig, applicationName, scopeName, roleName, username);
        return createSuccessResponse();
    }
}
