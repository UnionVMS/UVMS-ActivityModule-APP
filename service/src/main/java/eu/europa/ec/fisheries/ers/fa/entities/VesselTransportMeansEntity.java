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

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans", cascade = CascadeType.ALL)
    private ContactPartyEntity contactParty;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vesselTransportMeans", cascade = CascadeType.ALL)
    private Set<VesselIdentifierEntity> vesselIdentifiers = new HashSet<VesselIdentifierEntity>(0);

    public VesselTransportMeansEntity() {
    }

    public VesselTransportMeansEntity(RegistrationEventEntity registrationEvent,
                                      String roleCode, String roleCodeListId, String meansSchemaId,
                                      String name, String flapDocumentId, String flapDocumentSchemeId,
                                      ContactPartyEntity contactParty,
                                      Set<VesselIdentifierEntity> vesselIdentifiers,
                                      FaReportDocumentEntity faReportDocument) {
        this.registrationEvent = registrationEvent;
        this.roleCode = roleCode;
        this.roleCodeListId = roleCodeListId;
        this.name = name;
        this.flapDocumentId = flapDocumentId;
        this.flapDocumentSchemeId = flapDocumentSchemeId;
        this.contactParty = contactParty;
        this.vesselIdentifiers = vesselIdentifiers;
        this.faReportDocument = faReportDocument;
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

    public ContactPartyEntity getContactParty() {
        return contactParty;
    }

    public void setContactParty(ContactPartyEntity contactParty) {
        this.contactParty = contactParty;
    }
}
