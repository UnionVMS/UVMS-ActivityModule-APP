package eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress;

import java.util.Date;

public final class TraCatchProgressDTO extends DetailedCatchProgressDTO {

    public static  final String ACTIVITY_TYPE = "TRANSHIPMENT";

    public TraCatchProgressDTO(String reportType, boolean affectsCumulative, Date date) {
        super(ACTIVITY_TYPE, reportType, affectsCumulative, date);
    }
}
