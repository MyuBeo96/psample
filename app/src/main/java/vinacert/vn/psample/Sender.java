package vinacert.vn.psample;

import android.os.Bundle;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Scanner;
import java.util.concurrent.Executors;

import vinacert.vn.psample.data.DataColumn;
import vinacert.vn.psample.data.DataRow;
import vinacert.vn.psample.data.DataTable;


public class Sender {

    private static Sender instance = null;
    public static Sender GetInstance()
    {
        if(instance == null){
            instance = new Sender();
        }
        return instance;
    }

    private final String URL = "";

    private java.io.BufferedReader in;
    public String ROOT_HTTP = "http://118.70.124.172:8383/";

    public Sender()
    {
        String sIP = MainApp.getInstance().getConfig().getProperty("url");

        if(sIP.equals("")){
            sIP = ROOT_HTTP;
        }
        ROOT_HTTP = sIP;

    }

    private android.os.Message msg = null;
    private Bundle bundle = null;
    private ResponseData responseData;
    private int respCode;
    private int _errorCode;
    private String _sql;
    public synchronized void loadTable(ResponseData resp, int code, int errorCode, String sql)
    {
        this.responseData = resp;
        this.respCode = code;
        this._sql = sql;
        this._errorCode = errorCode;
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                DataTable dt = new DataTable("t");
                java.net.URL url = null;

                HttpURLConnection conn = null;

                try
                {

                    String _url = ROOT_HTTP + "service/getdata";
                    String param = "token=" + MainApp.getInstance().getConfig().getProperty("token")+ "&ac=gettable&q=" + Tool.getInstance().urlEncode(_sql);
                    byte[] postData = param.getBytes();
                    url = new java.net.URL(_url);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
                    conn.setUseCaches(false);

                    OutputStream out = conn.getOutputStream();
                    out.write(postData);
                    out.close();

                    Scanner inStream = new Scanner(conn.getInputStream());

                    while(inStream.hasNextLine())
                    {
                        String line = inStream.nextLine();
                        String[] items = line.split("\t");
                        if (dt.columns.isEmpty())
                        {
                            for (String c : items)
                            {
                                dt.addColumn(new DataColumn(c));
                            }
                        }else
                        {
                            DataRow nRow = dt.newRow();
                            for (int i = 0; i < dt.getColumnCount(); i++)
                            {
                                if(items.length>i){
                                    nRow.setValue(items[i], i);
                                }else
                                {
                                    nRow.setValue("", i);
                                }
                            }
                            dt.addRow(nRow);
                        }
                    }
                    inStream.close();

                    responseData.responseDataTable(respCode, dt);
                }catch(Exception ex){
                    responseData.responseText(_errorCode, "Table " + ex.toString());

                }finally
                {
                    try{
                        if(conn != null){
                            conn.disconnect();
                        }

                    }catch(Exception ex){
                        System.out.println(ex.toString());
                    }
                    conn = null;
                }
            }
        });


    }
    public synchronized void exec(ResponseData resp, int code, int errorCode, String sql)
    {

        this.responseData = resp;
        this.respCode = code;
        this._sql = sql;
        this._errorCode= errorCode;
        Executors.newSingleThreadExecutor().submit(new Runnable() {
           @Override
           public void run() {

               java.net.URL url = null;

               HttpURLConnection conn = null;

               try
               {

                   String _url = ROOT_HTTP + "service/getdata";
                   String param = "token=" + MainApp.getInstance().getConfig().getProperty("token")+ "&ac=exec&q=" + Tool.getInstance().urlEncode(_sql);
                   byte[] postData = param.getBytes();
                   url = new java.net.URL(_url);
                   conn = (HttpURLConnection) url.openConnection();
                   conn.setDoInput(true);
                   conn.setDoOutput(true);

                   conn.setRequestMethod("POST");
                   conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
                   conn.setUseCaches(false);

                   OutputStream out = conn.getOutputStream();
                   out.write(postData);
                   out.close();

                   Scanner inStream = new Scanner(conn.getInputStream());
                   String sreturn = "";
                   if(inStream.hasNextLine())
                   {
                       sreturn = inStream.nextLine();
                   }
                   inStream.close();
                   responseData.responseText(respCode, sreturn);

               }catch(Exception ex){
                   responseData.responseText(_errorCode, "Exec " + ex.toString());

               }finally
               {
                   try{
                       if(conn != null){
                           conn.disconnect();
                       }

                   }catch(Exception ex){
                       System.out.println(ex.toString());
                   }
                   conn = null;
               }
           }
        });

    }
    private String page;
    private String param;

    public synchronized void loadContent(ResponseData resp, int code, String pg, String pr)
    {
        this.page = pg;
        this.param = pr;
        this.respCode = code;
        this.responseData = resp;

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                StringBuilder buf = new StringBuilder();
                java.net.URL url = null;

                HttpURLConnection conn = null;

                try
                {


                    String _url = page;
                    byte[] postData = param.getBytes();
                    url = new java.net.URL(_url);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
                    conn.setUseCaches(false);

                    OutputStream out = conn.getOutputStream();
                    out.write(postData);
                    out.close();

                    Scanner inStream = new Scanner(conn.getInputStream());

                    while(inStream.hasNextLine())
                    {
                        String line = inStream.nextLine();
                        buf.append(line);
                    }
                    inStream.close();
                    responseData.responseText(respCode,  buf.toString());
                }catch(Exception ex){
                    //throw ex;
                    responseData.responseText(-1,  ex.toString());
                }finally
                {
                    try{
                        if(conn != null){
                            conn.disconnect();
                        }

                    }catch(Exception ex){
                        System.out.println(ex.toString());
                    }
                    conn = null;
                }

            }
        });
    }
    private byte[] bytes;
    public synchronized void writeDoc(ResponseData resp, int code, String pg, byte[] buf)
    {
        this.page = pg;
        this.respCode = code;
        this.responseData = resp;
        this.bytes = buf;

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                StringBuilder buf = new StringBuilder();
                java.net.URL url = null;

                HttpURLConnection conn = null;

                try
                {
                    String _url = page;
                    url = new java.net.URL(_url);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Length", Integer.toString(bytes.length));
                    conn.setUseCaches(false);

                    OutputStream out = conn.getOutputStream();
                    out.write(bytes);
                    out.close();

                    Scanner inStream = new Scanner(conn.getInputStream());

                    while(inStream.hasNextLine())
                    {
                        String line = inStream.nextLine();
                        buf.append(line);
                    }
                    inStream.close();
                    responseData.responseText(respCode,  buf.toString());
                }catch(Exception ex){
                    //throw ex;
                    responseData.responseText(-1,  ex.toString());
                }finally
                {
                    try{
                        if(conn != null){
                            conn.disconnect();
                        }

                    }catch(Exception ex){
                        System.out.println(ex.toString());
                    }
                    conn = null;
                }

            }
        });
    }




    private String RowToXML(DataRow r)
    {
        StringBuilder buf = new StringBuilder();
        buf.append("<d>");
        String table_name = r.getTable().getTableName();
        buf.append("<" + table_name + ">");
        for(int i=0; i<r.getTable().getColumnCount(); i++){
            String c_name = r.getTable().getColumnName(i);
            String c_value = r.getValue(i).toString();
            buf.append("<" + c_name + ">");
            buf.append(c_value);
            buf.append("</" + c_name + ">");
        }
        buf.append("</" + table_name + ">");
        buf.append("</d>");
        return buf.toString();
    }
}
