package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "activity_vessel_storage_characteristics")
public class VesselStorageCharacteristicsEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "vessel_id")
	private String vesselId;

	@Column(name = "vessel_scheme_id")
	private String vesselSchemaId;

	@Column(name = "vessel_type_code")
	private String vesselTypeCode;

	@Column(name = "vessel_type_code_list_id")
	private String vesselTypeCodeListId;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "destVesselCharId")
	private FishingActivityEntity fishingActivitiesForDestVesselCharId;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "sourceVesselCharId")
	private FishingActivityEntity fishingActivitiesForSourceVesselCharId;

	public VesselStorageCharacteristicsEntity() {
	}

	public VesselStorageCharacteristicsEntity(
			String vesselId,
			String vesselSchemaId,
			String vesselTypeCode,
			String vesselTypeCodeListId,
			FishingActivityEntity fishingActivitiesForDestVesselCharId,
			FishingActivityEntity fishingActivitiesForSourceVesselCharId) {
		this.vesselId = vesselId;
		this.vesselSchemaId = vesselSchemaId;
		this.vesselTypeCode = vesselTypeCode;
		this.vesselTypeCodeListId = vesselTypeCodeListId;
		this.fishingActivitiesForDestVesselCharId = fishingActivitiesForDestVesselCharId;
		this.fishingActivitiesForSourceVesselCharId = fishingActivitiesForSourceVesselCharId;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVesselId() {
		return this.vesselId;
	}

	public void setVesselId(String vesselId) {
		this.vesselId = vesselId;
	}

	public String getVesselSchemaId() {
		return this.vesselSchemaId;
	}

	public void setVesselSchemaId(String vesselSchemaId) {
		this.vesselSchemaId = vesselSchemaId;
	}

	public String getVesselTypeCode() {
		return this.vesselTypeCode;
	}

	public void setVesselTypeCode(String vesselTypeCode) {
		this.vesselTypeCode = vesselTypeCode;
	}

	public String getVesselTypeCodeListId() {
		return this.vesselTypeCodeListId;
	}

	public void setVesselTypeCodeListId(String vesselTypeCodeListId) {
		this.vesselTypeCodeListId = vesselTypeCodeListId;
	}

	public FishingActivityEntity getFishingActivitiesForDestVesselCharId() {
		return fishingActivitiesForDestVesselCharId;
	}

	public void setFishingActivitiesForDestVesselCharId(FishingActivityEntity fishingActivitiesForDestVesselCharId) {
		this.fishingActivitiesForDestVesselCharId = fishingActivitiesForDestVesselCharId;
	}

	public FishingActivityEntity getFishingActivitiesForSourceVesselCharId() {
		return fishingActivitiesForSourceVesselCharId;
	}

	public void setFishingActivitiesForSourceVesselCharId(FishingActivityEntity fishingActivitiesForSourceVesselCharId) {
		this.fishingActivitiesForSourceVesselCharId = fishingActivitiesForSourceVesselCharId;
	}
}
