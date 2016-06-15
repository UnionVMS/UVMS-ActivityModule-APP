package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FishingGear;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper
public abstract class FishingGearMapper extends BaseMapper {

    public static FishingGearMapper INSTANCE = Mappers.getMapper(FishingGearMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(fishingGear.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(fishingGear.getTypeCode()))"),
            @Mapping(target = "roleCode", expression = "java(getCodeTypeFromList(fishingGear.getRoleCodes()))"),
            @Mapping(target = "roleCodeListId", expression = "java(getCodeTypeListIdFromList(fishingGear.getRoleCodes()))")
    })
    public abstract FishingGearEntity mapToFishingGearEntity(FishingGear fishingGear);
}
