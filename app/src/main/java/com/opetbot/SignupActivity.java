package com.opetbot;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.opetbot.Webservices.AsyncTaskSignupformIsEmailExists;
import com.opetbot.Webservices.RequestUserRegistration;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.opetbot.Config.ConfigUrl.email_verification_url;
import static com.opetbot.Config.ConfigUrl.registration_email;
import static com.opetbot.Config.ConfigUrl.registration_fname;
import static com.opetbot.Config.ConfigUrl.registration_lname;
import static com.opetbot.Config.ConfigUrl.registration_mobile;
import static com.opetbot.Config.ConfigUrl.registration_password;
import static com.opetbot.Config.ConfigUrl.registration_url;
import static org.alicebot.ab.MagicStrings.comment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.opetbot.utils.CheckNetworkConnection;
import com.opetbot.utils.NoInternetClass;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    @Bind(R.id.input_fname)
    EditText _fnameText;
    @Bind(R.id.input_lname)
    EditText _lnameText;
    public static EditText _emailText;
    @Bind(R.id.input_mobile)
    EditText _mobileText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.input_reEnterPassword)
    EditText _reEnterPasswordText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.link_login)
    TextView _loginLink;
    RequestQueue queue;
    String fname;
    String lname;
    public static String email;
    String mobile;
    String password;
    String reEnterPassword;
    RequestUserRegistration registerobj;
    public static ImageView email_exists;
    public static Boolean valid_email;
    public static int email_status;
    public static ProgressBar email_progress;
    public static ProgressDialog progressDialog;
    public static Spinner mobile_country_code;
    public static String mobile_country_code_str;
    AlertDialog alertOtp;
    String otp = null;
    String email_str = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        init();
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

         /*Email existing */
        valid_email = false;
        _emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!_emailText.getText().toString().isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(_emailText.getText().toString()).matches()) {
                    _emailText.setError("Enter Valid Email Address");
                    email_exists.setVisibility(View.GONE);
                    valid_email = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!_emailText.getText().toString().isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(_emailText.getText().toString()).matches()) {
                    _emailText.setError("Enter Valid Email Address");
                    email_exists.setVisibility(View.GONE);
                    valid_email = false;
                } else if (_emailText.getText().toString().isEmpty()) {
                    _emailText.setError(null);
                    valid_email = true;
                } else if (!_emailText.getText().toString().isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(_emailText.getText().toString()).matches()) {
                    _emailText.setError(null);

                    email = _emailText.getText().toString();
                    sendRequestSignupCheckEmailDuplication();

                    if (email_status == 1) {
                        valid_email = false;
                        _emailText.setError("Email address already exists");
                    } else if (email_status == 0) {
                        valid_email = true;
                        _emailText.setError(null);
                    }
                }
            }
        });

        mobile_country_code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    mobile_country_code_str = mobile_country_code.getSelectedItem().toString();
                    Log.e("Selected code", mobile_country_code_str);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        } else {
            fname = _fnameText.getText().toString();
            lname = _lnameText.getText().toString();
            email = _emailText.getText().toString();
            mobile = _mobileText.getText().toString();
            password = _passwordText.getText().toString();
            reEnterPassword = _reEnterPasswordText.getText().toString();
            if (valid_email == true) {
                onSuccess();
            }
        }
    }


    public void onSuccess() {
        progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        _signupButton.setEnabled(false);
        postRequest(email);

    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        fname = _fnameText.getText().toString();
        lname = _lnameText.getText().toString();
        email = _emailText.getText().toString();
        mobile = _mobileText.getText().toString();
        password = _passwordText.getText().toString();
        reEnterPassword = _reEnterPasswordText.getText().toString();

        if (fname.isEmpty() || fname.length() < 3) {
            _fnameText.setError("at least 3 characters");
            valid = false;
        } else {
            _fnameText.setError(null);
        }

        if (lname.isEmpty() || lname.length() < 3) {
            _lnameText.setError("at least 3 characters");
            valid = false;
        } else {
            _lnameText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length() != 10) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        if (mobile_country_code.getSelectedItemPosition() == 0) {
            Toast.makeText(SignupActivity.this, "Select country code", Toast.LENGTH_LONG).show();
            valid = false;
        }


        return valid;
    }


    /*initializing variables*/
    void init() {
        /*Normal registration*/
        queue = Volley.newRequestQueue(this);
        registerobj = new RequestUserRegistration();
        email_progress = (ProgressBar) findViewById(R.id.email_progress);
        _emailText = (EditText) findViewById(R.id.input_email);
        email_exists = (ImageView) findViewById(R.id.email_exists);
        mobile_country_code = (Spinner) findViewById(R.id.mobile_country_code);

    }

    void sendRequestSignupCheckEmailDuplication() {
        if (CheckNetworkConnection.isConnectionAvailable(SignupActivity.this)) {
            new AsyncTaskSignupformIsEmailExists(SignupActivity.this).execute();
        } else {
            String message = getResources().getString(R.string.no_internet_connection);
            new NoInternetClass(SignupActivity.this).showCustomDialog(message, true);
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


    public void postRequest(final String email) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, email_verification_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response email verify", response);
                        try {
                            JSONObject main_obj = new JSONObject(response);
                            showDialog(main_obj);
                        } catch (JSONException e) {
                            Toast.makeText(SignupActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignupActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(registration_email, email);
                params.put("Content-Type", "application/json");
                Log.e("Data sent", params.toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SignupActivity.this);
        requestQueue.add(stringRequest);

    }

    public void showDialog(final JSONObject mainObj) {
        try {
            JSONObject main_obj = mainObj;
            JSONObject verification_data = main_obj.getJSONObject("verification_data");
            email_str = verification_data.getString("email");
            otp = verification_data.getString("otp");

        } catch (JSONException e) {
            Toast.makeText(SignupActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SignupActivity.this);
        LayoutInflater inflater = LayoutInflater.from(SignupActivity.this);
        final View dialogView = inflater.inflate(R.layout.dialog_otp_generation, null);
        dialogBuilder.setView(dialogView);
        final Button submit_otp, resend_otp;
        final ImageView cancel_action;
        final EditText edittext_otp = (EditText) dialogView.findViewById(R.id.edittext_otp);
        final TextInputLayout textinputlayout_otp = (TextInputLayout) dialogView.findViewById(R.id.textinputlayout_otp);
        submit_otp = (Button) dialogView.findViewById(R.id.submit_otp);
        resend_otp = (Button) dialogView.findViewById(R.id.resend_otp);
        cancel_action = (ImageView) dialogView.findViewById(R.id.cancel_action);
        alertOtp = dialogBuilder.create();
        alertOtp.setCancelable(false);
        submit_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edittext_otp.getText().toString().isEmpty()) {
                    textinputlayout_otp.setError("Enter otp");
                } else if (!edittext_otp.getText().toString().equals(otp)) {
                    textinputlayout_otp.setError("Invalid otp");
                } else {

                    registerobj.postRequest(SignupActivity.this, fname, lname, email, mobile, reEnterPassword, mobile_country_code_str);
                }
            }
        });
        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertOtp.dismiss();
                postRequest(email);
            }
        });

        cancel_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertOtp.dismiss();
            }
        });
        alertOtp.show();
    }

}