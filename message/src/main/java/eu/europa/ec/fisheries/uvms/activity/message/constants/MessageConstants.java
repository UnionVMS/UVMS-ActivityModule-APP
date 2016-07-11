/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.message.constants;

public class MessageConstants {

    public static final String CONNECTION_FACTORY = "java:/ConnectionFactory";
    public static final String CONNECTION_TYPE = "javax.jms.MessageListener";
    public static final String DESTINATION_TYPE_QUEUE = "javax.jms.Queue";

    public static final String ACTIVITY_MESSAGE_IN_QUEUE = "java:/jms/queue/UVMSActivityEvent";
    public static final String COMPONENT_MESSAGE_IN_QUEUE_NAME = "UVMSActivityEvent";

    public static final String EXCHANGE_MODULE_QUEUE = "java:/jms/queue/UVMSExchangeEvent";
    
	public static final String MODULE_NAME = "activity";
	public static final String ERS_MDR_QUEUE = "java:/jms/queue/ERSMDRPlugin";
	public static final String RULES_EVENT_QUEUE = "java:/jms/queue/UVMSRulesEvent";
}