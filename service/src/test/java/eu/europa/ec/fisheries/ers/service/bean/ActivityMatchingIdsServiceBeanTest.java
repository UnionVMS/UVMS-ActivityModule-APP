/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FluxReportIdentifierDao;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityTableType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityUniquinessList;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsResponse;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;

/**
 * Created by kovian on 17/07/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ActivityMatchingIdsServiceBeanTest {

    @InjectMocks
    ActivityMatchingIdsServiceBean matchingBean;

    @Mock
    FluxReportIdentifierDao fluxRepIdentDao;

    @Test
    public void getMatchingIdsResponse() {

        when(fluxRepIdentDao.getMatchingIdentifiers(anyList(), Mockito.any(ActivityTableType.class))).thenReturn(getMockedIdentifiers());

        List<ActivityUniquinessList> request = getMockedRequest();
        GetNonUniqueIdsResponse matchingIdsResponse = matchingBean.getMatchingIdsResponse(request);

        assertNotNull(matchingIdsResponse);
        assertTrue(CollectionUtils.isNotEmpty(matchingIdsResponse.getActivityUniquinessLists()));
        assertEquals(2, matchingIdsResponse.getActivityUniquinessLists().size());
    }

    @Test
    public void getMatchingIdsResponseNullPointer() {
        when(fluxRepIdentDao.getMatchingIdentifiers(anyList(), Mockito.any(ActivityTableType.class))).thenReturn(null);

        GetNonUniqueIdsResponse matchingIdsResponse = matchingBean.getMatchingIdsResponse(null);
        assertNull(matchingIdsResponse);
    }

    @Test
    public void getMatchingIdsResponseNullMap() {

        when(fluxRepIdentDao.getMatchingIdentifiers(anyList(), Mockito.any(ActivityTableType.class))).thenReturn(null);

        List<ActivityUniquinessList> request = getMockedRequest();
        GetNonUniqueIdsResponse matchingIdsResponse = matchingBean.getMatchingIdsResponse(request);

        assertNotNull(matchingIdsResponse);
        assertTrue(CollectionUtils.isNotEmpty(matchingIdsResponse.getActivityUniquinessLists()));
        assertEquals(2, matchingIdsResponse.getActivityUniquinessLists().size());
    }

    @Test
    public void initProcess() {
        matchingBean.init();
    }

    @Test
    public void getMatchingIdsResponseMappingEmptyList() {

        when(fluxRepIdentDao.getMatchingIdentifiers(anyList(), Mockito.any(ActivityTableType.class))).thenReturn(getMockedIdentifiers());

        List<ActivityUniquinessList> request = getMockedRequest();
        GetNonUniqueIdsResponse matchingIdsResponse = matchingBean.getMatchingIdsResponse(request);

        assertNotNull(matchingIdsResponse);
        assertTrue(CollectionUtils.isNotEmpty(matchingIdsResponse.getActivityUniquinessLists()));
        assertEquals(2, matchingIdsResponse.getActivityUniquinessLists().size());
    }

    @SneakyThrows
    private List<ActivityUniquinessList> getMockedRequest() {
        GetNonUniqueIdsRequest getNonUniqueIdsRequest = JAXBMarshaller.unmarshallTextMessage(getStrRequest(), GetNonUniqueIdsRequest.class);
        return getNonUniqueIdsRequest.getActivityUniquinessLists();
    }


    private String getStrRequest() {
        return "<ns2:GetNonUniqueIdsRequest xmlns:ns2=\"http://europa.eu/ec/fisheries/uvms/activity/model/schemas\">\n" +
                "    <method>GET_NON_UNIQUE_IDS</method>\n" +
                "    <activityUniquinessList>\n" +
                "        <activityTableType>RELATED_FLUX_REPORT_DOCUMENT_ENTITY</activityTableType>\n" +
                "        <ids>\n" +
                "            <value>46DCC44C-0AE2-434C-BC14-B85D86B29512</value>\n" +
                "            <identifierSchemeId>UUID</identifierSchemeId>\n" +
                "        </ids>\n" +
                "    </activityUniquinessList>\n" +
                "    <activityUniquinessList>\n" +
                "        <activityTableType>FLUX_REPORT_DOCUMENT_ENTITY</activityTableType>\n" +
                "        <ids>\n" +
                "            <value>46DCC44C-0AE2-434C-BC14-B85D86B29512</value>\n" +
                "            <identifierSchemeId>UUID</identifierSchemeId>\n" +
                "        </ids>\n" +
                "    </activityUniquinessList>\n" +
                "</ns2:GetNonUniqueIdsRequest>";
    }


    private List<FluxReportIdentifierEntity> getMockedIdentifiers() {
        FluxReportIdentifierEntity ident1 = new FluxReportIdentifierEntity();
        FluxReportIdentifierEntity ident2 = new FluxReportIdentifierEntity();
        ident1.setFluxReportIdentifierId("46DCC44C-0AE2-434C-BC14-B85D86B29512iiiii");
        ident1.setFluxReportIdentifierSchemeId("scheme-idvv");
        ident1.setFluxReportIdentifierId("46DCC44C-0AE2-434C-BC14-B85D86B29512bbbbb");
        ident1.setFluxReportIdentifierSchemeId("scheme-idqq");
        return Arrays.asList(ident1, ident2);
    }
}
