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
public abstract class FaCatchMapper {

    public static FaCatchMapper INSTANCE = Mappers.getMapper(FaCatchMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(faCatch.getTypeCode().getValue())"),
            @Mapping(target = "typeCodeListId", expression = "java(faCatch.getTypeCode().getListID())"),
            @Mapping(target = "speciesCode", expression = "java(faCatch.getSpeciesCode().getValue())"),
            @Mapping(target = "speciesCodeListid", expression = "java(faCatch.getSpeciesCode().getListID())"),
            @Mapping(target = "unitQuantity", expression = "java(faCatch.getUnitQuantity().getValue().longValue())"),
            @Mapping(target = "weightMeasure", expression = "java(faCatch.getWeightMeasure().getValue().doubleValue())"),
            @Mapping(target = "weightMeasureUnitCode", expression = "java(faCatch.getWeightMeasure().getUnitCode())"),
            @Mapping(target = "weightMeasureListId", expression = "java(faCatch.getWeightMeasure().getUnitCodeListVersionID())"),
            @Mapping(target = "usageCode", expression = "java(faCatch.getUsageCode().getValue())"),
            @Mapping(target = "usageCodeListId", expression = "java(faCatch.getUsageCode().getListID())"),
            @Mapping(target = "weighingMeansCode", expression = "java(faCatch.getWeighingMeansCode().getValue())"),
            @Mapping(target = "weighingMeansCodeListId", expression = "java(faCatch.getWeighingMeansCode().getListID())")
    })
    public abstract FaCatchEntity mapToFaCatchEntity(FACatch faCatch);
}
