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

@Entity
@Table(name = "activity_registration_location")
public class RegistrationLocationEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name="SEQ_GEN", sequenceName="reg_loc_seq")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
	private int id;

	@Column(columnDefinition = "text", name = "description")
	private String description;

	@Column(name = "desc_language_id")
	private String descLanguageId;

	@Column(name = "region_code")
	private String regionCode;

	@Column(name = "region_code_list_id")
	private String regionCodeListId;

	@Column(columnDefinition = "text", name = "name")
	private String name;

	@Column(name = "name_language_id")
	private String nameLanguageId;

	@Column(name = "type_code")
	private String typeCode;

	@Column(name = "type_code_list_id")
	private String typeCodeListId;

	@Column(name = "location_country_id")
	private String locationCountryId;

	@Column(name = "location_country_scheme_id")
	private String locationCountrySchemeId;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "registrationLocation")
	private RegistrationEventEntity registrationEvent;

	public RegistrationLocationEntity() {
		super();
	}

	public int getId() {
		return this.id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRegionCode() {
		return this.regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getRegionCodeListId() {
		return this.regionCodeListId;
	}

	public void setRegionCodeListId(String regionCodeListId) {
		this.regionCodeListId = regionCodeListId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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

	public RegistrationEventEntity getRegistrationEvent() {
		return registrationEvent;
	}

	public void setRegistrationEvent(RegistrationEventEntity registrationEvent) {
		this.registrationEvent = registrationEvent;
	}

	public String getLocationCountryId() {
		return locationCountryId;
	}

	public void setLocationCountryId(String locationCountryId) {
		this.locationCountryId = locationCountryId;
	}

	public String getLocationCountrySchemeId() {
		return locationCountrySchemeId;
	}

	public void setLocationCountrySchemeId(String locationCountrySchemeId) {
		this.locationCountrySchemeId = locationCountrySchemeId;
	}

	public String getDescLanguageId() {
		return descLanguageId;
	}

	public void setDescLanguageId(String descLanguageId) {
		this.descLanguageId = descLanguageId;
	}

	public String getNameLanguageId() {
		return nameLanguageId;
	}

	public void setNameLanguageId(String nameLanguageId) {
		this.nameLanguageId = nameLanguageId;
	}

	@Override
	public String toString() {
		return "RegistrationLocationEntity{" +
				"id=" + id +
				", description='" + description + '\'' +
				", regionCode='" + regionCode + '\'' +
				", regionCodeListId='" + regionCodeListId + '\'' +
				", name='" + name + '\'' +
				", typeCode='" + typeCode + '\'' +
				", typeCodeListId='" + typeCodeListId + '\'' +
				", locationCountryId='" + locationCountryId + '\'' +
				", locationCountrySchemeId='" + locationCountrySchemeId + '\'' +
				'}';
	}
}