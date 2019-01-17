package vinacert.vn.psample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Scanner;

public class Tool {

    private static Tool instance;
    public static Tool getInstance()
    {
        if(instance == null)
        {
            instance = new Tool();
        }
        return instance;
    }

    public synchronized String getId()
    {
        return  java.util.UUID.randomUUID().toString();
    }
    public synchronized short toShort(String s)
    {
        return Short.parseShort(s);
    }
    public synchronized int toInt(String s)
    {
        return Integer.parseInt(s);
    }
    public synchronized double toDouble(String s)
    {
        if(s.equals(""))
        {
            return 0;
        }
        return Double.parseDouble(s);
    }
    public String toString(short s)
    {
        return "" + s;
    }
    public String toString(int s)
    {
        return "" + s;
    }
    public String toString(long s)
    {
        return "" + s;
    }
    public String toString(float s)
    {
        return "" + s;
    }
    public String toString(double s)
    {
        return "" + s;
    }
    public String toString(String s)
    {
        return s;
    }
    public synchronized String base64_encode(String encoded_string)
    {
        return encoded_string;
    }

    public synchronized String passwordEncode(String user_id, String password)
    {
        String s = sha256("[" + user_id + "]" + password);
        for( int i=0; i<password.length(); i++)
        {
            char ch = (char) (i + 48);
            s = s + ch;
        }
        s = md5(s);
        return s;
    }
    public synchronized boolean existFolder(String path)
    {
        File f = new File(path);
        return f.exists() && f.isDirectory();
    }
    public synchronized void createFolder(String path)
    {
        new File(path).mkdirs();
    }
    public synchronized boolean existFile(String path)
    {
        File f = new File(path);
        return f.exists() && f.isFile();
    }
    public synchronized String sha1(String s) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.reset();
        byte[] buffer = s.getBytes();
        md.update(buffer);
        byte[] digest = md.digest();

        String hexStr = "";
        for (int i = 0; i < digest.length; i++) {
            hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return hexStr;
    }
    private synchronized String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
    public synchronized String sha256(String s)
    {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(s.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    public synchronized String md5(String md5) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e)
        {

        }
        return "";
    }
    public synchronized String url_encode(String s)
    {
        return URLEncoder.encode(s);
    }
    public synchronized String url_decode(String s)
    {
        return URLEncoder.encode(s);
    }
    public synchronized String download(String _url, String param) throws Exception
    {
        URL url = null;
        HttpURLConnection conn = null;
        StringBuilder buf = new StringBuilder();
        try
        {

            byte[] postData = param.getBytes();
            url = new URL(_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
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

                buf.append(inStream.nextLine());
            }
            inStream.close();
        }catch(Exception ex)
        {
            throw ex;
        }finally
        {
            if(conn != null)
            {
                try{
                    conn.disconnect();
                    conn = null;
                }catch(Exception ex){
                    System.out.println(ex.toString());
                }
            }
        }
        return buf.toString();
    }
    public synchronized String lastModifyFile(String file_name)
    {
        File file = new File(file_name);
        if(file.exists() && file.isFile())
        {
            return "" + file.lastModified();
        }
        return "";
    }
    public synchronized String replace(String src , String s1 , String s2) {
        String dst = src;
        try {

            int indx = -1;
            while ((indx=dst.indexOf(s1))!= -1) {
                dst = dst.substring(0 , indx) + s2 + dst.substring(indx + s1.length());
            }
        }
        catch ( Exception ex ) {}
        return dst;
    }
    public synchronized String padLeft(String s, int len, char c)
    {
        while(s.length()<len)
        {
            s = c + s;
        }
        return s;
    }
    public synchronized String[] split(String s, char c)
    {
        return s.split("" + c, -1);
    }
    public synchronized String urlEncode(String url) throws UnsupportedEncodingException
    {
        return URLEncoder.encode(url, "UTF-8");
    }
    public synchronized String lowerCase(String s)
    {
        return s.toLowerCase();
    }
    public synchronized Bitmap loadImage(String id, int width, int height)
    {
        try
        {
            URL url = new URL(MainApp.getInstance().getConfig().getProperty("url") + "document?id=" + id + "&width=" + width + "&height=" + height);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bmp;
        }catch (Exception ex)
        {
            System.out.println(ex.toString());
        }
        return null;

    }
    public synchronized boolean isNum(String s)
    {
        if(s.trim().equals(""))
        {
            return  false;
        }
        return  true;
    }
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public synchronized Date toDateTime(String value)
    {

        int index = value.indexOf(" ");
        String t = "";
        if(index != -1)
        {
            t = value.substring(index + 1);
            value = value.substring(0, index);
        }
        int year = 1900;
        index = value.indexOf('-');
        if(index != -1)
        {
            year = toInt(value.substring(0, index));
            if(year>2030)
            {
                year = year - 1900;
            }

            value = value.substring(index + 1);
        }
        int month = 1;
        index = value.indexOf('-');
        if(index != -1)
        {
            month = toInt(value.substring(0, index));
            value = value.substring(index + 1);
        }
        int day = 1;
        if(value != "")
        {
            day = toInt(value);
        }

        int h = 0;
        index = t.indexOf(':');
        if(index != -1)
        {
            h = toInt(t.substring(0, index));
            t = t.substring(index + 1);
        }
        int m = 0;
        index = t.indexOf(':');
        if(index != -1)
        {
            m = toInt(t.substring(0, index));
            t = t.substring(index + 1);
        }
        int s = 0;
        if(t != "")
        {
            index = t.indexOf('.');
            if(index != -1)
            {
                t = t.substring(0, index);
            }
            s= toInt(t);
        }
        try
        {
            return format.parse(year + "-" + month + "-" + day + " " + h + ":" + m + ":" + s);
        }catch (Exception ex){}

        return null;
    }

    public synchronized String formatDate(String value, String pattern)
    {

        int index = value.indexOf(" ");
        String t = "";
        if(index != -1)
        {
            t = value.substring(index + 1);
            value = value.substring(0, index);
        }
        int year = 1900;
        index = value.indexOf('-');
        if(index != -1)
        {
            year = toInt(value.substring(0, index));
            if(year>2030)
            {
                year = year - 1900;
            }
            value = value.substring(index + 1);
        }
        int month = 1;
        index = value.indexOf('-');
        if(index != -1)
        {
            month = toInt(value.substring(0, index));
            value = value.substring(index + 1);
        }
        int day = 1;
        if(value != "")
        {
            day = toInt(value);
        }

        int h = 0;
        index = t.indexOf(':');
        if(index != -1)
        {
            h = toInt(t.substring(0, index));
            t = t.substring(index + 1);
        }
        int m = 0;
        index = t.indexOf(':');
        if(index != -1)
        {
            m = toInt(t.substring(0, index));
            t = t.substring(index + 1);
        }
        int s = 0;
        if(t != "")
        {
            index = t.indexOf('.');
            if(index != -1)
            {
                t = t.substring(0, index);
            }
            s= toInt(t);
        }
        pattern = pattern.replace("yyyy", "" + year);
        pattern = pattern.replace("MM", "" + month);
        pattern = pattern.replace("dd", "" + day);
        pattern = pattern.replace("HH", "" + h);
        pattern = pattern.replace("mm", "" + m);
        pattern = pattern.replace("ss", "" + s);
        return pattern;
    }
    public  String formatDouble(double d)
    {
        NumberFormat numberFormatter = new DecimalFormat("#,###.##");
        return numberFormatter.format(d);
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

}
