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

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.AapProcessEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.AapProductEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingActivity;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.internal.util.collections.Sets.newSet;

public class FishingActivityMapperTest {

    @Test
    public void testFishingActivityMapper() {
        FishingActivity fishingActivity = MapperUtil.getFishingActivity();
        FaReportDocumentEntity faReportDocumentEntity = new FaReportDocumentEntity();
        FishingActivityEntity fishingActivityEntity = FishingActivityMapper.INSTANCE.mapToFishingActivityEntity(fishingActivity, faReportDocumentEntity);
        fishingActivityEntity.getFishingTrip().getFishingActivities().add(fishingActivityEntity);

        assertFishingActivityFields(fishingActivity, fishingActivityEntity);
        assertEquals(faReportDocumentEntity, fishingActivityEntity.getFaReportDocument());

        assertNotNull(fishingActivityEntity.getFishingTrip());
        FishingTripEntity fishingTripEntity = fishingActivityEntity.getFishingTrip();
        assertNotNull(fishingTripEntity);
        assertFishingActivityFields(fishingActivity, fishingTripEntity.getFishingActivities().iterator().next());

        assertNotNull(fishingActivityEntity.getFaCatchs());
        FaCatchEntity faCatchEntity = fishingActivityEntity.getFaCatchs().iterator().next();
        assertNotNull(faCatchEntity);
        assertFishingActivityFields(fishingActivity, faCatchEntity.getFishingActivity());

        assertNotNull(fishingActivityEntity.getSourceVesselCharId());
        assertFishingActivityFields(fishingActivity, fishingActivityEntity.getSourceVesselCharId().getFishingActivitiesForSourceVesselCharId());

        assertNotNull(fishingActivityEntity.getDestVesselCharId());
        assertFishingActivityFields(fishingActivity, fishingActivityEntity.getDestVesselCharId().getFishingActivitiesForDestVesselCharId());

        assertNotNull(fishingActivityEntity.getFluxLocations());
        FluxLocationEntity fluxLocationEntity = fishingActivityEntity.getFluxLocations().iterator().next();
        assertNotNull(fluxLocationEntity);
        assertFishingActivityFields(fishingActivity, fluxLocationEntity.getFishingActivity());

        assertNotNull(fishingActivityEntity.getAllRelatedFishingActivities());
        FishingActivityEntity relatedFishingActivityEntity = fishingActivityEntity.getAllRelatedFishingActivities().iterator().next();
        assertNotNull(relatedFishingActivityEntity);
        assertFishingActivityFields(fishingActivity.getRelatedFishingActivities().get(0), relatedFishingActivityEntity);
    }

    private void assertFishingActivityFields(FishingActivity fishingActivity, FishingActivityEntity fishingActivityEntity) {
        assertEquals(fishingActivity.getTypeCode().getValue(), fishingActivityEntity.getTypeCode());
        assertEquals(fishingActivity.getTypeCode().getListID(), fishingActivityEntity.getTypeCodeListid());
        assertEquals(fishingActivity.getOccurrenceDateTime().getDateTime().toGregorianCalendar().toInstant(), fishingActivityEntity.getOccurence());
        assertEquals(fishingActivity.getReasonCode().getValue(), fishingActivityEntity.getReasonCode());
        assertEquals(fishingActivity.getReasonCode().getListID(), fishingActivityEntity.getReasonCodeListId());
        assertEquals(fishingActivity.getVesselRelatedActivityCode().getValue(), fishingActivityEntity.getVesselActivityCode());
        assertEquals(fishingActivity.getVesselRelatedActivityCode().getListID(), fishingActivityEntity.getVesselActivityCodeListId());
        assertEquals(fishingActivity.getFisheryTypeCode().getValue(), fishingActivityEntity.getFisheryTypeCode());
        assertEquals(fishingActivity.getFisheryTypeCode().getListID(), fishingActivityEntity.getFisheryTypeCodeListId());
        assertEquals(fishingActivity.getSpeciesTargetCode().getValue(), fishingActivityEntity.getSpeciesTargetCode());
        assertEquals(fishingActivity.getSpeciesTargetCode().getListID(), fishingActivityEntity.getSpeciesTargetCodeListId());
        assertEquals(fishingActivity.getOperationsQuantity().getValue().intValue(), fishingActivityEntity.getOperationsQuantity().getValue().intValue());
        assertEquals(fishingActivity.getOperationsQuantity().getUnitCode(), fishingActivityEntity.getOperationsQuantity().getUnitCode());
        assertEquals(fishingActivity.getFishingDurationMeasure().getValue().intValue(), fishingActivityEntity.getFishingDurationMeasure().intValue());
        assertEquals(fishingActivity.getFishingDurationMeasure().getUnitCode(), fishingActivityEntity.getFishingDurationMeasureCode());
    //    assertEquals(fishingActivity.getSpecifiedFLAPDocument().getID().getValue(), fishingActivityEntity.getFlapDocumentId());
      //  assertEquals(fishingActivity.getSpecifiedFLAPDocument().getID().getSchemeID(), fishingActivityEntity.getFlapDocumentSchemeId());
    }

