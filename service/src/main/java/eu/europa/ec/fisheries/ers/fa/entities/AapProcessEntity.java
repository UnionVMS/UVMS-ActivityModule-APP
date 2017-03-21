/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "activity_aap_process")
public class AapProcessEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "aap_process_seq" , allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fa_catch_id")
	private FaCatchEntity faCatch;
	
	@Column(name = "conversion_factor")
	private Integer conversionFactor;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aapProcess", cascade = CascadeType.ALL)
	private Set<AapProductEntity> aapProducts;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aapProcess", cascade = CascadeType.ALL)
	private Set<AapProcessCodeEntity> aapProcessCode;

	public AapProcessEntity() {
		super();
	}

	public int getId() {
		return this.id;
	}


	public FaCatchEntity getFaCatch() {
		return this.faCatch;
	}

	public void setFaCatch(FaCatchEntity faCatch) {
		this.faCatch = faCatch;
	}

	public Integer getConversionFactor() {
		return this.conversionFactor;
	}

	public void setConversionFactor(Integer conversionFactor) {
		this.conversionFactor = conversionFactor;
	}

	
	public Set<AapProductEntity> getAapProducts() {
		return this.aapProducts;
	}

	public void setAapProducts(
			Set<AapProductEntity> aapProducts) {
		this.aapProducts = aapProducts;
	}

	public Set<AapProcessCodeEntity> getAapProcessCode() {
		return aapProcessCode;
	}

	public void setAapProcessCode(Set<AapProcessCodeEntity> aapProcessCode) {
		this.aapProcessCode = aapProcessCode;
	}
}