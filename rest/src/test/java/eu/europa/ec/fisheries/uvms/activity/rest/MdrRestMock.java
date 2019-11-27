package eu.europa.ec.fisheries.uvms.activity.rest;

import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import org.jetbrains.annotations.NotNull;
import un.unece.uncefact.data.standard.mdr.communication.ColumnDataType;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListRequest;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListResponse;
import un.unece.uncefact.data.standard.mdr.communication.MdrModuleMethod;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;
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
import java.util.List;

@Path("mdrmock/mdrnonsecure/json")
@Stateless
public class MdrRestMock {

    @POST
    @Path("getAcronym")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAcronymMock(@NotNull MdrGetCodeListRequest request) {
        MdrGetCodeListResponse mdrGetCodeListResponse = new MdrGetCodeListResponse();

        if ("FLUX_VESSEL_ID_TYPE".equals(request.getAcronym()) &&
                "*".equals(request.getFilter())) {
            mdrGetCodeListResponse.setAcronym(request.getAcronym());
            mdrGetCodeListResponse.setValidation(new ValidationResult(ValidationResultType.OK, "Validation is OK."));
            mdrGetCodeListResponse.setMethod(MdrModuleMethod.MDR_CODE_LIST_RESP);

            List<ObjectRepresentation> objectRepresentations = new ArrayList<>();
            for (VesselIdentifierSchemeIdEnum vesselIdentifierSchemeIdEnum : VesselIdentifierSchemeIdEnum.values()) {
                List<ColumnDataType> fields = new ArrayList<>();
                fields.add(new ColumnDataType("code", vesselIdentifierSchemeIdEnum.name(), "String"));
                objectRepresentations.add(new ObjectRepresentation(fields));

            }
            mdrGetCodeListResponse.setDataSets(objectRepresentations);

            return Response.ok(mdrGetCodeListResponse).build();
        }

        throw new IllegalStateException("*** The mock " + this.getClass().getName() + " called with unknown parameters. request:" + request);
    }
}
