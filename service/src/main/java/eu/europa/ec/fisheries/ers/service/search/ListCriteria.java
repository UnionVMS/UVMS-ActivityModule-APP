package eu.europa.ec.fisheries.ers.service.search;

/**
 * Created by sanera on 24/06/2016.
 */
public class ListCriteria {

    private SearchKey key;
    private String value;

    public void setKey(SearchKey key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SearchKey getKey() {

        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ListCriteria{" +
                "key=" + key +
                ", value='" + value + '\'' +
                '}';
    }
}
