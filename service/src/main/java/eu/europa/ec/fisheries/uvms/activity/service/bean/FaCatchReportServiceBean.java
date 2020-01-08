/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.fa.dao.FaCatchDao;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.proxy.FaCatchSummaryCustomProxy;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.activity.service.FaCatchReportService;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.summary.FACatchDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.summary.FACatchSummaryDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.summary.FACatchSummaryRecordDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.summary.SummaryTableDTO;
import eu.europa.ec.fisheries.uvms.activity.service.facatch.FACatchSummaryHelper;
import eu.europa.ec.fisheries.uvms.activity.service.facatch.FACatchSummaryPresentationHelper;
import eu.europa.ec.fisheries.uvms.activity.service.facatch.FACatchSummaryReportHelper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FACatchSummaryMapper;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Stateless
@Local(FaCatchReportService.class)
@Transactional
@Slf4j
public class FaCatchReportServiceBean extends BaseActivityBean implements FaCatchReportService {

    private FaCatchDao faCatchDao;

    @PostConstruct
    public void init() {
        faCatchDao = new FaCatchDao(entityManager);
    }

    /**
     * This method gets DTO representing catch details screen. It will hve 2 summary tables for catches and landing
     * @param tripId data for this trip should be returned
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
     *
     * This method gets you table structure for Catch details summary on TRIP SUMMARY VIEW
     * @param tripId data will be returned for this tripId
     * @param isLanding If landing then summary structure will include PRESENTATION information otherwise only species information will be included
     * @throws ServiceException
     */
    public FACatchSummaryDTO getCatchDetailsScreenTable(String tripId, boolean isLanding) throws ServiceException {
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
        groupByFields.add(GroupCriteria.DATE);
        groupByFields.add(GroupCriteria.FAO_AREA);
        groupByFields.add(GroupCriteria.TERRITORY);
        groupByFields.add(GroupCriteria.EFFORT_ZONE);
        groupByFields.add(GroupCriteria.GFCM_GSA);
        groupByFields.add(GroupCriteria.GFCM_STAT_RECTANGLE);
        groupByFields.add(GroupCriteria.ICES_STAT_RECTANGLE);
        groupByFields.add(GroupCriteria.SPECIES);

        if(isLanding)
             groupByFields.add(GroupCriteria.PRESENTATION);

        return groupByFields;
    }


    /**
     *  This method groups FACatch Data, performs business logic to create summary report
     *  and creates FacatchSummaryDTO object as expected by web interface.
     *  isReporting : TRUE indicates that the method should be used to generate result for REPORTING module
     *                FALSE indicates that the method should be used to generate result from TRIP SUMMARY VIEW
     * @param query
     * @throws ServiceException
     */
    @Override
    public FACatchSummaryDTO getCatchSummaryReport(FishingActivityQuery query, boolean isLanding) throws ServiceException{
        // get grouped data
        Map<FaCatchSummaryCustomProxy, List<FaCatchSummaryCustomProxy>> groupedData = faCatchDao.getGroupedFaCatchData(query, isLanding);

        // post process data to create Summary table part of Catch summary Report
        FACatchSummaryHelper faCatchSummaryHelper = isLanding ? new FACatchSummaryPresentationHelper() : new FACatchSummaryReportHelper();
        List<FACatchSummaryRecordDTO> catchSummaryList = faCatchSummaryHelper.buildFACatchSummaryRecordDTOList(groupedData);

        // Post process data to calculate Totals for each column
        SummaryTableDTO summaryTableDTOTotal = faCatchSummaryHelper.populateSummaryTableWithTotal(catchSummaryList);

        // Create DTO object to send back to the web
        FACatchSummaryDTO faCatchSummaryDTO = new FACatchSummaryDTO();
        faCatchSummaryDTO.setRecordDTOs(catchSummaryList);
        faCatchSummaryDTO.setTotal(summaryTableDTOTotal);

        return faCatchSummaryDTO;
    }

    /**
     * This method is used to create Catch Details page from Run Report.
     * So, To display summary  of catches, we need to consider all fishing Activity filters as well as aggregation factors specified by user .
     * Aggregation factors could be dynamically selected by user like veesel,period,area etc.
     * @param query
     * @throws ServiceException
     */
    @Override
    public FACatchSummaryReportResponse getFACatchSummaryReportResponse(FishingActivityQuery query) throws ServiceException {
        log.debug("FACatchSummaryReportResponse creation starts");

        //get processed information in the form of DTO
        FACatchSummaryDTO faCatchSummaryDTO = getCatchSummaryReport(query,false);
        log.debug("FACatchSummaryDTO created");

        // We can not transfter DTO as it is over JMS because of JAVA maps.so, Map DTO to the type transferrable over JMS
        FACatchSummaryHelper faCatchSummaryHelper = new FACatchSummaryReportHelper();

        // Create response object for JMS
        FACatchSummaryReportResponse faCatchSummaryReportResponse = new FACatchSummaryReportResponse();
        faCatchSummaryReportResponse.setSummaryRecords(faCatchSummaryHelper.buildFACatchSummaryRecordList(faCatchSummaryDTO.getRecordDTOs()));
        faCatchSummaryReportResponse.setTotal(FACatchSummaryMapper.INSTANCE.mapToSummaryTable(faCatchSummaryDTO.getTotal()));

        log.debug("SummaryTable XML Schema response---->"+FACatchSummaryHelper.printJsonstructure(faCatchSummaryReportResponse));

        return faCatchSummaryReportResponse;
    }
}
