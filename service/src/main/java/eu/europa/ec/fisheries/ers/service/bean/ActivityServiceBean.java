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
import eu.europa.ec.fisheries.ers.service.mapper.DelimitedPeriodMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FluxLocationMapper;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.DelimitedPeriodDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip.FluxLocationDTO;
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


        List<FishingActivityEntity> activityList = fishingActivityDao.getFishingActivityListByQuery(query);
        if(activityList==null || activityList.isEmpty()){
            LOG.info("Could not find FishingActivity entities matching search criteria");
            return Collections.emptyList();
        }

        List<FishingActivityReportDTO> dtos = FishingActivityMapper.INSTANCE.mapToFishingActivityReportDTOList(activityList);

       return dtos;
    }

    @Override
    public List<FishingActivityReportDTO> getFishingActivityList(){

        List<FishingActivityEntity> fishingActivityList =fishingActivityDao.getFishingActivityList();
        if(fishingActivityList==null || fishingActivityList.isEmpty()){
            LOG.info("Could not find FishingActivity entities.");
            return Collections.emptyList();
        }

        return FishingActivityMapper.INSTANCE.mapToFishingActivityReportDTOList(fishingActivityList);


    }

    public void getFishingActivityReportForFishingTrip(){
        LOG.info("inside getFishingActivityReportForFishingTrip:");
        List<FishingActivityEntity> fishingActivityList=fishingActivityDao.getFishingActivityListForFishingTrip("NOR-TRP-20160517234053706",null);

        for(FishingActivityEntity activityEntity:fishingActivityList){
            String faTypeCode =activityEntity.getTypeCode();
            LOG.info("TYPE (Notification/Declaration) ::::"+faTypeCode + "("+activityEntity.getFaReportDocument().getTypeCode()+ ")");
            LOG.info("Date ::::"+activityEntity.getOccurence() );

            Set<FluxLocationEntity> fluxLocations= activityEntity.getFluxLocations();
            StringBuilder location= new StringBuilder();
            for(FluxLocationEntity fluxLoc :fluxLocations ) {
                if (("DEPARTURE".equalsIgnoreCase(faTypeCode) || "ARRIVAL".equalsIgnoreCase(faTypeCode) || "LANDING".equalsIgnoreCase(faTypeCode) ||
                        "TRANSSHIPMENT".equalsIgnoreCase(faTypeCode)) && "LOCATION".equals(fluxLoc.getFluxLocationType()) || "AREA".equals(fluxLoc.getFluxLocationType())) {
                    location.append(fluxLoc.getFluxLocationIdentifierSchemeId() + "-");
                }else{
                    location.append(fluxLoc.getFluxLocationIdentifier()+ " ");
                    location.append(fluxLoc.getLongitude() + " " + fluxLoc.getLatitude() + " ");
                }

                location.append(fluxLoc.getRfmoCode()+" ");
            }

            LOG.info("Location ::::"+location );
            LOG.info("Reason ::::"+activityEntity.getReasonCode() );
            StringBuilder remarks = new StringBuilder();
            if ("DEPARTURE".equals(faTypeCode) || "FISHING_OPERATION".equals(faTypeCode)){
                Set<FishingGearEntity> gears= activityEntity.getFishingGears();
                for(FishingGearEntity fishingGear:gears){
                    remarks.append(fishingGear.getTypeCode()+" ");
                }
            }else if("ARRIVAL".equals(faTypeCode) && "notification".equals(activityEntity.getFaReportDocument().getTypeCode())){
                remarks.append(activityEntity.getFaReportDocument().getAcceptedDatetime());
            }else if("ARRIVAL".equals(faTypeCode) && "declaration".equals(activityEntity.getFaReportDocument().getTypeCode())){
                Set<FluxCharacteristicEntity> fluxCharacteristics=activityEntity.getFluxCharacteristics();
                for(FluxCharacteristicEntity fluxCharacteristic:fluxCharacteristics){
                    remarks.append(fluxCharacteristic.getValueDateTime()+" ");
                }
            }else if("LANDING".equals(faTypeCode) || "TRANSSHIPMENT".equals(faTypeCode)){
                Set<DelimitedPeriodEntity> delimitedPeriodEntities=activityEntity.getDelimitedPeriods();
                for(DelimitedPeriodEntity dp:delimitedPeriodEntities){
                    remarks.append(dp.getEndDate()+" ");
                }
            }else if("ENTRY".equals(faTypeCode) || "EXIT".equals(faTypeCode) || "RELOCATION".equals(faTypeCode)){
                remarks.append(activityEntity.getOccurence());
            }

            LOG.info("Remarks ::::"+remarks );
            List<DelimitedPeriodDTO> delimitedPeriodDTOEntities=new ArrayList<>();
            for(DelimitedPeriodEntity dp:activityEntity.getDelimitedPeriods()){
                delimitedPeriodDTOEntities.add(DelimitedPeriodMapper.INSTANCE.mapToDelimitedPeriodDTO(dp));
            }



            List<FluxLocationDTO> fluxLocationsDTO=new ArrayList<>();

            for(FluxLocationEntity fluxLoc :fluxLocations ) {
                fluxLocationsDTO.add(FluxLocationMapper.INSTANCE.mapToFluxLocationDTO(fluxLoc));
            }
        }
    }


}