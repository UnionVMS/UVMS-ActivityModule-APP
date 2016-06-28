package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.SizeDistributionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.SizeDistribution;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.CodeType;

import java.util.List;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper
public abstract class SizeDistributionMapper extends BaseMapper {

    public static SizeDistributionMapper INSTANCE = Mappers.getMapper(SizeDistributionMapper.class);

    @Mappings({
            @Mapping(target = "classCode", expression = "java(getCodeTypeFromList(sizeDistribution.getClassCodes()))"),
            @Mapping(target = "classCodeListId", expression = "java(getCodeTypeListIdFromList(sizeDistribution.getClassCodes()))"),
            @Mapping(target = "categoryCode", expression = "java(getCodeType(sizeDistribution.getCategoryCode()))"),
            @Mapping(target = "categoryCodeListId", expression = "java(getCodeTypeListId(sizeDistribution.getCategoryCode()))"),
            @Mapping(target = "faCatch", expression = "java(faCatchEntity)")
    })
    public abstract SizeDistributionEntity mapToSizeDistributionEntity(SizeDistribution sizeDistribution, FaCatchEntity faCatchEntity, @MappingTarget SizeDistributionEntity sizeDistributionEntity);
}
