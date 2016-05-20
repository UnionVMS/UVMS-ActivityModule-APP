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

	@OneToOne(fetch = FetchType.LAZY)
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch")
	private Set<AapProcessEntity> aapProcesses = new HashSet<AapProcessEntity>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch")
	private Set<FishingGearEntity> fishingGears = new HashSet<FishingGearEntity>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch")
	private Set<FluxLocationEntity> fluxLocations = new HashSet<FluxLocationEntity>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch")
	private Set<FluxCharacteristicEntity> fluxCharacteristics = new HashSet<FluxCharacteristicEntity>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch")
	private Set<AppStockEntity> appStocks = new HashSet<AppStockEntity>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch")
	private Set<FishingTripEntity> fishingTrips = new HashSet<FishingTripEntity>(0);

	public FaCatchEntity() {
	}

	public FaCatchEntity(SizeDistributionEntity sizeDistribution, String typeCode,
						 String typeCodeListId, String speciesCode, String speciesCodeListid) {
		this.sizeDistribution = sizeDistribution;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.speciesCode = speciesCode;
		this.speciesCodeListid = speciesCodeListid;
	}

	public FaCatchEntity(FishingActivityEntity fishingActivity,
						 SizeDistributionEntity sizeDistribution, String typeCode,
						 String typeCodeListId, String speciesCode,
						 String speciesCodeListid, Long unitQuantity, Double weightMeasure,
						 String weightMeasureUnitCode, String weightMeasureListId,
						 String usageCode, String usageCodeListId, String weighingMeansCode,
						 String weighingMeansCodeListId,
						 Set<AapProcessEntity> aapProcesses,
						 Set<FishingGearEntity> fishingGears,
						 Set<FluxLocationEntity> fluxLocations,
						 Set<FluxCharacteristicEntity> fluxCharacteristics,
						 Set<AppStockEntity> appStocks,
						 Set<FishingTripEntity> fishingTrips) {
		this.fishingActivity = fishingActivity;
		this.sizeDistribution = sizeDistribution;
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
		this.aapProcesses = aapProcesses;
		this.fishingGears = fishingGears;
		this.fluxLocations = fluxLocations;
		this.fluxCharacteristics = fluxCharacteristics;
		this.appStocks = appStocks;
		this.fishingTrips = fishingTrips;
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

	public Set<AppStockEntity> getAppStocks() {
		return this.appStocks;
	}

	public void setAppStocks(Set<AppStockEntity> appStocks) {
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
