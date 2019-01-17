package vinacert.vn.psample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;

import vinacert.vn.psample.data.DataTable;

public class DocumentView extends LinearLayout implements ResponseData{

    private  Activity context;
    private String for_id;
    private  boolean canEdit;
    private boolean canUpload;
    public  DocumentView(Activity context, String for_id, boolean canEdit, boolean canUpload)
    {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        this.canEdit = canEdit;
        this.canUpload = canUpload;

        this.context = context;
        this.for_id = for_id;
        MainApp.getInstance().DOC_FOR_ID = for_id;



        pnView = new LinearLayout(context);
        pnView.setOrientation(LinearLayout.VERTICAL);
        this.addView(pnView);


        if(canEdit || canUpload)
        {


            ImageView btnCamara = new ImageView(context);
            btnCamara.setImageResource(R.drawable.attach_file);
            btnCamara.setPadding(26,26, 26,26);
            btnCamara.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCamara();
                }
            });

            this.addView(btnCamara);
        }

    }

    private void showCamara()
    {
        MainApp.getInstance().mDocumentView= this;
        Intent intent = new Intent(context.getBaseContext(), UploadActivity.class);
        context.startActivity(intent);
    }
    private LinearLayout pnView;
    public  void loadData()
    {
        String sql = "SELECT d1.id, d1.document_name, d1.file_type, d1.extension";
        sql += ", d2.user_name, d1.create_date, d1.content_length";
        sql += " FROM document d1 LEFT OUTER JOIN res_user d2 ON(d1.create_uid = d2.id)";
        sql += " WHERE d1.status =0 AND d1.for_id ='" + for_id + "'";
        new Sender().loadTable(this, 1,-1, sql );
    }
    public void responseText(int code, String data)
    {

    }
    private DataTable dt;
    public void responseDataTable(int code, DataTable dt)
    {
        this.dt = dt;
        if(code == 1)
        {
            context.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    viewDocument();
                }
            });
        }
    }
    String ext_img= ",jpg,jpeg,png,gif,bmp,tiff,svg,";
    String ext_file = "doc,docx,rtf,pdf,xls,xlsx,txt,csv,html,xhtml,psd,sql,log,fla,xml,ade,adp,mdb,accdb,ppt,pptx,odt,ots,ott,odb,odg,otp,otg,odf,ods,odp,css,ai,";
    String ext_video = ",mov,mpeg,mp4,avi,mpg,wma,flv,webm,";
    String ext_music = ",mp3,m4a,ac3,aiff,mid,ogg,wav,";
    String ext_misc = ",zip,rar,gz,tar,iso,dmg,";
    String icon_file =",ac3,accdb,ade,adp,ai,aiff,avi,bmp,css,csv,dmg,doc,docx,fla,flv,gif,gz,html,ico,iso,jpeg,jpg,log,m4a,mdb,mid,mov,mp3,mp4,mpeg,mpg,odb,odf,odg,odp,odt,ogg,otg,ots,ott,pdf,png,ppt,pptx,psd,rar,rtf,sql,svg,tar,tiff,txt,wav,webm,wma,xhtml,xls,xlsx,xml,zip,";

    private  void viewDocument()
    {

        pnView.removeAllViews();
        for(int i=0; i<dt.getRowCount(); i++)
        {
            String file_id = dt.getString(i, 0);
            String file_name = dt.getString(i, 1);
            String extension = dt.getString(i, 3);

            boolean is_img = false;
            boolean is_video = false;
            boolean is_audio = false;

            extension = extension.toLowerCase();
            int index = ext_img.indexOf("," + extension + ",");
            if(index != -1)
            {
                is_img = true;
            }
            index = ext_video.indexOf("," + extension + ",");
            if(index != -1)
            {
                is_video = true;
            }
            index = ext_music.indexOf("," + extension + ",");
            if(index != -1)
            {
                is_audio = true;
            }
            String url = MainApp.getInstance().getConfig().getProperty("url");
            String src = url + "layout?id=img/ico/default.jpg";
            if(is_img)
            {
                src= url + "document?id=" + file_id + "&width=122&height=91";
            }else
            {
                if(icon_file.indexOf("," + extension + ",") != -1)
                {
                    src= url + "layout?id=img/ico/" + extension + ".jpg" ;
                }
            }
            LinearLayout pnLine = new LinearLayout(context);
            pnLine.setClickable(true);

            pnLine.setPadding(0, 2, 0, 2);

            ImageView img = new ImageView(context);
            new DownloadImageTask(img)
                    .execute(src);
            pnLine.addView(img);

            TextView lblTitle = new TextView(context);
            lblTitle.setText("   " + file_name);
            pnLine.addView(lblTitle);
            pnLine.setTag(file_id + ";" + file_name + "." + extension);
            pnLine.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = v.getTag().toString();
                    int index = tag.indexOf(";");
                    MainApp.getInstance().DOC_FILE_ID = tag.substring(0, index);
                    tag = tag.substring(index + 1);
                    index = tag.lastIndexOf('.');
                    if(index != -1)
                    {
                        MainApp.getInstance().DOC_FILE_EXTENSION = tag.substring(index + 1)
                                ;
                        tag =tag.substring(0, index);
                    }
                    MainApp.getInstance().DOC_FILE_NAME = tag;
                    showDoc();

                }
            });
            pnView.addView(pnLine);

        }
    }
    private  void showDoc()
    {
        boolean is_img = false;
        boolean is_video = false;
        boolean is_audio = false;
        boolean is_pdf = false;

        int index = ext_img.indexOf("," + MainApp.getInstance().DOC_FILE_EXTENSION + ",");
        if(index != -1)
        {
            is_img = true;
        }
        index = ext_video.indexOf("," + MainApp.getInstance().DOC_FILE_EXTENSION + ",");
        if(index != -1)
        {
            is_video = true;
        }
        index = ext_music.indexOf("," + MainApp.getInstance().DOC_FILE_EXTENSION + ",");
        if(index != -1)
        {
            is_audio = true;
        }
        is_pdf = ".pdf".indexOf( MainApp.getInstance().DOC_FILE_EXTENSION) != -1;

        if(is_img)
        {
            Intent intent = new Intent(context.getBaseContext(), DocumentImageActivity.class);
            context.startActivity(intent);
        }
        else if(is_pdf)
        {
            Intent intent = new Intent(context.getBaseContext(), DocumentPdfActivity.class);
            context.startActivity(intent);
        } else if(is_video || is_audio)
        {
            Intent intent = new Intent(context.getBaseContext(), DocumentVideoView.class);
            context.startActivity(intent);
        }else
        {
            doDownload();
        }

    }
    private File file;
    private  void doDownload()
    {
        file = new File(MainApp.getInstance().getDirectory("") + MainApp.getInstance().DOC_FILE_ID + "." + MainApp.getInstance().DOC_FILE_EXTENSION);
        if(file.exists())
        {
            viewFile();
            return;
        }
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try
                {

                    String _url = MainApp.getInstance().getConfig().getProperty("url") + "document?id=" + MainApp.getInstance().DOC_FILE_ID + "." + MainApp.getInstance().DOC_FILE_EXTENSION;

                    InputStream in = new java.net.URL(_url).openStream();
                    file = new File(MainApp.getInstance().getDirectory("") + MainApp.getInstance().DOC_FILE_ID + "." + MainApp.getInstance().DOC_FILE_EXTENSION);
                    OutputStream out = new FileOutputStream(file);
                    byte[] buf = new byte[10240];
                    int len;
                    while((len=in.read(buf))>0){
                        out.write(buf,0,len);
                    }
                    out.close();
                    viewFile();

                }catch (Exception ex)
                {
                    System.out.println(ex.toString());
                }
            }
        });
    }
    private  void viewFile()
    {
        context.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    Uri path = Uri.fromFile(file);
                    Intent fileIntent = new Intent(Intent.ACTION_VIEW);
                    fileIntent.setDataAndType(path, "application/" + MainApp.getInstance().DOC_FILE_EXTENSION);
                    context.startActivity(fileIntent);
                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            }
        });
    }

}
