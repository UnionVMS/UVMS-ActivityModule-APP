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

package eu.europa.ec.fisheries.ers.service.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.internal.util.collections.Sets.newSet;

import java.util.List;

import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingActivity;

/**
 * Created by padhyad on 8/1/2016.
 */
public class FishingActivityMapperTest {

    @Test
    public void testFishingActivityMapper() {
        FishingActivity fishingActivity = MapperUtil.getFishingActivity();
        FaReportDocumentEntity faReportDocumentEntity = new FaReportDocumentEntity();
        FishingActivityEntity fishingActivityEntity = FishingActivityMapper.INSTANCE.mapToFishingActivityEntity(fishingActivity, faReportDocumentEntity);

        assertFishingActivityFields(fishingActivity, fishingActivityEntity);
        assertEquals(faReportDocumentEntity, fishingActivityEntity.getFaReportDocument());

        assertNotNull(fishingActivityEntity.getFishingTrips());
        FishingTripEntity fishingTripEntity = fishingActivityEntity.getFishingTrips().iterator().next();
        assertNotNull(fishingTripEntity);
        assertFishingActivityFields(fishingActivity, fishingTripEntity.getFishingActivity());

        assertNotNull(fishingActivityEntity.getFaCatchs());
        FaCatchEntity faCatchEntity = fishingActivityEntity.getFaCatchs().iterator().next();
        assertNotNull(faCatchEntity);
        assertFishingActivityFields(fishingActivity, faCatchEntity.getFishingActivity());

        assertNotNull(fishingActivityEntity.getSourceVesselCharId());
        assertFishingActivityFields(fishingActivity, fishingActivityEntity.getSourceVesselCharId().getFishingActivitiesForSourceVesselCharId());

        assertNotNull(fishingActivityEntity.getDestVesselCharId());
        assertFishingActivityFields(fishingActivity, fishingActivityEntity.getDestVesselCharId().getFishingActivitiesForDestVesselCharId());

        assertNotNull(fishingActivityEntity.getDelimitedPeriods());
        DelimitedPeriodEntity delimitedPeriodEntity = fishingActivityEntity.getDelimitedPeriods().iterator().next();
        assertNotNull(delimitedPeriodEntity);
        assertFishingActivityFields(fishingActivity, delimitedPeriodEntity.getFishingActivity());

        assertNotNull(fishingActivityEntity.getFluxLocations());
        FluxLocationEntity fluxLocationEntity = fishingActivityEntity.getFluxLocations().iterator().next();
        assertNotNull(fluxLocationEntity);
        assertFishingActivityFields(fishingActivity, fluxLocationEntity.getFishingActivity());

        assertNotNull(fishingActivityEntity.getAllRelatedFishingActivities());
        FishingActivityEntity relatedFishingActivityEntity = fishingActivityEntity.getAllRelatedFishingActivities().iterator().next();
        assertNotNull(relatedFishingActivityEntity);
        assertFishingActivityFields(fishingActivity.getRelatedFishingActivities().get(0), relatedFishingActivityEntity);

        assertNotNull(fishingActivityEntity.getFishingActivityIdentifiers());
        FishingActivityIdentifierEntity fishingActivityIdentifierEntity = fishingActivityEntity.getFishingActivityIdentifiers().iterator().next();
        assertNotNull(fishingActivityIdentifierEntity);
        assertEquals(fishingActivity.getIDS().get(0).getValue(), fishingActivityIdentifierEntity.getFaIdentifierId());
        assertEquals(fishingActivity.getIDS().get(0).getSchemeID(), fishingActivityIdentifierEntity.getFaIdentifierSchemeId());
        assertFishingActivityFields(fishingActivity, fishingActivityIdentifierEntity.getFishingActivity());
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
}
