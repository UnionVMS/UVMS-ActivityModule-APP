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
import static eu.europa.ec.fisheries.ers.service.dto.view.parent.FishingActivityView.CommonView;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.CFR;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.EXT_MARK;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.GFCM;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.ICCAT;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.IRCS;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.UVI;
import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.fisheries.ers.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.ers.service.dto.FlapDocumentDto;
import eu.europa.ec.fisheries.ers.service.dto.StorageDto;
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
    private Set<AssetIdentifierDto> vesselIdentifiers;

    @JsonProperty("storage")
    private StorageDto storageDto;

    @JsonView(CommonView.class)
    @JsonProperty("authorizations")
    private Set<FlapDocumentDto> flapDocuments;

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

    public Set<AssetIdentifierDto> getVesselIdentifiers() {
        return vesselIdentifiers;
    }

    public void setVesselIdentifiers(Set<AssetIdentifierDto> vesselIdentifiers) {
        this.vesselIdentifiers = vesselIdentifiers;
    }

    public Set<ContactPartyDetailsDTO> getContactPartyDetailsDTOSet() {
        return contactPartyDetailsDTOSet;
    }

    public void setContactPartyDetailsDTOSet(Set<ContactPartyDetailsDTO> contactPartyDetailsDTOSet) {
        this.contactPartyDetailsDTOSet = contactPartyDetailsDTOSet;
    }

    public StorageDto getStorageDto() {
        return storageDto;
    }

    public void setStorageDto(StorageDto storageDto) {
        this.storageDto = storageDto;
    }

    public boolean hasEmptyIdentifierValues() {

        if (!CollectionUtils.isEmpty(vesselIdentifiers)) {
            for (IdentifierDto identifier : vesselIdentifiers) {
                if (isEmpty(identifier.getFaIdentifierId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void enrichIdentifiers(Asset asset) {
        if(asset!=null) {
            Map<VesselIdentifierSchemeIdEnum, String> assetDetails = new HashMap<>();

            assetDetails.put(IRCS, asset.getIrcs());
            assetDetails.put(CFR, asset.getCfr());
            assetDetails.put(EXT_MARK, asset.getExternalMarking());
            assetDetails.put(UVI, asset.getUvi());
            assetDetails.put(ICCAT, asset.getIccat());
            assetDetails.put(GFCM, asset.getGfcm());

            assetDetails.forEach(this::setIdentifierIfEmpty);
        }
    }

    private void setIdentifierIfEmpty(VesselIdentifierSchemeIdEnum identifierSchemeId, String faIdentifierId){
        if(!isEmpty(faIdentifierId)) {
            Optional<AssetIdentifierDto> optionalIdentifier = vesselIdentifiers.stream()
                    .filter(identifier -> identifier.getIdentifierSchemeId().equals(identifierSchemeId))
                    .findAny();
            if(optionalIdentifier.isPresent()){
                AssetIdentifierDto identifier = optionalIdentifier.get();
                if(isEmpty(identifier.getFaIdentifierId())){
                    setIdentifier(identifier, faIdentifierId);
                }
            } else {
                vesselIdentifiers.add(new AssetIdentifierDto(identifierSchemeId, faIdentifierId));
            }
        }
    }

    private void setIdentifier(AssetIdentifierDto identifier, String value) {
        identifier.setFaIdentifierId(value);
        identifier.setFromAssets(true);
    }

    public Set<FlapDocumentDto> getFlapDocuments() {
        return flapDocuments;
    }

    public void setFlapDocuments(Set<FlapDocumentDto> flapDocuments) {
        this.flapDocuments = flapDocuments;
    }
}
