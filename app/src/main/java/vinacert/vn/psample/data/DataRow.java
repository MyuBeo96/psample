package vinacert.vn.psample.data;

public class DataRow extends java.util.Vector<Object>{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /** Creates a new instance of DataRow */
    public DataRow(){}
    private DataTable table;
    private int rowIndex;
    public Object getValue(int index){
        if(index==-1)
        {
            return  null;
        }
        return this.elementAt(index);
    }
    public Object getValue(String columnName){
        int index=table.getColumnIndex(columnName);
        if(index==-1)
        {
            return "";
        }

        return getValue(index);
    }
    public String getString(int index)
    {
        Object obj = this.getValue(index);
        if(obj != null)
        {
            return obj.toString();
        }
        return "";
    }
    public String getString(String columnName)
    {
        int index= table.getColumnIndex(columnName);
        return getString(index);
    }
    public float getFloat(int index)
    {
        Object obj = this.getValue(index);
        if(obj != null)
        {
            return Float.parseFloat(obj.toString());
        }
        return 0;
    }
    public float getFloat(String columnName)
    {
        int index= table.getColumnIndex(columnName);
        return getFloat(index);
    }
    public void setValue(Object val, int colIndex)
    {
        if(colIndex!=-1){
            if(this.table.m_vector.indexOf(this)==-1)
                this.setElementAt(val, colIndex);
            else
                this.table.setValue(val, rowIndex, colIndex);
        }
    }
    public void setValue(Object val, String columnName)
    {
        int index=table.getColumnIndex(columnName);
        if(index==-1)
            return;
        Object tmp_val=this.getValue(index);
        if(tmp_val!=null && val !=null){
            if( !tmp_val.equals(val))
            {
                setValue(val, index);
            }
        }else
        {
            setValue(val, index);
        }


    }
    public void delete()
    {
        this.table.deleteRow(rowIndex);
    }
    public DataTable getTable() {
        return table;
    }

    public void setTable(DataTable table) {
        this.table = table;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }
}
