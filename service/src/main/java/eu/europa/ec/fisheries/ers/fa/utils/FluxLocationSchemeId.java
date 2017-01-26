package eu.europa.ec.fisheries.ers.fa.utils;

/**
 * Created by sanera on 24/01/2017.
 */
public enum FluxLocationSchemeId {

    TERRITORY("territory"),
    FAO_AREA("faoArea"),
    ICES_STAT_RECTANGLE("icesStatRectangle"),
    EFFORT_ZONE("effortZone"),
    GFCM_GSA("gfcmGsa"),
    GFCM_STAT_RECTANGLE("gfcmStatRectangle"),
    RFMO("rfmo");


    private String columnName;

    FluxLocationSchemeId(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return this.columnName;
    }

}
