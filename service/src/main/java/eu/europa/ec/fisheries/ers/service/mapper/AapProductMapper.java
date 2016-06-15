package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.AapProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.AAPProduct;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper
public abstract class AapProductMapper extends BaseMapper {

    public static AapProductMapper INSTANCE = Mappers.getMapper(AapProductMapper.class);

    @Mappings({
            @Mapping(target = "packagingTypeCode", expression = "java(getCodeType(aapProduct.getPackagingTypeCode()))"),
            @Mapping(target = "packagingTypeCodeListId", expression = "java(getCodeTypeListId(aapProduct.getPackagingTypeCode()))"),
            @Mapping(target = "packagingUnitAvarageWeight", expression = "java(getMeasure(aapProduct.getPackagingUnitAverageWeightMeasure()))"),
            @Mapping(target = "packagingUnitCount", expression = "java(getQuantity(aapProduct.getPackagingUnitQuantity()))")
    })
    public abstract AapProductEntity mapToAapProductEntity(AAPProduct aapProduct);
}
