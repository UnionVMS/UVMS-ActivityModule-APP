package eu.europa.ec.fisheries.ers.service.bean;

import lombok.SneakyThrows;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
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
    MdrModuleServiceBean mdrModuleServiceBeanMock;

    @Test
    @SneakyThrows
    public void getAcronymFromMdrCodeDescription() {
        String portDescription = "PORT VICTORIA";
        Map<String, List<String>> returnMap = new HashMap<>();
        returnMap.put("description",Arrays.asList(portDescription));
        when(mdrModuleServiceBeanMock.getAcronymFromMdr("LOCATION", "SCPOV", Arrays.asList("code"), 1, "description")).thenReturn(returnMap);

        List<String> portDescriptionList = mdrModuleServiceBeanMock.getAcronymFromMdr("LOCATION", "SCPOV",
                Arrays.asList("code"), 1, "description").get("description");

        assertEquals(1, portDescriptionList.size());
        assertEquals(portDescription, portDescriptionList.get(0));
    }
}
