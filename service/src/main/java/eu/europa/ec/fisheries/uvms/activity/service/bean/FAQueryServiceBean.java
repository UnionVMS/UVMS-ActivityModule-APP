/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.fa.dao.FaReportDocumentDao;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.service.FaQueryService;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.ActivityEntityToModelMapper;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubCriteriaType;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionDataCriteria;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import java.time.Instant;
import java.util.List;

@Stateless
@Slf4j
public class FAQueryServiceBean extends BaseActivityBean implements FaQueryService {

    private FaReportDocumentDao FAReportDAO;

    @PostConstruct
    public void init() {
        FAReportDAO = new FaReportDocumentDao(entityManager);
    }

    @Override
    public FLUXFAReportMessage getReportsByCriteria(List<SubscriptionDataCriteria> subscriptionDataCriteria) {
        if (CollectionUtils.isNotEmpty(subscriptionDataCriteria)) {
            Instant endDate = null;
            Instant startDate = null;

            for (SubscriptionDataCriteria dataCriteria : subscriptionDataCriteria) {

                SubCriteriaType subCriteria = dataCriteria.getSubCriteria();
                String value = dataCriteria.getValue();

                if (subCriteria == SubCriteriaType.END_DATE) {
                    endDate = Instant.parse(value);
                } else if (subCriteria == SubCriteriaType.START_DATE) {
                    startDate = Instant.parse(value);
                }
            }

            List<FaReportDocumentEntity> faReportDocumentsForTrip = FAReportDAO.loadReports(null, "N", startDate, endDate);
            return ActivityEntityToModelMapper.INSTANCE.mapToFLUXFAReportMessage(faReportDocumentsForTrip);
        }
        return null;
    }
}
