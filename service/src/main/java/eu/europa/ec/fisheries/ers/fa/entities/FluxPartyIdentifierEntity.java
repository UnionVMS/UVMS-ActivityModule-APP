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

/**
 * Created by padhyad on 9/15/2016.
 */
@Entity
@Table(name = "activity_flux_party_identifier")
public class FluxPartyIdentifierEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flux_party_id")
    private FluxPartyEntity fluxParty;

    @Column(name = "flux_party_identifier_id")
    private String fluxPartyIdentifierId;

    @Column(name = "flux_party_identifier_scheme_id")
    private String fluxPartyIdentifierSchemeId;

    public void setId(int id) {
        this.id = id;
    }

    public FluxPartyEntity getFluxParty() {
        return fluxParty;
    }

    public void setFluxParty(FluxPartyEntity fluxParty) {
        this.fluxParty = fluxParty;
    }

    public String getFluxPartyIdentifierId() {
        return fluxPartyIdentifierId;
    }

    public void setFluxPartyIdentifierId(String fluxPartyIdentifierId) {
        this.fluxPartyIdentifierId = fluxPartyIdentifierId;
    }

    public String getFluxPartyIdentifierSchemeId() {
        return fluxPartyIdentifierSchemeId;
    }

    public void setFluxPartyIdentifierSchemeId(String fluxPartyIdentifierSchemeId) {
        this.fluxPartyIdentifierSchemeId = fluxPartyIdentifierSchemeId;
    }
}
