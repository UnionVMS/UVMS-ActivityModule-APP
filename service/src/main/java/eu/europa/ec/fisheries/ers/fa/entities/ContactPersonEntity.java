package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "activity_contact_person")
public class ContactPersonEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "given_name")
	private String givenName;
	
	@Column(name = "middle_name")
	private String middleName;
	
	@Column(name = "family_name")
	private String familyName;
	
	@Column(name = "family_name_prefix")
	private String familyNamePrefix;
	
	@Column(name = "name_suffix")
	private String nameSuffix;
	
	@Column(name = "gender")
	private String gender;
	
	@Column(name = "alias")
	private String alias;
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "contactPerson")
	private ContactPartyEntity contactParty;

	public ContactPersonEntity() {
	}


	public ContactPersonEntity(String title, String givenName,
							   String middleName, String familyName, String familyNamePrefix,
							   String nameSuffix, String gender, String alias,
							   ContactPartyEntity contactParty) {
		this.title = title;
		this.givenName = givenName;
		this.middleName = middleName;
		this.familyName = familyName;
		this.familyNamePrefix = familyNamePrefix;
		this.nameSuffix = nameSuffix;
		this.gender = gender;
		this.alias = alias;
		this.contactParty = contactParty;
	}


	public int getId() {
		return this.id;
	}

	
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getGivenName() {
		return this.givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	
	public String getMiddleName() {
		return this.middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	
	public String getFamilyName() {
		return this.familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	
	public String getFamilyNamePrefix() {
		return this.familyNamePrefix;
	}

	public void setFamilyNamePrefix(String familyNamePrefix) {
		this.familyNamePrefix = familyNamePrefix;
	}

	
	public String getNameSuffix() {
		return this.nameSuffix;
	}

	public void setNameSuffix(String nameSuffix) {
		this.nameSuffix = nameSuffix;
	}

	
	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	
	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public ContactPartyEntity getContactParty() {
		return contactParty;
	}

	public void setContactParty(ContactPartyEntity contactParty) {
		this.contactParty = contactParty;
	}
}
