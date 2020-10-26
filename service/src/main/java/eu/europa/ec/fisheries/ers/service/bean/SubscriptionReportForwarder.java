/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2020.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.ers.service.mapper.FluxFaReportMessageMappingContext;

/**
 * Interface for forwarding a report to Subscription module.
 */
public interface SubscriptionReportForwarder {

    /**
     * Handle and forward FluxFaReportMessage data to Subscription module
     *
     * @param ctx
     * @param messageEntity
     */
    void forwardReportToSubscription(FluxFaReportMessageMappingContext ctx, FluxFaReportMessageEntity messageEntity);

    /**
     * Handle and request permission based on the FluxFaReportMessage data
     *
     * @param ctx
     * @param messageEntity
     * @return true or false depending whether the request is to be permitted or not
     */
    boolean requestPermissionFromSubscription(FluxFaReportMessageMappingContext ctx, FluxFaReportMessageEntity messageEntity);

    }
