/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */


package eu.europa.ec.fisheries.ers.fa.entities;

import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.ers.service.mapper.FluxLocationMapper;
import eu.europa.ec.fisheries.ers.service.util.Utils;
import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.GeometryMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@NamedQueries({
		@NamedQuery(name = FishingActivityEntity.ACTIVITY_FOR_FISHING_TRIP,
				query = "SELECT DISTINCT a from FishingActivityEntity a " +
						"JOIN FETCH a.faReportDocument fa " +
						"JOIN FETCH fa.fluxReportDocument flux " +
						"JOIN FETCH a.fishingTrips ft " +
						"JOIN FETCH ft.fishingTripIdentifiers fi " +
						"where (intersects(fa.geom, :area) = true " +
						"and fi.tripId =:fishingTripId) " +
						"order by a.typeCode,fa.acceptedDatetime"),
		@NamedQuery(name = FishingActivityEntity.FIND_FA_DOCS_BY_TRIP_ID_WITHOUT_GEOM,
				query = "SELECT DISTINCT a from FishingActivityEntity a " +
						"JOIN FETCH a.faReportDocument fa " +
						"JOIN FETCH fa.fluxReportDocument flux " +
						"JOIN FETCH a.fishingTrips ft " +
						"JOIN FETCH ft.fishingTripIdentifiers fi " +
						"where fi.tripId =:fishingTripId " +
						"order by a.typeCode,fa.acceptedDatetime"),
		@NamedQuery(name = FishingActivityEntity.FIND_FISHING_ACTIVITY_FOR_TRIP,
				query = "SELECT a from FishingActivityEntity a " +
						"JOIN FETCH a.faReportDocument fa " +
						"JOIN FETCH fa.fluxReportDocument flux " +
						"JOIN FETCH a.fishingTrips ft " +
						"JOIN FETCH ft.fishingTripIdentifiers fi " +
						"where fi.tripId = :fishingTripId and " +
						"fi.tripSchemeId = :tripSchemeId and " +
						"a.typeCode = :fishActTypeCode and " +
						"flux.purposeCode in (:flPurposeCodes)")
})
@Entity
@Table(name = "activity_fishing_activity")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FishingActivityEntity implements Serializable {

	public static final String ACTIVITY_FOR_FISHING_TRIP = "findActivityListForFishingTrips";
	public static final String FIND_FA_DOCS_BY_TRIP_ID_WITHOUT_GEOM = "findActivityListForFishingTripsWithoutGeom";
	public static final String FIND_FISHING_ACTIVITY_FOR_TRIP = "findFishingActivityForTrip";

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name = "SEQ_GEN", sequenceName = "fa_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
	private int id;

	@Type(type = "org.hibernate.spatial.GeometryType")
	@Column(name = "geom")
	private Geometry geom;


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
	private Double operationQuantity;

	@Column(name = "operation_quantity_code")
	private String operationQuantityCode;

	@Column(name = "calculated_operation_quantity")
	private Double calculatedOperationQuantity;

	@Column(name = "fishing_duration_measure", precision = 17, scale = 17)
	private Double fishingDurationMeasure;

	@Column(name = "fishing_duration_measure_code")
	private String fishingDurationMeasureCode;

	@Column(name = "calculated_fishing_duration")
	private Double calculatedFishingDuration;

	@Column(name = "vessel_transport_guid")
	private String vesselTransportGuid;

	@Column(name = "flag_state")
	private String flagState;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "calculated_start_time", length = 29)
	private Date calculatedStartTime;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<FlapDocumentEntity> flapDocuments;


	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<VesselTransportMeansEntity> vesselTransportMeans;


	@Transient
    private String wkt;

    public FlapDocumentEntity getFirstFlapDocument() {
        FlapDocumentEntity flapDocument = null;
        if (!isEmpty(flapDocuments)) {
            flapDocument = flapDocuments.iterator().next();
        }
        return flapDocument;
    }

	public int getId() {
		return this.id;
	}

	public Geometry getGeom() {
		return geom;
	}

	public void setGeom(Geometry geom) {
		this.geom = geom;
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

    public void setDestVesselCharId(VesselStorageCharacteristicsEntity destVesselCharId) {
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

	public Double getOperationQuantity() {
		return this.operationQuantity;
	}

	public void setOperationQuantity(Double operationQuantity) {
		this.operationQuantity = operationQuantity;
	}

	public Double getFishingDurationMeasure() {
		return this.fishingDurationMeasure;
	}

	public void setFishingDurationMeasure(Double fishingDurationMeasure) {
		this.fishingDurationMeasure = fishingDurationMeasure;
	}

	public FishingActivityEntity getRelatedFishingActivity() {
		return relatedFishingActivity;
	}

	public void setRelatedFishingActivity(FishingActivityEntity relatedFishingActivity) {
		this.relatedFishingActivity = relatedFishingActivity;
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

	public String getFishingDurationMeasureCode() {
		return fishingDurationMeasureCode;
	}

	public void setFishingDurationMeasureCode(String fishingDurationMeasureCode) {
		this.fishingDurationMeasureCode = fishingDurationMeasureCode;
	}

	public Double getCalculatedFishingDuration() {
		return calculatedFishingDuration;
	}

	public void setCalculatedFishingDuration(Double calculatedFishingDuration) {
		this.calculatedFishingDuration = calculatedFishingDuration;
	}

	public String getOperationQuantityCode() {
		return operationQuantityCode;
	}

	public void setOperationQuantityCode(String operationQuantityCode) {
		this.operationQuantityCode = operationQuantityCode;
	}

	public Double getCalculatedOperationQuantity() {
		return calculatedOperationQuantity;
	}

	public void setCalculatedOperationQuantity(Double calculatedOperationQuantity) {
		this.calculatedOperationQuantity = calculatedOperationQuantity;
	}



	public String getVesselTransportGuid() {
		return vesselTransportGuid;
	}

	public void setVesselTransportGuid(String vesselTransportGuid) {
		this.vesselTransportGuid = vesselTransportGuid;
	}

    public String getWkt() {
        return wkt;
    }

	public Set<VesselTransportMeansEntity> getVesselTransportMeans() {
		return vesselTransportMeans;
	}

	public void setVesselTransportMeans(Set<VesselTransportMeansEntity> vesselTransportMeans) {
		this.vesselTransportMeans = vesselTransportMeans;
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

				'}';
	}

	public Set<FlapDocumentEntity> getFlapDocuments() {
		return flapDocuments;
	}

	public void setFlapDocuments(Set<FlapDocumentEntity> flapDocuments) {
		this.flapDocuments = flapDocuments;
	}

    public String getFlagState() {
        return flagState;
    }

    public void setFlagState(String flagState) {
        this.flagState = flagState;
    }

	public Date getCalculatedStartTime() {
		return calculatedStartTime;
	}

	public void setCalculatedStartTime(Date calculatedStartTime) {
		this.calculatedStartTime = calculatedStartTime;
	}

	@PostLoad
	private void onLoad() {
        if (this.geom != null) {
            this.wkt = GeometryMapper.INSTANCE.geometryToWkt(this.geom).getValue();
        }
    }
    public Double getCalculatedDuration(){

        if (isEmpty(delimitedPeriods)) {
            return null;
        }
        Double durationSubTotal = null;
        for (DelimitedPeriodEntity period : delimitedPeriods) {
            durationSubTotal = Utils.addDoubles(period.getCalculatedDuration(), durationSubTotal);
        }
        return durationSubTotal;
	}

	public Double getDuration(){

		if (isEmpty(delimitedPeriods)) {
			return null;
		}
		Double durationSubTotal = null;
		for (DelimitedPeriodEntity period : delimitedPeriods) {
			durationSubTotal = Utils.addDoubles(period.getDuration(), durationSubTotal);
		}
		return durationSubTotal;
	}

	public String getDurationMeasure(){
        if (isEmpty(delimitedPeriods)) {
            return null;
        }
        return delimitedPeriods.iterator().next().getDurationUnitCode(); // As per rules only MIN is allowed
    }

    public Set<FluxLocationDto> getLocations_() {
        Set<FluxLocationDto> locationDtos = newHashSet();
        if (isNotEmpty(fluxLocations)) {
             locationDtos = FluxLocationMapper.INSTANCE.mapEntityToFluxLocationDto(fluxLocations);
        }
        return locationDtos;
    }
}