package eu.europa.ec.fisheries.mdr.dao;

import javax.persistence.EntityManager;

import eu.europa.ec.fisheries.mdr.domain.ActionType;

public class ActionTypeDao extends AbstractMdrDao<ActionType> {

    private EntityManager em;

    public ActionTypeDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    
}