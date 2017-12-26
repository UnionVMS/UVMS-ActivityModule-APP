/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activity_registration_location")
@Data
@NoArgsConstructor
public class RegistrationLocationEntity implements Serializable {

	@Id
	@Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "reg_loc_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

	@Column(columnDefinition = "text")
	private String description;

	@Column(name = "desc_language_id")
	private String descLanguageId;

	@Column(name = "region_code")
	private String regionCode;

	@Column(name = "region_code_list_id")
	private String regionCodeListId;

	@Column(columnDefinition = "text")
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

	@OneToOne(mappedBy = "registrationLocation")
	private RegistrationEventEntity registrationEvent;

}