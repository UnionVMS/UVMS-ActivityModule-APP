package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.service.util.CustomBigDecimal;
import eu.europa.ec.fisheries.uvms.commons.date.XMLDateUtils;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class DelimitedPeriodMapper {
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
            BigDecimal bigDecimal = customBigDecimal.createBigDecimal(Long.valueOf(durationInMinutes).doubleValue());

            measureType.setValue(bigDecimal);
            measureType.setUnitCode("MIN");

            delimitedPeriod.setDurationMeasure(measureType);
        }

        return delimitedPeriod;
    }
}
