package vinacert.vn.psample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import vinacert.vn.psample.data.DataTable;

public class LoginActivity extends AppCompatActivity  implements ResponseData {


    private AutoCompleteTextView mUserView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView lblAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        mUserView = (AutoCompleteTextView) findViewById(R.id.email);

        lblAlert = (TextView) findViewById(R.id.lbl_alert);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    login();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }
    private String user_id = "";
    public void responseText(int code, String data)
    {
        if(code == -1)
        {
            MainApp.getInstance().alert(this, data);
            return;
        }else if(code == 1)
        {
            if(data.equals("INCORRECT") || data.equals("INVALID"))
            {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showInvalid();

                    }
                });
            }else
            {
                String[] arr = data.split(";");

                if(arr.length>3)
                {
                    user_id = arr[3];
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            MainApp.getInstance().getConfig().setProperty("user_id", user_id);
                            MainApp.getInstance().getConfig().setProperty("user_name", mUserView.getText().toString());
                            MainApp.getInstance().getConfig().save();
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);

                        }
                    });
                }else
                {

                }
            }


        }
    }

    public void responseDataTable(int code, DataTable tb)
    {
        if(code == 1)
        {

        }
    }

    private void login()
    {
        try
        {
            String user = mUserView.getText().toString();
            String url = MainApp.getInstance().getConfig().getProperty("url");
            String param = "ac=login&user=" + user;
            param += "&pass=" + Tool.getInstance().sha1(mPasswordView.getText().toString());
            param += "&user_id=" + MainApp.getInstance().getConfig().getProperty("user_id");
            param += "&company_id=" + MainApp.getInstance().getConfig().getProperty("company_id");
            param += "&token=" + MainApp.getInstance().getConfig().getProperty("token");
            new Sender().loadContent(this,1, url + "service/getdata", param);
        }catch (Exception ex)
        {
            System.out.println(ex.toString());
        }

    }
    private  void showInvalid()
    {
        MainApp.getInstance().alert(this, "Đăng nhập không hợp lệ");
    }

}
