package vinacert.vn.psample;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import vinacert.vn.psample.data.DataRow;


public class MainApp {

    public DataRow TASK_ROW;

    public String DOC_FILE_ID;
    public String DOC_FILE_NAME;
    public String DOC_FILE_EXTENSION;
    public String DOC_FOR_ID = "";
    public DocumentView mDocumentView;
    public IBarcode mBarcode;

    public static String DB_PATH = "/data/data/vinacert.vn.psample/";

    private static MainApp instance;
    private AppConfig config;
    public String SIGNATURE_FILE_NAME;
    public ISignature iSignature;

    public static MainApp getInstance()
    {
        if (instance == null)
        {
            instance = new MainApp();
            instance.config = new AppConfig(new String[] { }, "");
            instance.config.load();
        }
        return instance;
    }
    public String getID()
    {
        return java.util.UUID.randomUUID().toString();
    }
    public AppConfig getConfig()
    {
        return config;
    }


    /**
     * @param mMainForm the mMainForm to set
     */


    public android.app.Activity ACTIVITY;

    public void getException(android.app.Activity mACTIVITY, Exception ex){
        android.widget.Toast.makeText(mACTIVITY , ex.toString(), android.widget.Toast.LENGTH_LONG).show();
    }
    private  String message;
    public void alert(android.app.Activity mACTIVITY, String _message){
        this.ACTIVITY = mACTIVITY;
        this.message = _message;
        mACTIVITY.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(message.equals(""))
                {
                    return;
                }
                android.widget.Toast.makeText(ACTIVITY, message, android.widget.Toast.LENGTH_SHORT).show();
            }
        });

    }



    public boolean alert(android.app.Activity mACTIVITY, String msg, String title){
        if(msg.equals(""))
        {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mACTIVITY);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //CustomTabActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        return true;
    }
    private ProgressDialog progDailog;
    public void showLoading(android.app.Activity mACTIVITY, String title, String msg)
    {
        progDailog = ProgressDialog.show(mACTIVITY,
                title, msg,
                true);
    }
    public void stopLoading()
    {
        if(progDailog != null){
            progDailog.dismiss();
        }
    }
    private String getCurrentFolder()
    {
        return DB_PATH;
    }
    public String getDirectory(String path)
    {
        if (path.equals(""))
        {

            return getCurrentFolder();
        }
        return getCurrentFolder() + path + "/";
    }

    public String getLanguage(String key){
        return key;
    }
    public boolean isOnline(android.app.Activity  mACTIVITY) {
        ConnectivityManager cm =
                (ConnectivityManager) mACTIVITY.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}