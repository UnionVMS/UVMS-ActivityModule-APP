/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.ers.service.dto.view;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.CommonView;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@JsonInclude(NON_EMPTY)
@EqualsAndHashCode
@AllArgsConstructor(suppressConstructorProperties = true)
@RequiredArgsConstructor(suppressConstructorProperties = true)
@NoArgsConstructor
@ToString
public class IdentifierDto {

    @JsonProperty("id")
    @JsonView(CommonView.class)
    private String identifierId;

    @JsonProperty("schemeId")
    @JsonView(CommonView.class)
    @NonNull
    private VesselIdentifierSchemeIdEnum identifierSchemeId;

    private boolean fromAssets;

    public String getIdentifierId() {
        return identifierId;
    }

    public void setIdentifierId(String identifierId) {
        this.identifierId = identifierId;
    }

    public boolean getFromAssets() {
        return fromAssets;
    }

    public void setFromAssets(boolean fromAssets) {
        this.fromAssets = fromAssets;
    }

    public VesselIdentifierSchemeIdEnum getIdentifierSchemeId() {
        return identifierSchemeId;
    }

    public void setIdentifierSchemeId(VesselIdentifierSchemeIdEnum identifierSchemeId) {
        this.identifierSchemeId = identifierSchemeId;
    }

}
