/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.fa.entities;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripIdentifierDao;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Created by sanera on 06/09/2016.
 */
public class FishingTripIdentifierDaoTest extends BaseErsFaDaoTest {

    private FishingTripIdentifierDao dao = new FishingTripIdentifierDao(em);

    @Before
    public void prepare() {
        super.prepare();
    }

    @Test
    @SneakyThrows
    public void testGetCurrentTrip() throws Exception {

        dbSetupTracker.skipNextLaunch();
        String tripId = dao.getCurrentTrip();
        assertNotNull(tripId);
        assertEquals("NOR-TRP-20160517234053706", tripId);
    }

    @Test
    @SneakyThrows
    public void testGetFishingTripsBefore() throws Exception {

        dbSetupTracker.skipNextLaunch();
        List<Object[]> objectList = dao.getFishingTripsBefore("NOR-TRP-20160517234053706", 1);
        assertNotNull(objectList);
        assertEquals(1, objectList.size());
    }

    @Test
    @SneakyThrows
    public void testGetFishingTripsAfter() throws Exception {

        dbSetupTracker.skipNextLaunch();
        List<Object[]> objectList = dao.getFishingTripsAfter("NOR-TRP-20160517234053706", 1);
        assertNotNull(objectList);
        assertEquals(1, objectList.size());
    }
}
