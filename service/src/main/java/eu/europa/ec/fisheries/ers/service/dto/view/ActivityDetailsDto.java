/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.ers.service.dto.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.service.dto.DelimitedPeriodDTO;
import eu.europa.ec.fisheries.ers.service.dto.FlapDocumentDto;
import eu.europa.ec.fisheries.ers.service.dto.FluxCharacteristicsDto;
import eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.AreaEntry;
import eu.europa.ec.fisheries.uvms.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.rest.serializer.CustomDateSerializer;
import lombok.ToString;
import org.mockito.internal.util.collections.Sets;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.AreaExit;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Arrival;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.CommonView;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Departure;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Discard;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.FishingOperation;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.JointFishingOperation;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Landing;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.NotificationOfArrival;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Relocation;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.Transhipment;

@JsonInclude(Include.NON_NULL)
@ToString
public class ActivityDetailsDto {

    @JsonView(CommonView.class)
    private String type;

    @JsonIgnore
    private Set<FluxCharacteristicsDto> fluxCharacteristics;

    @JsonProperty("authorizations")
    @JsonView({CommonView.class})
    private Set<FlapDocumentDto> flapDocuments;

    @JsonIgnore
    private Geometry geom;

    @JsonView({Arrival.class, NotificationOfArrival.class, AreaEntry.class, Discard.class})
    private String reason;

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonView({Landing.class, Departure.class, AreaEntry.class, AreaExit.class, FishingOperation.class, Discard.class,
            JointFishingOperation.class, Relocation.class,NotificationOfArrival.class})
    private Date occurrence;

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonView(Arrival.class)
    private Date arrivalTime;

    @JsonView(Arrival.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date intendedLandingTime;

    @JsonView(NotificationOfArrival.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date estimatedArrivalTime;

    @JsonView(Landing.class)
    private DelimitedPeriodDTO landingTime;

    @JsonView({Departure.class, AreaEntry.class, AreaExit.class, FishingOperation.class, JointFishingOperation.class})
    private String fisheryType;

    @JsonView({Departure.class, AreaEntry.class, AreaExit.class, FishingOperation.class, JointFishingOperation.class})
    private String speciesTarget;

    @JsonView({FishingOperation.class, JointFishingOperation.class})
    private String vesselActivity;

    @JsonView(FishingOperation.class)
    private Integer nrOfOperation;

    @JsonView({FishingOperation.class, Discard.class, Transhipment.class, JointFishingOperation.class
            , Relocation.class})
    private Set<IdentifierDto> identifiers;

    @JsonView({FishingOperation.class, JointFishingOperation.class})
    private DelimitedPeriodDTO fishingTime;

    @JsonView(Transhipment.class)
    private DelimitedPeriodDTO transhipmentTime;

    @JsonProperty("characteristics")
    @JsonView({CommonView.class})
    public Map<String, Set<Object>> getCharacteristics() {
        Map<String, Set<Object>> characMap = null;
        if (fluxCharacteristics != null) {
            characMap = Maps.newHashMap();
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

    @JsonProperty("geom")
    public String getWkt() {
        String wkt = null;
        if (geom != null) {
            wkt = GeometryMapper.INSTANCE.geometryToWkt(geom).getValue();
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
        if(key !=null) {
            Set<Object> valueSet = map.get(key);
            if (valueSet == null) {
                valueSet = Sets.newSet();
            } else if (value != null) {
                    valueSet.add(value);
            }
            map.put(key, valueSet);
        }
    }

    public Set<FlapDocumentDto> getFlapDocuments() {
        return flapDocuments;
    }

    public void setFlapDocuments(Set<FlapDocumentDto> flapDocuments) {
        this.flapDocuments = flapDocuments;
    }
}
