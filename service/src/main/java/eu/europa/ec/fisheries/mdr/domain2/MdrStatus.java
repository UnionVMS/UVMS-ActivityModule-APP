/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain2;

import eu.europa.ec.fisheries.mdr.converter.CharAcronymListStateConverter;
import eu.europa.ec.fisheries.mdr.domain.constants.AcronymListState;
import eu.europa.ec.fisheries.uvms.domain.BaseEntity;
import eu.europa.ec.fisheries.uvms.domain.CharBooleanConverter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;


/**
 * Entity that has to be updated each time a Code_List has been synchronized.
 * If the update of an entity was succesful then the fields : last_update,last_attempt
 * should have the same value.
 */
@Entity
@Table(name = "mdr_status")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MdrStatus extends BaseEntity {

    @Column(name = "object_acronym")
    private String objectAcronym;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_attempt")
    private Date lastAttempt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_update")
    private Date lastUpdate;

    @Column(name = "state")
    @Convert(converter = CharAcronymListStateConverter.class)
    private AcronymListState state;

    @Column(name = "updatable")
    @Convert(converter = CharBooleanConverter.class)
    private Boolean updatable;

    private MdrStatus(){}

    public MdrStatus(String objectAcronym, Date lastUpdate, Date lastAttempt, AcronymListState state, Boolean updatable) {
        this.objectAcronym = objectAcronym;
        this.lastUpdate = lastUpdate;
        this.lastAttempt = lastAttempt;
        this.state = state;
        this.updatable = updatable;
    }

    public String getObjectAcronym() {
        return objectAcronym;
    }
    public void setObjectAcronym(String objectAcronym) {
        this.objectAcronym = objectAcronym;
    }
    public Date getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    public AcronymListState getState() {
        return state;
    }
    public void setState(AcronymListState state) {
        this.state = state;
    }
    public Boolean getUpdatable() {
        return updatable;
    }
    public void setUpdatable(Boolean updatable) {
        this.updatable = updatable;
    }
    public Date getLastAttempt() {
        return lastAttempt;
    }
    public void setLastAttempt(Date lastAttempt) {
        this.lastAttempt = lastAttempt;
    }
}
