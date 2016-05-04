package eu.europa.ec.fisheries.mdr.dao;

import eu.europa.ec.fisheries.mdr.domain.MasterDataRegistry;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;

public abstract class AbstractMdrDao <E extends MasterDataRegistry> extends AbstractDAO<E> {
	
    /* public void bulkInsert(Map<String, List<Property>> features) throws ServiceException {
        StatelessSession session = (getEntityManager().unwrap(Session.class)).getSessionFactory().openStatelessSession();
        Transaction tx = session.beginTransaction();
        try {
            Query query = session.getNamedQuery(getDisableAreaNamedQuery());
            query.executeUpdate();
            for (List<Property> properties : features.values()) {
                Map<String, Object> values = BaseAreaEntity.createAttributesMap(properties);
                session.insert(createEntity(values));
            }
            log.debug("Commit transaction");
            tx.commit();
        }
        catch (ServiceException e){
            tx.rollback();
            throw new ServiceException("Rollback transaction", e);
        }
        finally {
            log.debug("Closing session");
            session.close();
        }
    } */
}
