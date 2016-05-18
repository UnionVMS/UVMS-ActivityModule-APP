package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "activity_fa_report_identifier")
public class FaReportIdentifierEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fa_report_document_id")
	private FaReportDocumentEntity faReportDocument;

	@Column(name = "fa_report_identifier_id")
	private String faReportIdentifierId;

	@Column(name = "fa_report_identifier_scheme_id")
	private String faReportIdentifierSchemeId;

	public FaReportIdentifierEntity() {
	}

	public FaReportIdentifierEntity(FaReportDocumentEntity faReportDocument,
									String faReportIdentifierId, String faReportIdentifierSchemeId) {
		this.faReportDocument = faReportDocument;
		this.faReportIdentifierId = faReportIdentifierId;
		this.faReportIdentifierSchemeId = faReportIdentifierSchemeId;
	}

	public int getId() {
		return this.id;
	}

	public FaReportDocumentEntity getFaReportDocument() {
		return this.faReportDocument;
	}

	public void setFaReportDocument(
			FaReportDocumentEntity faReportDocument) {
		this.faReportDocument = faReportDocument;
	}

	public String getFaReportIdentifierId() {
		return this.faReportIdentifierId;
	}

	public void setFaReportIdentifierId(String faReportIdentifierId) {
		this.faReportIdentifierId = faReportIdentifierId;
	}

	public String getFaReportIdentifierSchemeId() {
		return this.faReportIdentifierSchemeId;
	}

	public void setFaReportIdentifierSchemeId(String faReportIdentifierSchemeId) {
		this.faReportIdentifierSchemeId = faReportIdentifierSchemeId;
	}

}
