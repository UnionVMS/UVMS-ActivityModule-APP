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

import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;

/**
 * Created by kovian on 18/01/2017.
 */
@Mapper(uses = {FaReportDocumentMapper.class, FluxReportDocumentMapper.class})
public abstract class FluxFaReportMessageMapper extends BaseMapper {

    public static final FluxFaReportMessageMapper INSTANCE = Mappers.getMapper(FluxFaReportMessageMapper.class);

    @Mappings({
            @Mapping(target = "faReportDocuments", expression = "java(getFaReportDocuments(fluxfaReportMessage.getFAReportDocuments(), faReportSourceEnum, fluxFaReportMessage))"),
            @Mapping(target = "fluxReportDocument", expression = "java(getFluxReportDocument(fluxfaReportMessage.getFLUXReportDocument(), fluxFaReportMessage))")
    })
    public abstract FluxFaReportMessageEntity mapToFluxFaReportMessage(FLUXFAReportMessage fluxfaReportMessage, FaReportSourceEnum faReportSourceEnum, @MappingTarget FluxFaReportMessageEntity fluxFaReportMessage);

    protected Set<FaReportDocumentEntity> getFaReportDocuments(List<FAReportDocument> faReportDocuments,
                                                                FaReportSourceEnum faReportSourceEnum,
                                                                FluxFaReportMessageEntity fluxFaReportMessage){
        Set<FaReportDocumentEntity> faReportDocumentEntities = new HashSet<>();
        for (FAReportDocument faReportDocument : faReportDocuments) {
            FaReportDocumentEntity entity = FaReportDocumentMapper.INSTANCE.mapToFAReportDocumentEntity(faReportDocument, new FaReportDocumentEntity(), faReportSourceEnum);
            entity.setFluxFaReportMessage(fluxFaReportMessage);
            faReportDocumentEntities.add(entity);
        }
        return faReportDocumentEntities;
    }

    protected FluxReportDocumentEntity getFluxReportDocument(FLUXReportDocument fluxReportDocument, FluxFaReportMessageEntity fluxFaReportMessage){
        if(fluxReportDocument == null){
            return null;
        }
        FluxReportDocumentEntity entity = FluxReportDocumentMapper.INSTANCE.mapToFluxReportDocumentEntity(fluxReportDocument);
        entity.setFluxFaReportMessage(fluxFaReportMessage);
        return entity;
    }

}
