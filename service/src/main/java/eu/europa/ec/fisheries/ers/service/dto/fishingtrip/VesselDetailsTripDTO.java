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

package eu.europa.ec.fisheries.ers.service.dto.fishingtrip;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.ContactPersonDetailsDTO;
import java.util.*;

/**
 * Created by sanera on 22/08/2016.
 */
public class VesselDetailsTripDTO {

    @JsonProperty("name")
    private String name;
    @JsonProperty("nameEnriched")
    private boolean nameEnriched;

    @JsonProperty("exMark")
    private String exMark;
    @JsonProperty("exMarkEnriched")
    private boolean exMarkEnriched;

    @JsonProperty("flagState")
    private String flagState;
    @JsonProperty("flagStateEnriched")
    private boolean flagStateEnriched;

    @JsonProperty("ircs")
    private String ircs;
    @JsonProperty("ircsEnriched")
    private boolean ircsEnriched;

    @JsonProperty("cfr")
    private String cfr;
    @JsonProperty("cfrEnriched")
    private boolean cfrEnriched;

    @JsonProperty("uvi")
    private String uvi;
    @JsonProperty("uviEnriched")
    private boolean uviEnriched;

    @JsonProperty("iccat")
    private String iccat;
    @JsonProperty("iccatEnriched")
    private boolean iccatEnriched;

    @JsonProperty("gfcm")
    private String gfcm;
    @JsonProperty("gfcmEnriched")
    private boolean gfcmEnriched;

    @JsonProperty("contactPersons")
    private Set<ContactPersonDetailsDTO> contactPersons = new HashSet<>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }
    @JsonProperty("exMark")
    public String getExMark() {
        return exMark;
    }
    @JsonProperty("exMark")
    public void setExMark(String exMark) {
        this.exMark = exMark;
    }
    @JsonProperty("flagState")
    public String getFlagState() {
        return flagState;
    }
    @JsonProperty("flagState")
    public void setFlagState(String flagState) {
        this.flagState = flagState;
    }
    @JsonProperty("ircs")
    public String getIrcs() {
        return ircs;
    }
    @JsonProperty("ircs")
    public void setIrcs(String ircs) {
        this.ircs = ircs;
    }
    @JsonProperty("cfr")
    public String getCfr() {
        return cfr;
    }
    @JsonProperty("cfr")
    public void setCfr(String cfr) {
        this.cfr = cfr;
    }
    public String getUvi() {
        return uvi;
    }
    public void setUvi(String uvi) {
        this.uvi = uvi;
    }
    public String getIccat() {
        return iccat;
    }
    public void setIccat(String iccat) {
        this.iccat = iccat;
    }
    public String getGfcm() {
        return gfcm;
    }
    public void setGfcm(String gfcm) {
        this.gfcm = gfcm;
    }
    @JsonProperty("contactPersons")
    public Set<ContactPersonDetailsDTO> getContactPersons() {
        return contactPersons;
    }
    @JsonProperty("contactPersons")
    public void setContactPersons(Set<ContactPersonDetailsDTO> contPersons) {
        this.contactPersons = contPersons;
    }
    public boolean isNameEnriched() {
        return nameEnriched;
    }
    public void setNameEnriched(boolean nameEnriched) {
        this.nameEnriched = nameEnriched;
    }
    public boolean isExMarkEnriched() {
        return exMarkEnriched;
    }
    public void setExMarkEnriched(boolean exMarkEnriched) {
        this.exMarkEnriched = exMarkEnriched;
    }
    public boolean isFlagStateEnriched() {
        return flagStateEnriched;
    }
    public void setFlagStateEnriched(boolean flagStateEnriched) {
        this.flagStateEnriched = flagStateEnriched;
    }
    public boolean isIrcsEnriched() {
        return ircsEnriched;
    }
    public void setIrcsEnriched(boolean ircsEnriched) {
        this.ircsEnriched = ircsEnriched;
    }
    public boolean isCfrEnriched() {
        return cfrEnriched;
    }
    public void setCfrEnriched(boolean cfrEnriched) {
        this.cfrEnriched = cfrEnriched;
    }
    public boolean isUviEnriched() {
        return uviEnriched;
    }
    public void setUviEnriched(boolean uviEnriched) {
        this.uviEnriched = uviEnriched;
    }
    public boolean isIccatEnriched() {
        return iccatEnriched;
    }
    public void setIccatEnriched(boolean iccatEnriched) {
        this.iccatEnriched = iccatEnriched;
    }
    public boolean isGfcmEnriched() {
        return gfcmEnriched;
    }
    public void setGfcmEnriched(boolean gfcmEnriched) {
        this.gfcmEnriched = gfcmEnriched;
    }

    @Override
    public String toString() {
        return "VesselDetailsTripDTO{" +
                "name='" + name + '\'' +
                ", nameEnriched=" + nameEnriched +
                ", exMark='" + exMark + '\'' +
                ", exMarkEnriched=" + exMarkEnriched +
                ", flagState='" + flagState + '\'' +
                ", flagStateEnriched=" + flagStateEnriched +
                ", ircs='" + ircs + '\'' +
                ", ircsEnriched=" + ircsEnriched +
                ", cfr='" + cfr + '\'' +
                ", cfrEnriched=" + cfrEnriched +
                ", uvi='" + uvi + '\'' +
                ", uviEnriched=" + uviEnriched +
                ", iccat='" + iccat + '\'' +
                ", iccatEnriched=" + iccatEnriched +
                ", gfcm='" + gfcm + '\'' +
                ", gfcmEnriched=" + gfcmEnriched +
                ", contactPersons=" + contactPersons +
                '}';
    }
}
