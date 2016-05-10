package eu.europa.ec.fisheries.ers.fa.entities;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static junit.framework.TestCase.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import eu.europa.ec.fisheries.ers.fa.dao.FaCatchDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatch;

//import eu.europa.ec.fisheries.ers.fa.dao.FaCatchDao;

import lombok.SneakyThrows;

public class FaCatchDaoTest extends BaseErsFaDaoTest{
	
	
	private FaCatchDao dao = new FaCatchDao(em);


	
		@Before
	    @SneakyThrows
	    public void prepare(){
				Operation operation = sequenceOf(DELETE_ALL, INSERT_ERS_SIZE_DISTRIBUTION_DATA,INSERT_ERS_FA_CATCH_DATA);
		        DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), operation);
		        dbSetupTracker.launchIfNecessary(dbSetup);
	    }
		
		
		@Test
	    @SneakyThrows
	    public void testFindEntityById(){

		       dbSetupTracker.skipNextLaunch();
		       FaCatch entity=dao.findEntityById(FaCatch.class, 1);
		      assertNotNull(entity);
	    }
	    
	   

}
