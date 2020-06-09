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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.locationtech.jts.geom.Point;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@NamedQuery(name = FaCatchEntity.CATCHES_FOR_FISHING_TRIP,
		query = "SELECT faCatch.typeCode, faCatch.speciesCode, fluxLoc.fluxLocationIdentifier, sum(faCatch.weightMeasure) " +
				"FROM FaCatchEntity faCatch " +
				"JOIN faCatch.locations fluxLoc " +
				"JOIN faCatch.fishingActivity fishAct " +
				"JOIN fishAct.faReportDocument fa " +
				"JOIN fishAct.fishingTrip fishTrip " +
				"WHERE fishTrip.fishingTripKey.tripId =:tripId and faCatch.typeCode IN ('UNLOADED','ONBOARD','KEPT_IN_NET','TAKEN_ONBOARD')" +
				"GROUP BY faCatch.speciesCode, faCatch.typeCode,fluxLoc.fluxLocationIdentifier " +
				"ORDER BY faCatch.typeCode, faCatch.speciesCode")
@Entity
@Table(name = "activity_fa_catch")
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = {"typeCode", "speciesCode", "typeCodeListId", "speciesCodeListid", "unitQuantity", "unitQuantityCode", "calculatedUnitQuantity", "weightMeasureUnitCode", "weightMeasure", "usageCode", "fishClassCode"})
@ToString(exclude = {"fishingActivity", "aapProcesses", "locations", "fluxCharacteristics", "aapStocks"})
public class FaCatchEntity implements Serializable {

	public static final String CATCHES_FOR_FISHING_TRIP = "findCatchesForFishingTrip";

	@Id
	@Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "fa_catch_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_activity_id")
	private FishingActivityEntity fishingActivity;

	@Column(name = "type_code", nullable = false)
	private String typeCode;

	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;

	@Column(name = "species_code", nullable = false)
	private String speciesCode;

	@Column(name = "species_code_listid", nullable = false)
	private String speciesCodeListid;

	@Column(name = "unit_quantity")
	private Double unitQuantity;

	@Column(name = "unit_quantity_code")
	private String unitQuantityCode;

	@Column(name = "calculated_unit_quantity")
	private Double calculatedUnitQuantity;

	@Column(name = "weight_measure_unit_code")
	private String weightMeasureUnitCode;

	@Column(name = "weight_measure")
	private Double weightMeasure;

	@Column(name = "calculated_weight_measure")
	private Double calculatedWeightMeasure;

	@Column(name = "usage_code")
	private String usageCode;

	@Column(name = "usage_code_list_id")
	private String usageCodeListId;

	@Column(name = "weighing_means_code")
	private String weighingMeansCode;

	@Column(name = "weighing_means_code_list_id")
	private String weighingMeansCodeListId;

	@Column(name = "presentation")
	private String presentation;

	@Column(name = "gear_type_code")
	private String gearTypeCode;

	@Column(name = "fish_class_code")
	private String fishClassCode;

	@Column(name = "size_distribution_class_code")
	private String sizeDistributionClassCode;

	@Column(name = "size_distribution_class_code_list_id")
	private String sizeDistributionClassCodeListId;

	@Column(name = "size_distribution_category_code")
	private String sizeDistributionCategoryCode;

	@Column(name = "size_distribution_category_code_list_id")
	private String sizeDistributionCategoryCodeListId;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<AapProcessEntity> aapProcesses = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<FishingGearEntity> fishingGears = new HashSet<>(); // AKA usedFishingGears

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "activity_fa_catch_specified_location",
			joinColumns = @JoinColumn(name = "fa_catch_id"),
			inverseJoinColumns = @JoinColumn(name = "flux_location_id"))
	private Set<LocationEntity> locations = new HashSet<>();

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "activity_fa_catch_destination_location",
			joinColumns = @JoinColumn(name = "fa_catch_id"),
			inverseJoinColumns = @JoinColumn(name = "flux_location_id"))
	private Set<LocationEntity> destinations = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<FluxCharacteristicEntity> fluxCharacteristics = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<AapStockEntity> aapStocks = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trip_id", referencedColumnName = "trip_id")
	@JoinColumn(name = "trip_scheme_id", referencedColumnName = "trip_scheme_id")
	private FishingTripEntity fishingTrip;

	@Column(precision = 17, scale = 17)
	private Double longitude;

	@Column(precision = 17, scale = 17)
	private Double latitude;

	@Column(precision = 17, scale = 17)
	private Double altitude;

	@Column(name = "geom", columnDefinition = "Geometry")
	private Point geom;

	@PrePersist
	public void prePersist() {
		if (unitQuantity != null && unitQuantityCode != null) {
			UnitCodeEnum unitCodeEnum = UnitCodeEnum.getUnitCode(unitQuantityCode);
			if (unitCodeEnum != null) {
				BigDecimal quantity = new BigDecimal(unitQuantity);
				BigDecimal result = quantity.multiply(new BigDecimal(unitCodeEnum.getConversionFactor()));
				calculatedUnitQuantity =  result.doubleValue();
			}
		}
		if (weightMeasure != null && weightMeasureUnitCode != null) {
			UnitCodeEnum unitCodeEnum = UnitCodeEnum.getUnitCode(weightMeasureUnitCode);
			if (unitCodeEnum != null) {
				BigDecimal quantity = new BigDecimal(weightMeasure);
				BigDecimal result = quantity.multiply(new BigDecimal(unitCodeEnum.getConversionFactor()));
				calculatedWeightMeasure =  result.doubleValue();
			}
		}
	}

	public void addAAPProcess(AapProcessEntity aapProcessEntity) {
		aapProcesses.add(aapProcessEntity);
		aapProcessEntity.setFaCatch(this);
	}
}
