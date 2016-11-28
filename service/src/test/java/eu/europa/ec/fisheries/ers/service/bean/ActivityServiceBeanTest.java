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

import com.vividsolutions.jts.geom.Geometry;
import eu.europa.ec.fisheries.ers.fa.dao.*;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.message.producer.bean.ActivityMessageProducerBean;
import eu.europa.ec.fisheries.ers.service.SpatialModuleService;
import eu.europa.ec.fisheries.ers.service.search.Filters;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.Pagination;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.schema.audit.search.v1.ListCriteria;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FilterFishingActivityReportResultDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaIdentifierType;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;
import lombok.SneakyThrows;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Mock
    VesselIdentifiersDao vesselIdentifiersDao;

    @InjectMocks
    ActivityServiceBean activityService;

    @InjectMocks
    FishingTripServiceBean fishingTripService;

    @Mock
    ActivityMessageProducerBean activityProducer;

    @Mock
    AssetsMessageConsumerBean activityConsumer;

    @Mock
    private SpatialModuleService spatialModule;

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
    public void getFishingActivityListByQuery() throws ServiceException {

        FishingActivityQuery query = new FishingActivityQuery();
        List<ListCriteria> list = new ArrayList<ListCriteria>();

        Map<Filters,String> searchCriteriaMap = new HashMap<>();
        List<AreaIdentifierType> areaIdentifierTypes =new ArrayList<>();
        searchCriteriaMap.put(Filters.FROM_ID, "OWNER1");
        searchCriteriaMap.put(Filters.FROM_NAME, "OWNER_NAME1");

        Pagination pagination =new Pagination();
        pagination.setListSize(4);
        pagination.setPage(1);
      //  query.setSearchCriteria(list);
        query.setPagination(pagination);
        query.setSearchCriteriaMap(searchCriteriaMap);

        when(spatialModule.getFilteredAreaGeom(areaIdentifierTypes)).thenReturn("('MULTIPOINT (10 40, 40 30, 20 20, 30 10)')");

        when(fishingActivityDao.getFishingActivityListByQuery(query, null)).thenReturn(MapperUtil.getFishingActivityEntityList());

        //Trigger
        FilterFishingActivityReportResultDTO filterFishingActivityReportResultDTO= activityService.getFishingActivityListByQuery(query, null);

        Mockito.verify(fishingActivityDao, Mockito.times(1)).getFishingActivityListByQuery(Mockito.any(FishingActivityQuery.class), Mockito.any(Geometry.class));
        //Verify
        assertNotNull(filterFishingActivityReportResultDTO);
        assertNotNull(filterFishingActivityReportResultDTO.getResultList());
        assertNotNull(filterFishingActivityReportResultDTO.getPagination());

    }


    @Test
    @SneakyThrows
    public void getFishingActivityListByQuery_emptyResultSet() throws ServiceException {

        FishingActivityQuery query = new FishingActivityQuery();
        List<ListCriteria> list = new ArrayList<ListCriteria>();

        Map<Filters,String> searchCriteriaMap = new HashMap<>();
        List<AreaIdentifierType> areaIdentifierTypes =new ArrayList<>();
        searchCriteriaMap.put(Filters.FROM_ID, "OWNER1");
        searchCriteriaMap.put(Filters.FROM_NAME, "OWNER_NAME1");

        Pagination pagination =new Pagination();
        pagination.setListSize(4);
        pagination.setPage(1);
        //  query.setSearchCriteria(list);
        query.setPagination(pagination);
        query.setSearchCriteriaMap(searchCriteriaMap);

        when(spatialModule.getFilteredAreaGeom(areaIdentifierTypes)).thenReturn("('MULTIPOINT (10 40, 40 30, 20 20, 30 10)')");

        when(fishingActivityDao.getFishingActivityListByQuery(query, null)).thenReturn(new ArrayList<FishingActivityEntity>());

        //Trigger
        FilterFishingActivityReportResultDTO filterFishingActivityReportResultDTO= activityService.getFishingActivityListByQuery(query, new ArrayList<Dataset>());

        Mockito.verify(fishingActivityDao, Mockito.times(1)).getFishingActivityListByQuery(Mockito.any(FishingActivityQuery.class), Mockito.any(Geometry.class));
        //Verify
         assertNotNull(filterFishingActivityReportResultDTO);
      //  assertNotNull(filterFishingActivityReportResultDTO.getResultList());
       // assertNotNull(filterFishingActivityReportResultDTO.getPagination());

    }



}
