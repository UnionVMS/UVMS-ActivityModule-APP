package eu.europa.ec.fisheries.uvms.activity.message.constants;

public class MessageConstants {

    public static final String CONNECTION_FACTORY = "java:/ConnectionFactory";
    public static final String CONNECTION_TYPE = "javax.jms.MessageListener";
    public static final String DESTINATION_TYPE_QUEUE = "javax.jms.Queue";

    public static final String ACTIVITY_MESSAGE_IN_QUEUE = "java:/jms/queue/UVMSActivityEvent";
    public static final String COMPONENT_MESSAGE_IN_QUEUE_NAME = "UVMSActivityEvent";

    public static final String EXCHANGE_MODULE_QUEUE = "java:/jms/queue/UVMSExchangeEvent";
    
	public static final String MODULE_NAME = "activity";
}
