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
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityMapper;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FishingActivityReportDTO;
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
import java.util.List;

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
    public List<FishingActivityReportDTO> getFishingActivityListByQuery(FishingActivityQuery query){

        List<FishingActivityReportDTO> dtos = new ArrayList<FishingActivityReportDTO>();
        try {
            List<FishingActivityEntity> activityList = fishingActivityDao.getFishingActivityListByQuery(query);
            dtos = FishingActivityMapper.INSTANCE.mapToFishingActivityReportDTOList(activityList);
        } catch (ServiceException e) {
            LOG.error("Exception when trying to get Fishing Activity Report data.",e);
        }
       return dtos;
    }

    public List<FishingActivityReportDTO> getFishingActivityList(){

        List<FishingActivityEntity> fishingActivityList =fishingActivityDao.getFishingActivityList();
        List<FishingActivityReportDTO> dtos = FishingActivityMapper.INSTANCE.mapToFishingActivityReportDTOList(fishingActivityList);

        return dtos;
    }


}