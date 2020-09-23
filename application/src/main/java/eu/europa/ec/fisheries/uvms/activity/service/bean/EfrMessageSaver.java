/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.activity.message.producer.ExchangeProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.efr.activities.BaseEfrActivity;
import eu.europa.ec.fisheries.uvms.activity.model.efr.activities.PriorNotificationEfrActivity;
import eu.europa.ec.fisheries.uvms.activity.service.FluxMessageService;
import eu.europa.ec.fisheries.uvms.activity.service.dto.efrbackend.FishingReport;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.EfrToFluxMapper;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;

@Slf4j
@Stateless
@LocalBean
public class EfrMessageSaver {

    @Inject
    private EfrToFluxMapper efrToFluxMapper;

    @Inject
    private FluxMessageService fluxMessageService;

    @Inject
    private ExchangeProducerBean exchangeProducerBean;

    public void handleEfrActivity(String shouldBeEfrActivity) {
        log.info("handleEfrActivity - Raw string: {}", shouldBeEfrActivity);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            BaseEfrActivity baseActivity = jsonb.fromJson(shouldBeEfrActivity, BaseEfrActivity.class);

            log.debug("handleEfrActivity - Activity type: {}", baseActivity.getActivityType());

            FluxFaReportMessageEntity fluxMessage;

            switch(baseActivity.getActivityType()) {
                case PRIOR_NOTIFICATION:
                    PriorNotificationEfrActivity priorNotificationActivity = jsonb.fromJson(shouldBeEfrActivity, PriorNotificationEfrActivity.class);
                    fluxMessage = efrToFluxMapper.map(priorNotificationActivity);
                    break;
                // TODO expand with more types of activity
                default:
                    log.error("Unknown type for EFR activity message: {}", baseActivity.getActivityType());
                    return;
            }

            fluxMessageService.saveFishingActivityReportDocuments(fluxMessage);
            exchangeProducerBean.sendEfrActivitySavedAckToExchange(baseActivity.getActivityMessageId());

        } catch (JMSException e) {
            log.error("Error when trying to send ACK message indicating that an EFR fishing report was successfully saved.", e);
        } catch (JsonbException e) {
            log.error("Failed to convert incoming message to FishingReport", e);
        } catch (Exception e) {
            log.error("Error when trying to save EFR fishing report.", e);
        }
    }

    public void saveEfrReport(String shouldBeReportClass) {
        log.info("saveEfrReport - shouldBeReportClass: {}", shouldBeReportClass);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            FishingReport efrReport = jsonb.fromJson(shouldBeReportClass, FishingReport.class);

            FluxFaReportMessageEntity reportInFluxFormat = efrToFluxMapper.efrFishingReportToFluxMessageEntity(efrReport);

            fluxMessageService.saveFishingActivityReportDocuments(reportInFluxFormat);

            exchangeProducerBean.sendEfrActivitySavedAckToExchange(efrReport.getFishingReportId());
        } catch (JMSException e) {
            log.error("Error when trying to send ACK message indicating that an EFR fishing report was successfully saved.", e);
        } catch (JsonbException e) {
            log.error("Failed to convert incoming message to FishingReport", e);
        } catch (Exception e) {
            log.error("Error when trying to save EFR fishing report.", e);
        }
    }
}


