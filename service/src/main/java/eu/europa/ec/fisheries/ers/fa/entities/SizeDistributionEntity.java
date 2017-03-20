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
import java.util.Set;

@Entity
@Table(name = "activity_size_distribution")
public class SizeDistributionEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name="SEQ_GEN", sequenceName="size_dist_seq", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
	private int id;

	@Column(name = "category_code")
	private String categoryCode;

	@Column(name = "category_code_list_id")
	private String categoryCodeListId;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "sizeDistribution")
	private FaCatchEntity faCatch;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sizeDistribution", cascade = CascadeType.ALL)
	private Set<SizeDistributionClassCodeEntity> sizeDistributionClassCode;

	public SizeDistributionEntity() {
		super();
	}

	public SizeDistributionEntity(String categoryCode, String categoryCodeListId, FaCatchEntity faCatch) {
		this.categoryCode = categoryCode;
		this.categoryCodeListId = categoryCodeListId;
		this.faCatch = faCatch;
	}

	public int getId() {
		return this.id;
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

	public Set<SizeDistributionClassCodeEntity> getSizeDistributionClassCode() {
		return sizeDistributionClassCode;
	}

	public void setSizeDistributionClassCode(Set<SizeDistributionClassCodeEntity> sizeDistributionClassCode) {
		this.sizeDistributionClassCode = sizeDistributionClassCode;
	}
}