package eu.europa.ec.fisheries.ers.service.bean;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import un.unece.uncefact.data.standard.fluxfareportmessage._1.FLUXFAReportMessage;

import javax.ejb.EJB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * Created by padhyad on 5/19/2016.
 */
@RunWith(Arquillian.class)
public class FluxMessageServiceIT extends BaseActivityArquillianTest {

    @EJB
    private FluxMessageService fluxResponseMessageService;

    @Test
    public void shouldSaveFaReportDocument() throws Exception {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("fa_flux_message.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(FLUXFAReportMessage.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        FLUXFAReportMessage fluxfaReportMessage = (FLUXFAReportMessage) jaxbUnmarshaller.unmarshal(is);
        fluxResponseMessageService.saveFishingActivityReportDocuments(fluxfaReportMessage.getFAReportDocuments());
    }
}
