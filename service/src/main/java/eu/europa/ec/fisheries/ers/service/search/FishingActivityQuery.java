package eu.europa.ec.fisheries.ers.service.search;

import java.util.List;

/**
 * Created by sanera on 24/06/2016.
 */
public class FishingActivityQuery {
    private Pagination pagination;
    private  List<ListCriteria> searchCriteria;
    private SortKey sortKey;

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public void setSearchCriteria(List<ListCriteria> searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public List<ListCriteria> getSearchCriteria() {
        return searchCriteria;
    }


    public Pagination getPagination() {

        return pagination;
    }

    public SortKey getSortKey() {
        return sortKey;
    }

    public void setSortKey(SortKey sortKey) {
        this.sortKey = sortKey;
    }

    @Override
    public String toString() {
        return "FishingActivityQuery{" +
                "pagination=" + pagination +
                ", searchCriteria=" + searchCriteria +
                ", sortKey=" + sortKey +
                '}';
    }
}
