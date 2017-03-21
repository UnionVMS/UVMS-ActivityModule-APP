/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package eu.europa.ec.fisheries.ers.service.mapper;

import static org.mockito.internal.util.collections.Sets.newSet;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearRoleEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearCharacteristicEntity;
import eu.europa.ec.fisheries.ers.fa.entities.RegistrationEventEntity;
import eu.europa.ec.fisheries.ers.fa.entities.RegistrationLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.SizeDistributionClassCodeEntity;
import eu.europa.ec.fisheries.ers.fa.entities.SizeDistributionEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.UnitCodeEnum;
import eu.europa.ec.fisheries.ers.service.dto.view.IdentifierDto;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselCountry;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.QuantityType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.TextType;

/**
 * TODO create test
 */
@Slf4j
@NoArgsConstructor
public class BaseMapper {

    public static Set<SizeDistributionClassCodeEntity> mapToSizeDistributionClassCodes(List<CodeType> codeTypes, SizeDistributionEntity sizeDistributionEntity) {
        if (codeTypes == null || codeTypes.isEmpty()) {
            Collections.emptySet();
        }
        Set<SizeDistributionClassCodeEntity> classCodes = new HashSet<>();
        for (CodeType codeType : codeTypes) {
            SizeDistributionClassCodeEntity entity = SizeDistributionMapper.INSTANCE.mapToSizeDistributionClassCodeEntity(codeType);
            entity.setSizeDistribution(sizeDistributionEntity);
            classCodes.add(entity);
        }
        return classCodes;
    }

    public static RegistrationLocationEntity mapToRegistrationLocationEntity(RegistrationLocation registrationLocation, RegistrationEventEntity registrationEventEntity) {
        if (registrationLocation == null) {
            return null;
        }
        RegistrationLocationEntity registrationLocationEntity = RegistrationLocationMapper.INSTANCE.mapToRegistrationLocationEntity(registrationLocation);
        registrationLocationEntity.setRegistrationEvent(registrationEventEntity);
        return registrationLocationEntity;
    }

    public static Set<FishingGearRoleEntity> mapToFishingGears(List<CodeType> codeTypes, FishingGearEntity fishingGearEntity) {
        if (CollectionUtils.isEmpty(codeTypes)) {
            Collections.emptySet();
        }
        Set<FishingGearRoleEntity> fishingGearRoles = newSet();
        for (CodeType codeType : codeTypes) {
            FishingGearRoleEntity gearRole = FishingGearMapper.INSTANCE.mapToFishingGearRoleEntity(codeType);
            gearRole.setFishingGear(fishingGearEntity);
            fishingGearRoles.add(gearRole);
        }
        return fishingGearRoles;
    }

    public static Set<GearCharacteristicEntity> getGearCharacteristicEntities(List<GearCharacteristic> gearCharacteristics, FishingGearEntity fishingGearEntity) {
        if (CollectionUtils.isEmpty(gearCharacteristics)) {
            return Collections.emptySet();
        }
        Set<GearCharacteristicEntity> gearCharacteristicEntities = newSet();
        for (GearCharacteristic gearCharacteristic : gearCharacteristics) {
            GearCharacteristicEntity gearCharacteristicEntity = GearCharacteristicsMapper.INSTANCE.mapToGearCharacteristicEntity(gearCharacteristic, fishingGearEntity, new GearCharacteristicEntity());
            gearCharacteristicEntities.add(gearCharacteristicEntity);
        }
        return gearCharacteristicEntities;
    }

    public static FluxPartyEntity getFluxPartyEntity(FLUXParty fluxParty, FluxReportDocumentEntity fluxReportDocumentEntity) {
        if (fluxParty == null) {
            return null;
        }
        FluxPartyEntity fluxPartyEntity = FluxPartyMapper.INSTANCE.mapToFluxPartyEntity(fluxParty);
        fluxPartyEntity.setFluxReportDocument(fluxReportDocumentEntity);
        return fluxPartyEntity;
    }

