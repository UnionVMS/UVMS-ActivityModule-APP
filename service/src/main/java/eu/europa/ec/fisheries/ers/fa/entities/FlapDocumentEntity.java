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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "activity_flap_document")
@Data
@NoArgsConstructor
@ToString(of = {"flapDocumentId", "flapDocumentSchemeId", "flapTypeCode", "flapTypeCodeListId"})
@EqualsAndHashCode(of = {"flapDocumentId", "flapDocumentSchemeId", "flapTypeCode", "flapTypeCodeListId"})
public class FlapDocumentEntity implements Serializable {

    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "flap_doc_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

    @Column(name = "flap_document_id")
    private String flapDocumentId;

    @Column(name = "flap_document_scheme_id")
    private String flapDocumentSchemeId;

    @Column(name = "flap_type_code")
    private String flapTypeCode;

    @Column(name = "flap_type_code_list_id")
    private String flapTypeCodeListId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fishing_activity_id")
    private FishingActivityEntity fishingActivity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vessel_transport_means_id")
    private VesselTransportMeansEntity vesselTransportMeans;

    @OneToOne(mappedBy = "flapDocument")
    private FluxCharacteristicEntity fluxCharacteristic;

}
