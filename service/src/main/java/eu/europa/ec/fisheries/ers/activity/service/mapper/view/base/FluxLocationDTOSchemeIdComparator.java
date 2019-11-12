package eu.europa.ec.fisheries.ers.activity.service.mapper.view.base;

import com.google.common.collect.Ordering;
import eu.europa.ec.fisheries.ers.activity.fa.utils.FluxLocationSchemeId;
import eu.europa.ec.fisheries.ers.activity.service.dto.view.FluxLocationDto;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

public class FluxLocationDTOSchemeIdComparator implements Comparator<FluxLocationDto> {
    private Ordering<FluxLocationSchemeId> fluxLocationSchemeIdOrdering = Ordering.explicit(FluxLocationSchemeId.TERRITORY, FluxLocationSchemeId.MANAGEMENT_AREA,
            FluxLocationSchemeId.EFFORT_ZONE, FluxLocationSchemeId.FAO_AREA, FluxLocationSchemeId.GFCM_GSA, FluxLocationSchemeId.ICES_STAT_RECTANGLE).nullsLast();

    @Override
    public int compare(FluxLocationDto o1, FluxLocationDto o2) {
        FluxLocationSchemeId fluxLocationSchemeIdO1 = EnumUtils.getEnum(FluxLocationSchemeId.class, o1.getFluxLocationIdentifierSchemeId());
        FluxLocationSchemeId fluxLocationSchemeIdO2 = EnumUtils.getEnum(FluxLocationSchemeId.class, o2.getFluxLocationIdentifierSchemeId());

        int fluxLocationSchemeIdOrderingCompareResult = fluxLocationSchemeIdOrdering.compare(fluxLocationSchemeIdO1, fluxLocationSchemeIdO2);

        if (fluxLocationSchemeIdOrderingCompareResult == 0) {
            return StringUtils.compareIgnoreCase(o1.getFluxLocationIdentifier(), o2.getFluxLocationIdentifier());
        } else {
            return fluxLocationSchemeIdOrderingCompareResult;
        }
    }
}
