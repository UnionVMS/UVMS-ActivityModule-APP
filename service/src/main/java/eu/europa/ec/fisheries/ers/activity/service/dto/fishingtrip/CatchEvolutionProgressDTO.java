package eu.europa.ec.fisheries.ers.activity.service.dto.fishingtrip;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CatchEvolutionProgressDTO {
    private Map<String, CatchSummaryListDTO> catchEvolution;
    @JsonProperty("activityType")
    private String activityType;
    @JsonProperty
    private String reportType;
    @JsonProperty
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
