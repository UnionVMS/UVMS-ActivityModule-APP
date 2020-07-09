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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.ers.service.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by padhyad on 7/18/2016.
 */
public class FaReportDocumentMapperTest {

    @Test
    public void testMapToFaReportCorrection() {
        FaReportDocumentEntity faReportDocumentEntity = MapperUtil.getFaReportDocumentEntity();
        FaReportCorrectionDTO faReportCorrectionDTO = FaReportDocumentMapper.INSTANCE.mapToFaReportCorrectionDto(faReportDocumentEntity);

        assertEquals(faReportDocumentEntity.getStatus(), faReportCorrectionDTO.getCorrectionType());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getCreationDatetime(), faReportCorrectionDTO.getCreationDate());

        FluxReportIdentifierEntity entity = faReportDocumentEntity.getFluxReportDocument().getFluxReportIdentifiers().iterator().next();
        assertEquals(entity.getFluxReportIdentifierId(), faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getKey());
        assertEquals(entity.getFluxReportIdentifierSchemeId(), faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getValue());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getFluxParty().getFluxPartyName(), faReportCorrectionDTO.getOwnerFluxPartyName());
    }

    @Test
    public void testMapToFaReportCorrectionList() {
        FaReportDocumentEntity faReportDocumentEntity = MapperUtil.getFaReportDocumentEntity();
        List<FaReportCorrectionDTO> faReportCorrectionDTOs = FaReportDocumentMapper.INSTANCE.mapToFaReportCorrectionDtoList(Arrays.asList(faReportDocumentEntity));
        FaReportCorrectionDTO faReportCorrectionDTO = faReportCorrectionDTOs.get(0);

        assertEquals(faReportDocumentEntity.getStatus(), faReportCorrectionDTO.getCorrectionType());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getCreationDatetime(), faReportCorrectionDTO.getCreationDate());

        FluxReportIdentifierEntity entity = faReportDocumentEntity.getFluxReportDocument().getFluxReportIdentifiers().iterator().next();
        assertEquals(entity.getFluxReportIdentifierId(), faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getKey());
        assertEquals(entity.getFluxReportIdentifierSchemeId(), faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getValue());
        assertEquals(faReportDocumentEntity.getFluxReportDocument().getFluxParty().getFluxPartyName(), faReportCorrectionDTO.getOwnerFluxPartyName());
    }

    @Test
    public void testFaReportDocumentMapper() {
        FAReportDocument faReportDocument = MapperUtil.getFaReportDocument();
        FaReportDocumentEntity faReportDocumentEntity = FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, FaReportSourceEnum.FLUX);
        assertFaReportDocumentFields(faReportDocument, faReportDocumentEntity);
        assertNotNull(faReportDocumentEntity);
        assertNotNull(faReportDocumentEntity.getFluxReportDocument());
        assertFaReportDocumentFields(faReportDocument, faReportDocumentEntity.getFluxReportDocument().getFaReportDocument());
    }

    @Test
    public void testFaReportDocumentMapperNullReturns(){
        FluxFaReportMessageMappingContext ctx = new FluxFaReportMessageMappingContext();
        Set<FishingActivityEntity> fishingActivityEntities = FaReportDocumentMapper.mapFishingActivityEntities(ctx, null, new FaReportDocumentEntity(), null);
        assertTrue(fishingActivityEntities.size() == 0);
        Set<VesselTransportMeansEntity> vesselTransportMeansEntityList = FaReportDocumentMapper.mapVesselTransportMeansEntity(null, new FaReportDocumentEntity());
        assertNull(vesselTransportMeansEntityList);
        Set<FishingActivityEntity> fishingActivityEntities1 = FaReportDocumentMapper.mapFishingActivityEntities(ctx, null, new FaReportDocumentEntity(), null);
        assertTrue(fishingActivityEntities1.size() == 0);
    }

    private void assertFaReportDocumentFields(FAReportDocument faReportDocument, FaReportDocumentEntity faReportDocumentEntity) {
        assertEquals(faReportDocument.getTypeCode().getValue(), faReportDocumentEntity.getTypeCode());
        assertEquals(faReportDocument.getTypeCode().getListID(), faReportDocumentEntity.getTypeCodeListId());
        assertEquals(faReportDocument.getAcceptanceDateTime().getDateTime().toGregorianCalendar().getTime(), faReportDocumentEntity.getAcceptedDatetime());
        assertEquals(faReportDocument.getFMCMarkerCode().getValue(), faReportDocumentEntity.getFmcMarker());
        assertEquals(faReportDocument.getFMCMarkerCode().getListID(), faReportDocumentEntity.getFmcMarkerListId());
        assertEquals(FaReportStatusType.NEW.name(), faReportDocumentEntity.getStatus());
        assertEquals(FaReportSourceEnum.FLUX.getSourceType(), faReportDocumentEntity.getSource());
    }
}
