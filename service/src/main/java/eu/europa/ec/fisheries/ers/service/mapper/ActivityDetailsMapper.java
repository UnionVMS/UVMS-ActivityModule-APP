package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.dto.view.ActivityDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN, uses = FluxCharacteristicsMapper.class)
public interface ActivityDetailsMapper {

    ActivityDetailsMapper INSTANCE = Mappers.getMapper(ActivityDetailsMapper.class);

    @Mappings ({
        @Mapping(target = "type" , source = "typeCode"),
        @Mapping(target = "occurrence" , source = "occurence"),
        @Mapping(target = "characteristics" , source = "fluxCharacteristics")
    })
    ActivityDetailsDto mapFishingActivityEntityToActivityDetailsDto(FishingActivityEntity entity);
}
