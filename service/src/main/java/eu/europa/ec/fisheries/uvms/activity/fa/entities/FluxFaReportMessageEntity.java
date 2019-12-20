/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.uvms.activity.fa.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "activity_flux_fa_report_message")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"fluxReportDocument_Id", "fluxReportDocument_IdSchemeId"})
@ToString(exclude = "faReportDocuments")
public class FluxFaReportMessageEntity implements Serializable {

    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "rep_msg_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

    @Column(name = "flux_report_document_id")
    private String fluxReportDocument_Id;

    @Column(name = "flux_report_document_id_scheme_id")
    private String fluxReportDocument_IdSchemeId;

    @Column(name = "flux_report_document_reference_id")
    private String fluxReportDocument_ReferenceId;

    @Column(name = "flux_report_document_reference_scheme_id")
    private String fluxReportDocument_ReferenceIdSchemeId;

    @Column(name = "flux_report_document_creation_datetime", nullable = false)
    private Instant fluxReportDocument_CreationDatetime;

    @Column(name = "flux_report_document_purpose_code", nullable = false)
    private String fluxReportDocument_PurposeCode;

    @Column(name = "flux_report_document_purpose_code_list_id", nullable = false)
    private String fluxReportDocument_PurposeCodeListId;

    @Column(name = "flux_report_document_purpose")
    private String fluxReportDocument_Purpose;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "flux_report_document_flux_party_id")
    private FluxPartyEntity fluxReportDocument_FluxParty;

    @OneToMany(mappedBy = "fluxFaReportMessage", cascade = CascadeType.ALL)
    private Set<FaReportDocumentEntity> faReportDocuments;

}
