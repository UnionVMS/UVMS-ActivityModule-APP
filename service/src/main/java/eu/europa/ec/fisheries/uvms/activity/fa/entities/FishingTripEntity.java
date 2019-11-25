/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "activity_fishing_trip")
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"delimitedPeriods"})
@ToString(exclude = {"delimitedPeriods"})
@Data
public class FishingTripEntity implements Serializable {

	@Id
	@Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "fa_trip_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

	@ManyToOne
	@JoinColumn(name = "fa_catch_id")
	private FaCatchEntity faCatch;

    @OneToMany(mappedBy = "fishingTrip", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FishingActivityEntity> fishingActivities = new HashSet<>();

	@Column(name = "type_code")
	private String typeCode;

	@Column(name = "type_code_list_id")
	private String typeCodeListId;

	@OneToMany(mappedBy = "fishingTrip", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<DelimitedPeriodEntity> delimitedPeriods = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fishing_trip_id", referencedColumnName = "id")
	private FishingTripIdentifierEntity fishingTripIdentifier;

    public void addDelimitedPeriods(DelimitedPeriodEntity periodEntity) {
        delimitedPeriods.add(periodEntity);
        periodEntity.setFishingTrip(this);
    }

    public void removeDelimitedPeriods(DelimitedPeriodEntity area) {
        delimitedPeriods.remove(area);
        area.setFishingTrip(null);
    }
}
