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
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import eu.europa.ec.fisheries.ers.fa.utils.UnitCodeEnum;
import lombok.Data;

@Entity
@Table(name = "activity_delimited_period")
@Data
public class DelimitedPeriodEntity implements Serializable {

	@Id
	@Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "del_period_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_activity_id")
	private FishingActivityEntity fishingActivity;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_trip_id")
	private FishingTripEntity fishingTrip;
	
	@Column(name = "start_date", length = 29)
	private Instant startDate;
	
	@Column(name = "end_date", length = 29)
	private Instant endDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "value", column = @Column(name = "durationmeasure_value")),
            @AttributeOverride( name = "unitCode", column = @Column(name = "durationmeasure_unitCode")),
            @AttributeOverride( name = "unitCodeListVersionID", column = @Column(name = "durationmeasure_unitCodeListID"))
    })
	private MeasureType durationMeasure =  new MeasureType();

	@Column(name = "calculated_duration")
	private Double calculatedDuration;

    @PrePersist
    public void prePersist(){
        if (durationMeasure != null && durationMeasure.getUnitCode() != null && durationMeasure.getValue() != null) {
            UnitCodeEnum unitCodeEnum = UnitCodeEnum.getUnitCode(durationMeasure.getUnitCode());
            if (unitCodeEnum != null) {
                BigDecimal measuredValue = new BigDecimal(durationMeasure.getValue());
                BigDecimal result = measuredValue.multiply(new BigDecimal(unitCodeEnum.getConversionFactor()));
                calculatedDuration = result.doubleValue();
            }
        }
    }

    public Optional<Date> getStartDateAsDate() {
        if (startDate == null) {
            return  Optional.empty();
        }

        return Optional.of(Date.from(startDate));
    }

    public Optional<Date> getEndDateAsDate() {
        if (endDate == null) {
            return Optional.empty();
        }

        return Optional.of(Date.from(endDate));
    }
}