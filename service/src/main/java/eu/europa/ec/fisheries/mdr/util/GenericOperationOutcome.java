/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.
This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
*/
package eu.europa.ec.fisheries.mdr.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kovian on 30/08/2016.
 */
public class GenericOperationOutcome {

    private OperationOutcome status       = OperationOutcome.OK;
    private Map<Integer, String> messages = new HashMap<>();
    private int counter                   = 0;
    private Object includedObject         = null;

    public GenericOperationOutcome(){}

    public GenericOperationOutcome(OperationOutcome status){
        this.status = status;
    }

    public GenericOperationOutcome(OperationOutcome status, String message){
        this.status = status;
        messages.put(counter++, message);
    }

    public void addMessage(String message){
        messages.put(counter++, message);
    }

    public void clear(){
        setStatus(OperationOutcome.OK);
        setMessages(new HashMap<Integer, String>());
        setCounter(0);
        setIncludedObject(null);
    }

    public boolean isOK(){
        return status.equals(OperationOutcome.OK);
    }

    public OperationOutcome getStatus() {
        return status;
    }
    public void setStatus(OperationOutcome status) {
        this.status = status;
    }
    public Map<Integer, String> getMessages() {
        return messages;
    }
    public void setMessages(Map<Integer, String> messages) {
        this.messages = messages;
    }
    public int getCounter() {
        return counter;
    }
    public void setCounter(int counter) {
        this.counter = counter;
    }
    public Object getIncludedObject() {
        return includedObject;
    }
    public void setIncludedObject(Object includedObject) {
        this.includedObject = includedObject;
    }

}
