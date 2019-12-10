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


package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.FluxFaReportMessageDao;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.uvms.activity.service.AssetModuleService;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import javax.xml.datatype.DatatypeConfigurationException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;

@RunWith(MockitoJUnitRunner.class)
public class FluxMessageServiceBeanTest {

    @Mock
    private FaReportDocumentDao faReportDocumentDao;

    @Mock
    private FluxFaReportMessageDao fluxFaReportMessageDao;

    @Mock
    private MovementModuleServiceBean movementModule;

    @Mock
    private AssetModuleService assetModule;

    @Mock
    private FaMessageSaverBean faMessageSaverBean;

    @InjectMocks
    private FluxMessageServiceBean fluxMessageService;

    private List<FAReportDocument> faReportDocuments;

    private FLUXFAReportMessage fluxFaReportMessage;

    @Before
    public void setUp() throws Exception {
        FAReportDocument faReportDocument2 = MapperUtil.getFaReportDocument();
        faReportDocument2.getRelatedFLUXReportDocument().setPurposeCode(MapperUtil.getCodeType("9", "list-id-1"));
        IDType id = faReportDocument2.getRelatedFLUXReportDocument().getReferencedID();
        faReportDocument2.setAcceptanceDateTime(MapperUtil.getDateTimeType("2016-07-01 11:15:00"));
        faReportDocument2.getSpecifiedFishingActivities().get(0).setOccurrenceDateTime(MapperUtil.getDateTimeType("2016-07-01 11:15:00"));

        FAReportDocument faReportDocument1 = MapperUtil.getFaReportDocument();
        faReportDocument1.getRelatedFLUXReportDocument().setIDS(Arrays.asList(id));
        faReportDocument1.getRelatedFLUXReportDocument().setPurposeCode(MapperUtil.getCodeType("3", "list-id-1"));
        faReportDocument1.setAcceptanceDateTime(MapperUtil.getDateTimeType("2016-07-01 09:15:00"));
        faReportDocument1.getSpecifiedFishingActivities().get(0).setOccurrenceDateTime(MapperUtil.getDateTimeType("2016-07-01 09:15:00"));

        fluxFaReportMessage = new FLUXFAReportMessage();
        faReportDocuments = Arrays.asList(faReportDocument1, faReportDocument2);
        fluxFaReportMessage.setFAReportDocuments(faReportDocuments);
        fluxFaReportMessage.setFLUXReportDocument(MapperUtil.getFluxReportDocument());

        Mockito.doReturn(new FluxFaReportMessageEntity()).when(fluxFaReportMessageDao).createEntity(Mockito.any(FluxFaReportMessageEntity.class));

        Mockito.doReturn(getMockedAssets()).when(assetModule).getAssetGuids(anyListOf(VesselIdentifierEntity.class));
        Mockito.doReturn(getMockedMovements()).when(movementModule).getMovement(anyListOf(String.class), Mockito.any(Instant.class), Mockito.any(Instant.class));
        Mockito.doReturn(getMockedFishingActivityReportEntity()).when(faReportDocumentDao).findFaReportByIdAndScheme(Mockito.any(String.class), Mockito.any(String.class));
        Mockito.when(faMessageSaverBean.saveReportMessageNow(any(FluxFaReportMessageEntity.class))).then(invocation -> invocation.getArguments()[0]);
    }

    @Test
    public void testSaveFishingActivityReportDocuments() throws Exception {
        // Trigger
        fluxMessageService.saveFishingActivityReportDocuments(fluxFaReportMessage, FaReportSourceEnum.FLUX);

        //Verify
        ArgumentCaptor<FluxFaReportMessageEntity> argumentCaptor = ArgumentCaptor.forClass(FluxFaReportMessageEntity.class);
        Mockito.verify(faMessageSaverBean).saveReportMessageNow(argumentCaptor.capture());

        FluxFaReportMessageEntity fluxFaReportMessageEntity = argumentCaptor.getValue();

        Mockito.verify(faReportDocumentDao, Mockito.times(2)).findFaReportByIdAndScheme(Mockito.any(String.class), Mockito.any(String.class));

        //Test
        ArrayList<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>(fluxFaReportMessageEntity.getFaReportDocuments());
        FaReportDocumentEntity faReportDocumentEntity = faReportDocumentEntities.get(0);

        assertEquals(FaReportStatusType.getFaReportStatusEnum(Integer.parseInt(faReportDocuments.get(1).getRelatedFLUXReportDocument().getPurposeCode().getValue())).name(),
                faReportDocumentEntity.getStatus());
    }

    @Test
    public void saveFishingReportMessage_expectFishingTripStartAndEndDateToBeSet() throws Exception {
        // Trigger
        fluxMessageService.saveFishingActivityReportDocuments(fluxFaReportMessage, FaReportSourceEnum.FLUX);

        //Verify
        ArgumentCaptor<FluxFaReportMessageEntity> argumentCaptor = ArgumentCaptor.forClass(FluxFaReportMessageEntity.class);
        Mockito.verify(faMessageSaverBean).saveReportMessageNow(argumentCaptor.capture());

        FluxFaReportMessageEntity fluxFaReportMessageEntity = argumentCaptor.getValue();

        Mockito.verify(faReportDocumentDao, Mockito.times(2)).findFaReportByIdAndScheme(Mockito.any(String.class), Mockito.any(String.class));

        //Test
        ArrayList<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>(fluxFaReportMessageEntity.getFaReportDocuments());
        FaReportDocumentEntity faReportDocumentEntity = faReportDocumentEntities.get(0);

        Set<FishingActivityEntity> fishingActivities = faReportDocumentEntity.getFishingActivities();
        FishingActivityEntity fishingActivityEntity = fishingActivities.iterator().next();
        FishingTripEntity fishingTrip = fishingActivityEntity.getFishingTrip();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDate = simpleDateFormat.format(Date.from(fishingTrip.getCalculatedTripStartDate()));
        String endDate = simpleDateFormat.format(Date.from(fishingTrip.getCalculatedTripEndDate()));

        assertEquals("2016-07-01 09:15:00", startDate);
        assertEquals("2016-07-01 11:15:00", endDate);
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
