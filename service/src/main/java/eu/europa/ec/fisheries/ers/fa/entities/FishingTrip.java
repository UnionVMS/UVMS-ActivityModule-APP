package eu.europa.ec.fisheries.ers.fa.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "activity_fishing_trip")
public class FishingTrip {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@Column(name = "trip_id", nullable = false)
	private String tripId;
	
	@Column(name = "trip_scheme_id", nullable = false)
	private String tripSchemeId;
	
	@Column(name = "type_code")
	private String typeCode;
	
	@Column(name = "type_code_list_id")
	private String typeCodeListId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date", length = 29)
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date", length = 29)
	private Date endDate;
	
	@Column(name = "duration")
	private String duration;
	
	@Column(name = "duration_unit_code", precision = 17, scale = 17)
	private Double durationUnitCode;
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "FishingTrip", cascade = CascadeType.ALL)
	private FaCatch faCatchs;
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "FishingTrip", cascade = CascadeType.ALL)
	private FishingActivity fishingActivities ;

	public FishingTrip() {
	}

	public FishingTrip(int id, String tripId, String tripSchemeId) {
		this.id = id;
		this.tripId = tripId;
		this.tripSchemeId = tripSchemeId;
	}

	public FishingTrip(int id, String tripId, String tripSchemeId,
			String typeCode, String typeCodeListId, Date startDate,
			Date endDate, String duration, Double durationUnitCode,
			FaCatch faCatchs, FishingActivity fishingActivities) {
		this.id = id;
		this.tripId = tripId;
		this.tripSchemeId = tripSchemeId;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.duration = duration;
		this.durationUnitCode = durationUnitCode;
		this.faCatchs = faCatchs;
		this.fishingActivities = fishingActivities;
	}

	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public String getTripId() {
		return this.tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}


	public String getTripSchemeId() {
		return this.tripSchemeId;
	}

	public void setTripSchemeId(String tripSchemeId) {
		this.tripSchemeId = tripSchemeId;
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

	
	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	
	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	
	public String getDuration() {
		return this.duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	
	public Double getDurationUnitCode() {
		return this.durationUnitCode;
	}

	public void setDurationUnitCode(Double durationUnitCode) {
		this.durationUnitCode = durationUnitCode;
	}

	

	public FaCatch getFaCatchs() {
		return faCatchs;
	}

	public void setFaCatchs(FaCatch faCatchs) {
		this.faCatchs = faCatchs;
	}

	public FishingActivity getFishingActivities() {
		return fishingActivities;
	}

	public void setFishingActivities(FishingActivity fishingActivities) {
		this.fishingActivities = fishingActivities;
	}

	

}
