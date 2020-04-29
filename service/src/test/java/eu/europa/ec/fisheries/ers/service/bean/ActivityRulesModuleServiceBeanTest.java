package eu.europa.ec.fisheries.ers.service.bean;


import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.Destination;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import eu.europa.ec.fisheries.ers.service.exception.ActivityModuleException;
import eu.europa.ec.fisheries.uvms.activity.message.consumer.bean.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.ActivityRulesProducerBean;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleMethod;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.CreateAndSendFAQueryRequest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.PluginType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionPermissionAnswer;
import eu.europa.ec.fisheries.wsdl.subscription.module.SubscriptionPermissionResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQuery;
/**
 * Test class for ActivityRulesModuleServiceBean
 */
@RunWith(MockitoJUnitRunner.class)
public class ActivityRulesModuleServiceBeanTest {

    @Mock
    private ActivityRulesProducerBean rulesProducerBean;

    @Mock
    private ActivitySubscriptionPermissionChecker permissionChecker;

    @Mock
    private ActivityConsumerBean activityConsumerBean;

    @InjectMocks
    ActivityRulesModuleServiceBean sut;

    @Test
    public void testComposeAndSendVesselUpdateFaQueryToRulesWithValidRequest() throws ActivityModuleException, DatatypeConfigurationException, MessageException {
        SubscriptionPermissionResponse subscriptionPermissionResponse = new SubscriptionPermissionResponse();
        subscriptionPermissionResponse.setSubscriptionCheck(SubscriptionPermissionAnswer.YES);
        when(permissionChecker.checkPermissionForFaQuery(any(FAQuery.class))).thenReturn(subscriptionPermissionResponse);
        when(activityConsumerBean.getDestination()).thenReturn(new Destination() {});
        CreateAndSendFAQueryRequest request = new CreateAndSendFAQueryRequest(ActivityModuleMethod.CREATE_AND_SEND_FA_QUERY, PluginType.FLUX,
                "12345678", VesselIdentifierSchemeIdEnum.CFR.toString(), true, DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-01-01T12:00:00"), DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-02-01T12:00:00"), "XEU.XEU", "durn:un:unece:uncefact:fisheries:FLUX:FA:EU:2");
        sut.composeAndSendVesselUpdateFaQueryToRules(request);
        verify(rulesProducerBean).sendModuleMessage(anyString(), any());
    }

    @Test(expected = ActivityModuleException.class)
    public void testComposeAndSendVesselUpdateFaQueryToRulesWithMissingDataflow() throws ActivityModuleException, DatatypeConfigurationException {
        SubscriptionPermissionResponse subscriptionPermissionResponse = new SubscriptionPermissionResponse();
        subscriptionPermissionResponse.setSubscriptionCheck(SubscriptionPermissionAnswer.YES);
        when(permissionChecker.checkPermissionForFaQuery(any(FAQuery.class))).thenReturn(subscriptionPermissionResponse);
        when(activityConsumerBean.getDestination()).thenReturn(new Destination() {});
        CreateAndSendFAQueryRequest request = new CreateAndSendFAQueryRequest(ActivityModuleMethod.CREATE_AND_SEND_FA_QUERY, PluginType.FLUX,
                "12345678", VesselIdentifierSchemeIdEnum.CFR.toString(), true, DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-01-01T12:00:00"),
                DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-02-01T12:00:00"), "XEU.XEU", "");
        sut.composeAndSendVesselUpdateFaQueryToRules(request);
    }

    @Test(expected = ActivityModuleException.class)
    public void testComposeAndSendVesselUpdateFaQueryToRulesReceiveNoPermission() throws ActivityModuleException, DatatypeConfigurationException {
        SubscriptionPermissionResponse subscriptionPermissionResponse = new SubscriptionPermissionResponse();
        subscriptionPermissionResponse.setSubscriptionCheck(SubscriptionPermissionAnswer.NO);
        when(permissionChecker.checkPermissionForFaQuery(any(FAQuery.class))).thenReturn(subscriptionPermissionResponse);
        when(activityConsumerBean.getDestination()).thenReturn(new Destination() {});
        CreateAndSendFAQueryRequest request = new CreateAndSendFAQueryRequest(ActivityModuleMethod.CREATE_AND_SEND_FA_QUERY, PluginType.FLUX,
                "12345678", VesselIdentifierSchemeIdEnum.CFR.toString(), true, DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-01-01T12:00:00"),
                DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-02-01T12:00:00"), "XEU.XEU", "durn:un:unece:uncefact:fisheries:FLUX:FA:EU:2");
        sut.composeAndSendVesselUpdateFaQueryToRules(request);
    }
}
