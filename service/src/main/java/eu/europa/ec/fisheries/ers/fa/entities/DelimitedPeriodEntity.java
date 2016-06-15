package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "activity_delimited_period")
public class DelimitedPeriodEntity implements Serializable {

	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_activity_id")
	private FishingActivityEntity fishingActivity;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_trip_id")
	private FishingTripEntity fishingTrip;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date", length = 29)
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date", length = 29)
	private Date endDate;
	
	@Column(name = "duration", precision = 17, scale = 17)
	private Double duration;

	public DelimitedPeriodEntity() {
	}
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public FishingActivityEntity getFishingActivity() {
		return this.fishingActivity;
	}

	public void setFishingActivity(
			FishingActivityEntity fishingActivity) {
		this.fishingActivity = fishingActivity;
	}

	
	public FishingTripEntity getFishingTrip() {
		return this.fishingTrip;
	}

	public void setFishingTrip(FishingTripEntity fishingTrip) {
		this.fishingTrip = fishingTrip;
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

	
	public Double getDuration() {
		return this.duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

}
