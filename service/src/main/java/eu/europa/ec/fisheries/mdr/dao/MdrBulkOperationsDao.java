package eu.europa.ec.fisheries.mdr.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;

import eu.europa.ec.fisheries.mdr.domain.MasterDataRegistry;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kovian
 *
 * This class is used only for bulk insertions.
 *
 * @param <T extends MasterDataRegistry>
 */
@Slf4j
public class MdrBulkOperationsDao {
	
    private EntityManager em;
    private static final String HQL_DELETE= "DELETE FROM ";
	
    /**
     * Deletes all entries of all the given Entities and then inserts all the new ones.
     * The input is the list of all the entities and all their instances (List of tables) ready to be persisted (each entity contains one or more records).
     * 
     * @param masterDataGenericList
     * @throws ServiceException
     */
    public void multiEntityBulkDeleteAndInsert(List<List<? extends MasterDataRegistry>> masterDataGenericList) throws ServiceException {
        
    	StatelessSession session = (getEntityManager().unwrap(Session.class)).getSessionFactory().openStatelessSession();
        Transaction tx = session.beginTransaction();
        
        try {
        	
        	for(List<? extends MasterDataRegistry> entityRows : masterDataGenericList){
        		
        		log.info("Persisting entity entries for : " + masterDataGenericList.getClass().getSimpleName());

        		// DELETION PHASE (Deleting old entries)
        		session.createQuery(HQL_DELETE + entityRows.get(0).getClass().getSimpleName()).executeUpdate();
        		
        		// INSERTION PHASE (Inserting new entries)
        		for(MasterDataRegistry actualEnityRow : entityRows){
        			log.info("Persisting entity : " + actualEnityRow.getClass().getSimpleName());
        			actualEnityRow.createAudit();
        			session.insert(actualEnityRow);
        		}
        		   
        	}
        	log.debug("Committing transaction.");
            tx.commit();
        
        } catch (Exception e){
            tx.rollback();
            throw new ServiceException("Rollbacking transaction for reason : ", e);
        }
        finally {
            log.debug("Closing session");
            session.close();
        }
    }
    
    /**
     * Deletes all entries of all the given Entities and then inserts all the new ones.
     * The input is the list of all the entities and all their instances (List of tables) ready to be persisted (each entity contains one or more records).
     * 
     * @param masterDataGenericList
     * @throws ServiceException
     */
    public void singleEntityBulkDeleteAndInsert(List<? extends MasterDataRegistry> entityRows) throws ServiceException {
        
    	if(!CollectionUtils.isEmpty(entityRows)){
    		
        	StatelessSession session = (getEntityManager().unwrap(Session.class)).getSessionFactory().openStatelessSession();
            Transaction tx = session.beginTransaction();
            
            try {
        		log.info("Persisting entity entries for : " + entityRows.getClass().getSimpleName());

        		// DELETION PHASE (Deleting old entries)
        		session.createQuery(HQL_DELETE + entityRows.get(0).getClass().getSimpleName()).executeUpdate();
        		
        		// INSERTION PHASE (Inserting new entries)
        		for(MasterDataRegistry actualEnityRow : entityRows){
        			log.info("Persisting entity : " + actualEnityRow.getClass().getSimpleName());
        			actualEnityRow.createAudit();
        			session.insert(actualEnityRow);
        		}
            	log.debug("Committing transaction.");
                tx.commit();
            
            } catch (Exception e){
                tx.rollback();
                throw new ServiceException("Rollbacking transaction for reason : ", e);
            }
            finally {
                log.debug("Closing session");
                session.close();
            }
    	}
    	
    }

    public MdrBulkOperationsDao(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return em;
    }
}
