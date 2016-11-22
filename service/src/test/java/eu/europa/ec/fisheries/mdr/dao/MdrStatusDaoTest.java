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
import eu.europa.ec.fisheries.mdr.domain.AcronymVersion;
import eu.europa.ec.fisheries.mdr.domain.MdrCodeListStatus;
import eu.europa.ec.fisheries.mdr.domain.constants.AcronymListState;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class MdrStatusDaoTest extends BaseMdrDaoTest {

    private MdrStatusDao dao = new MdrStatusDao(em);

    @Before
    @SneakyThrows
    public void prepare(){
        Operation operation = sequenceOf(INSERT_MDR_CODELIST_STATUS, INSERT_ACRONYMS_VERSIONS);
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), operation);
        dbSetupTracker.launchIfNecessary(dbSetup);
    }

    @Test
    @SneakyThrows
    public void testGellAllAcronymStatuses() throws Exception {
        dbSetupTracker.skipNextLaunch();
        List<MdrCodeListStatus> allAcronymsStatuses = dao.getAllAcronymsStatuses();
        MdrCodeListStatus entity = allAcronymsStatuses.get(0);
        assertNotNull(entity);
    }

    @Test
    @SneakyThrows
    public void testFindStatusByAcronym(){
        dbSetupTracker.skipNextLaunch();
        MdrCodeListStatus status = dao.findStatusByAcronym("ACTION_TYPE");
        assertNotNull(status);
    }


    @Test
    @SneakyThrows
    public void testFindStatusAndRelatedVersionsByAcronym(){
        dbSetupTracker.skipNextLaunch();
        List<MdrCodeListStatus> actiontypeStatus = dao.findStatusAndVersionsForAcronym("ACTION_TYPE");
        Set<AcronymVersion> actiontypeVersions = actiontypeStatus.get(0).getVersions();
        assertEquals(2, actiontypeVersions.size());
        assertNotNull(actiontypeStatus.get(0));
    }

    @Test
    @SneakyThrows
    public void testFindAllUpdatableStatuses(){
        dbSetupTracker.skipNextLaunch();
        List<MdrCodeListStatus> status = dao.findAllUpdatableStatuses();
        assertEquals(2, status.size());
    }

    @Test
    @SneakyThrows
    public void testUpdateSchedulableForAcronym(){
        dbSetupTracker.skipNextLaunch();
        MdrCodeListStatus status = dao.findStatusByAcronym("ACTION_TYPE");
        assertNotNull(status);

        dao.updateSchedulableForAcronym("ACTION_TYPE", false);
        MdrCodeListStatus status_1 = dao.findStatusByAcronym("ACTION_TYPE");
        assertEquals(status_1.getSchedulable().toString(), "false");

        dao.updateSchedulableForAcronym("ACTION_TYPE", true);
        MdrCodeListStatus status_2 = dao.findStatusByAcronym("ACTION_TYPE");
        assertEquals(status_2.getSchedulable().toString(), "true");
    }

    @Test
    @SneakyThrows
    public void testUpdateStatusAttemptForAcronym(){

        dbSetupTracker.skipNextLaunch();
        MdrCodeListStatus status = dao.findStatusByAcronym("ACTION_TYPE");
        assertNotNull(status);

        dao.updateStatusAttemptForAcronym("ACTION_TYPE", AcronymListState.NEWENTRY, new Date());
        MdrCodeListStatus status_1 = dao.findStatusByAcronym("ACTION_TYPE");
        assertEquals(status_1.getLastStatus(), AcronymListState.NEWENTRY);

        dao.updateStatusAttemptForAcronym("ACTION_TYPE", AcronymListState.FAILED, new Date());
        MdrCodeListStatus status_2 = dao.findStatusByAcronym("ACTION_TYPE");
        assertEquals(status_2.getLastStatus(), AcronymListState.FAILED);
    }

    @Test
    @SneakyThrows
    public void testUpdateStatusFailedForAcronym(){

        dbSetupTracker.skipNextLaunch();
        MdrCodeListStatus status = dao.findStatusByAcronym("ACTION_TYPE");
        assertNotNull(status);

        dao.updateStatusFailedForAcronym("ACTION_TYPE");
        MdrCodeListStatus status_2 = dao.findStatusByAcronym("ACTION_TYPE");
        assertEquals(status_2.getLastStatus(), AcronymListState.FAILED);

    }

}
