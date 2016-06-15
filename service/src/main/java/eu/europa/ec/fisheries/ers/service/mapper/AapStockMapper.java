package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.AapStockEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.AAPStock;

/**
 * Created by padhyad on 6/15/2016.
 */
@Mapper
public abstract class AapStockMapper extends BaseMapper {

    public static AapStockMapper INSTANCE = Mappers.getMapper(AapStockMapper.class);

    @Mappings({
            @Mapping(target = "stockId", expression = "java(getIdType(aapStock.getID()))"),
            @Mapping(target = "stockSchemeId", expression = "java(getIdTypeSchemaId(aapStock.getID()))")
    })
    public abstract AapStockEntity mapToAapStockEntity(AAPStock aapStock);
}
