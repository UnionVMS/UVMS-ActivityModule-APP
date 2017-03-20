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
import java.io.Serializable;

/**
 * Created by padhyad on 9/16/2016.
 */
@Entity
@Table(name = "activity_vessel_storage_char_code")
public class VesselStorageCharCodeEntity implements Serializable {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "str_char_code_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vessel_storage_char_id")
    private VesselStorageCharacteristicsEntity vesselStorageCharacteristics;

    @Column(name = "vessel_type_code")
    private String vesselTypeCode;

    @Column(name = "vessel_type_code_list_id")
    private String vesselTypeCodeListId;

    public int getId() {
        return id;
    }

    public VesselStorageCharacteristicsEntity getVesselStorageCharacteristics() {
        return vesselStorageCharacteristics;
    }

    public void setVesselStorageCharacteristics(VesselStorageCharacteristicsEntity vesselStorageCharacteristics) {
        this.vesselStorageCharacteristics = vesselStorageCharacteristics;
    }

    public String getVesselTypeCode() {
        return vesselTypeCode;
    }

    public void setVesselTypeCode(String vesselTypeCode) {
        this.vesselTypeCode = vesselTypeCode;
    }

    public String getVesselTypeCodeListId() {
        return vesselTypeCodeListId;
    }

    public void setVesselTypeCodeListId(String vesselTypeCodeListId) {
        this.vesselTypeCodeListId = vesselTypeCodeListId;
    }
}
