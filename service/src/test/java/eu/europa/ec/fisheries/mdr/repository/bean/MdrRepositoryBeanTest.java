package eu.europa.ec.fisheries.mdr.repository.bean;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import eu.europa.ec.fisheries.mdr.dao.BaseMdrDaoTest;
import eu.europa.ec.fisheries.mdr.dao.MdrBulkOperationsDao;
import eu.europa.ec.fisheries.mdr.domain.codelists.FaoSpecies;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.SneakyThrows;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by georgige on 11/15/2016.
 */
public class MdrRepositoryBeanTest extends BaseMdrDaoTest {

    private MdrBulkOperationsDao bulkDao = new MdrBulkOperationsDao(em);

    private MdrRepositoryBean mdrRepoBean = new MdrRepositoryBean();

    @Before
    @SneakyThrows
    public void prepare() {
        Operation operation = sequenceOf(DELETE_ALL_MDR_SPECIES);
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), operation);
        dbSetupTracker.launchIfNecessary(dbSetup);

        //init the bean
        Whitebox.setInternalState(mdrRepoBean, "em", em);
        mdrRepoBean.init();
    }


    @Test
    @SneakyThrows
    public void testLuceneIndexingNoSearchFilters() throws ServiceException {
        List<FaoSpecies> species = new ArrayList<>(2);

        FaoSpecies species1 = new FaoSpecies();
        species1.setCode("COD");
        species1.setEnName("COD fish");

        FaoSpecies species2 = new FaoSpecies();
        species2.setCode("COD");
        species2.setEnName("COD fish");

        species.add(species1);
        species.add(species2);

        bulkDao.singleEntityBulkDeleteAndInsert(species);

        FullTextSession fullTextSession = Search.getFullTextSession((Session) em.getDelegate());
        Transaction tx = fullTextSession.beginTransaction();
        FaoSpecies FaoSpecies = (FaoSpecies) fullTextSession.load( FaoSpecies.class, 1L );

        fullTextSession.index(FaoSpecies);
        tx.commit(); //index only updated at commit time

        try {
            List<FaoSpecies> filterredEntities = (List<FaoSpecies>) mdrRepoBean.findCodeListItemsByAcronymAndFilter(species1.getAcronym(), 0, 5, "code", false, null, null);
            fail("ServiceException was expected but not thrown.");
        } catch (ServiceException exc) {
            assertTrue(exc.getCause() instanceof  IllegalArgumentException);
            assertEquals("No search attributes are provided.", exc.getCause().getMessage());
        }

    }

    @Test
    @SneakyThrows
    public void testLuceneSearch() throws ServiceException {
        List<FaoSpecies> species = new ArrayList<>(3);

        FaoSpecies species1 = new FaoSpecies();
        species1.setCode("COD");
        species1.setEnName("COD fish");

        FaoSpecies species2 = new FaoSpecies();
        species2.setCode("CAT");
        species2.setEnName("CAT fish");

        FaoSpecies species3 = new FaoSpecies();
        species3.setCode("WHL");
        species3.setEnName("Whale");

        species.add(species1);
        species.add(species2);
        species.add(species3);

        bulkDao.singleEntityBulkDeleteAndInsert(species);

        List<FaoSpecies> filterredEntities = (List<FaoSpecies>) mdrRepoBean.findCodeListItemsByAcronymAndFilter(species1.getAcronym(),
                0, 5, "code", true, "*", "code");

        assertEquals(3, filterredEntities.size());
        assertEquals("WHL", filterredEntities.get(0).getCode());
        assertEquals("COD", filterredEntities.get(1).getCode());
        assertEquals("CAT", filterredEntities.get(2).getCode());
    }

    @Test
    @SneakyThrows
    public void testLuceneSearchCount() throws ServiceException {
        List<FaoSpecies> species = new ArrayList<>(3);

        FaoSpecies species1 = new FaoSpecies();
        species1.setCode("COD");
        species1.setEnName("COD fish");

        FaoSpecies species2 = new FaoSpecies();
        species2.setCode("CAT");
        species2.setEnName("CAT fish");

        FaoSpecies species3 = new FaoSpecies();
        species3.setCode("WHL");
        species3.setEnName("Whale");

        species.add(species1);
        species.add(species2);
        species.add(species3);

        bulkDao.singleEntityBulkDeleteAndInsert(species);

        int totalCount=  mdrRepoBean.countCodeListItemsByAcronymAndFilter(species1.getAcronym(), "C*", "code");

        assertEquals(2, totalCount);
    }
}
