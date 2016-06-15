package eu.europa.ec.fisheries.ers.fa.entities;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static junit.framework.TestCase.assertNotNull;

public class FaReportDocumentTest extends BaseErsFaDaoTest {
		
	private FaReportDocumentDao dao=new FaReportDocumentDao(em);
	
	@Before
    public void prepare(){
       Operation operation = sequenceOf(DELETE_ALL, INSERT_ERS_FLUX_REPORT_DOCUMENT_DATA,INSERT_ERS_VESSEL_TRANSPORT_MEANS_DATA,INSERT_ERS_FA_REPORT_DOCUMENT_DATA);
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), operation);
       dbSetupTracker.launchIfNecessary(dbSetup);
   }
	
	
	@Test
	@SneakyThrows
    public void testFindEntityById() throws Exception {

       dbSetupTracker.skipNextLaunch();
        FaReportDocumentEntity entity=dao.findEntityById(FaReportDocumentEntity.class, 1);
         assertNotNull(entity);
      
   }

}
