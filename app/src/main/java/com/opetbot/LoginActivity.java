package com.opetbot;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.opetbot.Webservices.AsyncTaskAuthenticateOrRegister;
import com.opetbot.Webservices.RequestUserLogin;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_login)
    Button _loginButton;
    @Bind(R.id.link_signup)
    TextView _signupLink;
    CallbackManager callbackManager;
    RequestUserLogin reqLogin;
    String email, password;
    LoginButton loginButton;
    public static String email_str, fname_str, lname_str;
    /*google plus*/
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    SignInButton google_plus_login;
    public static ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();
        /*Google plus*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();

        google_plus_login.setSize(SignInButton.SIZE_STANDARD);
        google_plus_login.setScopes(gso.getScopeArray());

        /*Facebook*/
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject facebook_object, GraphResponse response) {

                                if (response.getError() != null) {
                                    // handle error
                                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.e("Facebook profile object", facebook_object.toString());
                                    try {
                                        email_str = facebook_object.getString("email");
                                        fname_str = facebook_object.getString("first_name");
                                        lname_str = facebook_object.getString("last_name");
                                        new AsyncTaskAuthenticateOrRegister(LoginActivity.this).execute();
                                    } catch (JSONException e) {
                                        Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "last_name,first_name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(LoginActivity.this,"Something went wrong",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(LoginActivity.this,"Something went wrong",Toast.LENGTH_LONG).show();
            }
        });
    }


    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        } else {
            _loginButton.setEnabled(false);
            email = _emailText.getText().toString();
            password = _passwordText.getText().toString();
            onLoginSuccess();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

       /*Google plus*/
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        //Facebook login
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e("Google profile", acct.getEmail() + "\t" + acct.getDisplayName());
            String fullname = acct.getDisplayName();
            String[] parts = fullname.split("\\s+");

            email_str = acct.getEmail();
            fname_str = parts[0];
            lname_str = parts[1];
            new AsyncTaskAuthenticateOrRegister(LoginActivity.this).execute();

            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {
            //updateUI(false);
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        _loginButton.setEnabled(false);
        reqLogin.postRequest(LoginActivity.this, email, password);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    /*Initializing views */
    void init() {
        reqLogin = new RequestUserLogin();
        google_plus_login = (SignInButton) findViewById(R.id.google_plus_login);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        _loginButton.setOnClickListener(this);
        google_plus_login.setOnClickListener(this);
        _signupLink.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /*normal login process*/
            case R.id.btn_login:
                login();
                break;

                /*switch to registration page*/
            case R.id.link_signup:
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                break;

                /*Google plus button*/
            case R.id.google_plus_login:
                String serverClientId = "773351432872-1sf53kb0qkn23603jq6hvc2q46qvkqnu.apps.googleusercontent.com";
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                        .requestEmail()
                        .build();
                mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) return super.onKeyDown(keyCode, event);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
        return super.onKeyDown(keyCode, event);
    }

}
