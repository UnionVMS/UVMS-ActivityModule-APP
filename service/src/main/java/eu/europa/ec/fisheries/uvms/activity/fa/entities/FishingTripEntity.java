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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NamedQueries({
        @NamedQuery(name = FishingTripEntity.FIND_CURRENT_TRIP,
                query = "SELECT fa.fishingTrip FROM FishingActivityEntity fa " +
                        "INNER JOIN fa.faReportDocument frd " +
                        "INNER JOIN frd.vesselTransportMeans vtm " +
                        "INNER JOIN vtm.vesselIdentifiers vi " +
                        "WHERE vi.vesselIdentifierId = :vesselId " +
                        "AND vi.vesselIdentifierSchemeId = :vesselSchemeId")

		/*
		//TODO: We need to rewrite this...
		@NamedQuery(name = FishingTripIdentifierEntity.FIND_PREVIOUS_TRIP,
				query = "SELECT fa.fishingTrip.fishingTripIdentifier FROM FishingActivityEntity fa " +
						"INNER JOIN fa.fishingTrip ft " +
						"INNER JOIN fa.faReportDocument frd " +
						"INNER JOIN frd.vesselTransportMeans vtm " +
						"INNER JOIN vtm.vesselIdentifiers vi " +
						"WHERE vi.vesselIdentifierId = :vesselId " +
						"AND vi.vesselIdentifierSchemeId = :vesselSchemeId " +
						"AND frd.acceptedDatetime < (" +
													"SELECT max(sfrd.acceptedDatetime) " +
													"FROM FishingTripIdentifierEntity sfti " +
													"INNER JOIN sfti.fishingTrip sft " +
													"INNER JOIN sft.fishingActivity sfa " +
													"INNER JOIN sfa.faReportDocument sfrd " +
													"WHERE sfti.tripId = :tripId" +
													")" +
						"ORDER BY frd.acceptedDatetime ASC"),

		@NamedQuery(name = FishingTripIdentifierEntity.FIND_NEXT_TRIP,
				query = "SELECT fti from FishingTripIdentifierEntity fti " +
						"INNER JOIN fti.fishingTrip ft " +
						"INNER JOIN ft.fishingActivity fa " +
						"INNER JOIN fa.faReportDocument frd " +
						"INNER JOIN frd.vesselTransportMeans vtm " +
						"INNER JOIN vtm.vesselIdentifiers vi " +
						"WHERE vi.vesselIdentifierId = :vesselId " +
						"AND vi.vesselIdentifierSchemeId = :vesselSchemeId " +
						"AND frd.acceptedDatetime > (" +
													"SELECT max(sfrd.acceptedDatetime) " +
													"FROM FishingTripIdentifierEntity sfti " +
													"INNER JOIN sfti.fishingTrip sft " +
													"INNER JOIN sft.fishingActivity sfa " +
													"INNER JOIN sfa.faReportDocument sfrd " +
													"WHERE sfti.tripId = :tripId" +
													")" +
						"ORDER BY frd.acceptedDatetime ASC")
		 */
})

@Entity
@Table(name = "activity_fishing_trip")
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"delimitedPeriods"})
@ToString(exclude = {"delimitedPeriods"})
@Data
public class FishingTripEntity implements Serializable {

    public static final String FIND_CURRENT_TRIP = "findCurrentTrip";
    public static final String FIND_PREVIOUS_TRIP = "findPreviousTrip";
    public static final String FIND_NEXT_TRIP = "findNextTrip";

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

    public void addDelimitedPeriods(DelimitedPeriodEntity periodEntity) {
        delimitedPeriods.add(periodEntity);
        periodEntity.setFishingTrip(this);
    }

    public void removeDelimitedPeriods(DelimitedPeriodEntity area) {
        delimitedPeriods.remove(area);
        area.setFishingTrip(null);
    }

    public static FishingTripEntity create(FishingTrip fishingTrip) {
        FishingTripEntity fishingTripEntity = new FishingTripEntity();

        List<IDType> ids = fishingTrip.getIDS();
        IDType idType = ids.get(0);
        if (idType != null) {
            FishingTripKey fishingTripKey = new FishingTripKey(idType.getValue(), idType.getSchemeID());
            fishingTripEntity.setFishingTripKey(fishingTripKey);
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
