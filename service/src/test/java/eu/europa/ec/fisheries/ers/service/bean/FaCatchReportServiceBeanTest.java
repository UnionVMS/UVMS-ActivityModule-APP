package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FaCatchDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by sanera on 07/03/2017.
 */
public class FaCatchReportServiceBeanTest {


    @Mock
    EntityManager em;

    @Mock
    FaCatchDao faCatchDao;


    @InjectMocks
    FaCatchReportServiceBean faCatchReportService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();


    @Test
    @SneakyThrows
    public void testGetCatchesTableForCatchDetailsScreen() throws ServiceException {
        FishingActivityQuery query = new FishingActivityQuery();
        List<GroupCriteria> groupByFields = new ArrayList<>();
        groupByFields.add(GroupCriteria.DATE_DAY);
        groupByFields.add(GroupCriteria.FAO_AREA);
        groupByFields.add(GroupCriteria.TERRITORY);
        groupByFields.add(GroupCriteria.EFFORT_ZONE);
        groupByFields.add(GroupCriteria.GFCM_GSA);
        groupByFields.add(GroupCriteria.GFCM_STAT_RECTANGLE);
        groupByFields.add(GroupCriteria.ICES_STAT_RECTANGLE);
        groupByFields.add(GroupCriteria.RFMO);
        groupByFields.add(GroupCriteria.SPECIES);
        query.setGroupByFields(groupByFields);
        Map<SearchFilter, String> searchCriteriaMap = new EnumMap<SearchFilter, String>(SearchFilter.class);
        searchCriteriaMap.put(SearchFilter.TRIP_ID,"NOR-TRP-20160517234053706");
        query.setSearchCriteriaMap(searchCriteriaMap);

        when(faCatchDao.getGroupedFaCatchData(query,Boolean.FALSE)).thenReturn(MapperUtil.getGroupedFaCatchSummaryCustomEntityData());
        when(faCatchDao.getGroupedFaCatchData(query,Boolean.TRUE)).thenReturn(MapperUtil.getGroupedFaCatchSummaryCustomEntityData());
     //   when(vesselIdentifiersDao.getLatestVesselIdByTrip("NOR-TRP-20160517234053706")).thenReturn(MapperUtil.getVesselIdentifiersList());

        //Trigger
        FACatchDetailsDTO faCatchDetailsDTO= faCatchReportService.getCatchDetailsScreen("NOR-TRP-20160517234053706");

        Mockito.verify(faCatchDao, Mockito.times(2)).getGroupedFaCatchData(Mockito.any(FishingActivityQuery.class),Mockito.any(Boolean.class));
        //Verify

        assertNotNull( faCatchDetailsDTO);

    }


    @Test
    @SneakyThrows
    public void testGetCatchSummaryReport() throws ServiceException {
        FishingActivityQuery query = new FishingActivityQuery();
        List<GroupCriteria> groupByFields = new ArrayList<>();
        groupByFields.add(GroupCriteria.DATE_DAY);
        groupByFields.add(GroupCriteria.FAO_AREA);
        groupByFields.add(GroupCriteria.TERRITORY);
        groupByFields.add(GroupCriteria.EFFORT_ZONE);
        groupByFields.add(GroupCriteria.GFCM_GSA);
        groupByFields.add(GroupCriteria.GFCM_STAT_RECTANGLE);
        groupByFields.add(GroupCriteria.ICES_STAT_RECTANGLE);
        groupByFields.add(GroupCriteria.RFMO);
        groupByFields.add(GroupCriteria.SPECIES);
        query.setGroupByFields(groupByFields);
        Map<SearchFilter, String> searchCriteriaMap = new EnumMap<SearchFilter, String>(SearchFilter.class);
        searchCriteriaMap.put(SearchFilter.TRIP_ID,"NOR-TRP-20160517234053706");
        query.setSearchCriteriaMap(searchCriteriaMap);
        Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupedData=MapperUtil.getGroupedFaCatchSummaryCustomEntityData();
        when((faCatchDao).getGroupedFaCatchData(any(FishingActivityQuery.class),any(Boolean.class))).thenReturn(groupedData);

   //     when(faCatchDao.getGroupedFaCatchData(query,true)).thenReturn(MapperUtil.getGroupedFaCatchSummaryCustomEntityData());
        //   when(vesselIdentifiersDao.getLatestVesselIdByTrip("NOR-TRP-20160517234053706")).thenReturn(MapperUtil.getVesselIdentifiersList());

        //Trigger
        FACatchSummaryDTO fACatchSummaryDTO= faCatchReportService.getCatchSummaryReport(query,false);

        Mockito.verify(faCatchDao, Mockito.times(1)).getGroupedFaCatchData(Mockito.any(FishingActivityQuery.class),Mockito.any(Boolean.class));
        //Verify
        assertNotNull( fACatchSummaryDTO);
    }


}
