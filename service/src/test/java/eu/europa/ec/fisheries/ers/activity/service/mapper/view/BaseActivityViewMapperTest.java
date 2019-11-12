/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.activity.service.mapper.view;

import com.google.common.collect.Ordering;
import eu.europa.ec.fisheries.ers.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.activity.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.activity.fa.utils.FluxLocationSchemeId;
import eu.europa.ec.fisheries.ers.activity.service.dto.view.AreaDto;
import eu.europa.ec.fisheries.ers.activity.service.mapper.view.base.BaseActivityViewMapper;
import org.fest.util.Collections;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Gregory Rinaldi
 */
public class BaseActivityViewMapperTest {

    @Test
    public void testGetAreas() {

        FishingActivityEntity entity = new FishingActivityEntity();

        FluxLocationEntity fluxLocationEntity = new FluxLocationEntity();
        fluxLocationEntity.setFluxLocationIdentifier("id");
        fluxLocationEntity.setFluxLocationIdentifierSchemeId("schemeId");
        fluxLocationEntity.setTypeCode("AREA");

        entity.setFluxLocations(Collections.set(fluxLocationEntity));

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
}
