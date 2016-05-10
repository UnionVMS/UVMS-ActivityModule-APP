package eu.europa.ec.fisheries.ers.fa.entities;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static junit.framework.TestCase.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;


import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivity;
import lombok.SneakyThrows;

public class FishingActivityDaoTest extends BaseErsFaDaoTest{

private FishingActivityDao dao=new FishingActivityDao(em);

 @Before   
  public void prepare(){
      Operation operation = sequenceOf(DELETE_ALL, INSERT_ERS_FISHING_ACTIVITY_DATA);
      DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), operation);
      dbSetupTracker.launchIfNecessary(dbSetup);
  }
	
	
	@Test
    @SneakyThrows
   public void testFindEntityById(){

      dbSetupTracker.skipNextLaunch();
      FishingActivity entity=dao.findEntityById(FishingActivity.class, 1);
      assertNotNull(entity);
      
  }
}
