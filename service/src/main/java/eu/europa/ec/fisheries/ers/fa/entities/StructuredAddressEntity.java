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

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "activity_structured_address")
@EqualsAndHashCode(exclude = {"contactParty"})
@Data
public class StructuredAddressEntity implements Serializable {

    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "str_add_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_party_id")
    private ContactPartyEntity contactParty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flux_location_id")
    private FluxLocationEntity fluxLocation;

    @Column(name = "block_name", length = 1000)
    private String blockName;

    @Column(name = "building_name", length = 1000)
    private String buildingName;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "city_subdivision_name")
    private String citySubdivisionName;

    private String country;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "country_subdivision_name")
    private String countrySubdivisionName;

    @Column(name = "country_id_scheme_id")
    private String countryIdSchemeId;

    @Column(name = "address_id")
    private String addressId;

    @Column(name = "plot_id", length = 1000)
    private String plotId;

    @Column(name = "post_office_box")
    private String postOfficeBox;

    private String postcode;

    @Column(name = "streetname", length = 1000)
    private String streetName;

    @Column(name = "structured_address_type")
    private String structuredAddressType;

}