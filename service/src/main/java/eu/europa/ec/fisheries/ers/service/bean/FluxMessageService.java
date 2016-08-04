/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;

import java.util.List;

/**
 * Created by padhyad on 5/13/2016.
 */
public interface FluxMessageService {

    /**
     * <p>This Service saves Fishing activity report received into Activity DB. It receives a list of <code>FAReportDocuments</code> which
     * is converted into <code>FaReportDocumentEntity</code> before saving. It checks if there is any correction messages received in the message.
     * If there is a correction (e.g. update, cancel, delete) than the original FaReportDocument is marked with the corresponding status.
     *</p>
     *
     * @param faReportDocuments
     * @throws ServiceException
     */
    void saveFishingActivityReportDocuments(List<FAReportDocument> faReportDocuments) throws ServiceException;
}