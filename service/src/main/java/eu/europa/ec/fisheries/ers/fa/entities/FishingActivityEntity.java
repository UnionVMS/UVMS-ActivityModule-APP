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
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.utils.UnitCodeEnum;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.ers.service.mapper.FluxLocationMapper;
import eu.europa.ec.fisheries.ers.service.util.Utils;
import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.GeometryMapper;
import lombok.*;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Type;
import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

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
@Data
@ToString(of = {"id", "typeCode", "reasonCode", "occurence"})
@EqualsAndHashCode(of = "id")
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

	@Embedded
	private QuantityType operationsQuantity;

	@Column(name = "calculated_operation_quantity")
	private Double calculatedOperationQuantity;

	@Column(name = "fishing_duration_measure", precision = 17, scale = 17)
	private Double fishingDurationMeasure;

	@Column(name = "fishing_duration_measure_code")
	private String fishingDurationMeasureCode;

	@Column(name = "fishing_duration_measure_unit_code_list_version_id")
	private String fishingDurationMeasureUnitCodeListVersionID;

	@Column(name = "calculated_fishing_duration")
	private Double calculatedFishingDuration;

	@Column(name = "vessel_transport_guid")
	private String vesselTransportGuid;

	@Column(name = "flag_state")
	private String flagState;

	@Column(name="latest")
	private Boolean latest;

	@Column(name="canceled_by")
	private Integer canceledBy;

	@Column(name="deleted_by")
	private Integer deletedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "calculated_start_time", length = 29)
	private Date calculatedStartTime;

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
	private Set<FluxCharacteristicEntity> fluxCharacteristics =  new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<GearProblemEntity> gearProblems;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<FluxLocationEntity> fluxLocations;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "relatedFishingActivity", cascade = CascadeType.ALL)
	private List<FishingActivityEntity> allRelatedFishingActivities;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<FlapDocumentEntity> flapDocuments = new HashSet<>();

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

    @PrePersist
    public void prePersist(){
        if (operationsQuantity != null){
            Double value = operationsQuantity.getValue();
            String unitCode = operationsQuantity.getUnitCode();
            if (value != null || unitCode != null){
                UnitCodeEnum unitCodeEnum = UnitCodeEnum.getUnitCode(unitCode);
                if (unitCodeEnum != null && value != null) {
                    BigDecimal quantity = new BigDecimal(value);
                    BigDecimal result = quantity.multiply(new BigDecimal(unitCodeEnum.getConversionFactor()));
                    calculatedOperationQuantity =  result.doubleValue();
                }
            }
        }
		if (fishingDurationMeasure != null && fishingDurationMeasureCode != null) {
			UnitCodeEnum unitCodeEnum = UnitCodeEnum.getUnitCode(fishingDurationMeasureCode);
			if (unitCodeEnum != null) {
				BigDecimal measuredValue = new BigDecimal(fishingDurationMeasure);
				BigDecimal result = measuredValue.multiply(new BigDecimal(unitCodeEnum.getConversionFactor()));
				calculatedFishingDuration = result.doubleValue();
			}
		}
	}

	@PostLoad
	private void onLoad() {
        if (this.geom != null) {
            this.wkt = GeometryMapper.INSTANCE.geometryToWkt(this.geom).getValue();
        }
    }

    public void addFluxCharacteristics(FluxCharacteristicEntity characteristicEntity){
        fluxCharacteristics.add(characteristicEntity);
        characteristicEntity.setFishingActivity(this);
	}

    public void addFlapDocuments(FlapDocumentEntity flapDocumentEntity){
        flapDocuments.add(flapDocumentEntity);
        flapDocumentEntity.setFishingActivity(this);
    }

	public void addFishingActivityIdentifiers(FishingActivityIdentifierEntity identifierEntity){
		fishingActivityIdentifiers.add(identifierEntity);
		identifierEntity.setFishingActivity(this);
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
			durationSubTotal = Utils.addDoubles(period.getDurationMeasure().getValue(), durationSubTotal);
		}
		return durationSubTotal;
	}

	public String getDurationMeasure(){
        if (CollectionUtils.isEmpty(delimitedPeriods)) {
            return null;
        }
        return delimitedPeriods.iterator().next().getDurationMeasure().getUnitCode(); // As per rules only MIN is allowed
    }

    public Set<FluxLocationDto> getLocations_() {
        Set<FluxLocationDto> locationDtos = newHashSet();
        if (isNotEmpty(fluxLocations)) {
             locationDtos = FluxLocationMapper.INSTANCE.mapEntityToFluxLocationDto(fluxLocations);
        }
        return locationDtos;
    }
}