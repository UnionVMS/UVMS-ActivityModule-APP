package eu.europa.ec.fisheries.ers.fa.dao;


import javax.persistence.EntityManager;

import eu.europa.ec.fisheries.ers.fa.entities.SizeDistribution;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;

public class SizeDistributionDao extends AbstractDAO<SizeDistribution>{
	
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
