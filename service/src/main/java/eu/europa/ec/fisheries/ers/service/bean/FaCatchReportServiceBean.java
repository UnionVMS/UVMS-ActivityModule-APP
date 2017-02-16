package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FaCatchDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity;
import eu.europa.ec.fisheries.ers.service.FaCatchReportService;
import eu.europa.ec.fisheries.ers.service.helper.FACatchSummaryHelper;
import eu.europa.ec.fisheries.ers.service.mapper.FACatchSummaryMapper;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.FACatchSummaryRecordDTO;
import eu.europa.ec.fisheries.uvms.activity.model.dto.facatch.SummaryTableDTO;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryRecord;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FACatchSummaryReportResponse;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SummaryTable;
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

    public FACatchSummaryDTO getCatchSummaryReportForWeb(FishingActivityQuery query) throws ServiceException{
        FACatchSummaryHelper faCatchSummaryHelper = FACatchSummaryHelper.createFACatchSummaryHelper();
        Map<FaCatchSummaryCustomEntity,List<FaCatchSummaryCustomEntity>> groupedData = faCatchDao.getGroupedFaCatchData(query);
        List<FACatchSummaryRecordDTO> catchSummaryList= faCatchSummaryHelper.buildFaCatchSummaryTable(groupedData);
        SummaryTableDTO summaryTableDTOTotal=  faCatchSummaryHelper.populateSummaryTableWithTotal(catchSummaryList);
        FACatchSummaryDTO faCatchSummaryDTO = new FACatchSummaryDTO();
        faCatchSummaryDTO.setRecordDTOs(catchSummaryList);
        faCatchSummaryDTO.setTotal(summaryTableDTOTotal);

        return faCatchSummaryDTO;
    }

    @Override
    public FACatchSummaryReportResponse getFACatchSummaryReportResponse(FishingActivityQuery query) throws ServiceException {
     log.debug("inside getCatchSummaryReport");

        FACatchSummaryDTO faCatchSummaryDTO= getCatchSummaryReportForWeb(query);

       FACatchSummaryHelper faCatchSummaryHelper = FACatchSummaryHelper.createFACatchSummaryHelper();
        List<FACatchSummaryRecord> faCatchSummaryRecords= faCatchSummaryHelper.buildFACatchSummaryRecordList(faCatchSummaryDTO.getRecordDTOs());
        SummaryTable summaryTable= FACatchSummaryMapper.INSTANCE.mapToSummaryTable(faCatchSummaryDTO.getTotal());


        FACatchSummaryReportResponse faCatchSummaryReportResponse =new FACatchSummaryReportResponse();
        faCatchSummaryReportResponse.setSummaryRecords(faCatchSummaryRecords);
        faCatchSummaryReportResponse.setTotal(summaryTable);

        log.debug("SummaryTable XML Schema response---->");
        faCatchSummaryHelper.printJsonstructure(faCatchSummaryReportResponse);
        return faCatchSummaryReportResponse;
    }
}
