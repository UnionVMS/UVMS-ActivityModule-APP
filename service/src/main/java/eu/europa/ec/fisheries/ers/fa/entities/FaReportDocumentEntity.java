package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "activity_fa_report_document", uniqueConstraints = {
		@UniqueConstraint(columnNames = "flux_report_document_id"),
		@UniqueConstraint(columnNames = "vessel_transport_means_id") })
public class FaReportDocumentEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flux_fa_report_message_id", nullable = false)
	private FluxFaReportMessageEntity fluxFaReportMessage;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vessel_transport_means_id", unique = true, nullable = false)
	private VesselTransportMeansEntity vesselTransportMeans;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flux_report_document_id", unique = true, nullable = false)
	private FluxReportDocumentEntity fluxReportDocument;

	@Column(name = "type_code", nullable = false)
	private String typeCode;

	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "accepted_datetime", length = 29)
	private Date acceptedDatetime;

	@Column(name = "fmc_marker")
	private String fmcMarker;

	@Column(name = "fmc_marker_list_id")
	private String fmcMarkerListId;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faReportDocument")
	private Set<FaReportIdentifierEntity> faReportIdentifiers = new HashSet<FaReportIdentifierEntity>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faReportDocument")
	private Set<FishingActivityEntity> fishingActivities = new HashSet<FishingActivityEntity>(0);

	public FaReportDocumentEntity() {
	}

	public FaReportDocumentEntity(FluxFaReportMessageEntity fluxFaReportMessage,
								  VesselTransportMeansEntity vesselTransportMeans,
								  FluxReportDocumentEntity fluxReportDocument,
								  String typeCode, String typeCodeListId) {
		this.fluxFaReportMessage = fluxFaReportMessage;
		this.vesselTransportMeans = vesselTransportMeans;
		this.fluxReportDocument = fluxReportDocument;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
	}

	public FaReportDocumentEntity(FluxFaReportMessageEntity fluxFaReportMessage,
								  VesselTransportMeansEntity vesselTransportMeans,
								  FluxReportDocumentEntity fluxReportDocument,
								  String typeCode, String typeCodeListId,
								  Date acceptedDatetime, String fmcMarker, String fmcMarkerListId,
								  Set<FaReportIdentifierEntity> faReportIdentifiers,
								  Set<FishingActivityEntity> fishingActivities) {
		this.fluxFaReportMessage = fluxFaReportMessage;
		this.vesselTransportMeans = vesselTransportMeans;
		this.fluxReportDocument = fluxReportDocument;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.acceptedDatetime = acceptedDatetime;
		this.fmcMarker = fmcMarker;
		this.fmcMarkerListId = fmcMarkerListId;
		this.faReportIdentifiers = faReportIdentifiers;
		this.fishingActivities = fishingActivities;
	}


	public int getId() {
		return this.id;
	}

	public FluxFaReportMessageEntity getFluxFaReportMessage() {
		return this.fluxFaReportMessage;
	}

	public void setFluxFaReportMessage(
			FluxFaReportMessageEntity fluxFaReportMessage) {
		this.fluxFaReportMessage = fluxFaReportMessage;
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

}
