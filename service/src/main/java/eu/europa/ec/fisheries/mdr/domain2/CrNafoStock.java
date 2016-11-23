/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain2;

import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "mdr_cr_nafo_stock")
@ToString(callSuper = true)
public class CrNafoStock extends MasterDataRegistry {

    @Column(name = "species_code")
    private String speciesCode;

    @Column(name = "species_name")
    private String speciesName;

    @Column(name = "area_code")
    private String areaCode;

    @Column(name = "area_description")
    private String areaDescription;

    public String getSpeciesCode() {
        return speciesCode;
    }
    public void setSpeciesCode(String speciesCode) {
        this.speciesCode = speciesCode;
    }
    public String getSpeciesName() {
        return speciesName;
    }
    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }
    public String getAreaCode() {
        return areaCode;
    }
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getAreaDescription() {
        return areaDescription;
    }
    public void setAreaDescription(String areaDescription) {
        this.areaDescription = areaDescription;
    }
    
	@Override
	public String getAcronym() {
		return "NAFO_STOCK";
	}

    @Override
    public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
        populateCommonFields(mdrDataType);
        for(MDRElementDataNodeType field : mdrDataType.getSubordinateMDRElementDataNodes()){
            String fieldName  = field.getName().getValue();
            String fieldValue  = field.getName().getValue();
            if(org.apache.commons.lang.StringUtils.equalsIgnoreCase("speciesCode", fieldName)){
                this.setSpeciesCode(fieldValue);
            } else if(org.apache.commons.lang.StringUtils.equalsIgnoreCase("speciesName", fieldName)){
                this.setSpeciesName(fieldValue);
            } else if(org.apache.commons.lang.StringUtils.equalsIgnoreCase("areaCode", fieldName)){
                this.setAreaCode(fieldValue);
            } else if(org.apache.commons.lang.StringUtils.equalsIgnoreCase("areaDescription", fieldName)){
                this.setAreaDescription(fieldValue);
            }  else {
                throw new FieldNotMappedException(this.getClass().getSimpleName(), fieldName);
            }
        }
    }

}