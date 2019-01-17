package vinacert.vn.psample.data;

import java.util.Hashtable;
import java.util.Vector;

public class DataTable {

    public Vector<DataRow> m_vector;
    public Vector<DataColumn> columns;
    private String tableName;

    public DataTable(String tableName){
        this.tableName=tableName;
        m_vector=new Vector<DataRow>();
        columns = new Vector<DataColumn>();
    }
    public void addColumn(DataColumn column)
    {
        if(columns.contains(column)){
            return;
        }
        columns.add(column);

    }


    public String getColumnName(int column) {
        return columns.get(column).getColumnName();
    }
    public boolean containColumn(String columnName){
        for(DataColumn c: columns){
            if(c.getColumnName().equals(columnName)){
                return true;
            }
        }
        return false;
    }
    public int getColumnCount() {
        if(columns==null){
            return 0;
        }
        return columns.size();

    }
    public Vector<String> columnName(){
        Vector<String> cols=new Vector<String>();
        for(int i=0; i<columns.size();i++){
            cols.add(columns.get(i).getColumnName());
        }
        return cols;
    }
    public DataColumn getDataColumn(String columnName){
        int index=this.getColumnIndex(columnName);
        if(index==-1)
            return null;
        return getDataColumn(index);
    }
    public DataColumn getDataColumn(int index){
        if(index<0 || index>=columns.size())
            return null;
        return columns.get(index);
    }
    public int getRowCount() {
        return m_vector.size();
    }
    public Object getValue(int row, int col) {
        Object obj = ((DataRow) m_vector.get(row)).getValue(col);
        if(obj == null){
            return "";
        }
        return obj;
    }
    public Object getValue(int row, String columnName) {
        int col=this.getColumnIndex(columnName);

        return getValue(row, col);
    }
    public String getString(int row, int col)
    {
        return ((DataRow) m_vector.get(row)).getString(col);
    }
    public String getString(int row, String columnName)
    {
        return ((DataRow) m_vector.get(row)).getString(columnName);
    }
    public float getFloat(int row, int col)
    {
        return ((DataRow) m_vector.get(row)).getFloat(col);
    }
    public float getFloat(int row, String columnName)
    {
        return ((DataRow) m_vector.get(row)).getFloat(columnName);
    }

    public void setValue(Object value, int nrow, int col) {

        DataRow row=(DataRow) m_vector.get(nrow);
        row.setElementAt(value, col);

    }
    public void setValue(Object value, int row, String columnName){
        int col=this.getColumnIndex(columnName);
        setValue(value, row, col);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public DataRow newRow()
    {
        DataRow row=new DataRow();
        row.setTable(this);
        for(int i=0; i<columns.size(); i++){
            row.add("");
        }
        row.setRowIndex(m_vector.size());
        return row;
    }
    public void addRow(DataRow row){
        row.setTable(this);
        m_vector.add(row);
        //int rowIndex=m_vector.size()-1;
    }
    public int rowCount()
    {
        return m_vector.size();
    }
    public Vector<DataRow> getRows()
    {
        return m_vector;
    }
    public void insertNewRow(DataRow row){
        //row.setTable(this);
        m_vector.add(row);
        //int rowIndex=m_vector.size()-1;
    }
    public void removeRow(int nRow){
        m_vector.remove(nRow);
    }
    public void clearRow(){
        m_vector.clear();

    }
    public void deleteRow(int nRow)
    {
        m_vector.remove(nRow);
    }
    public void updateRow(Object[] row, int nRow){
        DataRow r=(DataRow) m_vector.elementAt(nRow);
        r.removeAllElements();
        for(int i=0; i<row.length; i++){
            r.add(row[i]);
        }
    }
    public DataRow getDataRow(int row){
        return (DataRow) m_vector.get(row);
    }
    public int getColumnIndex(String columnName){
        for(int i=0; i<columns.size(); i++){
            if(columns.get(i).getColumnName().equalsIgnoreCase(columnName))
                return i;
        }
        return -1;
    }
    public Hashtable<String, Object> getData(int nRow)
    {
        boolean hasRow=false;
        Hashtable<String, Object> h=new Hashtable<String, Object>();
        if(nRow>=0)
            hasRow=true;
        for(int i=0; i<columns.size(); i++){

            if(hasRow)
                h.put(columns.get(i).getColumnName(), this.getValue(nRow, i));
            else
                h.put(columns.get(i).getColumnName(), "");
        }

        return h;
    }
}
