package vinacert.vn.psample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import vinacert.vn.psample.data.DataTable;

public class UploadActivity extends AppCompatActivity implements ResponseData {

    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();


    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int PICK_FILE = 300;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video

    private Button btnCapturePicture, btnRecordVideo, btnLibrary;
    private TextInputEditText txtDocumentName;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private VideoView vidPreview;
    private Button btnUpload;
    private String filePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        int permission_camera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int permission_read_storage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission_write_storage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission_camera != PackageManager.PERMISSION_GRANTED
                || permission_read_storage != PackageManager.PERMISSION_GRANTED
                || permission_write_storage != PackageManager.PERMISSION_GRANTED) {
            makeRequest();
        }

        btnCapturePicture = (Button) findViewById(R.id.btnCapturePicture);
        btnRecordVideo = (Button) findViewById(R.id.btnRecordVideo);
        btnLibrary = (Button) findViewById(R.id.btnLibrary);

        /**
         * Capture image button click event
         */
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });

        /**
         * Record video button click event
         */
        btnLibrary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showChooser();
            }
        });
        btnRecordVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // record video
                recordVideo();
            }
        });

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }

        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // uploading the file to server
               uploadToServer();
            }
        });
        txtDocumentName = (TextInputEditText) findViewById(R.id.document_name);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        vidPreview = (VideoView) findViewById(R.id.videoPreview);

        txtPercentage.setVisibility(View.INVISIBLE);
        btnUpload.setVisibility(View.INVISIBLE);
        txtDocumentName.setVisibility(View.INVISIBLE);
        imgPreview.setVisibility(View.INVISIBLE);
        vidPreview.setVisibility(View.INVISIBLE);

    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    public void responseText(int code, String data)
    {
        if(code == -1)
        {

        }else if(code == 1)
        {
            if(data.length() == 36)
            {

                file_id = data;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        postFile();
                    }
                });

                return;
            }
        }else if(code == 2)
        {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    postFile();
                }
            });

        }else if(code == 3)
        {
            runOnUiThread(new Runnable() {

                              @Override
                              public void run() {
                                  MainApp.getInstance().mDocumentView.loadData();
                                  finish();
                              }
                          });

        }
    }
    public void responseDataTable(int code, DataTable dt)
    {

    }
    private BufferedInputStream buf =null;
    private void uploadToServer() {
        try
        {
            File sourceFile = new File(filePath);
            file_name = txtDocumentName.getText().toString();
            if(sourceFile.exists())
            {
                sizes = sourceFile.length();
                totoalRead = 0;
                txtPercentage.setVisibility(View.VISIBLE);
                buf = new BufferedInputStream(new FileInputStream(sourceFile));
                String param = "ac=createFile&document_id=";
                param += "&token=" + MainApp.getInstance().getConfig().getProperty("token");

                new Sender().loadContent(this   , 1, MainApp.getInstance().getConfig().getProperty("url") + "service/document", param);
            }
        }catch (FileNotFoundException ioe)
        {
            System.out.println(ioe.toString());
        }

    }
    private long totoalRead =0;
    private  long sizes = 0;
    private  String file_id = "";
    private  String file_name = "";
    private void postFile()
    {

        try
        {
            int p = (int) ((totoalRead/sizes) * 100);
            txtPercentage.setText("" + p + " %");
            if(totoalRead>=sizes)
            {

                String param = "ac=commitFile&file_id=" + file_id;
                param += "&token=" + MainApp.getInstance().getConfig().getProperty("token");
                param += "&for_id=" + MainApp.getInstance().DOC_FOR_ID;
                param += "&user_id=" + MainApp.getInstance().getConfig().getProperty("user_id");
                param += "&company_id=" + MainApp.getInstance().getConfig().getProperty("company_id");
                param += "&len=" + sizes;
                if(file_name.equals(""))
                {
                    int index = filePath.lastIndexOf('/');
                    if(index !=-1)
                    {
                        file_name = filePath.substring(index + 1);
                    }else
                    {
                        index = filePath.lastIndexOf('\\');
                        if(index !=-1)
                        {
                            file_name = filePath.substring(index + 1);
                        }
                    }
                }else
                {
                    int index = filePath.lastIndexOf('.');
                    if(index !=-1)
                    {
                        file_name = file_name + filePath.substring(index );
                    }
                }
                param += "&file_name=" + Tool.getInstance().urlEncode(file_name);
                new Sender().loadContent(this   , 3, MainApp.getInstance().getConfig().getProperty("url") + "service/document", param);
                return;
            }

            int byteRead = 1024 * 100;
            byte[] bytes = new byte[byteRead];
            int read = buf.read(bytes, 0, bytes.length);
            if(read>0)
            {
                totoalRead  += read;
                byte[] b = new byte[read];
                for(int i=0; i<read; i++)
                {
                    b[i] = bytes[i];
                }
                String page = MainApp.getInstance().getConfig().getProperty("url") + "service/up_document/" + MainApp.getInstance().getConfig().getProperty("token") + "/" + file_id + "/" + b.length;
                new Sender().writeDoc(this   , 2, page, b);

            }
        }catch (IOException ex)
        {
            System.out.println(ex.toString());
        }

    }

    private void showChooser()
    {
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");


        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Chọn tập tin");
        //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(chooserIntent, PICK_FILE);
        //Intent intent = new Intent();
        //intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
       // startActivityForResult(Intent.createChooser(intent, "Thư viện"), PICK_FILE);
    }

    private void captureImage() {

        dispatchTakePictureIntent();
        /*
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);*/
    }

    private void recordVideo() {

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createVideoFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "vinacert.vn.psample.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
            }
        }

    }
    private File createVideoFile() throws IOException{

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "VID_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".mp4",         /* suffix */
                storageDir      /* directory */
        );
        filePath = image.getAbsolutePath();
        return image;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        filePath = image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "vinacert.vn.psample.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            }
        }
    }
    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {


                try
                {
                    imgPreview.setVisibility(View.VISIBLE);
                    vidPreview.setVisibility(View.GONE);

                    File file = new File(filePath);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                    if (bitmap != null) {
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        if(width>600)
                        {
                            float aspectRatio = bitmap.getWidth() /
                                    (float) bitmap.getHeight();
                            width = 600;
                            height = Math.round(width / aspectRatio);

                            bitmap = Bitmap.createScaledBitmap(
                                    bitmap, width, height, false);

                        }
                        filePath = MainApp.getInstance().getDirectory("") + "data.jpg";
                        File dest = new File(filePath);
                        FileOutputStream out = new FileOutputStream(dest);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        if(dest.exists()){
                            imgPreview.setImageBitmap(bitmap);
                            btnUpload.setVisibility(View.VISIBLE);
                            txtDocumentName.setVisibility(View.VISIBLE);

                        }

                    }

                }catch (Exception ex){}



            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                imgPreview.setVisibility(View.GONE);

                btnUpload.setVisibility(View.VISIBLE);
                txtDocumentName.setVisibility(View.VISIBLE);
                imgPreview.setVisibility(View.GONE);
                vidPreview.setVisibility(View.VISIBLE);
                vidPreview.setVideoPath("" + data.getData());
                // start playing
                vidPreview.start();

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }else  if (requestCode == PICK_FILE && resultCode == RESULT_OK) {
            if (data == null) {
                return;
            }

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn,
                    null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
            if(filePath == null)
            {
                return;
            }

            File myFile = new File(filePath);

            if(myFile.exists())
            {
                filePath = myFile.getAbsolutePath();
                uploadToServer();
            }

        }
    }
    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column);
        cursor.close();
        return result;
    }
    public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery( contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

}

