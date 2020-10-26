package eu.europa.ec.fisheries.ers.service.bean;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.schema.rules.module.v1.RulesBaseRequest;
import eu.europa.ec.fisheries.schema.rules.module.v1.SetFLUXFAReportMessageRequest;
//import eu.europa.ec.fisheries.uvms.rules.entity.FADocumentID;
//import eu.europa.ec.fisheries.uvms.rules.service.business.AbstractFact;
//import eu.europa.ec.fisheries.uvms.rules.service.business.ValidationResult;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

@XmlRootElement
public class PermissionData extends RulesBaseRequest {

    private boolean isRequestPermitted;
    private String rawMsgGuid;
//    private ValidationResult faReportValidationResult;
    private FLUXFAReportMessage fluxfaReportMessage;
//    private Collection<AbstractFact> faReportFacts;
    private List<IDType> messageGUID;
    private SetFLUXFAReportMessageRequest request;
    private List<String> faIdsPerTripsFromMessage;
//    private Set<FADocumentID> idsFromIncomingMessage;

    public PermissionData() {
    }

    public boolean isRequestPermitted() {
        return isRequestPermitted;
    }

    public void setRequestPermitted(boolean requestPermitted) {
        isRequestPermitted = requestPermitted;
    }

    public String getRawMsgGuid() {
        return rawMsgGuid;
    }

    public void setRawMsgGuid(String rawMsgGuid) {
        this.rawMsgGuid = rawMsgGuid;
    }

//    public ValidationResult getFaReportValidationResult() {
//        return faReportValidationResult;
//    }
//
//    public void setFaReportValidationResult(ValidationResult faReportValidationResult) {
//        this.faReportValidationResult = faReportValidationResult;
//    }

    public FLUXFAReportMessage getFluxfaReportMessage() {
        return fluxfaReportMessage;
    }

    public void setFluxfaReportMessage(FLUXFAReportMessage fluxfaReportMessage) {
        this.fluxfaReportMessage = fluxfaReportMessage;
    }

//    public Collection<AbstractFact> getFaReportFacts() {
//        return faReportFacts;
//    }
//
//    public void setFaReportFacts(Collection<AbstractFact> faReportFacts) {
//        this.faReportFacts = faReportFacts;
//    }

    public List<IDType> getMessageGUID() {
        return messageGUID;
    }

    public void setMessageGUID(List<IDType> messageGUID) {
        this.messageGUID = messageGUID;
    }

    public SetFLUXFAReportMessageRequest getRequest() {
        return request;
    }

    public void setRequest(SetFLUXFAReportMessageRequest request) {
        this.request = request;
    }

    public List<String> getFaIdsPerTripsFromMessage() {
        return faIdsPerTripsFromMessage;
    }

    public void setFaIdsPerTripsFromMessage(List<String> faIdsPerTripsFromMessage) {
        this.faIdsPerTripsFromMessage = faIdsPerTripsFromMessage;
    }

//    public Set<FADocumentID> getIdsFromIncomingMessage() {
//        return idsFromIncomingMessage;
//    }
//
//    public void setIdsFromIncomingMessage(Set<FADocumentID> idsFromIncomingMessage) {
//        this.idsFromIncomingMessage = idsFromIncomingMessage;
//    }
}
