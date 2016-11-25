/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain.codelists;

import eu.europa.ec.fisheries.mdr.domain.codelists.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.*;

/**
 * Created by kovian on 11/23/2016.
 */
@Entity
@Table(name = "mdr_effort_zone")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Indexed
public class EffortZone extends MasterDataRegistry {
    private static final long serialVersionUID = 1L;

    @Column(name = "legal_reference")
    @Field(name="legalReference", analyze= Analyze.NO, store = Store.YES)
    private String legalReference;

    @Override
    public String getAcronym() {
        return "EFFORT_ZONE";
    }

    @Override
    public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
        populateCommonFields(mdrDataType);
        for(MDRElementDataNodeType field : mdrDataType.getSubordinateMDRElementDataNodes()){
            String fieldName  = field.getName().getValue();
            String fieldValue  = field.getName().getValue();
            if(StringUtils.equalsIgnoreCase("LEGALREF", fieldName)){
                this.setLegalReference(fieldValue);
            }  else {
                throw new FieldNotMappedException(this.getClass().getSimpleName(), fieldName);
            }
        }
    }


    public String getLegalReference() {
        return legalReference;
    }
    public void setLegalReference(String legalReference) {
        this.legalReference = legalReference;
    }


}