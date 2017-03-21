package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.*;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.message.exception.ActivityMessageException;
import eu.europa.ec.fisheries.ers.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.ers.service.util.ActivityDataUtil;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.ers.service.dto.fishingtrip.VesselDetailsTripDTO;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.AssetProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import lombok.SneakyThrows;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by kovian on 29/09/2016.
 */
public class FishingActivityServiceBeanTest {


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
    VesselIdentifiersDao vesselIdentifiersDao;

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

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();


    @Test
    @SneakyThrows
    public void testGetVesselDetailsForFishingTrip() throws ServiceException {
        when(fishingTripDao.fetchVesselTransportDetailsForFishingTrip("NOR-TRP-20160517234053706")).thenReturn(MapperUtil.getFishingTripEntity());
        when(vesselIdentifiersDao.getLatestVesselIdByTrip("NOR-TRP-20160517234053706")).thenReturn(MapperUtil.getVesselIdentifiersList());

        //Trigger
        VesselDetailsTripDTO vesselDetailsTripDTO= fishingTripService.getVesselDetailsForFishingTrip("NOR-TRP-20160517234053706");

        Mockito.verify(vesselIdentifiersDao, Mockito.times(1)).getLatestVesselIdByTrip(Mockito.any(String.class));
        //Verify

        assertNotNull( vesselDetailsTripDTO);
        assertEquals("vesselGroup1", vesselDetailsTripDTO.getName());
    }

    @Test
    @SneakyThrows
    public void testGetVesselDetailsAndContactPartiesForFishingTrip() throws ServiceException {

        when(vesselIdentifiersDao.getLatestVesselIdByTrip("NOR-TRP-20160517234053706")).thenReturn(MapperUtil.getVesselIdentifiersList());

        VesselDetailsTripDTO vesselDetailsTripDTO = fishingTripService.getVesselDetailsForFishingTrip("NOR-TRP-20160517234053706");

        assertNotNull(vesselDetailsTripDTO);
        assertEquals(2, vesselDetailsTripDTO.getContactPersons().size());
        assertEquals("vesselGroup1", vesselDetailsTripDTO.getName());
    }

    @Test
    @SneakyThrows
    public void testEnrichVesselDetailsAndContactPartiesForFishingTrip() throws ServiceException, ActivityModelMarshallException, ActivityMessageException, MessageException, JMSException {

        when(vesselIdentifiersDao.getLatestVesselIdByTrip(Mockito.anyString())).thenReturn(MapperUtil.getVesselIdentifiersList());
        ListAssetResponse listAssetResponse = new ListAssetResponse();
        Asset asset = new Asset();
        asset.setCfr("UPDATED_CFR");
        asset.setIrcs("UPDATED_IRCS");
        asset.setImo("UPDATED_IMO");
        asset.setName("name");
        listAssetResponse.setTotalNumberOfPages(10);

        listAssetResponse.getAsset().add(asset);
        when(assetModule.getAssetListResponse(Mockito.any(VesselDetailsTripDTO.class))).thenReturn(listAssetResponse);

        VesselDetailsTripDTO vesselDetailsTripDTO = fishingTripService.getVesselDetailsForFishingTrip("NOR-TRP-20160517234053706");

        assertNotNull(vesselDetailsTripDTO);

        // Checking that the VesselDetailsTripDTO has been enriched;
        assertEquals("UPDATED_IRCS", vesselDetailsTripDTO.getIrcs());
        assertEquals(true, vesselDetailsTripDTO.isIrcsEnriched());

        assertEquals("UPDATED_IMO", vesselDetailsTripDTO.getUvi());
        assertEquals(true, vesselDetailsTripDTO.isUviEnriched());

        assertEquals(false, vesselDetailsTripDTO.isExMarkEnriched());
        assertEquals(false, vesselDetailsTripDTO.isFlagStateEnriched());
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
