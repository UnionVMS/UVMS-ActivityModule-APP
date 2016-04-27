package eu.europa.ec.fisheries.mdr.dao;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import eu.europa.ec.fisheries.mdr.domain.CrNafoStock;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class CrNafoStockDaoTest extends BaseActivityDaoTest {

    private CrNafoStockDao dao = new CrNafoStockDao(em);

    @Before
    @SneakyThrows
    public void prepare(){
        Operation operation = sequenceOf(DELETE_ALL, INSERT_MDR_CR_NAFO_STOCK_REFERENCE_DATA);
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), operation);
        dbSetupTracker.launchIfNecessary(dbSetup);
    }

    @Test
    @SneakyThrows
    public void testFindEntityById(){

        dbSetupTracker.skipNextLaunch();

        CrNafoStock entity = dao.findEntityById(CrNafoStock.class, 1L);

        assertNotNull(entity);
        assertEquals("2014-12-12 00:00:00.0", entity.getAudit().getCreatedOn().toString());
        assertEquals(true, entity.getRefreshable().booleanValue());
        assertEquals("NAFO 3N, 3O = FAO 21.3.N + 21.3.O", entity.getAreaDescription());
        assertEquals("N3NO", entity.getAreaCode());
        assertEquals("ANG", entity.getSpeciesCode());
        assertEquals("Lophius americanus", entity.getSpeciesName());
    }

}
