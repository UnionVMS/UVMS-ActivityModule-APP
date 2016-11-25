package eu.europa.ec.fisheries.mdr.exception;

/**
 * Created by kovian on 30/08/2016.
 */
public class ActivityStatusTableException extends Exception  {
    public ActivityStatusTableException() {
        super();
    }

    public ActivityStatusTableException(String message) {
        super(message);
    }

    public ActivityStatusTableException(String message, Throwable cause) {
        super(message, cause);
    }
}
