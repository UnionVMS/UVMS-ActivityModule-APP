/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Type;

@NamedQueries({
        @NamedQuery(name = FaReportDocumentEntity.FIND_BY_FA_ID_AND_SCHEME,
                query = "SELECT fareport FROM FaReportDocumentEntity fareport " +
                        "INNER JOIN fareport.fluxReportDocument fluxreport " +
                        "INNER JOIN fluxreport.fluxReportIdentifiers identifier " +
                        "WHERE identifier.fluxReportIdentifierId = :reportId " +
                        "AND identifier.fluxReportIdentifierSchemeId = :schemeId"),

        @NamedQuery(name = FaReportDocumentEntity.FA_QUERY,
                query = "SELECT rpt FROM FaReportDocumentEntity rpt " +
                        "LEFT JOIN FETCH rpt.fishingActivities act " +
                        "LEFT JOIN FETCH rpt.fluxReportDocument flxrep " +
                        "JOIN FETCH act.fishingTrips fshtrp " +
                        "JOIN FETCH fshtrp.fishingTripIdentifiers fshtrpids " +
                        "WHERE fshtrpids.tripId = :tripId AND " +
                        "((flxrep.purposeCode = '9' AND :consolidated = 'N') OR (:consolidated = 'Y' OR :consolidated is NULL))"
        ),
        @NamedQuery(name = FaReportDocumentEntity.FIND_LATEST_FA_DOCS_BY_TRIP_ID,
                query = "SELECT DISTINCT fareport FROM FaReportDocumentEntity fareport " +
                        "JOIN FETCH fareport.fishingActivities factivity " +
                        "JOIN FETCH factivity.fishingTrips fishingtrip " +
                        "JOIN FETCH fishingtrip.fishingTripIdentifiers ftidentifier " +
                        "JOIN FETCH fareport.fluxReportDocument fluxreport " +
                        "WHERE ftidentifier.tripId  = :tripId and fareport.status = 'new'"),

        @NamedQuery(name = FaReportDocumentEntity.FIND_FA_DOCS_BY_TRIP_ID,
                query = "SELECT fareport FROM FaReportDocumentEntity fareport " +
                        "JOIN FETCH fareport.fishingActivities factivity " +
                        "JOIN FETCH factivity.fishingTrips fishingtrip " +
                        "JOIN FETCH fishingtrip.fishingTripIdentifiers ftidentifier " +
                        "JOIN FETCH fareport.fluxReportDocument fluxreport " +
                        "WHERE ftidentifier.tripId  = :tripId"),
})
@Entity
@Table(name = "activity_fa_report_document")
@Data
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
public class FaReportDocumentEntity implements Serializable {

    public static final String FIND_BY_FA_ID_AND_SCHEME = "findByFaId";
    public static final String FIND_FA_DOCS_BY_TRIP_ID = "findByTripId";
    public static final String FIND_LATEST_FA_DOCS_BY_TRIP_ID = "findLatestByTripId";
    public static final String FA_QUERY = "findForFaQuery";

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "fa_rep_doc_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

    @Type(type = "org.hibernate.spatial.GeometryType")
    @Column(name = "geom")
    private Geometry geom;

    @Column(name = "type_code")
    private String typeCode;

    @Column(name = "type_code_list_id")
    private String typeCodeListId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "accepted_datetime", length = 29)
    private Date acceptedDatetime;

    @Column(name = "fmc_marker")
    private String fmcMarker;

    @Column(name = "fmc_marker_list_id")
    private String fmcMarkerListId;

    @Column(name = "status")
    private String status;

    @Column(name = "source")
    private String source;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name = "flux_fa_report_message_id")
    private FluxFaReportMessageEntity fluxFaReportMessage;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "flux_report_document_id", nullable = false)
    private FluxReportDocumentEntity fluxReportDocument;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "faReportDocument", cascade = CascadeType.ALL)
    private Set<FaReportIdentifierEntity> faReportIdentifiers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "faReportDocument", cascade = CascadeType.ALL)
    private Set<FishingActivityEntity> fishingActivities;

    /**
     * FIXME This one to many relationship is artificially created. From XML we will always receive only One VesselTreansportMeans per FaReportDocument.
     *  FIXME This is done to avoid cyclic dependency ( Vessel->FishingActivity->FaReportDocument->Vessel )
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "faReportDocument", cascade = CascadeType.ALL)
    private Set<VesselTransportMeansEntity> vesselTransportMeans;

    public FaReportDocumentEntity() {
        super();
    }
}