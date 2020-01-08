/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityIDType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityTableType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityUniquinessList;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActivityMatchingIdsServiceTest {

    @InjectMocks
    private ActivityMatchingIdsService matchingBean;

    @Mock
    private FaReportDocumentDao faReportDocumentDao;

    @Test
    public void getMatchingIdsResponse() throws Exception {
        // Given
        when(faReportDocumentDao.getMatchingIdentifiers(anyListOf(ActivityIDType.class), Mockito.any(ActivityTableType.class))).thenReturn(getMockedIdentifiers());

        List<ActivityUniquinessList> inputActivityUniquenessList = createInputActivityUniquenessList();

        // When
        List<ActivityUniquinessList> matchingIds = matchingBean.getMatchingIds(inputActivityUniquenessList);

        // Then
        assertEquals(2, matchingIds.size());
    }

    @Test
    public void getMatchingIds_simpleListOfIds() {
        // Given
        when(faReportDocumentDao.getMatchingIdentifiers(anyListOf(ActivityIDType.class), Mockito.any(ActivityTableType.class))).thenReturn(getMockedIdentifiers());

        List<ActivityIDType> activityIDTypes = new ArrayList<>();
        activityIDTypes.add(new ActivityIDType("46DCC44C-0AE2-434C-BC14-B85D86B29512iiiii", "scheme-idvv"));
        activityIDTypes.add(new ActivityIDType("46DCC44C-0AE2-434C-BC14-B85D86B29512bbbbb", "scheme-idgg"));

        // When
        List<ActivityIDType> matchingIds = matchingBean.getMatchingIds(activityIDTypes, ActivityTableType.RELATED_FLUX_REPORT_DOCUMENT_ENTITY);

        // Then
        assertEquals(2, matchingIds.size());
    }

    @Test
    public void getMatchingIdsResponseNullPointer() {
        // Given
        when(faReportDocumentDao.getMatchingIdentifiers(anyListOf(ActivityIDType.class), Mockito.any(ActivityTableType.class))).thenReturn(null);

        // When
        List<ActivityUniquinessList> matchingIds = matchingBean.getMatchingIds(null);

        // Then
        assertTrue(matchingIds.isEmpty());
    }

    @Test
    public void getMatchingIdsResponseNullMap() throws Exception {
        // Given
        when(faReportDocumentDao.getMatchingIdentifiers(anyListOf(ActivityIDType.class), Mockito.any(ActivityTableType.class))).thenReturn(null);

        List<ActivityUniquinessList> inputActivityUniquenessList = createInputActivityUniquenessList();

        // When
        List<ActivityUniquinessList> matchingIds = matchingBean.getMatchingIds(inputActivityUniquenessList);

        // Then
        assertEquals(2, matchingIds.size());
    }

    @Test
    public void getMatchingIdsResponseMappingEmptyList() throws Exception {
        // Given
        when(faReportDocumentDao.getMatchingIdentifiers(anyListOf(ActivityIDType.class), Mockito.any(ActivityTableType.class))).thenReturn(getMockedIdentifiers());

        List<ActivityUniquinessList> inputActivityUniquenessList = createInputActivityUniquenessList();

        // When
        List<ActivityUniquinessList> matchingIds = matchingBean.getMatchingIds(inputActivityUniquenessList);

        // Then
        assertEquals(2, matchingIds.size());
    }

    private List<ActivityUniquinessList> createInputActivityUniquenessList() throws ActivityModelMarshallException {
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

    private List<FaReportDocumentEntity> getMockedIdentifiers() {
        FaReportDocumentEntity faReportDocumentEntity1 = new FaReportDocumentEntity();
        FaReportDocumentEntity faReportDocumentEntity2 = new FaReportDocumentEntity();
        faReportDocumentEntity1.setFluxReportDocument_Id("46DCC44C-0AE2-434C-BC14-B85D86B29512iiiii");
        faReportDocumentEntity1.setFluxReportDocument_IdSchemeId("scheme-idvv");
        faReportDocumentEntity2.setFluxReportDocument_Id("46DCC44C-0AE2-434C-BC14-B85D86B29512bbbbb");
        faReportDocumentEntity2.setFluxReportDocument_IdSchemeId("scheme-idqq");
        return Arrays.asList(faReportDocumentEntity1, faReportDocumentEntity2);
    }
}
