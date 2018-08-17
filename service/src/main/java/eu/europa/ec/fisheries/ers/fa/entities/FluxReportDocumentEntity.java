/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.fa.entities;

import lombok.*;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "activity_flux_report_document")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(of = "id")
@EqualsAndHashCode(of = {"fluxReportIdentifiers"})
public class FluxReportDocumentEntity implements Serializable {

    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "flux_rep_doc_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

    @Column(name = "reference_id")
    private String referenceId;

    @Embedded
    private CodeType typeCode;

    @Column(name = "reference_scheme_id")
    private String referenceSchemeId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_datetime", nullable = false, length = 29)
    private Date creationDatetime;

    @Column(name = "purpose_code", nullable = false)
    private String purposeCode;

    @Column(name = "purpose_code_list_id", nullable = false)
    private String purposeCodeListId;

    @Column(columnDefinition = "text", name = "purpose")
    private String purpose;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "flux_fa_report_message_id")
    private FluxFaReportMessageEntity fluxFaReportMessage;

    @OneToOne(mappedBy = "fluxReportDocument")
    private FaReportDocumentEntity faReportDocument;

    @OneToOne(cascade = CascadeType.ALL, optional=false)
    @JoinColumn(name = "flux_party_id")
    private FluxPartyEntity fluxParty;

    @OneToMany(mappedBy = "fluxReportDocument", cascade = CascadeType.ALL)
    private Set<FluxReportIdentifierEntity> fluxReportIdentifiers;

    public String getFluxPartyIdentifierBySchemeId(String schemeId) {
        if (CollectionUtils.isNotEmpty(fluxReportIdentifiers)) {
            for (FluxReportIdentifierEntity fluxReportIdentifierEntity : fluxReportIdentifiers) {
                if (fluxReportIdentifierEntity.getFluxReportIdentifierSchemeId().equalsIgnoreCase(schemeId)) {
                    return fluxReportIdentifierEntity.getFluxReportIdentifierId();
                }
            }
        }
        return null;
    }
}