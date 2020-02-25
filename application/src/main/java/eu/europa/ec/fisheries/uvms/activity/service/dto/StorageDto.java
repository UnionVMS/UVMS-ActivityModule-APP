/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.service.dto;

import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import java.util.Set;

@EqualsAndHashCode
public class StorageDto {

    @JsonbTransient
    VesselStorageCharCodeDto vesselStorageCharCodeDto;

    @JsonbTransient
    StorageIdentifierDto identifier;

    @JsonbProperty("type")
    public String getVesselTypeCode() {
        String vesselTypeCode = null;
        if (vesselStorageCharCodeDto != null) {
            vesselTypeCode = vesselStorageCharCodeDto.getVesselTypeCode();
        }
        return vesselTypeCode;
    }

    public VesselStorageCharCodeDto getVesselStorageCharCodeDto() {
        return vesselStorageCharCodeDto;
    }

    public void setVesselStorageCharCodeDto(VesselStorageCharCodeDto vesselStorageCharCodeDto) {
        this.vesselStorageCharCodeDto = vesselStorageCharCodeDto;
    }

    @JsonbProperty("identifiers")
    public Set<StorageIdentifierDto> getIdentifiers() {
        Set<StorageIdentifierDto> identifiers = new HashSet<>();
        if (identifier != null) {
            identifiers.add(identifier);
        }
        return identifiers;
    }

    public StorageIdentifierDto getIdentifier() {
        return identifier;
    }

    public void setIdentifier(StorageIdentifierDto identifier) {
        this.identifier = identifier;
    }


}
