package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.GearProblemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.GearProblem;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.QuantityType;

import java.util.List;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper
public abstract class GearProblemMapper extends BaseMapper {

    public static GearProblemMapper INSTANCE = Mappers.getMapper(GearProblemMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(gearProblem.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(gearProblem.getTypeCode()))"),
            @Mapping(target = "affectedQuantity", expression = "java(getAffectedQuantity(gearProblem.getAffectedQuantity()))"),
            @Mapping(target = "recoveryMeasureCode", expression = "java(getCodeTypeFromList(gearProblem.getRecoveryMeasureCodes()))"),
            @Mapping(target = "recoveryMeasureCodeListId", expression = "java(getCodeTypeListIdFromList(gearProblem.getRecoveryMeasureCodes()))")
    })
    public abstract GearProblemEntity mapToGearProblemEntity(GearProblem gearProblem);

    protected Integer getAffectedQuantity(QuantityType quantityType) {
        Double qty = super.getQuantity(quantityType);
        if (qty != null) {
            return qty.intValue();
        } else {
            return null;
        }
    }
}
