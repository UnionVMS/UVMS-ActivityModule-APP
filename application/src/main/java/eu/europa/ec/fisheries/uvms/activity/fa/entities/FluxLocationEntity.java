/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import eu.europa.ec.fisheries.uvms.activity.fa.utils.FluxLocationCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FluxLocationEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.TextType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "activity_flux_location")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = {"fluxLocationIdentifier", "fluxLocationIdentifierSchemeId"})
@ToString(exclude = {"structuredAddresses", "fluxCharacteristic"})
public class FluxLocationEntity implements Serializable {

	@Id
	@Column(unique = true, nullable = false)
	@SequenceGenerator(name = "SEQ_GEN", sequenceName = "flux_loc_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
	private int id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type_code", nullable = false)
	private FluxLocationEnum typeCode;

	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;

	@XmlElement(nillable = true)
	@Column(name = "country_id")
	private String countryId;

	@Column(name = "country_id_scheme_id")
	private String countryIdSchemeId;

	@Column(name = "flux_location_identifier")
	private String fluxLocationIdentifier;

	@Column(name = "flux_location_identifier_scheme_id")
	private String fluxLocationIdentifierSchemeId;

	@Column(columnDefinition = "text", name = "namevalue")
	private String name;

	@Column(name = "name_laguage_id")
	private String nameLanguageId;

	@Column(name = "rfmo_code")
	private String regionalFisheriesManagementOrganizationCode;

	@Column(name = "rfmo_code_list_id")
	private String regionalFisheriesManagementOrganizationCodeListId;

	@OneToOne(mappedBy = "fluxLocation")
	private FluxCharacteristicEntity fluxCharacteristic;

	public List<TextType> getNames(){
        List<TextType> names = null;
		if (StringUtils.isNotEmpty(name)){
            names = new ArrayList<>();
            names.add(new TextType(name, nameLanguageId, null));
		}
		return names;
	}
}
