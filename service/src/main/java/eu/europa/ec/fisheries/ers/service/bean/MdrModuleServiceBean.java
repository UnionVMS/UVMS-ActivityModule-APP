package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.service.MdrModuleService;
import eu.europa.ec.fisheries.ers.service.ModuleService;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.MdrProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.mdr.model.exception.MdrModelMarshallException;
import eu.europa.ec.fisheries.uvms.mdr.model.mapper.MdrModuleMapper;
import lombok.extern.slf4j.Slf4j;
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
    public Map<String, List<String>> getAcronymFromMdr(String acronym, String filter, List<String> filterColumns, Integer nrOfResults, String... returnColumns) throws ServiceException {
        Map<String, List<String>> columnNameValuesMap = prepareColumnNameValuesMap(returnColumns);
        try {
            String request = MdrModuleMapper.createFluxMdrGetCodeListRequest(acronym, filter, filterColumns, nrOfResults);
            String correlationId = mdrProducer.sendModuleMessage(request, activityConsumer.getDestination());
            TextMessage message = activityConsumer.getMessage(correlationId, TextMessage.class);
            if (null != message) {
                String messageStr = message.getText();
                MdrGetCodeListResponse response = JAXBMarshaller.unmarshallTextMessage(messageStr, MdrGetCodeListResponse.class);
                for (ObjectRepresentation objectRep : response.getDataSets()) {
                    for (ColumnDataType nameVal : objectRep.getFields()) {
                        if (columnNameValuesMap.containsKey(nameVal.getColumnName())) {
                            columnNameValuesMap.get(nameVal.getColumnName()).add(nameVal.getColumnValue());
                        }
                    }
                }
                return columnNameValuesMap;
            } else {
                throw new ServiceException("Unable to get data from MDR Module");
            }
        } catch (JMSException | MessageException | MdrModelMarshallException | ActivityModelMarshallException e) {
            log.error("MdrModelMarshallException in communication with mdr", e.getCause());
            throw new ServiceException("Exception caught in mdrModuleServiceBean", e.getCause());
        }
    }

    private Map<String, List<String>> prepareColumnNameValuesMap(String[] returnColumns) {
        Map<String, List<String>> columnNameValuesMap = new HashMap<>(returnColumns.length);

        for (String columnName : returnColumns) {
            columnNameValuesMap.put(columnName, new ArrayList<String>());
        }

        return columnNameValuesMap;
    }
}
