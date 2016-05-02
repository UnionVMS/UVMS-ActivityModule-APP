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
@Table(name = "activity_flux_location", uniqueConstraints = {
		@UniqueConstraint(columnNames = "physical_structured_address_id"),
		@UniqueConstraint(columnNames = "gear_problem_id") })
public class FluxLocation {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gear_problem_id", nullable = true)
	private GearProblem gearProblem;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fa_catch_id")
	private FaCatch faCatch;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "physical_structured_address_id", nullable = true)
	private StructuredAddress structuredAddress;
	
	@Column(name = "fishing_activity_id")
	private Long fishingActivityId;
	
	@Column(name = "type_code", nullable = false)
	private String typeCode;
	
	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;
	
	@Column(name = "country_id")
	private String countryId;
	
	@Column(name = "country_id_list_id")
	private String countryIdListId;
	
	@Column(name = "flux_id")
	private String fluxId;
	
	@Column(name = "flux_list_id")
	private String fluxListId;
	
	@Column(columnDefinition = "text", name = "rfmo_code")
	private String rfmoCode;
	
	@Column(name = "longitude", precision = 17, scale = 17)
	private Double longitude;
	
	@Column(name = "latitude", precision = 17, scale = 17)
	private Double latitude;
	
	@Column(name = "flux_location_type", nullable = false)
	private String fluxLocationType;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fluxLocation")
	private Set<FluxCharacteristic> fluxChacteristics = new HashSet<FluxCharacteristic>(
			0);

	public FluxLocation() {
	}

	public FluxLocation(int id, String typeCode, String typeCodeListId,
			String fluxLocationType) {
		this.id = id;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.fluxLocationType = fluxLocationType;
	}

	public FluxLocation(int id, GearProblem gearProblem, FaCatch faCatch,
			StructuredAddress structuredAddress, Long fishingActivityId,
			String typeCode, String typeCodeListId, String countryId,
			String countryIdListId, String fluxId, String fluxListId,
			String rfmoCode, Double longitude, Double latitude,
			String fluxLocationType, Set<FluxCharacteristic> fluxChacteristics) {
		this.id = id;
		this.gearProblem = gearProblem;
		this.faCatch = faCatch;
		this.structuredAddress = structuredAddress;
		this.fishingActivityId = fishingActivityId;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.countryId = countryId;
		this.countryIdListId = countryIdListId;
		this.fluxId = fluxId;
		this.fluxListId = fluxListId;
		this.rfmoCode = rfmoCode;
		this.longitude = longitude;
		this.latitude = latitude;
		this.fluxLocationType = fluxLocationType;
		this.fluxChacteristics = fluxChacteristics;
	}

	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public GearProblem getGearProblem() {
		return this.gearProblem;
	}

	public void setGearProblem(GearProblem gearProblem) {
		this.gearProblem = gearProblem;
	}

	
	public FaCatch getFaCatch() {
		return this.faCatch;
	}

	public void setFaCatch(FaCatch faCatch) {
		this.faCatch = faCatch;
	}

	
	public StructuredAddress getStructuredAddress() {
		return this.structuredAddress;
	}

	public void setStructuredAddress(StructuredAddress structuredAddress) {
		this.structuredAddress = structuredAddress;
	}

	
	public Long getFishingActivityId() {
		return this.fishingActivityId;
	}

	public void setFishingActivityId(Long fishingActivityId) {
		this.fishingActivityId = fishingActivityId;
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

	
	public String getCountryId() {
		return this.countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	
	public String getCountryIdListId() {
		return this.countryIdListId;
	}

	public void setCountryIdListId(String countryIdListId) {
		this.countryIdListId = countryIdListId;
	}

	
	public String getFluxId() {
		return this.fluxId;
	}

	public void setFluxId(String fluxId) {
		this.fluxId = fluxId;
	}

	
	public String getFluxListId() {
		return this.fluxListId;
	}

	public void setFluxListId(String fluxListId) {
		this.fluxListId = fluxListId;
	}

	
	public String getRfmoCode() {
		return this.rfmoCode;
	}

	public void setRfmoCode(String rfmoCode) {
		this.rfmoCode = rfmoCode;
	}

	
	public Double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	
	public Double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	
	public String getFluxLocationType() {
		return this.fluxLocationType;
	}

	public void setFluxLocationType(String fluxLocationType) {
		this.fluxLocationType = fluxLocationType;
	}

	
	public Set<FluxCharacteristic> getFluxChacteristics() {
		return this.fluxChacteristics;
	}

	public void setFluxChacteristics(Set<FluxCharacteristic> fluxChacteristics) {
		this.fluxChacteristics = fluxChacteristics;
	}

}
