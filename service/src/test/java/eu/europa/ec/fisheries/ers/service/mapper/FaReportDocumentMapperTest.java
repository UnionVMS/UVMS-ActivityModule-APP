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

import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusEnum;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.FaReportCorrectionDTO;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.FAReportDocument;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by padhyad on 7/18/2016.
 */
public class FaReportDocumentMapperTest {

    @Test
    public void testMapToFaReportCorrection() {
        FaReportDocumentEntity faReportDocumentEntity = MapperUtil.getFaReportDocumentEntity();
        FaReportCorrectionDTO faReportCorrectionDTO = FaReportDocumentMapper.INSTANCE.mapToFaReportCorrectionDto(faReportDocumentEntity);

        assertEquals(faReportDocumentEntity.getStatus(), faReportCorrectionDTO.getCorrectionType());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getCreationDatetime(), faReportCorrectionDTO.getCorrectionDate());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getFluxReportDocumentId(), faReportCorrectionDTO.getFaReportIdentifier());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getOwnerFluxPartyId(), faReportCorrectionDTO.getOwnerFluxParty());
    }

    @Test
    public void testMapToFaReportCorrectionList() {
        FaReportDocumentEntity faReportDocumentEntity = MapperUtil.getFaReportDocumentEntity();
        List<FaReportCorrectionDTO> faReportCorrectionDTOs = FaReportDocumentMapper.INSTANCE.mapToFaReportCorrectionDtoList(Arrays.asList(faReportDocumentEntity));
        FaReportCorrectionDTO faReportCorrectionDTO = faReportCorrectionDTOs.get(0);

        assertEquals(faReportDocumentEntity.getStatus(), faReportCorrectionDTO.getCorrectionType());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getCreationDatetime(), faReportCorrectionDTO.getCorrectionDate());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getFluxReportDocumentId(), faReportCorrectionDTO.getFaReportIdentifier());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getOwnerFluxPartyId(), faReportCorrectionDTO.getOwnerFluxParty());
    }

    @Test
    public void testFaReportDocumentMapper() {
        FAReportDocument faReportDocument = MapperUtil.getFaReportDocument();
        FaReportDocumentEntity faReportDocumentEntity = new FaReportDocumentEntity();
        FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, faReportDocumentEntity);

        assertFaReportDocumentFields(faReportDocument, faReportDocumentEntity);

        assertNotNull(faReportDocumentEntity.getFaReportIdentifiers());
        FaReportIdentifierEntity faReportIdentifierEntity = faReportDocumentEntity.getFaReportIdentifiers().iterator().next();
        assertNotNull(faReportDocumentEntity);
        assertEquals(faReportDocument.getRelatedReportIDs().get(0).getValue(), faReportIdentifierEntity.getFaReportIdentifierId());
        assertEquals(faReportDocument.getRelatedReportIDs().get(0).getSchemeID(), faReportIdentifierEntity.getFaReportIdentifierSchemeId());
        assertFaReportDocumentFields(faReportDocument, faReportIdentifierEntity.getFaReportDocument());

        assertNotNull(faReportDocumentEntity.getFishingActivities());
        FishingActivityEntity fishingActivityEntity = faReportDocumentEntity.getFishingActivities().iterator().next();
        assertNotNull(fishingActivityEntity);
        assertFaReportDocumentFields(faReportDocument, fishingActivityEntity.getFaReportDocument());

        assertNotNull(faReportDocumentEntity.getFluxReportDocument());
        assertFaReportDocumentFields(faReportDocument, faReportDocumentEntity.getFluxReportDocument().getFaReportDocument());

        assertNotNull(faReportDocumentEntity.getVesselTransportMeans());
        assertFaReportDocumentFields(faReportDocument, faReportDocumentEntity.getVesselTransportMeans().getFaReportDocument());
    }

    private void assertFaReportDocumentFields(FAReportDocument faReportDocument, FaReportDocumentEntity faReportDocumentEntity) {
        assertEquals(faReportDocument.getTypeCode().getValue(), faReportDocumentEntity.getTypeCode());
        assertEquals(faReportDocument.getTypeCode().getListID(), faReportDocumentEntity.getTypeCodeListId());
        assertEquals(faReportDocument.getAcceptanceDateTime().getDateTime().toGregorianCalendar().getTime(), faReportDocumentEntity.getAcceptedDatetime());
        assertEquals(faReportDocument.getFMCMarkerCode().getValue(), faReportDocumentEntity.getFmcMarker());
        assertEquals(faReportDocument.getFMCMarkerCode().getListID(), faReportDocumentEntity.getFmcMarkerListId());
        assertEquals(FaReportStatusEnum.NEW.getStatus(), faReportDocumentEntity.getStatus());
    }
}
