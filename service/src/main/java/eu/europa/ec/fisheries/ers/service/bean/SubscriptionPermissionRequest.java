package eu.europa.ec.fisheries.ers.service.bean;

import javax.xml.bind.annotation.XmlRootElement;

import eu.europa.ec.fisheries.uvms.activity.model.schemas.ForwardReportToSubscriptionRequest;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionBaseRequest;

@XmlRootElement
public class SubscriptionPermissionRequest extends SubscriptionBaseRequest {

    private ForwardReportToSubscriptionRequest request;

    public SubscriptionPermissionRequest() {
    }

    public SubscriptionPermissionRequest(ForwardReportToSubscriptionRequest request) {
        this.request = request;
    }

    public ForwardReportToSubscriptionRequest getRequest() {
        return this.request;
    }

    public void setRequest(ForwardReportToSubscriptionRequest request) {
        this.request = request;
    }
}
