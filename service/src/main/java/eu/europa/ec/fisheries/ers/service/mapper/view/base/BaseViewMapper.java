/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.mapper.view.base;

import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.model.dto.viewDto.PortDto;
import eu.europa.ec.fisheries.uvms.activity.model.dto.viewDto.ReportDocumentDto;
import eu.europa.ec.fisheries.uvms.activity.model.dto.viewDto.parent.FishingActivityViewDTO;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.model.StringWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by kovian on 14/02/2017.
 *
 * Base Class to be extended by all the mappers related to Activity Views (LANDING, ARRIVAL etc..)
 */
public abstract class BaseViewMapper {

    public abstract FishingActivityViewDTO mapFaEntityToFaDto(FishingActivityEntity faEntity, @MappingTarget FishingActivityViewDTO viewDto);

    protected List<PortDto> getPortsFromFluxLocation(Set<FluxLocationEntity> fLocEntities) {
        if (CollectionUtils.isEmpty(fLocEntities)) {
            return null;
        }
        List<PortDto> portsListDto = new ArrayList<>();
        for(FluxLocationEntity flEntity : fLocEntities){
            PortDto port = new PortDto();
            StringWrapper geomStrWrapper = GeometryMapper.INSTANCE.geometryToWkt(flEntity.getGeom());
            if(geomStrWrapper != null){
                port.setGeometry(geomStrWrapper.getValue());
            }
            port.setName(flEntity.getFluxLocationIdentifierSchemeId());
            portsListDto.add(port);
        }
        return portsListDto;
    }


    protected ReportDocumentDto getReportDocsFromEntity(FaReportDocumentEntity faRepDocEntity){
        if(faRepDocEntity == null){
            return null;
        }
        ReportDocumentDto repDocDto = new ReportDocumentDto();
        repDocDto.setType(faRepDocEntity.getTypeCode());
        repDocDto.setDateAccepted(DateUtils.dateToString(faRepDocEntity.getAcceptedDatetime()));
        FluxReportDocumentEntity fluxReportDocument = faRepDocEntity.getFluxReportDocument();
        if(fluxReportDocument != null){
            repDocDto.setCreationDate(DateUtils.dateToString(fluxReportDocument.getCreationDatetime()));
            repDocDto.setId(fluxReportDocument.getReferenceSchemeId());
            repDocDto.setRefId(fluxReportDocument.getReferenceId());
            repDocDto.setPurpose(fluxReportDocument.getPurpose());
            repDocDto.setPurposeCode(fluxReportDocument.getPurposeCode());
        }
        return repDocDto;
    }


}
