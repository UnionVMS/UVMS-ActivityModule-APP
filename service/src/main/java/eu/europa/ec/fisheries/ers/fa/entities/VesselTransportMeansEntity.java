/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.fa.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

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
public class VesselTransportMeansEntity implements Serializable {

    public static final String FIND_LATEST_VESSEL_BY_TRIP_ID = "vesselTransportMeansEntity.findLatestVesselByTripId";

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "vsl_trp_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "registration_event_id")
    private RegistrationEventEntity registrationEvent;

    @Column(name = "role_code")
    private String roleCode;

    @Column(name = "role_code_list_id")
    private String roleCodeListId;

    @Column(columnDefinition = "text", name = "name")
    private String name;

    @Column(name = "country_scheme_id")
    private String countrySchemeId;

    @Column(name = "country")
    private String country;

    @Column(name = "guid")
    private String guid;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans")
    private FaReportDocumentEntity faReportDocument;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans")
    private FishingActivityEntity fishingActivity;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans", cascade = CascadeType.ALL)
    private Set<ContactPartyEntity> contactParty;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans", cascade = CascadeType.ALL)
    private Set<VesselIdentifierEntity> vesselIdentifiers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans", cascade = CascadeType.ALL)
    private Set<FlapDocumentEntity> flapDocuments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans", cascade = CascadeType.ALL)
    private Set<VesselPositionEventEntity> vesselPositionEvents;

    public int getId() {
        return this.id;
    }

    public RegistrationEventEntity getRegistrationEvent() {
        return this.registrationEvent;
    }

    public void setRegistrationEvent(
            RegistrationEventEntity registrationEvent) {
        this.registrationEvent = registrationEvent;
    }

    public String getRoleCode() {
        return this.roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleCodeListId() {
        return this.roleCodeListId;
    }

    public void setRoleCodeListId(String roleCodeListId) {
        this.roleCodeListId = roleCodeListId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<VesselIdentifierEntity> getVesselIdentifiers() {
        return this.vesselIdentifiers;
    }

    public void setVesselIdentifiers(
            Set<VesselIdentifierEntity> vesselIdentifiers) {
        this.vesselIdentifiers = vesselIdentifiers;
    }

    public FaReportDocumentEntity getFaReportDocument() {
        return faReportDocument;
    }

    public void setFaReportDocument(FaReportDocumentEntity faReportDocument) {
        this.faReportDocument = faReportDocument;
    }

    public Set<ContactPartyEntity> getContactParty() {
        return contactParty;
    }

    public void setContactParty(Set<ContactPartyEntity> contactParty) {
        this.contactParty = contactParty;
    }

    public String getCountrySchemeId() {
        return countrySchemeId;
    }

    public void setCountrySchemeId(String countrySchemeId) {
        this.countrySchemeId = countrySchemeId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Set<FlapDocumentEntity> getFlapDocuments() {
        return flapDocuments;
    }

    public void setFlapDocuments(Set<FlapDocumentEntity> flapDocuments) {
        this.flapDocuments = flapDocuments;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public FishingActivityEntity getFishingActivity() {
        return fishingActivity;
    }

    public void setFishingActivity(FishingActivityEntity fishingActivity) {
        this.fishingActivity = fishingActivity;
    }

    public Set<VesselPositionEventEntity> getVesselPositionEvents() {
        return vesselPositionEvents;
    }

    public void setVesselPositionEvents(Set<VesselPositionEventEntity> vesselPositionEvents) {
        this.vesselPositionEvents = vesselPositionEvents;
    }

    @Override
    public String toString() {
        return "VesselTransportMeansEntity{" +
                "id=" + id +
                ", registrationEvent=" + registrationEvent +
                ", roleCode='" + roleCode + '\'' +
                ", roleCodeListId='" + roleCodeListId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}