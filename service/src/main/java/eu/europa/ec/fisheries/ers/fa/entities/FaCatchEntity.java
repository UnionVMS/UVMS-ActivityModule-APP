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
import java.util.HashSet;
import java.util.Set;
import eu.europa.ec.fisheries.ers.fa.utils.UnitCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NamedQueries({
		@NamedQuery(name = FaCatchEntity.CATCHES_FOR_FISHING_TRIP,
				query = "SELECT faCatch.typeCode, faCatch.speciesCode, fluxLoc.fluxLocationIdentifier, sum(faCatch.weightMeasure) " +
						"FROM FaCatchEntity faCatch " +
						"JOIN faCatch.fluxLocations fluxLoc " +
						"JOIN faCatch.fishingActivity fishAct " +
						"JOIN fishAct.faReportDocument fa " +
						"JOIN fishAct.fishingTrips fishTrip " +
						"JOIN fishTrip.fishingTripIdentifiers fishIdent " +
						"WHERE fishIdent.tripId =:tripId and faCatch.typeCode IN ('UNLOADED','ONBOARD','KEPT_IN_NET','TAKEN_ONBOARD')" +
						"GROUP BY faCatch.speciesCode, faCatch.typeCode,fluxLoc.fluxLocationIdentifier " +
						"ORDER BY faCatch.typeCode, faCatch.speciesCode")
})
@Entity
@Table(name = "activity_fa_catch")
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = {"typeCode", "speciesCode", "typeCodeListId", "speciesCodeListid", "unitQuantity", "unitQuantityCode", "calculatedUnitQuantity", "weightMeasureUnitCode", "weightMeasure", "usageCode", "territory", "fishClassCode"})
@ToString(exclude = {"fishingActivity", "aapProcesses", "fishingGears", "fluxLocations", "fluxCharacteristics", "aapStocks", "fishingTrips", "fishingActivity"})
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

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "size_distribution_id")
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
	private Double unitQuantity;

	@Column(name = "unit_quantity_code")
	private String unitQuantityCode;

	@Column(name = "calculated_unit_quantity")
	private Double calculatedUnitQuantity;

	@Column(name = "weight_measure_unit_code")
	private String weightMeasureUnitCode;

	@Column(name = "weight_measure", precision = 17, scale = 17)
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

	@Column(name = "territory")
	private String territory;

	@Column(name = "fao_area")
	private String faoArea;

	@Column(name = "ices_stat_rectangle")
	private String icesStatRectangle;

	@Column(name = "effort_zone")
	private String effortZone;

    @Column(name = "rfmo")
    private String rfmo;

    @Column(name = "gfcm_gsa")
    private String gfcmGsa;

    @Column(name = "gfcm_stat_rectangle")
    private String gfcmStatRectangle;

	@Column(name = "presentation")
	private String presentation;

	@Column(name = "gear_type_code")
	private String gearTypeCode;

	@Column(name = "fish_class_code")
	private String fishClassCode;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<AapProcessEntity> aapProcesses = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<FishingGearEntity> fishingGears = new HashSet<>(); // AKA usedFishingGears

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<FluxLocationEntity> fluxLocations = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<FluxCharacteristicEntity> fluxCharacteristics = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<AapStockEntity> aapStocks = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<FishingTripEntity> fishingTrips = new HashSet<>();

	@PrePersist
	public void prePersist(){
		if (unitQuantity != null && unitQuantityCode != null){
			UnitCodeEnum unitCodeEnum = UnitCodeEnum.getUnitCode(unitQuantityCode);
			if (unitCodeEnum != null) {
				BigDecimal quantity = new BigDecimal(unitQuantity);
				BigDecimal result = quantity.multiply(new BigDecimal(unitCodeEnum.getConversionFactor()));
				calculatedUnitQuantity =  result.doubleValue();
			}
		}
		if (weightMeasure != null && weightMeasureUnitCode != null){
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