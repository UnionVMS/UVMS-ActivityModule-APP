/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.dao;

import static org.junit.Assert.assertNotNull;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import eu.europa.ec.fisheries.ers.fa.dao.VesselTransportMeansDao;
import eu.europa.ec.fisheries.ers.fa.entities.BaseErsFaDaoTest;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import org.junit.Before;
import org.junit.Test;

public class VesselTransportMeansDaoTest extends BaseErsFaDaoTest {

    private VesselTransportMeansDao dao = new VesselTransportMeansDao(em);

    @Before
    public void prepare() {
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), ERS_REFERENCE_DATA);
        dbSetupTracker.launchIfNecessary(dbSetup);
    }

    @Test
    public void testFindLatestVesselByTripId() throws Exception {

        VesselTransportMeansEntity latestVesselByTripId = dao.findLatestVesselByTripId("1");

        assertNotNull(latestVesselByTripId);
    }
}
