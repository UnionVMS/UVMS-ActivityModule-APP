package eu.europa.ec.fisheries.uvms.activity.rest;

import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetListResponse;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.AssetSearchInterface;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchBranch;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchFields;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchLeaf;

import javax.ejb.Stateless;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
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
                                 @DefaultValue("false") @QueryParam("includeInactivated") boolean includeInactivated,
                                 String searchBranchAsString) {
        JsonbConfig config = new JsonbConfig();
        config.withDeserializers(new SearchBranchDeserializer());

        SearchBranch query = JsonbBuilder.create(config).fromJson(searchBranchAsString, SearchBranch.class);

        boolean foundSupportedShipIdentifier =
                getLeafValue(query, SearchFields.CFR) != null ||
                getLeafValue(query, SearchFields.IRCS) != null ||
                getLeafValue(query, SearchFields.EXTERNAL_MARKING) != null;

        if (!foundSupportedShipIdentifier) {
            throw new IllegalStateException("*** The mock " + this.getClass().getName() + " was called without any ship identifier");
        }

        List<AssetDTO> assetDtoList = new ArrayList<>();

        AssetDTO assetSweTestDto = new AssetDTO();
        assetSweTestDto.setId(getId(query));
        assetSweTestDto.setCfr(getLeafValue(query, SearchFields.CFR));
        assetSweTestDto.setImo(getLeafValue(query, SearchFields.IMO));
        assetSweTestDto.setName(getName(query));
        assetDtoList.add(assetSweTestDto);

        AssetListResponse listAssetResponse = new AssetListResponse();
        listAssetResponse.setCurrentPage(1);
        listAssetResponse.setTotalNumberOfPages(1);
        listAssetResponse.getAssetList().addAll(assetDtoList);

        return Response.ok(listAssetResponse).build();
    }

    private UUID getId(SearchBranch query) {
        StringBuilder seed = new StringBuilder();
        for (AssetSearchInterface field : query.getFields()) {
            if (field instanceof SearchLeaf) {
                SearchLeaf leaf = (SearchLeaf) field;
                seed.append(leaf.getSearchValue());
            }
        }
        return UUID.nameUUIDFromBytes(seed.toString().getBytes());
    }

    private String getLeafValue(SearchBranch query, SearchFields fieldOfWantedValue) {
        for (AssetSearchInterface field : query.getFields()) {
            if (field instanceof SearchLeaf) {
                SearchLeaf leaf = (SearchLeaf) field;
                if (leaf.getSearchField() == fieldOfWantedValue) {
                    return leaf.getSearchValue();
                }
            }
        }

        return null;
    }

    private String getName(SearchBranch query) {
        for (AssetSearchInterface field : query.getFields()) {
            if (field instanceof SearchLeaf) {
                SearchLeaf leaf = (SearchLeaf) field;
                if (leaf.getSearchValue() != null) {
                    return "The so called " + leaf.getSearchValue();
                }
            }
        }

        return "Generic Boat Name";
    }

    /**
     * This allows us to deserialize a SearchBranch. This is however a very specialized and minimal
     * implementation that assumes that we get a SearchBranch that only has SearchLeaf instances
     * in its "fields" list.
     */
    private static class SearchBranchDeserializer implements JsonbDeserializer<SearchBranch> {

        @Override
        public SearchBranch deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            JsonObject object = parser.getObject();

            SearchBranch result = new SearchBranch();
            result.setLogicalAnd(object.getBoolean("logicalAnd", true));
            JsonArray fields = object.getJsonArray("fields");
            for (JsonValue jsonValue : fields) {
                JsonObject jsonObject = jsonValue.asJsonObject();
                SearchFields key = SearchFields.valueOf(jsonObject.getJsonString("searchField").getString());
                String value = jsonObject.getJsonString("searchValue").getString();
                result.getFields().add(new SearchLeaf(key, value));
            }

            return result;
        }
    }
}
