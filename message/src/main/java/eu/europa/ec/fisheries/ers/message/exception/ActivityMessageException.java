package eu.europa.ec.fisheries.ers.message.exception;

public class ActivityMessageException extends Exception {
    public ActivityMessageException() {
    }

    public ActivityMessageException(String message) {
        super(message);
    }

    public ActivityMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
