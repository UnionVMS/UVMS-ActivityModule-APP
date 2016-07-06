/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.dao;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import lombok.SneakyThrows;
import org.junit.Before;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;

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

  /*  @Test
    @SneakyThrows
    public void testFindActionTypeById(){

        dbSetupTracker.skipNextLaunch();

        ActionType entity = null;
        try {
            entity = dao.findEntityById(ActionType.class, 1L);
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        assertNotNull(entity);
        assertEquals("2014-12-12 00:00:00.0", entity.getAudit().getCreatedOn().toString());
        assertEquals(true, entity.getRefreshable().booleanValue());
//        assertEquals("NAFO 3N, 3O = FAO 21.3.N + 21.3.O", entity.getAreaDescription());
//        assertEquals("N3NO", entity.getAreaCode());
//        assertEquals("ANG", entity.getSpeciesCode());
//        assertEquals("Lophius americanus", entity.getSpeciesName());
    }*/
    
    /*  @Test
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
    }*/
    
}