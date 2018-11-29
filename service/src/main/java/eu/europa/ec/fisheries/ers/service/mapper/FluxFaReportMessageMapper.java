
/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import eu.europa.ec.fisheries.ers.fa.entities.*;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;

public class FluxFaReportMessageMapper {

    public FluxFaReportMessageEntity mapToFluxFaReportMessage(FLUXFAReportMessage fluxfaReportMessage, FaReportSourceEnum faReportSourceEnum, FluxFaReportMessageEntity fluxFaReportMessage) {
        if ( fluxfaReportMessage == null && faReportSourceEnum == null ) {
            return null;
        }
        if (fluxfaReportMessage != null){
            FLUXReportDocument fluxReportDocument = fluxfaReportMessage.getFLUXReportDocument();
            if(fluxReportDocument == null){
                return null;
            }
            FluxReportDocumentEntity fluxReportDocumentEntity = FluxReportDocumentMapper.INSTANCE.mapToFluxReportDocumentEntity(fluxReportDocument);
            fluxReportDocumentEntity.setFluxFaReportMessage(fluxFaReportMessage);
            fluxFaReportMessage.setFluxReportDocument(fluxReportDocumentEntity);
            fluxFaReportMessage.setFaReportDocuments(mapFaReportDocuments(fluxfaReportMessage.getFAReportDocuments(), faReportSourceEnum, fluxFaReportMessage) );
        }
        return fluxFaReportMessage;
    }

    private Set<FaReportDocumentEntity> mapFaReportDocuments(List<FAReportDocument> faReportDocuments, FaReportSourceEnum faReportSourceEnum, FluxFaReportMessageEntity fluxFaReportMessage){
        Set<FaReportDocumentEntity> faReportDocumentEntities = new HashSet<>();
        for (FAReportDocument faReportDocument : faReportDocuments) {
            FaReportDocumentEntity entity = FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, faReportSourceEnum);
            VesselTransportMeans specifiedVesselTransportMeans = faReportDocument.getSpecifiedVesselTransportMeans();
            Set<VesselTransportMeansEntity> vesselTransportMeansEntities = FaReportDocumentMapper.mapVesselTransportMeansEntity(specifiedVesselTransportMeans, entity);
            Set<FishingActivityEntity> fishingActivityEntities = new HashSet<>();
            if (CollectionUtils.isNotEmpty(vesselTransportMeansEntities)){
                VesselTransportMeansEntity vessTraspMeans = vesselTransportMeansEntities.iterator().next();
                fishingActivityEntities = FaReportDocumentMapper.mapFishingActivityEntities(faReportDocument.getSpecifiedFishingActivities(), entity, vessTraspMeans);
                vessTraspMeans.setFaReportDocument(entity);
            }
            entity.setFishingActivities(fishingActivityEntities);
            entity.setFluxFaReportMessage(fluxFaReportMessage);
            entity.setVesselTransportMeans(vesselTransportMeansEntities);
            faReportDocumentEntities.add(entity);
        }
        return faReportDocumentEntities;
    }

}
