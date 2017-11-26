/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NamedQueries({
        @NamedQuery(name = VesselTransportMeansEntity.FIND_LATEST_VESSEL_BY_TRIP_ID,
                query = "SELECT vt FROM FishingTripIdentifierEntity fti " +
                        "INNER JOIN fti.fishingTrip ft " +
                        "INNER JOIN ft.fishingActivity fa " +
                        "INNER JOIN fa.faReportDocument frd " +
                        "INNER JOIN frd.vesselTransportMeans vt " +
                        "INNER JOIN vt.vesselIdentifiers vi " +
                        "WHERE fti.tripId = :tripId " +
                        "ORDER BY frd.acceptedDatetime DESC")
})
@Entity
@Table(name = "activity_vessel_transport_means")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"contactParty", "vesselIdentifiers", "flapDocuments", "vesselPositionEvents"})
@ToString(exclude = {"contactParty", "vesselIdentifiers", "flapDocuments", "vesselPositionEvents"})
public class VesselTransportMeansEntity implements Serializable {

    public static final String FIND_LATEST_VESSEL_BY_TRIP_ID = "vesselTransportMeansEntity.findLatestVesselByTripId";

    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "vsl_trp_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "registration_event_id")
    private RegistrationEventEntity registrationEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fishing_activity_id")
    private FishingActivityEntity fishingActivity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fa_report_document_id")
    private FaReportDocumentEntity faReportDocument;

    @Column(name = "role_code")
    private String roleCode;

    @Column(name = "role_code_list_id")
    private String roleCodeListId;

    @Column(columnDefinition = "text")
    private String name;

    @Column(name = "country_scheme_id")
    private String countrySchemeId;

    private String country;

    private String guid;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans", cascade = CascadeType.ALL)
    private Set<ContactPartyEntity> contactParty;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans", cascade = CascadeType.ALL)
    private Set<VesselIdentifierEntity> vesselIdentifiers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans", cascade = CascadeType.ALL)
    private Set<FlapDocumentEntity> flapDocuments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans", cascade = CascadeType.ALL)
    private Set<VesselPositionEventEntity> vesselPositionEvents;

    public Map<VesselIdentifierSchemeIdEnum, String> getVesselIdentifiersMap() {
        Map<VesselIdentifierSchemeIdEnum, String> idMap = new HashMap<>();
        for (VesselIdentifierEntity entity :vesselIdentifiers) {
            idMap.put(Enum.valueOf(VesselIdentifierSchemeIdEnum.class, entity.getVesselIdentifierSchemeId()), entity.getVesselIdentifierId());
        }
        return idMap;
    }
}