package vinacert.vn.psample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import vinacert.vn.psample.data.DataTable;
import vinacert.vn.psample.printer.PrintHeight;
import vinacert.vn.psample.printer.PrintItem;
import vinacert.vn.psample.printer.PrintItemBarcode;
import vinacert.vn.psample.printer.PrintItemImage;
import vinacert.vn.psample.printer.PrintItemLine;
import vinacert.vn.psample.printer.PrintItemQrcode;
import vinacert.vn.psample.printer.PrintItemText;

public class PickupActivity extends AppCompatActivity implements ResponseData, ISignature {

    private Button btnScan;
    private LinearLayout pnView;
    private Button btnDone;
    private String sample_id = "";
    private String task_id = "";
    private String pickup_id= "";
    private String partner_name = "";
    private String phone = "";
    private String address = "";
    private String contact_name = "";
    private String mobile = "";
    private String product_name = "";
    private String product_code = "";
    private String start_date = "";
    private String quantity = "";
    private String weigth = "";
    private String lot = "";
    private String signuature_customer ="";
    private String emp_signature = "";

    private ImageView imgView;
    //private Bitmap empSignature;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);
        btnDone = findViewById(R.id.btnDone);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();

            }
        });


        pnView = (LinearLayout) findViewById(R.id.pickup_view);

        pickup_id = Tool.getInstance().getId();
        sample_id = MainApp.getInstance().TASK_ROW.getString("project_id");
        task_id = MainApp.getInstance().TASK_ROW.getString("id");
        partner_name = MainApp.getInstance().TASK_ROW.getString("partner_name");
        phone = MainApp.getInstance().TASK_ROW.getString("phone");
        address = MainApp.getInstance().TASK_ROW.getString("address");
        contact_name = MainApp.getInstance().TASK_ROW.getString("contact_name");
        mobile = MainApp.getInstance().TASK_ROW.getString("mobile");
        product_name = MainApp.getInstance().TASK_ROW.getString("product_name");
        product_code = MainApp.getInstance().TASK_ROW.getString("product_code");
        quantity = MainApp.getInstance().TASK_ROW.getString("quantity");
        weigth = MainApp.getInstance().TASK_ROW.getString("weigth");
        start_date = MainApp.getInstance().TASK_ROW.getString("start_date");
