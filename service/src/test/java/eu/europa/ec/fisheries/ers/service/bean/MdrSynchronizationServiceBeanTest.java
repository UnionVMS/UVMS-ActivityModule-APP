package eu.europa.ec.fisheries.ers.service.bean;

import java.util.Hashtable;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


import eu.europa.ec.fisheries.ers.message.producer.MdrMessageProducer;
import eu.europa.ec.fisheries.ers.message.producer.bean.MdrMessageProducerBean;
import eu.europa.ec.fisheries.mdr.service.MdrSynchronizationService;
import eu.europa.ec.fisheries.mdr.service.bean.MdrRepository;
import eu.europa.ec.fisheries.mdr.service.bean.MdrSynchronizationServiceBean;
import eu.europa.ec.fisheries.uvms.activity.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.activity.message.constants.ModuleQueue;
import lombok.SneakyThrows;

@RunWith(MockitoJUnitRunner.class)
public class MdrSynchronizationServiceBeanTest {

	@Mock
	private MdrMessageProducer producer = new MdrMessageProducerBean();

	@Mock
	private MdrRepository mdrRepository;

	@InjectMocks
	private MdrSynchronizationService syncBean = new MdrSynchronizationServiceBean();

	/* Only for testing JMS message sending */

	@Resource(lookup = MessageConstants.CONNECTION_FACTORY)
	@Mock
	private ConnectionFactory connectionFactory;

	private Connection connection;

	@Resource(mappedName = MessageConstants.ACTIVITY_MESSAGE_IN_QUEUE)
	private Queue responseQueue;

	@Resource(mappedName = MessageConstants.EXCHANGE_MODULE_QUEUE)
	private Queue exchangeQueue;

	// private String someMessage = "SomeMessage";

	@Before
	@SneakyThrows
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		// someMessage = "Message222";
	}

	@Test
	@SneakyThrows
	public void textSendMessageToExchangeModule() {
		// Mockito.when(producer.sendExchangeModuleMessage((Mockito.any(Class.class)).thenReturn(returnList);
		syncBean.manualStartMdrSynchronization();
		// Mockito.verify(producer).sendExchangeModuleMessage(someMessage);

	}

	@Test
	@SneakyThrows
	public void sendMessageToExchengeQueue() {
		producer.sendModuleMessage("HelloTest", ModuleQueue.EXCHANGE);
	}

	@Test
	@SneakyThrows
	public void testMessageSendingToExchange() {

		if (connection == null) {
			try {
				connection = connectionFactory.createConnection();
				connection.start();
			} catch (JMSException ex) {
			}
		}
		Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
		TextMessage message = session.createTextMessage();
		message.setJMSReplyTo(responseQueue);
		message.setText("Some Rando Message");

		MessageProducer producer = session.createProducer(exchangeQueue);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		producer.setTimeToLive(60000L);

		producer.send(message);

	}

	@SuppressWarnings("unchecked")
	@Test
	@SneakyThrows
	public void sendMessagesToExchangeQueue() {

		String queueName = "jms/UVMSExchangeEvent";
		String queueConnectionFactoryName = "jms/UVMSExchangeEvent";
		Context jndiContext = null;
		QueueConnectionFactory queueConnectionFactory = null;
		QueueConnection queueConnection = null;
		QueueSession queueSession = null;
		Queue queue = null;
		QueueSender queueSender = null;
		TextMessage message = null;
		int noMessages = 5;

		/*
		 * Set the environment for a connection to the OC4J instance
		 */
		@SuppressWarnings("rawtypes")
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
		env.put(Context.SECURITY_PRINCIPAL, "admin");
		env.put(Context.SECURITY_CREDENTIALS, "admin");
		env.put(Context.PROVIDER_URL, "http://localhost:8161");

		/*
		 * Set the Context Object. Lookup the Queue Connection Factory. Lookup
		 * the JMS Destination.
		 */
		try {
			jndiContext = new InitialContext(env);
			queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup(queueConnectionFactoryName);
			queue = (Queue) jndiContext.lookup(queueName);
		} catch (NamingException e) {
			System.out.println("JNDI lookup failed: " + e.toString());
		}

		/*
		 * Create connection. Create session from connection. Create sender.
		 * Create text message. Send messages. Send non text message to end text
		 * messages. Close connection.
		 */
		try {
			queueConnection = queueConnectionFactory.createQueueConnection();
			queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			queueSender = queueSession.createSender(queue);
			message = queueSession.createTextMessage();
			for (int i = 0; i < noMessages; i++) {
				message.setText("Message " + (i + 1));
				System.out.println("Producing message: " + message.getText());
				queueSender.send(message);
			}
			queueSender.send(queueSession.createBytesMessage());
			;
		} catch (JMSException e) {
			System.out.println("Exception occurred: " + e.toString());
		} finally {
			if (queueConnection != null) {
				try {
					queueConnection.close();
				} catch (JMSException e) {
					System.out.println("Closing error: " + e.toString());
				}
			}
		}
	}

}
