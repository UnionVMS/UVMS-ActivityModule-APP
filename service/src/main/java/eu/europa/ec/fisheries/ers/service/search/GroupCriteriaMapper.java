package eu.europa.ec.fisheries.ers.service.search;

/**
 * Created by sanera on 20/01/2017.
 */
public class GroupCriteriaMapper {

    private String tableJoin;
    private String columnName;
    private String methodName; // This is a method name which is used to map criteria to FaCatchSummaryCustomEntity object


    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public GroupCriteriaMapper(String tableJoin, String columnName, String methodName) {
        this.tableJoin = tableJoin;
        this.columnName = columnName;
        this.methodName = methodName;
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
