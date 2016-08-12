/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import junit.framework.Assert;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import un.unece.uncefact.data.standard.fluxfareportmessage._1.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

@Ignore
public class FaReportDocumentDaoTest extends BaseErsFaDaoTest {
		
	private FaReportDocumentDao dao=new FaReportDocumentDao(em);
	
	@Before
    public void prepare(){
       Operation operation = sequenceOf(DELETE_ALL, INSERT_ERS_FLUX_REPORT_DOCUMENT_DATA,INSERT_ERS_VESSEL_TRANSPORT_MEANS_DATA,INSERT_ERS_FA_REPORT_DOCUMENT_DATA,INSERT_ERS_FA_REPORT_IDENTIFIER_DATA);
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), operation);
       dbSetupTracker.launchIfNecessary(dbSetup);
   }
	
	
	@Test
	@SneakyThrows
    public void testFindEntityById() throws Exception {
        dbSetupTracker.skipNextLaunch();
        FaReportDocumentEntity entity=dao.findEntityById(FaReportDocumentEntity.class, 1);
        assertNotNull(entity);
    }

    @Test
    @SneakyThrows
    public void testFindEntityByReferenceId() throws Exception {
        dbSetupTracker.skipNextLaunch();
        List<FaReportDocumentEntity> faReportDocumentEntities = dao.findFaReportsByReferenceId("ID 4");
        assertNotNull(faReportDocumentEntities);
        assertNotNull(faReportDocumentEntities.get(0));
    }

    @Test
    @SneakyThrows
    public void testFindFaReportById() throws Exception {
        dbSetupTracker.skipNextLaunch();
        FaReportDocumentEntity entity = dao.findEntityById(FaReportDocumentEntity.class, 1);
        String identifier = entity.getFaReportIdentifiers().iterator().next().getFaReportIdentifierId();

        List<FaReportDocumentEntity> entities = dao.findFaReportsByIds(new HashSet<String>(Arrays.asList(identifier)));
        assertNotNull(entities);
        assertEquals(entity.getTypeCode(), entities.get(0).getTypeCode());
        assertEquals(entity.getTypeCodeListId(), entities.get(0).getTypeCodeListId());
        assertEquals(entity.getFmcMarker(), entities.get(0).getFmcMarker());
        assertEquals(entity.getFmcMarkerListId(), entities.get(0).getFmcMarkerListId());
    }


    @Test
    @SneakyThrows
    public void testInsertTable() throws Exception {
        dbSetupTracker.skipNextLaunch();
        List<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>();
        FAReportDocument faReportDocument = MapperUtil.getFaReportDocument();
        FaReportDocumentEntity faReportDocumentEntity = FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, new FaReportDocumentEntity());
        faReportDocumentEntities.add(faReportDocumentEntity);

        dao.bulkUploadFaData(faReportDocumentEntities);
        FaReportDocumentEntity entity = dao.findEntityById(FaReportDocumentEntity.class, 1);

        assertNotNull(entity);
        assertEquals(faReportDocument.getTypeCode().getValue(), entity.getTypeCode());
        assertEquals(faReportDocument.getTypeCode().getListID(), entity.getTypeCodeListId());
        assertEquals(faReportDocument.getAcceptanceDateTime().getDateTime().toGregorianCalendar().getTime(), entity.getAcceptedDatetime());
        assertEquals(faReportDocument.getFMCMarkerCode().getValue(), entity.getFmcMarker());
        assertEquals(faReportDocument.getFMCMarkerCode().getListID(), entity.getFmcMarkerListId());
    }

    @Test
    @SneakyThrows
    public void testUpdateTable() throws Exception {
        dbSetupTracker.skipNextLaunch();
        List<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>();
        FAReportDocument faReportDocument = MapperUtil.getFaReportDocument();
        FaReportDocumentEntity faReportDocumentEntity = FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, new FaReportDocumentEntity());
        faReportDocumentEntities.add(faReportDocumentEntity);

        dao.bulkUploadFaData(faReportDocumentEntities);
        FaReportDocumentEntity entity = dao.findEntityById(FaReportDocumentEntity.class, 1);

        entity.setStatus("cancel");
        entity.setTypeCode("Updated Type Code 1");
        entity.setTypeCodeListId("gjtu67-fd484jf8-5ej8f58e-n58dj48f");
        dao.updateAllFaData(Arrays.asList(entity));

        FaReportDocumentEntity updatedEntity = dao.findEntityById(FaReportDocumentEntity.class, 1);
        assertEquals("cancel", updatedEntity.getStatus());
        assertEquals("Updated Type Code 1", updatedEntity.getTypeCode());
        assertEquals("gjtu67-fd484jf8-5ej8f58e-n58dj48f", updatedEntity.getTypeCodeListId());

    }

}