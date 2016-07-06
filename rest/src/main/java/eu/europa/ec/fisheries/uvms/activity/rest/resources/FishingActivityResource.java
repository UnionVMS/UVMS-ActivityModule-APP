package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import eu.europa.ec.fisheries.ers.service.bean.FluxMessageService;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.fluxfareportmessage._1.FLUXFAReportMessage;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * Created by padhyad on 7/6/2016.
 */
@Path("/fa")
@Slf4j
@Stateless
public class FishingActivityResource extends UnionVMSResource {

    @EJB
    private FluxMessageService fluxResponseMessageService;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/save")
    public Response saveFaReportDocument() throws Exception {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("fa_flux_message.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(FLUXFAReportMessage.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        FLUXFAReportMessage fluxfaReportMessage = (FLUXFAReportMessage) jaxbUnmarshaller.unmarshal(is);
        fluxResponseMessageService.saveFishingActivityReportDocuments(fluxfaReportMessage.getFAReportDocuments());
        return createSuccessResponse();
    }
}
