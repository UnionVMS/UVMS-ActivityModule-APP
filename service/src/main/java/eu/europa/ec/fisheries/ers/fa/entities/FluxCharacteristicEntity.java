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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "activity_flux_characteristic")
@Data
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
public class FluxCharacteristicEntity implements Serializable {

	@Id
	@Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "flux_char_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

	@Column(name = "type_code", nullable = false)
	private String typeCode;

	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;

	@Column(name = "value_measure", precision = 17, scale = 17)
	private Double valueMeasure;

	@Column(name = "value_measure_unit_code")
	private String valueMeasureUnitCode;

	@Column(name = "calculated_value_measure")
	private Double calculatedValueMeasure;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "value_date_time", length = 29)
	private Date valueDateTime;

	@Column(name = "value_indicator")
	private String valueIndicator;

	@Column(name = "value_code")
	private String valueCode;

	@Column(columnDefinition = "text", name = "value_text")
	private String valueText;

	@Column (name = "value_language_id")
	private String valueLanguageId;

	@Column(name = "value_quantity", precision = 17, scale = 17)
	private Double valueQuantity;

	@Column(name = "value_quantity_code")
	private String valueQuantityCode;

	@Column(name = "calculated_value_quantity")
	private Double calculatedValueQuantity;

	@Column(columnDefinition = "text", name = "description")
	private String description;

	@Column(name = "description_language_id")
	private String descriptionLanguageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fa_catch_id")
    private FaCatchEntity faCatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fishing_activity_id")
    private FishingActivityEntity fishingActivity;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "specified_flux_location_id")
    private FluxLocationEntity fluxLocation;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "specified_flap_document_id")
    private FlapDocumentEntity flapDocument;

    public FluxCharacteristicEntity() {
		super();
	}

}