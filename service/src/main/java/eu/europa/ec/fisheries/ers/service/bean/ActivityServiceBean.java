/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.service.mapper.*;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by sanera on 29/06/2016.
 */
@Stateless
@Local(ActivityService.class)
@Transactional
@Slf4j
public class ActivityServiceBean implements ActivityService{

    private FishingActivityDao fishingActivityDao;

    @PersistenceContext(unitName = "activityPU")
    private EntityManager em;

    @PostConstruct
    public void init() {
        fishingActivityDao = new FishingActivityDao(em);
    }

    final static Logger LOG = LoggerFactory.getLogger(ActivityServiceBean.class);

    @Override
    public List<FishingActivityReportDTO> getFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException {
        List<FishingActivityEntity> activityList;
        if(query.getSearchCriteria() ==null || query.getSearchCriteria().size()==0){
            activityList =  fishingActivityDao.getFishingActivityList(query.getPagination());
        }else{
            activityList = fishingActivityDao.getFishingActivityListByQuery(query);
        }


        if(activityList==null || activityList.isEmpty()){
            LOG.info("Could not find FishingActivity entities matching search criteria");
            return Collections.emptyList();
        }

        List<FishingActivityReportDTO> dtos = FishingActivityMapper.INSTANCE.mapToFishingActivityReportDTOList(activityList);

       return dtos;
    }



    public List<ReportDTO> getFishingActivityReportForFishingTrip(String fishingTripId) throws ServiceException {

        List<FishingActivityEntity> fishingActivityList=fishingActivityDao.getFishingActivityListForFishingTrip(fishingTripId,null);

        if(fishingActivityList.size()==0)
            return Collections.emptyList();

        List<ReportDTO> reportDTOList=new ArrayList<>();
        for(FishingActivityEntity activityEntity:fishingActivityList){

            ReportDTO reportDTO=new ReportDTO();
            reportDTO.setFishingActivityId(activityEntity.getId());
            reportDTO.setActivityType(activityEntity.getTypeCode());

            FaReportDocumentEntity faReportDocumentEntity= activityEntity.getFaReportDocument();
            if(faReportDocumentEntity !=null) {
                reportDTO.setFaReportDocumentType(faReportDocumentEntity.getTypeCode());
                reportDTO.setFaReportAcceptedDateTime(faReportDocumentEntity.getAcceptedDatetime());
                if(faReportDocumentEntity.getFluxReportDocument() !=null )
                    reportDTO.setUniqueReportId(faReportDocumentEntity.getFluxReportDocument().getFluxReportDocumentId());
            }

            reportDTO.setOccurence(activityEntity.getOccurence());
            reportDTO.setReason(activityEntity.getReasonCode());


            List<DelimitedPeriodDTO> delimitedPeriodDTOEntities=new ArrayList<>();
            for(DelimitedPeriodEntity dp:activityEntity.getDelimitedPeriods()){
                delimitedPeriodDTOEntities.add(DelimitedPeriodMapper.INSTANCE.mapToDelimitedPeriodDTO(dp));
            }
            reportDTO.setDelimitedPeriod(delimitedPeriodDTOEntities);

            Set<FluxLocationEntity> fluxLocations= activityEntity.getFluxLocations();
            List<FluxLocationDTO> fluxLocationsDTOList=new ArrayList<>();
            for(FluxLocationEntity fluxLoc :fluxLocations ) {
                fluxLocationsDTOList.add(FluxLocationMapper.INSTANCE.mapToFluxLocationDTO(fluxLoc));
            }
            reportDTO.setFluxLocations(fluxLocationsDTOList);


            List<FluxCharacteristicsDTO> fluxCharacteristicsDTOList=new ArrayList<>();
            for(FluxCharacteristicEntity fluxCharacteristic:activityEntity.getFluxCharacteristics()){
                fluxCharacteristicsDTOList.add(FluxCharacteristicsMapper.INSTANCE.mapToFluxCharacteristicsDTO(fluxCharacteristic));
            }
            reportDTO.setFluxCharacteristics(fluxCharacteristicsDTOList);

            List<FishingGearDTO> fishingGearDTOList=new ArrayList<>();
            for(FishingGearEntity fishingGear:activityEntity.getFishingGears()){
               fishingGearDTOList.add(FishingGearMapper.INSTANCE.mapToFishingGearDTO(fishingGear));
            }
            reportDTO.setFishingGears(fishingGearDTOList);
            reportDTOList.add(reportDTO);
        }

        return reportDTOList;
    }


}