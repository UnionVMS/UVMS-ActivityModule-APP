package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "activity_country")
public class CountryEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "registration_location_id")
	private RegistrationLocationEntity registrationLocation;

	@Column(name = "country_id", nullable = false)
	private String countryId;

	@Column(name = "country_scheme_id", nullable = false)
	private String countrySchemeId;

	public CountryEntity() {
	}

	public CountryEntity(String countryId,
						 String countrySchemeId) {
		this.countryId = countryId;
		this.countrySchemeId = countrySchemeId;
	}

	public CountryEntity(RegistrationLocationEntity registrationLocation,
						 String countryId,
						 String countrySchemeId) {
		this.registrationLocation = registrationLocation;
		this.countryId = countryId;
		this.countrySchemeId = countrySchemeId;
	}

	public int getId() {
		return this.id;
	}

	public RegistrationLocationEntity getRegistrationLocation() {
		return registrationLocation;
	}

	public void setRegistrationLocation(RegistrationLocationEntity registrationLocation) {
		this.registrationLocation = registrationLocation;
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public String getCountrySchemeId() {
		return countrySchemeId;
	}

	public void setCountrySchemeId(String countrySchemeId) {
		this.countrySchemeId = countrySchemeId;
	}
}
