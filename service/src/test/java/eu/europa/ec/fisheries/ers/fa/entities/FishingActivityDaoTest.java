package eu.europa.ec.fisheries.ers.fa.entities;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.service.search.*;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static junit.framework.TestCase.assertNotNull;

@Ignore
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
   public void testFindEntityById() throws Exception {

      dbSetupTracker.skipNextLaunch();
      FishingActivityEntity entity=dao.findEntityById(FishingActivityEntity.class, 1);
      assertNotNull(entity);

  }

    @Test
    @SneakyThrows
    public void testSearchEntityByQuery() throws Exception {

        dbSetupTracker.skipNextLaunch();
        FishingActivityQuery query = new FishingActivityQuery();
        ListCriteria listCriteria = new ListCriteria();
        listCriteria.setKey(SearchKey.typeCode);
        listCriteria.setValue("FISHING_OPERATION");

        ListCriteria listCriteria1 = new ListCriteria();
        listCriteria1.setKey(SearchKey.reasonCodeListId);
        listCriteria1.setValue("LAN");

        List<ListCriteria> searchCriteria = new ArrayList<ListCriteria>();
        searchCriteria.add(listCriteria);
        query.setSearchCriteria(searchCriteria);

        query.setSortKey(new SortKey(SearchKey.occurence, SortOrder.ASC));

        query.setPagination( new Pagination(1,2));
        List<FishingActivityEntity> finishingActivityList=  dao.getFishingActivityListByQuery(query);

        assertNotNull(finishingActivityList);

    }

}
