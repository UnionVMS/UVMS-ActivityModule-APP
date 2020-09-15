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
import eu.europa.ec.fisheries.uvms.activity.service.FluxMessageService;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.EfrToFluxMapper;
import lombok.extern.slf4j.Slf4j;
import se.havochvatten.efr.efropenapi.model.FishingReport;

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

    public void saveEfrReport(String shouldBeReportClass) {
        log.info("saveEfrReport - shouldBeReportClass: {}", shouldBeReportClass);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            FishingReport efrReport = jsonb.fromJson(shouldBeReportClass, FishingReport.class);

            FluxFaReportMessageEntity reportInFluxFormat = efrToFluxMapper.efrFishingReportToFluxMessageEntity(efrReport);

            fluxMessageService.saveFishingActivityReportDocuments(reportInFluxFormat);

            exchangeProducerBean.sendEfrReportSavedAckToExchange(efrReport.getFishingReportId());
        } catch (JMSException e) {
            log.error("Error when trying to send ACK message indicating that an EFR fishing report was successfully saved.", e);
        } catch (JsonbException e) {
            log.error("Failed to convert incoming message to FishingReport", e);
        } catch (Exception e) {
            log.error("Error when trying to save EFR fishing report.", e);
        }
    }
}