    @Test
    public void testMapToFishingActivityReportDTOList() {
        FishingActivityEntity entity= MapperUtil.getFishingActivityEntity();
        assertNotNull(entity);
      //  assertNotNull(FishingActivityMapper.INSTANCE.mapToFishingActivityReportDTO(entity));
    }



    @Test
    public void testSpeciesCodeWithNullShouldNotBeMapped() {

        FaCatchEntity faCatchEntity = new FaCatchEntity();
        faCatchEntity.setSpeciesCode(null);

        FishingActivityEntity fa = new FishingActivityEntity();
        fa.setFaCatchs(newSet(faCatchEntity));

        List<String> speciesCode = FishingActivityMapper.INSTANCE.getSpeciesCode(fa);

        assertEquals(0, speciesCode.size());

    }

    @Test
    public void testSpeciesCodeWithDuplicatedShouldFilterDuplicates() {
        FaCatchEntity faCatchEntity = new FaCatchEntity();
        faCatchEntity.setSpeciesCode("2222");

        AapProductEntity aapProductEntity = new AapProductEntity();
        aapProductEntity.setSpeciesCode("2222");

        AapProcessEntity aapProcessEntity = new AapProcessEntity();
        aapProcessEntity.setAapProducts(newSet(aapProductEntity));

        faCatchEntity.setAapProcesses(newSet(aapProcessEntity));

        FishingActivityEntity fa = new FishingActivityEntity();
        fa.setFaCatchs(newSet(faCatchEntity));

        List<String> speciesCode = FishingActivityMapper.INSTANCE.getSpeciesCode(fa);

        assertEquals(1, speciesCode.size());
    }

    @Test
    public void getCalculatedStartTime_specifiedDelimitedPeriod() throws Exception {
        // Given
        Instant expectedStartDate = Instant.parse("2019-01-01T15:45:32.012Z");
        DateTimeType dateTimeType = getDateTimeType(expectedStartDate);

        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();
        delimitedPeriod.setStartDateTime(dateTimeType);

        FishingActivity fishingActivity = new FishingActivity();
        fishingActivity.setSpecifiedDelimitedPeriods(Lists.newArrayList(delimitedPeriod));

        // When
        Instant calculatedStartTime = FishingActivityMapper.INSTANCE.getCalculatedStartTime(fishingActivity);

        // Then
        assertEquals(expectedStartDate, calculatedStartTime);
    }

    @Test
    public void getCalculatedStartTime_occurrenceAndSpecifiedDelimitedPeriod_expectFirst() throws Exception {
        // Given
        Instant expectedStartDate = Instant.parse("2019-01-01T15:45:32.012Z");
        Instant occurrenceStartDate = Instant.parse("2019-01-01T15:50:32.012Z");
        DateTimeType dateTimeType = getDateTimeType(expectedStartDate);
        DateTimeType occurrenceDateTimeType = getDateTimeType(occurrenceStartDate);

        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();
        delimitedPeriod.setStartDateTime(dateTimeType);

        FishingActivity fishingActivity = new FishingActivity();
        fishingActivity.setOccurrenceDateTime(occurrenceDateTimeType);
        fishingActivity.setSpecifiedDelimitedPeriods(Lists.newArrayList(delimitedPeriod));

        // When
        Instant calculatedStartTime = FishingActivityMapper.INSTANCE.getCalculatedStartTime(fishingActivity);

        // Then
        assertEquals(expectedStartDate, calculatedStartTime);
    }

