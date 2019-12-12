/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.activity.fa.dao;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityIDType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityTableType;
import eu.europa.ec.fisheries.uvms.commons.service.dao.AbstractDAO;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FluxReportIdentifierDao extends AbstractDAO<FluxReportIdentifierEntity> {

    private static final String ID = "id";
    private static final String SCHEME_ID = "schemeId";

    private EntityManager em;

    public FluxReportIdentifierDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<FluxReportIdentifierEntity> getMatchingIdentifiers(List<ActivityIDType> ids, ActivityTableType tableType) {
        String namedQueryToSelect = tableType == ActivityTableType.FLUX_REPORT_DOCUMENT_ENTITY ? FluxReportIdentifierEntity.FIND_MATCHING_IDENTIFIER : FluxReportIdentifierEntity.FIND_RELATED_MATCHING_IDENTIFIER;
        List<FluxReportIdentifierEntity> resultList = new ArrayList<>();
        // FIXME avoid looping and querying
        for (ActivityIDType idType : ids) {
            TypedQuery<FluxReportIdentifierEntity> query = getEntityManager().createNamedQuery(namedQueryToSelect, FluxReportIdentifierEntity.class);
            query.setParameter(ID, idType.getValue());
            query.setParameter(SCHEME_ID, idType.getIdentifierSchemeId());
            resultList.addAll(query.getResultList());
        }
        resultList.removeAll(Collections.singleton(null));
        return resultList;
    }
}
