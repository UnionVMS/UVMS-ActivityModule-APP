package eu.europa.ec.fisheries.ers.service.search;

/**
 * Created by sanera on 20/01/2017.
 */
public class GroupCriteriaMapper {

    private String tableJoin;
    private String columnName;


    public GroupCriteriaMapper(String tableJoin, String columnName) {
        this.tableJoin = tableJoin;
        this.columnName = columnName;
    }

    public GroupCriteriaMapper() {
        super();
    }

    public String getTableJoin() {
        return tableJoin;
    }

    public void setTableJoin(String tableJoin) {
        this.tableJoin = tableJoin;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
