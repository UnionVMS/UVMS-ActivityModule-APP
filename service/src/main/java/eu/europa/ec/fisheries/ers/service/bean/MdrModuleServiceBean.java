package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.service.MdrModuleService;
import eu.europa.ec.fisheries.ers.service.ModuleService;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.MdrProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.mdr.model.exception.MdrModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.mdr.model.mapper.MdrModuleMapper;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import un.unece.uncefact.data.standard.mdr.communication.ColumnDataType;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListResponse;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jgroups.conf.ProtocolConfiguration.log;

/**
 * Created by patilva on 06/04/2017.
 */

@Stateless
@Transactional
@Slf4j
public class MdrModuleServiceBean extends ModuleService implements MdrModuleService {

    @EJB
    private MdrProducerBean mdrProducer;

    @EJB
    private ActivityConsumerBean activityConsumer;

    @Override
    public List<String> getAcronymFromMdr(String acronym, String filter, List<String> columns, Integer nrOfResults) throws ServiceException {
        List<String> codeMap = new ArrayList<>();
        try {
            String request = MdrModuleMapper.createFluxMdrGetCodeListRequest(acronym, filter, columns, nrOfResults);
            String correlationId = mdrProducer.sendModuleMessage(request, activityConsumer.getDestination());
            TextMessage message = activityConsumer.getMessage(correlationId, TextMessage.class);
            if (null != message) {
                String messageStr = message.getText();
                MdrGetCodeListResponse response = JAXBMarshaller.unmarshallTextMessage(messageStr, MdrGetCodeListResponse.class);
                for (ObjectRepresentation objectRep : response.getDataSets()) {
                    for (ColumnDataType nameVal : objectRep.getFields()) {
                        if (StringUtils.equals(nameVal.getColumnName(), "code")) {
                            codeMap.add(nameVal.getColumnValue());
                        }
                    }
                }
               return codeMap;
            }
            else{
                throw new ServiceException("Unable to get data from MDR Module");
            }
        } catch (JMSException|MessageException | MdrModelMarshallException |ActivityModelMarshallException e) {
            log.error("MdrModelMarshallException in communication with mdr", e.getCause());
            throw new ServiceException("Eception caught in mdrModuleServiceBean", e.getCause());
        }
       // return codeMap;
    }
}
