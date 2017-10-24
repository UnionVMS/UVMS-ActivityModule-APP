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

import com.vividsolutions.jts.geom.Geometry;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by sanera on 9/16/2016.
 */
@Entity
@Table(name = "activity_vessel_position_event")
public class VesselPositionEventEntity implements Serializable {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "pos_event_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vessel_transport_means_id")
    private VesselTransportMeansEntity vesselTransportMeans;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "obtained_occurrence_date_time", length = 29)
    private Date obtainedOccurrenceDateTime;

    @Column(name = "type_code")
    private String typeCode;

    @Column(name = "speed_value_measure")
    private Double speedValueMeasure;

    @Column(name = "course_value_measure")
    private Double courseValueMeasure;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "altitude")
    private Double altitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "activity_type_code")
    private String activityTypeCode;

    @Type(type = "org.hibernate.spatial.GeometryType")
    @Column(name = "geom")
    private Geometry geom;

    public int getId() {
        return id;
    }

    public VesselTransportMeansEntity getVesselTransportMeans() {
        return vesselTransportMeans;
    }

    public void setVesselTransportMeans(VesselTransportMeansEntity vesselTransportMeans) {
        this.vesselTransportMeans = vesselTransportMeans;
    }


    public void setId(int id) {
        this.id = id;
    }

    public Date getObtainedOccurrenceDateTime() {
        return obtainedOccurrenceDateTime;
    }

    public void setObtainedOccurrenceDateTime(Date obtainedOccurrenceDateTime) {
        this.obtainedOccurrenceDateTime = obtainedOccurrenceDateTime;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Double getSpeedValueMeasure() {
        return speedValueMeasure;
    }

    public void setSpeedValueMeasure(Double speedValueMeasure) {
        this.speedValueMeasure = speedValueMeasure;
    }

    public Double getCourseValueMeasure() {
        return courseValueMeasure;
    }

    public void setCourseValueMeasure(Double courseValueMeasure) {
        this.courseValueMeasure = courseValueMeasure;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getActivityTypeCode() {
        return activityTypeCode;
    }

    public Geometry getGeom() {
        return geom;
    }

    public void setGeom(Geometry geom) {
        this.geom = geom;
    }

    public void setActivityTypeCode(String activityTypeCode) {
        this.activityTypeCode = activityTypeCode;
    }
}
