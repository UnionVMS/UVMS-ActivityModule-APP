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
import java.util.Date;
import java.util.Set;


@NamedQueries({
		@NamedQuery(name = FishingActivityEntity.ACTIVITY_FOR_FISHING_TRIP,
				query = "SELECT DISTINCT a  from FishingActivityEntity a " +
						"JOIN FETCH a.faReportDocument fa " +
						"JOIN FETCH fa.fluxReportDocument flux " +
						"JOIN FETCH a.fishingTrips ft " +
						"JOIN FETCH ft.fishingTripIdentifiers fi " +
						"where fi.tripId =:fishingTripId order by a.typeCode,fa.acceptedDatetime")
})

@Entity
@Table(name = "activity_fishing_activity")
public class FishingActivityEntity implements Serializable {

	public static final String ACTIVITY_FOR_FISHING_TRIP = "findActivityListForFishingTrips";

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fa_report_document_id")
	private FaReportDocumentEntity faReportDocument;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "source_vessel_char_id")
	private VesselStorageCharacteristicsEntity sourceVesselCharId;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "dest_vessel_char_id")
	private VesselStorageCharacteristicsEntity destVesselCharId;

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
	private String flapDocumentId;

	@Column(name = "flap_document_scheme_id")
	private String flapDocumentSchemeId;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "related_fishing_activity_id")
	private FishingActivityEntity relatedFishingActivity;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<FaCatchEntity> faCatchs;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<DelimitedPeriodEntity> delimitedPeriods;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<FishingActivityIdentifierEntity> fishingActivityIdentifiers;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<FishingTripEntity> fishingTrips;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<FishingGearEntity> fishingGears;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<FluxCharacteristicEntity> fluxCharacteristics;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<GearProblemEntity> gearProblems;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<FluxLocationEntity> fluxLocations;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "relatedFishingActivity", cascade = CascadeType.ALL)
	private Set<FishingActivityEntity> allRelatedFishingActivities;


	public FishingActivityEntity() {
		super();
	}


	public FishingActivityEntity( FaReportDocumentEntity faReportDocument, VesselStorageCharacteristicsEntity sourceVesselCharId, VesselStorageCharacteristicsEntity destVesselCharId, String typeCode, String typeCodeListid, Date occurence, String reasonCode, String reasonCodeListId, String vesselActivityCode, String vesselActivityCodeListId, String fisheryTypeCode, String fisheryTypeCodeListId, String speciesTargetCode, String speciesTargetCodeListId, Long operationQuantity, Double fishingDurationMeasure, String flapDocumentId, String flapDocumentSchemeId, FishingActivityEntity relatedFishingActivity, Set<FaCatchEntity> faCatchs, Set<DelimitedPeriodEntity> delimitedPeriods, Set<FishingActivityIdentifierEntity> fishingActivityIdentifiers, Set<FishingTripEntity> fishingTrips, Set<FishingGearEntity> fishingGears, Set<FluxCharacteristicEntity> fluxCharacteristics, Set<GearProblemEntity> gearProblems, Set<FluxLocationEntity> fluxLocations, Set<FishingActivityEntity> allRelatedFishingActivities) {
      	this.faReportDocument = faReportDocument;
		this.sourceVesselCharId = sourceVesselCharId;
		this.destVesselCharId = destVesselCharId;
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
		this.flapDocumentSchemeId = flapDocumentSchemeId;
		this.relatedFishingActivity = relatedFishingActivity;
		this.faCatchs = faCatchs;
		this.delimitedPeriods = delimitedPeriods;
		this.fishingActivityIdentifiers = fishingActivityIdentifiers;
		this.fishingTrips = fishingTrips;
		this.fishingGears = fishingGears;
		this.fluxCharacteristics = fluxCharacteristics;
		this.gearProblems = gearProblems;
		this.fluxLocations = fluxLocations;
		this.allRelatedFishingActivities = allRelatedFishingActivities;
	}

	public int getId() {
		return this.id;
	}

	public FaReportDocumentEntity getFaReportDocument() {
		return this.faReportDocument;
	}

	public void setFaReportDocument(
			FaReportDocumentEntity faReportDocument) {
		this.faReportDocument = faReportDocument;
	}

	public VesselStorageCharacteristicsEntity getSourceVesselCharId() {
		return this.sourceVesselCharId;
	}

	public void setSourceVesselCharId(
			VesselStorageCharacteristicsEntity sourceVesselCharId) {
		this.sourceVesselCharId = sourceVesselCharId;
	}

	public VesselStorageCharacteristicsEntity getDestVesselCharId() {
		return this.destVesselCharId;
	}

	public void setDestVesselCharId(
			VesselStorageCharacteristicsEntity destVesselCharId) {
		this.destVesselCharId = destVesselCharId;
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

	public String getFlapDocumentId() {
		return this.flapDocumentId;
	}

	public void setFlapDocumentId(String flapDocumentId) {
		this.flapDocumentId = flapDocumentId;
	}

	public FishingActivityEntity getRelatedFishingActivity() {
		return relatedFishingActivity;
	}

	public void setRelatedFishingActivity(FishingActivityEntity relatedFishingActivity) {
		this.relatedFishingActivity = relatedFishingActivity;
	}

	public String getFlapDocumentSchemeId() {
		return this.flapDocumentSchemeId;
	}

	public void setFlapDocumentSchemeId(String flapDocumentSchemeId) {
		this.flapDocumentSchemeId = flapDocumentSchemeId;
	}

	public Set<FaCatchEntity> getFaCatchs() {
		return this.faCatchs;
	}

	public void setFaCatchs(Set<FaCatchEntity> faCatchs) {
		this.faCatchs = faCatchs;
	}

	public Set<DelimitedPeriodEntity> getDelimitedPeriods() {
		return this.delimitedPeriods;
	}

	public void setDelimitedPeriods(
			Set<DelimitedPeriodEntity> delimitedPeriods) {
		this.delimitedPeriods = delimitedPeriods;
	}

	public Set<FishingActivityIdentifierEntity> getFishingActivityIdentifiers() {
		return this.fishingActivityIdentifiers;
	}

	public void setFishingActivityIdentifiers(
			Set<FishingActivityIdentifierEntity> fishingActivityIdentifiers) {
		this.fishingActivityIdentifiers = fishingActivityIdentifiers;
	}

	public Set<FishingTripEntity> getFishingTrips() {
		return this.fishingTrips;
	}

	public void setFishingTrips(
			Set<FishingTripEntity> fishingTrips) {
		this.fishingTrips = fishingTrips;
	}

	public Set<FishingGearEntity> getFishingGears() {
		return this.fishingGears;
	}

	public void setFishingGears(
			Set<FishingGearEntity> fishingGears) {
		this.fishingGears = fishingGears;
	}

	public Set<FluxCharacteristicEntity> getFluxCharacteristics() {
		return this.fluxCharacteristics;
	}

	public void setFluxCharacteristics(
			Set<FluxCharacteristicEntity> fluxCharacteristics) {
		this.fluxCharacteristics = fluxCharacteristics;
	}

	public Set<GearProblemEntity> getGearProblems() {
		return this.gearProblems;
	}

	public void setGearProblems(
			Set<GearProblemEntity> gearProblems) {
		this.gearProblems = gearProblems;
	}

	public Set<FluxLocationEntity> getFluxLocations() {
		return fluxLocations;
	}

	public void setFluxLocations(Set<FluxLocationEntity> fluxLocations) {
		this.fluxLocations = fluxLocations;
	}

	public Set<FishingActivityEntity> getAllRelatedFishingActivities() {
		return allRelatedFishingActivities;
	}

	public void setAllRelatedFishingActivities(Set<FishingActivityEntity> allRelatedFishingActivities) {
		this.allRelatedFishingActivities = allRelatedFishingActivities;
	}

	@Override
	public String toString() {
		return "FishingActivityEntity{" +
				"id=" + id +
				", faReportDocument=" + faReportDocument +
				", sourceVesselCharId=" + sourceVesselCharId +
				", destVesselCharId=" + destVesselCharId +
				", typeCode='" + typeCode + '\'' +
				", typeCodeListid='" + typeCodeListid + '\'' +
				", occurence=" + occurence +
				", reasonCode='" + reasonCode + '\'' +
				", reasonCodeListId='" + reasonCodeListId + '\'' +
				", vesselActivityCode='" + vesselActivityCode + '\'' +
				", vesselActivityCodeListId='" + vesselActivityCodeListId + '\'' +
				", fisheryTypeCode='" + fisheryTypeCode + '\'' +
				", fisheryTypeCodeListId='" + fisheryTypeCodeListId + '\'' +
				", speciesTargetCode='" + speciesTargetCode + '\'' +
				", speciesTargetCodeListId='" + speciesTargetCodeListId + '\'' +
				", operationQuantity=" + operationQuantity +
				", fishingDurationMeasure=" + fishingDurationMeasure +
				", flapDocumentId='" + flapDocumentId + '\'' +
				", flapDocumentSchemeId='" + flapDocumentSchemeId + '\'' +
				'}';
	}
}