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

import javax.persistence.*;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activity_flux_report_identifier")
@NamedQueries({
        @NamedQuery(name = FluxReportIdentifierEntity.FIND_MATCHING_IDENTIFIER,
                query = "SELECT fRepIdent FROM FluxReportIdentifierEntity fRepIdent " +
                        //"INNER JOIN fRepIdent.fluxReportDocument flRepDoc " +
                        //"INNER JOIN flRepDoc.fluxFaReportMessage faRepMsg " +
                        "WHERE fRepIdent.fluxReportIdentifierId = :id " +
                        "AND fRepIdent.fluxReportIdentifierSchemeId = :schemeId"),

        @NamedQuery(name = FluxReportIdentifierEntity.FIND_RELATED_MATCHING_IDENTIFIER,
                query = "SELECT fRepIdent FROM FluxReportIdentifierEntity fRepIdent " +
                        //"INNER JOIN fRepIdent.fluxReportDocument flRepDoc " +
                        //"INNER JOIN flRepDoc.faReportDocument faRepDoc " +
                        "WHERE fRepIdent.fluxReportIdentifierId = :id " +
                        "AND fRepIdent.fluxReportIdentifierSchemeId = :schemeId")

})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FluxReportIdentifierEntity implements Serializable {

    public static final String FIND_MATCHING_IDENTIFIER = "findMatchingIdentifier";
    public static final String FIND_RELATED_MATCHING_IDENTIFIER = "findRelatedMatchingIdentifier";

    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "flux_rep_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

    @ManyToOne
    @JoinColumn(name = "flux_report_document_id")
    private FluxReportDocumentEntity fluxReportDocument;

    @Column(name = "flux_report_identifier_id")
    private String fluxReportIdentifierId;

    @Column(name = "flux_report_identifier_scheme_id")
    private String fluxReportIdentifierSchemeId;

}
