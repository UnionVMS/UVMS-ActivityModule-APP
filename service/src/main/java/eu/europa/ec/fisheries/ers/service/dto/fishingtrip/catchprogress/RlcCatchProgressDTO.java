package eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress;

import java.util.Date;

public final class RlcCatchProgressDTO extends SimpleCatchProgressDTO {

    public static  final String ACTIVITY_TYPE = "RELOCATION";

    public RlcCatchProgressDTO(String reportType, boolean affectsCumulative, Date date) {
        super(ACTIVITY_TYPE, reportType, affectsCumulative, date);
    }
}
