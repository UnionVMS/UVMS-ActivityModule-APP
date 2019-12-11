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
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FlapDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxPartyEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.uvms.activity.service.FishingTripCache;
import eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.RelatedReportDto;
import eu.europa.ec.fisheries.uvms.activity.service.dto.view.ReportDocumentDto;
import eu.europa.ec.fisheries.uvms.activity.service.util.Utils;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLAPDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingActivity;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingGear;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(uses = {FAReportIdentifierMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FaReportDocumentMapper extends BaseMapper {

    public static final FaReportDocumentMapper INSTANCE = Mappers.getMapper(FaReportDocumentMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "faReportDocument.typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "faReportDocument.typeCode.listID"),
            @Mapping(target = "acceptedDatetime", source = "faReportDocument.acceptanceDateTime", qualifiedByName = "dateTimeTypeToInstant"),
            @Mapping(target = "fmcMarker", source = "faReportDocument.FMCMarkerCode.value"),
            @Mapping(target = "fmcMarkerListId", source = "faReportDocument.FMCMarkerCode.listID"),
            @Mapping(target = "status", constant = "NEW"),
            @Mapping(target = "source", source = "faReportSourceEnum.sourceType"),
            @Mapping(target = "fluxReportDocument", expression = "java(getFluxReportDocument(faReportDocument.getRelatedFLUXReportDocument(), faReportDocumentEntity))"),
            @Mapping(target = "faReportIdentifiers", source = "faReportDocument.relatedReportIDs"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "geom", ignore = true),
            @Mapping(target = "fluxFaReportMessage", ignore = true),
            @Mapping(target = "fishingActivities", ignore = true),
            @Mapping(target = "vesselTransportMeans", ignore = true),
    })
    public abstract FaReportDocumentEntity mapToFAReportDocumentEntity(FAReportDocument faReportDocument, FaReportSourceEnum faReportSourceEnum);

    @Mappings({
            @Mapping(target = "correctionType", source = "status"),
            @Mapping(target = "creationDate", source = "fluxReportDocument.creationDatetime"),
            @Mapping(target = "acceptedDate", source = "acceptedDatetime"),
            @Mapping(target = "faReportIdentifiers", expression = "java(getReportIdMap(faReportDocumentEntity.getFluxReportDocument().getFluxReportIdentifiers()))"),
            @Mapping(target = "ownerFluxPartyName", source = "fluxReportDocument.fluxParty.fluxPartyName"),
            @Mapping(target = "purposeCode", source = "fluxReportDocument.purposeCode")
    })
    public abstract FaReportCorrectionDTO mapToFaReportCorrectionDto(FaReportDocumentEntity faReportDocumentEntity);

    public abstract List<FaReportCorrectionDTO> mapToFaReportCorrectionDtoList(List<FaReportDocumentEntity> faReportDocumentEntities);

    @Mappings({
            @Mapping(target = "type" , source = "typeCode"),
            @Mapping(target = "acceptedDate" , expression = "java(instantToDateUtilsStringFormat(faReportDocument.getAcceptedDatetime()))"),
            @Mapping(target = "creationDate" , source = "fluxReportDocument.creationDatetime", qualifiedByName = "instantToDateUtilsStringFormat"),
            @Mapping(target = "owner", expression = "java(faReportDocument.getFluxReportDocument().getReportOwner())"),
            @Mapping(target = "refId" , source = "fluxReportDocument.referenceId"),
            @Mapping(target = "purposeCode" , source = "fluxReportDocument.purposeCode"),
            @Mapping(target = "fmcMark" , source = "fmcMarker"),
            @Mapping(target = "relatedReports" , source = "faReportIdentifiers"),
            @Mapping(target = "id", expression = "java(faReportDocument.getFluxReportDocument().getFluxPartyIdentifierBySchemeId(\"UUID\"))"),
    })
    public abstract ReportDocumentDto mapFaReportDocumentToReportDocumentDto(FaReportDocumentEntity faReportDocument);

    @Mappings({
            @Mapping(target = "id" , source = "faReportIdentifierId"),
            @Mapping(target = "schemeId" , source = "faReportIdentifierSchemeId"),
    })
    public abstract RelatedReportDto mapFaReportDocumentEntityToRelatedReportDto(FaReportIdentifierEntity entity);

    public static Set<VesselTransportMeansEntity> mapVesselTransportMeansEntity(VesselTransportMeans vesselTransportMeans, FaReportDocumentEntity faReportDocumentEntity) {
        if (vesselTransportMeans == null) {
            return null;
        }
        Set<VesselTransportMeansEntity> entities = new HashSet<>();
        VesselTransportMeansEntity vesselTransportMeansEntity = VesselTransportMeansMapper.INSTANCE.mapToVesselTransportMeansEntity(vesselTransportMeans);
        vesselTransportMeansEntity.setFaReportDocument(faReportDocumentEntity);

        List<FLAPDocument> grantedFLAPDocuments = vesselTransportMeans.getGrantedFLAPDocuments();

        if (CollectionUtils.isNotEmpty(grantedFLAPDocuments)){
            Set<FlapDocumentEntity> flapDocumentEntities = new HashSet<>();
            for (FLAPDocument grantedFLAPDocument : grantedFLAPDocuments) {
                FlapDocumentEntity flapDocumentEntity = FlapDocumentMapper.INSTANCE.mapToFlapDocumentEntity(grantedFLAPDocument);
                flapDocumentEntity.setVesselTransportMeans(vesselTransportMeansEntity);
                flapDocumentEntities.add(flapDocumentEntity);
            }
            vesselTransportMeansEntity.setFlapDocuments(flapDocumentEntities);

        }
        entities.add(vesselTransportMeansEntity);
        return entities;
    }

    public static Set<FishingActivityEntity> mapFishingActivityEntities(List<FishingActivity> fishingActivities, FaReportDocumentEntity faReportDocumentEntity, VesselTransportMeansEntity vesselTransportMeansEntity, FishingTripCache fishingTripCache) {
        Set<FishingActivityEntity> specifiedFishingActivityEntities = new HashSet<>();

        if (CollectionUtils.isEmpty(fishingActivities)) {
            return specifiedFishingActivityEntities;
        }

        for (FishingActivity fishingActivity : fishingActivities) {
            List<FishingGear> specifiedFishingGears = fishingActivity.getSpecifiedFishingGears();
            FishingActivityEntity fishingActivityEntity = FishingActivityMapper.INSTANCE.mapToFishingActivityEntity(fishingActivity, faReportDocumentEntity);

            if (CollectionUtils.isNotEmpty(specifiedFishingGears)) {
                Set<FishingGearEntity> fishingGearEntitySet = new HashSet<>();
                for (FishingGear fishingGear : specifiedFishingGears) {
                    FishingGearEntity fishingGearEntity = FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear);
                    List<CodeType> roleCodes = fishingGear.getRoleCodes();
                    for (CodeType roleCode : Utils.safeIterable(roleCodes)) {
                        fishingGearEntity.addFishingGearRole(FishingGearMapper.INSTANCE.mapToFishingGearRoleEntity(roleCode));
                    }

                    List<GearCharacteristic> applicableGearCharacteristics = fishingGear.getApplicableGearCharacteristics();
                    for (GearCharacteristic applicableGearCharacteristic : Utils.safeIterable(applicableGearCharacteristics)) {
                        fishingGearEntity.addGearCharacteristic(GearCharacteristicsMapper.INSTANCE.mapToGearCharacteristicEntity(applicableGearCharacteristic));
                    }

                    fishingGearEntity.setFishingActivity(fishingActivityEntity);
                    fishingGearEntitySet.add(fishingGearEntity);
                }
                fishingActivityEntity.setFishingGears(fishingGearEntitySet);
            }

            List<FLAPDocument> specifiedFLAPDocuments = fishingActivity.getSpecifiedFLAPDocuments();
            for (FLAPDocument specifiedFLAPDocument : Utils.safeIterable(specifiedFLAPDocuments)) {
                FlapDocumentEntity flapDocumentEntity = FlapDocumentMapper.INSTANCE.mapToFlapDocumentEntity(specifiedFLAPDocument);
                flapDocumentEntity.setFishingActivity(fishingActivityEntity);
                flapDocumentEntity.setVesselTransportMeans(vesselTransportMeansEntity);
                fishingActivityEntity.addFlapDocuments(flapDocumentEntity);
            }

            specifiedFishingActivityEntities.add(fishingActivityEntity);

            List<FLUXCharacteristic> specifiedFLUXCharacteristics = fishingActivity.getSpecifiedFLUXCharacteristics();
            for (FLUXCharacteristic specifiedFLUXCharacteristic : Utils.safeIterable(specifiedFLUXCharacteristics)) {
                FluxCharacteristicEntity fluxCharacteristicEntity = FluxCharacteristicsMapper.INSTANCE.mapToFluxCharEntity(specifiedFLUXCharacteristic);
                List<FLUXLocation> specifiedFLUXLocations = specifiedFLUXCharacteristic.getSpecifiedFLUXLocations();
                for (FLUXLocation specifiedFLUXLocation : Utils.safeIterable(specifiedFLUXLocations)) {
                    FluxLocationEntity fluxLocationEntity = FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(specifiedFLUXLocation);
                    fluxCharacteristicEntity.setFluxLocation(fluxLocationEntity);
                    fluxCharacteristicEntity.setFishingActivity(fishingActivityEntity);
                }
                fishingActivityEntity.addFluxCharacteristics(fluxCharacteristicEntity);
            }

            fishingTripCache.addAndUpdateFishingTripOfActivityIfItExists(fishingActivityEntity);
        }
        return specifiedFishingActivityEntities;
    }

    protected FluxReportDocumentEntity getFluxReportDocument(FLUXReportDocument fluxReportDocument, FaReportDocumentEntity faReportDocumentEntity) {
        if (fluxReportDocument == null) {
            return null;
        }
        FluxReportDocumentEntity fluxReportDocumentEntity = FluxReportDocumentMapper.INSTANCE.mapToFluxReportDocumentEntity(fluxReportDocument);
        if (fluxReportDocument.getOwnerFLUXParty() != null){
            FluxPartyEntity fluxPartyEntity = FluxPartyMapper.INSTANCE.mapToFluxPartyEntity(fluxReportDocument.getOwnerFLUXParty());
            fluxPartyEntity.setFluxReportDocument(fluxReportDocumentEntity);
            fluxReportDocumentEntity.setFluxParty(fluxPartyEntity);
        }
        Set<FluxReportIdentifierEntity> reportIdentifierEntitySet = new HashSet<>();
        for (IDType idType : Utils.safeIterable(fluxReportDocument.getIDS())) {
            FluxReportIdentifierEntity fluxReportIdentifierEntity = FluxReportIdentifierMapper.INSTANCE.mapToFluxReportIdentifierEntity(idType);
            fluxReportIdentifierEntity.setFluxReportDocument(fluxReportDocumentEntity);
            reportIdentifierEntitySet.add(fluxReportIdentifierEntity);
        }
        fluxReportDocumentEntity.setFluxReportIdentifiers(reportIdentifierEntitySet);
        fluxReportDocumentEntity.setFaReportDocument(faReportDocumentEntity);
        return fluxReportDocumentEntity;
    }

}
