/*
 * ﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
 * © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.fa.dao.FluxFaReportMessageDao;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class FaMessageSaverBean extends BaseActivityBean {

    private FluxFaReportMessageDao fluxReportMessageDao;

    @PostConstruct
    public void init() {
        initEntityManager();
        fluxReportMessageDao = new FluxFaReportMessageDao(getEntityManager());
    }

    public FluxFaReportMessageEntity saveReportMessageNow(FluxFaReportMessageEntity messageEntity) {
        return fluxReportMessageDao.createEntity(messageEntity);
    }

}
