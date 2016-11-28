package eu.europa.ec.fisheries.mdr.repository.bean;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import eu.europa.ec.fisheries.mdr.dao.BaseMdrDaoTest;
import eu.europa.ec.fisheries.mdr.dao.MdrBulkOperationsDao;
import eu.europa.ec.fisheries.mdr.domain.SpeciesISO3Codes;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.SneakyThrows;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.junit.Before;
import org.junit.Ignore;
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
@Ignore
public class MdrRepositoryBeanTest extends BaseMdrDaoTest {

    private MdrBulkOperationsDao bulkDao = new MdrBulkOperationsDao(em);

    private MdrRepositoryBean mdrRepoBean = new MdrRepositoryBean();

    @Before
    @SneakyThrows
    public void prepare() {
        Operation operation = sequenceOf(DELETE_ALL_MDR_SPEICES);
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), operation);
        dbSetupTracker.launchIfNecessary(dbSetup);

        //init the bean
        Whitebox.setInternalState(mdrRepoBean, "em", em);
        mdrRepoBean.init();
    }


    @Test
    public void testLuceneIndexingNoSearchFilters() throws ServiceException {
        List<SpeciesISO3Codes> species = new ArrayList<>(2);

        SpeciesISO3Codes species1 = new SpeciesISO3Codes();
        species1.setCode("COD");
        species1.setEnglishName("COD fish");

        SpeciesISO3Codes species2 = new SpeciesISO3Codes();
        species2.setCode("COD");
        species2.setEnglishName("COD fish");

        species.add(species1);
        species.add(species2);

        bulkDao.singleEntityBulkDeleteAndInsert(species);

        FullTextSession fullTextSession = Search.getFullTextSession((Session) em.getDelegate());
        Transaction tx = fullTextSession.beginTransaction();
        SpeciesISO3Codes speciesISO3Codes = (SpeciesISO3Codes) fullTextSession.load( SpeciesISO3Codes.class, 1L );

        fullTextSession.index(speciesISO3Codes);
        tx.commit(); //index only updated at commit time

        try {
            List<SpeciesISO3Codes> filterredEntities = (List<SpeciesISO3Codes>) mdrRepoBean.findCodeListItemsByAcronymAndFilter(species1.getAcronym(), 0, 5, "code", false, null, null);
            fail("ServiceException was expected but not thrown.");
        } catch (ServiceException exc) {
            assertTrue(exc.getCause() instanceof  IllegalArgumentException);
            assertEquals("No search attributes are provided.", exc.getCause().getMessage());
        }

    }

    @Test
    public void testLuceneSearch() throws ServiceException {
        List<SpeciesISO3Codes> species = new ArrayList<>(3);

        SpeciesISO3Codes species1 = new SpeciesISO3Codes();
        species1.setCode("COD");
        species1.setEnglishName("COD fish");

        SpeciesISO3Codes species2 = new SpeciesISO3Codes();
        species2.setCode("CAT");
        species2.setEnglishName("CAT fish");

        SpeciesISO3Codes species3 = new SpeciesISO3Codes();
        species3.setCode("WHL");
        species3.setEnglishName("Whale");

        species.add(species1);
        species.add(species2);
        species.add(species3);

        bulkDao.singleEntityBulkDeleteAndInsert(species);

        List<SpeciesISO3Codes> filterredEntities = (List<SpeciesISO3Codes>) mdrRepoBean.findCodeListItemsByAcronymAndFilter(species1.getAcronym(), 0, 5, "sort_code", true, "*", "sort_code");

        assertEquals(3, filterredEntities.size());

        assertEquals("WHL", filterredEntities.get(0).getCode());
        assertEquals("COD", filterredEntities.get(1).getCode());
        assertEquals("CAT", filterredEntities.get(2).getCode());
    }

    @Test
    public void testLuceneSearchCount() throws ServiceException {
        List<SpeciesISO3Codes> species = new ArrayList<>(3);

        SpeciesISO3Codes species1 = new SpeciesISO3Codes();
        species1.setCode("COD");
        species1.setEnglishName("COD fish");

        SpeciesISO3Codes species2 = new SpeciesISO3Codes();
        species2.setCode("CAT");
        species2.setEnglishName("CAT fish");

        SpeciesISO3Codes species3 = new SpeciesISO3Codes();
        species3.setCode("WHL");
        species3.setEnglishName("Whale");

        species.add(species1);
        species.add(species2);
        species.add(species3);

        bulkDao.singleEntityBulkDeleteAndInsert(species);

        int totalCount=  mdrRepoBean.countCodeListItemsByAcronymAndFilter(species1.getAcronym(), "C*", "sort_code");

        assertEquals(2, totalCount);
    }
}
