package eu.europa.ec.fisheries.ers.fa.utils;

/**
 * Created by padhyad on 6/29/2016.
 */
public enum StructuredAddressTypeEnum {

    FLUX_PHYSICAL("flux_physical"),
    FLUX_POSTAL("flux_postal"),
    CANTACT_PARTY_SPECIFIED("contact_party_specified");

    private String type;

    StructuredAddressTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
