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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingTripIdentifierDao;
import eu.europa.ec.fisheries.ers.fa.dao.VesselTransportMeansDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.SpatialModuleService;
import eu.europa.ec.fisheries.ers.service.dto.FilterFishingActivityReportResultDTO;
import eu.europa.ec.fisheries.ers.service.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.schema.audit.search.v1.ListCriteria;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.dto.PaginationDto;
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

/**
 * Created by padhyad on 8/9/2016.
 */
public class ActivityServiceBeanTest {

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
    VesselTransportMeansDao vesselIdentifiersDao;
    @InjectMocks
    ActivityServiceBean activityService;
    @InjectMocks
    FishingTripServiceBean fishingTripService;

    @Mock
    private SpatialModuleService spatialModule;

    @Mock
    JAXBMarshaller marshaller;
    @Mock
    private SpatialModuleService spatialModule;

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


        Map<SearchFilter,String> searchCriteriaMap = new HashMap<>();

        searchCriteriaMap.put(SearchFilter.OWNER, "OWNER1");
        List<AreaIdentifierType> areaIdentifierTypes =new ArrayList<>();

        Map<SearchFilter,List<String>> searchCriteriaMapMultipleValue = new HashMap<>();
        List<String> purposeCodeList= new ArrayList<>();
        purposeCodeList.add("9");
        searchCriteriaMapMultipleValue.put(SearchFilter.PURPOSE,purposeCodeList);

        PaginationDto pagination =new PaginationDto();
        pagination.setPageSize(4);
        pagination.setOffset(1);
        query.setPagination(pagination);
        query.setSearchCriteriaMap(searchCriteriaMap);
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultipleValue);

        when(spatialModule.getFilteredAreaGeom(areaIdentifierTypes)).thenReturn("('MULTIPOINT (10 40, 40 30, 20 20, 30 10)')");

        when(fishingActivityDao.getFishingActivityListByQuery(query)).thenReturn(MapperUtil.getFishingActivityEntityList());

        //Trigger
        FilterFishingActivityReportResultDTO filterFishingActivityReportResultDTO= activityService.getFishingActivityListByQuery(query, null);

        Mockito.verify(fishingActivityDao, Mockito.times(1)).getFishingActivityListByQuery(Mockito.any(FishingActivityQuery.class));
        //Verify
        assertNotNull(filterFishingActivityReportResultDTO);
        assertNotNull(filterFishingActivityReportResultDTO.getResultList());
    }


    @Test
    @SneakyThrows
    public void getFishingActivityListByQuery_emptyResultSet() throws ServiceException {

        FishingActivityQuery query = new FishingActivityQuery();

        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();
        List<AreaIdentifierType> areaIdentifierTypes =new ArrayList<>();
        searchCriteriaMap.put(SearchFilter.OWNER, "OWNER1");

        Map<SearchFilter,List<String>> searchCriteriaMapMultipleValue = new HashMap<>();
        List<String> purposeCodeList= new ArrayList<>();
        purposeCodeList.add("9");
        searchCriteriaMapMultipleValue.put(SearchFilter.PURPOSE,purposeCodeList);

        PaginationDto pagination =new PaginationDto();
        pagination.setPageSize(4);
        pagination.setOffset(1);
        query.setPagination(pagination);
        query.setSearchCriteriaMap(searchCriteriaMap);
        query.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultipleValue);

        when(spatialModule.getFilteredAreaGeom(areaIdentifierTypes)).thenReturn("('MULTIPOINT (10 40, 40 30, 20 20, 30 10)')");

        when(fishingActivityDao.getFishingActivityListByQuery(query)).thenReturn(new ArrayList<FishingActivityEntity>());

        //Trigger
        FilterFishingActivityReportResultDTO filterFishingActivityReportResultDTO= activityService.getFishingActivityListByQuery(query, new ArrayList<Dataset>());

        Mockito.verify(fishingActivityDao, Mockito.times(1)).getFishingActivityListByQuery(Mockito.any(FishingActivityQuery.class));
        //Verify
         assertNotNull(filterFishingActivityReportResultDTO);
    }
}
