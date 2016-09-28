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

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by padhyad on 9/15/2016.
 */
@Entity
@Table(name = "activity_fishing_gear_role")
public class FishingGearRoleEntity implements Serializable {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fishing_gear_id")
    private FishingGearEntity fishingGear;

    @Column(name = "role_code", nullable = false)
    private String roleCode;

    @Column(name = "role_code_list_id", nullable = false)
    private String roleCodeListId;

    public int getId() {
        return id;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleCodeListId() {
        return roleCodeListId;
    }

    public void setRoleCodeListId(String roleCodeListId) {
        this.roleCodeListId = roleCodeListId;
    }

    public FishingGearEntity getFishingGear() {
        return fishingGear;
    }

    public void setFishingGear(FishingGearEntity fishingGear) {
        this.fishingGear = fishingGear;
    }
}
