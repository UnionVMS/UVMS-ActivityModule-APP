/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.mapper.view;

import eu.europa.ec.fisheries.ers.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.ers.service.dto.facatch.FluxCharacteristicsViewDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

/**
 * Created by kovian on 06/03/2017.
 */
@Mapper
public interface FluxCharacteristicsViewDtoMapper {

    FluxCharacteristicsViewDtoMapper INSTANCE = Mappers.getMapper(FluxCharacteristicsViewDtoMapper.class);

    FluxCharacteristicsViewDto mapFluxCharacteristicsEntityListToDtoList(FluxCharacteristicEntity firstCatchEntity);

    List<FluxCharacteristicsViewDto> mapFluxCharacteristicsList(Set<FluxCharacteristicEntity> fluxCharacteristicEntities);
}
