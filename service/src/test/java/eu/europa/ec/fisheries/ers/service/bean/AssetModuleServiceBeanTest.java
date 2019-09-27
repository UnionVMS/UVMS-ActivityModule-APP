package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.AssetProducerBean;
import eu.europa.ec.fisheries.uvms.asset.client.AssetClient;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssetModuleServiceBeanTest {

    @Mock
    private AssetProducerBean assetProducerBean;

    @Mock
    private ActivityConsumerBean activityConsumerBean;

    @Mock
    private AssetClient assetClient;

    private AssetModuleServiceBean assetModuleServiceBean;

    @Before
    public void setUp() throws Exception {
        assetModuleServiceBean = new AssetModuleServiceBean(assetProducerBean, activityConsumerBean, assetClient);
    }

    @Test
    public void getAssetGuids_withCFR() {
        // Given
        VesselIdentifierEntity vesselIdentifier = new VesselIdentifierEntity();
        vesselIdentifier.setVesselIdentifierId("vessel-id-123");
        vesselIdentifier.setVesselIdentifierSchemeId("cfr");

        List<VesselIdentifierEntity> vesselIdentifiers = new ArrayList<>();
        vesselIdentifiers.add(vesselIdentifier);

        UUID vesselId = UUID.randomUUID();

        AssetDTO assetDTO = new AssetDTO();
        assetDTO.setId(vesselId);
        assetDTO.setCfr("vessel-id-123");

        List<AssetDTO> assetList = new ArrayList<>();
        assetList.add(assetDTO);

        when(assetClient.getAssetList(any(AssetQuery.class))).thenReturn(assetList);

        // When
        List<String> assetGuids = assetModuleServiceBean.getAssetGuids(vesselIdentifiers);

        // Then
        assertEquals(1, assetGuids.size());
        assertEquals(vesselId.toString(), assetGuids.get(0));
    }
}