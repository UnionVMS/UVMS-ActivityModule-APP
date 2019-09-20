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

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.dao.FluxFaReportMessageDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.ers.service.AssetModuleService;
import eu.europa.ec.fisheries.ers.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.ers.service.util.Postgres;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;
import static org.junit.Assert.assertEquals;

@Ignore
public class FluxMessageServiceBeanTest {

    @Mock
    private FaReportDocumentDao faReportDocumentDao;

    @Mock
    private FluxFaReportMessageDao fluxFaReportMessageDao;

    @Mock
    private MovementModuleServiceBean movementModule;

    @Mock
    private AssetModuleService assetModule;

    @InjectMocks
    private FluxMessageServiceBean fluxMessageService;

    private List<FAReportDocument> faReportDocuments;

    private FLUXFAReportMessage fluxFaReportMessage;

    @Captor
    ArgumentCaptor<List<FaReportDocumentEntity>> captor;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() {
        FAReportDocument faReportDocument2 = MapperUtil.getFaReportDocument();
        faReportDocument2.getRelatedFLUXReportDocument().setPurposeCode(MapperUtil.getCodeType("9", "4fyrh-58fj4-5jtu-58tjr"));
        IDType id = faReportDocument2.getRelatedFLUXReportDocument().getReferencedID();

        FAReportDocument faReportDocument1 = MapperUtil.getFaReportDocument();
        faReportDocument1.getRelatedFLUXReportDocument().setIDS(Arrays.asList(id));
        faReportDocument1.getRelatedFLUXReportDocument().setPurposeCode(MapperUtil.getCodeType("3", "4f5yrh-58f7j4-5j5tu-58tj7r"));

        fluxFaReportMessage = new FLUXFAReportMessage();
        faReportDocuments = Arrays.asList(faReportDocument1, faReportDocument2);
        fluxFaReportMessage.setFAReportDocuments(faReportDocuments);
        fluxFaReportMessage.setFLUXReportDocument(MapperUtil.getFluxReportDocument());
    }

    @Test
    @SneakyThrows
    public void testSaveFishingActivityReportDocuments() throws ServiceException, ParseException, DatatypeConfigurationException {

        //Mock the APIs
        Mockito.doReturn(Mockito.anyList()).when(fluxFaReportMessageDao).createEntity(Mockito.any(FluxFaReportMessageEntity.class));

        Mockito.doReturn(getMockedAssets()).when(assetModule).getAssetGuids(Mockito.anyCollection());
        Mockito.doReturn(getMockedMovements()).when(movementModule).getMovement(Mockito.anyList(), Mockito.any(Instant.class), Mockito.any(Instant.class));
        Mockito.doReturn(getMockedFishingActivityReportEntity()).when(faReportDocumentDao).findFaReportByIdAndScheme(Mockito.any(String.class), Mockito.any(String.class));

        // Trigger
        fluxMessageService.setDialect(new Postgres());
        fluxMessageService.saveFishingActivityReportDocuments(fluxFaReportMessage, FaReportSourceEnum.FLUX);

        //Verify
       // Mockito.verify(fluxFaReportMessageDao, Mockito.times(1)).saveFluxFaReportMessage(Mockito.any(FluxFaReportMessageEntity.class));
        Mockito.verify(faReportDocumentDao, Mockito.times(2)).findFaReportByIdAndScheme(Mockito.any(String.class), Mockito.any(String.class));

        //Test
        List<FaReportDocumentEntity>  faReportDocumentEntities = captor.getValue();
        assertEquals(FaReportStatusType.getFaReportStatusEnum(Integer.parseInt(faReportDocuments.get(1).getRelatedFLUXReportDocument().getPurposeCode().getValue())).name(),
                faReportDocumentEntities.get(0).getStatus());
    }

    private FaReportDocumentEntity getMockedFishingActivityReportEntity() {
        FAReportDocument faReportDocument = MapperUtil.getFaReportDocument();
        return FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, FaReportSourceEnum.MANUAL);
    }

    private List<String> getMockedAssets() {
        List<String> assets = new ArrayList<>();
        assets.add("ASSET1");
        assets.add("ASSET2");
        return assets;
    }

    private List<MovementType> getMockedMovements() throws ParseException, DatatypeConfigurationException {
        MovementType movementType1 = new MovementType();
        movementType1.setCalculatedCourse(1.0);
        movementType1.setCalculatedSpeed(1.0);
        movementType1.setWkt("MULTIPOINT(11.09245 58.8386666666667,675 859)");
        movementType1.setPositionTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-07-01 09:15:00"));

        MovementType movementType2 = new MovementType();
        movementType2.setCalculatedCourse(1.0);
        movementType2.setCalculatedSpeed(1.0);
        movementType2.setWkt("MULTIPOINT(11.09245 58.8386666666667,675 859)");
        movementType2.setPositionTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-07-01 12:15:00"));

        return Arrays.asList(movementType1, movementType2);
    }

}
