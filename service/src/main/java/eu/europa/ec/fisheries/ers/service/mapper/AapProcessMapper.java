package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.AapProcessEntity;
import eu.europa.ec.fisheries.ers.fa.entities.AapProductEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.AAPProcess;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.AAPProduct;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper
public abstract class AapProcessMapper extends BaseMapper {

    public static AapProcessMapper INSTANCE = Mappers.getMapper(AapProcessMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeTypeFromList(aapProcess.getTypeCodes()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListIdFromList(aapProcess.getTypeCodes()))"),
            @Mapping(target = "conversionFactor", expression = "java(getNumericInteger(aapProcess.getConversionFactorNumeric()))"),
            @Mapping(target = "faCatch", expression = "java(faCatchEntity)"),
            @Mapping(target = "aapProducts", expression = "java(getAapProductEntities(aapProcess.getResultAAPProducts(), aapProcessEntity))")
    })
    public abstract AapProcessEntity mapToAapProcessEntity(AAPProcess aapProcess, FaCatchEntity faCatchEntity, @MappingTarget AapProcessEntity aapProcessEntity);

    protected Set<AapProductEntity> getAapProductEntities(List<AAPProduct> aapProducts, AapProcessEntity aapProcessEntity) {
        if (aapProducts == null || aapProducts.isEmpty()) {
            return null;
        }
        Set<AapProductEntity> aapProductEntities = new HashSet<>();
        for (AAPProduct aapProduct : aapProducts) {
            aapProductEntities.add(AapProductMapper.INSTANCE.mapToAapProductEntity(aapProduct, aapProcessEntity, new AapProductEntity()));
        }
        return aapProductEntities;
    }
}
