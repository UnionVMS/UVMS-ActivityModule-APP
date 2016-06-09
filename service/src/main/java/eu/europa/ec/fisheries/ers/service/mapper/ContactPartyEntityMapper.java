package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.ContactParty;
import un.unece.uncefact.data.standard.unqualifieddatatype._18.CodeType;

import java.util.List;

/**
 * Created by padhyad on 6/8/2016.
 */
public abstract class ContactPartyEntityMapper {

    public static ContactPartyEntityMapper INSTANCE = Mappers.getMapper(ContactPartyEntityMapper.class);

    @Mappings({
            @Mapping(target = "title", expression = "java(getRoleCode(contactParty.getRoleCodes()))"),
            @Mapping(target = "roleCodeListId", expression = "java(getRoleCodeListId(contactParty.getRoleCodes()))")
    })
    public abstract ContactPartyEntity mapToContactPartyEntity(ContactParty contactParty);

    protected String getRoleCode(List<CodeType> codeTypes) {
        return (codeTypes == null || !codeTypes.isEmpty()) ? null : codeTypes.get(0).getValue();
    }

    protected String getRoleCodeListId(List<CodeType> codeTypes) {
        return (codeTypes == null || !codeTypes.isEmpty()) ? null : codeTypes.get(0).getListID();
    }
}
