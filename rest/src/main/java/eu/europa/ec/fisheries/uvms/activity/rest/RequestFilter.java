/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.activity.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebFilter(asyncSupported = true, urlPatterns = {"/*"})
public class RequestFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestFilter.class);

    /**
     * {@code corsOriginRegex} is valid for given host/origin names/IPs and any range of sub domains.
     * <p>
     * localhost:[2]8080
     * 127.0.0.1:[2]8080
     * 192.168.***.***:[2]8080
     * liaswf05[t,u,d]:[2]8080
     * havochvatten.se:[2]8080
     */
    @Resource(lookup = "java:global/cors_allowed_host_regex")
    private String corsOriginRegex;

    @Override
    public void init(FilterConfig filterConfig) {
        LOG.info("RequestFilter starting up!");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String origin = httpServletRequest.getHeader("ORIGIN");

        if(origin != null && validateOrigin(origin)) {
            HttpServletResponse response = (HttpServletResponse) res;
            response.setHeader(RestConstants.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            response.setHeader(RestConstants.ACCESS_CONTROL_ALLOW_METHODS, RestConstants.ACCESS_CONTROL_ALLOWED_METHODS);
            response.setHeader(RestConstants.ACCESS_CONTROL_ALLOW_HEADERS, RestConstants.ACCESS_CONTROL_ALLOW_HEADERS_ALL);

            if (httpServletRequest.getMethod().equals("OPTIONS")) {
                response.setStatus(200);
                return;
            }
        }
        chain.doFilter(request, res);
    }


    private boolean validateOrigin(String origin) {
        Pattern pattern = Pattern.compile(corsOriginRegex);
        Matcher matcher = pattern.matcher(origin);
        return matcher.matches();
    }

    @Override
    public void destroy() {
        LOG.info("RequestFilter shutting down!");
    }
}