    public static Set<FluxReportIdentifierEntity> mapToFluxReportIdentifierEntities(List<IDType> idTypes, FluxReportDocumentEntity fluxReportDocumentEntity) {
        if (idTypes == null || idTypes.isEmpty()) {
            Collections.emptySet();
        }
        Set<FluxReportIdentifierEntity> identifiers = newSet();
        for (IDType idType : idTypes) {
            FluxReportIdentifierEntity entity = FluxReportDocumentMapper.INSTANCE.mapToFluxReportIdentifierEntity(idType);
            entity.setFluxReportDocument(fluxReportDocumentEntity);
            identifiers.add(entity);
        }
        return identifiers;
    }

    public static FishingTripEntity getSpecifiedFishingTrip(FishingActivityEntity activityEntity) {
        FishingTripEntity fishingTripEntity = null;
        Set<FishingTripEntity> fishingTrips = activityEntity.getFishingTrips();
        if (!CollectionUtils.isEmpty(fishingTrips)) {
            fishingTripEntity = activityEntity.getFishingTrips().iterator().next();
        }
        return fishingTripEntity;
    }

    public static Set<FluxLocationEntity> getRelatedFluxLocations(FishingActivityEntity activityEntity) {
        FishingTripEntity specifiedFishingTrip = getSpecifiedFishingTrip(activityEntity);
        Set<FluxLocationEntity> relatedFluxLocations = newSet();
        if (specifiedFishingTrip != null) {
            relatedFluxLocations = getRelatedFluxLocations(specifiedFishingTrip);
        }
        return relatedFluxLocations;
    }

    public static Set<FluxLocationEntity> getRelatedFluxLocations(FishingTripEntity tripEntity) {
        Set<FluxLocationEntity> fluxLocations = newSet();
        FishingActivityEntity fishingActivity = tripEntity.getFishingActivity();
        if (fishingActivity != null) {
            fluxLocations = fishingActivity.getFluxLocations();
        }
        return fluxLocations;
    }

    public static List<AssetListCriteriaPair> mapToAssetListCriteriaPairList(Set<IdentifierDto> identifierDtoSet) {
        List<AssetListCriteriaPair> criteriaList = new ArrayList<>();
        for (IdentifierDto identifierDto : identifierDtoSet) {
            AssetListCriteriaPair criteriaPair = new AssetListCriteriaPair();
            VesselIdentifierSchemeIdEnum identifierSchemeId = identifierDto.getIdentifierSchemeId();
            ConfigSearchField key = VesselIdentifierMapper.INSTANCE.map(identifierSchemeId);
            String identifierId = identifierDto.getIdentifierId();
            if (key != null && identifierId != null) {
                criteriaPair.setKey(key);
                criteriaPair.setValue(identifierId);
                criteriaList.add(criteriaPair);
            }
        }
        return criteriaList;
    }

    public static Double getCalculatedMeasure(MeasureType measureType) {
        if (measureType == null) {
            return null;
        }
        UnitCodeEnum unitCodeEnum = UnitCodeEnum.getUnitCode(measureType.getUnitCode());
        if (unitCodeEnum != null) {
            BigDecimal measuredValue = measureType.getValue();
            BigDecimal result = measuredValue.multiply(new BigDecimal(unitCodeEnum.getConversionFactor()));
            return result.doubleValue();
        } else {
            return null;
        }
    }

    public static String getLanguageIdFromList(List<TextType> textTypes) {
        if (CollectionUtils.isEmpty(textTypes)) {
            return null;
        }
        return textTypes.get(0).getLanguageID();
    }

    public static String getTextFromList(List<TextType> textTypes) {
        if (CollectionUtils.isEmpty(textTypes)) {
            return null;
        }
        return textTypes.get(0).getValue();
    }

    protected static String getFirstFishingActivity(List<FishingTripEntity> fishingTripList) {
        if (CollectionUtils.isEmpty(fishingTripList) || fishingTripList.get(0).getFishingActivity() == null) {
            return null;
        }
        return fishingTripList.get(0).getFishingActivity().getTypeCode();

    }

