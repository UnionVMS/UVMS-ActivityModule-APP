/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.dao;

import eu.europa.ec.fisheries.mdr.domain.MdrCodeListStatus;
import eu.europa.ec.fisheries.mdr.domain.constants.AcronymListState;
import eu.europa.ec.fisheries.uvms.domain.DateRange;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import un.unece.uncefact.data.standard.unqualifieddatatype._13.TextType;
import xeu.ec.fisheries.flux_bl.flux_aggregatebusinessinformationentity._1._1.FLUXPeriodType;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.MDRCodeListType;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

/**
 * Created by kovian on 29/07/2016.
 */
@Slf4j
public class MdrStatusDao extends AbstractDAO<MdrCodeListStatus> {

    private EntityManager em;

    private static final String SELECT_FROM_MDRSTATUS_WHERE_ACRONYM = "FROM MdrCodeListStatus WHERE objectAcronym=";
    private static final String SELECT_UPDATABLE_FROM_MDRSTATUS = "FROM MdrCodeListStatus WHERE schedulable='Y'";

    public MdrStatusDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<MdrCodeListStatus> findAllStatuses() throws ServiceException {
        return findAllEntity(MdrCodeListStatus.class);
    }

    public MdrCodeListStatus findStatusByAcronym(String acronym) {
        MdrCodeListStatus entity = null;
        List<MdrCodeListStatus> stausList = null;
        try {
            stausList = findEntityByHqlQuery(MdrCodeListStatus.class, SELECT_FROM_MDRSTATUS_WHERE_ACRONYM + "'"+acronym+"'");
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

    public void saveAcronymsStatusList(List<MdrCodeListStatus> statusList) throws ServiceException {
        if (CollectionUtils.isNotEmpty(statusList)) {
            Session session = (getEntityManager().unwrap(Session.class)).getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            try {
                log.info("Persisting entity entries for : MdrCodeListStatus");
                int counter = 0;
                for(MdrCodeListStatus actStatus : statusList){
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

    public List<MdrCodeListStatus> findAllUpdatableStatuses() {
        List<MdrCodeListStatus> statuses = null;
        try {
            statuses = findEntityByHqlQuery(MdrCodeListStatus.class, SELECT_UPDATABLE_FROM_MDRSTATUS);
        } catch (ServiceException e) {
            log.error("Error while trying to get schedulable Acronyms list : ", e);
        }
        return statuses;
    }

    public void updateStatusSuccessForAcronym(String acronym, AcronymListState newStatus, Date lastSuccess) {
        MdrCodeListStatus mdrCodeListElement = findStatusByAcronym(acronym);
        mdrCodeListElement.setLastSuccess(lastSuccess);
        mdrCodeListElement.setLastStatus(newStatus);
        try {
            saveOrUpdateEntity(mdrCodeListElement);
        } catch (ServiceException e) {
            log.error("Error while trying to save/update new MDR Code List Status",e);
        }
    }

    public void updateStatusSuccessForAcronym(MDRCodeListType codeListType, AcronymListState newStatus, Date lastSuccess) {
        MdrCodeListStatus mdrCodeListElement = findStatusByAcronym(codeListType.getObjAcronym().getValue());
        fillMetaDataForMDRStatus(codeListType, mdrCodeListElement);
        mdrCodeListElement.setLastSuccess(lastSuccess);
        mdrCodeListElement.setLastStatus(newStatus);
        try {
            saveOrUpdateEntity(mdrCodeListElement);
        } catch (ServiceException e) {
            log.error("Error while trying to save/update new MDR Code List Status",e);
        }
    }

    private void fillMetaDataForMDRStatus(MDRCodeListType xmlCodeListMetaData, MdrCodeListStatus codeListFromDB) {
        codeListFromDB.setObjectSource(xmlCodeListMetaData.getSource().getValue());
        codeListFromDB.setObjectDescription(xmlCodeListMetaData.getObjDescription().getValue());
        codeListFromDB.setObjectName(xmlCodeListMetaData.getObjName().getValue());

        FLUXPeriodType validityXml = xmlCodeListMetaData.getValidityPeriod();
        DateRange validity =new DateRange(validityXml.getStartDate().toGregorianCalendar().getTime(),
                validityXml.getEndDate().toGregorianCalendar().getTime());
        codeListFromDB.setValidity(validity);

        StringBuffer buffer = new StringBuffer();
        for(TextType version : xmlCodeListMetaData.getVersions()){
            buffer.append(version.getValue()).append(",");
        }
        codeListFromDB.setVersions(buffer.toString());
    }

    public void updateStatusFailedForAcronym(String acronym) {
        MdrCodeListStatus mdrCodeListElement = findStatusByAcronym(acronym);
        mdrCodeListElement.setLastStatus(AcronymListState.FAILED);
        try {
            saveOrUpdateEntity(mdrCodeListElement);
        } catch (ServiceException e) {
            log.error("Error while trying to save/update new MDR Code List Status",e);
        }
    }

    public void updateSchedulableForAcronym(String acronym, boolean schedulable) {
        MdrCodeListStatus mdrCodeListElement = findStatusByAcronym(acronym);
        try {
            mdrCodeListElement.setSchedulable(schedulable);
            saveOrUpdateEntity(mdrCodeListElement);
        } catch (ServiceException | NullPointerException e) {
            log.error("Error while trying to save/update new MDR Code List Status",e);
        }
    }

    public void updateStatusAttemptForAcronym(String acronym, AcronymListState newStatus, Date lastAttempt) {
        MdrCodeListStatus mdrCodeListElement = findStatusByAcronym(acronym);
        mdrCodeListElement.setLastAttempt(lastAttempt);
        mdrCodeListElement.setLastStatus(newStatus);
        try {
            if(mdrCodeListElement != null){
                saveOrUpdateEntity(mdrCodeListElement);
            }
        } catch (NullPointerException | ServiceException e) {
            log.error("Error while trying to save/update new MDR Code List Status",e);
        }
    }

    public List<MdrCodeListStatus> getAllUpdatableAcronymsStatuses() {
        List<MdrCodeListStatus> statussesList =  findAllUpdatableStatuses();
        return statussesList;
    }

    public List<MdrCodeListStatus> getAllAcronymsStatuses() {
        List<MdrCodeListStatus> statussesList = null;
        try {
            statussesList =  findAllStatuses();
        } catch (ServiceException e) {
            log.error("Error while getting MDR Code List Statusses",e);
        }
        return statussesList;
    }

    public MdrCodeListStatus getStatusForAcronym(String acronym) {
        return findStatusByAcronym(acronym);
    }
}