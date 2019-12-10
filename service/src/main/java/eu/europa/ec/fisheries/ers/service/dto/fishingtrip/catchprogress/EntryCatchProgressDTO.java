package eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress;

import java.util.Date;

public final class EntryCatchProgressDTO extends SimpleCatchProgressDTO {

    public static  final String ACTIVITY_TYPE = "AREA_ENTRY";

    public EntryCatchProgressDTO(String reportType, boolean affectsCumulative, Date date) {
        super(ACTIVITY_TYPE,reportType, affectsCumulative, date);
    }
}
