package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import eu.europa.ec.fisheries.uvms.activity.fa.utils.UnitCodeEnum;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.PrePersist;
import java.math.BigDecimal;
import java.time.Instant;

@Embeddable
public class DelimitedPeriodEmbeddable {

    @Column(name = "start_date")
    private Instant startDate;
    @Column(name = "end_date")
    private Instant endDate;
    @Column(name = "calculated_duration")
    private double calculatedDuration;

    @Embedded
    private MeasureType durationMeasure = new MeasureType();

    @PrePersist
    public void prePersist(){
        if (durationMeasure != null && durationMeasure.getUnitCode() != null && durationMeasure.getValue() != null) {
            UnitCodeEnum unitCodeEnum = UnitCodeEnum.getUnitCode(durationMeasure.getUnitCode());
            if (unitCodeEnum != null) {
                BigDecimal measuredValue = BigDecimal.valueOf(durationMeasure.getValue());
                BigDecimal result = measuredValue.multiply(new BigDecimal(unitCodeEnum.getConversionFactor()));
                calculatedDuration = result.doubleValue();
            }
        }
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public double getCalculatedDuration() {
        return calculatedDuration;
    }

    public void setCalculatedDuration(double calculatedDuration) {
        this.calculatedDuration = calculatedDuration;
    }

    public MeasureType getDurationMeasure() {
        return durationMeasure;
    }

    public void setDurationMeasure(MeasureType durationMeasure) {
        this.durationMeasure = durationMeasure;
    }
}
