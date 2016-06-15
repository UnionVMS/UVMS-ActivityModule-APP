package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FACatch;

/**
 * Created by padhyad on 5/17/2016.
 */
@Mapper
public abstract class FaCatchMapper extends BaseMapper {

    public static FaCatchMapper INSTANCE = Mappers.getMapper(FaCatchMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(faCatch.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(faCatch.getTypeCode()))"),
            @Mapping(target = "speciesCode", expression = "java(getCodeType(faCatch.getSpeciesCode()))"),
            @Mapping(target = "speciesCodeListid", expression = "java(getCodeTypeListId(faCatch.getSpeciesCode()))"),
            @Mapping(target = "unitQuantity", expression = "java(getQuantityInLong(faCatch.getUnitQuantity()))"),
            @Mapping(target = "weightMeasure", expression = "java(getMeasure(faCatch.getWeightMeasure()))"),
            @Mapping(target = "weightMeasureUnitCode", expression = "java(getMeasureUnitCode(faCatch.getWeightMeasure()))"),
            @Mapping(target = "weightMeasureListId", expression = "java(getMeasureListId(faCatch.getWeightMeasure()))"),
            @Mapping(target = "usageCode", expression = "java(getCodeType(faCatch.getUsageCode()))"),
            @Mapping(target = "usageCodeListId", expression = "java(getCodeTypeListId(faCatch.getUsageCode()))"),
            @Mapping(target = "weighingMeansCode", expression = "java(getCodeType(faCatch.getWeighingMeansCode()))"),
            @Mapping(target = "weighingMeansCodeListId", expression = "java(getCodeTypeListId(faCatch.getWeighingMeansCode()))")
    })
    public abstract FaCatchEntity mapToFaCatchEntity(FACatch faCatch);
}
