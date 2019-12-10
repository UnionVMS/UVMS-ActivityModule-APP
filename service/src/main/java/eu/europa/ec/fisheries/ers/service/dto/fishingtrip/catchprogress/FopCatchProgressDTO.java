package eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress;

import java.util.Date;

public final class FopCatchProgressDTO extends SimpleCatchProgressDTO {

    public static  final String ACTIVITY_TYPE = "FISHING_OPERATION";

    public FopCatchProgressDTO(String reportType, boolean affectsCumulative, Date date) {
        super(ACTIVITY_TYPE, reportType, affectsCumulative, date);
    }
}
