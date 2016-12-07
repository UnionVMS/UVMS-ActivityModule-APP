package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.service.search.FishingTripId;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * Created by sanera on 02/12/2016.
 */
@Mapper
public abstract class FishingTripIdWithGeometryMapper extends BaseMapper  {
    public static final FishingTripIdWithGeometryMapper INSTANCE = Mappers.getMapper(FishingTripIdWithGeometryMapper.class);

    @Mappings({
            @Mapping(target = "tripId", expression = "java(dto.getTripId())"),
            @Mapping(target = "schemeId", expression = "java(dto.getSchemeID())"),
            @Mapping(target = "geometry", expression = "java(geometry)")
    })
    public abstract FishingTripIdWithGeometry mapToFishingTripIdWithGeometry(FishingTripId dto, String geometry);

}
