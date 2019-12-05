package eu.europa.ec.fisheries.uvms.activity.rest;

import eu.europa.ec.fisheries.uvms.activity.service.bean.BaseActivityBean;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Stateless
public class TestHelperServiceBean extends BaseActivityBean {

    @PostConstruct
    public void init() {
        initEntityManager();
    }

    @Transactional
    public void deleteAllFromDb(Class entityClass) {
        String className = entityClass.getSimpleName();
        String queryString = "DELETE FROM " + className + " e";
        Query query = getEntityManager().createQuery(queryString);
        query.executeUpdate();
    }
}
