
/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportSourceEnum;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FluxFaReportMessageMapper {

    public FluxFaReportMessageEntity mapToFluxFaReportMessage(FLUXFAReportMessage fluxfaReportMessage, FaReportSourceEnum faReportSourceEnum) {
        if (fluxfaReportMessage == null && faReportSourceEnum == null) {
            return null;
        }

        FluxFaReportMessageEntity fluxFaReportMessage = new FluxFaReportMessageEntity();

        if (fluxfaReportMessage != null) {
            FLUXReportDocument fluxReportDocument = fluxfaReportMessage.getFLUXReportDocument();
            if(fluxReportDocument == null) {
                return null;
            }
            FluxReportDocumentEntity fluxReportDocumentEntity = mapFluxReportDocumentEntity(fluxReportDocument);
            fluxReportDocumentEntity.setFluxFaReportMessage(fluxFaReportMessage);
            fluxFaReportMessage.setFluxReportDocument(fluxReportDocumentEntity);
            fluxFaReportMessage.setFaReportDocuments(mapFaReportDocuments(fluxfaReportMessage.getFAReportDocuments(), faReportSourceEnum, fluxFaReportMessage) );
        }
        return fluxFaReportMessage;
    }

    private FluxReportDocumentEntity mapFluxReportDocumentEntity(FLUXReportDocument fluxReportDocument) {
        FluxReportDocumentEntity fluxReportDocumentEntity = FluxReportDocumentMapper.INSTANCE.mapToFluxReportDocumentEntity(fluxReportDocument);
        fluxReportDocumentEntity.getFluxReportIdentifiers().forEach(id -> id.setFluxReportDocument(fluxReportDocumentEntity));
        return fluxReportDocumentEntity;
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
