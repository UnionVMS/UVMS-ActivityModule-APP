/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "activity_contact_person")
@ToString
public class ContactPersonEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name="SEQ_GEN", sequenceName="ct_person_seq")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
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
		super();
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