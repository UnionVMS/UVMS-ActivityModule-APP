/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.VesselCountry;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.*;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by padhyad on 6/14/2016.
 */
@Slf4j
public abstract class BaseMapper {

    protected String getIdType(IDType idType) {
        return (idType == null) ? null : idType.getValue();
    }

    protected String getIdTypeSchemaId(IDType idType) {
        return (idType == null) ? null : idType.getSchemeID();
    }

/*    protected String getIdTypeFromList(List<IDType> ids) {
        return (ids == null || ids.isEmpty()) ? null : ids.get(0).getValue();
    }*/

    protected String getTextType(TextType textType) {
        return textType == null ? null : textType.getValue();
    }

    protected String getLanguageId(TextType textType) {
        return textType == null ? null : textType.getLanguageID();
    }

    protected String getIndicatorType(IndicatorType indicatorType) {
        if (indicatorType == null) {
            return null;
        }
        return indicatorType.getIndicatorString() == null ? null : indicatorType.getIndicatorString().getValue();
    }

    protected String getCodeType(CodeType codeType) {
        return (codeType == null) ? null : codeType.getValue();
    }

    protected String getCodeTypeListId(CodeType codeType) {
        return (codeType == null) ? null : codeType.getListID();
    }

/*    protected String getCodeTypeFromList(List<CodeType> codeTypes) {
        return (codeTypes == null || codeTypes.isEmpty()) ? null : codeTypes.get(0).getValue();
    }

    protected String getCodeTypeListIdFromList(List<CodeType> codeTypes) {
        return (codeTypes == null || codeTypes.isEmpty()) ? null : codeTypes.get(0).getListID();
    }*/

    protected Date convertToDate(DateTimeType dateTime) {
        Date value = null;
        try {
            if (dateTime != null) {
                if (dateTime.getDateTime() != null) {
                    value = dateTime.getDateTime().toGregorianCalendar().getTime();
                } else if (dateTime.getDateTimeString() != null) {
                    DateFormat df = new SimpleDateFormat(dateTime.getDateTimeString().getFormat());
                    value = df.parse(dateTime.getDateTimeString().getValue());
                }
            }
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return value;
    }

    protected String getValueIndicator(IndicatorType indicatorType) {
        if (indicatorType == null) {
            return null;
        }
        return indicatorType.getIndicatorString() == null ? null : indicatorType.getIndicatorString().getValue();
    }

    protected String getTextFromList(List<TextType> textTypes) {
        if (textTypes == null) {
            return null;
        }
        return textTypes.get(0).getValue();
    }

    protected String getLanguageIdFromList(List<TextType> textTypes) {
        if (textTypes == null) {
            return null;
        }
        return textTypes.get(0).getLanguageID();
    }

    protected Double getQuantity(QuantityType quantityType) {
        if (quantityType == null) {
            return null;
        }
        return quantityType.getValue().doubleValue();
    }

    protected Long getQuantityInLong(QuantityType quantityType) {
        if (quantityType == null) {
            return null;
        }
        return quantityType.getValue().longValue();
    }

    protected Double getMeasure(MeasureType measureType) {
        if (measureType == null) {
            return null;
        }
        return measureType.getValue().doubleValue();
    }

    protected String getMeasureUnitCode(MeasureType measureType) {
        if (measureType == null) {
            return null;
        }
        return measureType.getUnitCode();
    }

    protected String getMeasureListId(MeasureType measureType) {
        if (measureType == null) {
            return null;
        }
        return measureType.getUnitCodeListVersionID();
    }

    protected Integer getNumericInteger(NumericType numericType) {
        BigDecimal conversionFactor = numericType.getValue();
        return conversionFactor != null ? conversionFactor.intValue() : null;
    }

    protected String getCountry(VesselCountry country) {
        return getIdType(country.getID());
    }

    protected String getCountrySchemeId(VesselCountry country) {
        return getIdTypeSchemaId(country.getID());
    }
}