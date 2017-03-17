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
import java.util.Set;

/**
 * Created by padhyad on 9/15/2016.
 */
@Entity
@Table(name = "activity_flux_party")
public class FluxPartyEntity implements Serializable {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name="SEQ_GEN", sequenceName="flux_pty_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
    private int id;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "fluxParty")
    private FluxReportDocumentEntity fluxReportDocument;

    @Column(name = "flux_party_name")
    private String fluxPartyName;

    @Column(name = "name_language_id")
    private String nameLanguageId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fluxParty", cascade = CascadeType.ALL)
    private Set<FluxPartyIdentifierEntity> fluxPartyIdentifiers;

    public int getId() {
        return id;
    }

    public FluxReportDocumentEntity getFluxReportDocument() {
        return fluxReportDocument;
    }

    public void setFluxReportDocument(FluxReportDocumentEntity fluxReportDocument) {
        this.fluxReportDocument = fluxReportDocument;
    }

    public Set<FluxPartyIdentifierEntity> getFluxPartyIdentifiers() {
        return fluxPartyIdentifiers;
    }

    public void setFluxPartyIdentifiers(Set<FluxPartyIdentifierEntity> fluxPartyIdentifiers) {
        this.fluxPartyIdentifiers = fluxPartyIdentifiers;
    }

    public String getFluxPartyName() {
        return fluxPartyName;
    }

    public void setFluxPartyName(String fluxPartyName) {
        this.fluxPartyName = fluxPartyName;
    }

    public String getNameLanguageId() {
        return nameLanguageId;
    }

    public void setNameLanguageId(String nameLanguageId) {
        this.nameLanguageId = nameLanguageId;
    }
}
