package eu.europa.ec.fisheries.ers.fa.dao;

import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;

import java.io.Serializable;
import java.util.List;

/**
 * Created by padhyad on 5/18/2016.
 */
@Slf4j
public abstract class AbstractFaDao<T extends Serializable> extends AbstractDAO<T> {

    public void bulkUploadFaData(List<T> entities) throws ServiceException {
        for (T entity : entities) {
            getEntityManager().persist(entity);
        }
    }
}
