/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.fa.dao.proxy;

import lombok.EqualsAndHashCode;

/**
 * This class do not consider PRESENTATION value in equals in Hashcode.
 * For landing screen we want to calculate values for PRESENTATION separately so, we want to omit it from equals and hashCode
 * Created by sanera on 03/03/2017.
 */
@EqualsAndHashCode(exclude = {"count", "typeCode", "species", "fishClass", "presentation"})
public class FaCatchSummaryCustomChildProxy extends FaCatchSummaryCustomProxy {

    @Override
    public void setVesselTransportGuid(String vesselTransportGuid) { //NOSONAR
        super.setVesselTransportGuid(vesselTransportGuid);
    }

    @Override
    public void setDay(String day) { //NOSONAR
        super.setDay(day);
    }

    @Override
    public void setMonth(String month) { //NOSONAR
        super.setMonth(month);
    }

    @Override
    public void setYear(String year) { //NOSONAR
        super.setYear(year);
    }

    @Override
    public void setCount(double count) { //NOSONAR
        super.setCount(count);
    }

    @Override
    public void setFishClass(String fishClass) {  //NOSONAR
        super.setFishClass(fishClass);
    }

    @Override
    public void setFlagState(String flagState) {  //NOSONAR
        super.setFlagState(flagState);
    }

    @Override
    public void setSpecies(String species) { //NOSONAR
        super.setSpecies(species);
    }

    @Override
    public void setGearType(String gearType) { //NOSONAR
        super.setGearType(gearType);
    }

    @Override
    public void setPresentation(String presentation) { //NOSONAR
        super.setPresentation(presentation);
    }

    @Override
    public void setTerritory(String territory) { //NOSONAR
        super.setTerritory(territory);
    }

    @Override
    public void setFaoArea(String faoArea) { //NOSONAR
        super.setFaoArea(faoArea);
    }

    @Override
    public void setIcesStatRectangle(String icesStatRectangle) { //NOSONAR
        super.setIcesStatRectangle(icesStatRectangle);
    }

    @Override
    public void setEffortZone(String effortZone) { //NOSONAR
        super.setEffortZone(effortZone);
    }

    @Override
    public void setRfmo(String rfmo) { //NOSONAR
        super.setRfmo(rfmo);
    }

    @Override
    public void setGfcmGsa(String gfcmGsa) { //NOSONAR
        super.setGfcmGsa(gfcmGsa);
    }

    @Override
    public void setGfcmStatRectangle(String gfcmStatRectangle) { //NOSONAR
        super.setGfcmStatRectangle(gfcmStatRectangle);
    }

    @Override
    public void setTypeCode(String typeCode) { //NOSONAR
        super.setTypeCode(typeCode);
    }

    @Override
    public String toString() { //NOSONAR
        return super.toString();
    }

}
