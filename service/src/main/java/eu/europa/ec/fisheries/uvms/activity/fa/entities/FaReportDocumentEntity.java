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
import org.locationtech.jts.geom.Geometry;

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
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

@NamedQueries({
        @NamedQuery(name = FaReportDocumentEntity.FIND_BY_FA_ID_AND_SCHEME,
                query = "SELECT fareport FROM FaReportDocumentEntity fareport " +
                        "LEFT JOIN FETCH fareport.fluxReportDocument fluxreport " +
                        "LEFT JOIN FETCH fluxreport.fluxReportIdentifiers identifier " +
                        "WHERE identifier.fluxReportIdentifierId IN (lower(:reportId), upper(:reportId), :reportId) " +
                        "AND identifier.fluxReportIdentifierSchemeId = :schemeId"
        ),
        @NamedQuery(name = FaReportDocumentEntity.FIND_BY_REF_FA_ID_AND_SCHEME,
                query = "SELECT fareport FROM FaReportDocumentEntity fareport " +
                        "LEFT JOIN FETCH fareport.fluxReportDocument fluxreport " +
                        "WHERE fluxreport.referenceId IN (lower(:reportRefId), upper(:reportRefId), :reportRefId) " +
                        "AND fluxreport.referenceSchemeId = :schemeRefId"
        ),
        @NamedQuery(name = FaReportDocumentEntity.LOAD_REPORTS,
                query = "SELECT DISTINCT rpt FROM FaReportDocumentEntity rpt " +
                        "LEFT JOIN FETCH rpt.fishingActivities act " +
                        "LEFT JOIN FETCH rpt.vesselTransportMeans vtm " +
                        "LEFT OUTER JOIN FETCH vtm.vesselIdentifiers vtmids " +
                        "LEFT JOIN FETCH rpt.fluxReportDocument flxrep " +
                        "JOIN FETCH act.fishingTrip fshtrp " +
                        "LEFT OUTER JOIN fshtrp.fishingTripIdentifier fshtrpid " +
                        "WHERE rpt.status IN (:statuses) " +
                        "AND ((:tripId IS NULL) OR fshtrpid.tripId = :tripId) " +
                        "AND (" +
                                ":startDate <= flxrep.creationDatetime " +
                                "AND flxrep.creationDatetime <= :endDate)"
        ),
        @NamedQuery(name = FaReportDocumentEntity.FIND_FA_DOCS_BY_TRIP_ID,
                query = "SELECT DISTINCT rpt FROM FaReportDocumentEntity rpt " +
                        "LEFT JOIN FETCH rpt.fishingActivities act " +
                        "LEFT JOIN FETCH rpt.fluxReportDocument flxrep " +
                        "JOIN FETCH act.fishingTrip fshtrp " +
                        "LEFT OUTER JOIN fshtrp.fishingTripIdentifier fshtrpid " +
                        "WHERE fshtrpid.tripId = :tripId " +
                        "AND ((:area IS NULL) OR intersects(act.geom, :area) = true)"
        ),
        @NamedQuery(name = FaReportDocumentEntity.FIND_BY_FA_IDS_LIST,
                query = "SELECT fareport FROM FaReportDocumentEntity fareport " +
                        "WHERE fareport.id IN (:ids)"
        )
})
@Entity
@Table(name = "activity_fa_report_document")
@Data
@ToString(of = "id")
@EqualsAndHashCode(of = {"acceptedDatetime"})
@NoArgsConstructor
public class FaReportDocumentEntity implements Serializable {

    public static final String FIND_BY_FA_ID_AND_SCHEME = "findByFaId";
    public static final String FIND_BY_FA_IDS_LIST = "findByFaIds";
    public static final String FIND_FA_DOCS_BY_TRIP_ID = "findByTripId";
    public static final String FIND_LATEST_FA_DOCS_BY_TRIP_ID = "findLatestByTripId";
    public static final String LOAD_REPORTS = "FaReportDocumentEntity.loadReports";
    public static final String FIND_BY_REF_FA_ID_AND_SCHEME = "findByRefFaId";

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "fa_rep_doc_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "flux_report_document_id", nullable = false)
    private FluxReportDocumentEntity fluxReportDocument;

    @Column(name = "geom", columnDefinition = "Geometry")
    private Geometry geom;

    @Column(name = "type_code")
    private String typeCode;

    @Column(name = "type_code_list_id")
    private String typeCodeListId;

    @Column(name = "accepted_datetime")
    private Instant acceptedDatetime;

    @Column(name = "fmc_marker")
    private String fmcMarker;

    @Column(name = "fmc_marker_list_id")
    private String fmcMarkerListId;

    @Column(name = "status")
    private String status;

    @Column(name = "source")
    private String source;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "flux_fa_report_message_id")
    private FluxFaReportMessageEntity fluxFaReportMessage;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "faReportDocument", cascade = CascadeType.ALL)
    private Set<FaReportIdentifierEntity> faReportIdentifiers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "faReportDocument", cascade = CascadeType.ALL)
    private Set<FishingActivityEntity> fishingActivities;

    /**
     * From XML we will always receive only One VesselTreansportMeans per FaReportDocument.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "faReportDocument", cascade = CascadeType.ALL)
    private Set<VesselTransportMeansEntity> vesselTransportMeans;

    public Optional<Date> getAcceptedDateTimeAsDate() {
        if (acceptedDatetime == null) {
            return Optional.empty();
        }

        return Optional.of(Date.from(acceptedDatetime));
    }
}
