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

package eu.europa.ec.fisheries.uvms.activity.model.dto.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by padhyad on 8/23/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FaReportConfigDTO {

    @JsonProperty("faReport")
    private List<String> faReport;

    @JsonProperty("fishingActivity")
    private List<String> fishingActivity;

    @JsonProperty("fishingTrip")
    private List<String> fishingTrip;

    @JsonProperty("delimitedPeriod")
    private List<String> delimitedPeriod;

    @JsonProperty("faCatches")
    private List<String> faCatches;

    @JsonProperty("fishingGears")
    private List<String> fishingGears;

    @JsonProperty("gearCharacteristics")
    private List<String> gearCharacteristics;

    @JsonProperty("FluxLocations")
    private List<String> FluxLocations;

    @JsonProperty("fluxCharacteristics")
    private List<String> fluxCharacteristics;

    @JsonProperty("structuredAddress")
    private List<String> structuredAddress;

    @JsonProperty("aapProcess")
    private List<String> aapProcess;

    @JsonProperty("aapProduct")
    private List<String> aapProduct;

    @JsonProperty("aapStock")
    private List<String> aapStock;

    @JsonProperty("gearProblems")
    private List<String> gearProblems;

    @JsonProperty("vessel")
    private List<String> vessel;

    @JsonProperty("contactParty")
    private List<String> contactParty;

    @JsonProperty("contactPerson")
    private List<String> contactPerson;

    public FaReportConfigDTO() {
    }

    public FaReportConfigDTO(List<String> faReport, List<String> fishingActivity, List<String> fishingTrip, List<String> delimitedPeriod, List<String> faCatches, List<String> fishingGears, List<String> gearCharacteristics, List<String> fluxLocations, List<String> fluxCharacteristics, List<String> structuredAddress, List<String> aapProcess, List<String> aapProduct, List<String> aapStock, List<String> gearProblems, List<String> vessel, List<String> contactParty, List<String> contactPerson) {
        this.faReport = faReport;
        this.fishingActivity = fishingActivity;
        this.fishingTrip = fishingTrip;
        this.delimitedPeriod = delimitedPeriod;
        this.faCatches = faCatches;
        this.fishingGears = fishingGears;
        this.gearCharacteristics = gearCharacteristics;
        FluxLocations = fluxLocations;
        this.fluxCharacteristics = fluxCharacteristics;
        this.structuredAddress = structuredAddress;
        this.aapProcess = aapProcess;
        this.aapProduct = aapProduct;
        this.aapStock = aapStock;
        this.gearProblems = gearProblems;
        this.vessel = vessel;
        this.contactParty = contactParty;
        this.contactPerson = contactPerson;
    }

    @JsonProperty("faReport")
    public List<String> getFaReport() {
        return faReport;
    }

    @JsonProperty("faReport")
    public void setFaReport(List<String> faReport) {
        this.faReport = faReport;
    }

    @JsonProperty("fishingActivity")
    public List<String> getFishingActivity() {
        return fishingActivity;
    }

    @JsonProperty("fishingActivity")
    public void setFishingActivity(List<String> fishingActivity) {
        this.fishingActivity = fishingActivity;
    }

    @JsonProperty("fishingTrip")
    public List<String> getFishingTrip() {
        return fishingTrip;
    }

    @JsonProperty("fishingTrip")
    public void setFishingTrip(List<String> fishingTrip) {
        this.fishingTrip = fishingTrip;
    }

    @JsonProperty("delimitedPeriod")
    public List<String> getDelimitedPeriod() {
        return delimitedPeriod;
    }

    @JsonProperty("delimitedPeriod")
    public void setDelimitedPeriod(List<String> delimitedPeriod) {
        this.delimitedPeriod = delimitedPeriod;
    }

    @JsonProperty("faCatches")
    public List<String> getFaCatches() {
        return faCatches;
    }

    @JsonProperty("faCatches")
    public void setFaCatches(List<String> faCatches) {
        this.faCatches = faCatches;
    }

    @JsonProperty("fishingGears")
    public List<String> getFishingGears() {
        return fishingGears;
    }

    @JsonProperty("fishingGears")
    public void setFishingGears(List<String> fishingGears) {
        this.fishingGears = fishingGears;
    }

    @JsonProperty("gearCharacteristics")
    public List<String> getGearCharacteristics() {
        return gearCharacteristics;
    }

    @JsonProperty("gearCharacteristics")
    public void setGearCharacteristics(List<String> gearCharacteristics) {
        this.gearCharacteristics = gearCharacteristics;
    }

    @JsonProperty("FluxLocations")
    public List<String> getFluxLocations() {
        return FluxLocations;
    }

    @JsonProperty("FluxLocations")
    public void setFluxLocations(List<String> fluxLocations) {
        FluxLocations = fluxLocations;
    }

    @JsonProperty("fluxCharacteristics")
    public List<String> getFluxCharacteristics() {
        return fluxCharacteristics;
    }

    @JsonProperty("fluxCharacteristics")
    public void setFluxCharacteristics(List<String> fluxCharacteristics) {
        this.fluxCharacteristics = fluxCharacteristics;
    }

    @JsonProperty("structuredAddress")
    public List<String> getStructuredAddress() {
        return structuredAddress;
    }

    @JsonProperty("structuredAddress")
    public void setStructuredAddress(List<String> structuredAddress) {
        this.structuredAddress = structuredAddress;
    }

    @JsonProperty("aapProcess")
    public List<String> getAapProcess() {
        return aapProcess;
    }

    @JsonProperty("aapProcess")
    public void setAapProcess(List<String> aapProcess) {
        this.aapProcess = aapProcess;
    }

    @JsonProperty("aapProduct")
    public List<String> getAapProduct() {
        return aapProduct;
    }

    @JsonProperty("aapProduct")
    public void setAapProduct(List<String> aapProduct) {
        this.aapProduct = aapProduct;
    }

    @JsonProperty("aapStock")
    public List<String> getAapStock() {
        return aapStock;
    }

    @JsonProperty("aapStock")
    public void setAapStock(List<String> aapStock) {
        this.aapStock = aapStock;
    }

    @JsonProperty("gearProblems")
    public List<String> getGearProblems() {
        return gearProblems;
    }

    @JsonProperty("gearProblems")
    public void setGearProblems(List<String> gearProblems) {
        this.gearProblems = gearProblems;
    }

    @JsonProperty("vessel")
    public List<String> getVessel() {
        return vessel;
    }

    @JsonProperty("vessel")
    public void setVessel(List<String> vessel) {
        this.vessel = vessel;
    }

    @JsonProperty("contactParty")
    public List<String> getContactParty() {
        return contactParty;
    }

    @JsonProperty("contactParty")
    public void setContactParty(List<String> contactParty) {
        this.contactParty = contactParty;
    }

    @JsonProperty("contactPerson")
    public List<String> getContactPerson() {
        return contactPerson;
    }

    @JsonProperty("contactPerson")
    public void setContactPerson(List<String> contactPerson) {
        this.contactPerson = contactPerson;
    }
}
