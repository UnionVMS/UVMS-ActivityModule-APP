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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activity_flux_party_identifier")
@NoArgsConstructor
@AllArgsConstructor
@Data
@NamedQueries({
        @NamedQuery(name = FluxPartyIdentifierEntity.MESSAGE_OWNER_FROM_TRIP_ID,
                query = "SELECT DISTINCT fPartyIdentifier FROM FluxPartyIdentifierEntity fPartyIdentifier LEFT JOIN fPartyIdentifier.fluxParty flParty " +
                        "LEFT JOIN flParty.fluxReportDocument fluxRepDoc LEFT JOIN fluxRepDoc.fluxFaReportMessage fluxRepMessage " +
                        "LEFT JOIN fluxRepMessage.faReportDocuments faRepDocs LEFT JOIN faRepDocs.fishingActivities fishActivities " +
                        "LEFT JOIN fishActivities.fishingTrips fishTrips JOIN fishTrips.fishingTripIdentifiers fTripIdentifier " +
                        "WHERE fTripIdentifier.tripId =:fishingTripId")
})
@EqualsAndHashCode(of = {"fluxPartyIdentifierId","fluxPartyIdentifierSchemeId"})
public class FluxPartyIdentifierEntity implements Serializable {

    public static final String MESSAGE_OWNER_FROM_TRIP_ID = "findMessageOwnerFromTripId";

    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "pty_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "flux_party_id")
    private FluxPartyEntity fluxParty;

    @Column(name = "flux_party_identifier_id")
    private String fluxPartyIdentifierId;

    @Column(name = "flux_party_identifier_scheme_id")
    private String fluxPartyIdentifierSchemeId;

}
