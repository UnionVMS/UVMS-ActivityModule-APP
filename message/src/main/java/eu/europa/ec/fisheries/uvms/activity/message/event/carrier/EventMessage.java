/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.message.event.carrier;

import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityFault;

import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleMethod;
import javax.jms.TextMessage;

/**
 **/
public class EventMessage {

    private TextMessage jmsMessage;
    private String errorMessage;
    private ActivityFault fault;
    private ActivityModuleMethod method;

    public EventMessage(TextMessage jmsMessage) {
        this.jmsMessage = jmsMessage;
    }

    public EventMessage(TextMessage jmsMessage, ActivityModuleMethod method) {
        this.jmsMessage = jmsMessage;
        this.method = method;
    }

    public EventMessage(TextMessage jmsMessage, String errorMessage) {
        this.jmsMessage = jmsMessage;
        this.errorMessage = errorMessage;
    }

    public EventMessage(TextMessage jmsMessage, ActivityFault falut) {
        this.jmsMessage = jmsMessage;
        this.fault = falut;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public TextMessage getJmsMessage() {
        return jmsMessage;
    }
    public void setJmsMessage(TextMessage jmsMessage) {
        this.jmsMessage = jmsMessage;
    }
    public ActivityFault getFault() {
        return fault;
    }
    public ActivityModuleMethod getMethod() {
        return method;
    }
    public void setMethod(ActivityModuleMethod method) {
        this.method = method;
    }
}