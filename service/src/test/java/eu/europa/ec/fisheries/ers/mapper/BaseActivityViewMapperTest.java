/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.mapper;

import com.google.common.collect.Ordering;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearRoleEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearCharacteristicEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationSchemeId;
import eu.europa.ec.fisheries.ers.service.dto.view.AreaDto;
import eu.europa.ec.fisheries.ers.service.dto.view.GearDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.BaseActivityViewMapper;
import eu.europa.ec.fisheries.ers.service.mapper.view.base.ViewConstants;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link BaseActivityViewMapper}.
 */
public class BaseActivityViewMapperTest {

    /**
     * A dummy implementation for testing.
     */
    static class DummyActivityViewMapper extends BaseActivityViewMapper {
        @Override
        public FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity) {
            return null;
        }
    }


    @Test
    public void testGetAreas() {

        FishingActivityEntity entity = new FishingActivityEntity();

        FluxLocationEntity fluxLocationEntity = new FluxLocationEntity();
        fluxLocationEntity.setFluxLocationIdentifier("id");
        fluxLocationEntity.setFluxLocationIdentifierSchemeId("schemeId");
        fluxLocationEntity.setTypeCode("AREA");

        entity.setFluxLocations(Collections.singleton(fluxLocationEntity));

        AreaDto areas = BaseActivityViewMapper.getAreas(entity);

        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("id", "id");
        stringMap.put("schemeId", "schemeId");

        assertEquals(stringMap, areas.getIdentifiers());
    }

    @Test
    public void testFluxLocationDTOSchemeIdComparator() {
        Ordering<FluxLocationSchemeId> fluxLocationSchemeIdOrdering = Ordering.explicit(FluxLocationSchemeId.TERRITORY, FluxLocationSchemeId.MANAGEMENT_AREA,
                FluxLocationSchemeId.EFFORT_ZONE, FluxLocationSchemeId.FAO_AREA, FluxLocationSchemeId.GFCM_GSA, FluxLocationSchemeId.ICES_STAT_RECTANGLE).nullsLast();

        Set<FluxLocationSchemeId> fluxLocationSchemeIds = new TreeSet<FluxLocationSchemeId>(fluxLocationSchemeIdOrdering);
        fluxLocationSchemeIds.addAll(Arrays.asList(FluxLocationSchemeId.FAO_AREA, FluxLocationSchemeId.TERRITORY,
                FluxLocationSchemeId.MANAGEMENT_AREA, FluxLocationSchemeId.GFCM_GSA, null, FluxLocationSchemeId.EFFORT_ZONE, FluxLocationSchemeId.ICES_STAT_RECTANGLE));

        Iterator<FluxLocationSchemeId> fluxLocationSchemeIdIterator = fluxLocationSchemeIds.iterator();

        assertTrue(fluxLocationSchemeIdIterator.next().equals(FluxLocationSchemeId.TERRITORY));
        assertTrue(fluxLocationSchemeIdIterator.next().equals(FluxLocationSchemeId.MANAGEMENT_AREA));
        assertTrue(fluxLocationSchemeIdIterator.next().equals(FluxLocationSchemeId.EFFORT_ZONE));
        assertTrue(fluxLocationSchemeIdIterator.next().equals(FluxLocationSchemeId.FAO_AREA));
        assertTrue(fluxLocationSchemeIdIterator.next().equals(FluxLocationSchemeId.GFCM_GSA));
        assertTrue(fluxLocationSchemeIdIterator.next().equals(FluxLocationSchemeId.ICES_STAT_RECTANGLE));
        assertTrue(fluxLocationSchemeIdIterator.next() == null);
    }

    @Test
    public void testGetGearsFromEntity() {
        Set<FishingGearEntity> fishingGearEntities = new HashSet<>();
        FishingGearEntity fishingGearEntity0 = addFishingGearEntity(fishingGearEntities, "TC", "RC");
        addGearCharacteristic(fishingGearEntity0, ViewConstants.GEAR_CHARAC_TYPE_CODE_ME, 10.1, "U1");
        addGearCharacteristic(fishingGearEntity0, ViewConstants.GEAR_CHARAC_TYPE_CODE_GM, 20.2, "U2");
        addGearCharacteristic(fishingGearEntity0, ViewConstants.GEAR_CHARAC_TYPE_CODE_GN, 30.3, "U3");

        BaseActivityViewMapper sut = new DummyActivityViewMapper();
        List<GearDto> result = sut.getGearsFromEntity(fishingGearEntities);

        assertEquals(1, result.size());
        GearDto result0 = result.get(0);
        assertEquals("TC", result0.getType());
        assertEquals("RC", result0.getRole());
        assertEquals("10.1U1", result0.getMeshSize());
        assertEquals("20.2U2", result0.getLengthWidth());
        assertEquals(Integer.valueOf(30), result0.getNumberOfGears());
    }

    /**
     * Regression guard for UNIONVMS-4199.
     */
    @Test
    public void testGetGearsFromEntityForNullNumberOfGears() {
        Set<FishingGearEntity> fishingGearEntities = new HashSet<>();
        FishingGearEntity fishingGearEntity0 = addFishingGearEntity(fishingGearEntities, "TC", "RC");
        addGearCharacteristic(fishingGearEntity0, ViewConstants.GEAR_CHARAC_TYPE_CODE_GN, null, null);

        BaseActivityViewMapper sut = new DummyActivityViewMapper();
        GearDto result = sut.getGearsFromEntity(fishingGearEntities).get(0);

        assertEquals(Integer.valueOf(0), result.getNumberOfGears());
    }

    private FishingGearEntity addFishingGearEntity(Set<FishingGearEntity> fishingGearEntities, String typeCode, String fishingGearRoleCode) {
        FishingGearEntity result = new FishingGearEntity();
        result.setTypeCode(typeCode);
        result.setFishingGearRole(Optional.ofNullable(fishingGearRoleCode)
                .map(code -> {
                    FishingGearRoleEntity role = new FishingGearRoleEntity();
                    role.setRoleCode(fishingGearRoleCode);
                    return role;
                })
                .map(Collections::singleton)
                .orElse(Collections.emptySet())
        );
        result.setGearCharacteristics(new HashSet<>());
        fishingGearEntities.add(result);
        return result;
    }

    private void addGearCharacteristic(FishingGearEntity fishingGearEntity, String typeCode, Double valueMeasure, String unitCode) {
        GearCharacteristicEntity gch = new GearCharacteristicEntity();
        gch.setTypeCode(typeCode);
        gch.setValueMeasure(valueMeasure);
        gch.setValueMeasureUnitCode(unitCode);
        fishingGearEntity.getGearCharacteristics().add(gch);
    }
}
