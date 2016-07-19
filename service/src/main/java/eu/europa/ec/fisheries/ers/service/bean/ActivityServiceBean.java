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
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
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
        LOG.info("inside  init creating FishingActivityDao");
        fishingActivityDao = new FishingActivityDao(em);
    }

    final static Logger LOG = LoggerFactory.getLogger(ActivityServiceBean.class);
    public void getFishingActivityListByQuery(FishingActivityQuery query){
        LOG.info("inside  getFishingActivityDao:");
        try {
            List<FishingActivityEntity> activityList = fishingActivityDao.getFishingActivityListByQuery(query);
            LOG.info("activityList size :"+activityList.size());
            for(FishingActivityEntity entity: activityList){
                entity.getFaReportDocument();
                LOG.info("entity :"+entity);
            }

            LOG.info("show all fishing Activities:");

        } catch (ServiceException e) {
            LOG.error("Exception when trying to get Fishing Activity Report data.",e);
        }

    }

    public void getFishingActivityList(){

        List<FishingActivityEntity> fishingActivityList =fishingActivityDao.getFishingActivityList();

        for(FishingActivityEntity entity: fishingActivityList){
            LOG.info("entity:"+entity);
            LOG.info("FaReportDocument:"+entity.getFaReportDocument());
            LOG.info("DelimitedPeriods:"+((entity.getDelimitedPeriods()!=null)?entity.getDelimitedPeriods().size() : null));
            LOG.info("FaCatchs:"+((entity.getFaCatchs()!=null)?entity.getFaCatchs().size() : null));
            LOG.info("FishingGears:"+((entity.getFishingGears()!=null)?entity.getFishingGears().size() : null));
            LOG.info("FluxLocations:"+((entity.getFluxLocations()!=null)?entity.getFluxLocations().size() : null));

           LOG.info("----------------------------------------------------");
        }
    }



}