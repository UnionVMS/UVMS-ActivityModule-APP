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
@Table(name = "activity_delimited_period")
public class DelimitedPeriodEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name="SEQ_GEN", sequenceName="del_period_seq", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
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

	@Column(name = "duration_unit_code")
	private String durationUnitCode;

	@Column(name = "calculated_duration")
	private Double calculatedDuration;

	public DelimitedPeriodEntity() {
		super();
	}

	public DelimitedPeriodEntity(FishingActivityEntity fishingActivity, FishingTripEntity fishingTrip, Date startDate, Date endDate, Double duration) {
		this.fishingActivity = fishingActivity;
		this.fishingTrip = fishingTrip;
		this.startDate = startDate;
		this.endDate = endDate;
		this.duration = duration;
	}

	public int getId() {
		return this.id;
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

	public String getDurationUnitCode() {
		return durationUnitCode;
	}

	public void setDurationUnitCode(String durationUnitCode) {
		this.durationUnitCode = durationUnitCode;
	}

	public Double getCalculatedDuration() {
		return calculatedDuration;
	}

	public void setCalculatedDuration(Double calculatedDuration) {
		this.calculatedDuration = calculatedDuration;
	}

	@Override
	public String toString() {
		return "DelimitedPeriodEntity{" +
				"id=" + id +
				", fishingActivity=" + fishingActivity +
				", fishingTrip=" + fishingTrip +
				", startDate=" + startDate +
				", endDate=" + endDate +
				", duration=" + duration +
				'}';
	}
}