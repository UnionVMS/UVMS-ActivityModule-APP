/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.fa.utils.UnitCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselCountry;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


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

    protected XMLGregorianCalendar convertToXMLGregorianCalendar(Date dateTime, boolean includeTimeZone) {
        XMLGregorianCalendar calander = null;
        try {
            GregorianCalendar cal = new  GregorianCalendar();
            cal.setTimeInMillis(dateTime.getTime());
            calander = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
            if(!includeTimeZone) {
                calander.setTimezone(DatatypeConstants.FIELD_UNDEFINED); //If we do not want timeZone to be included, set this
            }
        } catch (DatatypeConfigurationException e) {
            log.error(e.getMessage(), e);
        }
      return calander;
    }

    protected String getValueIndicator(IndicatorType indicatorType) {
        if (indicatorType == null) {
            return null;
        }
        return indicatorType.getIndicatorString() == null ? null : indicatorType.getIndicatorString().getValue();
    }

    protected String getTextFromList(List<TextType> textTypes) {
        if (CollectionUtils.isEmpty(textTypes)) {
            return null;
        }
        return textTypes.get(0).getValue();
    }

    protected String getLanguageIdFromList(List<TextType> textTypes) {
        if (CollectionUtils.isEmpty(textTypes)) {
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

    protected String getQuantityUnitCode(QuantityType quantityType) {
        if (quantityType == null) {
            return null;
        }
        return quantityType.getUnitCode();
    }

    protected Double getCalculatedQuantity(QuantityType quantityType) {
        if (quantityType == null) {
            return null;
        }
        UnitCodeEnum unitCodeEnum = UnitCodeEnum.getUnitCode(quantityType.getUnitCode());
        if (unitCodeEnum != null) {
            BigDecimal quantity = quantityType.getValue();
            BigDecimal result = quantity.multiply(new BigDecimal(unitCodeEnum.getConversionFactor()));
            return result.doubleValue();
        } else {
            return null;
        }
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

    protected Double getCalculatedMeasure(MeasureType measureType) {
        if (measureType == null) {
            return null;
        }
        UnitCodeEnum unitCodeEnum = UnitCodeEnum.getUnitCode(measureType.getUnitCode());
        if (unitCodeEnum != null) {
            BigDecimal measuredValue = measureType.getValue();
            BigDecimal result = measuredValue.multiply(new BigDecimal(unitCodeEnum.getConversionFactor()));
            return result.doubleValue();
        } else {
            return null;
        }
    }

    protected Integer getNumericInteger(NumericType numericType) {
        BigDecimal conversionFactor = numericType.getValue();
        return conversionFactor != null ? conversionFactor.intValue() : null;
    }

    protected String getCountry(VesselCountry country) {
        if(country==null){
            return null;
        }
        return getIdType(country.getID());
    }

    protected String getCountrySchemeId(VesselCountry country) {
        return getIdTypeSchemaId(country.getID());
    }

    protected Map<String, String> getReportIdMap(Collection<FluxReportIdentifierEntity> identifiers) {
        Map<String, String> recordMap = new HashMap<>();
        for (FluxReportIdentifierEntity identifier : identifiers) {
            recordMap.put(identifier.getFluxReportIdentifierId(), identifier.getFluxReportIdentifierSchemeId());
        }
        return recordMap;
    }

    protected Integer getPurposeCode(String purposeCode) {
        try {
            return Integer.parseInt(purposeCode);
        } catch (NumberFormatException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private FishingTripEntity getSpecifiedFishingTrip(FishingActivityEntity activityEntity) {

        FishingTripEntity fishingTripEntity = null;

        Set<FishingTripEntity> fishingTrips = activityEntity.getFishingTrips();
        if (!CollectionUtils.isEmpty(fishingTrips)) {
            fishingTripEntity = activityEntity.getFishingTrips().iterator().next();
        }

        return fishingTripEntity;
    }

    public FluxReportDocumentEntity getFluxReportDocument(FishingActivityEntity activityEntity) {
        FaReportDocumentEntity faReportDocument = activityEntity.getFaReportDocument();
        return faReportDocument != null ? faReportDocument.getFluxReportDocument() : null;
    }

    public Set<FluxLocationEntity> getRelatedFluxLocations(FishingActivityEntity activityEntity) {

        FishingTripEntity specifiedFishingTrip = getSpecifiedFishingTrip(activityEntity);
        Set<FluxLocationEntity> relatedFluxLocations = new HashSet<>();

        if (specifiedFishingTrip != null){
            relatedFluxLocations = getRelatedFluxLocations(specifiedFishingTrip);
        }

        return relatedFluxLocations;
    }

    public Set<FaReportIdentifierEntity> getFaReportIdentifiers(FishingActivityEntity activityEntity) {
        return activityEntity.getFaReportDocument().getFaReportIdentifiers();
    }

    public Set<FluxLocationEntity> getRelatedFluxLocations(FishingTripEntity tripEntity) {
        Set<FluxLocationEntity> fluxLocations = new HashSet<>();
        FishingActivityEntity fishingActivity = tripEntity.getFishingActivity();
        if (fishingActivity != null){
            fluxLocations = fishingActivity.getFluxLocations();
        }
        return fluxLocations;
    }
}