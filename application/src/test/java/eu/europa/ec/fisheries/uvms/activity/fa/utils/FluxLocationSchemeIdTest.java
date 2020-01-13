package eu.europa.ec.fisheries.uvms.activity.fa.utils;

import com.google.common.collect.Ordering;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FluxLocationSchemeIdTest {
    @Test
    public void testFluxLocationDTOSchemeIdComparator() {
        Ordering<FluxLocationSchemeId> fluxLocationSchemeIdOrdering = Ordering.explicit(FluxLocationSchemeId.TERRITORY, FluxLocationSchemeId.MANAGEMENT_AREA,
                FluxLocationSchemeId.EFFORT_ZONE, FluxLocationSchemeId.FAO_AREA, FluxLocationSchemeId.GFCM_GSA, FluxLocationSchemeId.ICES_STAT_RECTANGLE).nullsLast();

        Set<FluxLocationSchemeId> fluxLocationSchemeIds = new TreeSet<FluxLocationSchemeId>(fluxLocationSchemeIdOrdering);
        fluxLocationSchemeIds.addAll(Arrays.asList(FluxLocationSchemeId.FAO_AREA, FluxLocationSchemeId.TERRITORY,
                FluxLocationSchemeId.MANAGEMENT_AREA, FluxLocationSchemeId.GFCM_GSA, null, FluxLocationSchemeId.EFFORT_ZONE, FluxLocationSchemeId.ICES_STAT_RECTANGLE));

        Iterator<FluxLocationSchemeId> fluxLocationSchemeIdIterator = fluxLocationSchemeIds.iterator();

        assertEquals(FluxLocationSchemeId.TERRITORY, fluxLocationSchemeIdIterator.next());
        assertEquals(FluxLocationSchemeId.MANAGEMENT_AREA, fluxLocationSchemeIdIterator.next());
        assertEquals(FluxLocationSchemeId.EFFORT_ZONE, fluxLocationSchemeIdIterator.next());
        assertEquals(FluxLocationSchemeId.FAO_AREA, fluxLocationSchemeIdIterator.next());
        assertEquals(FluxLocationSchemeId.GFCM_GSA, fluxLocationSchemeIdIterator.next());
        assertEquals(FluxLocationSchemeId.ICES_STAT_RECTANGLE, fluxLocationSchemeIdIterator.next());
        assertNull(fluxLocationSchemeIdIterator.next());
    }
}
