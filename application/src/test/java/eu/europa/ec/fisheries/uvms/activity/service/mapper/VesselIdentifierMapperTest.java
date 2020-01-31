/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.service.dto.AssetIdentifierDto;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselIdentifierEntity.builder;
import static eu.europa.ec.fisheries.uvms.activity.service.mapper.VesselIdentifierMapper.INSTANCE;
import static org.junit.Assert.assertEquals;

public class VesselIdentifierMapperTest {

    @Test
    public void testMapToIdentifierDtoSet() {

        VesselIdentifierEntity entity = builder()
                .vesselIdentifierSchemeId("CFR")
                .vesselIdentifierId("schemeId")
                .build();

        Set<VesselIdentifierEntity> vesselIdentifierEntities = new HashSet<>();
        vesselIdentifierEntities.add(entity);

        Set<AssetIdentifierDto> identifierDtos = INSTANCE.mapToIdentifierDotSet(vesselIdentifierEntities);

        assertEquals(1, identifierDtos.size());
        assertEquals("schemeId", identifierDtos.iterator().next().getFaIdentifierId());
        assertEquals(VesselIdentifierSchemeIdEnum.CFR, identifierDtos.iterator().next().getIdentifierSchemeId());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapToIndentifierDtoSetWithUndefinedEnum() {

        VesselIdentifierEntity entity = builder()
                .vesselIdentifierSchemeId("UNDEFINED")
                .build();
        Set<VesselIdentifierEntity> vesselIdentifierEntities = new HashSet<>();
        vesselIdentifierEntities.add(entity);

        INSTANCE.mapToIdentifierDotSet(vesselIdentifierEntities);
    }

}
