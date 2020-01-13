/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.fa.dao.proxy;

/**
 * This class do not consider PRESENTATION value in equals in Hashcode.
 * For landing screen we want to calculate values for PRESENTATION separately so, we want to omit it from equals and hashCode.
 */
public class FaCatchSummaryCustomChildProxy extends FaCatchSummaryCustomProxy {

    /**
     * DO NOT USE EqualsAndHashCode annotation by lombok here. If you use it, then java HashMap is not able to use the objects
     * of this class as keys as required. It is not able to identify objects uniquely in a correct way breaking the functionality.
     *
     * We have modified equals method to use parent class to make it work in GroupBy
     * @param o
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FaCatchSummaryCustomProxy)) return false;

        FaCatchSummaryCustomProxy that = (FaCatchSummaryCustomProxy) o;

        if (getDay() != null ? !getDay().equals(that.getDay()) : that.getDay() != null) return false;
        if (getMonth() != null ? !getMonth().equals(that.getMonth()) : that.getMonth() != null) return false;
        if (getYear() != null ? !getYear().equals(that.getYear()) : that.getYear() != null) return false;
        if (getDate() != null ? !getDate().equals(that.getDate()) : that.getDate() != null) return false;
        if (getVesselTransportGuid() != null ? !getVesselTransportGuid().equals(that.getVesselTransportGuid()) : that.getVesselTransportGuid() != null)
            return false;
        if (getFlagState() != null ? !getFlagState().equals(that.getFlagState()) : that.getFlagState() != null)
            return false;
        if (getGearType() != null ? !getGearType().equals(that.getGearType()) : that.getGearType() != null)
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
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        result = 31 * result + (getVesselTransportGuid() != null ? getVesselTransportGuid().hashCode() : 0);
        result = 31 * result + (getFlagState() != null ? getFlagState().hashCode() : 0);
        result = 31 * result + (getGearType() != null ? getGearType().hashCode() : 0);
        result = 31 * result + (getTerritory() != null ? getTerritory().hashCode() : 0);
        result = 31 * result + (getFaoArea() != null ? getFaoArea().hashCode() : 0);
        result = 31 * result + (getIcesStatRectangle() != null ? getIcesStatRectangle().hashCode() : 0);
        result = 31 * result + (getEffortZone() != null ? getEffortZone().hashCode() : 0);
        result = 31 * result + (getRfmo() != null ? getRfmo().hashCode() : 0);
        result = 31 * result + (getGfcmGsa() != null ? getGfcmGsa().hashCode() : 0);
        result = 31 * result + (getGfcmStatRectangle() != null ? getGfcmStatRectangle().hashCode() : 0);
        return result;
    }

}
