/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import eu.europa.ec.fisheries.uvms.activity.service.dto.AssetIdentifierDto;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import org.junit.Before;
import org.junit.Test;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.CFR;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.EXT_MARK;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.GFCM;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.IRCS;
import static eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum.UVI;
import static org.junit.Assert.assertEquals;
import static org.mockito.internal.util.collections.Sets.newSet;

public class BaseMapperTest {

    private BaseMapper baseMapper;

    @Before
    public void setUp() {
        baseMapper = new BaseMapper();
    }

    @Test
    public void mapToAssetListCriteriaPairList() {
        // Given
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

        ImmutableMap<ConfigSearchField, AssetListCriteriaPair> map = Maps.uniqueIndex(pairs, AssetListCriteriaPair::getKey);

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

    @Test
    public void mapMdrCodeListToAssetListCriteriaPairList() {
        AssetIdentifierDto cfr = new AssetIdentifierDto(CFR);
        cfr.setFaIdentifierId("cfrValue");
        AssetIdentifierDto gfmc = new AssetIdentifierDto(GFCM);
        AssetIdentifierDto ext = new AssetIdentifierDto(EXT_MARK);
        ext.setFaIdentifierId("extValue");
        AssetIdentifierDto ircs = new AssetIdentifierDto(IRCS);
        ircs.setFaIdentifierId("ircsValue");
        AssetIdentifierDto uvi = new AssetIdentifierDto(UVI);

        Set<AssetIdentifierDto> identifierDtos = newSet(cfr, gfmc, ext, ircs, uvi);
        List<String> mdrCodeList=new ArrayList<String>();
        mdrCodeList.add("CFR");
        mdrCodeList.add("IRCS");
        mdrCodeList.add("EXT_MARK");
        mdrCodeList.add("UVI");
        mdrCodeList.add("ICCAT");
        mdrCodeList.add("GFCM");
        List<AssetListCriteriaPair> pairs = BaseMapper.mapMdrCodeListToAssetListCriteriaPairList(identifierDtos,mdrCodeList);

        ImmutableMap<ConfigSearchField, AssetListCriteriaPair> map = Maps.uniqueIndex(pairs, AssetListCriteriaPair::getKey);

        assertEquals(2, map.size());
        AssetListCriteriaPair cfrPair = map.get(ConfigSearchField.CFR);
        assertEquals(ConfigSearchField.CFR, cfrPair.getKey());
        assertEquals("cfrValue", cfrPair.getValue());

        AssetListCriteriaPair ircsPair = map.get(ConfigSearchField.IRCS);
        assertEquals(ConfigSearchField.IRCS, ircsPair.getKey());
        assertEquals("ircsValue", ircsPair.getValue());
    }

    @Test
    public void instantToDate() {
        // Given
        Instant instant = Instant.now();

        // When
        Date date = baseMapper.instantToDate(instant);

        // Then
        // note: It seems to be impossible to create a Date that is reliably always UTC
        assertEquals(instant.toEpochMilli(), date.getTime());
    }

    @Test
    public void instantToDateUtilsStringFormat() {
        // Given
        Instant instant = Instant.ofEpochSecond(1_000_000);

        // When
        String formattedString = baseMapper.instantToDateUtilsStringFormat(instant);

        // Then
        assertEquals("1970-01-12T13:46:40", formattedString);
    }

    @Test
    public void instantToXMLGregorianCalendarUTC() {
        // Given
        Instant instant = Instant.now();

        // When
        XMLGregorianCalendar calendar = baseMapper.instantToXMLGregorianCalendarUTC(instant);

        // Then
        // Make sure there where has been no funny business with timezones
        assertEquals(0, calendar.getTimezone());
        assertEquals(instant.getEpochSecond(), calendar.toGregorianCalendar().toInstant().getEpochSecond());
    }

    @Test
    public void instantToDateTimeTypeUTC() {
        // Given
        Instant instant = Instant.now();

        // When
        DateTimeType dateTimeType = baseMapper.instantToDateTimeTypeUTC(instant);

        // Then
        XMLGregorianCalendar calendar = dateTimeType.getDateTime();
        // Make sure there where has been no funny business with timezones
        assertEquals(0, calendar.getTimezone());
        assertEquals(instant.getEpochSecond(), calendar.toGregorianCalendar().toInstant().getEpochSecond());
    }

    @Test
    public void dateTimeTypeToInstant() {
        // Given
        XMLGregorianCalendar xmlGregorianCalendar = baseMapper.instantToXMLGregorianCalendarUTC(Instant.now());
        DateTimeType dateTimeType = new DateTimeType();
        dateTimeType.setDateTime(xmlGregorianCalendar);

        // When
        Instant instant = baseMapper.dateTimeTypeToInstant(dateTimeType);

        // Then
        assertEquals(dateTimeType.getDateTime().toGregorianCalendar().toInstant(), instant);
    }
}