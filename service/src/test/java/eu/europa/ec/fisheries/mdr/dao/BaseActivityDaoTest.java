package eu.europa.ec.fisheries.mdr.dao;

import com.ninja_squad.dbsetup.operation.Operation;
import eu.europa.ec.fisheries.uvms.BaseDAOTest;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;

public abstract class BaseActivityDaoTest extends BaseDAOTest {


    protected static final Operation DELETE_ALL = sequenceOf(
            deleteAllFrom("activity.mdr_cr_nafo_stock")
    );

    protected static final Operation INSERT_MDR_CR_NAFO_STOCK_REFERENCE_DATA = sequenceOf(
            insertInto("activity.mdr_cr_nafo_stock")
                    .columns("id", "created_on", "refreshable", "species_code", "species_name", "area_code", "area_description")
                    .values(1L, java.sql.Date.valueOf("2014-12-12"), "Y", "ANG", "Lophius americanus", "N3NO", "NAFO 3N, 3O = FAO 21.3.N + 21.3.O")
                    .build()
    );

    protected String getSchema() {
        return "activity";
    }

    protected String getPersistenceUnitName() {
        return "testPU";
    }
}
