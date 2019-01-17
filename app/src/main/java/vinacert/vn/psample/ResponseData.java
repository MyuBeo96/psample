package vinacert.vn.psample;

import vinacert.vn.psample.data.DataTable;

public interface ResponseData {
    public void responseText(int code, String data);
    public void responseDataTable(int code, DataTable dt);
}
