package eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress;

import java.util.Date;

public final class ArrCatchProgressDTO extends DetailedCatchProgressDTO {

    public static  final String ACTIVITY_TYPE = "ARRIVAL";

    public ArrCatchProgressDTO(String reportType, boolean affectsCumulative, Date date) {
        super(ACTIVITY_TYPE, reportType, affectsCumulative, date);
    }
}
