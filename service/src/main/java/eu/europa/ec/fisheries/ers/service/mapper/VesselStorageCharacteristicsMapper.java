package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselStorageCharacteristicsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.VesselStorageCharacteristic;

/**
 * Created by padhyad on 6/13/2016.
 */
@Mapper
public abstract class VesselStorageCharacteristicsMapper extends BaseMapper {

    public static VesselStorageCharacteristicsMapper INSTANCE = Mappers.getMapper(VesselStorageCharacteristicsMapper.class);

    @Mappings({
            @Mapping(target = "vesselId", expression = "java(getIdType(vesselStorageCharacteristic.getID()))"),
            @Mapping(target = "vesselSchemaId", expression = "java(getIdTypeSchemaId(vesselStorageCharacteristic.getID()))"),
            @Mapping(target = "vesselTypeCode", expression = "java(getCodeTypeFromList(vesselStorageCharacteristic.getTypeCodes()))"),
            @Mapping(target = "vesselTypeCodeListId", expression = "java(getCodeTypeListIdFromList(vesselStorageCharacteristic.getTypeCodes()))"),
            @Mapping(target = "fishingActivitiesForDestVesselCharId", expression = "java(fishingActivityEntity)")
    })
    public abstract VesselStorageCharacteristicsEntity mapToDestVesselStorageCharEntity(VesselStorageCharacteristic vesselStorageCharacteristic, FishingActivityEntity fishingActivityEntity, @MappingTarget VesselStorageCharacteristicsEntity vesselStorageCharacteristicsEntity);

    @Mappings({
            @Mapping(target = "vesselId", expression = "java(getIdType(vesselStorageCharacteristic.getID()))"),
            @Mapping(target = "vesselSchemaId", expression = "java(getIdTypeSchemaId(vesselStorageCharacteristic.getID()))"),
            @Mapping(target = "vesselTypeCode", expression = "java(getCodeTypeFromList(vesselStorageCharacteristic.getTypeCodes()))"),
            @Mapping(target = "vesselTypeCodeListId", expression = "java(getCodeTypeListIdFromList(vesselStorageCharacteristic.getTypeCodes()))"),
            @Mapping(target = "fishingActivitiesForSourceVesselCharId", expression = "java(fishingActivityEntity)")
    })
    public abstract VesselStorageCharacteristicsEntity mapToSourceVesselStorageCharEntity(VesselStorageCharacteristic vesselStorageCharacteristic, FishingActivityEntity fishingActivityEntity, @MappingTarget VesselStorageCharacteristicsEntity vesselStorageCharacteristicsEntity);

}
