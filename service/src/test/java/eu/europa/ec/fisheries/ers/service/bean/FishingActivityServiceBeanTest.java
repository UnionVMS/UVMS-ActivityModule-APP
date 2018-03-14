package eu.europa.ec.fisheries.ers.service.bean;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripIdentifierDao;
import eu.europa.ec.fisheries.ers.fa.dao.VesselIdentifierDao;
import eu.europa.ec.fisheries.ers.fa.dao.VesselTransportMeansDao;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.AssetProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
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
    MdrModuleServiceBean mdrModuleServiceBean;

    @Mock
    JAXBMarshaller marshaller;

    @Test
    @SneakyThrows
    public void testGetVesselDetailsForFishingTrip() throws ServiceException {
        Map<String, List<String>> returnMap = new HashMap<>();
        returnMap.put("code", new ArrayList<String>());
        when(vesselTransportMeansDao.findLatestVesselByTripId("NOR-TRP-20160517234053706")).thenReturn(new VesselTransportMeansEntity());
        when(mdrModuleServiceBean.getAcronymFromMdr("FLUX_VESSEL_ID_TYPE", "*", new ArrayList<String>(), 9999999, "code")).thenReturn(returnMap);
        //Trigger
        VesselDetailsDTO vesselDetailsDTO = fishingTripService.getVesselDetailsForFishingTrip("NOR-TRP-20160517234053706");

        Mockito.verify(vesselTransportMeansDao, Mockito.times(1)).findLatestVesselByTripId(Mockito.any(String.class));
        //Verify

        assertNotNull(vesselDetailsDTO);
    }

    @Test
    @SneakyThrows
    public void testGetVesselDetailsAndContactPartiesForFishingTrip() {
        Map<String, List<String>> returnMap = new HashMap<>();
        returnMap.put("code", new ArrayList<String>());
        when(vesselTransportMeansDao.findLatestVesselByTripId("NOR-TRP-20160517234053706")).thenReturn(new VesselTransportMeansEntity());
        when(mdrModuleServiceBean.getAcronymFromMdr("FLUX_VESSEL_ID_TYPE", "*", new ArrayList<String>(), 9999999, "code")).thenReturn(returnMap);

        VesselDetailsDTO vesselDetailsDTO = fishingTripService.getVesselDetailsForFishingTrip("NOR-TRP-20160517234053706");

        assertNotNull(vesselDetailsDTO);
    }

    @Test
    @SneakyThrows
    public void testEnrichVesselDetailsAndContactPartiesForFishingTrip() {
        Map<String, List<String>> returnMap = new HashMap<>();
        returnMap.put("code", new ArrayList<String>());
        when(vesselTransportMeansDao.findLatestVesselByTripId(Mockito.anyString())).thenReturn(new VesselTransportMeansEntity());
        when(mdrModuleServiceBean.getAcronymFromMdr("FLUX_VESSEL_ID_TYPE", "*", new ArrayList<String>(), 9999999, "code")).thenReturn(returnMap);
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
    }

    @Test
    @SneakyThrows
    public void testGetAcronymFromMdrCodeDescription() {
        String portDescription = "PORT VICTORIA";
        Map<String, List<String>> returnMap = new HashMap<>();
        returnMap.put("description", new ArrayList<String>(Arrays.asList(portDescription)));
        when(mdrModuleServiceBean.getAcronymFromMdr("LOCATION", "SCPOV", new ArrayList<String>(Arrays.asList("code")), 1, "description")).thenReturn(returnMap);

        List<String> portDescriptionList = mdrModuleServiceBean.getAcronymFromMdr("LOCATION", "SCPOV",
                new ArrayList<String>(Arrays.asList("code")), 1, "description").get("description");

        assertTrue(portDescriptionList.size() == 1);
        assertTrue(portDescriptionList.get(0).equals(portDescription));
    }
}
