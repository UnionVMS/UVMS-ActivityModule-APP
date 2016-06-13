package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.StructuredAddressEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.StructuredAddress;

/**
 * Created by padhyad on 6/13/2016.
 */
@Mapper
public abstract class StructuredAddressMapper {

    public static StructuredAddressMapper INSTANCE = Mappers.getMapper(StructuredAddressMapper.class);

    @Mappings({
            @Mapping(target = "blockName", expression = "java(structuredAddress.getBlockName().getValue())"),
            @Mapping(target = "buildingName", expression = "java(structuredAddress.getBuildingName().getValue())"),
            @Mapping(target = "cityName", expression = "java(structuredAddress.getCityName().getValue())"),
            @Mapping(target = "citySubdivisionName", expression = "java(structuredAddress.getCitySubDivisionName().getValue())"),
            @Mapping(target = "country", expression = "java(Integer.parseInt(structuredAddress.getCountryID().getValue()))"),
            @Mapping(target = "countryName", expression = "java(structuredAddress.getCountryName().getValue())"),
            @Mapping(target = "countrySubdivisionName", expression = "java(structuredAddress.getCountrySubDivisionName().getValue())"),
            @Mapping(target = "addressId", expression = "java(structuredAddress.getID().getValue())"),
            @Mapping(target = "plotId", expression = "java(structuredAddress.getPlotIdentification().getValue())"),
            @Mapping(target = "postOfficeBox", expression = "java(structuredAddress.getPostOfficeBox().getValue())"),
            @Mapping(target = "postcode", expression = "java(structuredAddress.getPostcodeCode().getValue())"),
            @Mapping(target = "streetname", expression = "java(structuredAddress.getStreetName().getValue())")
    })
    public abstract StructuredAddressEntity mapToStructuredAddress(StructuredAddress structuredAddress);
}
