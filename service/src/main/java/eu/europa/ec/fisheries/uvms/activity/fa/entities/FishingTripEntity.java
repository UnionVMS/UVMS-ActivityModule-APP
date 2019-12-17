/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import eu.europa.ec.fisheries.uvms.activity.service.mapper.DelimitedPeriodMapper;
import eu.europa.ec.fisheries.uvms.activity.service.util.Utils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingTrip;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "activity_fishing_trip")
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"delimitedPeriods"})
@ToString(exclude = {"delimitedPeriods"})
@Data
public class FishingTripEntity implements Serializable {

    @EmbeddedId
    private FishingTripKey fishingTripKey;

    @OneToMany(mappedBy = "fishingTrip", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FaCatchEntity> catchEntities = new HashSet<>();

    @OneToMany(mappedBy = "fishingTrip", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FishingActivityEntity> fishingActivities = new HashSet<>();

	@Column(name = "type_code")
	private String typeCode;

	@Column(name = "type_code_list_id")
	private String typeCodeListId;

	@OneToMany(mappedBy = "fishingTrip", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<DelimitedPeriodEntity> delimitedPeriods = new HashSet<>();

    @Column(name = "calculated_trip_start_date")
    private Instant calculatedTripStartDate;

    @Column(name = "calculated_trip_end_date")
    private Instant calculatedTripEndDate;

    @Embedded
    private DelimitedPeriodEmbeddable delimitedPeriod;

    public void addDelimitedPeriods(DelimitedPeriodEntity periodEntity) {
        delimitedPeriods.add(periodEntity);
        periodEntity.setFishingTrip(this);
    }

    public void removeDelimitedPeriods(DelimitedPeriodEntity area) {
        delimitedPeriods.remove(area);
        area.setFishingTrip(null);
    }

    public static FishingTripEntity create(FishingTrip fishingTrip) {
        if (fishingTrip == null) {
            return null;
        }

        FishingTripEntity fishingTripEntity = new FishingTripEntity();

        List<IDType> ids = fishingTrip.getIDS();
        IDType idType = ids.get(0); // We only take the first ID because we don't expect more than one ID
        if (idType != null) {
            FishingTripKey fishingTripKey = new FishingTripKey(idType.getValue(), idType.getSchemeID());
            fishingTripEntity.setFishingTripKey(fishingTripKey);
        }

        List<DelimitedPeriod> specifiedDelimitedPeriods = fishingTrip.getSpecifiedDelimitedPeriods();
        for (DelimitedPeriod delimitedPeriod : Utils.safeIterable(specifiedDelimitedPeriods)) {
            fishingTripEntity.addDelimitedPeriods(DelimitedPeriodMapper.INSTANCE.mapToDelimitedPeriodEntity(delimitedPeriod));
        }

        CodeType typeCode = fishingTrip.getTypeCode();
        if (typeCode != null) {
            fishingTripEntity.setTypeCode(typeCode.getValue());
            fishingTripEntity.setTypeCodeListId(typeCode.getListID());
        }

        return fishingTripEntity;
    }

    public FishingTrip convert() {
        FishingTrip fishingTrip = new FishingTrip();

        List<IDType> idTypes = new ArrayList<>();
        IDType idType = new IDType();
        idType.setValue(fishingTripKey.getTripId());
        idType.setSchemeID(fishingTripKey.getTripSchemeId());
        idTypes.add(idType);

        fishingTrip.setIDS(idTypes);

        List<DelimitedPeriod> convertedDelimitedPeriods = DelimitedPeriodMapper.INSTANCE.mapToDelimitedPeriodList(this.delimitedPeriods);
        fishingTrip.setSpecifiedDelimitedPeriods(convertedDelimitedPeriods);

        if (typeCode != null && typeCodeListId != null) {
            CodeType codeType = new CodeType();
            codeType.setValue(typeCode);
            codeType.setListID(typeCodeListId);
            fishingTrip.setTypeCode(codeType);
        }

        return fishingTrip;
    }
}
