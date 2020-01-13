package eu.europa.ec.fisheries.uvms.activity.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.util.List;

@ApplicationScoped
public class TestDataDestroyer {

    @Inject
    private UserTransaction userTransaction;

    @PersistenceContext(unitName = "activityPUpostgres")
    private EntityManager entityManager;

    public void truncateAllTables(@Observes @Destroyed(ApplicationScoped.class) final Object event) throws NotSupportedException, HeuristicMixedException, SystemException, HeuristicRollbackException, RollbackException {
        userTransaction.begin();

        String allTablesQueryString = "SELECT relname\n" +
                "  FROM pg_stat_user_tables\n" +
                "  WHERE schemaname = 'activity'";
        Query allTablesQuery = entityManager.createNativeQuery(allTablesQueryString);
        List allTables = allTablesQuery.getResultList();

        for (Object tableName : allTables) {
            String queryString = "TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE";
            Query query = entityManager.createNativeQuery(queryString);
            query.executeUpdate();
        }

        userTransaction.commit();
    }
}
