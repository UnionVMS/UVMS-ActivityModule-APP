package eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress;

import java.util.Date;

public final class DisCatchProgressDTO extends SimpleCatchProgressDTO {

    public static  final String ACTIVITY_TYPE = "DISCARD";

    public DisCatchProgressDTO(String reportType, boolean affectsCumulative, Date date) {
        super(ACTIVITY_TYPE, reportType, affectsCumulative, date);
    }
}
