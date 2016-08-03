/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.service.bean;

import eu.europa.ec.fisheries.mdr.mapper.MasterDataRegistryEntityCacheFactory;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.TimerService;

/**
 * Created by kovian on 29/07/2016.
 */
@Slf4j
@Singleton
//@Startup
public class MdrInitializationBean {

    //@EJB
    private MdrSynchronizationServiceBean synchBean;

    @Resource
    private TimerService service;

    /**
     * 1. Initialize the MasterDataRegistryEntityCacheFactory;
     *
     * 2. Get configuration from mdr_config table;
     *    If the configuration is present then : Initialize a scheduledExpression
     */
    @PostConstruct
    public void initializeMdrFromDbConfiguration() {
        MasterDataRegistryEntityCacheFactory.initialize();
    }
}
