package vinacert.vn.psample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import vinacert.vn.psample.data.DataTable;

public class RegisterActivity extends AppCompatActivity implements ResponseData{

    // UI references.
    private AutoCompleteTextView mURLView;
    private AutoCompleteTextView mUserView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView lblAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up the login form.
        mUserView = (AutoCompleteTextView) findViewById(R.id.email);
        mURLView = (AutoCompleteTextView) findViewById(R.id.url);
        lblAlert = (TextView) findViewById(R.id.lbl_alert);

        mURLView.setText(MainApp.getInstance().getConfig().getProperty("url"));
        if(mURLView.getText().toString().equals(""))
        {
            mURLView.setText("http://118.70.124.172:8383/");
        }

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }
    public void responseDataTable(int code, DataTable dt)
    {

    }
    public void responseText(int code, String data)
    {
        if(code == -1)
        {
            MainApp.getInstance().alert(this, "Lỗi: " + data);
            return;
        }
        String[] arr = Tool.getInstance().split(data, ';');
        if(arr.length>=3)
        {
            MainApp.getInstance().getConfig().setProperty("user_name", mUserView.getText().toString());
            for(int i=0; i<arr.length; i++)
            {
                int index = arr[i].indexOf("=");
                if(index != -1)
                {
                    String key = arr[i].substring(0, index);
                    String value = arr[i].substring(index + 1);
                    if(key.equals("token"))
                    {
                        MainApp.getInstance().getConfig().setProperty("token",value);
                    }
                    else if(key.equals("company_id"))
                    {
                        MainApp.getInstance().getConfig().setProperty("company_id",value);
                    } else if(key.equals("user_id"))
                    {
                        MainApp.getInstance().getConfig().setProperty("user_id",value);
                    } else if(key.equals("company_name"))
                    {
                        MainApp.getInstance().getConfig().setProperty("company_name",value);
                    }
                    else if(key.equals("user_name"))
                    {
                        MainApp.getInstance().getConfig().setProperty("user_name", value);
                    }
                    else if(key.equals("user_group_id"))
                    {
                        MainApp.getInstance().getConfig().setProperty("user_group_id", value);
                    }
                }

            }
            MainApp.getInstance().getConfig().setProperty("url", mURLView.getText().toString());
            Sender.GetInstance().ROOT_HTTP = mURLView.getText().toString();
            MainApp.getInstance().getConfig().save();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }
        else
        {
            MainApp.getInstance().alert(this, "Đang nhập không hợp lệ.");
        }
    }
    private void attemptLogin() {

        lblAlert.setText("");
        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }


        if (cancel) {

            focusView.requestFocus();
        } else {
            try
            {
                String pass = mPasswordView.getText().toString();
                String param = "user=" + Tool.getInstance().urlEncode(mUserView.getText().toString());
                param += "&pass=" + Tool.getInstance().urlEncode(Tool.getInstance().sha1(pass));
                String url = mURLView.getText().toString();
                if(url.charAt(url.length()-1) != '/')
                {
                    url = url + "/";
                }
                mURLView.setText(url);

                MainApp.getInstance().getConfig().setProperty("url", url);
                Sender.GetInstance().ROOT_HTTP = url;

                Sender.GetInstance().loadContent(this,1, url + "service/register", param);


            }catch(Exception ex)
            {
                MainApp.getInstance().getException(this, ex);
            }

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 1;
    }

}
