/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

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
    @Transactional
    public void testInsertTable() throws Exception {
        dbSetupTracker.skipNextLaunch();
        List<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>();
        FAReportDocument faReportDocument = MapperUtil.getFaReportDocument();
        FaReportDocumentEntity faReportDocumentEntity = FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, new FaReportDocumentEntity(), FaReportSourceEnum.FLUX);
        faReportDocumentEntities.add(faReportDocumentEntity);

        insertIntoDB(faReportDocumentEntities);
        List<FaReportDocumentEntity> entityList = dao.findAllEntity(FaReportDocumentEntity.class);

        assertNotNull(entityList);
        assertNotEquals(0, entityList.size());
    }

    @Transactional
    private void insertIntoDB(List<FaReportDocumentEntity> faReportDocumentEntities) {
        dao.bulkUploadFaData(faReportDocumentEntities);
    }

    @Test
    @SneakyThrows
    public void testUpdateTable() throws Exception {
        dbSetupTracker.skipNextLaunch();
        List<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>();
        FAReportDocument faReportDocument = MapperUtil.getFaReportDocument();
        FaReportDocumentEntity faReportDocumentEntity = FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, new FaReportDocumentEntity(), FaReportSourceEnum.FLUX);
        faReportDocumentEntities.add(faReportDocumentEntity);

        dao.bulkUploadFaData(faReportDocumentEntities);
        FaReportDocumentEntity entity = dao.findEntityById(FaReportDocumentEntity.class, 1);

        entity.setStatus("cancel");
        entity.setTypeCode("Updated Type Code 1");
        entity.setTypeCodeListId("gjtu67-fd484jf8-5ej8f58e-n58dj48f");
        entity.setSource(FaReportSourceEnum.MANUAL.getSourceType());
        dao.updateAllFaData(Arrays.asList(entity));

        FaReportDocumentEntity updatedEntity = dao.findEntityById(FaReportDocumentEntity.class, 1);
        assertEquals("cancel", updatedEntity.getStatus());
        assertEquals("Updated Type Code 1", updatedEntity.getTypeCode());
        assertEquals("gjtu67-fd484jf8-5ej8f58e-n58dj48f", updatedEntity.getTypeCodeListId());
        assertEquals(FaReportSourceEnum.MANUAL.getSourceType(), updatedEntity.getSource());

    }


    @Test
    @SneakyThrows
    public void testGetLatestFaReportDocumentsForTrip() throws Exception {
        dbSetupTracker.skipNextLaunch();
        List<FaReportDocumentEntity> entities=dao.getLatestFaReportDocumentsForTrip("NOR-TRP-20160517234053706");
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
        List<FaReportDocumentEntity> faReportDocumentsForTrip = dao.getFaReportDocumentsForTrip("NOR-TRP-20160517234053706");
        assertNotNull(faReportDocumentsForTrip);
        assertTrue(faReportDocumentsForTrip.size() > 0);
    }


}