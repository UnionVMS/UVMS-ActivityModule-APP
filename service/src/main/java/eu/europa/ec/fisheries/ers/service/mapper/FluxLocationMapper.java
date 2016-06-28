package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by padhyad on 6/14/2016.
 */
@Mapper
public abstract class FluxLocationMapper extends BaseMapper {

    public static FluxLocationMapper INSTANCE = Mappers.getMapper(FluxLocationMapper.class);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(fluxLocation.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(fluxLocation.getTypeCode()))"),
            @Mapping(target = "countryId", expression = "java(getIdType(fluxLocation.getCountryID()))"),
            @Mapping(target = "rfmoCode", expression = "java(getCodeType(fluxLocation.getRegionalFisheriesManagementOrganizationCode()))"),
            @Mapping(target = "longitude", expression = "java(getLongitude(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "latitude", expression = "java(getLatitude(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "countryIdSchemeId", expression = "java(getIdTypeSchemaId(fluxLocation.getCountryID()))"),
            @Mapping(target = "fluxLocationIdentifier", expression = "java(getIdType(fluxLocation.getID()))"),
            @Mapping(target = "fluxLocationIdentifierSchemeId", expression = "java(getIdTypeSchemaId(fluxLocation.getID()))"),
            @Mapping(target = "geopoliticalRegionCode", expression = "java(getCodeType(fluxLocation.getGeopoliticalRegionCode()))"),
            @Mapping(target = "geopoliticalRegionCodeListId", expression = "java(getCodeTypeListId(fluxLocation.getGeopoliticalRegionCode()))"),
            @Mapping(target = "name", expression = "java(getTextFromList(fluxLocation.getNames()))"),
            @Mapping(target = "sovereignRightsCountryCode", expression = "java(getIdType(fluxLocation.getSovereignRightsCountryID()))"),
            @Mapping(target = "jurisdictionCountryCode", expression = "java(getIdType(fluxLocation.getJurisdictionCountryID()))"),
            @Mapping(target = "altitude", expression = "java(getAltitude(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "systemId", expression = "java(getSystemId(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "fishingActivity", expression = "java(fishingActivityEntity)"),
            @Mapping(target = "fluxCharacteristics", expression = "java(getFluxCharacteristicEntities(fluxLocation.getApplicableFLUXCharacteristics(), fluxLocationEntity))"),
            @Mapping(target = "structuredAddresses", expression = "java(getStructuredAddressEntities(fluxLocation.getPostalStructuredAddresses(), fluxLocation.getPhysicalStructuredAddress(), fluxLocationEntity))")
    })
    public abstract FluxLocationEntity mapToFluxLocationEntity(FLUXLocation fluxLocation, FishingActivityEntity fishingActivityEntity, @MappingTarget FluxLocationEntity fluxLocationEntity);

    @Mappings({
            @Mapping(target = "typeCode", expression = "java(getCodeType(fluxLocation.getTypeCode()))"),
            @Mapping(target = "typeCodeListId", expression = "java(getCodeTypeListId(fluxLocation.getTypeCode()))"),
            @Mapping(target = "countryId", expression = "java(getIdType(fluxLocation.getCountryID()))"),
            @Mapping(target = "rfmoCode", expression = "java(getCodeType(fluxLocation.getRegionalFisheriesManagementOrganizationCode()))"),
            @Mapping(target = "longitude", expression = "java(getLongitude(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "latitude", expression = "java(getLatitude(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "countryIdSchemeId", expression = "java(getIdTypeSchemaId(fluxLocation.getCountryID()))"),
            @Mapping(target = "fluxLocationIdentifier", expression = "java(getIdType(fluxLocation.getID()))"),
            @Mapping(target = "fluxLocationIdentifierSchemeId", expression = "java(getIdTypeSchemaId(fluxLocation.getID()))"),
            @Mapping(target = "geopoliticalRegionCode", expression = "java(getCodeType(fluxLocation.getGeopoliticalRegionCode()))"),
            @Mapping(target = "geopoliticalRegionCodeListId", expression = "java(getCodeTypeListId(fluxLocation.getGeopoliticalRegionCode()))"),
            @Mapping(target = "name", expression = "java(getTextFromList(fluxLocation.getNames()))"),
            @Mapping(target = "sovereignRightsCountryCode", expression = "java(getIdType(fluxLocation.getSovereignRightsCountryID()))"),
            @Mapping(target = "jurisdictionCountryCode", expression = "java(getIdType(fluxLocation.getJurisdictionCountryID()))"),
            @Mapping(target = "altitude", expression = "java(getAltitude(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "systemId", expression = "java(getSystemId(fluxLocation.getSpecifiedPhysicalFLUXGeographicalCoordinate()))"),
            @Mapping(target = "faCatch", expression = "java(faCatchEntity)"),
            @Mapping(target = "fluxCharacteristics", expression = "java(getFluxCharacteristicEntities(fluxLocation.getApplicableFLUXCharacteristics(), fluxLocationEntity))"),
            @Mapping(target = "structuredAddresses", expression = "java(getStructuredAddressEntities(fluxLocation.getPostalStructuredAddresses(), fluxLocation.getPhysicalStructuredAddress(), fluxLocationEntity))")
    })
    public abstract FluxLocationEntity mapToFluxLocationEntity(FLUXLocation fluxLocation, FaCatchEntity faCatchEntity, @MappingTarget FluxLocationEntity fluxLocationEntity);

    protected Set<StructuredAddressEntity> getStructuredAddressEntities(List<StructuredAddress> postalStructuredAddresses, StructuredAddress physicalStructuredAddress, FluxLocationEntity fluxLocationEntity) {
        Set<StructuredAddressEntity> structuredAddressEntities = new HashSet<>();
        if (postalStructuredAddresses != null && !postalStructuredAddresses.isEmpty()) {
            for (StructuredAddress structuredAddress : postalStructuredAddresses) {
                StructuredAddressEntity structuredAddressEntity = StructuredAddressMapper.INSTANCE.mapToStructuredAddress(structuredAddress, fluxLocationEntity, new StructuredAddressEntity());
                structuredAddressEntities.add(structuredAddressEntity);
            }
        }
        if (physicalStructuredAddress != null) {
            structuredAddressEntities.add(StructuredAddressMapper.INSTANCE.mapToStructuredAddress(physicalStructuredAddress, fluxLocationEntity, new StructuredAddressEntity()));
        }
       return structuredAddressEntities;
    }

    protected Set<FluxCharacteristicEntity> getFluxCharacteristicEntities(List<FLUXCharacteristic> fluxCharacteristics, FluxLocationEntity fluxLocationEntity) {
        if (fluxCharacteristics == null || fluxCharacteristics.isEmpty()) {
            return null;
        }
        Set<FluxCharacteristicEntity> fluxCharacteristicEntities = new HashSet<>();
        for(FLUXCharacteristic fluxCharacteristic : fluxCharacteristics) {
            FluxCharacteristicEntity fluxCharacteristicEntity = FluxCharacteristicsMapper.INSTANCE.mapToFluxCharEntity(fluxCharacteristic, fluxLocationEntity, new FluxCharacteristicEntity());
            fluxCharacteristicEntities.add(fluxCharacteristicEntity);
        }
       return fluxCharacteristicEntities;
    }

    protected String getSystemId(FLUXGeographicalCoordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        if (coordinate.getSystemID() != null) {
            return coordinate.getSystemID().getValue();
        }
        else {
            return null;
        }
    }

    protected Double getLongitude(FLUXGeographicalCoordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        if (coordinate.getLongitudeMeasure() != null) {
            return coordinate.getLongitudeMeasure().getValue().doubleValue();
        } else {
            return null;
        }
    }

    protected Double getLatitude(FLUXGeographicalCoordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        if (coordinate.getLatitudeMeasure() != null) {
            return coordinate.getLatitudeMeasure().getValue().doubleValue();
        } else {
            return null;
        }
    }

    protected Double getAltitude(FLUXGeographicalCoordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        if (coordinate.getAltitudeMeasure() != null) {
            return coordinate.getAltitudeMeasure().getValue().doubleValue();
        } else {
            return null;
        }
    }
}
