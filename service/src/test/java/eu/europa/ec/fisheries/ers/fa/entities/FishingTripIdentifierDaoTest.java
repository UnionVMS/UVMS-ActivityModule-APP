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

import eu.europa.ec.fisheries.ers.fa.dao.FishingTripIdentifierDao;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

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
        FishingTripIdentifierEntity fishingTrip = dao.getCurrentTrip("CFR123", "CFR");
        assertNotNull(fishingTrip);
        assertEquals(fishingTrip.getTripId(), "NOR-TRP-20160517234053706");
        assertEquals(fishingTrip.getTripSchemeId(), "EU_TRIP_ID");
    }

    @Test
    @SneakyThrows
    public void testGetPreviousTrips() throws Exception {
        dbSetupTracker.skipNextLaunch();
        List<FishingTripIdentifierEntity> previousTrips = dao.getPreviousTrips("CFR123", "CFR", "NOR-TRP-20160517234053705", 5);
        assertNotNull(previousTrips);
        assertNotEquals(0, previousTrips.size());
    }

    @Test
    @SneakyThrows
    public void testGetNextTrips() throws Exception {
        dbSetupTracker.skipNextLaunch();
        List<FishingTripIdentifierEntity> nextTrips = dao.getNextTrips("EXT_MARK123", "EXT_MARK", "NOR-TRP-20160517234053705", 5);
        assertNotNull(nextTrips);
        assertNotEquals(0, nextTrips.size());
    }

    @Test
    @SneakyThrows
    public void testGetCurrentTripNullReturn() throws Exception {
        dbSetupTracker.skipNextLaunch();
        FishingTripIdentifierEntity fishingTrip = dao.getCurrentTrip("SOME-NON-EXISTENT-VAL", "SOME-NON-EXISTENT-VAL");
        assertNull(fishingTrip);
    }
}
