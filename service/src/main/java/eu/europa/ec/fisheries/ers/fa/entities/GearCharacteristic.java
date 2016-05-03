package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "activity_gear_characteristic")
public class GearCharacteristic implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_gear_id")
	private FishingGear fishingGear;
	
	@Column(name = "type_code", nullable = false)
	private String typeCode;
	
	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;
	
	@Column(columnDefinition = "text", name = "description")
	private String description;
	
	@Column(name = "value_measure", precision = 17, scale = 17)
	private Double valueMeasure;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "value_date_time", length = 29)
	private Date valueDateTime;
	
	@Column(name = "value_indicator")
	private String valueIndicator;
	
	@Column(name = "value_code")
	private String valueCode;
	
	@Column(columnDefinition = "text", name = "value_text")
	private String valueText;
	
	@Column(name = "value_quantity", precision = 17, scale = 17)
	private Double valueQuantity;

	public GearCharacteristic() {
	}

	public GearCharacteristic(int id, String typeCode, String typeCodeListId) {
		this.id = id;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
	}

	public GearCharacteristic(int id, FishingGear fishingGear, String typeCode,
			String typeCodeListId, String description, Double valueMeasure,
			Date valueDateTime, String valueIndicator, String valueCode,
			String valueText, Double valueQuantity) {
		this.id = id;
		this.fishingGear = fishingGear;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.description = description;
		this.valueMeasure = valueMeasure;
		this.valueDateTime = valueDateTime;
		this.valueIndicator = valueIndicator;
		this.valueCode = valueCode;
		this.valueText = valueText;
		this.valueQuantity = valueQuantity;
	}

	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public FishingGear getFishingGear() {
		return this.fishingGear;
	}

	public void setFishingGear(FishingGear fishingGear) {
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

}