    protected static XMLGregorianCalendar getFirstFishingActivityDateTime(List<FishingTripEntity> fishingTripList) {
        if (CollectionUtils.isEmpty(fishingTripList) || fishingTripList.get(0).getFishingActivity() == null
                || fishingTripList.get(0).getFishingActivity().getCalculatedStartTime() == null) {
            return null;
        }

        return convertToXMLGregorianCalendar(fishingTripList.get(0).getFishingActivity().getCalculatedStartTime(), false);
    }

    protected static String getLastFishingActivity(List<FishingTripEntity> fishingTripList) {
        if (CollectionUtils.isEmpty(fishingTripList) || fishingTripList.get(fishingTripList.size() - 1).getFishingActivity() == null) {
            return null;
        }
        int totalFishTripEntityCount = fishingTripList.size();
        return fishingTripList.get(totalFishTripEntityCount - 1).getFishingActivity().getTypeCode();
    }

    protected static XMLGregorianCalendar getLastFishingActivityDateTime(List<FishingTripEntity> fishingTripList) {
        if (CollectionUtils.isEmpty(fishingTripList) || fishingTripList.get(fishingTripList.size() - 1).getFishingActivity() == null
                || fishingTripList.get(fishingTripList.size() - 1).getFishingActivity().getCalculatedStartTime() == null) {
            return null;
        }
        int totalFishTripEntityCount = fishingTripList.size();
        return convertToXMLGregorianCalendar(fishingTripList.get(totalFishTripEntityCount - 1).getFishingActivity().getCalculatedStartTime(), false);
    }

    protected static List<VesselIdentifierType> getVesselIdLists(List<FishingTripEntity> fishingTripList) {
        if (CollectionUtils.isEmpty(fishingTripList) || fishingTripList.get(fishingTripList.size() - 1).getFishingActivity() == null ||
                fishingTripList.get(fishingTripList.size() - 1).getFishingActivity().getFaReportDocument() == null ||
                fishingTripList.get(fishingTripList.size() - 1).getFishingActivity().getFaReportDocument().getVesselTransportMeans() == null) {
            return Collections.emptyList();
        }
        int totalFishTripEntityCount = fishingTripList.size();
        FishingTripEntity fishingTripEntity = fishingTripList.get(totalFishTripEntityCount - 1);
        VesselTransportMeansEntity vesselTransportMeansEntity = fishingTripEntity.getFishingActivity().getFaReportDocument().getVesselTransportMeans();
        Set<VesselIdentifierEntity> vesselIdentifierEntities = vesselTransportMeansEntity.getVesselIdentifiers();
        List<VesselIdentifierType> vesselIdentifierTypes = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(vesselIdentifierEntities)) {
            for (VesselIdentifierEntity vesselIdentifierEntity : vesselIdentifierEntities) {
                VesselIdentifierType vesselIdentifierType = new VesselIdentifierType();
                vesselIdentifierType.setKey(VesselIdentifierSchemeIdEnum.valueOf(vesselIdentifierEntity.getVesselIdentifierSchemeId()));
                vesselIdentifierType.setValue(vesselIdentifierEntity.getVesselIdentifierId());
                vesselIdentifierTypes.add(vesselIdentifierType);
            }
        }

