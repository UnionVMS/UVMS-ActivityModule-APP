/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activity_aap_product")
@Data
@NoArgsConstructor
public class AapProductEntity implements Serializable {

	@Id
	@Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "aap_product_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

	@Column(name = "packaging_type_code")
	private String packagingTypeCode;
	
	@Column(name = "packaging_type_code_list_id")
	private String packagingTypeCodeListId;
	
	@Column(name = "packaging_unit_avarage_weight", precision = 17, scale = 17)
	private Double packagingUnitAvarageWeight;

	@Column(name = "packaging_weight_unit_code")
	private String packagingWeightUnitCode;

	@Column(name = "calculated_packaging_weight")
	private Double calculatedPackagingWeight;
	
	@Column(name = "packaging_unit_count", precision = 17, scale = 17)
	private Double packagingUnitCount;

	@Column(name = "packaging_unit_count_code")
	private String packagingUnitCountCode;

	@Column(name = "calculated_packaging_unit_count")
	private Double calculatedPackagingUnitCount;

	@Column(name = "species_code")
	private String speciesCode;

	@Column(name = "spacies_code_list_id")
	private String speciesCodeListId;

	@Column(name = "unit_quantity")
	private Double unitQuantity;

	@Column(name = "unit_quantity_code")
	private String unitQuantityCode;

	@Column(name = "calculated_unit_quantity")
	private Double calculatedUnitQuantity;

	@Column(name = "weight_measure")
	private Double weightMeasure;

	@Column(name = "weight_measure_unit_code")
	private String weightMeasureUnitCode;

	@Column(name = "calculated_weight_measure")
	private Double calculatedWeightMeasure;

	@Column(name = "weighing_means_code")
	private String weighingMeansCode;

	@Column(name = "weighting_means_code_list_id")
	private String weighingMeansCodeListId;

	@Column(name = "usage_code")
	private String usageCode;

	@Column(name = "usage_code_list_id")
	private String usageCodeListId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "aap_process_id")
	private AapProcessEntity aapProcess;

}