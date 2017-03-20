/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by padhyad on 9/15/2016.
 */
@Entity
@Table(name = "activity_flux_report_identifier")
public class FluxReportIdentifierEntity implements Serializable {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "flux_rep_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flux_report_document_id")
    private FluxReportDocumentEntity fluxReportDocument;

    @Column(name = "flux_report_identifier_id")
    private String fluxReportIdentifierId;

    @Column(name = "flux_report_identifier_scheme_id")
    private String fluxReportIdentifierSchemeId;

    public int getId() {
        return id;
    }

    public FluxReportDocumentEntity getFluxReportDocument() {
        return fluxReportDocument;
    }

    public void setFluxReportDocument(FluxReportDocumentEntity fluxReportDocument) {
        this.fluxReportDocument = fluxReportDocument;
    }

    public String getFluxReportIdentifierId() {
        return fluxReportIdentifierId;
    }

    public void setFluxReportIdentifierId(String fluxReportIdentifierId) {
        this.fluxReportIdentifierId = fluxReportIdentifierId;
    }

    public String getFluxReportIdentifierSchemeId() {
        return fluxReportIdentifierSchemeId;
    }

    public void setFluxReportIdentifierSchemeId(String fluxReportIdentifierSchemeId) {
        this.fluxReportIdentifierSchemeId = fluxReportIdentifierSchemeId;
    }
}
