package eu.europa.ec.fisheries.ers.service;

import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;

import java.io.OutputStream;
import java.util.List;

public interface JasperReportService {

    void generateLogBookReport(String tripId, List<FaReportDocumentEntity> faReportDocumentEntities, OutputStream destination) throws ServiceException;
}
