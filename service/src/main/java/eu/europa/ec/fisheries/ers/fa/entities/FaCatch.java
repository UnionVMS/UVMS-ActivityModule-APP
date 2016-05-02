package eu.europa.ec.fisheries.ers.fa.entities;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "activity_fa_catch", uniqueConstraints = {
		@UniqueConstraint(columnNames = "fishing_trip_id"),
		@UniqueConstraint(columnNames = "size_distribution_id") })
public class FaCatch {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_activity_id")
	private FishingActivity fishingActivity;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "size_distribution_id", nullable = false)
	private SizeDistribution sizeDistribution;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_trip_id", nullable = true)
	private FishingTrip fishingTrip;
	
	
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
	
	@Column(name = "aap_stock_type")
	private String aapStockType;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch")
	private Set<AapProcess> aapProcesses = new HashSet<AapProcess>(0);
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch")
	private Set<FishingGear> fishingGears = new HashSet<FishingGear>(0);
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch")
	private Set<FluxLocation> fluxLocations = new HashSet<FluxLocation>(0);

	public FaCatch() {
	}

	public FaCatch(int id, SizeDistribution sizeDistribution, String typeCode,
			String typeCodeListId, String speciesCode, String speciesCodeListid) {
		this.id = id;
		this.sizeDistribution = sizeDistribution;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.speciesCode = speciesCode;
		this.speciesCodeListid = speciesCodeListid;
	}

	public FaCatch(int id, FishingActivity fishingActivity,
			SizeDistribution sizeDistribution, FishingTrip fishingTrip,
			String typeCode, String typeCodeListId, String speciesCode,
			String speciesCodeListid, Long unitQuantity, Double weightMeasure,
			String weightMeasureUnitCode, String weightMeasureListId,
			String usageCode, String usageCodeListId, String weighingMeansCode,
			String weighingMeansCodeListId, String aapStockType,
			Set<AapProcess> aapProcesses, Set<FishingGear> fishingGears,
			Set<FluxLocation> fluxLocations) {
		this.id = id;
		this.fishingActivity = fishingActivity;
		this.sizeDistribution = sizeDistribution;
		this.fishingTrip = fishingTrip;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.speciesCode = speciesCode;
		this.speciesCodeListid = speciesCodeListid;
		this.unitQuantity = unitQuantity;
		this.weightMeasure = weightMeasure;
		this.weightMeasureUnitCode = weightMeasureUnitCode;
		this.weightMeasureListId = weightMeasureListId;
		this.usageCode = usageCode;
		this.usageCodeListId = usageCodeListId;
		this.weighingMeansCode = weighingMeansCode;
		this.weighingMeansCodeListId = weighingMeansCodeListId;
		this.aapStockType = aapStockType;
		this.aapProcesses = aapProcesses;
		this.fishingGears = fishingGears;
		this.fluxLocations = fluxLocations;
	}

	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public FishingActivity getFishingActivity() {
		return this.fishingActivity;
	}

	public void setFishingActivity(FishingActivity fishingActivity) {
		this.fishingActivity = fishingActivity;
	}

	
	public SizeDistribution getSizeDistribution() {
		return this.sizeDistribution;
	}

	public void setSizeDistribution(SizeDistribution sizeDistribution) {
		this.sizeDistribution = sizeDistribution;
	}

	
	public FishingTrip getFishingTrip() {
		return this.fishingTrip;
	}

	public void setFishingTrip(FishingTrip fishingTrip) {
		this.fishingTrip = fishingTrip;
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

	
	public String getAapStockType() {
		return this.aapStockType;
	}

	public void setAapStockType(String aapStockType) {
		this.aapStockType = aapStockType;
	}

	
	public Set<AapProcess> getAapProcesses() {
		return this.aapProcesses;
	}

	public void setAapProcesses(Set<AapProcess> aapProcesses) {
		this.aapProcesses = aapProcesses;
	}

	
	public Set<FishingGear> getFishingGears() {
		return this.fishingGears;
	}

	public void setFishingGears(Set<FishingGear> fishingGears) {
		this.fishingGears = fishingGears;
	}

	
	public Set<FluxLocation> getFluxLocations() {
		return this.fluxLocations;
	}

	public void setFluxLocations(Set<FluxLocation> fluxLocations) {
		this.fluxLocations = fluxLocations;
	}

}
