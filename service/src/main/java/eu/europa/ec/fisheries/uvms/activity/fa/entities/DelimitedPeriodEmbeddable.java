package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import eu.europa.ec.fisheries.uvms.activity.fa.utils.UnitCodeEnum;
import eu.europa.ec.fisheries.uvms.activity.service.util.CustomBigDecimal;
import eu.europa.ec.fisheries.uvms.commons.date.XMLDateUtils;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.PrePersist;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

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

    public static DelimitedPeriodEmbeddable create(DelimitedPeriod delimitedPeriod) {
        DelimitedPeriodEmbeddable delimitedPeriodEmbeddable = new DelimitedPeriodEmbeddable();

        DateTimeType startDateTime = delimitedPeriod.getStartDateTime();
        if (startDateTime != null) {
            Date date = XMLDateUtils.xmlGregorianCalendarToDate(startDateTime.getDateTime());
            delimitedPeriodEmbeddable.setStartDate(date.toInstant());
        }

        DateTimeType endDateTime = delimitedPeriod.getEndDateTime();
        if (endDateTime != null) {
            Date date = XMLDateUtils.xmlGregorianCalendarToDate(endDateTime.getDateTime());
            delimitedPeriodEmbeddable.setEndDate(date.toInstant());
        }

        un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType inputDurationMeasure = delimitedPeriod.getDurationMeasure();
        if (inputDurationMeasure != null) {
            MeasureType measureType = new MeasureType();

            BigDecimal value = inputDurationMeasure.getValue();
            if (value != null) {
                measureType.setValue(value.doubleValue());
            }
            measureType.setUnitCode(inputDurationMeasure.getUnitCode());
            measureType.setUnitCodeListVersionID(inputDurationMeasure.getUnitCodeListVersionID());

            delimitedPeriodEmbeddable.setDurationMeasure(measureType);
        }

        return delimitedPeriodEmbeddable;
    }

    public DelimitedPeriod convert() {
        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();

        if (startDate != null) {
            XMLGregorianCalendar xmlGregorianCalendar = XMLDateUtils.dateToXmlGregorian(Date.from(startDate));
            DateTimeType dateTimeType = new DateTimeType();
            dateTimeType.setDateTime(xmlGregorianCalendar);
            delimitedPeriod.setStartDateTime(dateTimeType);
        }

        if (endDate != null) {
            XMLGregorianCalendar xmlGregorianCalendar = XMLDateUtils.dateToXmlGregorian(Date.from(endDate));
            DateTimeType dateTimeType = new DateTimeType();
            dateTimeType.setDateTime(xmlGregorianCalendar);
            delimitedPeriod.setEndDateTime(dateTimeType);
        }

        if (durationMeasure != null) {
            un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType measureType = new un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType();
            CustomBigDecimal customBigDecimal = new CustomBigDecimal();
            BigDecimal bigDecimal = customBigDecimal.createBigDecimal(durationMeasure.getValue());

            measureType.setValue(bigDecimal);
            measureType.setUnitCode(durationMeasure.getUnitCode());
            measureType.setUnitCodeListVersionID(durationMeasure.getUnitCodeListVersionID());

            delimitedPeriod.setDurationMeasure(measureType);
        }

        return delimitedPeriod;
    }
}
