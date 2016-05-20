package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "activity_registration_location")
public class RegistrationLocationEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(columnDefinition = "text", name = "description")
	private String description;

	@Column(name = "region_code")
	private String regionCode;

	@Column(name = "region_code_list_id")
	private String regionCodeListId;

	@Column(columnDefinition = "text", name = "name")
	private String name;

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
	}


	public RegistrationLocationEntity(String description, String regionCode, String regionCodeListId,
									  String name, String typeCode,
									  String typeCodeListId,
									  RegistrationEventEntity registrationEvent) {
		this.description = description;
		this.regionCode = regionCode;
		this.regionCodeListId = regionCodeListId;
		this.name = name;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.registrationEvent = registrationEvent;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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
}
