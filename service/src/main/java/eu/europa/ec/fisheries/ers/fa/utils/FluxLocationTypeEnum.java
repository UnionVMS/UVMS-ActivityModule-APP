package eu.europa.ec.fisheries.ers.fa.utils;

/**
 * Created by padhyad on 6/29/2016.
 */
public enum FluxLocationTypeEnum {

    FA_CATCH_SPECIFIED("fa_catch_specified"),
    FA_CATCH_DESTINATION("fa_catch_destination"),
    FA_RELATED("fa_related");

    private String type;

    FluxLocationTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
