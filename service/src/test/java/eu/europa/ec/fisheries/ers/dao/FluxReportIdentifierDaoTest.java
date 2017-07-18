/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.dao;

import eu.europa.ec.fisheries.ers.fa.dao.FluxReportIdentifierDao;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityIDType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityTableType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityUniquinessList;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GetNonUniqueIdsRequest;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by kovian on 17/07/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class FluxReportIdentifierDaoTest {

    @InjectMocks
    FluxReportIdentifierDao dao;

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<FluxReportIdentifierEntity> query;

    @Test
    public void testGetMatchingIdentifiersEmptyList(){
        final List<FluxReportIdentifierEntity> matchingIdentifiers = dao.getMatchingIdentifiers(new ArrayList<ActivityIDType>(), ActivityTableType.FLUX_REPORT_DOCUMENT_ENTITY);

        assertNotNull(matchingIdentifiers);
        assertTrue(matchingIdentifiers.size() == 0);
    }

    @Test
    public void testGetMatchingIdentifiers(){
        when(dao.getEntityManager().createNamedQuery(anyString(), any(FluxReportIdentifierEntity.class.getClass()))).thenReturn(query);
        ActivityUniquinessList reqElem = getMockedRequest().iterator().next();
        final List<FluxReportIdentifierEntity> matchingIdentifiers = dao.getMatchingIdentifiers(reqElem.getIds(), reqElem.getActivityTableType());
        assertNotNull(matchingIdentifiers);
        assertTrue(matchingIdentifiers.size() == 0);
    }


    @SneakyThrows
    private List<ActivityUniquinessList> getMockedRequest() {
        GetNonUniqueIdsRequest getNonUniqueIdsRequest = JAXBMarshaller.unmarshallTextMessage(getStrRequest(), GetNonUniqueIdsRequest.class);
        return getNonUniqueIdsRequest.getActivityUniquinessLists();
    }

    private String getStrRequest(){
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

}
