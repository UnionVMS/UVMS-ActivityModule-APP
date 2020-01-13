/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.uvms.activity.service.dto.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
public class IdentifierDto {

    @JsonProperty("id")
    private String faIdentifierId;

    @JsonProperty("schemeId")
    @NonNull
    private String faIdentifierSchemeId;

    public String getFaIdentifierId() {
        return faIdentifierId;
    }

    public void setFaIdentifierId(String faIdentifierId) {
        this.faIdentifierId = faIdentifierId;
    }

    public String getFaIdentifierSchemeId() {
        return faIdentifierSchemeId;
    }

    public void setFaIdentifierSchemeId(String faIdentifierSchemeId) {
        this.faIdentifierSchemeId = faIdentifierSchemeId;
    }
}
