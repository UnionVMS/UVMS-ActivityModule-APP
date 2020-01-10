package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.service.util.CustomBigDecimal;
import eu.europa.ec.fisheries.uvms.commons.date.XMLDateUtils;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DelimitedPeriodMapper {
    private DelimitedPeriodMapper() {
        // Util class, no public constructor
    }

    public static Instant getStartDate(DelimitedPeriod delimitedPeriod) {
        if (delimitedPeriod == null) {
            return null;
        }

        DateTimeType startDateTime = delimitedPeriod.getStartDateTime();
        DateTimeType endDateTime = delimitedPeriod.getEndDateTime();
        MeasureType durationMeasure = delimitedPeriod.getDurationMeasure();

        if (startDateTime != null) {
            Date date = XMLDateUtils.xmlGregorianCalendarToDate(startDateTime.getDateTime());
            return date.toInstant();
        }

        if (endDateTime == null || durationMeasure == null) {
            return null;
        }

        Date endDate = XMLDateUtils.xmlGregorianCalendarToDate(endDateTime.getDateTime());
        Instant endDateInstant = endDate.toInstant();

        BigDecimal bigDecimalDuration = durationMeasure.getValue();
        if (bigDecimalDuration == null) {
            return null;
        }

        int durationInMinutes = bigDecimalDuration.intValue();
        return endDateInstant.minus(durationInMinutes, ChronoUnit.MINUTES);
    }

    public static Instant getEndDate(DelimitedPeriod delimitedPeriod) {
        if (delimitedPeriod == null) {
            return null;
        }

        DateTimeType startDateTime = delimitedPeriod.getStartDateTime();
        DateTimeType endDateTime = delimitedPeriod.getEndDateTime();
        MeasureType durationMeasure = delimitedPeriod.getDurationMeasure();

        if (endDateTime != null) {
            Date date = XMLDateUtils.xmlGregorianCalendarToDate(endDateTime.getDateTime());
            return date.toInstant();
        }

        if (startDateTime == null || durationMeasure == null) {
            return null;
        }

        Date startDate = XMLDateUtils.xmlGregorianCalendarToDate(startDateTime.getDateTime());
        Instant startDateInstant = startDate.toInstant();

        BigDecimal bigDecimalDuration = durationMeasure.getValue();
        if (bigDecimalDuration == null) {
            return null;
        }

        int durationInMinutes = bigDecimalDuration.intValue();
        return startDateInstant.plus(durationInMinutes, ChronoUnit.MINUTES);
    }

    public static Duration getDuration(DelimitedPeriod delimitedPeriod) {
        if (delimitedPeriod == null) {
            return null;
        }

        MeasureType durationMeasure = delimitedPeriod.getDurationMeasure();
        if (durationMeasure == null) {
            return null;
        }

        BigDecimal bigDecimalDuration = durationMeasure.getValue();
        if (bigDecimalDuration == null) {
            return null;
        }

        int durationInMinutes = bigDecimalDuration.intValue();
        return Duration.of(durationInMinutes, ChronoUnit.MINUTES);
    }

    public static DelimitedPeriod convert(Instant startDate, Instant endDate) {
        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();

        if (startDate != null) {
            Date from = Date.from(startDate);
            XMLGregorianCalendar xmlGregorianCalendar = XMLDateUtils.dateToXmlGregorian(from);
            DateTimeType dateTimeType = new DateTimeType();
            dateTimeType.setDateTime(xmlGregorianCalendar);
            delimitedPeriod.setStartDateTime(dateTimeType);
        }

        if (endDate != null) {
            Date from = Date.from(endDate);
            XMLGregorianCalendar xmlGregorianCalendar = XMLDateUtils.dateToXmlGregorian(from);
            DateTimeType dateTimeType = new DateTimeType();
            dateTimeType.setDateTime(xmlGregorianCalendar);
            delimitedPeriod.setEndDateTime(dateTimeType);
        }

        if (startDate != null && endDate != null) {
            Duration duration = Duration.between(startDate, endDate);
            long durationInMinutes = duration.toMinutes();

            un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType measureType = new un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType();
            CustomBigDecimal customBigDecimal = new CustomBigDecimal();
            BigDecimal bigDecimal = customBigDecimal.createBigDecimal((double) durationInMinutes);

            measureType.setValue(bigDecimal);
            measureType.setUnitCode("MIN");

            delimitedPeriod.setDurationMeasure(measureType);
        }

        return delimitedPeriod;
    }
}
