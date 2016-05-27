package eu.europa.ec.fisheries.mdr.dao;

import javax.persistence.EntityManager;

import eu.europa.ec.fisheries.mdr.domain.MasterDataRegistry;

public class MasterDataRegistryDao<T extends MasterDataRegistry> extends AbstractMdrDao<T>  {
    private EntityManager em;

    public MasterDataRegistryDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
