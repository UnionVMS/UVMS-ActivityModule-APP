/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.service.mapper.*;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.dto.*;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details.FaReportDocumentDetailsDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.ReportDTO;
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
import java.util.*;

/**
 * Created by sanera on 29/06/2016.
 */
@Stateless
@Local(ActivityService.class)
@Transactional
@Slf4j
public class ActivityServiceBean implements ActivityService {


    @PersistenceContext(unitName = "activityPU")
    private EntityManager em;

    private FaReportDocumentDao faReportDocumentDao;

    private FishingActivityDao fishingActivityDao;

    @PostConstruct
    public void init() {
        fishingActivityDao = new FishingActivityDao(em);
        faReportDocumentDao = new FaReportDocumentDao(em);
    }

    final static Logger LOG = LoggerFactory.getLogger(ActivityServiceBean.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FaReportCorrectionDTO> getFaReportCorrections(String selectedFaReportId) throws ServiceException {
        List<FaReportDocumentEntity> faReportDocumentEntities = getReferencedFaReportDocuments(selectedFaReportId);
        List<FaReportCorrectionDTO> faReportCorrectionDTOs = FaReportDocumentMapper.INSTANCE.mapToFaReportCorrectionDtoList(faReportDocumentEntities);
        if (!faReportCorrectionDTOs.isEmpty()) {
            log.info("Sort collection by date if the list is not empty");
            Collections.sort(faReportCorrectionDTOs);
        }
        return faReportCorrectionDTOs;
    }

    private List<FaReportDocumentEntity> getReferencedFaReportDocuments(String referenceId) throws ServiceException {
        if (referenceId == null) {
            return Collections.emptyList();
        }
        log.info("Find reference fishing activity report for  : " + referenceId);
        List<FaReportDocumentEntity> allFaReportDocuments = new ArrayList<>();
        List<FaReportDocumentEntity> faReportDocumentEntities = faReportDocumentDao.findFaReportsByReferenceId(referenceId);
        allFaReportDocuments.addAll(faReportDocumentEntities);
        for (FaReportDocumentEntity faReportDocumentEntity : faReportDocumentEntities) {  //Find all the referenced FA Report recursively
            allFaReportDocuments.addAll(getReferencedFaReportDocuments(faReportDocumentEntity.getFluxReportDocument().getFluxReportDocumentId()));
        }
        return allFaReportDocuments;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public FaReportDocumentDetailsDTO getFaReportDocumentDetails(String faReportDocumentId) throws ServiceException {
        log.info("Find Fa Report document for report Id : " + faReportDocumentId);
        List<FaReportDocumentEntity> faReportDocumentEntities = faReportDocumentDao.findFaReportsByIds(Arrays.asList(faReportDocumentId));
        if (faReportDocumentEntities == null || faReportDocumentEntities.isEmpty()) {
            throw new ServiceException("Report Does not Exist");
        }
        FaReportDocumentEntity faReportDocumentEntity = faReportDocumentEntities.get(0);
        log.info("Map first element from the list to DTO");
        return FaReportDocumentMapper.INSTANCE.mapToFaReportDocumentDetailsDTO(faReportDocumentEntity);
    }

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
                FluxReportDocumentEntity fluxReportDocument =faReportDocumentEntity.getFluxReportDocument();
                if(fluxReportDocument !=null ) {
                    reportDTO.setUniqueReportId(fluxReportDocument.getFluxReportDocumentId());
                    boolean isCorrection =true;
                    if(fluxReportDocument.getReferenceId() ==null || fluxReportDocument.getReferenceId().length()==0)
                        isCorrection =false;
                    reportDTO.setCorrection(isCorrection);
                }
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