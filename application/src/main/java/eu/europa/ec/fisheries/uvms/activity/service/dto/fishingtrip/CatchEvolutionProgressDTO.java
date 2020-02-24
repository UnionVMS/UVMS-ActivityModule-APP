package eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip;

import javax.json.bind.annotation.JsonbProperty;
import java.util.Map;

public class CatchEvolutionProgressDTO {
    private Map<String, CatchSummaryListDTO> catchEvolution;
    @JsonbProperty("activityType")
    private String activityType;
    @JsonbProperty
    private String reportType;
    @JsonbProperty
    private int orderId;

    public Map<String, CatchSummaryListDTO> getCatchEvolution() {
        return catchEvolution;
    }

    public void setCatchEvolution(Map<String, CatchSummaryListDTO> catchEvolution) {
        this.catchEvolution = catchEvolution;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
