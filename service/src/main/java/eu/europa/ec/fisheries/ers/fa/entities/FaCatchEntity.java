/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.fa.entities;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@NamedQueries({
		@NamedQuery(name = FaCatchEntity.CATCHES_FOR_FISHING_TRIP,
				query = "SELECT faCatch.typeCode, faCatch.speciesCode, fluxLoc.fluxLocationIdentifier, sum(faCatch.weightMeasure) " +
						"FROM FaCatchEntity faCatch " +
						"JOIN faCatch.fluxLocations fluxLoc " +
						"JOIN faCatch.fishingActivity fishAct " +
						"JOIN fishAct.faReportDocument fa " +
						"JOIN fishAct.fishingTrips fishTrip " +
						"JOIN fishTrip.fishingTripIdentifiers fishIdent " +
						"WHERE fishIdent.tripId =:tripId " +
						"GROUP BY faCatch.speciesCode, faCatch.typeCode,fluxLoc.fluxLocationIdentifier " +
						"ORDER BY faCatch.typeCode, faCatch.speciesCode")
})
@Entity
@Table(name = "activity_fa_catch")
public class FaCatchEntity implements Serializable {

	public static final String CATCHES_FOR_FISHING_TRIP = "findCatchesForFishingTrip";

	@Id
	@Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "fa_catch_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_activity_id")
	private FishingActivityEntity fishingActivity;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
	private Set<AapProcessEntity> aapProcesses;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<FishingGearEntity> fishingGears;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<FluxLocationEntity> fluxLocations;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<FluxCharacteristicEntity> fluxCharacteristics;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<AapStockEntity> aapStocks;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faCatch", cascade = CascadeType.ALL)
	private Set<FishingTripEntity> fishingTrips;

	public FaCatchEntity() {
		super();
	}

	public int getId() {
		return this.id;
	}
	public FishingActivityEntity getFishingActivity() {
		return this.fishingActivity;
	}

    public void setFishingActivity(FishingActivityEntity fishingActivity) {
        this.fishingActivity = fishingActivity;
    }
	public SizeDistributionEntity getSizeDistribution() {
		return this.sizeDistribution;
	}

	public void setSizeDistribution(
			SizeDistributionEntity sizeDistribution) {
		this.sizeDistribution = sizeDistribution;
	}

    public String getGearTypeCode() {
        return gearTypeCode;
    }

    public void setGearTypeCode(String gearTypeCode) {
        this.gearTypeCode = gearTypeCode;
    }

    public String getFishClassCode() {
        return fishClassCode;
    }

    public void setFishClassCode(String fishClassCode) {
        this.fishClassCode = fishClassCode;
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

    public Double getUnitQuantity() {
        return this.unitQuantity;
	}

    public void setUnitQuantity(Double unitQuantity) {
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
	public Double getCalculatedWeightMeasure() {
		return calculatedWeightMeasure;
	}

    public void setCalculatedWeightMeasure(Double calculatedWeightMeasure) {
        this.calculatedWeightMeasure = calculatedWeightMeasure;
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

    public void setAapProcesses(Set<AapProcessEntity> aapProcesses) {
        this.aapProcesses = aapProcesses;
    }

    public Set<FishingGearEntity> getFishingGears() {
        return this.fishingGears;
	}

    public void setFishingGears(Set<FishingGearEntity> fishingGears) {
        this.fishingGears = fishingGears;
    }

    public Set<FluxLocationEntity> getFluxLocations() {
        return this.fluxLocations;
	}

    public void setFluxLocations(Set<FluxLocationEntity> fluxLocations) {
        this.fluxLocations = fluxLocations;
    }

    public Set<FluxCharacteristicEntity> getFluxCharacteristics() {
        return this.fluxCharacteristics;
	}

    public void setFluxCharacteristics(Set<FluxCharacteristicEntity> fluxCharacteristics) {
        this.fluxCharacteristics = fluxCharacteristics;
    }

    public Set<AapStockEntity> getAapStocks() {
        return this.aapStocks;
	}

    public void setAapStocks(Set<AapStockEntity> aapStocks) {
        this.aapStocks = aapStocks;
	}

    public Set<FishingTripEntity> getFishingTrips() {
        return this.fishingTrips;
	}

    public void setFishingTrips(Set<FishingTripEntity> fishingTrips) {
        this.fishingTrips = fishingTrips;
    }

    public String getUnitQuantityCode() {
        return unitQuantityCode;
	}

    public void setUnitQuantityCode(String unitQuantityCode) {
        this.unitQuantityCode = unitQuantityCode;
	}

    public Double getCalculatedUnitQuantity() {
        return calculatedUnitQuantity;
	}

    public void setCalculatedUnitQuantity(Double calculatedUnitQuantity) {
        this.calculatedUnitQuantity = calculatedUnitQuantity;
	}

    public String getTerritory() {
        return territory;
	}

    public void setTerritory(String territory) {
        this.territory = territory;
	}

    public String getFaoArea() {
        return faoArea;
	}

    public void setFaoArea(String faoArea) {
        this.faoArea = faoArea;
	}

    public String getIcesStatRectangle() {
        return icesStatRectangle;
	}

    public void setIcesStatRectangle(String icesStatRectangle) {
        this.icesStatRectangle = icesStatRectangle;
	}

    public String getEffortZone() {
        return effortZone;
	}

    public void setEffortZone(String effortZone) {
        this.effortZone = effortZone;
	}

    public String getRfmo() {
        return rfmo;
    }

    public void setRfmo(String rfmo) {
        this.rfmo = rfmo;
    }

    public String getGfcmGsa() {
        return gfcmGsa;
    }

    public void setGfcmGsa(String gfcmGsa) {
        this.gfcmGsa = gfcmGsa;
    }

    public String getGfcmStatRectangle() {
        return gfcmStatRectangle;
    }

    public void setGfcmStatRectangle(String gfcmStatRectangle) {
        this.gfcmStatRectangle = gfcmStatRectangle;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
	}

	@Override
	public String toString() {
		return "FaCatchEntity{" +
				"id=" + id +
				", typeCode='" + typeCode + '\'' +
				", typeCodeListId='" + typeCodeListId + '\'' +
				", speciesCode='" + speciesCode + '\'' +
				", speciesCodeListid='" + speciesCodeListid + '\'' +
				", unitQuantity=" + unitQuantity +
				", weightMeasure=" + weightMeasure +
				", weightMeasureUnitCode='" + weightMeasureUnitCode + '\'' +
				", calculatedWeightMeasure='" + calculatedWeightMeasure + '\'' +
				", usageCode='" + usageCode + '\'' +
				", usageCodeListId='" + usageCodeListId + '\'' +
				", weighingMeansCode='" + weighingMeansCode + '\'' +
				", weighingMeansCodeListId='" + weighingMeansCodeListId + '\'' +
				'}';
	}
}