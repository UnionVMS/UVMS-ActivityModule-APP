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
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusEnum;
import eu.europa.ec.fisheries.ers.service.FluxMessageService;
import eu.europa.ec.fisheries.ers.service.mapper.FaReportDocumentMapper;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by padhyad on 5/13/2016.
 */
@Stateless
@Transactional
@Slf4j
public class FluxMessageServiceBean implements FluxMessageService {

    @PersistenceContext(unitName = "activityPU")
    private EntityManager em;

    private FaReportDocumentDao faReportDocumentDao;

    @PostConstruct
    public void init() {
        faReportDocumentDao = new FaReportDocumentDao(em);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void saveFishingActivityReportDocuments(List<FAReportDocument> faReportDocuments, FaReportSourceEnum faReportSourceEnum) throws ServiceException {
        List<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>();
        for (FAReportDocument faReportDocument : faReportDocuments) {
            FaReportDocumentEntity entity = FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, new FaReportDocumentEntity(), faReportSourceEnum);
            faReportDocumentEntities.add(entity);
            log.debug("fishing activity records to be saved : " + entity.getFluxReportDocument().getId());
        }
        log.info("Insert fishing activity records into DB");
        faReportDocumentDao.bulkUploadFaData(faReportDocumentEntities);
        updateFaReportCorrections(faReportDocuments);
    }

    /**
     * If there is a reference Id exist for any of the FaReport Document, than it means this is an update to an existing report.
     */
    private void updateFaReportCorrections(List<FAReportDocument> faReportDocuments) throws ServiceException {
        List<FaReportDocumentEntity> faReportDocumentEntities = new ArrayList<>();
        for (FAReportDocument faReportDocument : faReportDocuments) {
            if (faReportDocument.getRelatedFLUXReportDocument().getReferencedID() != null &&
                    faReportDocument.getRelatedFLUXReportDocument().getPurposeCode() != null) {
                FaReportDocumentEntity faReportDocumentEntity = faReportDocumentDao.findFaReportByIdAndScheme(
                        faReportDocument.getRelatedFLUXReportDocument().getReferencedID().getValue(),
                        faReportDocument.getRelatedFLUXReportDocument().getReferencedID().getSchemeID()); // Find the existing report by using reference Id and scheme Id
                if (faReportDocumentEntity != null) { // If found then update the entity with respect to purpose code in the Report
                    FaReportStatusEnum faReportStatusEnum = FaReportStatusEnum.getFaReportStatusEnum(Integer.parseInt(faReportDocument.getRelatedFLUXReportDocument().getPurposeCode().getValue()));
                    faReportDocumentEntity.setStatus(faReportStatusEnum.getStatus());
                    faReportDocumentEntities.add(faReportDocumentEntity);
                }
            }
            faReportDocumentDao.updateAllFaData(faReportDocumentEntities); // Update all the Entities together
        }
    }
}