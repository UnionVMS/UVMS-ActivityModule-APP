/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselPositionEventEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.ers.service.dto.view.AreaDto;
import eu.europa.ec.fisheries.ers.service.dto.view.PositionDto;
import org.apache.commons.collections.CollectionUtils;

public class AreaDtoMapper extends BaseMapper {

    public AreaDto mapToAreaDto(FishingActivityEntity faEntity) {
        if ( faEntity == null ) {
            return null;
        }

        AreaDto areaDto = new AreaDto();

        areaDto.setFluxLocations( BaseMapper.mapFromFluxLocation( faEntity.getFluxLocations() ) );

        areaDto.setStartFishing( getStartFishing(faEntity) );
        areaDto.setTransmission( getTransmission(faEntity) );
        areaDto.setCrossing( extractPositionDtoFromFishingActivity(faEntity) );
        areaDto.setStartActivity( getStartActivity(faEntity) );

        return areaDto;
    }

    protected PositionDto getTransmission(FishingActivityEntity faEntity) {
        if (faEntity == null) {
            return null;
        }
        PositionDto positionDto = new PositionDto();

        if(faEntity.getFaReportDocument()!=null && faEntity.getFaReportDocument().getVesselTransportMeans()!=null
                && CollectionUtils.isNotEmpty(faEntity.getFaReportDocument().getVesselTransportMeans())
                && CollectionUtils.isNotEmpty(faEntity.getFaReportDocument().getVesselTransportMeans().iterator().next().getVesselPositionEvents()))
        {
            VesselPositionEventEntity vesselPositionEventEntity = faEntity.getFaReportDocument().getVesselTransportMeans().iterator().next().getVesselPositionEvents().iterator().next();
            positionDto.setOccurence(vesselPositionEventEntity.getObtainedOccurrenceDateTime());            ;
            positionDto.setGeometry(extractGeometryWkt(vesselPositionEventEntity.getLongitude(),vesselPositionEventEntity.getLatitude()));
        }else if(faEntity.getFaReportDocument()!=null){
            positionDto.setOccurence(faEntity.getFaReportDocument().getAcceptedDatetime());
        }

        return positionDto;
    }

    protected PositionDto getStartActivity(FishingActivityEntity faEntity) {
        if(faEntity ==null){
            return null;
        }

        FishingActivityEntity fishingActivityEntity=  extractSubFishingActivity(faEntity.getAllRelatedFishingActivities(), FishingActivityTypeEnum.START_ACTIVITY);
        return extractPositionDtoFromFishingActivity(fishingActivityEntity);
    }

    protected PositionDto getStartFishing(FishingActivityEntity faEntity) {
        if(faEntity ==null){
            return null;
        }

        FishingActivityEntity fishingActivityEntity=  extractSubFishingActivity(faEntity.getAllRelatedFishingActivities(), FishingActivityTypeEnum.START_FISHING);
        return extractPositionDtoFromFishingActivity(fishingActivityEntity);
    }

}
