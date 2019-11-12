/*
 * ﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
 * © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.activity.service.dto.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.ers.activity.service.dto.view.parent.FishingActivityView;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityHistoryDtoElement implements Comparable<ActivityHistoryDtoElement> {

    @JsonView(FishingActivityView.CommonView.class)
    private Integer faReportId;

    @JsonView(FishingActivityView.CommonView.class)
    private Integer purposeCode;

    @JsonView(FishingActivityView.CommonView.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.DATE_TIME_UI_FORMAT)
    private Date acceptanceDate;

    @JsonView(FishingActivityView.CommonView.class)
    private List<Integer> fishingActivityIds;

    public ActivityHistoryDtoElement(Integer faReportId, Date acceptanceDate, Integer purposeCode, List<Integer> fishingActivityIds) {
        this.fishingActivityIds = fishingActivityIds;
        this.faReportId = faReportId;
        this.acceptanceDate = acceptanceDate;
        this.purposeCode = purposeCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityHistoryDtoElement that = (ActivityHistoryDtoElement) o;
        return Objects.equals(acceptanceDate, that.acceptanceDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acceptanceDate);
    }

    @Override
    public int compareTo(ActivityHistoryDtoElement o) {
        if(getAcceptanceDate() != null && o != null){
            return getAcceptanceDate().compareTo(o.getAcceptanceDate());
        }
        return 0;
    }
}
