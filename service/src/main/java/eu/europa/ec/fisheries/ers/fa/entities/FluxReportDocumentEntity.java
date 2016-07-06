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
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "activity_flux_report_document")
public class FluxReportDocumentEntity implements Serializable {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "flux_report_document_id")
    private String fluxReportDocumentId;

    @Column(name = "reference_id")
    private String referenceId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_datetime", nullable = false, length = 29)
    private Date creationDatetime;

    @Column(name = "purpose_code", nullable = false)
    private String purposeCode;

    @Column(name = "purpose_code_list_id", nullable = false)
    private String purposeCodeListId;

    @Column(columnDefinition = "text", name = "purpose")
    private String purpose;

    @Column(name = "owner_flux_party_id", nullable = false)
    private String ownerFluxPartyId;

    @Column(name = "owner_flux_party_name")
    private String ownerFluxPartyName;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "fluxReportDocument")
    private FaReportDocumentEntity faReportDocument;

    public FluxReportDocumentEntity() {
    }

    public int getId() {
        return this.id;
    }

    public String getFluxReportDocumentId() {
        return this.fluxReportDocumentId;
    }

    public void setFluxReportDocumentId(String fluxReportDocumentId) {
        this.fluxReportDocumentId = fluxReportDocumentId;
    }

    public String getReferenceId() {
        return this.referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public Date getCreationDatetime() {
        return this.creationDatetime;
    }

    public void setCreationDatetime(Date creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public String getPurposeCode() {
        return this.purposeCode;
    }

    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    public String getPurposeCodeListId() {
        return this.purposeCodeListId;
    }

    public void setPurposeCodeListId(String purposeCodeListId) {
        this.purposeCodeListId = purposeCodeListId;
    }

    public String getPurpose() {
        return this.purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
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

    public FaReportDocumentEntity getFaReportDocument() {
        return faReportDocument;
    }

    public void setFaReportDocument(FaReportDocumentEntity faReportDocument) {
        this.faReportDocument = faReportDocument;
    }
}