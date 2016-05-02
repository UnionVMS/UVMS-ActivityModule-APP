package eu.europa.ec.fisheries.ers.fa.entities;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "activity_flux_fa_report_message")
public class FluxFaReportMessage {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flux_report_document_id")
	private FluxReportDocument fluxReportDocument;
	
	@Column(name = "owner_flux_party_id", nullable = false)
	private String ownerFluxPartyId;
	
	@Column(columnDefinition = "text", name = "owner_flux_party_name")
	private String ownerFluxPartyName;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fluxFaReportMessage")
	private Set<FaReportDocument> faReportDocuments = new HashSet<FaReportDocument>(
			0);

	public FluxFaReportMessage() {
	}

	public FluxFaReportMessage(int id, String ownerFluxPartyId) {
		this.id = id;
		this.ownerFluxPartyId = ownerFluxPartyId;
	}

	public FluxFaReportMessage(int id, FluxReportDocument fluxReportDocument,
			String ownerFluxPartyId, String ownerFluxPartyName,
			Set<FaReportDocument> faReportDocuments) {
		this.id = id;
		this.fluxReportDocument = fluxReportDocument;
		this.ownerFluxPartyId = ownerFluxPartyId;
		this.ownerFluxPartyName = ownerFluxPartyName;
		this.faReportDocuments = faReportDocuments;
	}


	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public FluxReportDocument getFluxReportDocument() {
		return this.fluxReportDocument;
	}

	public void setFluxReportDocument(FluxReportDocument fluxReportDocument) {
		this.fluxReportDocument = fluxReportDocument;
	}

	
	public String getOwnerFluxPartyId() {
		return this.ownerFluxPartyId;
	}

	public void setOwnerFluxPartyId(String ownerFluxPartyId) {
		this.ownerFluxPartyId = ownerFluxPartyId;
	}

	
	public String getOwnerFluxPartyName() {
		return this.ownerFluxPartyName;
	}

	public void setOwnerFluxPartyName(String ownerFluxPartyName) {
		this.ownerFluxPartyName = ownerFluxPartyName;
	}

	
	public Set<FaReportDocument> getFaReportDocuments() {
		return this.faReportDocuments;
	}

	public void setFaReportDocuments(Set<FaReportDocument> faReportDocuments) {
		this.faReportDocuments = faReportDocuments;
	}

}
