/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import eu.europa.ec.fisheries.uvms.activity.fa.utils.UnitCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTWriter;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@NamedQuery(name = FishingActivityEntity.ACTIVITY_FOR_FISHING_TRIP,
		query = "SELECT DISTINCT a from FishingActivityEntity a " +
				"JOIN FETCH a.faReportDocument fa " +
				"JOIN FETCH a.fishingTrip ft " +
				"where (intersects(a.geom, :area) = true " +
				"and ft.fishingTripKey.tripId =: fishingTripId) " +
				"order by a.calculatedStartTime ASC, fa.fluxReportDocument_PurposeCode DESC")
@NamedQuery(name = FishingActivityEntity.FIND_FA_DOCS_BY_TRIP_ID_WITHOUT_GEOM,
		query = "SELECT DISTINCT a from FishingActivityEntity a " +
				"JOIN FETCH a.faReportDocument fa " +
				"JOIN FETCH a.fishingTrip ft " +
				"where ft.fishingTripKey.tripId =:fishingTripId " +
				"order by a.calculatedStartTime ASC, fa.fluxReportDocument_PurposeCode DESC")

@Entity
@Table(name = "activity_fishing_activity")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(of = {"id", "typeCode", "reasonCode", "occurence"})
public class FishingActivityEntity implements Serializable {

	public static final String ACTIVITY_FOR_FISHING_TRIP = "findActivityListForFishingTrips";
	public static final String FIND_FA_DOCS_BY_TRIP_ID_WITHOUT_GEOM = "findActivityListForFishingTripsWithoutGeom";

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name = "SEQ_GEN", sequenceName = "fa_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
	private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fa_report_document_id")
    private FaReportDocumentEntity faReportDocument;

	@Column(name = "geom", columnDefinition = "Geometry")
	private Point geom;

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

	@Column(name = "occurence")
	private Instant occurence;

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
	@AttributeOverride( name = "value", column = @Column(name = "operationsquantity_value"))
	@AttributeOverride( name = "unitCode", column = @Column(name = "operationsquantity_unitCode"))
	@AttributeOverride( name = "unitCodeListID", column = @Column(name = "operationsquantity_unitCodeListID"))
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

	@Column(name = "calculated_start_time")
	private Instant calculatedStartTime;

	@Column(name = "calculated_end_time")
	private Instant calculatedEndTime;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "related_fishing_activity_id")
	private FishingActivityEntity relatedFishingActivity;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<FaCatchEntity> faCatchs;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "trip_id", referencedColumnName = "trip_id")
	@JoinColumn(name = "trip_scheme_id", referencedColumnName = "trip_scheme_id")
	private FishingTripEntity fishingTrip;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<FishingGearEntity> fishingGears;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<FluxCharacteristicEntity> fluxCharacteristics =  new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<GearProblemEntity> gearProblems;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "activity_fishing_activity_flux_location",
			joinColumns = @JoinColumn(name = "fishing_activity_id"),
			inverseJoinColumns = @JoinColumn(name = "flux_location_id"))
	private Set<LocationEntity> fluxLocations;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "relatedFishingActivity", cascade = CascadeType.ALL)
	private Set<FishingActivityEntity> allRelatedFishingActivities;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<FlapDocumentEntity> flapDocuments = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingActivity", cascade = CascadeType.ALL)
	private Set<VesselTransportMeansEntity> vesselTransportMeans;

	@Transient
    private String wkt;

	@Column(precision = 17, scale = 17)
	private Double longitude;

	@Column(precision = 17, scale = 17)
	private Double latitude;

	@Column(precision = 17, scale = 17)
	private Double altitude;

    @PrePersist
    public void prePersist() {
        if (operationsQuantity != null) {
            Double value = operationsQuantity.getValue();
            String unitCode = operationsQuantity.getUnitCode();
            if (value != null || unitCode != null) {
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
            this.wkt = new WKTWriter().write(this.geom);
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
}
