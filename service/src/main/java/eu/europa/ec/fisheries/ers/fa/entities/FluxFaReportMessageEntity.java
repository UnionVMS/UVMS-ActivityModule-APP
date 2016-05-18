package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "activity_flux_fa_report_message")
public class FluxFaReportMessageEntity implements Serializable {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flux_report_document_id")
    private FluxReportDocumentEntity fluxReportDocument;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fluxFaReportMessage")
    private Set<FaReportDocumentEntity> faReportDocuments = new HashSet<FaReportDocumentEntity>(0);

    public FluxFaReportMessageEntity() {
    }

    public FluxFaReportMessageEntity(FluxReportDocumentEntity fluxReportDocument, Set<FaReportDocumentEntity> faReportDocuments) {
        this.fluxReportDocument = fluxReportDocument;
        this.faReportDocuments = faReportDocuments;
    }

    public int getId() {
        return this.id;
    }

    public FluxReportDocumentEntity getFluxReportDocument() {
        return this.fluxReportDocument;
    }

    public void setFluxReportDocument(FluxReportDocumentEntity fluxReportDocument) {
        this.fluxReportDocument = fluxReportDocument;
    }

    public Set<FaReportDocumentEntity> getFaReportDocuments() {
        return this.faReportDocuments;
    }

    public void setFaReportDocuments(Set<FaReportDocumentEntity> faReportDocuments) {
        this.faReportDocuments = faReportDocuments;
    }
}
