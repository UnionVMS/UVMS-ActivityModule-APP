package eu.europa.ec.fisheries.uvms.activity.fa.utils;

import com.google.common.collect.Ordering;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class FluxLocationSchemeIdTest {
    @Test
    public void testFluxLocationDTOSchemeIdComparator() {
        Ordering<FluxLocationSchemeId> fluxLocationSchemeIdOrdering = Ordering.explicit(FluxLocationSchemeId.TERRITORY, FluxLocationSchemeId.MANAGEMENT_AREA,
                FluxLocationSchemeId.EFFORT_ZONE, FluxLocationSchemeId.FAO_AREA, FluxLocationSchemeId.GFCM_GSA, FluxLocationSchemeId.ICES_STAT_RECTANGLE).nullsLast();

        Set<FluxLocationSchemeId> fluxLocationSchemeIds = new TreeSet<FluxLocationSchemeId>(fluxLocationSchemeIdOrdering);
        fluxLocationSchemeIds.addAll(Arrays.asList(FluxLocationSchemeId.FAO_AREA, FluxLocationSchemeId.TERRITORY,
                FluxLocationSchemeId.MANAGEMENT_AREA, FluxLocationSchemeId.GFCM_GSA, null, FluxLocationSchemeId.EFFORT_ZONE, FluxLocationSchemeId.ICES_STAT_RECTANGLE));

        Iterator<FluxLocationSchemeId> fluxLocationSchemeIdIterator = fluxLocationSchemeIds.iterator();

        assertEquals(fluxLocationSchemeIdIterator.next(), FluxLocationSchemeId.TERRITORY);
        assertEquals(fluxLocationSchemeIdIterator.next(), FluxLocationSchemeId.MANAGEMENT_AREA);
        assertEquals(fluxLocationSchemeIdIterator.next(), FluxLocationSchemeId.EFFORT_ZONE);
        assertEquals(fluxLocationSchemeIdIterator.next(), FluxLocationSchemeId.FAO_AREA);
        assertEquals(fluxLocationSchemeIdIterator.next(), FluxLocationSchemeId.GFCM_GSA);
        assertEquals(fluxLocationSchemeIdIterator.next(), FluxLocationSchemeId.ICES_STAT_RECTANGLE);
        assertNull(fluxLocationSchemeIdIterator.next());
    }
}
