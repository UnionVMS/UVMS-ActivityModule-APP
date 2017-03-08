/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.fa.entities;



import java.io.Serializable;

/**
 * Created by sanera on 26/01/2017.
 */
//@Entity
public class FaCatchSummaryCustomEntity implements Serializable {


    private String day;
    private String month;
    private String year;
    private String vesselTransportGuid;
    private String flagState;
    private String gearType;
    private String presentation;
    private String territory;
    private String faoArea;
    private String icesStatRectangle;
    private String effortZone;
    private String rfmo;
    private String gfcmGsa;
    private String gfcmStatRectangle;
    private String fishClass;
    private String species;
    private String typeCode;
    private double count;

    public FaCatchSummaryCustomEntity(){

    }


    public FaCatchSummaryCustomEntity(String day, String month, String year, String vesselTransportGuid, String flagState, String gearType, String presentation, String icesStatRectangle, String territory, String faoArea, String effortZone, String rfmo, String gfcmGsa, String gfcmStatRectangle, String fishClass, String species, String typeCode, double count) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.vesselTransportGuid = vesselTransportGuid;
        this.flagState = flagState;
        this.gearType = gearType;
        this.presentation = presentation;
        this.icesStatRectangle = icesStatRectangle;
        this.territory = territory;
        this.faoArea = faoArea;
        this.effortZone = effortZone;
        this.rfmo = rfmo;
        this.gfcmGsa = gfcmGsa;
        this.gfcmStatRectangle = gfcmStatRectangle;
        this.fishClass = fishClass;
        this.species = species;
        this.typeCode = typeCode;
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FaCatchSummaryCustomEntity)) return false;

        FaCatchSummaryCustomEntity that = (FaCatchSummaryCustomEntity) o;

        if (getDay() != null ? !getDay().equals(that.getDay()) : that.getDay() != null) return false;
        if (getMonth() != null ? !getMonth().equals(that.getMonth()) : that.getMonth() != null) return false;
        if (getYear() != null ? !getYear().equals(that.getYear()) : that.getYear() != null) return false;
        if (getVesselTransportGuid() != null ? !getVesselTransportGuid().equals(that.getVesselTransportGuid()) : that.getVesselTransportGuid() != null)
            return false;
        if (getFlagState() != null ? !getFlagState().equals(that.getFlagState()) : that.getFlagState() != null)
            return false;
        if (getGearType() != null ? !getGearType().equals(that.getGearType()) : that.getGearType() != null)
            return false;
        if (getPresentation() != null ? !getPresentation().equals(that.getPresentation()) : that.getPresentation() != null)
            return false;
        if (getTerritory() != null ? !getTerritory().equals(that.getTerritory()) : that.getTerritory() != null)
            return false;
        if (getFaoArea() != null ? !getFaoArea().equals(that.getFaoArea()) : that.getFaoArea() != null) return false;
        if (getIcesStatRectangle() != null ? !getIcesStatRectangle().equals(that.getIcesStatRectangle()) : that.getIcesStatRectangle() != null)
            return false;
        if (getEffortZone() != null ? !getEffortZone().equals(that.getEffortZone()) : that.getEffortZone() != null)
            return false;
        if (getRfmo() != null ? !getRfmo().equals(that.getRfmo()) : that.getRfmo() != null) return false;
        if (getGfcmGsa() != null ? !getGfcmGsa().equals(that.getGfcmGsa()) : that.getGfcmGsa() != null) return false;
        return getGfcmStatRectangle() != null ? getGfcmStatRectangle().equals(that.getGfcmStatRectangle()) : that.getGfcmStatRectangle() == null;

    }

    @Override
    public int hashCode() {
        int result = getDay() != null ? getDay().hashCode() : 0;
        result = 31 * result + (getMonth() != null ? getMonth().hashCode() : 0);
        result = 31 * result + (getYear() != null ? getYear().hashCode() : 0);
        result = 31 * result + (getVesselTransportGuid() != null ? getVesselTransportGuid().hashCode() : 0);
        result = 31 * result + (getFlagState() != null ? getFlagState().hashCode() : 0);
        result = 31 * result + (getGearType() != null ? getGearType().hashCode() : 0);
        result = 31 * result + (getPresentation() != null ? getPresentation().hashCode() : 0);
        result = 31 * result + (getTerritory() != null ? getTerritory().hashCode() : 0);
        result = 31 * result + (getFaoArea() != null ? getFaoArea().hashCode() : 0);
        result = 31 * result + (getIcesStatRectangle() != null ? getIcesStatRectangle().hashCode() : 0);
        result = 31 * result + (getEffortZone() != null ? getEffortZone().hashCode() : 0);
        result = 31 * result + (getRfmo() != null ? getRfmo().hashCode() : 0);
        result = 31 * result + (getGfcmGsa() != null ? getGfcmGsa().hashCode() : 0);
        result = 31 * result + (getGfcmStatRectangle() != null ? getGfcmStatRectangle().hashCode() : 0);
        return result;
    }




    public String getVesselTransportGuid() {
        return vesselTransportGuid;
    }

    public void setVesselTransportGuid(String vesselTransportGuid) {
        this.vesselTransportGuid = vesselTransportGuid;
    }

    public String getDay() {

        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }



    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }



    public String getFishClass() {
        return fishClass;
    }

    public void setFishClass(String fishClass) {
        this.fishClass = fishClass;
    }

    public String getFlagState() {
        return flagState;
    }

    public void setFlagState(String flagState) {
        this.flagState = flagState;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getGearType() {
        return gearType;
    }

    public void setGearType(String gearType) {
        this.gearType = gearType;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public String getTerritory() {
        return territory;
    }

    public void setTerritory(String territory) {
        this.territory = territory;
    }

    public String getFaoArea() {
        return faoArea;
    }

    public void setFaoArea(String faoArea) {
        this.faoArea = faoArea;
    }

    public String getIcesStatRectangle() {
        return icesStatRectangle;
    }

    public void setIcesStatRectangle(String icesStatRectangle) {
        this.icesStatRectangle = icesStatRectangle;
    }

    public String getEffortZone() {
        return effortZone;
    }

    public void setEffortZone(String effortZone) {
        this.effortZone = effortZone;
    }

    public String getRfmo() {
        return rfmo;
    }

    public void setRfmo(String rfmo) {
        this.rfmo = rfmo;
    }

    public String getGfcmGsa() {
        return gfcmGsa;
    }

    public void setGfcmGsa(String gfcmGsa) {
        this.gfcmGsa = gfcmGsa;
    }

    public String getGfcmStatRectangle() {
        return gfcmStatRectangle;
    }

    public void setGfcmStatRectangle(String gfcmStatRectangle) {
        this.gfcmStatRectangle = gfcmStatRectangle;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @Override
    public String toString() {
        return "FaCatchSummaryCustomEntity{" +
                "day=" + day +
                ", month='" + month + '\'' +
                ", year=" + year +
                ", flagState='" + flagState + '\'' +
                ", gearType='" + gearType + '\'' +
                ", presentation='" + presentation + '\'' +
                ", territory='" + territory + '\'' +
                ", faoArea='" + faoArea + '\'' +
                ", icesStatRectangle='" + icesStatRectangle + '\'' +
                ", effortZone='" + effortZone + '\'' +
                ", rfmo='" + rfmo + '\'' +
                ", gfcmGsa='" + gfcmGsa + '\'' +
                ", gfcmStatRectangle='" + gfcmStatRectangle + '\'' +
                ", fishClass='" + fishClass + '\'' +
                ", species='" + species + '\'' +
                ", typeCode='" + typeCode + '\'' +
                ", count=" + count +
                '}';
    }
}
