/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.uvms.mapper.GeometryMapper;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.uvms.mapper.GeometryMapper;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "activity_flux_location")
public class FluxLocationEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name="SEQ_GEN", sequenceName="flux_loc_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

	@Type(type = "org.hibernate.spatial.GeometryType")
	@Column(name = "geom")
	private Geometry geom;

	@Transient
	private String wkt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fa_catch_id")
	private FaCatchEntity faCatch;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_activity_id")
	private FishingActivityEntity fishingActivity;

	@Column(name = "type_code", nullable = false)
	private String typeCode;

	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;

	@Column(name = "country_id")
	private String countryId;

	@Column(name = "rfmo_code")
	private String rfmoCode;

	@Column(name = "longitude", precision = 17, scale = 17)
	private Double longitude;

	@Column(name = "latitude", precision = 17, scale = 17)
	private Double latitude;

	@Column(name = "flux_location_type", nullable = false)
	private String fluxLocationType;

	@Column(name = "country_id_scheme_id")
	private String countryIdSchemeId;

	@Column(name = "flux_location_identifier")
	private String fluxLocationIdentifier;

	@Column(name = "flux_location_identifier_scheme_id")
	private String fluxLocationIdentifierSchemeId;

	@Column(name = "geopolitical_region_code")
	private String geopoliticalRegionCode;

	@Column(name = "geopolitical_region_code_list_id")
	private String geopoliticalRegionCodeListId;

	@Column(columnDefinition = "text", name = "name")
	private String name;

	@Column(name = "name_laguage_id")
	private String nameLanguageId;

	@Column(name = "sovereign_rights_country_code")
	private String sovereignRightsCountryCode;

	@Column(name = "jurisdiction_country_code")
	private String jurisdictionCountryCode;

	@Column(name = "altitude", precision = 17, scale = 17)
	private Double altitude;

	@Column(name = "system_id")
	private String systemId;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "fluxLocation")
	private FluxCharacteristicEntity fluxCharacteristic;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fluxLocation", cascade = CascadeType.ALL)
	private Set<StructuredAddressEntity> structuredAddresses;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gear_problem_id")
	private GearProblemEntity gearProblem;

	public FluxLocationEntity() {
		super();
	}

	public int getId() {
		return this.id;
	}

	public FaCatchEntity getFaCatch() {
		return this.faCatch;
	}

	public void setFaCatch(FaCatchEntity faCatch) {
		this.faCatch = faCatch;
	}

	public FishingActivityEntity getFishingActivity() {
		return fishingActivity;
	}

	public void setFishingActivity(FishingActivityEntity fishingActivity) {
		this.fishingActivity = fishingActivity;
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

	public String getRfmoCode() {
		return this.rfmoCode;
	}

	public void setRfmoCode(String rfmoCode) {
		this.rfmoCode = rfmoCode;
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

	public String getCountryIdSchemeId() {
		return this.countryIdSchemeId;
	}

	public void setCountryIdSchemeId(String countryIdSchemeId) {
		this.countryIdSchemeId = countryIdSchemeId;
	}

	public String getFluxLocationIdentifier() {
		return this.fluxLocationIdentifier;
	}

	public void setFluxLocationIdentifier(String fluxLocationIdentifier) {
		this.fluxLocationIdentifier = fluxLocationIdentifier;
	}

	public String getFluxLocationIdentifierSchemeId() {
		return this.fluxLocationIdentifierSchemeId;
	}

	public void setFluxLocationIdentifierSchemeId(
			String fluxLocationIdentifierSchemeId) {
		this.fluxLocationIdentifierSchemeId = fluxLocationIdentifierSchemeId;
	}

	public String getGeopoliticalRegionCode() {
		return this.geopoliticalRegionCode;
	}

	public void setGeopoliticalRegionCode(String geopoliticalRegionCode) {
		this.geopoliticalRegionCode = geopoliticalRegionCode;
	}

	public String getGeopoliticalRegionCodeListId() {
		return this.geopoliticalRegionCodeListId;
	}

	public void setGeopoliticalRegionCodeListId(
			String geopoliticalRegionCodeListId) {
		this.geopoliticalRegionCodeListId = geopoliticalRegionCodeListId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSovereignRightsCountryCode() {
		return this.sovereignRightsCountryCode;
	}

	public void setSovereignRightsCountryCode(String sovereignRightsCountryCode) {
		this.sovereignRightsCountryCode = sovereignRightsCountryCode;
	}

	public String getJurisdictionCountryCode() {
		return this.jurisdictionCountryCode;
	}

	public void setJurisdictionCountryCode(String jurisdictionCountryCode) {
		this.jurisdictionCountryCode = jurisdictionCountryCode;
	}

	public Double getAltitude() {
		return this.altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public String getSystemId() {
		return this.systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}


	public Set<StructuredAddressEntity> getStructuredAddresses() {
		return this.structuredAddresses;
	}

	public void setStructuredAddresses(
			Set<StructuredAddressEntity> structuredAddresses) {
		this.structuredAddresses = structuredAddresses;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getNameLanguageId() {
		return nameLanguageId;
	}

	public void setNameLanguageId(String nameLanguageId) {
		this.nameLanguageId = nameLanguageId;
	}

	public Geometry getGeom() {
		return geom;
	}

	public void setGeom(Geometry geom) {
		this.geom = geom;
	}

	public String getWkt() {
		return wkt;
	}

	public FluxCharacteristicEntity getFluxCharacteristic() {
		return fluxCharacteristic;
	}

	public void setFluxCharacteristic(FluxCharacteristicEntity fluxCharacteristic) {
		this.fluxCharacteristic = fluxCharacteristic;
	}

	@Override
	public String toString() {
		return "FluxLocationEntity{" +
				"id=" + id +
				", faCatch=" + faCatch +
				", fishingActivity=" + fishingActivity +
				", typeCode='" + typeCode + '\'' +
				", typeCodeListId='" + typeCodeListId + '\'' +
				", countryId='" + countryId + '\'' +
				", rfmoCode='" + rfmoCode + '\'' +
				", longitude=" + longitude +
				", latitude=" + latitude +
				", fluxLocationType='" + fluxLocationType + '\'' +
				", countryIdSchemeId='" + countryIdSchemeId + '\'' +
				", fluxLocationIdentifier='" + fluxLocationIdentifier + '\'' +
				", fluxLocationIdentifierSchemeId='" + fluxLocationIdentifierSchemeId + '\'' +
				", geopoliticalRegionCode='" + geopoliticalRegionCode + '\'' +
				", geopoliticalRegionCodeListId='" + geopoliticalRegionCodeListId + '\'' +
				", name='" + name + '\'' +
				", sovereignRightsCountryCode='" + sovereignRightsCountryCode + '\'' +
				", jurisdictionCountryCode='" + jurisdictionCountryCode + '\'' +
				", altitude=" + altitude +
				", systemId='" + systemId + '\'' +
				'}';
	}

	@PostLoad
	private void onLoad() {
		if(this.geom != null){
			this.wkt = GeometryMapper.INSTANCE.geometryToWkt(this.geom).getValue();
		}
	}

	public GearProblemEntity getGearProblem() {
		return gearProblem;
	}

	public void setGearProblem(GearProblemEntity gearProblem) {
		this.gearProblem = gearProblem;
	}
}