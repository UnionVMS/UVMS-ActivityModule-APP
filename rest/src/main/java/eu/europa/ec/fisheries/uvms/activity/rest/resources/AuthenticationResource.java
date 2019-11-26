/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import eu.europa.ec.fisheries.uvms.activity.rest.LoginResponse;
import eu.europa.ec.fisheries.uvms.activity.service.auth.LdapAuthentication;
import eu.europa.ec.mare.usm.jwt.JwtTokenHandler;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/auth")
@Slf4j
@Stateless
public class AuthenticationResource {

    @EJB
    private LdapAuthentication ldapAuthentication;

    @EJB
    private JwtTokenHandler tokenHandler;

    @POST
    @Path("/login")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginDto loginDto) {
        boolean authenticated = ldapAuthentication.doAuthentication(loginDto.getUsername(), loginDto.getPassword());
        if (authenticated) {
            String token = tokenHandler.createToken(loginDto.getUsername());
            return Response.ok(new LoginResponse(1, 1234, token)).build();
        }
        return Response.status(401).build();
    }
}


