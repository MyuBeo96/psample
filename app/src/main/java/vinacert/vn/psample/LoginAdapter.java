package vinacert.vn.psample;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import vinacert.vn.psample.data.DataTable;

public class LoginAdapter extends BaseAdapter {
    private DataTable dt;
    private Context context;
    public LoginAdapter(Context context, DataTable dt)
    {
        this.context = context;
        this.dt = dt;
    }
    public int getCount() {
        return dt.getRowCount();
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }
    private View focusView;
    public View getView(int position, View convertView, ViewGroup parent)
    {
        String user_name = dt.getString(position, "user_name");
        TextView lblname = new TextView(context);
        lblname.setMinimumHeight(36);
        lblname.setText(user_name);
        return lblname  ;
    }
    public View createView(int position, DataTable dt){
        return null;
    }
}
