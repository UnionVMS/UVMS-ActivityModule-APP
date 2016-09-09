/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain;

import eu.europa.ec.fisheries.mdr.converter.CharAcronymListStateConverter;
import eu.europa.ec.fisheries.mdr.domain.constants.AcronymListState;
import eu.europa.ec.fisheries.uvms.domain.BaseEntity;
import eu.europa.ec.fisheries.uvms.domain.CharBooleanConverter;
import eu.europa.ec.fisheries.uvms.domain.DateRange;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;


/**
 * @author kovian
 * 
 * Entity that has to be updated each time a Code_List has been synchronized.
 * If the update of an entity was succesful then the fields : last_update,last_attempt
 * should have the same value.
 */
@Entity
@Table(name = "mdr_codelist_status")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MdrCodeListStatus extends BaseEntity {

    @Column(name = "object_acronym")
    private String objectAcronym;
    
    @Column(name = "object_name")
    private String objectName;

    @Column(name = "object_description")
    private String objectDescription;

    @Column(name = "object_source")
    private String objectSource;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_attempt")
    private Date lastAttempt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_success")
    private Date lastSuccess;

    @Embedded
    private DateRange validity;

    @Column(name = "last_status")
    @Convert(converter = CharAcronymListStateConverter.class)
    private AcronymListState lastStatus;

    @Column(name = "schedulable")
    @Convert(converter = CharBooleanConverter.class)
    private Boolean schedulable;

    @Column(name = "versions")
    private String versions;

    private MdrCodeListStatus(){super();}

    public MdrCodeListStatus(String objectAcronym, String objectName, Date lastUpdate, Date lastAttempt, AcronymListState state, Boolean schedulable) {
        this.objectAcronym = objectAcronym;
        this.objectName  = objectName;
        this.lastSuccess = lastUpdate;
        this.lastAttempt = lastAttempt;
        this.lastStatus = state;
        this.schedulable = schedulable;
    }

    public MdrCodeListStatus(String objectAcronym, String objectName, String objectDescription, String objectSource, Date lastAttempt, Date lastSuccess, AcronymListState lastStatus, Boolean schedulable) {
        this.objectAcronym = objectAcronym;
        this.objectName = objectName;
        this.objectDescription = objectDescription;
        this.objectSource = objectSource;
        this.lastAttempt = lastAttempt;
        this.lastSuccess = lastSuccess;
        this.lastStatus = lastStatus;
        this.schedulable = schedulable;
    }

    public String getObjectAcronym() {
        return objectAcronym;
    }
    public void setObjectAcronym(String objectAcronym) {
        this.objectAcronym = objectAcronym;
    }
    public Date getLastSuccess() {
        return lastSuccess;
    }
    public void setLastSuccess(Date lastUpdate) {
        this.lastSuccess = lastUpdate;
    }
    public AcronymListState getLastStatus() {
        return lastStatus;
    }
    public void setLastStatus(AcronymListState state) {
        this.lastStatus = state;
    }
    public Boolean getSchedulable() {
        return schedulable;
    }
    public void setSchedulable(Boolean updatable) {
        this.schedulable = updatable;
    }
    public Date getLastAttempt() {
        return lastAttempt;
    }
    public void setLastAttempt(Date lastAttempt) {
        this.lastAttempt = lastAttempt;
    }
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
    public String getObjectDescription() {
        return objectDescription;
    }
    public void setObjectDescription(String objectDescription) {
        this.objectDescription = objectDescription;
    }
    public String getObjectSource() {
        return objectSource;
    }
    public void setObjectSource(String objectSource) {
        this.objectSource = objectSource;
    }
    public String getVersions() {
        return versions;
    }
    public void setVersions(String versions) {
        this.versions = versions;
    }
    public DateRange getValidity() {
        return validity;
    }
    public void setValidity(DateRange validity) {
        this.validity = validity;
    }
}
