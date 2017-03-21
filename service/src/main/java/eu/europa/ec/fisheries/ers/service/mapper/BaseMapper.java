/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */


package eu.europa.ec.fisheries.ers.service.mapper;

import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyRoleEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationEnum;
import eu.europa.ec.fisheries.ers.fa.utils.UnitCodeEnum;
import eu.europa.ec.fisheries.ers.service.dto.view.PositionDto;
import eu.europa.ec.fisheries.uvms.common.utils.GeometryUtils;
import eu.europa.ec.fisheries.uvms.mapper.GeometryMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselCountry;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IndicatorType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.NumericType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.QuantityType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.TextType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
        if (numericType != null) {
            BigDecimal conversionFactor = numericType.getValue();
            return conversionFactor.intValue();
        }
        return null;
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

    public static List<String> getRoles(Set<ContactPartyRoleEntity> contactPartyRoles){
        List<String> roles = new ArrayList<>();
        for(ContactPartyRoleEntity roleEntity : contactPartyRoles){
            roles.add(roleEntity.getRoleCode());
        }
        return roles;
    }

    public static FishingTripEntity getSpecifiedFishingTrip(FishingActivityEntity activityEntity) {
        FishingTripEntity fishingTripEntity = null;
        Set<FishingTripEntity> fishingTrips = activityEntity.getFishingTrips();
        if (!CollectionUtils.isEmpty(fishingTrips)) {
            fishingTripEntity = activityEntity.getFishingTrips().iterator().next();
        }
        return fishingTripEntity;
    }

    public static FluxReportDocumentEntity getFluxReportDocument(FishingActivityEntity activityEntity) {
        FaReportDocumentEntity faReportDocument = activityEntity.getFaReportDocument();
        return faReportDocument != null ? faReportDocument.getFluxReportDocument() : null;
    }

    public static Set<FluxLocationEntity> getRelatedFluxLocations(FishingActivityEntity activityEntity) {
        FishingTripEntity specifiedFishingTrip = getSpecifiedFishingTrip(activityEntity);
        Set<FluxLocationEntity> relatedFluxLocations = new HashSet<>();
        if (specifiedFishingTrip != null){
            relatedFluxLocations = getRelatedFluxLocations(specifiedFishingTrip);
        }
        return relatedFluxLocations;
    }

    public static Set<FluxLocationEntity> getRelatedFluxLocations(FishingTripEntity tripEntity) {
        Set<FluxLocationEntity> fluxLocations = new HashSet<>();
        FishingActivityEntity fishingActivity = tripEntity.getFishingActivity();
        if (fishingActivity != null){
            fluxLocations = fishingActivity.getFluxLocations();
        }
        return fluxLocations;
    }

    protected FishingActivityEntity extractSubFishingActivity(Set<FishingActivityEntity> fishingActivityList,FishingActivityTypeEnum faTypeToExtract) {
       if(CollectionUtils.isEmpty(fishingActivityList)){
           return null;
       }

        for(FishingActivityEntity fishingActivityEntity:fishingActivityList){
            if(faTypeToExtract.toString().equalsIgnoreCase(fishingActivityEntity.getTypeCode())){
                return fishingActivityEntity;
            }
        }

        return null;
    }


    protected FluxLocationEntity extractFLUXPosition(Set<FluxLocationEntity> fluxLocationEntityList) {
        if(CollectionUtils.isEmpty(fluxLocationEntityList)){
            return null;
        }

        for(FluxLocationEntity locationEntity : fluxLocationEntityList){
            if(FluxLocationEnum.LOCATION.toString().equalsIgnoreCase(locationEntity.getTypeCode())){
                return locationEntity;
            }
        }
        return null;
    }

    protected String extractGeometryWkt(Double longitude,Double latitude){
        Geometry geom = GeometryUtils.createPoint(longitude, latitude);

       return GeometryMapper.INSTANCE.geometryToWkt(geom).getValue();
    }

    @NotNull
    protected PositionDto extractPositionDtoFromFishingActivity(FishingActivityEntity faEntity) {
        if(faEntity == null){
            return null;
        }
        PositionDto positionDto = new PositionDto();
        positionDto.setOccurence(faEntity.getOccurence());
        if(CollectionUtils.isNotEmpty(faEntity.getFluxLocations())){
            FluxLocationEntity locationEntity= extractFLUXPosition(faEntity.getFluxLocations());
            if(locationEntity !=null) {
                positionDto.setGeometry(extractGeometryWkt(locationEntity.getLongitude(), locationEntity.getLatitude()));
            }
        }
        return positionDto;
    }
}