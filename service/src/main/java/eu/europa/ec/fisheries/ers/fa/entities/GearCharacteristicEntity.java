/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "activity_gear_characteristic")
public class GearCharacteristicEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_gear_id")
	private FishingGearEntity fishingGear;

	@Column(name = "type_code", nullable = false)
	private String typeCode;

	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;

	@Column(name = "description")
	private String description;

	@Column(name = "desc_language_id")
	private String descLanguageId;

	@Column(name = "value_measure", precision = 17, scale = 17)
	private Double valueMeasure;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "value_date_time", length = 29)
	private Date valueDateTime;

	@Column(name = "value_indicator")
	private String valueIndicator;

	@Column(name = "value_code")
	private String valueCode;

	@Column(name = "value_text")
	private String valueText;

	@Column(name = "value_quantity", precision = 17, scale = 17)
	private Double valueQuantity;

	public GearCharacteristicEntity() {
		super();
	}

	public int getId() {
		return this.id;
	}

	public FishingGearEntity getFishingGear() {
		return this.fishingGear;
	}

	public void setFishingGear(FishingGearEntity fishingGear) {
		this.fishingGear = fishingGear;
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

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getDescLanguageId() {
		return descLanguageId;
	}

	public void setDescLanguageId(String descLanguageId) {
		this.descLanguageId = descLanguageId;
	}
}