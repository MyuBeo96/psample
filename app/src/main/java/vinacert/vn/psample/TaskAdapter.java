package vinacert.vn.psample;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import vinacert.vn.psample.data.DataTable;


public class TaskAdapter extends BaseAdapter
{
    private DataTable dt;
    private Context context;
    public TaskAdapter(Context context, DataTable dt)
    {
        this.context = context;
        this.dt = dt;
    }
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public int getCount() {
        return dt.getRowCount();
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //Bitmap bmp = (Bitmap) arrPictures.get(position);
        //com.bzb.ui.BzBImageView imgView = new com.bzb.ui.BzBImageView();
        //imgView.setImageBitmap(bmp);
        //return imgView;
        LinearLayout pnView = new LinearLayout(context);
        pnView.setMinimumHeight(36);
        pnView.setPadding(6, 2, 2,6);
        pnView.setOrientation(LinearLayout.VERTICAL);


        String product_name = dt.getString(position, "product_name");
        String company_name = dt.getString(position, "partner_name");
        String phone = dt.getString(position, "phone");
        String address = dt.getString(position, "address");
        String contact_name = dt.getString(position, "contact_name");
        String mobile = dt.getString(position, "mobile");
        String description = dt.getString(position, "description");
        String start_date = dt.getString(position, "start_date");
        String end_date = dt.getString(position, "end_date");

        TextView lbl= new TextView(context);
        lbl.setTextColor(Color.RED);
        lbl.setText(product_name);
        pnView.addView(lbl, new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView lbltime= new TextView(context);
        int index = start_date.lastIndexOf(":");
        if(index !=-1)
        {
            start_date = start_date.substring(0, index);
        }

        index = end_date.lastIndexOf(":");
        if(index !=-1)
        {
            end_date = end_date.substring(0, index);
        }
        lbltime.setText(start_date + " -> " + end_date);

        pnView.addView(lbltime, new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        if(!company_name.equals(""))
        {
            TextView lblcompany_name = new TextView(context);
            lblcompany_name.setText("Công ty: ");
            pnView.addView(lblcompany_name);
            TextView editcompany_name = new TextView(context);
            editcompany_name.setText(company_name);
            editcompany_name.setTextColor(Color.GRAY);
            pnView.addView(editcompany_name, new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }


        if(!phone.equals(""))
        {
            TextView lbltel = new TextView(context);
            lbltel.setText("Điện thoại: ");
            pnView.addView(lbltel);
            TextView edittel = new TextView(context);
            edittel.setText(phone);
            edittel.setTextColor(Color.GRAY);
            pnView.addView(edittel, new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        if(!address.equals(""))
        {
            TextView lblTitle = new TextView(context);
            lblTitle.setText("Địa chỉ: ");
            pnView.addView(lblTitle);
            TextView edit = new TextView(context);
            edit.setText(address);
            edit.setTextColor(Color.GRAY);
            pnView.addView(edit, new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        if(!contact_name.equals(""))
        {
            TextView lblTitle = new TextView(context);
            lblTitle.setText("Người liên hệ: ");
            pnView.addView(lblTitle);
            TextView edit = new TextView(context);
            edit.setText(contact_name);
            edit.setTextColor(Color.GRAY);
            pnView.addView(edit, new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        if(!mobile.equals(""))
        {
            TextView lblTitle = new TextView(context);
            lblTitle.setText("Di động: ");
            pnView.addView(lblTitle);
            TextView edit = new TextView(context);
            edit.setText(mobile);
            edit.setTextColor(Color.GRAY);
            pnView.addView(edit, new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        if(!description.equals(""))
        {
            TextView lblTitle = new TextView(context);
            lblTitle.setText("Mô tả: ");
            pnView.addView(lblTitle);
            TextView edit = new TextView(context);
            edit.setText(description);
            edit.setTextColor(Color.GRAY);
            pnView.addView(edit, new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }


        return pnView  ;

    }
    public View createView(int position, DataTable dt){
        return null;
    }
}
