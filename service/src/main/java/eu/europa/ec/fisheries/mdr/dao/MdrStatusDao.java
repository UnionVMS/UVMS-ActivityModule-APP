package eu.europa.ec.fisheries.mdr.dao;

import eu.europa.ec.fisheries.mdr.domain2.MdrStatus;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by kovian on 29/07/2016.
 */
@Slf4j
public class MdrStatusDao extends AbstractDAO<MdrStatus> {

    private EntityManager em;

    private static final String SELECT_FROM_MDRSTATUS_WHERE_ACRONYM = "FROM MdrStatus WHERE objectAcronym eq ";

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

    public MdrStatus findStatusByAcronym(String acronym){
        MdrStatus entity = null;
        try {
            entity =  findEntityByHqlQuery(MdrStatus.class, SELECT_FROM_MDRSTATUS_WHERE_ACRONYM + acronym).get(0);
        } catch (ServiceException e) {
            log.error("Error while trying to get Status for acronym : ", acronym, e);
        }
        return entity;
    }
}