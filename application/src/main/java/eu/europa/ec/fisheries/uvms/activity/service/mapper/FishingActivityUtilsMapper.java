/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.*;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.LocationEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.LocationEnum;
import eu.europa.ec.fisheries.uvms.activity.service.dto.DelimitedPeriodDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FluxReportIdentifierDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fishingtrip.ReportDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Mapper(componentModel = "cdi", imports = {FaReportStatusType.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Slf4j
public abstract class FishingActivityUtilsMapper extends BaseMapper {

    @Mapping(target = "fishingActivityId", source = "id")
    @Mapping(target = "uniqueFAReportId", expression = "java(getUniqueId(entity))")
    @Mapping(target = "faReportID", source = "faReportDocument.id")
    @Mapping(target = "fromId", expression = "java(getFromId(entity))")
    @Mapping(target = "fromName", source = "faReportDocument.fluxParty_name")
    @Mapping(target = "vesselTransportMeansName", expression = "java(getFaReportDocVesselTransportMeans(entity).getName())")
    @Mapping(target = "purposeCode", expression = "java(FaReportStatusType.valueOf(entity.getFaReportDocument().getStatus()).getPurposeCode().toString())")
    @Mapping(target = "faReportType", source = "faReportDocument.typeCode")
    @Mapping(source = "typeCode", target = "activityType")
    @Mapping(target = "areas", expression = "java(getAreasForFishingActivity(entity))")
    @Mapping(target = "port", expression = "java(getPortsForFishingActivity(entity))")
    @Mapping(target = "fishingGear", expression = "java(getFishingGears(entity))")
    @Mapping(target = "speciesCode", expression = "java(getSpeciesCode(entity))")
    @Mapping(target = "quantity", expression = "java(getQuantity(entity))")
    @Mapping(target = "fishingGears", expression = "java(null)")
    @Mapping(target = "fluxCharacteristics", expression = "java(null)")
    @Mapping(target = "dataSource", source = "faReportDocument.source")
    @Mapping(target = "vesselIdTypes", expression = "java(getVesselIdType(entity))")
    @Mapping(target = "startDate", source = "calculatedStartTime", qualifiedByName = "instantToDate")
    @Mapping(target = "endDate", expression = "java(getEndDate(entity))")
    @Mapping(target = "hasCorrection", expression = "java(BaseUtil.getCorrection(entity))")
    @Mapping(target = "fluxReportReferenceId", source = "faReportDocument.fluxReportDocument_ReferencedFaReportDocumentId")
    @Mapping(target = "fluxReportReferenceSchemeId", source = "faReportDocument.fluxReportDocument_ReferencedFaReportDocumentSchemeId")
    @Mapping(target = "cancelingReportID", source = "canceledBy")
    @Mapping(target = "deletingReportID", source = "deletedBy")
    @Mapping(target = "occurence", source = "occurence", qualifiedByName = "instantToDate")
    public abstract FishingActivityReportDTO mapToFishingActivityReportDTO(FishingActivityEntity entity);

    @Mapping(target = "fishingActivityId", source = "id")
    @Mapping(target = "activityType", source = "typeCode")
    @Mapping(target = "geometry", source = "wkt")
    @Mapping(target = "faReferenceID", source = "faReportDocument.fluxReportDocument_ReferencedFaReportDocumentId")
    @Mapping(target = "faReferenceSchemeID", source = "faReportDocument.fluxReportDocument_ReferencedFaReportDocumentSchemeId")
    @Mapping(target = "faUniqueReportID", expression = "java(getUniqueFaReportId(entity))")
    @Mapping(target = "faUniqueReportSchemeID", expression = "java(getUniqueFaReportSchemeId(entity))")
    @Mapping(target = "reason", source = "reasonCode")
    @Mapping(target = "purposeCode", source = "faReportDocument.fluxReportDocument_PurposeCode")
    @Mapping(target = "faReportDocumentType", source = "faReportDocument.typeCode")
    @Mapping(target = "faReportAcceptedDateTime", source = "faReportDocument.acceptedDatetime", qualifiedByName = "instantToDate")
    @Mapping(target = "correction", expression = "java(BaseUtil.getCorrection(entity))")
    @Mapping(target = "delimitedPeriod", expression = "java(getDelimitedPeriodDTOList(entity))")
    @Mapping(target = "faReportID", source = "faReportDocument.id")
    @Mapping(target = "occurence", source = "occurence", qualifiedByName = "instantToDate")
    public abstract ReportDTO mapToReportDTO(FishingActivityEntity entity);

    protected VesselTransportMeansEntity getFaReportDocVesselTransportMeans(FishingActivityEntity entity) {
        if (entity == null || entity.getFaReportDocument() == null || CollectionUtils.isEmpty(entity.getFaReportDocument().getVesselTransportMeans())) {
            return new VesselTransportMeansEntity();
        }
        return entity.getFaReportDocument().getVesselTransportMeans().iterator().next();
    }

    protected Instant getEndDate(FishingActivityEntity entity) {
        if (entity == null) {
            return null;
        }

        return entity.getCalculatedEndTime();
    }

    protected List<DelimitedPeriodDTO> getDelimitedPeriodDTOList(FishingActivityEntity entity) {
        if (entity == null) {
            return Collections.emptyList();
        }

        Instant calculatedStartTime = entity.getCalculatedStartTime();
        Instant calculatedEndTime = entity.getCalculatedEndTime();

        DelimitedPeriodDTO delimitedPeriodDTO = new DelimitedPeriodDTO();

        if (calculatedStartTime != null) {
            delimitedPeriodDTO.setStartDate(Date.from(calculatedStartTime));
        }

        if (calculatedEndTime != null) {
            delimitedPeriodDTO.setEndDate(Date.from(calculatedEndTime));
        }

        if (calculatedStartTime != null && calculatedEndTime != null) {
            Duration between = Duration.between(calculatedStartTime, calculatedEndTime);
            long durationInMinutes = between.toMinutes();
            delimitedPeriodDTO.setDuration((double) durationInMinutes);
            delimitedPeriodDTO.setUnitCode("MIN");
        }

        return Arrays.asList(delimitedPeriodDTO);
    }

    protected List<String> getSpeciesCode(FishingActivityEntity entity) {
        if (entity == null || entity.getFaCatchs() == null) {
            return Collections.emptyList();
        }
        HashSet<String> speciesCode = new HashSet<>();
        Set<FaCatchEntity> faCatchList = entity.getFaCatchs();
        for (FaCatchEntity faCatch : faCatchList) {
            speciesCode.add(faCatch.getSpeciesCode());
            getSpeciesCodeFromAapProduct(faCatch, speciesCode);
        }
        speciesCode.remove(null);
        return new ArrayList<>(speciesCode);
    }

    protected void getSpeciesCodeFromAapProduct(FaCatchEntity faCatch, HashSet<String> speciesCode) {
        if (faCatch.getAapProcesses() == null)
            return;
        for (AapProcessEntity aapProcessEntity : faCatch.getAapProcesses()) {
            Set<AapProductEntity> aapProductList = aapProcessEntity.getAapProducts();
            if (aapProductList != null) {
                for (AapProductEntity aapProduct : aapProductList) {
                    speciesCode.add(aapProduct.getSpeciesCode());
                }
            }
        }
    }

    protected String getUniqueFaReportId(FishingActivityEntity entity) {
        List<FluxReportIdentifierDTO> fluxReportIdentifierDTOs = getUniqueId(entity);
        if (CollectionUtils.isEmpty(fluxReportIdentifierDTOs)) {
            return null;
        }
        // for EU implementation we are expecting only single value for the FLUXReportIdentifier per FLUXReportDocument as per implementation guide.
        FluxReportIdentifierDTO fluxReportIdentifierDTO = fluxReportIdentifierDTOs.get(0);
        return fluxReportIdentifierDTO.getFluxReportId();
    }

    protected String getUniqueFaReportSchemeId(FishingActivityEntity entity) {
        List<FluxReportIdentifierDTO> fluxReportIdentifierDTOs = getUniqueId(entity);
        if (CollectionUtils.isEmpty(fluxReportIdentifierDTOs)) {
            return null;
        }
        // for EU implementation we are expecting only single value for the FLUXReportIdentifier per FLUXReportDocument as per implementation guide.
        FluxReportIdentifierDTO fluxReportIdentifierDTO = fluxReportIdentifierDTOs.get(0);
        return fluxReportIdentifierDTO.getFluxReportSchemeId();
    }

    protected List<FluxReportIdentifierDTO> getUniqueId(FishingActivityEntity entity) {
        if (entity == null || entity.getFaReportDocument() == null || StringUtils.isEmpty(entity.getFaReportDocument().getFluxReportDocument_Id())) {
            return Collections.emptyList();
        }

        FluxReportIdentifierDTO fluxReportIdentifierDTO = new FluxReportIdentifierDTO();
        fluxReportIdentifierDTO.setFluxReportId(entity.getFaReportDocument().getFluxReportDocument_Id());
        fluxReportIdentifierDTO.setFluxReportSchemeId(entity.getFaReportDocument().getFluxReportDocument_IdSchemeId());

        List<FluxReportIdentifierDTO> identifierDTOs = new ArrayList<>();
        identifierDTOs.add(fluxReportIdentifierDTO);

        return identifierDTOs;
    }

    protected List<String> getFromId(FishingActivityEntity entity) {
        if (entity == null ||
                entity.getFaReportDocument() == null ||
                entity.getFaReportDocument().getFluxParty_identifier() == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(entity.getFaReportDocument().getFluxParty_identifier());
    }

    protected Map<String, String> getVesselIdType(FishingActivityEntity entity) {
        if (entity == null ||
                entity.getFaReportDocument() == null ||
                entity.getFaReportDocument().getVesselTransportMeans() == null ||
                CollectionUtils.isEmpty(entity.getFaReportDocument().getVesselTransportMeans()) ||
                CollectionUtils.isEmpty(entity.getFaReportDocument().getVesselTransportMeans().iterator().next().getVesselIdentifiers())) {
            return Collections.emptyMap();
        }
        Map<String, String> vesselTransportIdList = new HashMap<>();
        /*
            We can assume that FaReportDocument will always have only one VesselTransportMeans.One to many relation is artificially created
         */
        Set<VesselIdentifierEntity> identifierList = entity.getFaReportDocument().getVesselTransportMeans().iterator().next().getVesselIdentifiers();

        for (VesselIdentifierEntity identity : identifierList) {
            vesselTransportIdList.put(identity.getVesselIdentifierSchemeId(), identity.getVesselIdentifierId());
        }
        return vesselTransportIdList;
    }

    /**
     * This method will calculate sum of all the weights of FACatches
     *
     * @param entity
     */
    protected Double getQuantity(FishingActivityEntity entity) {
        if (entity == null || entity.getFaCatchs() == null) {
            return 0d;
        }

        Double quantity = 0d;
        Set<FaCatchEntity> faCatchList = entity.getFaCatchs();

        for (FaCatchEntity faCatch : faCatchList) {
            if (faCatch.getCalculatedWeightMeasure() != null)
                quantity = quantity + faCatch.getCalculatedWeightMeasure();

            getQuantityFromAapProduct(faCatch, quantity);
        }

        return quantity;
    }

    private void getQuantityFromAapProduct(FaCatchEntity faCatch, Double quantity) {
        if (faCatch.getAapProcesses() == null)
            return;

        for (AapProcessEntity aapProcessEntity : faCatch.getAapProcesses()) {
            Set<AapProductEntity> aapProductList = aapProcessEntity.getAapProducts();
            if (aapProductList != null) {
                for (AapProductEntity aapProduct : aapProductList) {
                    if (aapProduct.getWeightMeasure() != null)
                        quantity = quantity + aapProduct.getWeightMeasure();
                }
            }
        }
    }

    protected Set<String> getFishingGears(FishingActivityEntity entity) {
        if (entity == null || entity.getFishingGears() == null) {
            return Collections.emptySet();
        }
        Set<String> gears = new HashSet<>();
        Set<FishingGearEntity> gearList = entity.getFishingGears();

        for (FishingGearEntity gear : gearList) {
            gears.add(gear.getTypeCode());
        }
        gears.remove(null);
        return gears;
    }

    protected List<String> getAreasForFishingActivity(FishingActivityEntity entity) {
        if (entity == null || entity.getFluxLocations() == null) {
            return Collections.emptyList();
        }
        Set<String> areas = new HashSet<>();
        Set<LocationEntity> fluxLocations = entity.getFluxLocations();

        for (LocationEntity location : fluxLocations) {
            if (location.getTypeCode() == LocationEnum.AREA) {
                areas.add(location.getFluxLocationIdentifier());
            }
        }
        areas.remove(null);
        return new ArrayList<>(areas);
    }

    protected List<String> getPortsForFishingActivity(FishingActivityEntity entity) {
        if (entity == null || entity.getFluxLocations() == null) {
            return Collections.emptyList();
        }
        Set<String> ports = new HashSet<>();
        Set<LocationEntity> fluxLocations = entity.getFluxLocations();
        for (LocationEntity location : fluxLocations) {
            if (location.getTypeCode() == LocationEnum.LOCATION) {
                ports.add(location.getFluxLocationIdentifier());
            }
        }
        ports.remove(null);
        return new ArrayList<>(ports);
    }
}
