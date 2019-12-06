package eu.europa.ec.fisheries.uvms.activity.rest;

import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetListResponse;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetQuery;
import org.apache.commons.collections.CollectionUtils;

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

        if (CollectionUtils.isEmpty(query.getCfr()) &&
            CollectionUtils.isEmpty(query.getIrcs()) &&
            CollectionUtils.isEmpty(query.getExternalMarking())) {
            throw new IllegalStateException("*** The mock " + this.getClass().getName() + " was called without any ship identifier");
        }

        List<AssetDTO> assetDtoList = new ArrayList<>();

        AssetDTO assetSweTestDto = new AssetDTO();
        assetSweTestDto.setId(getId(query));
        assetSweTestDto.setCfr(getFirstElementOrNull(query.getCfr()));
        assetSweTestDto.setImo(getImo(query));
        assetSweTestDto.setName(getName(query));
        assetDtoList.add(assetSweTestDto);

        AssetListResponse listAssetResponse = new AssetListResponse();
        listAssetResponse.setCurrentPage(1);
        listAssetResponse.setTotalNumberOfPages(1);
        listAssetResponse.getAssetList().addAll(assetDtoList);

        return Response.ok(listAssetResponse).build();
    }

    private UUID getId(AssetQuery query) {
        // TODO based on IDs in query
        return UUID.fromString("054cef8a-5cb2-48d2-9247-61a3be5ef03a");
    }

    private String getImo(AssetQuery query) {
        // TODO based on IDs in query
        return "7774444";
    }

    private String getFirstElementOrNull(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    private String getName(AssetQuery query) {
        if (!CollectionUtils.isEmpty(query.getCfr())) {
            return "The so called " + query.getCfr().get(0);
        }
        if (!CollectionUtils.isEmpty(query.getIrcs())) {
            return "The so called " + query.getIrcs().get(0);
        }
        if (!CollectionUtils.isEmpty(query.getExternalMarking())) {
            return "The so called " + query.getExternalMarking().get(0);
        }

        return "Generic Boat Name";
    }
}
