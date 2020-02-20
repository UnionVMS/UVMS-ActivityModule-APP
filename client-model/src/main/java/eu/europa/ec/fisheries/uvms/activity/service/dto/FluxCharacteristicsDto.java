/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.uvms.activity.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class FluxCharacteristicsDto {

    private Date valueDateTime;

    @JsonIgnore
    private String typeCode;

    @JsonIgnore
    private String typeCodeListId;

    @JsonIgnore
    private Double valueMeasure;

    @JsonIgnore
    private String valueMeasureUnitCode;

    @JsonIgnore
    private Double calculatedValueMeasure;

    @JsonIgnore
    private String valueIndicator;

    @JsonIgnore
    private String valueCode;

    @JsonIgnore
    private String valueLanguageId;

    @JsonIgnore
    private Double valueQuantity;

    @JsonIgnore
    private String valueQuantityCode;

    @JsonIgnore
    private Double calculatedValueQuantity;

    @JsonIgnore
    private String description;

    @JsonIgnore
    private String descriptionLanguageId;

    public Date getValueDateTime() {
        return valueDateTime;
    }

    public void setValueDateTime(Date valueDateTime) {
        this.valueDateTime = valueDateTime;
    }

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
