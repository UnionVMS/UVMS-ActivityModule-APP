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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

import com.vividsolutions.jts.geom.Geometry;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activity_vessel_position_event")
@Data
@NoArgsConstructor
public class VesselPositionEventEntity implements Serializable {

    @Id
    @Column(unique = true, nullable = false)
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

    private Double latitude;

    private Double altitude;

    private Double longitude;

    @Column(name = "activity_type_code")
    private String activityTypeCode;

    @Column(name = "geom", columnDefinition = "Geometry")
    private Geometry geom;

}
