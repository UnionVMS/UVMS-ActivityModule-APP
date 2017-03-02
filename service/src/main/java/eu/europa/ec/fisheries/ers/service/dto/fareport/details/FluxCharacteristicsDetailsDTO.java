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

package eu.europa.ec.fisheries.ers.service.dto.fareport.details;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.europa.ec.fisheries.uvms.rest.serializer.CustomDateSerializer;

import java.util.Date;

/**
 * Created by padhyad on 8/11/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FluxCharacteristicsDetailsDTO {

    @JsonProperty("type")
    private String type;

    @JsonProperty("measure")
    private Double measure;

    @JsonProperty("dateTime")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date dateTime;

    @JsonProperty("indicator")
    private String indicator;

    @JsonProperty("code")
    private String code;

    @JsonProperty("text")
    private String text;

    @JsonProperty("quantity")
    private Double quantity;

    @JsonProperty("description")
    private String description;

    public FluxCharacteristicsDetailsDTO() {
    }

    public FluxCharacteristicsDetailsDTO(String type, Double measure, Date dateTime, String indicator, String code, String text, Double quantity, String description) {
        this.type = type;
        this.measure = measure;
        this.dateTime = dateTime;
        this.indicator = indicator;
        this.code = code;
        this.text = text;
        this.quantity = quantity;
        this.description = description;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("measure")
    public Double getMeasure() {
        return measure;
    }

    @JsonProperty("measure")
    public void setMeasure(Double measure) {
        this.measure = measure;
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

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }
}
