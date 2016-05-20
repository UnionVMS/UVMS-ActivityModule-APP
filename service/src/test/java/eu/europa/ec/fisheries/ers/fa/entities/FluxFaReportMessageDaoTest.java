package eu.europa.ec.fisheries.ers.fa.entities;


import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import eu.europa.ec.fisheries.ers.fa.dao.FluxFaReportMessageDao;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static junit.framework.TestCase.assertNotNull;

public class FluxFaReportMessageDaoTest extends BaseErsFaDaoTest{
	
	private FluxFaReportMessageDao dao=new FluxFaReportMessageDao(em);
	
	@Before
    @SneakyThrows
    public void prepare(){
       Operation operation = sequenceOf(DELETE_ALL, INSERT_ERS_FLUX_FA_REPORT_MESSAGE_DATA);
       DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), operation);
       dbSetupTracker.launchIfNecessary(dbSetup);
   }
	
	
	@Test
    @SneakyThrows
    public void testFindEntityById() throws Exception {

       dbSetupTracker.skipNextLaunch();
          FluxFaReportMessageEntity entity=dao.findEntityById(FluxFaReportMessageEntity.class, 1);
         assertNotNull(entity);     
   }

}
