/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "activity_vessel_transport_means")
public class VesselTransportMeansEntity implements Serializable {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "flap_document_id")
    private String flapDocumentId;

    @Column(name = "flap_document_scheme_id")
    private String flapDocumentSchemeId;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans")
    private FaReportDocumentEntity faReportDocument;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans", cascade = CascadeType.ALL)
    private Set<ContactPartyEntity> contactParty;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans", cascade = CascadeType.ALL)
    private Set<VesselIdentifierEntity> vesselIdentifiers = new HashSet<VesselIdentifierEntity>(0);

    public VesselTransportMeansEntity() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getFlapDocumentId() {
        return this.flapDocumentId;
    }

    public void setFlapDocumentId(String flapDocumentId) {
        this.flapDocumentId = flapDocumentId;
    }

    public String getFlapDocumentSchemeId() {
        return this.flapDocumentSchemeId;
    }

    public void setFlapDocumentSchemeId(String flapDocumentSchemeId) {
        this.flapDocumentSchemeId = flapDocumentSchemeId;
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
}