    @Test
    public void getCalculatedStartTime_specifiedDelimitedPeriodOnRelatedActivity() throws Exception {
        // Given
        Instant startDate1 = Instant.parse("2019-01-01T15:45:32.012Z");
        Instant expectedStartDate = Instant.parse("2019-01-01T15:43:32.012Z");
        DateTimeType dateTimeType1 = getDateTimeType(startDate1);
        DateTimeType dateTimeType2 = getDateTimeType(expectedStartDate);

        DelimitedPeriod delimitedPeriod1 = new DelimitedPeriod();
        delimitedPeriod1.setStartDateTime(dateTimeType1);

        DelimitedPeriod delimitedPeriod2 = new DelimitedPeriod();
        delimitedPeriod2.setStartDateTime(dateTimeType2);

        FishingActivity relatedFishingActivity1 = new FishingActivity();
        relatedFishingActivity1.setSpecifiedDelimitedPeriods(Lists.newArrayList(delimitedPeriod1));

        FishingActivity relatedFishingActivity2 = new FishingActivity();
        relatedFishingActivity2.setSpecifiedDelimitedPeriods(Lists.newArrayList(delimitedPeriod2));

        FishingActivity fishingActivity = new FishingActivity();
        fishingActivity.setRelatedFishingActivities(Lists.newArrayList(relatedFishingActivity1, relatedFishingActivity2));

        // When
        Instant calculatedStartTime = FishingActivityMapper.INSTANCE.getCalculatedStartTime(fishingActivity);

        // Then
        assertEquals(expectedStartDate, calculatedStartTime);
    }

    @Test
    public void getCalculatedStartTime_occurrenceAndSpecifiedDelimitedPeriodOnRelatedActivity_expectFirst() throws Exception {
        // Given
        Instant occurrenceStartDate = Instant.parse("2019-01-01T15:44:32.012Z");
        Instant startDate1 = Instant.parse("2019-01-01T15:45:32.012Z");
        Instant expectedStartDate = Instant.parse("2019-01-01T15:43:32.012Z");

        DateTimeType dateTimeType1 = getDateTimeType(startDate1);
        DateTimeType dateTimeType2 = getDateTimeType(expectedStartDate);
        DateTimeType occurrenceDateTimeType = getDateTimeType(occurrenceStartDate);

        DelimitedPeriod delimitedPeriod1 = new DelimitedPeriod();
        delimitedPeriod1.setStartDateTime(dateTimeType1);

        DelimitedPeriod delimitedPeriod2 = new DelimitedPeriod();
        delimitedPeriod2.setStartDateTime(dateTimeType2);

        FishingActivity relatedFishingActivity1 = new FishingActivity();
        relatedFishingActivity1.setSpecifiedDelimitedPeriods(Lists.newArrayList(delimitedPeriod1));

        FishingActivity relatedFishingActivity2 = new FishingActivity();
        relatedFishingActivity2.setSpecifiedDelimitedPeriods(Lists.newArrayList(delimitedPeriod2));

        FishingActivity fishingActivity = new FishingActivity();
        fishingActivity.setOccurrenceDateTime(occurrenceDateTimeType);
        fishingActivity.setRelatedFishingActivities(Lists.newArrayList(relatedFishingActivity1, relatedFishingActivity2));

        // When
        Instant calculatedStartTime = FishingActivityMapper.INSTANCE.getCalculatedStartTime(fishingActivity);

        // Then
        assertEquals(expectedStartDate, calculatedStartTime);
    }

    @Test
    public void getCalculatedStartTime_occurrence() throws Exception {
        // Given
        Instant expectedStartTime = Instant.parse("2019-01-01T15:45:32.012Z");
        DateTimeType dateTimeType = getDateTimeType(expectedStartTime);

        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();
        delimitedPeriod.setStartDateTime(dateTimeType);

        FishingActivity fishingActivity = new FishingActivity();
        fishingActivity.setOccurrenceDateTime(dateTimeType);

        // When
        Instant calculatedStartTime = FishingActivityMapper.INSTANCE.getCalculatedStartTime(fishingActivity);

        // Then
        assertEquals(expectedStartTime, calculatedStartTime);
    }

