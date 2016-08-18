/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by padhyad on 8/11/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GearCharacteristicsDetailsDTO {

    @JsonProperty("typeCode")
    private String typeCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("valueMeasure")
    private Double valueMeasure;

    @JsonProperty("dateTime")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date dateTime;

    @JsonProperty("indicator")
    private String indicator;

    @JsonProperty("code")
    private String code;

    @JsonProperty("text")
    private String text;

    @JsonProperty("quantity")
    private Double quantity;

    public GearCharacteristicsDetailsDTO() {
    }

    public GearCharacteristicsDetailsDTO(String typeCode, String description, Double valueMeasure, Date dateTime, String indicator, String code, String text, Double quantity) {
        this.typeCode = typeCode;
        this.description = description;
        this.valueMeasure = valueMeasure;
        this.dateTime = dateTime;
        this.indicator = indicator;
        this.code = code;
        this.text = text;
        this.quantity = quantity;
    }

    @JsonProperty("typeCode")
    public String getTypeCode() {
        return typeCode;
    }

    @JsonProperty("typeCode")
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("valueMeasure")
    public Double getValueMeasure() {
        return valueMeasure;
    }

    @JsonProperty("valueMeasure")
    public void setValueMeasure(Double valueMeasure) {
        this.valueMeasure = valueMeasure;
    }

    @JsonProperty("dateTime")
    public Date getDateTime() {
        return dateTime;
    }

    @JsonProperty("dateTime")
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @JsonProperty("indicator")
    public String getIndicator() {
        return indicator;
    }

    @JsonProperty("indicator")
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("quantity")
    public Double getQuantity() {
        return quantity;
    }

    @JsonProperty("quantity")
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}
