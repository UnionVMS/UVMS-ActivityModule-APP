/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import java.util.*;

import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FlapDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.service.dto.fareport.FaReportCorrectionDTO;
import eu.europa.ec.fisheries.ers.service.dto.view.RelatedReportDto;
import eu.europa.ec.fisheries.ers.service.dto.view.ReportDocumentDto;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
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

@Mapper(uses = {FAReportIdentifierMapper.class})
public abstract class FaReportDocumentMapper extends BaseMapper {

    public static final FaReportDocumentMapper INSTANCE = Mappers.getMapper(FaReportDocumentMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", source = "faReportDocument.typeCode.value"),
            @Mapping(target = "typeCodeListId", source = "faReportDocument.typeCode.listID"),
            @Mapping(target = "acceptedDatetime", source = "faReportDocument.acceptanceDateTime.dateTime"),
            @Mapping(target = "fmcMarker", source = "faReportDocument.FMCMarkerCode.value"),
            @Mapping(target = "fmcMarkerListId", source = "faReportDocument.FMCMarkerCode.listID"),
            @Mapping(target = "status", constant = "NEW"),
            @Mapping(target = "source", source = "faReportSourceEnum.sourceType"),
            @Mapping(target = "fluxReportDocument", expression = "java(getFluxReportDocument(faReportDocument.getRelatedFLUXReportDocument(), faReportDocumentEntity))"),
            @Mapping(target = "faReportIdentifiers", source = "faReportDocument.relatedReportIDs")
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
            @Mapping(target = "acceptedDate" , source = "acceptedDatetime", dateFormat = DateUtils.DATE_TIME_UI_FORMAT),
            @Mapping(target = "creationDate" , source = "fluxReportDocument.creationDatetime", dateFormat = DateUtils.DATE_TIME_UI_FORMAT),
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

    public static List<FishingActivityEntity> mapFishingActivityEntities(List<FishingActivity> fishingActivities, FaReportDocumentEntity faReportDocumentEntity, VesselTransportMeansEntity vesselTransportMeansEntity) {
        if (CollectionUtils.isEmpty(fishingActivities)) {
            return new ArrayList<>();
        }
        List<FishingActivityEntity> specifiedFishingActivityEntities = new ArrayList<>();

        for (FishingActivity fishingActivity : fishingActivities) {
            List<FishingGear> specifiedFishingGears = fishingActivity.getSpecifiedFishingGears();
            FishingActivityEntity fishActEntity = FishingActivityMapper.INSTANCE.mapToFishingActivityEntity(fishingActivity, faReportDocumentEntity, new FishingActivityEntity());
            if (CollectionUtils.isNotEmpty(specifiedFishingGears)){
                Set<FishingGearEntity> fishingGearEntitySet = new HashSet<>();
                for (FishingGear fishingGear : specifiedFishingGears) {
                    FishingGearEntity fishingGearEntity = FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear);
                    List<CodeType> roleCodes = fishingGear.getRoleCodes();
                    if (CollectionUtils.isNotEmpty(roleCodes)){
                        for (CodeType roleCode : roleCodes) {
                            fishingGearEntity.addFishingGearRole(FishingGearMapper.INSTANCE.mapToFishingGearRoleEntity(roleCode));
                        }
                    }
                    List<GearCharacteristic> applicableGearCharacteristics = fishingGear.getApplicableGearCharacteristics();
                    if (CollectionUtils.isNotEmpty(applicableGearCharacteristics)){
                        for (GearCharacteristic applicableGearCharacteristic : applicableGearCharacteristics) {
                            fishingGearEntity.addGearCharacteristic(GearCharacteristicsMapper.INSTANCE.mapToGearCharacteristicEntity(applicableGearCharacteristic));
                        }
                    }
                    fishingGearEntity.setFishingActivity(fishActEntity);
                    fishingGearEntitySet.add(fishingGearEntity);
                }
                fishActEntity.setFishingGears(fishingGearEntitySet);
            }

            List<FLAPDocument> specifiedFLAPDocuments = fishingActivity.getSpecifiedFLAPDocuments();
            if (CollectionUtils.isNotEmpty(specifiedFLAPDocuments)){
                for (FLAPDocument specifiedFLAPDocument : specifiedFLAPDocuments) {
                    FlapDocumentEntity flapDocumentEntity = FlapDocumentMapper.INSTANCE.mapToFlapDocumentEntity(specifiedFLAPDocument);
                    flapDocumentEntity.setFishingActivity(fishActEntity);
                    flapDocumentEntity.setVesselTransportMeans(vesselTransportMeansEntity);
                    fishActEntity.addFlapDocuments(flapDocumentEntity);
                }
            }

            List<IDType> ids = fishingActivity.getIDS();
            if (CollectionUtils.isNotEmpty(ids)){
                for (IDType id : ids) {
                    fishActEntity.addFishingActivityIdentifiers(FishingActivityIdentifierMapper.INSTANCE.mapToFishingActivityIdentifierEntity(id));
                }
            }

            List<FLUXCharacteristic> specifiedFLUXCharacteristics = fishingActivity.getSpecifiedFLUXCharacteristics();
            if (CollectionUtils.isNotEmpty(specifiedFLUXCharacteristics)){
                for (FLUXCharacteristic specifiedFLUXCharacteristic : specifiedFLUXCharacteristics) {
                    FluxCharacteristicEntity fluxCharacteristicEntity = FluxCharacteristicsMapper.INSTANCE.mapToFluxCharEntity(specifiedFLUXCharacteristic);
                    List<FLUXLocation> specifiedFLUXLocations = specifiedFLUXCharacteristic.getSpecifiedFLUXLocations();
                    if (CollectionUtils.isNotEmpty(specifiedFLUXLocations)){
                        for (FLUXLocation specifiedFLUXLocation : specifiedFLUXLocations) {
                            FluxLocationEntity fluxLocationEntity = FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(specifiedFLUXLocation);
                            fluxCharacteristicEntity.setFluxLocation(fluxLocationEntity);
                            fluxCharacteristicEntity.setFishingActivity(fishActEntity);
                        }
                    }
                    fishActEntity.addFluxCharacteristics(fluxCharacteristicEntity);
                }
            }

            List<FishingActivity> relatedFishingActivities = fishingActivity.getRelatedFishingActivities();

            List<FishingActivityEntity> relatedFishingActivityEntities = new ArrayList<>();

            for (FishingActivity relatedFishingActivity : relatedFishingActivities) {
                    List<FishingGear> relatedSpecifiedFishingGears = relatedFishingActivity.getSpecifiedFishingGears();
                    FishingActivityEntity relatedFishActEntity = FishingActivityMapper.INSTANCE.mapToFishingActivityEntity(relatedFishingActivity, faReportDocumentEntity, new FishingActivityEntity());
                    if (CollectionUtils.isNotEmpty(relatedSpecifiedFishingGears)){
                        Set<FishingGearEntity> fishingGearEntitySet = new HashSet<>();
                        for (FishingGear fishingGear : relatedSpecifiedFishingGears) {
                            FishingGearEntity fishingGearEntity = FishingGearMapper.INSTANCE.mapToFishingGearEntity(fishingGear);
                            List<CodeType> roleCodes = fishingGear.getRoleCodes();
                            if (CollectionUtils.isNotEmpty(roleCodes)){
                                for (CodeType roleCode : roleCodes) {
                                    fishingGearEntity.addFishingGearRole(FishingGearMapper.INSTANCE.mapToFishingGearRoleEntity(roleCode));
                                }
                            }
                            List<GearCharacteristic> applicableGearCharacteristics = fishingGear.getApplicableGearCharacteristics();
                            if (CollectionUtils.isNotEmpty(applicableGearCharacteristics)){
                                for (GearCharacteristic applicableGearCharacteristic : applicableGearCharacteristics) {
                                    fishingGearEntity.addGearCharacteristic(GearCharacteristicsMapper.INSTANCE.mapToGearCharacteristicEntity(applicableGearCharacteristic));
                                }
                            }
                            fishingGearEntity.setFishingActivity(relatedFishActEntity);
                            fishingGearEntitySet.add(fishingGearEntity);
                        }
                        relatedFishActEntity.setFishingGears(fishingGearEntitySet);
                    }

                    List<FLAPDocument> relatedSpecifiedFLAPDocuments = relatedFishingActivity.getSpecifiedFLAPDocuments();
                    if (CollectionUtils.isNotEmpty(relatedSpecifiedFLAPDocuments)){
                        for (FLAPDocument specifiedFLAPDocument : relatedSpecifiedFLAPDocuments) {
                            FlapDocumentEntity flapDocumentEntity = FlapDocumentMapper.INSTANCE.mapToFlapDocumentEntity(specifiedFLAPDocument);
                            flapDocumentEntity.setFishingActivity(relatedFishActEntity);
                            flapDocumentEntity.setVesselTransportMeans(vesselTransportMeansEntity);
                            relatedFishActEntity.addFlapDocuments(flapDocumentEntity);
                        }
                    }

                    List<IDType> relatedIds = relatedFishingActivity.getIDS();
                    if (CollectionUtils.isNotEmpty(relatedIds)){
                        for (IDType id : relatedIds) {
                            relatedFishActEntity.addFishingActivityIdentifiers(FishingActivityIdentifierMapper.INSTANCE.mapToFishingActivityIdentifierEntity(id));
                        }
                    }

                    List<FLUXCharacteristic> relatedsSpecifiedFLUXCharacteristics = relatedFishingActivity.getSpecifiedFLUXCharacteristics();
                    if (CollectionUtils.isNotEmpty(relatedsSpecifiedFLUXCharacteristics)){
                        for (FLUXCharacteristic specifiedFLUXCharacteristic : relatedsSpecifiedFLUXCharacteristics) {
                            FluxCharacteristicEntity fluxCharacteristicEntity = FluxCharacteristicsMapper.INSTANCE.mapToFluxCharEntity(specifiedFLUXCharacteristic);
                            List<FLUXLocation> specifiedFLUXLocations = specifiedFLUXCharacteristic.getSpecifiedFLUXLocations();
                            if (CollectionUtils.isNotEmpty(specifiedFLUXLocations)){
                                for (FLUXLocation specifiedFLUXLocation : specifiedFLUXLocations) {
                                    FluxLocationEntity fluxLocationEntity = FluxLocationMapper.INSTANCE.mapToFluxLocationEntity(specifiedFLUXLocation);
                                    fluxCharacteristicEntity.setFluxLocation(fluxLocationEntity);
                                    fluxCharacteristicEntity.setFishingActivity(relatedFishActEntity);
                                }
                            }
                            relatedFishActEntity.addFluxCharacteristics(fluxCharacteristicEntity);
                        }
                    }

                relatedFishingActivityEntities.add(relatedFishActEntity);
            }
            fishActEntity.setAllRelatedFishingActivities(relatedFishingActivityEntities);

            specifiedFishingActivityEntities.add(fishActEntity);

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
        if (CollectionUtils.isNotEmpty(fluxReportDocument.getIDS())){
            for (IDType idType : fluxReportDocument.getIDS()){
                FluxReportIdentifierEntity fluxReportIdentifierEntity = FluxReportIdentifierMapper.INSTANCE.mapToFluxReportIdentifierEntity(idType);
                fluxReportIdentifierEntity.setFluxReportDocument(fluxReportDocumentEntity);
                reportIdentifierEntitySet.add(fluxReportIdentifierEntity);
            }
        }
        fluxReportDocumentEntity.setFluxReportIdentifiers(reportIdentifierEntitySet);
        fluxReportDocumentEntity.setFaReportDocument(faReportDocumentEntity);
        return fluxReportDocumentEntity;
    }

}