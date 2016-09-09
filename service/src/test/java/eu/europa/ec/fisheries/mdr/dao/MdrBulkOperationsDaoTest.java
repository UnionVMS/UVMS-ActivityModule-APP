/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

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
import eu.europa.ec.fisheries.mdr.domain.ActionType;
import eu.europa.ec.fisheries.mdr.domain.CrNafoStock;
import eu.europa.ec.fisheries.mdr.domain.MasterDataRegistry;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.junit.Assert.assertEquals;


public class MdrBulkOperationsDaoTest extends BaseActivityDaoTest {

	private MdrBulkOperationsDao bulkDao = new MdrBulkOperationsDao(em);

	@Before
	@SneakyThrows
	public void prepare() {
		Operation operation = sequenceOf(DELETE_ALL_MDR_CR_NAFO_STOCK, DELETE_ALL_ACTION_TYPE, INSERT_MDR_ACTION_TYPE, INSERT_MDR_CR_NAFO_STOCK_REFERENCE_DATA);
		DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), operation);
		dbSetupTracker.launchIfNecessary(dbSetup);
	}

	@Test
	@SneakyThrows
	public void testBulkDeletionAndInsertion() {

		dbSetupTracker.skipNextLaunch();

		// List of Entity rows (List of instances of an entity) == List of
		// Lists;
		List<List<? extends MasterDataRegistry>> entitiesList = new ArrayList<List<? extends MasterDataRegistry>>();
		
		// CrNafoStocks list
		List<CrNafoStock> crNafoStockEntityRows = mockNafoStocks();
		
		// ActionType list
		List<ActionType> actionEntityRows = mockActionType();
		
		// Adding the Entities rows to the entitiesList
		entitiesList.addAll(Arrays.asList(crNafoStockEntityRows,actionEntityRows));

		// BulkInsertion of 2 different entities
		em.getTransaction().begin();
		bulkDao.multiEntityBulkDeleteAndInsert(entitiesList);

		em.flush();
		em.getTransaction().commit();

		assertEquals(11, bulkDao.getEntityManager().createQuery("FROM " + CrNafoStock.class.getSimpleName(), CrNafoStock.class).getResultList().size());
		assertEquals(11, bulkDao.getEntityManager().createQuery("FROM " + ActionType.class.getSimpleName(), ActionType.class).getResultList().size());

		
	}

	private List<CrNafoStock> mockNafoStocks() {
		List<CrNafoStock> crNafoStockEntityRows = new ArrayList<CrNafoStock>();
		// Creating new CrNafoStocs entity to persist and adding it to this entity list (rows);
		for(int i = 0; i < 11; i++){
			CrNafoStock crNafoStockToPersist = new CrNafoStock();
			crNafoStockToPersist.setAreaCode("areaCode"+i);
			crNafoStockToPersist.setAreaDescription("someDescription"+i);
			crNafoStockToPersist.setRefreshable(true);
			crNafoStockToPersist.setSpeciesCode("44"+i);
			crNafoStockToPersist.setSpeciesName("StrangeName"+i);
			crNafoStockEntityRows.add(crNafoStockToPersist);
		}
		return crNafoStockEntityRows;
	}
	
	private List<ActionType> mockActionType() {
		List<ActionType> actionTypeRows = new ArrayList<ActionType>();
		// Creating new CrNafoStocs entity to persist and adding it to this entity list (rows);
		for(int i = 0; i < 11; i++){			
			ActionType actionType = new ActionType();
			actionType.setCode("areaCode"+i);
			actionType.setDescription("someDescription"+i);
			actionType.setRefreshable(true);
			actionTypeRows.add(actionType);	
		}	
		return actionTypeRows;
	}

}