        return vesselIdentifierTypes;
    }

    protected static String getFlagState(List<FishingTripEntity> fishingTripList) {
        if (CollectionUtils.isEmpty(fishingTripList) || fishingTripList.get(fishingTripList.size() - 1).getFishingActivity() == null ||
                fishingTripList.get(fishingTripList.size() - 1).getFishingActivity().getFaReportDocument() == null ||
                fishingTripList.get(fishingTripList.size() - 1).getFishingActivity().getFaReportDocument().getVesselTransportMeans() == null) {
            return null;
        }
        int totalFishTripEntityCount = fishingTripList.size();
        FishingTripEntity fishingTripEntity = fishingTripList.get(totalFishTripEntityCount - 1);
        VesselTransportMeansEntity vesselTransportMeansEntity = fishingTripEntity.getFishingActivity().getFaReportDocument().getVesselTransportMeans();
        return vesselTransportMeansEntity.getCountry();
    }

    protected static Double getTotalDuration(List<FishingTripEntity> fishingTripList) {
        if (CollectionUtils.isEmpty(fishingTripList)) {
            return 0d;
        }
        FishingActivityEntity firstFishingActivity = fishingTripList.get(0).getFishingActivity();
        FishingActivityEntity lastFishingActivity = fishingTripList.get(fishingTripList.size() - 1).getFishingActivity();

        Double duration = 0d;
        if (firstFishingActivity != null && firstFishingActivity.getCalculatedStartTime() != null && lastFishingActivity != null && lastFishingActivity.getCalculatedStartTime() != null) {
            Date startDate = firstFishingActivity.getCalculatedStartTime();
            Date endDate = lastFishingActivity.getCalculatedStartTime();
            duration = (double) (endDate.getTime() - startDate.getTime());
        }
        return duration;
    }

    protected static int getNumberOfCorrections(List<FishingTripEntity> fishingTripList) {
        if (CollectionUtils.isEmpty(fishingTripList)) {
            return 0;
        }
        int noOfCorrections = 0;
        for (FishingTripEntity fishingTripEntity : fishingTripList) {
            if (getCorrection(fishingTripEntity.getFishingActivity())) {
                noOfCorrections++;
            }
        }
        return noOfCorrections;
    }

    public static boolean getCorrection(FishingActivityEntity entity) {
        if (entity == null || entity.getFaReportDocument() == null || entity.getFaReportDocument().getFluxReportDocument() == null) {
            return false;
        }

        FluxReportDocumentEntity fluxReportDocument = entity.getFaReportDocument().getFluxReportDocument();
        return fluxReportDocument.getReferenceId() != null && fluxReportDocument.getReferenceId().length() != 0;
    }

    protected static XMLGregorianCalendar convertToXMLGregorianCalendar(Date dateTime, boolean includeTimeZone) {
        XMLGregorianCalendar calendar = null;
        try {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(dateTime.getTime());
            calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
            if (!includeTimeZone) {
                calendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED); //If we do not want timeZone to be included, set this
            }
        } catch (DatatypeConfigurationException e) {
            log.error(e.getMessage(), e);
        }
        return calendar;
    }

    protected String getIdType(IDType idType) {
        return (idType == null) ? null : idType.getValue();
    }

    protected String getIdTypeSchemaId(IDType idType) {
        return (idType == null) ? null : idType.getSchemeID();
    }

    protected String getCodeType(CodeType codeType) {
        return (codeType == null) ? null : codeType.getValue();
    }

    protected String getCodeTypeListId(CodeType codeType) {
        return (codeType == null) ? null : codeType.getListID();
    }

    protected Double getCalculatedQuantity(QuantityType quantityType) {
        if (quantityType == null) {
            return null;
        }
        UnitCodeEnum unitCodeEnum = UnitCodeEnum.getUnitCode(quantityType.getUnitCode());
        if (unitCodeEnum != null) {
            BigDecimal quantity = quantityType.getValue();
            BigDecimal result = quantity.multiply(new BigDecimal(unitCodeEnum.getConversionFactor()));
            return result.doubleValue();
        } else {
            return null;
        }
    }

    protected String getCountry(VesselCountry country) {
        if (country == null) {
            return null;
        }
        return getIdType(country.getID());
    }

    protected Map<String, String> getReportIdMap(Collection<FluxReportIdentifierEntity> identifiers) {
        Map<String, String> recordMap = new HashMap<>();
        for (FluxReportIdentifierEntity identifier : identifiers) {
            recordMap.put(identifier.getFluxReportIdentifierId(), identifier.getFluxReportIdentifierSchemeId());
        }
        return recordMap;
    }
}