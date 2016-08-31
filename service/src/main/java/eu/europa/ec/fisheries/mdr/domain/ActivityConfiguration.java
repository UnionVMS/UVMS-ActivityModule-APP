/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.domain;

/**
 * Created by kovian on 29/07/2016.
 */
import eu.europa.ec.fisheries.uvms.domain.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity that will map the configuration of Activity module.
 */
@Entity
@Table(name = "activity_configuration")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ActivityConfiguration extends BaseEntity {

    @Column(name = "config_name")
    private String configName;

    @Column(name = "config_value")
    private String configValue;

    public ActivityConfiguration(){super();}

    public ActivityConfiguration(String configName, String configValue) {
    	setConfigName(configName);
    	setConfigValue(configValue);
	}
	public String getConfigName() {
        return configName;
    }
    public void setConfigName(String configName) {
        this.configName = configName;
    }
    public String getConfigValue() {
        return configValue;
    }
    public void setConfigValue(String value) {
        this.configValue = value;
    }

}
