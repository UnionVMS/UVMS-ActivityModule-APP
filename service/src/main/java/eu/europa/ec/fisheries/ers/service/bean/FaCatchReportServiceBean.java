package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FaCatchDao;
import eu.europa.ec.fisheries.ers.service.FaCatchReportService;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
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

    @Override
    public void getCatchSummaryReport(FishingActivityQuery query) throws ServiceException {
     log.debug("inside getCatchSummaryReport");
       // List<Object[]> list= fishingActivityDao.getCatchSummary();
      /*  FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();

        List<GroupCriteria> groupByFields = new ArrayList<>();
        groupByFields.add(GroupCriteria.DATE_MONTH);
        //  groupByFields.add(GroupCriteria.SIZE_CLASS);
        //groupByFields.add(GroupCriteria.SPECIES);
        groupByFields.add(GroupCriteria.AREA);
        query.setGroupByFields(groupByFields);

        searchCriteriaMap.put(SearchFilter.SOURCE, "FLUX");

        query.setSearchCriteriaMap(searchCriteriaMap);*/

        StringBuilder sqlGenerated = faCatchDao.getFACatchSummaryReportString(query);
    }
}
