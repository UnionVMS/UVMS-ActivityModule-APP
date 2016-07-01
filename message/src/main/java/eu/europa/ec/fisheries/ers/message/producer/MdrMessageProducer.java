package eu.europa.ec.fisheries.ers.message.producer;

import javax.ejb.Local;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.ers.message.exception.ActivityMessageException;
import eu.europa.ec.fisheries.uvms.activity.message.constants.ModuleQueue;


@Local
public interface MdrMessageProducer {

	void sendMessageBackToRecipient(TextMessage requestMessage, String returnMessage) throws ActivityMessageException;

	String sendModuleMessage(String text, ModuleQueue queue) throws ActivityMessageException;

	String sendRulesModuleMessage(String strReqObj);
}
