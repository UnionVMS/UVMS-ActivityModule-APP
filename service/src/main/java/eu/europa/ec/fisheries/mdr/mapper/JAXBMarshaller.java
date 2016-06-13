package eu.europa.ec.fisheries.mdr.mapper;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.exception.ModelMarshallException;


public class JAXBMarshaller {
	
	static final Logger LOG = LoggerFactory.getLogger(JAXBMarshaller.class);
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<String, JAXBContext> contexts = new HashMap();

	@SuppressWarnings("unchecked")
	public static <T> String marshallJaxBObjectToString(T data) throws ModelMarshallException {
		try {
			JAXBContext ex = (JAXBContext) contexts.get(data.getClass().getName());
			if (ex == null) {
				long marshaller = System.currentTimeMillis();
				ex = JAXBContext.newInstance(new Class[] { data.getClass() });
				contexts.put(data.getClass().getName(), ex);
				LOG.debug("Stored contexts: {}", Integer.valueOf(contexts.size()));
				LOG.debug("JAXBContext creation time: {}", Long.valueOf(System.currentTimeMillis() - marshaller));
			}

			Marshaller marshaller1 = ex.createMarshaller();
			marshaller1.setProperty("jaxb.formatted.output", Boolean.valueOf(true));
			StringWriter sw = new StringWriter();	
			JAXBElement<T> element = new JAXBElement<T>(new QName("uri","local"), (Class<T>) data.getClass(), data);
			marshaller1.marshal(element, sw);
			long before = System.currentTimeMillis();
			String marshalled = sw.toString();
			LOG.debug("StringWriter time: {}", Long.valueOf(System.currentTimeMillis() - before));
			return marshalled;
		} catch (JAXBException arg6) {
			LOG.error("[ Error when marshalling object to string ] {} ", arg6.getMessage());
			throw new ModelMarshallException("[ Error when marshalling Object to String ]", arg6);
		}
	}

	@SuppressWarnings("unchecked")
	public static <R> R unmarshallTextMessage(TextMessage textMessage, @SuppressWarnings("rawtypes") Class clazz) throws ModelMarshallException {
		try {
			JAXBContext ex = (JAXBContext) contexts.get(clazz.getName());
			if (ex == null) {
				long unmarshaller = System.currentTimeMillis();
				ex = JAXBContext.newInstance(new Class[] { clazz });
				contexts.put(clazz.getName(), ex);
				LOG.debug("Stored contexts: {}", Integer.valueOf(contexts.size()));
				LOG.debug("JAXBContext creation time: {}", Long.valueOf(System.currentTimeMillis() - unmarshaller));
			}

			Unmarshaller unmarshaller1 = ex.createUnmarshaller();
			StringReader sr = new StringReader(textMessage.getText());
			StreamSource source = new StreamSource(sr);
			long before = System.currentTimeMillis();
			Object object = unmarshaller1.unmarshal(source);
			LOG.debug("Unmarshalling time: {}", Long.valueOf(System.currentTimeMillis() - before));
			return (R) object;
		} catch (JMSException | JAXBException | NullPointerException arg8) {
			throw new ModelMarshallException("[Error when unmarshalling response in ResponseMapper ]", arg8);
		}
	}
}
