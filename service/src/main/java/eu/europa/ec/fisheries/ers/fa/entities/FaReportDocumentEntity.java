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
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@NamedQueries({
        @NamedQuery(name = FaReportDocumentEntity.FIND_BY_FA_ID_AND_SCHEME,
                query = "SELECT fareport FROM FaReportDocumentEntity fareport " +
                        "INNER JOIN fareport.fluxReportDocument fluxreport " +
                        "INNER JOIN fluxreport.fluxReportIdentifiers identifier " +
                        "WHERE identifier.fluxReportIdentifierId = :reportId " +
                        "AND identifier.fluxReportIdentifierSchemeId = :schemeId"),

        @NamedQuery(name = FaReportDocumentEntity.FIND_FA_DOCS_BY_TRIP_ID,
                query = "SELECT DISTINCT fareport FROM FaReportDocumentEntity fareport " +
                        "JOIN FETCH fareport.fishingActivities factivity " +
                        "JOIN FETCH factivity.fishingTrips fishingtrip " +
                        "JOIN FETCH fishingtrip.fishingTripIdentifiers ftidentifier " +
                        "JOIN FETCH fareport.fluxReportDocument fluxreport " +
                        "WHERE ftidentifier.tripId  = :tripId"),

        @NamedQuery(name = FaReportDocumentEntity.FIND_LATEST_FA_DOCS_BY_TRIP_ID,
                query = "SELECT DISTINCT fareport FROM FaReportDocumentEntity fareport " +
                        "JOIN FETCH fareport.fishingActivities factivity " +
                        "JOIN FETCH factivity.fishingTrips fishingtrip " +
                        "JOIN FETCH fishingtrip.fishingTripIdentifiers ftidentifier " +
                        "JOIN FETCH fareport.fluxReportDocument fluxreport " +
                        "WHERE ftidentifier.tripId  = :tripId and fareport.status = 'new'")
})
@Entity
@Table(name = "activity_fa_report_document")
public class FaReportDocumentEntity implements Serializable {

    public static final String FIND_BY_FA_ID_AND_SCHEME = "findByFaId";
    public static final String FIND_FA_DOCS_BY_TRIP_ID = "findByTripId";
    public static final String FIND_LATEST_FA_DOCS_BY_TRIP_ID = "findLatestByTripId";

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name="SEQ_GEN", sequenceName="fa_rep_doc_seq", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
    private int id;

    @Type(type = "org.hibernate.spatial.GeometryType")
    @Column(name = "geom")
    private Geometry geom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flux_fa_report_message_id")
    private FluxFaReportMessageEntity fluxFaReportMessage;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "vessel_transport_means_id")
    private VesselTransportMeansEntity vesselTransportMeans;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "flux_report_document_id", nullable = false)
    private FluxReportDocumentEntity fluxReportDocument;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "faReportDocument", cascade = CascadeType.ALL)
    private Set<FaReportIdentifierEntity> faReportIdentifiers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "faReportDocument", cascade = CascadeType.ALL)
    private Set<FishingActivityEntity> fishingActivities;

    public FaReportDocumentEntity() {
        super();
    }

    public int getId() {
        return this.id;
    }

    public VesselTransportMeansEntity getVesselTransportMeans() {
        return this.vesselTransportMeans;
    }

    public void setVesselTransportMeans(
            VesselTransportMeansEntity vesselTransportMeans) {
        this.vesselTransportMeans = vesselTransportMeans;
    }

    public FluxReportDocumentEntity getFluxReportDocument() {
        return this.fluxReportDocument;
    }

    public void setFluxReportDocument(
            FluxReportDocumentEntity fluxReportDocument) {
        this.fluxReportDocument = fluxReportDocument;
    }

    public String getTypeCode() {
        return this.typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeCodeListId() {
        return this.typeCodeListId;
    }

    public void setTypeCodeListId(String typeCodeListId) {
        this.typeCodeListId = typeCodeListId;
    }

    public Date getAcceptedDatetime() {
        return this.acceptedDatetime;
    }

    public void setAcceptedDatetime(Date acceptedDatetime) {
        this.acceptedDatetime = acceptedDatetime;
    }

    public String getFmcMarker() {
        return this.fmcMarker;
    }

    public void setFmcMarker(String fmcMarker) {
        this.fmcMarker = fmcMarker;
    }

    public String getFmcMarkerListId() {
        return this.fmcMarkerListId;
    }

    public void setFmcMarkerListId(String fmcMarkerListId) {
        this.fmcMarkerListId = fmcMarkerListId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Set<FaReportIdentifierEntity> getFaReportIdentifiers() {
        return this.faReportIdentifiers;
    }

    public void setFaReportIdentifiers(
            Set<FaReportIdentifierEntity> faReportIdentifiers) {
        this.faReportIdentifiers = faReportIdentifiers;
    }

    public Set<FishingActivityEntity> getFishingActivities() {
        return this.fishingActivities;
    }

    public void setFishingActivities(
            Set<FishingActivityEntity> fishingActivities) {
        this.fishingActivities = fishingActivities;
    }

    public FluxFaReportMessageEntity getFluxFaReportMessage() {
        return fluxFaReportMessage;
    }

    public void setFluxFaReportMessage(FluxFaReportMessageEntity fluxFaReportMessage) {
        this.fluxFaReportMessage = fluxFaReportMessage;
    }

    public Geometry getGeom() {
        return geom;
    }

    public void setGeom(Geometry geom) {
        this.geom = geom;
    }

    @Override
    public String toString() {
        return "FaReportDocumentEntity{" +
                "id=" + id +
                ", vesselTransportMeans=" + vesselTransportMeans +
                ", fluxReportDocument=" + fluxReportDocument +
                ", typeCode='" + typeCode + '\'' +
                ", typeCodeListId='" + typeCodeListId + '\'' +
                ", acceptedDatetime=" + acceptedDatetime +
                ", fmcMarker='" + fmcMarker + '\'' +
                ", fmcMarkerListId='" + fmcMarkerListId + '\'' +
                '}';
    }
}