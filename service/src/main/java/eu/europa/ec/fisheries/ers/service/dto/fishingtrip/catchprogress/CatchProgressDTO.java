package eu.europa.ec.fisheries.ers.service.dto.fishingtrip.catchprogress;

import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;

import java.util.Date;
import java.util.function.BiFunction;

public abstract class CatchProgressDTO {

    protected static final BiFunction<Double, Double, Double> ADD = (oldValue, newValue) -> oldValue + newValue;

    private String activityType;
    private String reportType;
    private boolean affectsCumulative;
    private String date;
    private int orderId;

    public CatchProgressDTO(String activityType, String reportType, boolean affectsCumulative, Date date) {
        this.activityType = activityType;
        this.reportType = reportType;
        this.affectsCumulative = affectsCumulative;
        this.date = DateUtils.dateToString(date);
    }

    public String getActivityType() {
        return activityType;
    }

    public String getReportType() {
        return reportType;
    }

    public boolean isAffectsCumulative() {
        return affectsCumulative;
    }

    public String getDate() {
        return date;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
