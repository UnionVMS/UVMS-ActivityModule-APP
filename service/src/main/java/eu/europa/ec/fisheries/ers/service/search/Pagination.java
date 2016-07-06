package eu.europa.ec.fisheries.ers.service.search;

/**
 * Created by sanera on 24/06/2016.
 */
public class Pagination {
    public void setPage(int page) {
        this.page = page;
    }

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    public int getPage() {

        return page;
    }

    public int getListSize() {
        return listSize;
    }

    private int page;
    private int listSize;

    public Pagination(int page, int listSize) {
        this.page = page;
        this.listSize = listSize;
    }

    public Pagination(){

    }

    @Override
    public String toString() {
        return "Pagination{" +
                "page=" + page +
                ", listSize=" + listSize +
                '}';
    }
}
