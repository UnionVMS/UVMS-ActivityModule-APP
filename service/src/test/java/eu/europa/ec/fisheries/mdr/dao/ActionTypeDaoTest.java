package eu.europa.ec.fisheries.mdr.dao;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import eu.europa.ec.fisheries.mdr.domain.ActionType;
import eu.europa.ec.fisheries.mdr.domain.CodeLocation;
import eu.europa.ec.fisheries.mdr.domain.MasterDataRegistry;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.SneakyThrows;

public class ActionTypeDaoTest extends BaseActivityDaoTest {
	
    private ActionTypeDao dao;

    @Before
    @SneakyThrows
    public void prepare(){
    	dao = new ActionTypeDao(em);
        Operation operation = sequenceOf(DELETE_ALL_ACTION_TYPE, INSERT_MDR_ACTION_TYPE);
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), operation);
        dbSetupTracker.launchIfNecessary(dbSetup);
    }

    @Test
    @SneakyThrows
    public void testFindActionTypeById(){

        dbSetupTracker.skipNextLaunch();

        ActionType entity =  dao.findEntityById(ActionType.class, 1L);

        assertNotNull(entity);
        assertEquals("2014-12-12 00:00:00.0", entity.getAudit().getCreatedOn().toString());
        assertEquals(true, entity.getRefreshable().booleanValue());
//        assertEquals("NAFO 3N, 3O = FAO 21.3.N + 21.3.O", entity.getAreaDescription());
//        assertEquals("N3NO", entity.getAreaCode());
//        assertEquals("ANG", entity.getSpeciesCode());
//        assertEquals("Lophius americanus", entity.getSpeciesName());
    }
    
    @Test
    @SneakyThrows
    public void persistMasterDataRows(List<? extends MasterDataRegistry> mDataRegistryList){
    	
    	// Entity 1 rows
    	ActionType actType           = new ActionType();
    	actType.setCode("222"); actType.setDescription("Some description");
    	ActionType actType2          = new ActionType();
    	actType2.setCode("333"); actType2.setDescription("Some description 2");
    	List<ActionType> actTypeList = new ArrayList<ActionType>();
    	actTypeList.addAll(Arrays.asList(actType, actType2));
    	
    	// Entity 2 rows
    	CodeLocation codelocType = new CodeLocation();
    	CodeLocation codelocType2 = new CodeLocation();
    	List<CodeLocation> codelocTypeList = new ArrayList<CodeLocation>();
    	codelocTypeList.addAll(Arrays.asList(codelocType, codelocType2));
    	
    	// List of all entities rows
    	List<List<? extends MasterDataRegistry>> masterDataGenericList = new ArrayList<List<? extends MasterDataRegistry>>();
    	masterDataGenericList.addAll(Arrays.asList(actTypeList, codelocTypeList));
    	
//    	try {
//    		dao.multiEntityBulkDeleteAndInsert(masterDataGenericList);
//		} catch (ServiceException e) {
//			e.printStackTrace();
//		}
    }
    
}
