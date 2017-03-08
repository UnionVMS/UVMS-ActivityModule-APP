/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FaCatchDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity;
import eu.europa.ec.fisheries.ers.service.FaCatchReportService;
import eu.europa.ec.fisheries.ers.service.facatch.FACatchSummaryHelper;
import eu.europa.ec.fisheries.ers.service.facatch.FACatchSummaryHelperFactory;
import eu.europa.ec.fisheries.ers.service.mapper.FACatchSummaryMapper;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.dto.fareport.summary.FACatchDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.fareport.summary.FACatchSummaryDTO;
import eu.europa.ec.fisheries.ers.service.dto.fareport.summary.FACatchSummaryRecordDTO;
import eu.europa.ec.fisheries.ers.service.dto.fareport.summary.SummaryTableDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sanera on 17/01/2017.
 */
@Stateless
@Local(FaCatchReportService.class)
@Transactional
@Slf4j
public class FaCatchReportServiceBean implements FaCatchReportService {

    @PersistenceContext(unitName = "activityPU")
    private EntityManager em;

    private FaCatchDao faCatchDao;



    @PostConstruct
    public void init() {
        faCatchDao = new FaCatchDao(em);

    }

    /**
     * This method gets DTO representing catch details screen. It will hve 2 summary tables for catches and landing
     * @param tripId data for this trip should be returned
     * @return
     * @throws ServiceException
     */

    @Override
    public FACatchDetailsDTO getCatchDetailsScreen(String tripId) throws ServiceException {

        FACatchDetailsDTO faCatchDetailsDTO = new FACatchDetailsDTO();
        faCatchDetailsDTO.setCatches(getCatchDetailsScreenTable(tripId,false));
        faCatchDetailsDTO.setLanding(getCatchDetailsScreenTable(tripId,true));
        return faCatchDetailsDTO ;
   }

    /**
     * This method gets you table structure for Catch details summary
     * @param tripId data will be returned for this tripId
     * @param isLanding If landing then summary structure will include PRESENTATION information otherwise only species information will be included
     * @return
     * @throws ServiceException
     */
    public FACatchSummaryDTO getCatchDetailsScreenTable(String tripId,boolean isLanding) throws ServiceException {
        FishingActivityQuery query = new FishingActivityQuery();
        List<GroupCriteria> groupByFields = getGroupByFields(isLanding);
        query.setGroupByFields(groupByFields);

        Map<SearchFilter, String> searchCriteriaMap = new EnumMap<>(SearchFilter.class);
        searchCriteriaMap.put(SearchFilter.TRIP_ID,tripId);
        query.setSearchCriteriaMap(searchCriteriaMap);
        return getCatchSummaryReport(query,isLanding);
    }

    @NotNull
    private List<GroupCriteria> getGroupByFields(boolean isLanding) {
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

        if(isLanding)
             groupByFields.add(GroupCriteria.PRESENTATION);

        return groupByFields;
    }


    /**
     *  This method groups FACatch Data, performs business logic to create summary report
     *  and creates FacatchSummaryDTO object as expected by web interface.
     * @param query
     * @return
     * @throws ServiceException
     */
    @Override
    public FACatchSummaryDTO getCatchSummaryReport(FishingActivityQuery query, boolean isLanding) throws ServiceException{
        // get grouped data
        Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupedData = faCatchDao.getGroupedFaCatchData(query,isLanding);

        // post process data to create Summary table part of Catch summary Report
        FACatchSummaryHelper faCatchSummaryHelper = isLanding? FACatchSummaryHelperFactory.getFACatchSummaryHelper(FACatchSummaryHelperFactory.PRESENTATION):FACatchSummaryHelperFactory.getFACatchSummaryHelper(FACatchSummaryHelperFactory.STANDARD);
        List<FACatchSummaryRecordDTO> catchSummaryList= faCatchSummaryHelper.buildFACatchSummaryRecordDTOList(groupedData);

        // Post process data to calculate Totals for each column
        SummaryTableDTO summaryTableDTOTotal=   faCatchSummaryHelper.populateSummaryTableWithTotal(catchSummaryList);

        // Create DTO object to send back to the web
        FACatchSummaryDTO faCatchSummaryDTO = new FACatchSummaryDTO();
        faCatchSummaryDTO.setRecordDTOs(catchSummaryList);
        faCatchSummaryDTO.setTotal(summaryTableDTOTotal);

        return faCatchSummaryDTO;
    }

    @Override
    public FACatchSummaryReportResponse getFACatchSummaryReportResponse(FishingActivityQuery query) throws ServiceException {
        log.debug("FACatchSummaryReportResponse creation starts");

        //get processed information in the form of DTO
        FACatchSummaryDTO faCatchSummaryDTO= getCatchSummaryReport(query,false);
        log.debug("FACatchSummaryDTO created");

        // We can not transfter DTO as it is over JMS because of JAVA maps.so, Map DTO to the type transferrable over JMS
        FACatchSummaryHelper faCatchSummaryHelper = FACatchSummaryHelperFactory.getFACatchSummaryHelper(FACatchSummaryHelperFactory.STANDARD);

        // Create response object
        FACatchSummaryReportResponse faCatchSummaryReportResponse =new FACatchSummaryReportResponse();
        faCatchSummaryReportResponse.setSummaryRecords(faCatchSummaryHelper.buildFACatchSummaryRecordList(faCatchSummaryDTO.getRecordDTOs()));
        faCatchSummaryReportResponse.setTotal(FACatchSummaryMapper.INSTANCE.mapToSummaryTable(faCatchSummaryDTO.getTotal()));

        log.debug("SummaryTable XML Schema response---->"+FACatchSummaryHelper.printJsonstructure(faCatchSummaryReportResponse));

        return faCatchSummaryReportResponse;
    }



}
