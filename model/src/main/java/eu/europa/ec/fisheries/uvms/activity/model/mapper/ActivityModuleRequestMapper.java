package eu.europa.ec.fisheries.uvms.activity.model.mapper;

import eu.europa.ec.fisheries.uvms.activity.model.exception.ModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleMethod;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SetFLUXFAReportMessageRequest;

/**
 * Created by sanera on 06/06/2016.
 */
public class ActivityModuleRequestMapper {

    public ActivityModuleRequestMapper(){

    }

    public static String mapToSetFLUXFAReportMessageRequest(String fluxFAReportMessae, String username) throws ModelMarshallException {
        SetFLUXFAReportMessageRequest request = new SetFLUXFAReportMessageRequest();
        request.setMethod(ActivityModuleMethod.GET_FLUX_FA_REPORT);
        request.setRequest(fluxFAReportMessae);

        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
}
