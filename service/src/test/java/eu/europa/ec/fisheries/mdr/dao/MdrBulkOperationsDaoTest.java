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
import eu.europa.ec.fisheries.mdr.domain2.ActionType;
import eu.europa.ec.fisheries.mdr.domain2.SpeciesISO3Codes;
import eu.europa.ec.fisheries.mdr.domain.codelists.base.MasterDataRegistry;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.junit.Assert.assertEquals;


public class MdrBulkOperationsDaoTest extends BaseMdrDaoTest {

	private MdrBulkOperationsDao bulkDao = new MdrBulkOperationsDao(em);

	private MdrStatusDao statusDao = new MdrStatusDao(em);

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
		
		// ActionType list
		List<SpeciesISO3Codes> actionEntityRows = mockSpecies();

		// BulkInsertion of 2 different entities
		em.getTransaction().begin();
		bulkDao.singleEntityBulkDeleteAndInsert(actionEntityRows);

		em.flush();
		em.getTransaction().commit();

		assertEquals(11, bulkDao.getEntityManager().createQuery("FROM " + SpeciesISO3Codes.class.getSimpleName(), SpeciesISO3Codes.class).getResultList().size());

		
	}

	private List<SpeciesISO3Codes> mockSpecies() {
		List<SpeciesISO3Codes> list = new ArrayList<>();
		// Creating new CrNafoStocs entity to persist and adding it to this entity list (rows);
		for(int i = 0; i < 11; i++){
			SpeciesISO3Codes species = new SpeciesISO3Codes();
			species.setCode("areaCode"+i);
			species.setEnglishName("someDescription"+i);
			species.setCode("44"+i);
			species.setScientificName("StrangeName"+i);
			list.add(species);
		}
		return list;
	}
	
	private List<ActionType> mockActionType() {
		List<ActionType> actionTypeRows = new ArrayList<ActionType>();
		// Creating new CrNafoStocs entity to persist and adding it to this entity list (rows);
		for(int i = 0; i < 11; i++){			
			ActionType actionType = new ActionType();
			actionType.setCode("areaCode"+i);
			actionType.setDescription("someDescription"+i);
			actionTypeRows.add(actionType);	
		}	
		return actionTypeRows;
	}

}