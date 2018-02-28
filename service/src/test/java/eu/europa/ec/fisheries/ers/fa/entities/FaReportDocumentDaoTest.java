/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

public class FaReportDocumentDaoTest extends BaseErsFaDaoTest {

    private FaReportDocumentDao dao = new FaReportDocumentDao(em);

    @Before
    public void prepare() {
        super.prepare();
    }

    @Test
    @SneakyThrows
    public void testFindEntityById() throws Exception {
        dbSetupTracker.skipNextLaunch();
        FaReportDocumentEntity entity = dao.findEntityById(FaReportDocumentEntity.class, 1);
        assertNotNull(entity);
    }

    @Test
    @SneakyThrows
    public void testFindFaReportById() throws Exception {
        dbSetupTracker.skipNextLaunch();
        FaReportDocumentEntity entity = dao.findEntityById(FaReportDocumentEntity.class, 1);
        String identifier = entity.getFluxReportDocument().getFluxReportIdentifiers().iterator().next().getFluxReportIdentifierId();
        String schemeId = entity.getFluxReportDocument().getFluxReportIdentifiers().iterator().next().getFluxReportIdentifierSchemeId();
        FaReportDocumentEntity faReportDocumentEntity = dao.findFaReportByIdAndScheme(identifier, schemeId);
        assertNotNull(faReportDocumentEntity);
        assertEquals(entity.getTypeCode(), faReportDocumentEntity.getTypeCode());
        assertEquals(entity.getTypeCodeListId(), faReportDocumentEntity.getTypeCodeListId());
        assertEquals(entity.getFmcMarker(), faReportDocumentEntity.getFmcMarker());
        assertEquals(entity.getFmcMarkerListId(), faReportDocumentEntity.getFmcMarkerListId());
    }

    @Test
    @SneakyThrows
    public void testGetLatestFaReportDocumentsForTrip() throws Exception {
        dbSetupTracker.skipNextLaunch();
        List<FaReportDocumentEntity> entities=dao.loadReports("NOR-TRP-20160517234053706", "Y");
        assertNotNull(entities);
        assertEquals(2, entities.size());
    }

    @Test
    @SneakyThrows
    public void testReturnNullIfEmpty() throws Exception {
        dbSetupTracker.skipNextLaunch();
        FaReportDocumentEntity faReportDocEntity = dao.findFaReportByIdAndScheme("TEST_NON_EXISTANT-REP-ID", "TEST_NON_EXISTANT-SCH-ID");
        assertNull(faReportDocEntity);
    }

    @Test
    @SneakyThrows
    public void testGetFaReportDocumentsForTrip() throws Exception {
        dbSetupTracker.skipNextLaunch();
        List<FaReportDocumentEntity> faReportDocumentsForTrip = dao.loadReports("NOR-TRP-20160517234053706", "N");
        assertNotNull(faReportDocumentsForTrip);
        assertTrue(faReportDocumentsForTrip.size() > 0);
    }


}