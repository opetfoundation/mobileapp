package com.opetbot.Webservices;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.opetbot.MainActivity;
import com.opetbot.R;
import com.opetbot.SharedPreferences.SharedPreferencesData;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.internal.zzahg.runOnUiThread;
import static com.opetbot.Config.ConfigUrl.mobile_country_code;
import static com.opetbot.Config.ConfigUrl.registration_device_id;
import static com.opetbot.Config.ConfigUrl.registration_email;
import static com.opetbot.Config.ConfigUrl.registration_fname;
import static com.opetbot.Config.ConfigUrl.registration_lname;
import static com.opetbot.Config.ConfigUrl.registration_mobile;
import static com.opetbot.Config.ConfigUrl.registration_password;
import static com.opetbot.Config.ConfigUrl.registration_url;

/**
 * Created by Softeligent on 16-01-18.
 */

public class RequestUserRegistration {

    private String result;
    Context context1;
    ProgressDialog progressDialog;
    public void postRequest(final Context context, final String fname, final String lname, final String email, final String mobile, final String reEnterPassword,final String mobilecountrycode) {
        context1 = context;
        //Showing the progress dialog
        progressDialog = new ProgressDialog(context1, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, registration_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response Login", response);

                        try {
                            JSONObject result = new JSONObject(response);
                            if (!result.isNull("user_data")) {
                                JSONObject user_obj = result.getJSONObject("user_data");

                                Log.e("Customer data", String.valueOf(user_obj));
                                String cust_id = user_obj.getString("user_id");
                                String first_name = user_obj.getString("first_name");
                                String last_name = user_obj.getString("last_name");
                                saveCust_Id(cust_id);
                                saveLoginDetails(cust_id, first_name + " " + last_name);
                                saveChannels(result.getJSONArray("channels").toString());
                                progressDialog.dismiss();
                                Toast.makeText(context, "Registration successful", Toast.LENGTH_LONG).show();
                                Intent it = new Intent(context, MainActivity.class);
                                context.startActivity(it);
                                if (context instanceof Activity) {
                                    ((Activity) context).finish();
                                }

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(registration_fname, fname);
                params.put(registration_lname, lname);
                params.put(registration_email, email);
                params.put(registration_mobile, mobile);
                params.put(registration_password, reEnterPassword);
                params.put(mobile_country_code,mobilecountrycode);
                params.put(registration_device_id, "dojLCmd4hig:APA91bGSY25fTxt9Jdkhvf8oRRxz25geQ9yI9s-Gvju0Zs-WOsjNH8EoP1XEEg9AHALQE4tk4rf60NK4v8IrzQPtzmUFs0k1RnJYAgCnuVNwCb2XnxP3x8IVHbLsAgEftz7yLvF_aQjA");
                String token = new SharedPreferencesData(context1).getDeviceToken();
              /*  if (token != null) {*/

                /*}
                else {
                    new Thread()
                    {
                        public void run()
                        {
                           runOnUiThread(new Runnable()
                            {
                                public void run()
                                {
                                    Toast.makeText(context1,"Something went wrong",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }.start();

                }*/
                params.put("Content-Type", "application/json");
                Log.e("Data sent", params.toString());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    public void saveLoginDetails(String member_id, String cust_fname) {
        new SharedPreferencesData(context1).saveLoginDetails(member_id, cust_fname);
    }

    public void saveCust_Id(String MembershipId) {
        new SharedPreferencesData(context1).saveUser_Id(MembershipId);
    }

    public void saveChannels(String Channels) {
        new SharedPreferencesData(context1).saveChannels(Channels);
    }
}
