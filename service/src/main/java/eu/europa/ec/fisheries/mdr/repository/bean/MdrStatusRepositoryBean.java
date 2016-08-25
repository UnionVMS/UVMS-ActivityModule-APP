/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.repository.bean;

import eu.europa.ec.fisheries.mdr.dao.MdrStatusDao;
import eu.europa.ec.fisheries.mdr.domain.MdrStatus;
import eu.europa.ec.fisheries.mdr.domain.constants.AcronymListState;
import eu.europa.ec.fisheries.mdr.repository.MdrStatusRepository;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

/**
 * Created by kovian on 29/07/2016.
 */
@Stateless
@Slf4j
public class MdrStatusRepositoryBean implements MdrStatusRepository {

    @PersistenceContext(unitName = "activityPU")
    private EntityManager em;

    private MdrStatusDao statusDao;

    @PostConstruct
    public void init() {
        statusDao = new MdrStatusDao(em);
    }

    @Override
    public List<MdrStatus> getAllAcronymsStatuses(){
        List<MdrStatus> statussesList = null;
        try {
            statussesList =  statusDao.findAllStatuses();
        } catch (ServiceException e) {
            log.error("Error while getting MDR Code List Statusses",e);
        }
        return statussesList;
    }

    @Override
    public List<MdrStatus> getAllUpdatableAcronymsStatuses() {
        List<MdrStatus> statussesList = null;
        statussesList =  statusDao.findAllUpdatableStatuses();
        return statussesList;
    }

    @Override
    public void saveAcronymsStatusList(List<MdrStatus> diffList) throws ServiceException {
        statusDao.saveAcronymsStatusList(diffList);
    }

    @Override
    public MdrStatus getStatusForAcronym(String acronym){
        return statusDao.findStatusByAcronym(acronym);
    }

    @Override
    public void updateStatusAttemptForAcronym(String acronym, AcronymListState newStatus, Date lastAttempt){
        MdrStatus mdrCodeListElement = statusDao.findStatusByAcronym(acronym);
        mdrCodeListElement.setLastAttempt(lastAttempt);
        mdrCodeListElement.setLastStatus(newStatus);
        try {
            statusDao.saveOrUpdateEntity(mdrCodeListElement);
        } catch (ServiceException e) {
            log.error("Error while trying to save/update new MDR Code List Status",e);
        }
    }

    @Override
    public void updateStatusSuccessForAcronym(String acronym, AcronymListState newStatus, Date lastSuccess){
        MdrStatus mdrCodeListElement = statusDao.findStatusByAcronym(acronym);
        mdrCodeListElement.setLastSuccess(lastSuccess);
        mdrCodeListElement.setLastStatus(newStatus);
        try {
            statusDao.saveOrUpdateEntity(mdrCodeListElement);
        } catch (ServiceException e) {
            log.error("Error while trying to save/update new MDR Code List Status",e);
        }
    }

    @Override
    public void updateStatusFailedForAcronym(String acronym) {
        MdrStatus mdrCodeListElement = statusDao.findStatusByAcronym(acronym);
        mdrCodeListElement.setLastStatus(AcronymListState.FAILED);
        try {
            statusDao.saveOrUpdateEntity(mdrCodeListElement);
        } catch (ServiceException e) {
            log.error("Error while trying to save/update new MDR Code List Status",e);
        }
    }

    @Override
    public void updateSchedulableForAcronym(String acronym, boolean schedulable){
        MdrStatus mdrCodeListElement = statusDao.findStatusByAcronym(acronym);
        mdrCodeListElement.setSchedulable(schedulable);
        try {
            statusDao.saveOrUpdateEntity(mdrCodeListElement);
        } catch (ServiceException e) {
            log.error("Error while trying to save/update new MDR Code List Status",e);
        }
    }
}
