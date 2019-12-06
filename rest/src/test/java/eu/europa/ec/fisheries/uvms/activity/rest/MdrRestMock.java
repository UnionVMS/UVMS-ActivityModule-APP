package eu.europa.ec.fisheries.uvms.activity.rest;

import org.jetbrains.annotations.NotNull;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListRequest;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListResponse;
import un.unece.uncefact.data.standard.mdr.communication.MdrModuleMethod;
import un.unece.uncefact.data.standard.mdr.communication.ValidationResult;
import un.unece.uncefact.data.standard.mdr.communication.ValidationResultType;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("mdrmock/mdrnonsecure/json")
@Stateless
public class MdrRestMock {

    @POST
    @Path("getAcronym")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAcronymMock(@NotNull MdrGetCodeListRequest request) {
        MdrGetCodeListResponse mdrGetCodeListResponse = new MdrGetCodeListResponse();

        // TODO base return result on request content
        mdrGetCodeListResponse.setAcronym(request.getAcronym());
        mdrGetCodeListResponse.setValidation(new ValidationResult(ValidationResultType.OK, "Validation is OK."));
        mdrGetCodeListResponse.setMethod(MdrModuleMethod.MDR_CODE_LIST_RESP);
        mdrGetCodeListResponse.setDataSets(new ArrayList<>(0));
        return Response.ok(mdrGetCodeListResponse).build();
    }
}
