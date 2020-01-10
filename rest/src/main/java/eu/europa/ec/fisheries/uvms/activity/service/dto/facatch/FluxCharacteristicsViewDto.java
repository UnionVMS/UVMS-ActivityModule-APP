/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.activity.service.dto.facatch;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

/**
 * Created by kovian on 02/03/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FluxCharacteristicsViewDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String typeCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String typeCodeListId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double valueMeasure;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String valueMeasureUnitCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double calculatedValueMeasure;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date valueDateTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String valueIndicator;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String valueCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String valueText;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String valueLanguageId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double valueQuantity;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String valueQuantityCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double calculatedValueQuantity;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String descriptionLanguageId;


    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeCodeListId() {
        return typeCodeListId;
    }

    public void setTypeCodeListId(String typeCodeListId) {
        this.typeCodeListId = typeCodeListId;
    }

    public Double getValueMeasure() {
        return valueMeasure;
    }

    public void setValueMeasure(Double valueMeasure) {
        this.valueMeasure = valueMeasure;
    }

    public String getValueMeasureUnitCode() {
        return valueMeasureUnitCode;
    }

    public void setValueMeasureUnitCode(String valueMeasureUnitCode) {
        this.valueMeasureUnitCode = valueMeasureUnitCode;
    }

    public Double getCalculatedValueMeasure() {
        return calculatedValueMeasure;
    }

    public void setCalculatedValueMeasure(Double calculatedValueMeasure) {
        this.calculatedValueMeasure = calculatedValueMeasure;
    }

    public Date getValueDateTime() {
        return valueDateTime;
    }

    public void setValueDateTime(Date valueDateTime) {
        this.valueDateTime = valueDateTime;
    }

    public String getValueIndicator() {
        return valueIndicator;
    }

    public void setValueIndicator(String valueIndicator) {
        this.valueIndicator = valueIndicator;
    }

    public String getValueCode() {
        return valueCode;
    }

    public void setValueCode(String valueCode) {
        this.valueCode = valueCode;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    public String getValueLanguageId() {
        return valueLanguageId;
    }

    public void setValueLanguageId(String valueLanguageId) {
        this.valueLanguageId = valueLanguageId;
    }

    public Double getValueQuantity() {
        return valueQuantity;
    }

    public void setValueQuantity(Double valueQuantity) {
        this.valueQuantity = valueQuantity;
    }

    public String getValueQuantityCode() {
        return valueQuantityCode;
    }

    public void setValueQuantityCode(String valueQuantityCode) {
        this.valueQuantityCode = valueQuantityCode;
    }

    public Double getCalculatedValueQuantity() {
        return calculatedValueQuantity;
    }

    public void setCalculatedValueQuantity(Double calculatedValueQuantity) {
        this.calculatedValueQuantity = calculatedValueQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionLanguageId() {
        return descriptionLanguageId;
    }

    public void setDescriptionLanguageId(String descriptionLanguageId) {
        this.descriptionLanguageId = descriptionLanguageId;
    }
}
