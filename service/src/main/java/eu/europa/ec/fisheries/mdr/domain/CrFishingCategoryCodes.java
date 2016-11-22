/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain;

import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.exception.FieldNotMappedException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import un.unece.uncefact.data.standard.response.MDRDataNodeType;
import un.unece.uncefact.data.standard.response.MDRElementDataNodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "mdr_cr_fishing_category_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CrFishingCategoryCodes extends MasterDataRegistry {
    private static final long serialVersionUID = 1L;

    @Column(name = "sovereignty_waters_report_declaration")
    private String sovereigntyWatersReportDeclaration;

    public String getSovereigntyWatersReportDeclaration() {
        return sovereigntyWatersReportDeclaration;
    }

    public void setSovereigntyWatersReportDeclaration(String sovereigntyWatersReportDeclaration) {
        this.sovereigntyWatersReportDeclaration = sovereigntyWatersReportDeclaration;
    }

    @Override
    public String getAcronym() {
        return "CR_FISH_CATEGORY";
    }

    @Override
    public void populate(MDRDataNodeType mdrDataType) throws FieldNotMappedException {
        populateCommonFields(mdrDataType);
        for(MDRElementDataNodeType field : mdrDataType.getSubordinateMDRElementDataNodes()){
            String fieldName  = field.getName().getValue();
            String fieldValue  = field.getName().getValue();
            if (StringUtils.equalsIgnoreCase("sovereigntyWatersReportDeclaration", fieldName)) {
                this.setSovereigntyWatersReportDeclaration(fieldValue);
            } else {
                throw new FieldNotMappedException(getClass().getSimpleName(), fieldName);
            }
        }
    }

}