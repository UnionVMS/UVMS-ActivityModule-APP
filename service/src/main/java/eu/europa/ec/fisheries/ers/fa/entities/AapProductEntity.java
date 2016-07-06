/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "activity_aap_product")
public class AapProductEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "aap_process_id")
	private AapProcessEntity aapProcess;
	
	@Column(name = "packaging_type_code")
	private String packagingTypeCode;
	
	@Column(name = "packaging_type_code_list_id")
	private String packagingTypeCodeListId;
	
	@Column(name = "packaging_unit_avarage_weight", precision = 17, scale = 17)
	private Double packagingUnitAvarageWeight;
	
	@Column(name = "packaging_unit_count", precision = 17, scale = 17)
	private Double packagingUnitCount;

	public AapProductEntity() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public AapProcessEntity getAapProcess() {
		return this.aapProcess;
	}

	public void setAapProcess(AapProcessEntity aapProcess) {
		this.aapProcess = aapProcess;
	}


	public String getPackagingTypeCode() {
		return this.packagingTypeCode;
	}

	public void setPackagingTypeCode(String packagingTypeCode) {
		this.packagingTypeCode = packagingTypeCode;
	}

	
	public String getPackagingTypeCodeListId() {
		return this.packagingTypeCodeListId;
	}

	public void setPackagingTypeCodeListId(String packagingTypeCodeListId) {
		this.packagingTypeCodeListId = packagingTypeCodeListId;
	}

	
	public Double getPackagingUnitAvarageWeight() {
		return this.packagingUnitAvarageWeight;
	}

	public void setPackagingUnitAvarageWeight(Double packagingUnitAvarageWeight) {
		this.packagingUnitAvarageWeight = packagingUnitAvarageWeight;
	}

	
	public Double getPackagingUnitCount() {
		return this.packagingUnitCount;
	}

	public void setPackagingUnitCount(Double packagingUnitCount) {
		this.packagingUnitCount = packagingUnitCount;
	}

}