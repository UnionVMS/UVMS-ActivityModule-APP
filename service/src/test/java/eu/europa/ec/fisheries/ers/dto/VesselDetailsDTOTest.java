/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.dto;

import static eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO.builder;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.CFR;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.EXT_MARK;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.ICCAT;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.IRCS;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.UVI;
import static junit.framework.Assert.assertTrue;
import static org.mockito.internal.util.collections.Sets.newSet;

import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import eu.europa.ec.fisheries.ers.service.dto.fareport.details.VesselDetailsDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.IdentifierDto;
import eu.europa.ec.fisheries.uvms.BaseUnitilsTest;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import org.junit.Test;

public class VesselDetailsDTOTest extends BaseUnitilsTest {

    @Test
    public void testEnrichIdentifiers() throws Exception {

        Asset asset = new Asset();
        asset.setCfr("cfrValueFromAsset");

        IdentifierDto cfr = new IdentifierDto(CFR);

        IdentifierDto ext = new IdentifierDto(EXT_MARK);
        ext.setIdentifierId("extMarkingFromActivity");

        IdentifierDto ircs = new IdentifierDto(IRCS);
        ircs.setIdentifierId("ircsFromActivity");

        IdentifierDto iccat = new IdentifierDto(ICCAT);
        iccat.setIdentifierId("iccat");

        IdentifierDto uvi = new IdentifierDto(UVI);

        Set<IdentifierDto> identifiers = newSet(ircs, cfr, iccat, uvi, ext);

        VesselDetailsDTO dto = builder().vesselIdentifiers(identifiers).build();

        dto.enrichIdentifiers(asset);

        Set<IdentifierDto> vesselIdentifiers = dto.getVesselIdentifiers();

        ImmutableMap<VesselIdentifierSchemeIdEnum, IdentifierDto> map = Maps.uniqueIndex(vesselIdentifiers,
                new Function<IdentifierDto, VesselIdentifierSchemeIdEnum>() {
                    public VesselIdentifierSchemeIdEnum apply(IdentifierDto from) {
                        return from.getIdentifierSchemeId();
                    }
                });

        IdentifierDto cfr_ = map.get(CFR);
        IdentifierDto ext_ = map.get(EXT_MARK);
        IdentifierDto ircs_ = map.get(IRCS);
        IdentifierDto uvi_ = map.get(UVI);
        IdentifierDto iccat_ = map.get(ICCAT);

        assertTrue(uvi.equals(uvi_));
        assertTrue(cfr_.equals(new IdentifierDto("cfrValueFromAsset", CFR, true)));
        assertTrue(ext_.equals(new IdentifierDto("extMarkingFromActivity", EXT_MARK, false))); // Don't override value
        assertTrue(ircs.equals(ircs_));
        assertTrue(iccat.equals(iccat_));

    }
}
