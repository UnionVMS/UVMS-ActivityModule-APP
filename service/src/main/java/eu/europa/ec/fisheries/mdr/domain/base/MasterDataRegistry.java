/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain.base;

import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import eu.europa.ec.fisheries.uvms.domain.BaseEntity;
import eu.europa.ec.fisheries.uvms.domain.DateRange;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import un.unece.uncefact.data.standard.response.DateTimeType;
import un.unece.uncefact.data.standard.response.DelimitedPeriodType;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import java.util.List;

@SuppressWarnings("serial")
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
@ToString
abstract public class MasterDataRegistry extends BaseEntity {

    @Embedded
    private DateRange validity;

    public void populateFromJAXBFields(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
        final DelimitedPeriodType validityPeriod = mdrDataType.getEffectiveDelimitedPeriod();
        final DateTimeType startDateTime = validityPeriod.getStartDateTime();
        final DateTimeType endDateTime = validityPeriod.getEndDateTime();
        if (validityPeriod != null) {
            this.setValidity(new DateRange(startDateTime.getDateTime().toGregorianCalendar().getTime(),
                    endDateTime.getDateTime().toGregorianCalendar().getTime()));
        }
        populate(mdrDataType.getSubordinateMDRElementDataNodes());
    }

    public void populate(List<MDRElementDataNodeType> fields) throws FieldNotMappedException {};

    public abstract String getAcronym();

    public DateRange getValidity() {
        return validity;
    }
    public void setValidity(DateRange validity) {
        this.validity = validity;
    }

}