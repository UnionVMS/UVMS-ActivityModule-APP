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

package eu.europa.ec.fisheries.uvms.activity.rest.resources.util;

import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.constants.ErrorCodes;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import lombok.extern.slf4j.Slf4j;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * Created by padhyad on 8/8/2016.
 */
@Interceptor
@Slf4j
public class ActivityExceptionInterceptor extends UnionVMSResource {

    private static final String EXCEPTION_THROWN_IN_ACTIVITY_MODULE = "Exception thrown in Activity module while trying to call the following method : {}";

    @AroundInvoke
    public Object createResponse(final InvocationContext ic) {
        log.info("ExceptionInterceptor received");
        try {
            return ic.proceed();
        } catch (IllegalArgumentException e) {
            log.error(EXCEPTION_THROWN_IN_ACTIVITY_MODULE,ic.getMethod(),e);
            return createErrorResponse(ErrorCodes.INPUT_NOT_SUPPORTED);
        } catch(ServiceException e){
            log.error(EXCEPTION_THROWN_IN_ACTIVITY_MODULE,ic.getMethod(),e);
            return createErrorResponse(e.getMessage());
        } catch (RuntimeException e){
            log.error(EXCEPTION_THROWN_IN_ACTIVITY_MODULE,ic.getMethod(),e);
            return createErrorResponse(e.getCause().getMessage());
        } catch (Exception e) {
            log.error(EXCEPTION_THROWN_IN_ACTIVITY_MODULE,ic.getMethod(),e);
            return createErrorResponse(ErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }
}
