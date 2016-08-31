package eu.europa.ec.fisheries.mdr.exception;

/**
 * Created by kovian on 31/08/2016.
 */
public class ActivityMappingException extends Throwable {
    public ActivityMappingException(String message, Throwable ex) {
        super(message, ex);
    }
    public ActivityMappingException(String message) {
        super(message);
    }
    public ActivityMappingException(Throwable ex) {
        super(ex);
    }
}
