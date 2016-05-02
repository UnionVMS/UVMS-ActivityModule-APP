package eu.europa.ec.fisheries.ers.fa.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "activity_flux_characteristic")
public class FluxCharacteristic {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_activity_id")
	private FishingActivity fishingActivity;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flux_location_id")
	private FluxLocation fluxLocation;
	
	@Column(name = "type_code", nullable = false)
	private String typeCode;
	
	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;
	
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

	public FluxCharacteristic() {
	}

	public FluxCharacteristic(int id, String typeCode, String typeCodeListId) {
		this.id = id;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
	}

	public FluxCharacteristic(int id, FishingActivity fishingActivity,
							  FluxLocation fluxLocation, String typeCode, String typeCodeListId,
							  Double valueMeasure, Date valueDateTime, String valueIndicator,
							  String valueCode, String valueText, Double valueQuantity) {
		this.id = id;
		this.fishingActivity = fishingActivity;
		this.fluxLocation = fluxLocation;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
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


	public FishingActivity getFishingActivity() {
		return this.fishingActivity;
	}

	public void setFishingActivity(FishingActivity fishingActivity) {
		this.fishingActivity = fishingActivity;
	}

	
	public FluxLocation getFluxLocation() {
		return this.fluxLocation;
	}

	public void setFluxLocation(FluxLocation fluxLocation) {
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

}
