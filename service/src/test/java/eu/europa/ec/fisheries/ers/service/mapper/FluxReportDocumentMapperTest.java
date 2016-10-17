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

import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.ers.service.util.MapperUtil;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by padhyad on 8/1/2016.
 */
public class FluxReportDocumentMapperTest {

    @Test
    public void testFluxReportDocumentMapper() {
        FLUXReportDocument fluxReportDocument = MapperUtil.getFluxReportDocument();
        FluxReportDocumentEntity entity = new FluxReportDocumentEntity();
        FluxReportDocumentMapper.INSTANCE.mapToFluxReportDocumentEntity(fluxReportDocument, null, entity);

        assertEquals(fluxReportDocument.getIDS().get(0).getValue(), entity.getFluxReportIdentifiers().iterator().next().getFluxReportIdentifierId());
        assertEquals(fluxReportDocument.getIDS().get(0).getSchemeID(), entity.getFluxReportIdentifiers().iterator().next().getFluxReportIdentifierSchemeId());
        assertEquals(fluxReportDocument.getReferencedID().getValue(), entity.getReferenceId());
        assertEquals(fluxReportDocument.getCreationDateTime().getDateTime().toGregorianCalendar().getTime(), entity.getCreationDatetime());
        assertEquals(fluxReportDocument.getPurposeCode().getValue(), entity.getPurposeCode());
        assertEquals(fluxReportDocument.getPurposeCode().getListID(), entity.getPurposeCodeListId());
        assertTrue(entity.getPurpose().startsWith(fluxReportDocument.getPurpose().getValue()));
        assertEquals(fluxReportDocument.getOwnerFLUXParty().getIDS().get(0).getValue(), entity.getFluxParty().getFluxPartyIdentifiers().iterator().next().getFluxPartyIdentifierId());
        assertEquals(fluxReportDocument.getOwnerFLUXParty().getNames().get(0).getValue(), entity.getFluxParty().getFluxPartyName());
        assertNull(entity.getFaReportDocument());

    }
}
