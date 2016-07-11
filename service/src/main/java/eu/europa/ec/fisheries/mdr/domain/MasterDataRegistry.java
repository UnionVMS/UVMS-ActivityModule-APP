/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain;

import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.domain.Audit;
import eu.europa.ec.fisheries.uvms.domain.BaseEntity;
import eu.europa.ec.fisheries.uvms.domain.CharBooleanConverter;
import eu.europa.ec.fisheries.uvms.domain.DateRange;
import xeu.ec.fisheries.flux_bl.flux_aggregatebusinessinformationentity._1._1.FLUXPeriodType;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.CodeElementType;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

@SuppressWarnings("serial")
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
@ToString
abstract public class MasterDataRegistry extends BaseEntity {

	@Convert(converter = CharBooleanConverter.class)
    @Column(nullable = true, length = 1)
    private Boolean refreshable;

    @Embedded
    private Audit audit;
    
    @Embedded
    private DateRange validity;
    
    @PrePersist
    private void onCreate() {
        audit = new Audit(DateUtils.nowUTC().toDate());
    }

    public Boolean getRefreshable() {
        return refreshable;
    }

    public void setRefreshable(Boolean refreshable) {
        this.refreshable = refreshable;
    }

    public Audit getAudit() {
        return audit;
    }
    
    // Manual creation of audit; TODO : If not needed anymore take this mathod off.
    public void createAudit(){
    	this.onCreate();
    }

	public DateRange getValidity() {
		return validity;
	}

	public void setValidity(DateRange validity) {
		this.validity = validity;
	}
	
	public abstract String getAcronym();
	
	public void populateFromJAXBFields(CodeElementType actualJaxbElement){
		 	FLUXPeriodType jaxbValidity = actualJaxbElement.getValidityPeriod();
		    if(jaxbValidity != null){
		    	this.setValidity(new DateRange(jaxbValidity.getStartDate().toGregorianCalendar().getTime(), jaxbValidity.getEndDate().toGregorianCalendar().getTime()));
		    }
		    populate(actualJaxbElement.getFields());
	}

	public abstract void populate(List<FieldType> fields);
    
}