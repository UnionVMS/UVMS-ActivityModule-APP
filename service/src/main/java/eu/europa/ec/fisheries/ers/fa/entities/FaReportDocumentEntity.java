/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.fa.entities;

import com.vividsolutions.jts.geom.Geometry;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@NamedQueries({
        @NamedQuery(name = FaReportDocumentEntity.FIND_BY_FA_ID_AND_SCHEME,
                query = "SELECT fareport FROM FaReportDocumentEntity fareport " +
                        "LEFT JOIN FETCH fareport.fluxReportDocument fluxreport " +
                        "LEFT JOIN FETCH fluxreport.fluxReportIdentifiers identifier " +
                        "WHERE identifier.fluxReportIdentifierId IN (lower(:reportId), upper(:reportId), :reportId) " +
                        "AND identifier.fluxReportIdentifierSchemeId = :schemeId"
        ),
        @NamedQuery(name = FaReportDocumentEntity.FIND_BY_FLUX_REPORT_IDENTIFIER_REF_FA_ID_AND_SCHEME,
                query = "SELECT fareport FROM FaReportDocumentEntity fareport " +
                        "LEFT JOIN FETCH fareport.fluxReportDocument fluxreport " +
                        "LEFT JOIN FETCH fluxreport.fluxReportIdentifiers identifier " +
                        "WHERE identifier.fluxReportIdentifierId IN (lower(:reportRefId), upper(:reportRefId), :reportRefId) " +
                        "AND identifier.fluxReportIdentifierSchemeId = :schemeRefId"
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
                        "JOIN FETCH act.fishingTrips fshtrp " +
                        "LEFT OUTER JOIN fshtrp.fishingTripIdentifiers fshtrpids " +
                        "WHERE rpt.status IN (:statuses) " +
                        "AND ((:tripId IS NULL) OR fshtrpids.tripId = :tripId) " +
                        "AND ((:vesselId IS NULL OR :schemeId IS NULL) OR (vtmids.vesselIdentifierId = :vesselId AND vtmids.vesselIdentifierSchemeId = :schemeId AND (:startDate <= flxrep.creationDatetime AND flxrep.creationDatetime <= :endDate)))"
        ),
        @NamedQuery(name = FaReportDocumentEntity.FIND_FA_DOCS_BY_TRIP_ID,
                query = "SELECT DISTINCT rpt FROM FaReportDocumentEntity rpt " +
                        "LEFT JOIN FETCH rpt.fishingActivities act " +
                        "LEFT JOIN FETCH rpt.fluxReportDocument flxrep " +
                        "JOIN FETCH act.fishingTrips fshtrp " +
                        "LEFT OUTER JOIN fshtrp.fishingTripIdentifiers fshtrpids " +
                        "WHERE fshtrpids.tripId = :tripId " +
                        "AND ((:area IS NULL) OR intersects(act.geom, :area) = true)"
        ),
        @NamedQuery(name = FaReportDocumentEntity.FIND_BY_FA_IDS_LIST,
                query = "SELECT fareport FROM FaReportDocumentEntity fareport " +
                        "WHERE fareport.id IN (:ids)"
        ),
        @NamedQuery(name = FaReportDocumentEntity.FIND_BY_ASSET_GUID_AND_DATE_PERIOD,
                query = "SELECT DISTINCT faReport FROM FaReportDocumentEntity faReport " +
                        "LEFT JOIN FETCH faReport.vesselTransportMeans vesselTransportMeans " +
                        "WHERE vesselTransportMeans.guid = :assetGuid " +
                        "AND EXISTS( " +
                        "SELECT fishingActivity FROM FishingActivityEntity fishingActivity " +
                        "WHERE fishingActivity.occurence BETWEEN :startDate and :endDate " +
                        "AND fishingActivity.faReportDocument.id = faReport.id  " +
                        ")"

        ),
        @NamedQuery(name = FaReportDocumentEntity.FIND_FA_REPORT_DOCUMENTS_BY_IDENTIFIER_IDS,
                query = "SELECT faReport " +
                        "FROM FaReportDocumentEntity faReport " +
                        "LEFT JOIN faReport.fluxReportDocument reportDocument " +
                        "LEFT JOIN reportDocument.fluxReportIdentifiers identifiers " +
                        "WHERE concat(identifiers.fluxReportIdentifierSchemeId,':',identifiers.fluxReportIdentifierId) IN :reportIds " +
                        "ORDER BY faReport.id DESC "
        )
})
@Entity
@Table(name = "activity_fa_report_document")
@Data
@ToString(of = "id")
@NoArgsConstructor
public class FaReportDocumentEntity implements Serializable {

    public static final String FIND_BY_FA_ID_AND_SCHEME = "findByFaId";
    public static final String FIND_BY_FA_IDS_LIST = "findByFaIds";
    public static final String FIND_FA_DOCS_BY_TRIP_ID = "findByTripId";
    public static final String FIND_LATEST_FA_DOCS_BY_TRIP_ID = "findLatestByTripId";
    public static final String LOAD_REPORTS = "FaReportDocumentEntity.loadReports";
    public static final String FIND_BY_REF_FA_ID_AND_SCHEME = "findByRefFaId";
    public static final String FIND_BY_FLUX_REPORT_IDENTIFIER_REF_FA_ID_AND_SCHEME = "findByFluxReportIdentifierRefFaId";
    public static final String FIND_BY_ASSET_GUID_AND_DATE_PERIOD = "findByAssetGuidAndDatePeriod";
    public static final String FIND_FA_REPORT_DOCUMENTS_BY_IDENTIFIER_IDS = "findFaReportDocumentsByIdentifierIds";

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN_activity_fa_report_document", sequenceName = "fa_rep_doc_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN_activity_fa_report_document")
    private int id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "flux_report_document_id", nullable = false)
    private FluxReportDocumentEntity fluxReportDocument;

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

    @Override
    public boolean equals(Object other) {
        return other instanceof FaReportDocumentEntity
                && (
                (this.fluxReportDocument == null && ((FaReportDocumentEntity) other).fluxReportDocument == null)
                        || (this.fluxReportDocument != null && ((FaReportDocumentEntity) other).fluxReportDocument != null && Objects.equals(this.fluxReportDocument.getFluxReportIdentifiers(), ((FaReportDocumentEntity) other).fluxReportDocument.getFluxReportIdentifiers()))
        );
    }

    @Override
    public int hashCode() {
        return fluxReportDocument == null || fluxReportDocument.getFluxReportIdentifiers() == null ? 0 : fluxReportDocument.getFluxReportIdentifiers().stream().mapToInt(Object::hashCode).sum();
    }
}
