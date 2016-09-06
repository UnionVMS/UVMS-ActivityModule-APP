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
import eu.europa.ec.fisheries.ers.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.ers.service.search.Pagination;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.FaReportDocumentDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.SneakyThrows;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;

import javax.persistence.EntityManager;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    @SneakyThrows
    public void testGetFaReportCorrections() {

        //Mock
        FaReportDocumentEntity faReportDocumentEntity = MapperUtil.getFaReportDocumentEntity();
        faReportDocumentEntity.getFluxReportDocument().setFluxReportDocumentId(null);
        Mockito.doReturn(Arrays.asList(faReportDocumentEntity)).when(faReportDocumentDao).findFaReportsByReferenceId(Mockito.any(String.class));

        //Trigger
        List<FaReportCorrectionDTO> faReportCorrectionDTOList = activityService.getFaReportCorrections("TEST ID");

        //Verify
        Mockito.verify(faReportDocumentDao, Mockito.times(1)).findFaReportsByReferenceId("TEST ID");

        //Test
        FaReportCorrectionDTO faReportCorrectionDTO = faReportCorrectionDTOList.get(0);
        assertEquals(faReportDocumentEntity.getStatus(), faReportCorrectionDTO.getCorrectionType());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getCreationDatetime(), faReportCorrectionDTO.getCorrectionDate());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getFluxReportDocumentId(), faReportCorrectionDTO.getFaReportIdentifier());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getOwnerFluxPartyId(), faReportCorrectionDTO.getOwnerFluxParty());
    }

    @Test
    @SneakyThrows
    public void testGetFaReportDocumentDetails() throws Exception {

        //Mock
        List<FaReportDocumentEntity> faReportDocumentEntities = getMockedFishingActivityReportEntities();
        Mockito.doReturn(getMockedFishingActivityReportEntities()).when(faReportDocumentDao).findFaReportsByIds(Mockito.any(Collection.class));

        //Trigger
        FaReportDocumentDetailsDTO faReportDocumentDetailsDTO = activityService.getFaReportDocumentDetails("TEST");

        //Verify
        Mockito.verify(faReportDocumentDao, Mockito.times(1)).findFaReportsByIds(Mockito.any(Collection.class));

        //Test
        assertEquals(faReportDocumentEntities.get(0).getTypeCode(), faReportDocumentDetailsDTO.getTypeCode());
        assertEquals(faReportDocumentEntities.get(0).getFmcMarker(), faReportDocumentDetailsDTO.getFmcMarker());
        assertEquals(faReportDocumentEntities.get(0).getAcceptedDatetime(), faReportDocumentDetailsDTO.getAcceptedDateTime());
        assertEquals(faReportDocumentEntities.get(0).getFluxReportDocument().getCreationDatetime(), faReportDocumentDetailsDTO.getCreationDateTime());
        assertEquals(faReportDocumentEntities.get(0).getFluxReportDocument().getFluxReportDocumentId(), faReportDocumentDetailsDTO.getFluxReportDocumentId());
        assertEquals(faReportDocumentEntities.get(0).getFluxReportDocument().getPurposeCode(), faReportDocumentDetailsDTO.getPurposeCode());
        assertEquals(faReportDocumentEntities.get(0).getFluxReportDocument().getReferenceId(), faReportDocumentDetailsDTO.getReferenceId());
        assertEquals(faReportDocumentEntities.get(0).getFluxReportDocument().getOwnerFluxPartyId(), faReportDocumentDetailsDTO.getOwnerFluxPartyId());
        assertEquals(faReportDocumentEntities.get(0).getStatus(), faReportDocumentDetailsDTO.getStatus());

    }

    @Test(expected = ServiceException.class)
    @SneakyThrows
    public void testGetFaReportDocumentDetails_ExpectedException() throws Exception {

        //Mock
        List<FaReportDocumentEntity> faReportDocumentEntities = getMockedFishingActivityReportEntities();
        Mockito.doReturn(null).when(faReportDocumentDao).findFaReportsByIds(Mockito.any(Collection.class));

        //Trigger
        FaReportDocumentDetailsDTO faReportDocumentDetailsDTO = activityService.getFaReportDocumentDetails("TEST");

        //Verify
        Mockito.verify(faReportDocumentDao, Mockito.times(1)).findFaReportsByIds(Mockito.any(Collection.class));
    }

    @Test
    @SneakyThrows
    public void testGetCurrentTripId() {

        Mockito.doReturn(null).when(fishingTripIdentifierDao).getCurrentTrip();

        //Trigger
        String tripID=activityService.getCurrentTripId();

        //Verify
        Mockito.verify(fishingTripIdentifierDao, Mockito.times(1)).getCurrentTrip();
    }

    @Test
    @SneakyThrows
    public void testGetCronologyForTripIds() {

        Mockito.doReturn(null).when(fishingTripIdentifierDao).getFishingTripsBefore(Mockito.any(String.class),Mockito.any(Integer.class));
        Mockito.doReturn(null).when(fishingTripIdentifierDao).getFishingTripsAfter(Mockito.any(String.class),Mockito.any(Integer.class));

        //Trigger
        List<CronologyDTO> cronologyDTOList=activityService.getCronologyForTripIds("TRIPID_TEST",2);

        //Verify
        Mockito.verify(fishingTripIdentifierDao, Mockito.times(1)).getFishingTripsBefore(Mockito.any(String.class),Mockito.any(Integer.class));
        Mockito.verify(fishingTripIdentifierDao, Mockito.times(1)).getFishingTripsAfter(Mockito.any(String.class),Mockito.any(Integer.class));

    }


    @Test
    @SneakyThrows
    public void testGetFishingTripSummary() throws ServiceException {

        when(activityService.getCurrentTripId()).thenReturn(MapperUtil.getCurrentTripID());
        when(fishingTripDao.fetchVesselTransportDetailsForFishingTrip("NOR-TRP-20160517234053706")).thenReturn(MapperUtil.getFishingTripEntity());
        when(fishingActivityDao.getFishingActivityListForFishingTrip("NOR-TRP-20160517234053706",null)).thenReturn(MapperUtil.getFishingActivityEntityList());

        //Trigger
        FishingTripSummaryViewDTO fishingTripSummaryViewDTO=activityService.getFishingTripSummary("NOR-TRP-20160517234053706");

        Mockito.verify(fishingActivityDao, Mockito.times(1)).getFishingActivityListForFishingTrip(Mockito.any(String.class),Mockito.any(Pagination.class));
        Mockito.verify(fishingTripDao, Mockito.times(1)).fetchVesselTransportDetailsForFishingTrip(Mockito.any(String.class));
        //Verify
        assertEquals("currentTripID", fishingTripSummaryViewDTO.getCurrentTripId());
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

    private List<FaReportDocumentEntity> getMockedFishingActivityReportEntities() {
        List<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>();
        FAReportDocument faReportDocument = MapperUtil.getFaReportDocument();
        faReportDocumentEntities.add(FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, new FaReportDocumentEntity()));
        return faReportDocumentEntities;
    }
}
