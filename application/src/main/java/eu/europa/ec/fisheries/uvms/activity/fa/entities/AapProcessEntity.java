/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "activity_aap_process")
@Data
@EqualsAndHashCode(exclude = {"aapProducts", "aapProcessCode"})
@ToString(exclude = {"aapProducts", "aapProcessCode"})
@NoArgsConstructor
public class AapProcessEntity implements Serializable {

	@Id
	@Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "aap_process_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

	@Column(name = "conversion_factor")
	private Double conversionFactor;
	
	@OneToMany(mappedBy = "aapProcess", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<AapProductEntity> aapProducts = new HashSet<>();

	@OneToMany(mappedBy = "aapProcess", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<AapProcessCodeEntity> aapProcessCode = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fa_catch_id")
	private FaCatchEntity faCatch;

	public void addAapProducts(AapProductEntity aapProductEntity){
		aapProducts.add(aapProductEntity);
		aapProductEntity.setAapProcess(this);
	}

	public void addProcessCode(AapProcessCodeEntity aapProcessCodeEntity){
		aapProcessCode.add(aapProcessCodeEntity);
		aapProcessCodeEntity.setAapProcess(this);
	}

}
