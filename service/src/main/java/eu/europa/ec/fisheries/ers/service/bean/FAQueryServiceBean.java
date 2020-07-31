/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.bean;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import eu.europa.ec.fisheries.ers.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.service.FaQueryService;
import eu.europa.ec.fisheries.ers.service.mapper.ActivityEntityToModelMapper;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubCriteriaType;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataCriteria;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

@Stateless
@Slf4j
public class FAQueryServiceBean implements FaQueryService {

    private static final String FLUX_LOCAL_NATION_CODE = "flux_local_nation_code";

    @PersistenceContext(unitName = "activityPUpostgres")
    private EntityManager postgres;

    @PersistenceContext(unitName = "activityPUoracle")
    private EntityManager oracle;

    private EntityManager em;
    private FaReportDocumentDao FAReportDAO;

    @Inject
    ParameterService parameterService;

    private String localNodeName;

    @PostConstruct
    public void init() {

        String dbDialect = System.getProperty("db.dialect");
        if ("oracle".equalsIgnoreCase(dbDialect)) {
            em = oracle;
        } else {
            em = postgres;
        }
        FAReportDAO = new FaReportDocumentDao(em);
        try {
            localNodeName = parameterService.getParamValueById(FLUX_LOCAL_NATION_CODE);
        } catch (ConfigServiceException e) {
            log.warn("Could not retrieve value for param FLUX_LOCAL_NATION_CODE",e);
        }
    }

    @Override
    public FLUXFAReportMessage getReportsByCriteria(List<SubscriptionDataCriteria> subscriptionDataCriteria) {

        if (CollectionUtils.isNotEmpty(subscriptionDataCriteria)){

            String consolidated = "N";
            String tripID = null;
            String vesselId = null;
            String schemeId = null;
            String endDate = null;
            String startDate = null;

            for (SubscriptionDataCriteria dataCriteria : subscriptionDataCriteria){

                SubCriteriaType subCriteria = dataCriteria.getSubCriteria();
                String valueType = dataCriteria.getValueType().value();
                String value = dataCriteria.getValue();

                if (subCriteria == SubCriteriaType.END_DATE) {
                    endDate = value;
                }

                else if (subCriteria == SubCriteriaType.START_DATE) {
                    startDate = value;
                }

            }

            List<FaReportDocumentEntity> faReportDocumentsForTrip = FAReportDAO.loadReports(tripID, consolidated, vesselId, schemeId, startDate, endDate);
            return ActivityEntityToModelMapper.INSTANCE.mapToFLUXFAReportMessage(faReportDocumentsForTrip, localNodeName);

        }

        return null;
    }
}
