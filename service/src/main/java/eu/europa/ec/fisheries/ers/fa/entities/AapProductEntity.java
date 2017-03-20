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
@Table(name = "activity_aap_product")
public class AapProductEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name="SEQ_GEN", sequenceName="aap_product_seq", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
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

	public AapProductEntity() {
		super();
	}

	public int getId() {
		return this.id;
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

	public String getSpeciesCode() {
		return speciesCode;
	}

	public void setSpeciesCode(String speciesCode) {
		this.speciesCode = speciesCode;
	}

	public String getSpeciesCodeListId() {
		return speciesCodeListId;
	}

	public void setSpeciesCodeListId(String speciesCodeListId) {
		this.speciesCodeListId = speciesCodeListId;
	}

	public Double getUnitQuantity() {
		return unitQuantity;
	}

	public void setUnitQuantity(Double unitQuantity) {
		this.unitQuantity = unitQuantity;
	}

	public Double getWeightMeasure() {
		return weightMeasure;
	}

	public void setWeightMeasure(Double weightMeasure) {
		this.weightMeasure = weightMeasure;
	}

	public String getWeighingMeansCode() {
		return weighingMeansCode;
	}

	public void setWeighingMeansCode(String weighingMeansCode) {
		this.weighingMeansCode = weighingMeansCode;
	}

	public String getWeighingMeansCodeListId() {
		return weighingMeansCodeListId;
	}

	public void setWeighingMeansCodeListId(String weighingMeansCodeListId) {
		this.weighingMeansCodeListId = weighingMeansCodeListId;
	}

	public String getUsageCode() {
		return usageCode;
	}

	public void setUsageCode(String usageCode) {
		this.usageCode = usageCode;
	}

	public String getUsageCodeListId() {
		return usageCodeListId;
	}

	public void setUsageCodeListId(String usageCodeListId) {
		this.usageCodeListId = usageCodeListId;
	}

	public String getPackagingWeightUnitCode() {
		return packagingWeightUnitCode;
	}

	public void setPackagingWeightUnitCode(String packagingWeightUnitCode) {
		this.packagingWeightUnitCode = packagingWeightUnitCode;
	}

	public Double getCalculatedPackagingWeight() {
		return calculatedPackagingWeight;
	}

	public void setCalculatedPackagingWeight(Double calculatedPackagingWeight) {
		this.calculatedPackagingWeight = calculatedPackagingWeight;
	}

	public String getWeightMeasureUnitCode() {
		return weightMeasureUnitCode;
	}

	public void setWeightMeasureUnitCode(String weightMeasureUnitCode) {
		this.weightMeasureUnitCode = weightMeasureUnitCode;
	}

	public Double getCalculatedWeightMeasure() {
		return calculatedWeightMeasure;
	}

	public void setCalculatedWeightMeasure(Double calculatedWeightMeasure) {
		this.calculatedWeightMeasure = calculatedWeightMeasure;
	}

	public String getPackagingUnitCountCode() {
		return packagingUnitCountCode;
	}

	public void setPackagingUnitCountCode(String packagingUnitCountCode) {
		this.packagingUnitCountCode = packagingUnitCountCode;
	}

	public Double getCalculatedPackagingUnitCount() {
		return calculatedPackagingUnitCount;
	}

	public void setCalculatedPackagingUnitCount(Double calculatedPackagingUnitCount) {
		this.calculatedPackagingUnitCount = calculatedPackagingUnitCount;
	}

	public String getUnitQuantityCode() {
		return unitQuantityCode;
	}

	public void setUnitQuantityCode(String unitQuantityCode) {
		this.unitQuantityCode = unitQuantityCode;
	}

	public Double getCalculatedUnitQuantity() {
		return calculatedUnitQuantity;
	}

	public void setCalculatedUnitQuantity(Double calculatedUnitQuantity) {
		this.calculatedUnitQuantity = calculatedUnitQuantity;
	}
}