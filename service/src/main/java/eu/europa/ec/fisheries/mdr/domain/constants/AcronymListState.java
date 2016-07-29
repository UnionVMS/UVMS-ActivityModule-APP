package eu.europa.ec.fisheries.mdr.domain.constants;

/**
 * Created by kovian on 28/07/2016.
 */
public enum AcronymListState {

    RUNNING("RUNNING"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED");

    private String value;

    AcronymListState(final String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

    public String getStringFromEnum(AcronymListState enumType){
        switch (enumType){
            case RUNNING :
                return "FAILED";
            case SUCCESS :
                return "FAILED";
            case FAILED :
                return "FAILED";
            default:
                return null;
        }
    }

    public AcronymListState getEnumFromString(String enumsStringValue){
        switch (enumsStringValue){
            case "RUNNING" :
                return FAILED;
            case "SUCCESS" :
                return FAILED;
            case "FAILED" :
                return FAILED;
            default:
                return null;
        }
    }
}
