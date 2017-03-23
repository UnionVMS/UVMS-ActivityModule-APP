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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import eu.europa.ec.fisheries.ers.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FluxLocationEnum;
import eu.europa.ec.fisheries.ers.service.dto.DelimitedPeriodDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.FluxLocationDto;
import eu.europa.ec.fisheries.ers.service.dto.view.IdentifierDto;
import eu.europa.ec.fisheries.uvms.BaseUnitilsTest;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import lombok.SneakyThrows;
import org.junit.Test;

public class BaseMapperTest extends BaseUnitilsTest {


    @Test
    @SneakyThrows
    public void testMapFluxLocations() {

        FluxLocationEntity entity1 = new FluxLocationEntity();
        entity1.setTypeCode("LOCATION");
        entity1.setRfmoCode("RFMO1");

        FluxLocationEntity entity2 = new FluxLocationEntity();
        entity2.setTypeCode("DUMMY");
        entity2.setRfmoCode("RFMO2");

        List<FluxLocationDto> fluxLocationDtos = BaseMapper.mapFromFluxLocation(newSet(entity1, entity2), FluxLocationEnum.LOCATION);

        assertEquals(1, fluxLocationDtos.size());
        assertEquals("RFMO1", fluxLocationDtos.get(0).getRfmoCode());
    }

    @Test
    @SneakyThrows
    public void testCalculateFishingActivity() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        DelimitedPeriodEntity period1 = new DelimitedPeriodEntity();
        period1.setStartDate(sdf.parse("21/12/2011"));
        period1.setEndDate(sdf.parse("21/12/2013"));
        period1.setCalculatedDuration(22.22d);

        DelimitedPeriodEntity period2 = new DelimitedPeriodEntity();
        period2.setStartDate(sdf.parse("21/11/2010"));
        period2.setEndDate(sdf.parse("21/11/2012"));
        period2.setCalculatedDuration(2.24d);

        DelimitedPeriodDTO periodDTO = BaseMapper.calculateFishingTime(newSet(period1, period2));

        assertEquals(24.46d, periodDTO.getDuration());
        assertEquals(period2.getStartDate(), periodDTO.getStartDate());
        assertEquals(period1.getEndDate(), periodDTO.getEndDate());

    }

    @Test
    public void testMapToAssetListCriteriaPairList() {

        IdentifierDto cfr = new IdentifierDto(CFR);
        cfr.setIdentifierId("cfrValue");
        IdentifierDto gfmc = new IdentifierDto(GFCM);
        IdentifierDto ext = new IdentifierDto(EXT_MARK);
        ext.setIdentifierId("extValue");
        IdentifierDto ircs = new IdentifierDto(IRCS);
        ircs.setIdentifierId("ircsValue");
        IdentifierDto uvi = new IdentifierDto(UVI);

        Set<IdentifierDto> identifierDtos = newSet(cfr, gfmc, ext, ircs, uvi);

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
