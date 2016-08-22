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
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by kovian on 29/07/2016.
 */
@Slf4j
public class MdrStatusDao extends AbstractDAO<MdrStatus> {

    private EntityManager em;

    private static final String SELECT_FROM_MDRSTATUS_WHERE_ACRONYM = "FROM MdrStatus WHERE objectAcronym=";

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
        try {
            entity = findEntityByHqlQuery(MdrStatus.class, SELECT_FROM_MDRSTATUS_WHERE_ACRONYM + ""+acronym).get(0);
        } catch (ServiceException e) {
            log.error("Error while trying to get Status for acronym : ", acronym, e);
        }
        return entity;
    }

    public void saveAcronymsStatusList(List<MdrStatus> diffList) throws ServiceException {
        if (CollectionUtils.isNotEmpty(diffList)) {
            Session session = (getEntityManager().unwrap(Session.class)).getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            try {
                log.info("Persisting entity entries for : MdrStatus");
                session.save(diffList);
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
        em.flush();
    }
}