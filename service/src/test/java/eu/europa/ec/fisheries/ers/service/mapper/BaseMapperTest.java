/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.service.mapper;

import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.CFR;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.EXT_MARK;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.GFCM;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.IRCS;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.UVI;
import static junit.framework.Assert.assertEquals;
import static org.mockito.internal.util.collections.Sets.newSet;

import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import eu.europa.ec.fisheries.ers.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.uvms.BaseUnitilsTest;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import org.junit.Test;

public class BaseMapperTest extends BaseUnitilsTest {

    @Test
    public void testMapToAssetListCriteriaPairList() {

        AssetIdentifierDto cfr = new AssetIdentifierDto(CFR);
        cfr.setFaIdentifierId("cfrValue");
        AssetIdentifierDto gfmc = new AssetIdentifierDto(GFCM);
        AssetIdentifierDto ext = new AssetIdentifierDto(EXT_MARK);
        ext.setFaIdentifierId("extValue");
        AssetIdentifierDto ircs = new AssetIdentifierDto(IRCS);
        ircs.setFaIdentifierId("ircsValue");
        AssetIdentifierDto uvi = new AssetIdentifierDto(UVI);

        Set<AssetIdentifierDto> identifierDtos = newSet(cfr, gfmc, ext, ircs, uvi);

        List<AssetListCriteriaPair> pairs = BaseMapper.mapToAssetListCriteriaPairList(identifierDtos);

        ImmutableMap<ConfigSearchField, AssetListCriteriaPair> map = Maps.uniqueIndex(pairs,
                new Function<AssetListCriteriaPair, ConfigSearchField>() {
                    public ConfigSearchField apply(AssetListCriteriaPair from) {
                        return from.getKey();
                    }
                });

        assertEquals(3, map.size());
        AssetListCriteriaPair cfrPair = map.get(ConfigSearchField.CFR);
        assertEquals(ConfigSearchField.CFR, cfrPair.getKey());
        assertEquals("cfrValue", cfrPair.getValue());

        AssetListCriteriaPair extPair = map.get(ConfigSearchField.EXTERNAL_MARKING);
        assertEquals(ConfigSearchField.EXTERNAL_MARKING, extPair.getKey());
        assertEquals("extValue", extPair.getValue());

        AssetListCriteriaPair ircsPair = map.get(ConfigSearchField.IRCS);
        assertEquals(ConfigSearchField.IRCS, ircsPair.getKey());
        assertEquals("ircsValue", ircsPair.getValue());

    }
}
