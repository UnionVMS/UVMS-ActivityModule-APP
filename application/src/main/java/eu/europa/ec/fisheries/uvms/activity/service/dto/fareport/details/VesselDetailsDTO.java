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

package eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details;

import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FlapDocumentDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.StorageDto;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.CFR;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.EXT_MARK;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.GFCM;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.ICCAT;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.IRCS;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.UVI;
import static org.apache.commons.lang.StringUtils.isEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class VesselDetailsDTO {

    @JsonbTransient
    private Integer id;

    @JsonbProperty("role")
    private String roleCode;

    private String name;

    private String country;

    @JsonbProperty("contactParties")
    private Set<ContactPartyDetailsDTO> contactPartyDetailsDTOSet;

    @JsonbProperty("vesselIds")
    private Set<AssetIdentifierDto> vesselIdentifiers;

    @JsonbProperty("storage")
    private StorageDto storageDto;

    @JsonbProperty("authorizations")
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

    public void enrichVesselIdentifiersFromAsset(AssetDTO assetDTO) {
        if (this.vesselIdentifiers == null) {
            this.vesselIdentifiers = new HashSet<>();
        }
        if (assetDTO != null) {
            addVesselIdentifierIfMissing(this.vesselIdentifiers, CFR, assetDTO.getCfr());
            addVesselIdentifierIfMissing(this.vesselIdentifiers, EXT_MARK, assetDTO.getExternalMarking());
            addVesselIdentifierIfMissing(this.vesselIdentifiers, GFCM, assetDTO.getGfcm());
            addVesselIdentifierIfMissing(this.vesselIdentifiers, ICCAT, assetDTO.getIccat());
            addVesselIdentifierIfMissing(this.vesselIdentifiers, IRCS, assetDTO.getIrcs());
            addVesselIdentifierIfMissing(this.vesselIdentifiers, UVI, assetDTO.getUvi());
        }
    }

    private void addVesselIdentifierIfMissing(Set<AssetIdentifierDto> vesselIdentifiers, VesselIdentifierSchemeIdEnum idScheme, String idValue) {
        if (isEmpty(idValue)) {
            return;
        }

        AssetIdentifierDto assetId = null;
        final Iterator<AssetIdentifierDto> assetIdIterator = vesselIdentifiers.iterator();
        while (assetIdIterator.hasNext() && assetId == null) {
            AssetIdentifierDto tempAssetId = assetIdIterator.next();
            if (tempAssetId.getIdentifierSchemeId() == idScheme) {
                assetId = tempAssetId;
            }
        }
        if (assetId == null) {
            assetId = new AssetIdentifierDto(idScheme);
            vesselIdentifiers.add(assetId);
        }
        if (isEmpty(assetId.getFaIdentifierId())) {
            assetId.setFaIdentifierId(idValue);
            assetId.setFromAssets(true);
        }
    }

    public Set<FlapDocumentDto> getFlapDocuments() {
        return flapDocuments;
    }

    public void setFlapDocuments(Set<FlapDocumentDto> flapDocuments) {
        this.flapDocuments = flapDocuments;
    }
}
