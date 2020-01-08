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

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.uvms.activity.service.FishingTripCache;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.RelatedReportDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ReportDocumentDto;
import eu.europa.ec.fisheries.uvms.activity.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class FaReportDocumentMapperTest {

    @Test
    public void mapToFaReportCorrection() {
        FaReportDocumentEntity faReportDocumentEntity = MapperUtil.getFaReportDocumentEntity();
        FaReportCorrectionDTO faReportCorrectionDTO = FaReportDocumentMapper.INSTANCE.mapToFaReportCorrectionDto(faReportDocumentEntity);

        assertEquals(faReportDocumentEntity.getStatus(), faReportCorrectionDTO.getCorrectionType());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getCreationDatetime().toEpochMilli(), faReportCorrectionDTO.getCreationDate().toInstant().toEpochMilli());

        FluxReportIdentifierEntity entity = faReportDocumentEntity.getFluxReportDocument().getFluxReportIdentifiers().iterator().next();
        assertEquals(entity.getFluxReportIdentifierId(), faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getKey());
        assertEquals(entity.getFluxReportIdentifierSchemeId(), faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getValue());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getFluxParty().getFluxPartyName(), faReportCorrectionDTO.getOwnerFluxPartyName());
    }

    @Test
    public void mapToFaReportCorrectionList() {
        FaReportDocumentEntity faReportDocumentEntity = MapperUtil.getFaReportDocumentEntity();
        List<FaReportCorrectionDTO> faReportCorrectionDTOs = FaReportDocumentMapper.INSTANCE.mapToFaReportCorrectionDtoList(Arrays.asList(faReportDocumentEntity));
        FaReportCorrectionDTO faReportCorrectionDTO = faReportCorrectionDTOs.get(0);

        assertEquals(faReportDocumentEntity.getStatus(), faReportCorrectionDTO.getCorrectionType());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getCreationDatetime().toEpochMilli(), faReportCorrectionDTO.getCreationDate().toInstant().toEpochMilli());

        FluxReportIdentifierEntity entity = faReportDocumentEntity.getFluxReportDocument().getFluxReportIdentifiers().iterator().next();
        assertEquals(entity.getFluxReportIdentifierId(), faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getKey());
        assertEquals(entity.getFluxReportIdentifierSchemeId(), faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getValue());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getFluxParty().getFluxPartyName(), faReportCorrectionDTO.getOwnerFluxPartyName());
    }

    @Test
    public void faReportDocumentMapper() {
        FAReportDocument faReportDocument = MapperUtil.getFaReportDocument();
        FaReportDocumentEntity faReportDocumentEntity = FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, FaReportSourceEnum.FLUX);
        assertFaReportDocumentFields(faReportDocument, faReportDocumentEntity);
        assertNotNull(faReportDocumentEntity);
        assertNotNull(faReportDocumentEntity.getFluxReportDocument());
        assertFaReportDocumentFields(faReportDocument, faReportDocumentEntity.getFluxReportDocument().getFaReportDocument());
    }

    @Test
    public void faReportDocumentMapperNullReturns(){
        Set<FishingActivityEntity> fishingActivityEntities = FaReportDocumentMapper.INSTANCE.mapFishingActivityEntities(null, new FaReportDocumentEntity(), null, new FishingTripCache());
        assertEquals(0, fishingActivityEntities.size());
        Set<VesselTransportMeansEntity> vesselTransportMeansEntityList = FaReportDocumentMapper.INSTANCE.mapVesselTransportMeansEntity(null, new FaReportDocumentEntity());
        assertNull(vesselTransportMeansEntityList);
        Set<FishingActivityEntity> fishingActivityEntities1 = FaReportDocumentMapper.INSTANCE.mapFishingActivityEntities(null, new FaReportDocumentEntity(), null, new FishingTripCache());
        assertEquals(0, fishingActivityEntities1.size());
    }

    @Test
    public void mapFaReportDocumentToReportDocumentDto() {
        // Given
        FAReportDocument faReportDocument = MapperUtil.getFaReportDocument();
        FaReportDocumentEntity faReportDocumentEntity = FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, FaReportSourceEnum.FLUX);

        // When
        ReportDocumentDto dto = FaReportDocumentMapper.INSTANCE.mapFaReportDocumentToReportDocumentDto(faReportDocumentEntity);

        // Then
        assertEquals(faReportDocumentEntity.getTypeCode(), dto.getType());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getPurposeCode(), dto.getPurposeCode());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getReferenceId(), dto.getRefId());
        assertEquals(faReportDocumentEntity.getFmcMarker(), dto.getFmcMark());

        assertEquals("2016-07-01T11:15:00", dto.getAcceptedDate());
        assertEquals("2016-07-01T11:15:00", dto.getCreationDate());

        FaReportIdentifierEntity faReportIdentifier = faReportDocumentEntity.getFaReportIdentifiers().iterator().next();
        assertEquals(1, dto.getRelatedReports().size());
        RelatedReportDto relatedReport = dto.getRelatedReports().get(0);
        assertEquals(faReportIdentifier.getFaReportIdentifierId(), relatedReport.getId());
        assertEquals(faReportIdentifier.getFaReportIdentifierSchemeId(), relatedReport.getSchemeId());
    }

    private void assertFaReportDocumentFields(FAReportDocument faReportDocument, FaReportDocumentEntity faReportDocumentEntity) {
        assertEquals(faReportDocument.getTypeCode().getValue(), faReportDocumentEntity.getTypeCode());
        assertEquals(faReportDocument.getTypeCode().getListID(), faReportDocumentEntity.getTypeCodeListId());
        assertEquals(faReportDocument.getAcceptanceDateTime().getDateTime().toGregorianCalendar().toInstant(), faReportDocumentEntity.getAcceptedDatetime());
        assertEquals(faReportDocument.getFMCMarkerCode().getValue(), faReportDocumentEntity.getFmcMarker());
        assertEquals(faReportDocument.getFMCMarkerCode().getListID(), faReportDocumentEntity.getFmcMarkerListId());
        assertEquals(FaReportStatusType.NEW.name(), faReportDocumentEntity.getStatus());
        assertEquals(FaReportSourceEnum.FLUX.getSourceType(), faReportDocumentEntity.getSource());
    }
}
