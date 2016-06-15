package eu.europa.ec.fisheries.ers.service.mapper;

import eu.europa.ec.fisheries.ers.fa.entities.ContactPartyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._18.ContactParty;

/**
 * Created by padhyad on 6/8/2016.
 */
@Mapper
public abstract class ContactPartyMapper extends BaseMapper {

    public static ContactPartyMapper INSTANCE = Mappers.getMapper(ContactPartyMapper.class);

    @Mappings({
            @Mapping(target = "roleCode", expression = "java(getCodeTypeFromList(contactParty.getRoleCodes()))"),
            @Mapping(target = "roleCodeListId", expression = "java(getCodeTypeListIdFromList(contactParty.getRoleCodes()))")
    })
    public abstract ContactPartyEntity mapToContactPartyEntity(ContactParty contactParty);
}
