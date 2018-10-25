/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NamedQueries({
        @NamedQuery(name = ActivityConfiguration.GET_PROPERTY_VALUE,
                query = "SELECT config.configValue " +
                        "FROM ActivityConfiguration config " +
                        "WHERE config.configName =:" + ActivityConfiguration.CONFIG_NAME)
})
@Entity
@Table(name = "activity_configuration")
@Data
@NoArgsConstructor
public class ActivityConfiguration implements Serializable {

    public static final String GET_PROPERTY_VALUE ="GetPropertyValue";
    public static final String CONFIG_NAME = "configName";
    public static final String LIMIT_FISHING_TRIPS ="LIMIT_FISHING_TRIPS";

    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN_activity_configuration", sequenceName = "act_config_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN_activity_configuration")
    private int id;

    @Column(name = "config_name")
    private String configName;

    @Column(name = "config_value")
    private String configValue;

}
