package eu.europa.ec.fisheries.uvms.activity.rest;

import eu.europa.ec.fisheries.uvms.movement.client.model.MicroMovementExtended;
import eu.europa.ec.fisheries.uvms.movement.model.dto.MicroMovementsForConnectIdsBetweenDatesRequest;
import org.slf4j.MDC;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("movementmock/internal")
@Stateless
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
public class MovementRestMock {

    @POST
    @Path("microMovementsForConnectIdsBetweenDates")
    public Response getMicroMovementsForConnectIdsBetweenDates(MicroMovementsForConnectIdsBetweenDatesRequest request) {
        //List<String> vesselIds = request.getConnectIds();
        //Instant fromDate = request.getFromDate();
        //Instant toDate = request.getToDate();

        // TODO populate based on request parameters
        List<MicroMovementExtended> microMovements = new ArrayList<>(0);

        return Response.ok(microMovements).header("MDC", MDC.get("requestId")).build();
    }
}
