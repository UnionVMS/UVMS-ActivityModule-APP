package eu.europa.ec.fisheries.ers.fa.entities;

import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "activity_fishing_activity", uniqueConstraints = {
		@UniqueConstraint(columnNames = "dest_vessel_char_id"),
		@UniqueConstraint(columnNames = "fishing_trip_id"),
		@UniqueConstraint(columnNames = "source_vessel_char_id") })
public class FishingActivity {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fa_report_document_id")
	private FaReportDocument faReportDocument;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "delimited_period_id")
	private DelimitedPeriod delimitedPeriod;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "source_vessel_char_id", nullable = true)
	private VesselStorageCharacteristics vesselStorageCharctersticsBySourceVesselCharId;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dest_vessel_char_id", nullable = true)
	private VesselStorageCharacteristics vesselStorageCharctersticsByDestVesselCharId;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_trip_id", nullable = true)
	private FishingTrip fishingTrip;
	
	@Column(name = "type_code", nullable = false)
	private String typeCode;
	
	@Column(name = "type_code_listid", nullable = false)
	private String typeCodeListid;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "occurence", length = 29)
	private Date occurence;
	
	
	@Column(name = "reason_code")
	private String reasonCode;
	
	@Column(name = "reason_code_list_id")
	private String reasonCodeListId;
	
	@Column(name = "vessel_activity_code")
	private String vesselActivityCode;
	
	@Column(name = "vessel_activity_code_list_id")
	private String vesselActivityCodeListId;
	
	@Column(name = "fishery_type_code")
	private String fisheryTypeCode;
	
	@Column(name = "fishery_type_code_list_id")
	private String fisheryTypeCodeListId;
	
	@Column(name = "species_target_code")
	private String speciesTargetCode;
	
	@Column(name = "species_target_code_list_id")
	private String speciesTargetCodeListId;
	
	@Column(name = "operation_quantity")
	private Long operationQuantity;
	
	@Column(name = "fishing_duration_measure", precision = 17, scale = 17)
	private Double fishingDurationMeasure;
	
	@Column(name = "flap_document_id")
	private Integer flapDocumentId;
	
	@Column(name = "flap_document_schema_id")
	private String flapDocumentSchemaId;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity")
	private Set<FaCatch> faCatchs = new HashSet<FaCatch>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity")
	private Set<FishingGear> fishingGears = new HashSet<FishingGear>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity")
	private Set<FluxCharacteristic> fluxChacteristics = new HashSet<FluxCharacteristic>(
			0);

	public FishingActivity() {
	}

	public FishingActivity(int id, String typeCode, String typeCodeListid) {
		this.id = id;
		this.typeCode = typeCode;
		this.typeCodeListid = typeCodeListid;
	}

	public FishingActivity(
			int id,
			FaReportDocument faReportDocument,
			DelimitedPeriod delimitedPeriod,
			VesselStorageCharacteristics vesselStorageCharctersticsBySourceVesselCharId,
			VesselStorageCharacteristics vesselStorageCharctersticsByDestVesselCharId,
			FishingTrip fishingTrip, String typeCode, String typeCodeListid,
			Date occurence, String reasonCode, String reasonCodeListId,
			String vesselActivityCode, String vesselActivityCodeListId,
			String fisheryTypeCode, String fisheryTypeCodeListId,
			String speciesTargetCode, String speciesTargetCodeListId,
			Long operationQuantity, Double fishingDurationMeasure,
			Integer flapDocumentId, String flapDocumentSchemaId,
			Set<FaCatch> faCatchs, Set<FishingGear> fishingGears,
			Set<FluxCharacteristic> fluxChacteristics) {
		this.id = id;
		this.faReportDocument = faReportDocument;
		this.delimitedPeriod = delimitedPeriod;
		this.vesselStorageCharctersticsBySourceVesselCharId = vesselStorageCharctersticsBySourceVesselCharId;
		this.vesselStorageCharctersticsByDestVesselCharId = vesselStorageCharctersticsByDestVesselCharId;
		this.fishingTrip = fishingTrip;
		this.typeCode = typeCode;
		this.typeCodeListid = typeCodeListid;
		this.occurence = occurence;
		this.reasonCode = reasonCode;
		this.reasonCodeListId = reasonCodeListId;
		this.vesselActivityCode = vesselActivityCode;
		this.vesselActivityCodeListId = vesselActivityCodeListId;
		this.fisheryTypeCode = fisheryTypeCode;
		this.fisheryTypeCodeListId = fisheryTypeCodeListId;
		this.speciesTargetCode = speciesTargetCode;
		this.speciesTargetCodeListId = speciesTargetCodeListId;
		this.operationQuantity = operationQuantity;
		this.fishingDurationMeasure = fishingDurationMeasure;
		this.flapDocumentId = flapDocumentId;
		this.flapDocumentSchemaId = flapDocumentSchemaId;
		this.faCatchs = faCatchs;
		this.fishingGears = fishingGears;
		this.fluxChacteristics = fluxChacteristics;
	}

	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public FaReportDocument getFaReportDocument() {
		return this.faReportDocument;
	}

	public void setFaReportDocument(FaReportDocument faReportDocument) {
		this.faReportDocument = faReportDocument;
	}

	
	public DelimitedPeriod getDelimitedPeriod() {
		return this.delimitedPeriod;
	}

	public void setDelimitedPeriod(DelimitedPeriod delimitedPeriod) {
		this.delimitedPeriod = delimitedPeriod;
	}

	
	public VesselStorageCharacteristics getVesselStorageCharctersticsBySourceVesselCharId() {
		return this.vesselStorageCharctersticsBySourceVesselCharId;
	}

	public void setVesselStorageCharctersticsBySourceVesselCharId(
			VesselStorageCharacteristics vesselStorageCharctersticsBySourceVesselCharId) {
		this.vesselStorageCharctersticsBySourceVesselCharId = vesselStorageCharctersticsBySourceVesselCharId;
	}

	
	public VesselStorageCharacteristics getVesselStorageCharctersticsByDestVesselCharId() {
		return this.vesselStorageCharctersticsByDestVesselCharId;
	}

	public void setVesselStorageCharctersticsByDestVesselCharId(
			VesselStorageCharacteristics vesselStorageCharctersticsByDestVesselCharId) {
		this.vesselStorageCharctersticsByDestVesselCharId = vesselStorageCharctersticsByDestVesselCharId;
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

	
	public String getTypeCodeListid() {
		return this.typeCodeListid;
	}

	public void setTypeCodeListid(String typeCodeListid) {
		this.typeCodeListid = typeCodeListid;
	}

	
	public Date getOccurence() {
		return this.occurence;
	}

	public void setOccurence(Date occurence) {
		this.occurence = occurence;
	}

	
	public String getReasonCode() {
		return this.reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	
	public String getReasonCodeListId() {
		return this.reasonCodeListId;
	}

	public void setReasonCodeListId(String reasonCodeListId) {
		this.reasonCodeListId = reasonCodeListId;
	}


	public String getVesselActivityCode() {
		return this.vesselActivityCode;
	}

	public void setVesselActivityCode(String vesselActivityCode) {
		this.vesselActivityCode = vesselActivityCode;
	}

	
	public String getVesselActivityCodeListId() {
		return this.vesselActivityCodeListId;
	}

	public void setVesselActivityCodeListId(String vesselActivityCodeListId) {
		this.vesselActivityCodeListId = vesselActivityCodeListId;
	}

	
	public String getFisheryTypeCode() {
		return this.fisheryTypeCode;
	}

	public void setFisheryTypeCode(String fisheryTypeCode) {
		this.fisheryTypeCode = fisheryTypeCode;
	}

	
	public String getFisheryTypeCodeListId() {
		return this.fisheryTypeCodeListId;
	}

	public void setFisheryTypeCodeListId(String fisheryTypeCodeListId) {
		this.fisheryTypeCodeListId = fisheryTypeCodeListId;
	}

	
	public String getSpeciesTargetCode() {
		return this.speciesTargetCode;
	}

	public void setSpeciesTargetCode(String speciesTargetCode) {
		this.speciesTargetCode = speciesTargetCode;
	}

	
	public String getSpeciesTargetCodeListId() {
		return this.speciesTargetCodeListId;
	}

	public void setSpeciesTargetCodeListId(String speciesTargetCodeListId) {
		this.speciesTargetCodeListId = speciesTargetCodeListId;
	}


	public Long getOperationQuantity() {
		return this.operationQuantity;
	}

	public void setOperationQuantity(Long operationQuantity) {
		this.operationQuantity = operationQuantity;
	}


	public Double getFishingDurationMeasure() {
		return this.fishingDurationMeasure;
	}

	public void setFishingDurationMeasure(Double fishingDurationMeasure) {
		this.fishingDurationMeasure = fishingDurationMeasure;
	}


	public Integer getFlapDocumentId() {
		return this.flapDocumentId;
	}

	public void setFlapDocumentId(Integer flapDocumentId) {
		this.flapDocumentId = flapDocumentId;
	}


	public String getFlapDocumentSchemaId() {
		return this.flapDocumentSchemaId;
	}

	public void setFlapDocumentSchemaId(String flapDocumentSchemaId) {
		this.flapDocumentSchemaId = flapDocumentSchemaId;
	}


	public Set<FaCatch> getFaCatchs() {
		return this.faCatchs;
	}

	public void setFaCatchs(Set<FaCatch> faCatchs) {
		this.faCatchs = faCatchs;
	}


	public Set<FishingGear> getFishingGears() {
		return this.fishingGears;
	}

	public void setFishingGears(Set<FishingGear> fishingGears) {
		this.fishingGears = fishingGears;
	}

	
	public Set<FluxCharacteristic> getFluxChacteristics() {
		return this.fluxChacteristics;
	}

	public void setFluxChacteristics(Set<FluxCharacteristic> fluxChacteristics) {
		this.fluxChacteristics = fluxChacteristics;
	}

}
