/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

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
@Table(name = "activity_size_distribution")
public class SizeDistributionEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "class_code", nullable = false)
	private String classCode;

	@Column(name = "class_code_list_id", nullable = false)
	private String classCodeListId;

	@Column(name = "category_code")
	private String categoryCode;

	@Column(name = "category_code_list_id")
	private String categoryCodeListId;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "sizeDistribution")
	private FaCatchEntity faCatch;

	public SizeDistributionEntity() {
		super();
	}

	public SizeDistributionEntity(int id,String classCode, String classCodeListId, String categoryCode, String categoryCodeListId, FaCatchEntity faCatch) {
                   this.id=id;
		this.classCode = classCode;
		this.classCodeListId = classCodeListId;
		this.categoryCode = categoryCode;
		this.categoryCodeListId = categoryCodeListId;
		this.faCatch = faCatch;
	}

	public int getId() {
		return this.id;
	}

	public String getClassCode() {
		return this.classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public String getClassCodeListId() {
		return this.classCodeListId;
	}

	public void setClassCodeListId(String classCodeListId) {
		this.classCodeListId = classCodeListId;
	}

	public String getCategoryCode() {
		return this.categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCategoryCodeListId() {
		return this.categoryCodeListId;
	}

	public void setCategoryCodeListId(String categoryCodeListId) {
		this.categoryCodeListId = categoryCodeListId;
	}

	public FaCatchEntity getFaCatch() {
		return faCatch;
	}

	public void setFaCatch(FaCatchEntity faCatch) {
		this.faCatch = faCatch;
	}
}