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

package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripIdentifierDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.message.exception.ActivityMessageException;
import eu.europa.ec.fisheries.ers.message.producer.bean.ActivityMessageProducerBean;
import eu.europa.ec.fisheries.ers.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.ers.service.search.Filters;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.ListCriteria;
import eu.europa.ec.fisheries.ers.service.search.Pagination;
import eu.europa.ec.fisheries.ers.service.util.ActivityDataUtil;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FilterFishingActivityReportResultDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.*;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import lombok.SneakyThrows;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by padhyad on 8/9/2016.
 */
public class ActivityServiceBeanTest {

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

    @InjectMocks
    ActivityServiceBean activityService;

    @Mock
    ActivityMessageProducerBean activityProducer;

    @Mock
    AssetsMessageConsumerBean activityConsumer;

    @Mock
    JAXBMarshaller marshaller;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    @SneakyThrows
    public void testGetFaReportCorrections() throws ServiceException {

        //Mock
        FaReportDocumentEntity faReportDocumentEntity = MapperUtil.getFaReportDocumentEntity();
        Mockito.doReturn(faReportDocumentEntity).when(faReportDocumentDao).findFaReportByIdAndScheme(Mockito.any(String.class), Mockito.any(String.class));

        //Trigger
        List<FaReportCorrectionDTO> faReportCorrectionDTOList = activityService.getFaReportCorrections("TEST ID", "SCHEME ID");

        //Verify
        Mockito.verify(faReportDocumentDao, Mockito.times(1)).findFaReportByIdAndScheme("TEST ID", "SCHEME ID");

        //Test
        FaReportCorrectionDTO faReportCorrectionDTO = faReportCorrectionDTOList.get(0);
        assertEquals(faReportDocumentEntity.getStatus(), faReportCorrectionDTO.getCorrectionType());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getCreationDatetime(), faReportCorrectionDTO.getCreationDate());
        assertEquals(faReportDocumentEntity.getAcceptedDatetime(), faReportCorrectionDTO.getAcceptedDate());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getFluxReportIdentifiers().iterator().next().getFluxReportIdentifierId(),
                faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getKey());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getFluxReportIdentifiers().iterator().next().getFluxReportIdentifierSchemeId(),
                faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getValue());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getFluxParty().getFluxPartyName(), faReportCorrectionDTO.getOwnerFluxPartyName());
    }


    @Test
    @SneakyThrows
    public void testGetFishingTripSummary() throws ServiceException {
        when(fishingTripDao.fetchVesselTransportDetailsForFishingTrip("NOR-TRP-20160517234053706")).thenReturn(MapperUtil.getFishingTripEntity());
        when(fishingActivityDao.getFishingActivityListForFishingTrip("NOR-TRP-20160517234053706",null)).thenReturn(MapperUtil.getFishingActivityEntityList());

        //Trigger
        FishingTripSummaryViewDTO fishingTripSummaryViewDTO=activityService.getFishingTripSummary("NOR-TRP-20160517234053706");

        Mockito.verify(fishingActivityDao, Mockito.times(1)).getFishingActivityListForFishingTrip(Mockito.any(String.class),Mockito.any(Pagination.class));
        Mockito.verify(fishingTripDao, Mockito.times(1)).fetchVesselTransportDetailsForFishingTrip(Mockito.any(String.class));
        //Verify
        assertNotNull( fishingTripSummaryViewDTO.getVesselDetails());
        assertEquals("vesselGroup1", fishingTripSummaryViewDTO.getVesselDetails().getName());
        assertEquals(3, fishingTripSummaryViewDTO.getSummary().size());
        assertEquals(3, fishingTripSummaryViewDTO.getActivityReports().size());
        assertNotNull( fishingTripSummaryViewDTO.getMessagesCount());
    }


    @Test
    @SneakyThrows
    public void testGetVesselDetailsForFishingTrip() throws ServiceException {

        when(fishingTripDao.fetchVesselTransportDetailsForFishingTrip("NOR-TRP-20160517234053706")).thenReturn(MapperUtil.getFishingTripEntity());

        //Trigger
        VesselDetailsTripDTO vesselDetailsTripDTO= activityService.getVesselDetailsForFishingTrip("NOR-TRP-20160517234053706");

        Mockito.verify(fishingTripDao, Mockito.times(1)).fetchVesselTransportDetailsForFishingTrip(Mockito.any(String.class));
        //Verify

        assertNotNull( vesselDetailsTripDTO);
        assertEquals("vesselGroup1", vesselDetailsTripDTO.getName());
    }

    @Test
    @SneakyThrows
    public void testGetVesselDetailsAndContactPartiesForFishingTrip() throws ServiceException {

        when(fishingTripDao.fetchVesselTransportDetailsForFishingTrip("NOR-TRP-20160517234053706")).thenReturn(MapperUtil.getFishingTripEntityWithContactParties());

        VesselDetailsTripDTO vesselDetailsTripDTO = activityService.getVesselDetailsForFishingTrip("NOR-TRP-20160517234053706");

        assertNotNull(vesselDetailsTripDTO);
        assertEquals(2, vesselDetailsTripDTO.getContactPersons().size());
        assertEquals("vesselGroup1", vesselDetailsTripDTO.getName());
    }

    @Test
    @SneakyThrows
    public void testEnrichVesselDetailsAndContactPartiesForFishingTrip() throws ServiceException, ModelMarshallException, ActivityMessageException, MessageException, JMSException {

        String response = JAXBMarshaller.marshallJaxBObjectToString(ActivityDataUtil.getListAssetResponse());
        TextMessage mockTextMessage = mock(TextMessage.class);

        when(fishingTripDao.fetchVesselTransportDetailsForFishingTrip("NOR-TRP-20160517234053706")).thenReturn(MapperUtil.getFishingTripEntityWithContactParties());
        when(activityProducer.sendAssetsModuleSynchronousMessage("")).thenReturn("0101");
        when(activityConsumer.getMessage(null, TextMessage.class)).thenReturn(mockTextMessage);
        when(mockTextMessage.getText()).thenReturn(response);

        VesselDetailsTripDTO vesselDetailsTripDTO = activityService.getVesselDetailsForFishingTrip("NOR-TRP-20160517234053706");

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

        when(fishingActivityDao.getFishingActivityListForFishingTrip("NOR-TRP-20160517234053706",null)).thenReturn(MapperUtil.getFishingActivityEntityList());

        List<ReportDTO> reportDTOList = new ArrayList<>();
        Map<String, FishingActivityTypeDTO > summary = new HashMap<>();
        MessageCountDTO messagesCount = new MessageCountDTO();
        //Trigger
        activityService.getFishingActivityReportAndRelatedDataForFishingTrip("NOR-TRP-20160517234053706",reportDTOList,summary,messagesCount);

        Mockito.verify(fishingActivityDao, Mockito.times(1)).getFishingActivityListForFishingTrip(Mockito.any(String.class),Mockito.any(Pagination.class));
        //Verify

        assertEquals(3, summary.size());
        assertEquals(3, reportDTOList.size());
        assertEquals(3, messagesCount.getNoOfDeclarations());

    }

    @Test
    @SneakyThrows
    public void getFishingActivityListByQuery() throws ServiceException {

        FishingActivityQuery query = new FishingActivityQuery();
        List<ListCriteria> list = new ArrayList<ListCriteria>();

        Map<Filters,String> searchCriteriaMap = new HashMap<>();

        searchCriteriaMap.put(Filters.FROM_ID, "OWNER1");
        searchCriteriaMap.put(Filters.FROM_NAME, "OWNER_NAME1");

        Pagination pagination =new Pagination();
        pagination.setListSize(4);
        pagination.setPage(1);
      //  query.setSearchCriteria(list);
        query.setPagination(pagination);
        query.setSearchCriteriaMap(searchCriteriaMap);

        when(fishingActivityDao.getFishingActivityListByQuery(query)).thenReturn(MapperUtil.getFishingActivityEntityList());

        //Trigger
        FilterFishingActivityReportResultDTO filterFishingActivityReportResultDTO= activityService.getFishingActivityListByQuery(query);

        Mockito.verify(fishingActivityDao, Mockito.times(1)).getFishingActivityListByQuery(Mockito.any(FishingActivityQuery.class));
        //Verify
        assertNotNull(filterFishingActivityReportResultDTO);
        assertNotNull(filterFishingActivityReportResultDTO.getResultList());
        assertNotNull(filterFishingActivityReportResultDTO.getPagination());

    }

    private List<FaReportDocumentEntity> getMockedFishingActivityReportEntities() {
        List<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>();
        FAReportDocument faReportDocument = MapperUtil.getFaReportDocument();
        faReportDocumentEntities.add(FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, new FaReportDocumentEntity(), FaReportSourceEnum.MANUAL));
        return faReportDocumentEntities;
    }
}
