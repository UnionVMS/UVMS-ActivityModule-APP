package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

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
    public void getFishingActivityDao(FishingActivityQuery query){

        LOG.info("inside getFishingActivityDao");
        fishingActivityDao.getFishingActivityListByQuery(query);
    }



}
