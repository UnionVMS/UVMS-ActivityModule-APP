package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripIdentifierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FishingTrip;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.IDType;


import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 6/15/2016.
 */
@Mapper
public abstract class FishingTripMapper extends BaseMapper {

    public static FishingTripMapper INSTANCE = Mappers.getMapper(FishingTripMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(fishingTrip.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(fishingTrip.getTypeCode()))"),
            @Mapping(target = "fishingTripIdentifiers", expression = "java(mapToFishingTripIdentifierEntities(fishingTrip.getIDS()))")
    })
    public abstract FishingTripEntity mapToFishingTripEntity(FishingTrip fishingTrip);

    public abstract Set<FishingTripIdentifierEntity> mapToFishingTripIdentifierEntities(List<IDType> idTypes);

    @Mappings({
            @Mapping(target = "tripId", expression = "java(getIdType(idType))"),
            @Mapping(target = "tripSchemeId", expression = "java(getIdTypeSchemaId(idType))")
    })
    public abstract FishingTripIdentifierEntity mapToFishingTripIdentifierEntity(IDType idType);
}
