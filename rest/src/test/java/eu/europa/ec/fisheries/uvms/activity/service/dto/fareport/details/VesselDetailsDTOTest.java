/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import org.junit.Test;

import java.util.Set;

import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.CFR;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.EXT_MARK;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.ICCAT;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.IRCS;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.UVI;
import static eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details.VesselDetailsDTO.builder;
import static org.junit.Assert.assertEquals;
import static org.mockito.internal.util.collections.Sets.newSet;

public class VesselDetailsDTOTest {

    @Test
    public void testEnrichIdentifiersAllFromAsset() {

        // Given
        AssetDTO assetDTO = new AssetDTO();
        assetDTO.setCfr("cfrFromAsset");
        assetDTO.setExternalMarking("extMarkingFromAsset");
        assetDTO.setIrcs("ircsFromAsset");
        assetDTO.setIccat("iccatFromAsset");
        assetDTO.setUvi("uviFromAsset");

        VesselDetailsDTO vesselDetailsDTO = builder().build();

        // When
        vesselDetailsDTO.enrichVesselIdentifiersFromAsset(assetDTO);

        // Then
        Set<AssetIdentifierDto> vesselIdentifiers = vesselDetailsDTO.getVesselIdentifiers();
        ImmutableMap<VesselIdentifierSchemeIdEnum, AssetIdentifierDto> vesselIdentifierMap = Maps.uniqueIndex(vesselIdentifiers, AssetIdentifierDto::getIdentifierSchemeId);
        assertEquals("cfrFromAsset", vesselIdentifierMap.get(CFR).getFaIdentifierId());
        assertEquals(true, vesselIdentifierMap.get(CFR).isFromAssets());

        assertEquals("extMarkingFromAsset", vesselIdentifierMap.get(EXT_MARK).getFaIdentifierId());
        assertEquals(true, vesselIdentifierMap.get(EXT_MARK).isFromAssets());

        assertEquals("ircsFromAsset", vesselIdentifierMap.get(IRCS).getFaIdentifierId());
        assertEquals(true, vesselIdentifierMap.get(IRCS).isFromAssets());

        assertEquals("iccatFromAsset", vesselIdentifierMap.get(ICCAT).getFaIdentifierId());
        assertEquals(true, vesselIdentifierMap.get(ICCAT).isFromAssets());

        assertEquals("uviFromAsset", vesselIdentifierMap.get(UVI).getFaIdentifierId());
        assertEquals(true, vesselIdentifierMap.get(UVI).isFromAssets());
    }

    @Test
    public void testEnrichIdentifiersNoneFromAsset() {

        // Given
        AssetDTO assetDTO = new AssetDTO();
        assetDTO.setCfr("cfrFromAsset");
        assetDTO.setExternalMarking("extMarkingFromAsset");
        assetDTO.setIrcs("ircsFromAsset");
        assetDTO.setIccat("iccatFromAsset");
        assetDTO.setUvi("uviFromAsset");

        AssetIdentifierDto cfr = new AssetIdentifierDto(CFR);
        cfr.setFaIdentifierId("cfrFromVessel");

        AssetIdentifierDto ext = new AssetIdentifierDto(EXT_MARK);
        ext.setFaIdentifierId("extMarkingFromVessel");

        AssetIdentifierDto ircs = new AssetIdentifierDto(IRCS);
        ircs.setFaIdentifierId("ircsFromVessel");

        AssetIdentifierDto iccat = new AssetIdentifierDto(ICCAT);
        iccat.setFaIdentifierId("iccatFromVessel");

        AssetIdentifierDto uvi = new AssetIdentifierDto(UVI);
        uvi.setFaIdentifierId("uviFromVessel");

        Set<AssetIdentifierDto> identifiers = newSet(cfr, ext, ircs, iccat, uvi);
        VesselDetailsDTO vesselDetailsDTO = builder().vesselIdentifiers(identifiers).build();

        // When
        vesselDetailsDTO.enrichVesselIdentifiersFromAsset(assetDTO);

        // Then
        Set<AssetIdentifierDto> vesselIdentifiers = vesselDetailsDTO.getVesselIdentifiers();
        ImmutableMap<VesselIdentifierSchemeIdEnum, AssetIdentifierDto> vesselIdentifierMap = Maps.uniqueIndex(vesselIdentifiers, AssetIdentifierDto::getIdentifierSchemeId);
        assertEquals("cfrFromVessel", vesselIdentifierMap.get(CFR).getFaIdentifierId());
        assertEquals(false, vesselIdentifierMap.get(CFR).isFromAssets());

        assertEquals("extMarkingFromVessel", vesselIdentifierMap.get(EXT_MARK).getFaIdentifierId());
        assertEquals(false, vesselIdentifierMap.get(EXT_MARK).isFromAssets());

        assertEquals("ircsFromVessel", vesselIdentifierMap.get(IRCS).getFaIdentifierId());
        assertEquals(false, vesselIdentifierMap.get(IRCS).isFromAssets());

        assertEquals("iccatFromVessel", vesselIdentifierMap.get(ICCAT).getFaIdentifierId());
        assertEquals(false, vesselIdentifierMap.get(ICCAT).isFromAssets());

        assertEquals("uviFromVessel", vesselIdentifierMap.get(UVI).getFaIdentifierId());
        assertEquals(false, vesselIdentifierMap.get(UVI).isFromAssets());
    }

    @Test
    public void testEnrichIdentifiersMix() {

        // Given
        AssetDTO assetDTO = new AssetDTO();
        assetDTO.setCfr("cfrFromAsset");
        assetDTO.setExternalMarking("extMarkingFromAsset");

        AssetIdentifierDto ext = new AssetIdentifierDto(EXT_MARK);
        ext.setFaIdentifierId("extMarkingFromVessel");

        AssetIdentifierDto ircs = new AssetIdentifierDto(IRCS);
        ircs.setFaIdentifierId("ircsFromVessel");

        Set<AssetIdentifierDto> identifiers = newSet(ext, ircs);
        VesselDetailsDTO vesselDetailsDTO = builder().vesselIdentifiers(identifiers).build();

        // When
        vesselDetailsDTO.enrichVesselIdentifiersFromAsset(assetDTO);

        // Then
        Set<AssetIdentifierDto> vesselIdentifiers = vesselDetailsDTO.getVesselIdentifiers();
        ImmutableMap<VesselIdentifierSchemeIdEnum, AssetIdentifierDto> vesselIdentifierMap = Maps.uniqueIndex(vesselIdentifiers, AssetIdentifierDto::getIdentifierSchemeId);
        assertEquals("cfrFromAsset", vesselIdentifierMap.get(CFR).getFaIdentifierId());
        assertEquals(true, vesselIdentifierMap.get(CFR).isFromAssets());

        assertEquals("extMarkingFromVessel", vesselIdentifierMap.get(EXT_MARK).getFaIdentifierId());
        assertEquals(false, vesselIdentifierMap.get(EXT_MARK).isFromAssets());

        assertEquals("ircsFromVessel", vesselIdentifierMap.get(IRCS).getFaIdentifierId());
        assertEquals(false, vesselIdentifierMap.get(IRCS).isFromAssets());

        assertEquals(null, vesselIdentifierMap.get(ICCAT));

        assertEquals(null, vesselIdentifierMap.get(UVI));
    }
}
