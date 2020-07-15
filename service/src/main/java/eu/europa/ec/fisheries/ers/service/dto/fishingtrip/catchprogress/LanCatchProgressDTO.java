package eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress;

import java.util.Date;

public final class LanCatchProgressDTO extends DetailedCatchProgressDTO {

    public static  final String ACTIVITY_TYPE = "LANDING";

    public LanCatchProgressDTO(String reportType, boolean affectsCumulative, Date date) {
        super(ACTIVITY_TYPE, reportType, affectsCumulative, date);
    }
}
