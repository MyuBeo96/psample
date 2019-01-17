package vinacert.vn.psample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

public class DocumentImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_image);
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

        ImageView imgView = (ImageView) findViewById(R.id.imageView);
        String url = MainApp.getInstance().getConfig().getProperty("url") + "document?id=" + MainApp.getInstance().DOC_FILE_ID + "." + MainApp.getInstance().DOC_FILE_EXTENSION;
        new DownloadImageTask(imgView)
                .execute(url);
    }

}
