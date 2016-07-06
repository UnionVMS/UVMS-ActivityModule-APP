/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;

@Entity
@Table(name = "mdr_cr_landing_indicator_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)	
public class CrLandingIndicatorCodes extends ExtendedMasterDataRegistry {

	private static final long serialVersionUID = 1L;

	@Override
	public String getAcronym() {		
		return "CR_LAND_IND";
	}

	@Override
	public void populate(List<FieldType> fields) {
		// TODO Auto-generated method stub

	}

}