//        lot = MainApp.getInstance().TASK_ROW.getString("lot");


        LinearLayout pnTask = new LinearLayout(this);

        btnScan = new Button(this );
        btnScan.setText("In phiếu nhận");
        btnScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                findReceiptNo();

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTask();
            }
        });

        pnTask.addView(btnScan);
        pnView.addView(pnTask);
        imgView = new ImageView(this);
        //imgView.getLayoutParams().height = 100; // OR
        //imgView.getLayoutParams().width = 100;
        imgView.setBackgroundColor(Color.RED);
        pnView.addView(imgView);
        DocumentView docs= new DocumentView(this, pickup_id , true, true);
        pnView.addView(docs);

    }
    private String error = "";
    private String receipt_no;
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
        }else if(code == 1)
        {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    updateTask();
                }
            });
        }else if(code == 2)
        {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    back();
                }
            });
        }else if(code == 6)
        {
            if(data != "")
            {
                receipt_no = data;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        signature();
                    }
                });
            }
        }
    }
    private void updateTask()
    {
        String sql = "UPDATE project_task SET percentage =1 WHERE id='" + task_id + "'";
        new Sender().exec(this, 2, -1, sql);
    }
    private void back()
    {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }
    private void showError()
    {
        MainApp.getInstance().alert(this, "Lỗi: " + error);
    }

    private DataTable dt_task;
    private int lastCount = -1;
    public void responseDataTable(int code, DataTable dt)
    {

        if(code == 1)
        {
           if(dt.getRowCount()>0)
           {
               runOnUiThread(new Runnable() {

                   @Override
                   public void run() {
                       showExistCode();
                   }
               });
           }
        }
    }
    private void showExistCode()
    {
        new AlertDialog.Builder(this)
                .setMessage("Mã đang đã tồn tại")
                .setCancelable(false)
                .setNegativeButton("Đóng", null)
                .show();
    }
    private void signature()
    {
            MainApp.getInstance().SIGNATURE_FILE_NAME = "signuature_customer";
            MainApp.getInstance().iSignature  = PickupActivity.this;
            Intent intent = new Intent(getBaseContext(), SignatureActivity.class);
            startActivity(intent);
    }

    public void signatureDone(String file_name)
    {
        signuature_customer = file_name;
        printBill();
    }

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    byte FONT_TYPE;

    private ArrayList<PrintItem[]> commands;
    private boolean printed = false;
    private void printBill()
    {
        commands = new ArrayList<>();
        try
        {
            String name = " ";
//            if(name == "signuature_customer"){
//                File f = new File(MainApp.getInstance().getConfig().getProperty(name));
//                if(f.exists()){
//                    signuature_customer = f.getAbsolutePath();
//                }
//            }

            File f = new File(MainApp.getInstance().getConfig().getProperty("emp_signature"));
            if(f.exists()){
                emp_signature = f.getAbsolutePath();
            }

            String s = "<ticket>";
            s += "<line><text align=\"center\" font-size=\"26\">PHIẾU MẪU</text></line>";
            s += "<line><text align=\"left\" ></text></line>";
            s += "</ticket>";
            s += "<ticket>";
            s += "<line><text align=\"left\" font-size=\"18\">Mã mẫu: " + product_code + "</text></line>";
            s += "<line><text align=\"left\" font-size=\"18\">Tên mẫu: " + product_name + "</text></line>";
            s += "<line><height>15</height></line>";
            s += "</ticket>";
            s += "<ticket>";
            s += "<line><text align=\"left\" font-size=\"18\">Số hiệu lô hàng (Lot No): ........... </text></line>";
            s += "<line><text align=\"left\" font-size=\"18\">Ngày sản xuất: ................... </text></line>";
            s += "<line><text align=\"left\" font-size=\"18\">Tên DN nhập khẩu: " +  partner_name + "</text></line>";
//            s += "<line><height>10</height></line>";
//            s += "</ticket>";
//            s += "<ticket>";
//            s += "<line>";
            s += "<line><text align=\"left\" font-size=\"18\">Địa điểm lấy mẫu: ........</text></line>";
            s += "<line><text align=\"left\" font-size=\"18\">Ngày lấy mẫu: " + start_date + "</text></line>";
            s += "<line><height>20</height></line>";
            s += "</ticket>";
            s += "<ticket>";
            s += "<line>";
            s += "<text align=\"left\" font-size=\"18\" width=\"50%\">Người đại diện: </text>";
            s += "<imgage  width=\"50%\" w=\"64\" h=\"60\">" + signuature_customer + "</imgage>";
            s += "</line>";
            s += "</ticket>";
            s += "<ticket>";

            s += "<line>";
            s += "<text align=\"left\" font-size=\"18\" width=\"50%\">Nhân viên lấy mẫu: </text>";
            s += "<imgage  width=\"50%\" w=\"64\" h=\"60\">" + emp_signature + "</imgage>";
            s += "<line><height>20</height></line>";
            s += "</line>";
            s += "</ticket>";
            s += "<ticket>";
            s += "<line><text align=\"left\" font-size=\"18\"></text></line>";
            s += "<line><text align=\"left\" width=\"30%\" font-size=\"20\">Mã hóa:</text>";
            s += "<barcode align=\"center\" width=\"70%\" w=\"100\" h=\"50\" scale=\"1\">" + receipt_no + "</barcode>";
            s += "</line>";
            s += "</ticket>";
            LineParser parser = new LineParser();

            Line c = parser.parse(s);
            if(c != null) {
                ArrayList<Line> tickets = c.listNames("ticket");
                for (Line ticket : tickets) {
                    commands = new ArrayList<>();
                    travelLine(ticket);
                    int w = 380;
                    int h = 0;
                    for(int i=0;  i<commands.size(); i++)
                    {
                        PrintItem[] items = commands.get(i);
                        for (PrintItem item : items) {
                            String width = item.getLine().getAttribute("width");
                            int a =w;
                            if (!"".equals(width)) {
                                int index = width.indexOf('%');
                                if (index != -1) {
                                    width = width.substring(0, index);
                                    a = (Integer.parseInt(width) * w) / 100;
                                } else {
                                    a = Integer.parseInt(width);
                                }
                            }
                            item.setWidth(a);
                        }
                        int a =0;
                        for(int j=0; j<commands.get(i).length; j++)
                        {
                            int b = commands.get(i)[j].getHeight();
                            if(b>a)
                            {
                                a = b;
                            }
                        }
                        h += a;
                    }
                    Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                    Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap

                    Canvas canvas = new Canvas(bmp);
                    canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                    canvas.drawColor(0xffffffff);

                    int px =0;
                    for(int i=0;  i<commands.size(); i++)
                    {

                        PrintItem[] items = commands.get(i);


                        int x =0;
                        int y = px;
                        int heightItem =0;
                        for (PrintItem item : items)
                        {

                            int padding_top = 0;
                            int padding_left = 0;

                            if (!"".equals(item.getLine().getAttribute("x"))) {
                                x = Integer.parseInt(item.getLine().getAttribute("x"));

                            }
                            if (!"".equals(item.getLine().getAttribute("y"))) {
                                y = Integer.parseInt(item.getLine().getAttribute("y"));

                            }

                            if (!"".equals(item.getLine().getAttribute("padding-top"))) {
                                padding_top = Integer.parseInt(item.getLine().getAttribute("padding-top"));

                            }
                            if (!"".equals(item.getLine().getAttribute("padding-left"))) {
                                padding_left = Integer.parseInt(item.getLine().getAttribute("padding-left"));

                            }
                            heightItem = item.draw(canvas, x + padding_left, y + padding_top, item.getWidth());
                            x = x + item.getWidth();
                        }
                        px = heightItem;

                    }
                    imgView.setImageBitmap(bmp);
                    if(printPhoto(bmp))
                    {
                        printed = true;

                    }
                }
                if(printed)
                {
                    printNewLine();
                    printNewLine();
                    printNewLine();
                    resetPrint();
                }

            }

            btnDone.setVisibility(View.VISIBLE);
        }catch (Exception ex)
        {
            MainApp.getInstance().alert(this, ex.toString());
        }
    }

    private void travelLine(Line l)
    {
        for(int k =0; k<l.size(); k++)
        {
            Line c = l.get(k);

            if("line".equals(c.getName()))
            {


                PrintItem[] items = new PrintItem[c.size()];

                for(int i=0; i<c.size(); i++)
                {
                    if("hr".equals(c.get(i).getName()))
                    {
                        items[i] = new PrintItemLine(c.get(i));
                    }else if("barcode".equals(c.get(i).getName()))
                    {
                        items[i] = new PrintItemBarcode(c.get(i));
                    }else if("qrcode".equals(c.get(i).getName()))
                    {
                        items[i] = new PrintItemQrcode(c.get(i));
                    }
                    else if("imgage".equals(c.get(i).getName()))
                    {
                        items[i] = new PrintItemImage(c.get(i));
                    }else if("height".equals(c.get(i).getName()))
                    {
                        items[i] = new PrintHeight(c.get(i));
                    }
                    else
                    {
                        items[i] = new PrintItemText(c.get(i));
                    }

                }
                commands.add(items);

            }
            else
            {
                travelLine(c);
            }
        }
    }

    private void findReceiptNo()
    {
        //MainApp.getInstance().mBarcode  = this;
        //Intent intent = new Intent(getBaseContext(), ScanBarcodeActivity.class);
        //startActivity(intent);
        String url = MainApp.getInstance().getConfig().getProperty("url");
        String param = "ac=findSampleCode&sample_id=" + sample_id;
        param += "&user_id=" + MainApp.getInstance().getConfig().getProperty("user_id");
        param += "&company_id=" + MainApp.getInstance().getConfig().getProperty("company_id");
        param += "&token=" + MainApp.getInstance().getConfig().getProperty("token");
        new Sender().loadContent(this,6, url + "service/workorder", param);

    }

    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
                MainApp.getInstance().alert(this, "No bluetooth adapter available");
            }

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    String name = device.getName();
                    if (device.getName().equals("BlueTooth Printer")) {
                        mmDevice = device;
                        break;
                    }
                }
            }

            MainApp.getInstance().alert(this, "Bluetooth device found.");

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            //beginListenForData();

            MainApp.getInstance().alert(this, "Bluetooth Opened");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //print custom
    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
        try {
            switch (size){
                case 0:
                    mmOutputStream.write(cc);
                    break;
                case 1:
                    mmOutputStream.write(bb);
                    break;
                case 2:
                    mmOutputStream.write(bb2);
                    break;
                case 3:
                    mmOutputStream.write(bb3);
                    break;
            }

            switch (align){
                case 0:
                    //left align
                    mmOutputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    mmOutputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    mmOutputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            mmOutputStream.write(msg.getBytes());
            mmOutputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print photo
    public boolean printPhoto(Bitmap bmp) {
        try {

            if(mmOutputStream == null)
            {
                findBT();
                openBT();
            }
            if(mmOutputStream ==null)
            {
                MainApp.getInstance().alert(this,"Không kết nối đến máy in");
            }
            if(bmp!=null){
                byte[] command = Utils.decodeBitmap(bmp);
                mmOutputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                return printText(command);

            }else{
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
        return  false;
    }

    //print unicode
    public void printUnicode(){
        try {
            mmOutputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
            printText(Utils.UNICODE_TEXT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //print new line
    private void printNewLine() {
        try {
            mmOutputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public  void resetPrint() {
        try{
            mmOutputStream.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT);
            mmOutputStream.write(PrinterCommands.FS_FONT_ALIGN);
            mmOutputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
            mmOutputStream.write(PrinterCommands.ESC_CANCEL_BOLD);
            mmOutputStream.write(PrinterCommands.LF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //print byte[]
    private boolean printText(byte[] msg) {
        try {
            // Print normal text
            mmOutputStream.write(msg);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  false;
    }


    private String leftRightAlign(String str1, String str2) {
        String ans = str1 +str2;
        if(ans.length() <31){
            int n = (31 - str1.length() + str2.length());
            ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
        }
        return ans;
    }


    private String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String dateTime [] = new String[2];
        dateTime[0] = c.get(Calendar.DAY_OF_MONTH) +"/"+ c.get(Calendar.MONTH) +"/"+ c.get(Calendar.YEAR);
        dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        return dateTime;
    }

    protected void onDestroy() {
        super.onDestroy();
        try {
            if(mmSocket!= null){
                mmOutputStream.close();
                mmSocket.close();
                mmSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
