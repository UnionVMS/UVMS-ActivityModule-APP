/*
 *
 *  Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2020.
 *
 *  This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 *  and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 *  the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 *  details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.service.dto.DateRangeDto;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FishingActivityService {
    /**
     * @param identifierIds These ids assumed to be of type {@code FluxReportIdentifierEntity.fluxReportIdentifierSchemeId}:{@code FluxReportIdentifierEntity.fluxReportIdentifierId}
     *                  for example 'UUID:3af68135-9265-4377-9610-39849b04f0f4'
     * @param assetGuid Asset guid
     * @return List of movement guids
     * @throws ServiceException
     */
    List<String> findMovementGuidsByIdentifierIdsAndAssetGuid(List<String> identifierIds,String assetGuid) throws ServiceException;

    /**
     * Find start and end end of the report based on its activities.
     *
     * @param faReportDocumentEntity The report
     * @return The range
     */
    DateRangeDto findStartAndEndDate(FaReportDocumentEntity faReportDocumentEntity);

    /**
     * Find the first non-empty date from the given delimited periods.
     *
     * @param delimitedPeriods The delimited periods, tolerates null
     * @return The first date or {@code null}
     */
    Date getFirstDateFromDelimitedPeriods(Collection<DelimitedPeriodEntity> delimitedPeriods);
}
