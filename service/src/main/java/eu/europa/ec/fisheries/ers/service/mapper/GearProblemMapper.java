package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearProblemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FishingGear;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.GearProblem;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.QuantityType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            @Mapping(target = "recoveryMeasureCodeListId", expression = "java(getCodeTypeListIdFromList(gearProblem.getRecoveryMeasureCodes()))"),
            @Mapping(target = "fishingActivity", expression = "java(fishingActivityEntity)"),
            @Mapping(target = "fishingGears", expression = "java(getFishingGearsEntities(gearProblem.getRelatedFishingGears(), gearProblemEntity))")
    })
    public abstract GearProblemEntity mapToGearProblemEntity(GearProblem gearProblem, FishingActivityEntity fishingActivityEntity, @MappingTarget GearProblemEntity gearProblemEntity);

    protected Set<FishingGearEntity> getFishingGearsEntities(List<FishingGear> fishingGears, GearProblemEntity gearProblemEntity) {
        if (fishingGears == null || fishingGears.isEmpty()) {
            return null;
        }
        Set<FishingGearEntity> fishingGearEntities = new HashSet<>();
        for (FishingGear fishingGear : fishingGears) {
            FishingGearEntity fishingGearEntity = FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear, gearProblemEntity, new FishingGearEntity());
            fishingGearEntities.add(fishingGearEntity);
        }
        return fishingGearEntities;
    }

    protected Integer getAffectedQuantity(QuantityType quantityType) {
        Double qty = super.getQuantity(quantityType);
        if (qty != null) {
            return qty.intValue();
        } else {
            return null;
        }
    }
}
