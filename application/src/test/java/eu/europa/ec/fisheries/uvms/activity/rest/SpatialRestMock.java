package eu.europa.ec.fisheries.uvms.activity.rest;

import eu.europa.ec.fisheries.uvms.spatial.model.schemas.FilterAreasSpatialRQ;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.FilterAreasSpatialRS;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.GeometryByPortCodeRequest;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.GeometryByPortCodeResponse;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("spatialmock/spatialnonsecure/json")
@Stateless
public class SpatialRestMock {

    @POST
    @Path("getGeometryByPortCode")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getGeometryByPortCode(GeometryByPortCodeRequest request) {

        // TODO populate based on request parameters
        return Response.ok(new GeometryByPortCodeResponse()).build();
    }

    @POST
    @Path("getFilterArea")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getFilteredAreaGeometry(FilterAreasSpatialRQ request) {

        // TODO populate based on request parameters
        return Response.ok(new FilterAreasSpatialRS()).build();
    }
}
