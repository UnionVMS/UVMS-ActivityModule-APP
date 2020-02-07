/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.GearCharacteristicEntity;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.GearDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class GearCharacteristicsMapperTest {

    @Parameter
    public GearCharacteristicEntity entity;

    @Parameter(1)
    public String typeCode;

    @Parameter(2)
    public GearDto expectedDto;

    @Test
    public void testMapGearDtoToFishingGearEntityWithTypeCodeDG() {
        entity.setTypeCode(typeCode);
        FishingGearEntity fishingGearEntity = new FishingGearEntity();
        Set<GearCharacteristicEntity> gearCharacteristicEntities = new HashSet<>();
        gearCharacteristicEntities.add(entity);
        fishingGearEntity.setGearCharacteristics(gearCharacteristicEntities);
        GearCharacteristicsMapper gearCharacteristicsMapper = Mappers.getMapper(GearCharacteristicsMapper.class);
        GearDto mappedDto = gearCharacteristicsMapper.mapGearDtoToFishingGearEntity(fishingGearEntity);
        assertEquals(expectedDto, mappedDto);
    }

    @Parameters
    public static Object[] gearEntities() {
        GearCharacteristicEntity entity = new GearCharacteristicEntity().
                toBuilder(). // weird way of creating the builder, but otherwise Lombok removes field initializers
                        valueMeasure(20.25).
                        valueMeasureUnitCode("kg").
                        typeCode(GearCharacteristicConstants.GEAR_CHARAC_TYPE_CODE_GD).
                        description("Trawls & Seines")
                .build();

        return new Object[][] {
                {entity, GearCharacteristicConstants.GEAR_CHARAC_TYPE_CODE_GD, GearDto.builder().description("Trawls & Seines").build()},
                {entity, GearCharacteristicConstants.GEAR_CHARAC_TYPE_CODE_ME, GearDto.builder().meshSize("20.25kg").build()}
        };
    }
}
