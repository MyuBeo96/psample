package vinacert.vn.psample;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import vinacert.vn.psample.data.DataTable;

public class MainActivity extends AppCompatActivity implements ResponseData {

    private ListView listView;
    private boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(MainApp.getInstance().getConfig().getProperty("url").equals("")
                || MainApp.getInstance().getConfig().getProperty("company_id").equals("")
                || MainApp.getInstance().getConfig().getProperty("token").equals("")

                )
        {
            Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
            startActivity(intent);
            return;
        }else if( MainApp.getInstance().getConfig().getProperty("user_id").equals(""))
        {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            return;
        }
        listView = (ListView)findViewById(R.id.listView);


        android.view.Display display = getWindowManager().getDefaultDisplay();
        int w = display.getWidth();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View itemView, int itemPosition, long itemId)
            {
                selectTask(itemPosition);
            }
        });
        running = true;
        loadTasks();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            new AlertDialog.Builder(this)
                    .setMessage("Bạn muốn thiếng lập lại?")
                    .setCancelable(false)
                    .setPositiveButton("Thiết lập", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setting();
                        }
                    })
                    .setNegativeButton("Đóng", null)
                    .show();

            return true;
        }else  if (id == R.id.action_logout) {

            new AlertDialog.Builder(this)
                    .setMessage("Bạn muốn thoát?")
                    .setCancelable(false)
                    .setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            logout();
                        }
                    })
                    .setNegativeButton("Đóng", null)
                    .show();

            return true;
        }else if(id == R.id.action_getdata)
        {
            loadTasks();
        }else if(id == R.id.action_signature)
        {
            MainApp.getInstance().SIGNATURE_FILE_NAME = "emp_signature";
            MainApp.getInstance().iSignature  = null;
            Intent intent = new Intent(getBaseContext(), SignatureActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    private void setting()
    {
        running = false;
        Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
        startActivity(intent);
    }
    private void logout()
    {
        running = false;
        MainApp.getInstance().getConfig().setProperty("user_id", "");
        MainApp.getInstance().getConfig().setProperty("user_name", "");
        MainApp.getInstance().getConfig().save();
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(intent);
    }

    private String error = "";
    public void responseText(int code, String data)
    {
        if(code == -1)
        {
            error = data;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showError();
                }
            });

            return;
        }
    }
    private void showError()
    {
        MainApp.getInstance().alert(this, "Lỗi: " + error);
    }

    private DataTable dt_task;
    private int lastCount = -1;
    public void responseDataTable(int code, DataTable dt)
    {

        if(code == -1)
        {
            notification();

        }
        else if(code == 1)
        {
            dt_task = dt;
            lastCount = dt.getRowCount();
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    listTask();
                }
            });

        }else if(code == 2)
        {
            if(dt.getRowCount()>0 && !"".equals(dt.getString(0, 0)))
            {
                int c = Integer.parseInt(dt.getString(0, 0));
                if(c != lastCount)
                {
                    lastCount = c;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            loadTasks();
                        }
                    });

                }
            }
        }
    }
    private  DataTable dt_notification;
    private void listTask()
    {
        TaskAdapter adapter = new TaskAdapter(this, dt_task);
        listView.setAdapter(adapter);
    }


    private void loadTasks()
    {

        String sql = "SELECT d1.id, d1.project_id, d1.rel_id";
        sql += " , d5.partner_name, d5.phone, d5.address, d5.contact_name, d5.mobile";
        sql += ", d2.name AS product_name, d2.code AS product_code, d2.quantity, d2.weigth";
        sql += " , d1.description, d1.start_date, d1.end_date";
        sql += " FROM project_task d1 ";
        sql += " LEFT OUTER JOIN sale_request_sample d2 ON(d1.project_id = d2.id)";
        sql += " LEFT OUTER JOIN sale_request d3 ON(d2.request_id = d3.id)";
        sql += " LEFT OUTER JOIN customer d4 ON(d3.customer_id = d4.id)";
        sql += " LEFT OUTER JOIN res_partner d5 ON(d4.partner_id = d5.id)";
        sql += " LEFT OUTER JOIN res_user_company d6 ON(d1.employee_id = d6.employee_id)";
        sql += " WHERE d1.status =0 AND d6.status =0 AND d2.id IS NOT NULL AND d1.percentage<1";
        sql += " AND d6.user_id ='" + MainApp.getInstance().getConfig().getProperty("user_id") + "'";
        sql += " ORDER BY d1.start_date ASC";

        new Sender().loadTable(this,1, -1, sql);

    }
    private void notification()
    {

    }
    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("VICB - Lấy mẫu")
                        .setContentText("Có " + dt_notification.getRowCount() + " lấy mẫu mới");


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    private void checkTask() {

        while(running)
        {

            try {


                String sql = "SELECT COUNT(d1.id) AS c";
                sql += " FROM project_task d1 ";
                sql += "LEFT OUTER JOIN sale_request_sample d2 ON(d1.project_id = d2.id)";
                sql += " LEFT OUTER JOIN res_user_company d6 ON(d1.employee_id = d6.employee_id)";
                sql += " WHERE d1.status =0 AND d6.status =0 AND d2.id IS NOT NULL AND d1.percentage<1";
                sql += " AND d6.user_id ='" + MainApp.getInstance().getConfig().getProperty("user_id") + "'";
                new Sender().loadTable(this,2, -1, sql);
                Thread.sleep(600000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }catch(Exception e){
                System.out.println(e.toString());
            }
        }

    }
    private void selectTask(int position)
    {
        MainApp.getInstance().TASK_ROW = dt_task.getDataRow(position);
        Intent intent = new Intent(getBaseContext(), PickupActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;

    }

}
