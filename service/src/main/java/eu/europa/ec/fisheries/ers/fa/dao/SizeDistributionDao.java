package eu.europa.ec.fisheries.ers.fa.dao;


import javax.persistence.EntityManager;

import eu.europa.ec.fisheries.ers.fa.entities.SizeDistributionEntity;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;

public class SizeDistributionDao extends AbstractDAO<SizeDistributionEntity>{
	
	private EntityManager em;
	
	public SizeDistributionDao(EntityManager em) {
        this.em = em;
    }

	@Override
	public EntityManager getEntityManager() {
		// TODO Auto-generated method stub
		return em;
	}

}
