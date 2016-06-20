package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.ers.fa.entities.StructuredAddressEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.StructuredAddress;

/**
 * Created by padhyad on 6/13/2016.
 */
@Mapper
public abstract class StructuredAddressMapper extends BaseMapper {

    public static StructuredAddressMapper INSTANCE = Mappers.getMapper(StructuredAddressMapper.class);

    @Mappings({
            @Mapping(target = "blockName", expression = "java(getTextType(structuredAddress.getBlockName()))"),
            @Mapping(target = "buildingName", expression = "java(getTextType(structuredAddress.getBuildingName()))"),
            @Mapping(target = "cityName", expression = "java(getTextType(structuredAddress.getCityName()))"),
            @Mapping(target = "citySubdivisionName", expression = "java(getTextType(structuredAddress.getCitySubDivisionName()))"),
            @Mapping(target = "country", expression = "java(getIdType(structuredAddress.getCountryID()))"),
            @Mapping(target = "countryName", expression = "java(getTextType(structuredAddress.getCountryName()))"),
            @Mapping(target = "countrySubdivisionName", expression = "java(getTextType(structuredAddress.getCountrySubDivisionName()))"),
            @Mapping(target = "addressId", expression = "java(getIdType(structuredAddress.getID()))"),
            @Mapping(target = "plotId", expression = "java(getTextType(structuredAddress.getPlotIdentification()))"),
            @Mapping(target = "postOfficeBox", expression = "java(getTextType(structuredAddress.getPostOfficeBox()))"),
            @Mapping(target = "postcode", expression = "java(getCodeType(structuredAddress.getPostcodeCode()))"),
            @Mapping(target = "streetname", expression = "java(getTextType(structuredAddress.getStreetName()))"),
            @Mapping(target = "contactParty", expression = "java(contactPartyEntity)")
    })
    public abstract StructuredAddressEntity mapToStructuredAddress(StructuredAddress structuredAddress, ContactPartyEntity contactPartyEntity, @MappingTarget StructuredAddressEntity structuredAddressEntity);
}
