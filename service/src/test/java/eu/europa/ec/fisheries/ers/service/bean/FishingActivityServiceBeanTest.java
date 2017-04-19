package eu.europa.ec.fisheries.ers.service.bean;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripIdentifierDao;
import eu.europa.ec.fisheries.ers.fa.dao.VesselIdentifierDao;
import eu.europa.ec.fisheries.ers.fa.dao.VesselTransportMeansDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.ers.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.AssetProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import lombok.SneakyThrows;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;

public class FishingActivityServiceBeanTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    EntityManager em;

    @Mock
    FishingActivityDao fishingActivityDao;

    @Mock
    FaReportDocumentDao faReportDocumentDao;

    @Mock
    FishingTripDao fishingTripDao;

    @Mock
    FishingTripIdentifierDao fishingTripIdentifierDao;

    @Mock
    VesselIdentifierDao vesselIdentifiersDao;

    @Mock
    VesselTransportMeansDao vesselTransportMeansDao;

    @InjectMocks
    ActivityServiceBean activityService;

    @InjectMocks
    FishingTripServiceBean fishingTripService;

    @Mock
    AssetProducerBean assetProducer;

    @Mock
    ActivityConsumerBean activityConsumer;

    @Mock
    AssetModuleServiceBean assetModule;

    @Mock
    JAXBMarshaller marshaller;

    @Test
    @SneakyThrows
    public void testGetVesselDetailsForFishingTrip() throws ServiceException {

        when(vesselTransportMeansDao.findLatestVesselByTripId("NOR-TRP-20160517234053706")).thenReturn(new VesselTransportMeansEntity());

        //Trigger
        VesselDetailsDTO vesselDetailsDTO = fishingTripService.getVesselDetailsForFishingTrip("NOR-TRP-20160517234053706");

        Mockito.verify(vesselTransportMeansDao, Mockito.times(1)).findLatestVesselByTripId(Mockito.any(String.class));
        //Verify

        assertNotNull(vesselDetailsDTO);
    }

    @Test
    @SneakyThrows
    public void testGetVesselDetailsAndContactPartiesForFishingTrip() {

        when(vesselTransportMeansDao.findLatestVesselByTripId("NOR-TRP-20160517234053706")).thenReturn(new VesselTransportMeansEntity());

        VesselDetailsDTO vesselDetailsDTO = fishingTripService.getVesselDetailsForFishingTrip("NOR-TRP-20160517234053706");

        assertNotNull(vesselDetailsDTO);
    }

    @Test
    @SneakyThrows
    public void testEnrichVesselDetailsAndContactPartiesForFishingTrip() {

        when(vesselTransportMeansDao.findLatestVesselByTripId(Mockito.anyString())).thenReturn(new VesselTransportMeansEntity());
        List<Asset> listAssetResponse = new ArrayList<>();
        Asset asset = new Asset();
        asset.setCfr("UPDATED_CFR");
        asset.setIrcs("UPDATED_IRCS");
        asset.setImo("UPDATED_IMO");
        asset.setName("name");
        listAssetResponse.add(asset);

        when(assetModule.getAssetListResponse(Mockito.any(AssetListQuery.class))).thenReturn(listAssetResponse);

        VesselDetailsDTO vesselDetailsDTO = fishingTripService.getVesselDetailsForFishingTrip("NOR-TRP-20160517234053706");

        assertNotNull(vesselDetailsDTO);

    }

    @Test
    @SneakyThrows
    public void getFishingActivityReportAndRelatedDataForFishingTrip() throws ServiceException {

        when(fishingActivityDao.getFishingActivityListForFishingTrip("NOR-TRP-20160517234053706", null)).thenReturn(MapperUtil.getFishingActivityEntityList());
        when(vesselIdentifiersDao.getLatestVesselIdByTrip(Mockito.anyString())).thenReturn(MapperUtil.getVesselIdentifiersList());

     /*   List<ReportDTO> reportDTOList = new ArrayList<>();
        Map<String, FishingActivityTypeDTO > summary = new HashMap<>();
        MessageCountDTO messagesCount = new MessageCountDTO();
        //Trigger
        fishingTripService.getFishingActivityReportAndRelatedDataForFishingTrip("NOR-TRP-20160517234053706",reportDTOList,summary,messagesCount, null);

        Mockito.verify(fishingActivityDao, Mockito.times(1)).getFishingActivityListForFishingTrip(Mockito.any(String.class),Mockito.any(Pagination.class));
        //Verify

        assertEquals(3, summary.size());
        assertEquals(3, reportDTOList.size()); */

    }


    private List<FaReportDocumentEntity> getMockedFishingActivityReportEntities() {
        List<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>();
        FAReportDocument faReportDocument = MapperUtil.getFaReportDocument();
        faReportDocumentEntities.add(FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, new FaReportDocumentEntity(), FaReportSourceEnum.MANUAL));
        return faReportDocumentEntities;
    }
}
