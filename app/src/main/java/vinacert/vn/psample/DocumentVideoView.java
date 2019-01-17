package vinacert.vn.psample;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;

public class DocumentVideoView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_video_view);
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


        videoView = (VideoView) findViewById(R.id.videoView);
        if (mediaController == null) {
            mediaController = new MediaController(this);

            // Set the videoView that acts as the anchor for the MediaController.
            mediaController.setAnchorView(videoView);


            // Set MediaController for VideoView
            videoView.setMediaController(mediaController);
        }
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {


                videoView.seekTo(position);
                if (position == 0) {
                    videoView.start();
                }

                // When video Screen change size.
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                        // Re-Set the videoView that acts as the anchor for the MediaController
                        mediaController.setAnchorView(videoView);
                    }
                });
            }
        });
        loadPdf();
    }
    private VideoView videoView;
    private int position = 0;
    private MediaController mediaController;
    private File file;
    private  void loadPdf()
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

                    OutputStream out = new FileOutputStream(file);
                    byte[] buf = new byte[100240];
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
    private  void viewFile() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {


                    videoView.requestFocus();
                    videoView.setVideoPath(MainApp.getInstance().getDirectory("") + MainApp.getInstance().DOC_FILE_ID + "." + MainApp.getInstance().DOC_FILE_EXTENSION);


                } catch (Exception ex) {

                }
            }
        });
    }

}
