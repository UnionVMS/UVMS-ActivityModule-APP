package eu.europa.ec.fisheries.mdr.exception;

/**
 * Created by kovian on 27/07/2016.
 */
public class FieldNotMappedException extends Exception {
    public FieldNotMappedException(String className, String fieldName) {
        super("The field : "+fieldName+" has not been mapped in the Entity : "+className);
    }
}
