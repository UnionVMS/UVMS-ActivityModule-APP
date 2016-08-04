/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.dao;


import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 5/3/2016.
 */
public class FaReportDocumentDao extends AbstractFaDao<FaReportDocumentEntity> {

    private EntityManager em;

    public FaReportDocumentDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    private static final String REPORT_IDS = "reportIds";

    /**
     * Get FaReportDocument by one or more Report identifiers
     *
     * @param strings
     * @return
     * @throws ServiceException
     */
    public List<FaReportDocumentEntity> findFaReportsByIds(Set<String> strings) throws ServiceException {
        TypedQuery query = getEntityManager().createNamedQuery(FaReportDocumentEntity.FIND_BY_FA_ID, FaReportDocumentEntity.class);
        query.setParameter(REPORT_IDS, strings);
        return query.getResultList();
    }
}