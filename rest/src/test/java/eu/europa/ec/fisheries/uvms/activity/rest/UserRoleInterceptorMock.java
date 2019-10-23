package eu.europa.ec.fisheries.uvms.activity.rest;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * Replaces UserRoleInterceptor in the test war file used in BaseActivityArquillianTest.
 * Completely skips over all checks for if the user has the correct role or not.
 */
@IUserRoleInterceptor
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class UserRoleInterceptorMock {
    @AroundInvoke
    public Object interceptRequest(final InvocationContext ic) throws Exception {
        // Come on in, everybody!
        return ic.proceed();
    }
}
