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
@Table(name = "activity_size_distribution_classcode")
public class SizeDistributionClassCodeEntity implements Serializable {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "dist_code_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_distribution_id")
    private SizeDistributionEntity sizeDistribution;

    @Column(name = "class_code", nullable = false)
    private String classCode;

    @Column(name = "class_code_list_id", nullable = false)
    private String classCodeListId;

    public int getId() {
        return id;
    }

    public SizeDistributionEntity getSizeDistribution() {
        return sizeDistribution;
    }

    public void setSizeDistribution(SizeDistributionEntity sizeDistribution) {
        this.sizeDistribution = sizeDistribution;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassCodeListId() {
        return classCodeListId;
    }

    public void setClassCodeListId(String classCodeListId) {
        this.classCodeListId = classCodeListId;
    }
}
