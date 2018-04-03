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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "activity_size_distribution")
@Data
@EqualsAndHashCode(exclude = "sizeDistributionClassCodeEntities")
@ToString(exclude = {"sizeDistributionClassCodeEntities", "faCatch"})
@NoArgsConstructor
@AllArgsConstructor
public class SizeDistributionEntity implements Serializable {

	@Id
	@Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "size_dist_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

	@Column(name = "category_code")
	private String categoryCode;

	@Column(name = "category_code_list_id")
	private String categoryCodeListId;

	@OneToOne(mappedBy = "sizeDistribution")
	private FaCatchEntity faCatch;

	@OneToMany(mappedBy = "sizeDistribution", cascade = CascadeType.ALL)
	private Set<SizeDistributionClassCodeEntity> sizeDistributionClassCodeEntities = new HashSet<>();

	public void addSizeDistribution(SizeDistributionClassCodeEntity classCodeEntity){
		sizeDistributionClassCodeEntities.add(classCodeEntity);
		classCodeEntity.setSizeDistribution(this);
	}
}