package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.VesselStorageCharacteristicsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
            @Mapping(target = "vesselTypeCodeListId", expression = "java(getCodeTypeListIdFromList(vesselStorageCharacteristic.getTypeCodes()))")
    })
    public abstract VesselStorageCharacteristicsEntity mapToVesselStorageCharEntity(VesselStorageCharacteristic vesselStorageCharacteristic);
}
