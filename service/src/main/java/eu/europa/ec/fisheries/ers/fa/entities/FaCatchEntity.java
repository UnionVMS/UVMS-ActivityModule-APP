/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "activity_fa_catch")
public class FaCatchEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_activity_id")
	private FishingActivityEntity fishingActivity;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "size_distribution_id", nullable = false)
	private SizeDistributionEntity sizeDistribution;

	@Column(name = "type_code", nullable = false)
	private String typeCode;

	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;

	@Column(name = "species_code", nullable = false)
	private String speciesCode;

	@Column(name = "species_code_listid", nullable = false)
	private String speciesCodeListid;

	@Column(name = "unit_quantity")
	private Long unitQuantity;

	@Column(name = "weight_measure", precision = 17, scale = 17)
	private Double weightMeasure;

	@Column(name = "weight_measure_unit_code")
	private String weightMeasureUnitCode;

	@Column(name = "weight_measure_list_id")
	private String weightMeasureListId;

	@Column(name = "usage_code")
	private String usageCode;

	@Column(name = "usage_code_list_id")
	private String usageCodeListId;

	@Column(name = "weighing_means_code")
	private String weighingMeansCode;

	@Column(name = "weighing_means_code_list_id")
	private String weighingMeansCodeListId;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<AapProcessEntity> aapProcesses = new HashSet<AapProcessEntity>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<FishingGearEntity> fishingGears = new HashSet<FishingGearEntity>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<FluxLocationEntity> fluxLocations = new HashSet<FluxLocationEntity>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<FluxCharacteristicEntity> fluxCharacteristics = new HashSet<FluxCharacteristicEntity>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<AapStockEntity> appStocks = new HashSet<AapStockEntity>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<FishingTripEntity> fishingTrips = new HashSet<FishingTripEntity>(0);

	public FaCatchEntity() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public FishingActivityEntity getFishingActivity() {
		return this.fishingActivity;
	}

	public void setFishingActivity(
			FishingActivityEntity fishingActivity) {
		this.fishingActivity = fishingActivity;
	}

	public SizeDistributionEntity getSizeDistribution() {
		return this.sizeDistribution;
	}

	public void setSizeDistribution(
			SizeDistributionEntity sizeDistribution) {
		this.sizeDistribution = sizeDistribution;
	}

	public String getTypeCode() {
		return this.typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeCodeListId() {
		return this.typeCodeListId;
	}

	public void setTypeCodeListId(String typeCodeListId) {
		this.typeCodeListId = typeCodeListId;
	}

	public String getSpeciesCode() {
		return this.speciesCode;
	}

	public void setSpeciesCode(String speciesCode) {
		this.speciesCode = speciesCode;
	}

	public String getSpeciesCodeListid() {
		return this.speciesCodeListid;
	}

	public void setSpeciesCodeListid(String speciesCodeListid) {
		this.speciesCodeListid = speciesCodeListid;
	}

	public Long getUnitQuantity() {
		return this.unitQuantity;
	}

	public void setUnitQuantity(Long unitQuantity) {
		this.unitQuantity = unitQuantity;
	}

	public Double getWeightMeasure() {
		return this.weightMeasure;
	}

	public void setWeightMeasure(Double weightMeasure) {
		this.weightMeasure = weightMeasure;
	}

	public String getWeightMeasureUnitCode() {
		return this.weightMeasureUnitCode;
	}

	public void setWeightMeasureUnitCode(String weightMeasureUnitCode) {
		this.weightMeasureUnitCode = weightMeasureUnitCode;
	}

	public String getWeightMeasureListId() {
		return this.weightMeasureListId;
	}

	public void setWeightMeasureListId(String weightMeasureListId) {
		this.weightMeasureListId = weightMeasureListId;
	}

	public String getUsageCode() {
		return this.usageCode;
	}

	public void setUsageCode(String usageCode) {
		this.usageCode = usageCode;
	}

	public String getUsageCodeListId() {
		return this.usageCodeListId;
	}

	public void setUsageCodeListId(String usageCodeListId) {
		this.usageCodeListId = usageCodeListId;
	}

	public String getWeighingMeansCode() {
		return this.weighingMeansCode;
	}

	public void setWeighingMeansCode(String weighingMeansCode) {
		this.weighingMeansCode = weighingMeansCode;
	}

	public String getWeighingMeansCodeListId() {
		return this.weighingMeansCodeListId;
	}

	public void setWeighingMeansCodeListId(String weighingMeansCodeListId) {
		this.weighingMeansCodeListId = weighingMeansCodeListId;
	}

	public Set<AapProcessEntity> getAapProcesses() {
		return this.aapProcesses;
	}

	public void setAapProcesses(
			Set<AapProcessEntity> aapProcesses) {
		this.aapProcesses = aapProcesses;
	}

	public Set<FishingGearEntity> getFishingGears() {
		return this.fishingGears;
	}

	public void setFishingGears(
			Set<FishingGearEntity> fishingGears) {
		this.fishingGears = fishingGears;
	}

	public Set<FluxLocationEntity> getFluxLocations() {
		return this.fluxLocations;
	}

	public void setFluxLocations(
			Set<FluxLocationEntity> fluxLocations) {
		this.fluxLocations = fluxLocations;
	}

	public Set<FluxCharacteristicEntity> getFluxCharacteristics() {
		return this.fluxCharacteristics;
	}

	public void setFluxCharacteristics(
			Set<FluxCharacteristicEntity> fluxCharacteristics) {
		this.fluxCharacteristics = fluxCharacteristics;
	}

	public Set<AapStockEntity> getAppStocks() {
		return this.appStocks;
	}

	public void setAppStocks(Set<AapStockEntity> appStocks) {
		this.appStocks = appStocks;
	}

	public Set<FishingTripEntity> getFishingTrips() {
		return this.fishingTrips;
	}

	public void setFishingTrips(
			Set<FishingTripEntity> fishingTrips) {
		this.fishingTrips = fishingTrips;
	}

}