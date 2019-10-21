package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.fa.dao.VesselIdentifierDao;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import lombok.SneakyThrows;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class FishingActivityServiceBeanTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    FishingActivityDao fishingActivityDao;

    @Mock
    VesselIdentifierDao vesselIdentifiersDao;

    @Mock
    MdrModuleServiceBean mdrModuleServiceBean;

    @Test
    @SneakyThrows
    public void getFishingActivityReportAndRelatedDataForFishingTrip() {
        when(fishingActivityDao.getFishingActivityListForFishingTrip("NOR-TRP-20160517234053706", null)).thenReturn(MapperUtil.getFishingActivityEntityList());
        when(vesselIdentifiersDao.getLatestVesselIdByTrip(Mockito.anyString())).thenReturn(MapperUtil.getVesselIdentifiersList());
    }

    @Test
    @SneakyThrows
    public void testGetAcronymFromMdrCodeDescription() {
        String portDescription = "PORT VICTORIA";
        Map<String, List<String>> returnMap = new HashMap<>();
        returnMap.put("description",Arrays.asList(portDescription));
        when(mdrModuleServiceBean.getAcronymFromMdr("LOCATION", "SCPOV", Arrays.asList("code"), 1, "description")).thenReturn(returnMap);

        List<String> portDescriptionList = mdrModuleServiceBean.getAcronymFromMdr("LOCATION", "SCPOV",
                Arrays.asList("code"), 1, "description").get("description");

        assertEquals(1, portDescriptionList.size());
        assertEquals(portDescription, portDescriptionList.get(0));
    }
}
