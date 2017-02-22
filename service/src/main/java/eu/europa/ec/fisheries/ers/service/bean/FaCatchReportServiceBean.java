package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FaCatchDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity;
import eu.europa.ec.fisheries.ers.service.FaCatchReportService;
import eu.europa.ec.fisheries.ers.service.facatch.FACatchSummaryHelper;
import eu.europa.ec.fisheries.ers.service.mapper.FACatchSummaryMapper;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryRecordDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.SummaryTableDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportResponse;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
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
     *  This method groups FACatch Data, performs business logic to create summary report
     *  and creates FacatchSummaryDTO object as expected by web interface.
     * @param query
     * @return
     * @throws ServiceException
     */
    @Override
    public FACatchSummaryDTO getCatchSummaryReportForWeb(FishingActivityQuery query) throws ServiceException{
        // get grouped data
        Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupedData = faCatchDao.getGroupedFaCatchData(query);

        // post process data to create Summary table part of Catch summary Report
        FACatchSummaryHelper faCatchSummaryHelper = FACatchSummaryHelper.createFACatchSummaryHelper();
        List<FACatchSummaryRecordDTO> catchSummaryList= faCatchSummaryHelper.buildFACatchSummaryRecordDTOList(groupedData);

        // Post process data to calculate Totals for each column
        SummaryTableDTO summaryTableDTOTotal=  faCatchSummaryHelper.populateSummaryTableWithTotal(catchSummaryList);

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
        FACatchSummaryDTO faCatchSummaryDTO= getCatchSummaryReportForWeb(query);
        log.debug("FACatchSummaryDTO created");

        // We can not transfter DTO as it is over JMS because of JAVA maps.so, Map DTO to the type transferrable over JMS
        FACatchSummaryHelper faCatchSummaryHelper = FACatchSummaryHelper.createFACatchSummaryHelper();

        // Create response object
        FACatchSummaryReportResponse faCatchSummaryReportResponse =new FACatchSummaryReportResponse();
        faCatchSummaryReportResponse.setSummaryRecords(faCatchSummaryHelper.buildFACatchSummaryRecordList(faCatchSummaryDTO.getRecordDTOs()));
        faCatchSummaryReportResponse.setTotal(FACatchSummaryMapper.INSTANCE.mapToSummaryTable(faCatchSummaryDTO.getTotal()));
        log.debug("SummaryTable created and mapped to response");

        log.debug("SummaryTable XML Schema response---->"+faCatchSummaryHelper.printJsonstructure(faCatchSummaryReportResponse));

        return faCatchSummaryReportResponse;
    }
}
