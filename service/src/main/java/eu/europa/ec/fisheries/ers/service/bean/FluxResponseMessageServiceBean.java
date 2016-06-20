package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by padhyad on 5/13/2016.
 */
@Stateless
@Transactional
@Slf4j
public class FluxResponseMessageServiceBean implements FluxResponseMessageService {

    @PersistenceContext(unitName = "activityPU")
    private EntityManager em;

    private FaReportDocumentDao faReportDocumentDao;

    @PostConstruct
    public void init() {
        faReportDocumentDao = new FaReportDocumentDao(em);
    }

    /**
     * This Service saves Fishing activity report received into Activity Database.
     * It receives a list of FAReportDocuments which is converted into FaReportDocumentEntity
     * and all the other Entities that it has relation with before saving into database.
     *
     * @param faReportDocuments
     * @throws ServiceException
     */
    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void saveFishingActivityReportDocuments(List<FAReportDocument> faReportDocuments) throws ServiceException {
        List<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>();
        for (FAReportDocument faReportDocument : faReportDocuments) {
            FaReportDocumentEntity entity = FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, new FaReportDocumentEntity());
            faReportDocumentEntities.add(entity);
        }
        faReportDocumentDao.bulkUploadFaData(faReportDocumentEntities);
    }
}
