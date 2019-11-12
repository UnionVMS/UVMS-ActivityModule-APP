/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.activity.fa.entities;

import eu.europa.ec.fisheries.ers.activity.service.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
    @AttributeOverrides({
            @AttributeOverride( name = "value", column = @Column(name = "codetype_value")),
            @AttributeOverride( name = "listID", column = @Column(name = "codetype_listID")),
            @AttributeOverride( name = "listAgencyID", column = @Column(name = "codetype_listAgencyID")),
            @AttributeOverride( name = "listAgencyName", column = @Column(name = "codetype_listAgencyName")),
            @AttributeOverride( name = "listName", column = @Column(name = "codetype_listName")),
            @AttributeOverride( name = "listVersionID", column = @Column(name = "codetype_listVersionID")),
            @AttributeOverride( name = "name", column = @Column(name = "codetype_name")),
            @AttributeOverride( name = "languageID", column = @Column(name = "codetype_languageID")),
            @AttributeOverride( name = "listURI", column = @Column(name = "codetype_listURI")),
            @AttributeOverride( name = "listSchemeURI", column = @Column(name = "codetype_listSchemeURI"))
    })
    private CodeType typeCode;
    
    @Column(name = "reference_scheme_id")
    private String referenceSchemeId;

    @Column(name = "creation_datetime", nullable = false)
    private Instant creationDatetime;

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
        for (FluxReportIdentifierEntity fluxReportIdentifierEntity : Utils.safeIterable(fluxReportIdentifiers)) {
            if (fluxReportIdentifierEntity.getFluxReportIdentifierSchemeId().equalsIgnoreCase(schemeId)) {
                return fluxReportIdentifierEntity.getFluxReportIdentifierId();
            }
        }
        return null;
    }

    public String getReportOwner() {
        if (fluxParty != null){
            Set<FluxPartyIdentifierEntity> fluxPartyIdentifiers = fluxParty.getFluxPartyIdentifiers();
            for (FluxPartyIdentifierEntity fluxReportIdentifierEntity : Utils.safeIterable(fluxPartyIdentifiers)) {
                if (fluxReportIdentifierEntity.getFluxPartyIdentifierSchemeId().equalsIgnoreCase("FLUX_GP_PARTY")) {
                    return fluxReportIdentifierEntity.getFluxPartyIdentifierId();
                }
            }
        }
        return null;
    }

}
