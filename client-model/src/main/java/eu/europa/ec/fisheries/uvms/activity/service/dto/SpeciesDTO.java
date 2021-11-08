package eu.europa.ec.fisheries.uvms.activity.service.dto;

import java.util.List;

public class SpeciesDTO {

    private String code;

    private String typeCode;

    private Double weight;

    private String weightMeans;

    private String sizeClass;

    private String presentation;

    private String preservation;

    private Double longitude;

    private Double latitude;

    private List<String> areas;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getWeightMeans() {
        return weightMeans;
    }

    public void setWeightMeans(String weightMeans) {
        this.weightMeans = weightMeans;
    }

    public String getSizeClass() {
        return sizeClass;
    }

    public void setSizeClass(String sizeClass) {
        this.sizeClass = sizeClass;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public String getPreservation() {
        return preservation;
    }

    public void setPreservation(String preservation) {
        this.preservation = preservation;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public List<String> getAreas() {
        return areas;
    }

    public void setAreas(List<String> areas) {
        this.areas = areas;
    } 
}
