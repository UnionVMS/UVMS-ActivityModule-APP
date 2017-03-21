/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.ers.service.dto.fareport.details;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.CFR;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.EXT_MARK;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.IRCS;
import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.europa.ec.fisheries.ers.service.dto.view.IdentifierDto;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;

@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class VesselDetailsDTO {

    @JsonIgnore
    private Integer id;

    @JsonProperty("role")
    private String roleCode;

    private String name;

    private String country;

    @JsonProperty("contactParties")
    private Set<ContactPartyDetailsDTO> contactPartyDetailsDTOSet;

    @JsonProperty("vesselIds")
    private Set<IdentifierDto> vesselIdentifiers;

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Set<IdentifierDto> getVesselIdentifiers() {
        return vesselIdentifiers;
    }

    public void setVesselIdentifiers(Set<IdentifierDto> vesselIdentifiers) {
        this.vesselIdentifiers = vesselIdentifiers;
    }

    public Set<ContactPartyDetailsDTO> getContactPartyDetailsDTOSet() {
        return contactPartyDetailsDTOSet;
    }

    public void setContactPartyDetailsDTOSet(Set<ContactPartyDetailsDTO> contactPartyDetailsDTOSet) {
        this.contactPartyDetailsDTOSet = contactPartyDetailsDTOSet;
    }

    public boolean hasEmptyIdentifierValues() {

        if (!CollectionUtils.isEmpty(vesselIdentifiers)) {
            for (IdentifierDto identifier : vesselIdentifiers) {
                if (isEmpty(identifier.getIdentifierId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void enrichIdentifiers(Asset asset) {

        String ircs = asset.getIrcs();
        String cfr = asset.getCfr();
        String externalMarking = asset.getExternalMarking();

        for (IdentifierDto identifier : vesselIdentifiers) {

            if (isEmpty(identifier.getIdentifierId())) {

                VesselIdentifierSchemeIdEnum identifierSchemeId = identifier.getIdentifierSchemeId();

                if (CFR.equals(identifierSchemeId) && !isEmpty(cfr)) {
                    setIdentifier(identifier, cfr);
                } else if (EXT_MARK.equals(identifierSchemeId) && !isEmpty(externalMarking)) {
                    setIdentifier(identifier, externalMarking);
                } else if (IRCS.equals(identifierSchemeId) && !isEmpty(ircs)) {
                    setIdentifier(identifier, ircs);
                }
            }
        }
    }

    private void setIdentifier(IdentifierDto identifier, String value) {
        identifier.setIdentifierId(value);
        identifier.setFromAssets(true);
    }
}
