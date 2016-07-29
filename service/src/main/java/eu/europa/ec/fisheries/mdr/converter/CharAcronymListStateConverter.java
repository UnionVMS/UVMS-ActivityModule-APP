package eu.europa.ec.fisheries.mdr.converter;

import eu.europa.ec.fisheries.mdr.domain.constants.AcronymListState;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by kovian on 29/07/2016.
 */
@Converter
public class CharAcronymListStateConverter implements AttributeConverter<AcronymListState, String> {

    public CharAcronymListStateConverter() {
    }

    @Override
    public String convertToDatabaseColumn(AcronymListState attribute) {
        return attribute.getValue();
    }

    @Override
    public AcronymListState convertToEntityAttribute(String dbData) {
        return AcronymListState.valueOf(dbData);
    }
}