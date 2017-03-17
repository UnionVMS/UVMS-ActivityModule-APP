/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.service.EventService;
import eu.europa.ec.fisheries.ers.service.FaCatchReportService;
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.FluxMessageService;
import eu.europa.ec.fisheries.ers.service.mapper.FishingActivityRequestMapper;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityMessageServiceBean;
import eu.europa.ec.fisheries.uvms.activity.message.event.ActivityMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFACatchSummaryReportEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFLUXFAReportMessageEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.GetFishingTripListEvent;
import eu.europa.ec.fisheries.uvms.activity.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.ActivityModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.FaultCode;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

@LocalBean
@Stateless
@Slf4j
public class ActivityEventServiceBean implements EventService {

    @Inject
    @ActivityMessageErrorEvent
    Event<EventMessage> errorEvent;

    @EJB
    private FluxMessageService fluxMessageService;

    @EJB
    private FishingTripService fishingTripService;

    @EJB
    private FaCatchReportService faCatchReportService;

    @EJB
    private ActivityMessageServiceBean producer;

    @Override
    public void getFLUXFAReportMessage(@Observes @GetFLUXFAReportMessageEvent EventMessage message) {
        log.info("inside Activity module GetFLUXFAReportMessage");
        try {
            SetFLUXFAReportMessageRequest baseRequest = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetFLUXFAReportMessageRequest.class);
            log.info("ActivityModuleRequest unmarshalled");
            if(baseRequest==null){
                log.error("Unmarshalled SetFLUXFAReportMessageRequest is null. Something went wrong");
                return;
            }
            FLUXFAReportMessage fluxFAReportMessage = extractFLUXFAReportMessage(baseRequest.getRequest());
            fluxMessageService.saveFishingActivityReportDocuments(fluxFAReportMessage, extractPluginType(baseRequest.getPluginType()));

        } catch (ActivityModelMarshallException | ServiceException e) {
            sendError(message, e);
        }
    }

    @Override
    public void getFishingTripList(@Observes @GetFishingTripListEvent EventMessage message) throws ServiceException {
        log.info("Got JMS inside Activity to get FishingTripIds:");
        try {
            log.debug("JMS Incoming text message: {}", message.getJmsMessage().getText());
            FishingTripRequest baseRequest = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), FishingTripRequest.class);

            log.debug("FishingTriId Request Unmarshalled");
            FishingTripResponse baseResponse = fishingTripService.getFishingTripIdsForFilter(FishingActivityRequestMapper.buildFishingActivityQueryFromRequest(baseRequest));

            String response = JAXBMarshaller.marshallJaxBObjectToString(baseResponse);
            log.debug("FishingTriId response marshalled");

            producer.sendModuleResponseMessage(message.getJmsMessage(), response, producer.getModuleName());
            log.debug("Response sent back.");
        } catch (ActivityModelMarshallException | JMSException e) {
            sendError(message, e);
        }
    }


    @Override
    public void getFACatchSummaryReport(@Observes @GetFACatchSummaryReportEvent EventMessage message) throws ServiceException {
        log.info("Got JMS inside Activity to get FACatchSummaryReport:");
        try {
            log.debug("JMS Incoming text message: {}", message.getJmsMessage().getText());
            FACatchSummaryReportRequest baseRequest = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), FACatchSummaryReportRequest.class);
            FACatchSummaryReportResponse faCatchSummaryReportResponse= faCatchReportService.getFACatchSummaryReportResponse(FishingActivityRequestMapper.buildFishingActivityQueryFromRequest(baseRequest));
            String response = JAXBMarshaller.marshallJaxBObjectToString(faCatchSummaryReportResponse);
            producer.sendModuleResponseMessage(message.getJmsMessage(), response, producer.getModuleName());
        } catch (ActivityModelMarshallException | JMSException e) {
            sendError(message, e);
        }

    }


    private FaReportSourceEnum extractPluginType(PluginType pluginType) {
        if(pluginType == null){
            return FaReportSourceEnum.FLUX;
        }
        return pluginType == PluginType.FLUX ? FaReportSourceEnum.FLUX : FaReportSourceEnum.MANUAL;
    }

    public FLUXFAReportMessage extractFLUXFAReportMessage(String request) throws ActivityModelMarshallException{
        JAXBContext jc;
        FLUXFAReportMessage fluxFAReportMessage;
        try {
            jc = JAXBContext.newInstance(FLUXFAReportMessage.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            StringReader sr = new StringReader(request);
            StreamSource source = new StreamSource(sr);
             fluxFAReportMessage = (FLUXFAReportMessage) unmarshaller.unmarshal(source);
        } catch (JAXBException | NullPointerException e) {
            throw new ActivityModelMarshallException("[Exception while trying to unmarshall FLUXFAReportMessage in Activity ]", e);
        }
       return fluxFAReportMessage;
    }

    private void sendError(EventMessage message, Exception e) {
        log.error("[ Error in spatial module. ] ", e);
        errorEvent.fire(new EventMessage(message.getJmsMessage(), ActivityModuleResponseMapper.createFaultMessage(FaultCode.ACTIVITY_MESSAGE, "Exception in activity [ " + e.getMessage())));
    }
}