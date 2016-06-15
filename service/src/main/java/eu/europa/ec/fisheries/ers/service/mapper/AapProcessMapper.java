package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.AapProcessEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.AAPProcess;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper
public abstract class AapProcessMapper extends BaseMapper {

    private static AapProcessMapper INSTANCE = Mappers.getMapper(AapProcessMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeTypeFromList(aapProcess.getTypeCodes()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListIdFromList(aapProcess.getTypeCodes()))"),
            @Mapping(target = "conversionFactor", expression = "java(getNumericInteger(aapProcess.getConversionFactorNumeric()))")
    })
    public abstract AapProcessEntity mapToAapProcessEntity(AAPProcess aapProcess);
}
