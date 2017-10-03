/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.uvms.init.AbstractModuleInitializerBean;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.InputStream;
import java.util.TimeZone;

/**
 * Created by padhyad on 5/2/2016.
 */
@Singleton
@Startup
@Slf4j
public class ActivityInitializerBean extends AbstractModuleInitializerBean {

    @PostConstruct
    void init() {
        log.info("In InitializeBean Current Default timezone is:"+ TimeZone.getDefault().getDisplayName());
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        log.info("New Default timezone set: "+  TimeZone.getDefault().getDisplayName());
    }

    @Override
    protected InputStream getDeploymentDescriptorRequest() {
        return this.getClass().getClassLoader().getResourceAsStream("usmDeploymentDescriptor.xml");
    }

    @Override
    protected boolean mustRedeploy() {
        return true;
    }


}