    @Test
    public void getCalculatedEndTime_specifiedDelimitedPeriod() throws Exception {
        // Given
        Instant expectedEndDate = Instant.parse("2019-01-01T15:45:32.012Z");
        DateTimeType dateTimeType = getDateTimeType(expectedEndDate);

        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();
        delimitedPeriod.setEndDateTime(dateTimeType);

        FishingActivity fishingActivity = new FishingActivity();
        fishingActivity.setSpecifiedDelimitedPeriods(Lists.newArrayList(delimitedPeriod));

        // When
        Instant calculatedEndTime = FishingActivityMapper.INSTANCE.getCalculatedEndTime(fishingActivity);

        // Then
        assertEquals(expectedEndDate, calculatedEndTime);
    }

    @Test
    public void getCalculatedEndTime_specifiedDelimitedPeriodWithOnlyDuration() throws Exception {
        // Given
        Instant expectedEndDate = Instant.parse("2019-01-01T16:27:32.012Z");
        Instant occurrence = Instant.parse("2019-01-01T15:45:32.012Z");
        DateTimeType dateTimeType = getDateTimeType(occurrence);

        MeasureType measureType = new MeasureType();
        measureType.setValue(BigDecimal.valueOf(42));

        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();
        delimitedPeriod.setDurationMeasure(measureType);

        FishingActivity fishingActivity = new FishingActivity();
        fishingActivity.setOccurrenceDateTime(dateTimeType);
        fishingActivity.setSpecifiedDelimitedPeriods(Lists.newArrayList(delimitedPeriod));

        // When
        Instant calculatedEndTime = FishingActivityMapper.INSTANCE.getCalculatedEndTime(fishingActivity);

        // Then
        assertEquals(expectedEndDate, calculatedEndTime);
    }

    @Test
    public void getCalculatedEndTime_specifiedDelimitedPeriodOnRelatedActivity() throws Exception {
        // Given
        Instant endDate1 = Instant.parse("2019-01-01T15:43:32.012Z");
        Instant expectedEndDate = Instant.parse("2019-01-01T15:45:32.012Z");
        DateTimeType dateTimeType1 = getDateTimeType(endDate1);
        DateTimeType dateTimeType2 = getDateTimeType(expectedEndDate);

        DelimitedPeriod delimitedPeriod1 = new DelimitedPeriod();
        delimitedPeriod1.setEndDateTime(dateTimeType1);

        DelimitedPeriod delimitedPeriod2 = new DelimitedPeriod();
        delimitedPeriod2.setEndDateTime(dateTimeType2);

        FishingActivity relatedFishingActivity1 = new FishingActivity();
        relatedFishingActivity1.setSpecifiedDelimitedPeriods(Lists.newArrayList(delimitedPeriod1));

        FishingActivity relatedFishingActivity2 = new FishingActivity();
        relatedFishingActivity2.setSpecifiedDelimitedPeriods(Lists.newArrayList(delimitedPeriod2));

        FishingActivity fishingActivity = new FishingActivity();
        fishingActivity.setRelatedFishingActivities(Lists.newArrayList(relatedFishingActivity1, relatedFishingActivity2));

        // When
        Instant calculatedEndTime = FishingActivityMapper.INSTANCE.getCalculatedEndTime(fishingActivity);

        // Then
        assertEquals(expectedEndDate, calculatedEndTime);
    }

    @Test
    public void getCalculatedEndTime_occurrence() throws Exception {
        // Given
        Instant expectedEndDate = Instant.parse("2019-01-01T15:45:32.012Z");
        DateTimeType dateTimeType = getDateTimeType(expectedEndDate);

        DelimitedPeriod delimitedPeriod = new DelimitedPeriod();
        delimitedPeriod.setEndDateTime(dateTimeType);

        FishingActivity fishingActivity = new FishingActivity();
        fishingActivity.setOccurrenceDateTime(dateTimeType);

        // When
        Instant calculatedEndTime = FishingActivityMapper.INSTANCE.getCalculatedEndTime(fishingActivity);

        // Then
        assertEquals(expectedEndDate, calculatedEndTime);
    }

    private DateTimeType getDateTimeType(Instant instant) throws DatatypeConfigurationException {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(Date.from(instant));
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        DateTimeType dateTimeType = new DateTimeType();
        dateTimeType.setDateTime(xmlGregorianCalendar);

        return dateTimeType;
    }
}
