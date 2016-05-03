package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "activity_fa_report_document", uniqueConstraints = {
		@UniqueConstraint(columnNames = "vessel_transport_means_id"),
		@UniqueConstraint(columnNames = "flux_report_document_id") })
public class FaReportDocument implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flux_fa_report_message_id", nullable = false)
	private FluxFaReportMessage fluxFaReportMessage;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vessel_transport_means_id", nullable = false)
	private VesselTransportMeans vesselTransportMeans;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flux_report_document_id", nullable = false)
	private FluxReportDocument fluxReportDocument;
	
	@Column(name = "type_code", nullable = false)
	private String typeCode;
	
	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;
	
	@Column(columnDefinition = "text", name = "related_report")
	private String relatedReport;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "accepted_datetime", length = 29)
	private Date acceptedDatetime;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "faReportDocument")
	private Set<FishingActivity> fishingActivities = new HashSet<FishingActivity>(
			0);

	public FaReportDocument() {
	}

	public FaReportDocument(int id, FluxFaReportMessage fluxFaReportMessage,
			VesselTransportMeans vesselTransportMeans,
			FluxReportDocument fluxReportDocument, String typeCode,
			String typeCodeListId) {
		this.id = id;
		this.fluxFaReportMessage = fluxFaReportMessage;
		this.vesselTransportMeans = vesselTransportMeans;
		this.fluxReportDocument = fluxReportDocument;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
	}

	public FaReportDocument(int id, FluxFaReportMessage fluxFaReportMessage,
			VesselTransportMeans vesselTransportMeans,
			FluxReportDocument fluxReportDocument, String typeCode,
			String typeCodeListId, String relatedReport, Date acceptedDatetime,
			Set<FishingActivity> fishingActivities) {
		this.id = id;
		this.fluxFaReportMessage = fluxFaReportMessage;
		this.vesselTransportMeans = vesselTransportMeans;
		this.fluxReportDocument = fluxReportDocument;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.relatedReport = relatedReport;
		this.acceptedDatetime = acceptedDatetime;
		this.fishingActivities = fishingActivities;
	}

	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public FluxFaReportMessage getFluxFaReportMessage() {
		return this.fluxFaReportMessage;
	}

	public void setFluxFaReportMessage(FluxFaReportMessage fluxFaReportMessage) {
		this.fluxFaReportMessage = fluxFaReportMessage;
	}


	public VesselTransportMeans getVesselTransportMeans() {
		return this.vesselTransportMeans;
	}

	public void setVesselTransportMeans(
			VesselTransportMeans vesselTransportMeans) {
		this.vesselTransportMeans = vesselTransportMeans;
	}

	
	public FluxReportDocument getFluxReportDocument() {
		return this.fluxReportDocument;
	}

	public void setFluxReportDocument(FluxReportDocument fluxReportDocument) {
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

	
	public String getRelatedReport() {
		return this.relatedReport;
	}

	public void setRelatedReport(String relatedReport) {
		this.relatedReport = relatedReport;
	}

	
	public Date getAcceptedDatetime() {
		return this.acceptedDatetime;
	}

	public void setAcceptedDatetime(Date acceptedDatetime) {
		this.acceptedDatetime = acceptedDatetime;
	}

	
	public Set<FishingActivity> getFishingActivities() {
		return this.fishingActivities;
	}

	public void setFishingActivities(Set<FishingActivity> fishingActivities) {
		this.fishingActivities = fishingActivities;
	}

}
