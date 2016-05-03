package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "activity_delimited_period")
public class DelimitedPeriod implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date", length = 29)
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date", length = 29)
	private Date endDate;
	
	@Column(name = "duration")
	private String duration;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "delimitedPeriod")
	private Set<FishingActivity> fishingActivities = new HashSet<FishingActivity>(
			0);

	public DelimitedPeriod() {
	}

	public DelimitedPeriod(int id) {
		this.id = id;
	}

	public DelimitedPeriod(int id, Date startDate, Date endDate,
			String duration, Set<FishingActivity> fishingActivities) {
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.duration = duration;
		this.fishingActivities = fishingActivities;
	}

	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	
	public Set<FishingActivity> getFishingActivities() {
		return this.fishingActivities;
	}

	public void setFishingActivities(Set<FishingActivity> fishingActivities) {
		this.fishingActivities = fishingActivities;
	}

}
