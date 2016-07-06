package eu.europa.ec.fisheries.ers.service.search;

/**
 * Created by sanera on 01/07/2016.
 */
public class SortKey {

    private SearchKey field;
    private SortOrder order;


    public SearchKey getField() {
        return field;
    }

    public void setField(SearchKey field) {
        this.field = field;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

    public SortKey(SearchKey field, SortOrder order) {
        this.field = field;
        this.order = order;
    }
    public SortKey(){

    }

    @Override
    public String toString() {
        return "SortKey{" +
                "field=" + field +
                ", order=" + order +
                '}';
    }
}
