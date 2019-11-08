package eu.europa.ec.fisheries.uvms.activity.rest;

import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListRequest;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListResponse;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("mdrnonsecure/json")
@Stateless
public class MdrResourceMock {

    @POST
    @Path("getAcronym")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAcronymMock(MdrGetCodeListRequest request) {
        MdrGetCodeListResponse entity = new MdrGetCodeListResponse();
        entity.setDataSets(new ArrayList<>(0));
        return Response.ok(entity).build();
    }
}
