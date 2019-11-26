package eu.europa.ec.fisheries.uvms.activity.rest;

import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetListResponse;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetQuery;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("assetmock/internal")
@Stateless
public class AssetRestMock {

    @POST
    @Path("query")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssetList(@DefaultValue("1") @QueryParam("page") int page,
                                 @DefaultValue("100") @QueryParam("size") int size,
                                 @DefaultValue("true") @QueryParam("dynamic") boolean dynamic,
                                 @DefaultValue("false") @QueryParam("includeInactivated") boolean includeInactivated,
                                 AssetQuery query) {

        final String SWE_TEST_CFR = "SWEFAKE00001";
        if (query.getCfr() != null && query.getCfr().size() > 0 &&
                SWE_TEST_CFR.equals(query.getCfr().get(0))) {
            List<AssetDTO> assetDtoList = new ArrayList<>();

            AssetDTO assetSweTestDto = new AssetDTO();
            assetSweTestDto.setId(UUID.fromString("054cef8a-5cb2-48d2-9247-61a3be5ef03a"));
            assetSweTestDto.setCfr(SWE_TEST_CFR);
            assetSweTestDto.setImo("7774444");
            assetSweTestDto.setName("The so called " + SWE_TEST_CFR);
            assetDtoList.add(assetSweTestDto);

            AssetListResponse listAssetResponse = new AssetListResponse();
            listAssetResponse.setCurrentPage(1);
            listAssetResponse.setTotalNumberOfPages(1);
            listAssetResponse.getAssetList().addAll(assetDtoList);

            return Response.ok(listAssetResponse).build();
        }

        throw new IllegalStateException("*** The mock " + this.getClass().getName() + " called with unknown parameters");
    }
}
