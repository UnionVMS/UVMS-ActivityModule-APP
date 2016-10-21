/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusEnum;
import eu.europa.ec.fisheries.ers.service.AssetModuleService;
import eu.europa.ec.fisheries.ers.service.MovementModuleService;
import eu.europa.ec.fisheries.ers.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import javax.persistence.EntityManager;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by padhyad on 8/3/2016.
 */
public class FluxMessageServiceBeanTest {

    @Mock
    EntityManager em;

    @Mock
    FaReportDocumentDao faReportDocumentDao;

    @Mock
    MovementModuleServiceBean movementModule;

    @Mock
    private AssetModuleService assetModule;

    @InjectMocks
    FluxMessageServiceBean fluxMessageService;

    List<FAReportDocument> faReportDocuments;

    @Captor
    ArgumentCaptor<List<FaReportDocumentEntity>> captor;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() {
        FAReportDocument faReportDocument2 = MapperUtil.getFaReportDocument();
        faReportDocument2.getRelatedFLUXReportDocument().setPurposeCode(MapperUtil.getCodeType("5", "4fyrh-58fj4-5jtu-58tjr"));
        IDType id = faReportDocument2.getRelatedFLUXReportDocument().getReferencedID();

        FAReportDocument faReportDocument1 = MapperUtil.getFaReportDocument();
        faReportDocument1.getRelatedFLUXReportDocument().setIDS(Arrays.asList(id));
        faReportDocument1.getRelatedFLUXReportDocument().setPurposeCode(MapperUtil.getCodeType("3", "4f5yrh-58f7j4-5j5tu-58tj7r"));

        faReportDocuments = Arrays.asList(faReportDocument1, faReportDocument2);
    }

    @Test
    @SneakyThrows
    public void testSaveFishingActivityReportDocuments() {

        //Mock the APIs
        Mockito.doNothing().when(faReportDocumentDao).bulkUploadFaData(Mockito.any(List.class));
        Mockito.doNothing().when(faReportDocumentDao).updateAllFaData(Mockito.any(List.class));
        Mockito.doReturn(getMockedAssets()).when(assetModule).getAssetGuids(Mockito.anyCollection());
        Mockito.doReturn(null).when(movementModule).getMovement(Mockito.anyList(), Mockito.any(Date.class), Mockito.any(Date.class));
        Mockito.doReturn(getMockedFishingActivityReportEntity()).when(faReportDocumentDao).findFaReportByIdAndScheme(Mockito.any(String.class), Mockito.any(String.class));

        // Trigger
        fluxMessageService.saveFishingActivityReportDocuments(faReportDocuments, FaReportSourceEnum.FLUX);

        //Verify
        Mockito.verify(faReportDocumentDao, Mockito.times(1)).bulkUploadFaData(Mockito.any(List.class));
        Mockito.verify(faReportDocumentDao, Mockito.times(2)).findFaReportByIdAndScheme(Mockito.any(String.class), Mockito.any(String.class));
        Mockito.verify(faReportDocumentDao, Mockito.times(1)).updateAllFaData(captor.capture());

        //Test
        List<FaReportDocumentEntity>  faReportDocumentEntities = captor.getValue();
        assertEquals(FaReportStatusEnum.getFaReportStatusEnum(Integer.parseInt(faReportDocuments.get(1).getRelatedFLUXReportDocument().getPurposeCode().getValue())).getStatus(),
                faReportDocumentEntities.get(0).getStatus());
    }

    private FaReportDocumentEntity getMockedFishingActivityReportEntity() {
        FAReportDocument faReportDocument = MapperUtil.getFaReportDocument();
        return FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, new FaReportDocumentEntity(), FaReportSourceEnum.MANUAL);
    }

    private List<String> getMockedAssets() {
        List<String> assets = new ArrayList<>();
        assets.add("ASSET1");
        assets.add("ASSET2");
        return assets;
    }
}
