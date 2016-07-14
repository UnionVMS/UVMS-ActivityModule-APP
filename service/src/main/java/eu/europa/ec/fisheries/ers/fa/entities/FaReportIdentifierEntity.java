/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
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