package vinacert.vn.psample.data;

public class DataColumn {

    /** Creates a new instance of DataColumn */
    public DataColumn() {
    }
    public DataColumn(String columnName)
    {
        this.columnName=columnName;
    }

    private String columnName;
    private int dataType = java.sql.Types.VARCHAR;
    private Object defaultValue;
    private int columnIndex=-1;
    private DataTable dataTable;
    private boolean primaryKey=false;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }
}