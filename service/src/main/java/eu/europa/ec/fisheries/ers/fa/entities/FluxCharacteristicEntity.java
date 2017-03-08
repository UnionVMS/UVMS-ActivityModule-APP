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
import java.util.Date;

@Entity
@Table(name = "activity_flux_characteristic")
public class FluxCharacteristicEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

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

	public FluxCharacteristicEntity() {
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
		return this.fishingActivity;
	}

	public void setFishingActivity(
			FishingActivityEntity fishingActivity) {
		this.fishingActivity = fishingActivity;
	}

	public FluxLocationEntity getFluxLocation() {
		return this.fluxLocation;
	}

	public void setFluxLocation(
			FluxLocationEntity fluxLocation) {
		this.fluxLocation = fluxLocation;
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

	public Double getValueMeasure() {
		return this.valueMeasure;
	}

	public void setValueMeasure(Double valueMeasure) {
		this.valueMeasure = valueMeasure;
	}

	public Date getValueDateTime() {
		return this.valueDateTime;
	}

	public void setValueDateTime(Date valueDateTime) {
		this.valueDateTime = valueDateTime;
	}

	public String getValueIndicator() {
		return this.valueIndicator;
	}

	public void setValueIndicator(String valueIndicator) {
		this.valueIndicator = valueIndicator;
	}

	public String getValueCode() {
		return this.valueCode;
	}

	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}

	public String getValueText() {
		return this.valueText;
	}

	public void setValueText(String valueText) {
		this.valueText = valueText;
	}

	public Double getValueQuantity() {
		return this.valueQuantity;
	}

	public void setValueQuantity(Double valueQuantity) {
		this.valueQuantity = valueQuantity;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValueLanguageId() {
		return valueLanguageId;
	}

	public void setValueLanguageId(String valueLanguageId) {
		this.valueLanguageId = valueLanguageId;
	}

	public String getDescriptionLanguageId() {
		return descriptionLanguageId;
	}

	public void setDescriptionLanguageId(String descriptionLanguageId) {
		this.descriptionLanguageId = descriptionLanguageId;
	}

	public String getValueMeasureUnitCode() {
		return valueMeasureUnitCode;
	}

	public void setValueMeasureUnitCode(String valueMeasureUnitCode) {
		this.valueMeasureUnitCode = valueMeasureUnitCode;
	}

	public Double getCalculatedValueMeasure() {
		return calculatedValueMeasure;
	}

	public void setCalculatedValueMeasure(Double calculatedValueMeasure) {
		this.calculatedValueMeasure = calculatedValueMeasure;
	}

	public String getValueQuantityCode() {
		return valueQuantityCode;
	}

	public void setValueQuantityCode(String valueQuantityCode) {
		this.valueQuantityCode = valueQuantityCode;
	}

	public Double getCalculatedValueQuantity() {
		return calculatedValueQuantity;
	}

	public void setCalculatedValueQuantity(Double calculatedValueQuantity) {
		this.calculatedValueQuantity = calculatedValueQuantity;
	}

	public FlapDocumentEntity getFlapDocument() {
		return flapDocument;
	}

	public void setFlapDocument(FlapDocumentEntity flapDocument) {
		this.flapDocument = flapDocument;
	}
}