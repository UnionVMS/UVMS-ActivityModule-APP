/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.uvms.activity.service.dto.view;

import com.google.common.collect.Maps;
import eu.europa.ec.fisheries.uvms.activity.service.dto.DelimitedPeriodDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FlapDocumentDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FluxCharacteristicsDto;
import lombok.ToString;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ToString
public class ActivityDetailsDto {

    private String type;

    private Long id;

    @JsonbTransient
    private Set<FluxCharacteristicsDto> fluxCharacteristics;

    @JsonbProperty("authorizations")
    private Set<FlapDocumentDto> flapDocuments;

    @JsonbTransient
    private Geometry geom;

    private String reason;

    private Date occurrence;

    private Date arrivalTime;

    private Date intendedLandingTime;

    private Date estimatedArrivalTime;

    private DelimitedPeriodDTO landingTime;

    private String fisheryType;

    private String speciesTarget;

    private String vesselActivity;

    private Integer nrOfOperation;

    private Set<IdentifierDto> identifiers;

    private DelimitedPeriodDTO fishingTime;

    private DelimitedPeriodDTO transhipmentTime;

    @JsonbProperty("characteristics")
    public Map<String, Set<Object>> getCharacteristics() {
        if (fluxCharacteristics == null) {
            return null;
        }

        Map<String, Set<Object>> characMap = new HashMap<>();

        for (FluxCharacteristicsDto fluxCharacteristicsDto : fluxCharacteristics) {
            Double calculatedValueMeasure = fluxCharacteristicsDto.getCalculatedValueMeasure();
            add("calculatedValueMeasure", calculatedValueMeasure, characMap);
            Double calculatedValueQuantity = fluxCharacteristicsDto.getCalculatedValueQuantity();
            add("calculatedValueQuantity", calculatedValueQuantity, characMap);
            String description = fluxCharacteristicsDto.getDescription();
            add("description", description, characMap);
            String descriptionLanguageId = fluxCharacteristicsDto.getDescriptionLanguageId();
            add("descriptionLanguageId", descriptionLanguageId, characMap);
            String typeCode = fluxCharacteristicsDto.getTypeCode();
            add("typeCode", typeCode, characMap);
            String typeCodeListId = fluxCharacteristicsDto.getTypeCodeListId();
            add("typeCodeListId", typeCodeListId, characMap);
            Date valueDateTime = fluxCharacteristicsDto.getValueDateTime();
            add("valueDateTime", valueDateTime, characMap);
            String valueIndicator = fluxCharacteristicsDto.getValueIndicator();
            add("valueIndicator", valueIndicator, characMap);
            Double valueMeasure = fluxCharacteristicsDto.getValueMeasure();
            add("valueMeasure", valueMeasure, characMap);
            String valueMeasureUnitCode = fluxCharacteristicsDto.getValueMeasureUnitCode();
            add("valueMeasureUnitCode", valueMeasureUnitCode, characMap);
            Double valueQuantity = fluxCharacteristicsDto.getValueQuantity();
            add("valueQuantity", valueQuantity, characMap);
            String valueQuantityCode = fluxCharacteristicsDto.getValueQuantityCode();
            add("valueQuantityCode", valueQuantityCode, characMap);
            String valueLanguageId = fluxCharacteristicsDto.getValueLanguageId();
            add("valueLanguageId", valueLanguageId, characMap);
            String valueCode = fluxCharacteristicsDto.getValueCode();
            add("valueCode", valueCode, characMap);
        }

        return characMap;
    }

    public Set<FluxCharacteristicsDto> getFluxCharacteristics() {
        return fluxCharacteristics;
    }

    public void setFluxCharacteristics(Set<FluxCharacteristicsDto> fluxCharacteristics) {
        this.fluxCharacteristics = fluxCharacteristics;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(Date occurrence) {
        this.occurrence = occurrence;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Date getIntendedLandingTime() {
        return intendedLandingTime;
    }

    public void setIntendedLandingTime(Date intendedLandingTime) {
        this.intendedLandingTime = intendedLandingTime;
    }

    public Date getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setEstimatedArrivalTime(Date estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public DelimitedPeriodDTO getLandingTime() {
        return landingTime;
    }

    public void setLandingTime(DelimitedPeriodDTO landingTime) {
        this.landingTime = landingTime;
    }

    public String getFisheryType() {
        return fisheryType;
    }

    public void setFisheryType(String fisheryType) {
        this.fisheryType = fisheryType;
    }

    public String getSpeciesTarget() {
        return speciesTarget;
    }

    public void setSpeciesTarget(String speciesTarget) {
        this.speciesTarget = speciesTarget;
    }

    public String getVesselActivity() {
        return vesselActivity;
    }

    public void setVesselActivity(String vesselActivity) {
        this.vesselActivity = vesselActivity;
    }

    public Integer getNrOfOperation() {
        return nrOfOperation;
    }

    public void setNrOfOperation(Integer nrOfOperation) {
        this.nrOfOperation = nrOfOperation;
    }

    public Set<IdentifierDto> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(Set<IdentifierDto> identifiers) {
        this.identifiers = identifiers;
    }

    public DelimitedPeriodDTO getFishingTime() {
        return fishingTime;
    }

    public void setFishingTime(DelimitedPeriodDTO fishingTime) {
        this.fishingTime = fishingTime;
    }

    public DelimitedPeriodDTO getTranshipmentTime() {
        return transhipmentTime;
    }

    public void setTranshipmentTime(DelimitedPeriodDTO transhipmentTime) {
        this.transhipmentTime = transhipmentTime;
    }

    @JsonbProperty("geom")
    public String getWkt() {
        String wkt = null;
        if (geom != null) {
            wkt = new WKTWriter().write(geom);
        }
        return wkt;
    }

    public Geometry getGeom() {
        return geom;
    }

    public void setGeom(Geometry geom) {
        this.geom = geom;
    }

    private void add(String key, Object value, Map<String, Set<Object>> map) {
        if (value != null) {
            map.putIfAbsent(key, new HashSet<>());
            map.get(key).add(value);
        }
    }

    public Set<FlapDocumentDto> getFlapDocuments() {
        return flapDocuments;
    }

    public void setFlapDocuments(Set<FlapDocumentDto> flapDocuments) {
        this.flapDocuments = flapDocuments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
