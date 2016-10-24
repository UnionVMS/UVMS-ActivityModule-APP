/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.fa.utils;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by padhyad on 10/12/2016.
 */
public class JaxbUtil {

    private JaxbUtil() {// Static utility class, not supposed to have instances.
        super();
    }

    public static <T> String marshallJaxBObjectToString(T data) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(new Class[]{data.getClass()});
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(true));
        StringWriter sw = new StringWriter();
        marshaller.marshal(data, sw);
        return sw.toString();
    }

    public static <T> T unmarshallTextMessage(TextMessage textMessage, Class clazz) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(new Class[]{clazz});
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        StringReader sr = null;
        try {
            sr = new StringReader(textMessage.getText());
        } catch (JMSException e) {
           throw new JAXBException(e);
        }
        return (T) unmarshaller.unmarshal(sr);
    }
}
