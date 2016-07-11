/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.dao;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import eu.europa.ec.fisheries.mdr.domain.CrNafoStock;
import lombok.SneakyThrows;

public class CrNafoStockDaoTest extends BaseActivityDaoTest {

    private CrNafoStockDao dao = new CrNafoStockDao(em);

    @Before
    @SneakyThrows
    public void prepare(){
        Operation operation = sequenceOf(DELETE_ALL_MDR_CR_NAFO_STOCK, INSERT_MDR_CR_NAFO_STOCK_REFERENCE_DATA);
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), operation);
        dbSetupTracker.launchIfNecessary(dbSetup);
    }

    @Test
    @SneakyThrows
    public void testFindEntityById() throws Exception {

        dbSetupTracker.skipNextLaunch();

        CrNafoStock entity = dao.findEntityById(CrNafoStock.class, 1L);

        assertNotNull(entity);
        assertEquals("2014-12-12 00:00:00.0", entity.getAudit().getCreatedOn().toString());
        assertEquals(true, entity.getRefreshable().booleanValue());
        assertEquals("NAFO 3N, 3O = FAO 21.3.N + 21.3.O", entity.getAreaDescription());
        assertEquals("N3NO", entity.getAreaCode());
        assertEquals("ANG", entity.getSpeciesCode());
        assertEquals("Lophius americanus", entity.getSpeciesName());
    }

}
