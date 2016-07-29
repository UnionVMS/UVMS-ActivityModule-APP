package eu.europa.ec.fisheries.mdr.dao;

import eu.europa.ec.fisheries.mdr.domain2.MdrConfiguration;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;

import javax.persistence.EntityManager;

/**
 * Created by kovian on 29/07/2016.
 */
public class MdrConfigurationDao extends AbstractDAO<MdrConfiguration> {

    private EntityManager em;

    public MdrConfigurationDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

}