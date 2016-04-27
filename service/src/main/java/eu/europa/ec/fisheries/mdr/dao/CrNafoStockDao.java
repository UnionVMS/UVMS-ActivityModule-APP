package eu.europa.ec.fisheries.mdr.dao;

import eu.europa.ec.fisheries.mdr.domain.CrNafoStock;
import javax.persistence.EntityManager;

public class CrNafoStockDao extends AbstractMdrDao<CrNafoStock> {

    private EntityManager em;

    public CrNafoStockDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
