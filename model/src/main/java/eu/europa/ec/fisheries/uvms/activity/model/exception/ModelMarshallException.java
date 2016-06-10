/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.activity.model.exception;

/**
 *
 * @author jojoha
 */
public class ModelMarshallException extends ModelMapperException {

    public ModelMarshallException(String message) {
        super(message);
    }

    public ModelMarshallException(String message, Throwable cause) {
        super(message, cause);
    }

}
