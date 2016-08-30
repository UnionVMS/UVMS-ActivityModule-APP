/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.dao;

import eu.europa.ec.fisheries.mdr.domain.MdrStatus;
import eu.europa.ec.fisheries.mdr.domain.constants.AcronymListState;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

/**
 * Created by kovian on 29/07/2016.
 */
@Slf4j
public class MdrStatusDao extends AbstractDAO<MdrStatus> {

    private EntityManager em;

    private static final String SELECT_FROM_MDRSTATUS_WHERE_ACRONYM = "FROM MdrStatus WHERE objectAcronym=";
    private static final String SELECT_UPDATABLE_FROM_MDRSTATUS = "FROM MdrStatus WHERE schedulable='Y'";

    public MdrStatusDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<MdrStatus> findAllStatuses() throws ServiceException {
        return findAllEntity(MdrStatus.class);
    }

    public MdrStatus findStatusByAcronym(String acronym) {
        MdrStatus entity = null;
        List<MdrStatus> stausList = null;
        try {
            stausList = findEntityByHqlQuery(MdrStatus.class, SELECT_FROM_MDRSTATUS_WHERE_ACRONYM + "'"+acronym+"'");
            if(CollectionUtils.isNotEmpty(stausList)){
                entity = stausList.get(0);
            } else {
                log.error("Couldn't find Status for acronym : {}",acronym);
            }
        } catch (ServiceException e) {
            log.error("Error while trying to get Status for acronym : ", acronym, e);
        }
        return entity;
    }

    public void saveAcronymsStatusList(List<MdrStatus> statusList) throws ServiceException {
        if (CollectionUtils.isNotEmpty(statusList)) {
            Session session = (getEntityManager().unwrap(Session.class)).getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            try {
                log.info("Persisting entity entries for : MdrStatus");
                int counter = 0;
                for(MdrStatus actStatus : statusList){
                    counter++;
                    session.save(actStatus);
                    if (counter % 20 == 0 ) {
                        //Each 20 rows persist and release memory;
                        session.flush();
                        session.clear();
                    }
                }
                log.debug("Committing transaction.");
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw new ServiceException("Rollbacking transaction for reason : ", e);
            } finally {
                log.debug("Closing session");
                session.close();
            }
        }
    }

    public List<MdrStatus> findAllUpdatableStatuses() {
        List<MdrStatus> statuses = null;
        try {
            statuses = findEntityByHqlQuery(MdrStatus.class, SELECT_UPDATABLE_FROM_MDRSTATUS);
        } catch (ServiceException e) {
            log.error("Error while trying to get schedulable Acronyms list : ", e);
        }
        return statuses;
    }

    public void updateStatusSuccessForAcronym(String acronym, AcronymListState newStatus, Date lastSuccess) {
        MdrStatus mdrCodeListElement = findStatusByAcronym(acronym);
        mdrCodeListElement.setLastSuccess(lastSuccess);
        mdrCodeListElement.setLastStatus(newStatus);
        try {
            saveOrUpdateEntity(mdrCodeListElement);
        } catch (ServiceException e) {
            log.error("Error while trying to save/update new MDR Code List Status",e);
        }
    }

    public void updateStatusFailedForAcronym(String acronym) {
        MdrStatus mdrCodeListElement = findStatusByAcronym(acronym);
        mdrCodeListElement.setLastStatus(AcronymListState.FAILED);
        try {
            saveOrUpdateEntity(mdrCodeListElement);
        } catch (ServiceException e) {
            log.error("Error while trying to save/update new MDR Code List Status",e);
        }
    }

    public void updateSchedulableForAcronym(String acronym, boolean schedulable) {
        MdrStatus mdrCodeListElement = findStatusByAcronym(acronym);
        try {
            mdrCodeListElement.setSchedulable(schedulable);
            saveOrUpdateEntity(mdrCodeListElement);
        } catch (ServiceException | NullPointerException e) {
            log.error("Error while trying to save/update new MDR Code List Status",e);
        }
    }

    public void updateStatusAttemptForAcronym(String acronym, AcronymListState newStatus, Date lastAttempt) {
        MdrStatus mdrCodeListElement = findStatusByAcronym(acronym);
        mdrCodeListElement.setLastAttempt(lastAttempt);
        mdrCodeListElement.setLastStatus(newStatus);
        try {
            saveOrUpdateEntity(mdrCodeListElement);
        } catch (ServiceException e) {
            log.error("Error while trying to save/update new MDR Code List Status",e);
        }
    }

    public List<MdrStatus> getAllUpdatableAcronymsStatuses() {
        List<MdrStatus> statussesList =  findAllUpdatableStatuses();
        return statussesList;
    }

    public List<MdrStatus> getAllAcronymsStatuses() {
        List<MdrStatus> statussesList = null;
        try {
            statussesList =  findAllStatuses();
        } catch (ServiceException e) {
            log.error("Error while getting MDR Code List Statusses",e);
        }
        return statussesList;
    }

    public MdrStatus getStatusForAcronym(String acronym) {
        return findStatusByAcronym(acronym);
    }
}