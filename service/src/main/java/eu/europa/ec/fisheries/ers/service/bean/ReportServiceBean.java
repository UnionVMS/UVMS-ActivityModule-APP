package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FishingActivityDao;
import eu.europa.ec.fisheries.ers.service.ReportService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 * Created by sanera on 17/01/2017.
 */
@Stateless
@Local(ReportService.class)
@Transactional
@Slf4j
public class ReportServiceBean implements ReportService {

    @PersistenceContext(unitName = "activityPU")
    private EntityManager em;

    private FishingActivityDao fishingActivityDao;

    @PostConstruct
    public void init() {
        fishingActivityDao = new FishingActivityDao(em);

    }

    public void getCatchSummaryReport(){
     log.debug("inside getCatchSummaryReport");
       // List<Object[]> list= fishingActivityDao.getCatchSummary();
    }
}
