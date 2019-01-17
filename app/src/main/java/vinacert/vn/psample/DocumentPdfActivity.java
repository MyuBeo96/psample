package vinacert.vn.psample;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;


public class DocumentPdfActivity extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_pdf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle(MainApp.getInstance().DOC_FILE_NAME);


        pdfView = (ScrollView) findViewById(R.id.pdfView);
        pnView = new LinearLayout(this);
        pnView.setOrientation(LinearLayout.VERTICAL);
        pdfView.addView(pnView);
        loadPdf();

    }


    private ScrollView pdfView;
    private  LinearLayout pnView;
    private  File file;
    private  void loadPdf()
    {

        file = new File(MainApp.getInstance().getDirectory("") +  MainApp.getInstance().DOC_FILE_ID + ".pdf");
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
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try
                {


                    ParcelFileDescriptor fileDescriptor = null;
                    fileDescriptor = ParcelFileDescriptor.open(
                            file, ParcelFileDescriptor.MODE_READ_ONLY);

                    //min. API Level 21
                    PdfRenderer pdfRenderer = null;
                    pdfRenderer = new PdfRenderer(fileDescriptor);

                    final int pageCount = pdfRenderer.getPageCount();

                    //Display page 0

                    for(int i = 0; i<pageCount; i++)
                    {
                        PdfRenderer.Page rendererPage = pdfRenderer.openPage(i);
                        int rendererPageWidth = rendererPage.getWidth();
                        int rendererPageHeight = rendererPage.getHeight();
                        Bitmap bitmap = Bitmap.createBitmap(
                                rendererPageWidth,
                                rendererPageHeight,
                                Bitmap.Config.ARGB_8888);
                        rendererPage.render(bitmap, null, null,
                                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                        addImage(bitmap);
                        rendererPage.close();
                    }



                    pdfRenderer.close();
                    fileDescriptor.close();
                }catch (Exception ex)
                {
                    System.out.println(ex.toString());
                }

            }
        });

    }
    private  void addImage(Bitmap bitmap)
    {
        ImageView imgView = new ImageView(this );
        imgView.setImageBitmap(bitmap);
        pnView.addView(imgView);
    }


}
