package com.opetbot.Webservices;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.opetbot.MainActivity;
import com.opetbot.SharedPreferences.SharedPreferencesData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.opetbot.Config.ConfigUrl.login_url;
import static com.opetbot.Config.ConfigUrl.registration_device_id;
import static com.opetbot.Config.ConfigUrl.registration_email;
import static com.opetbot.Config.ConfigUrl.registration_password;
import static com.opetbot.LoginActivity.progressDialog;


/**
 * Created by Softeligent on 16-01-18.
 */

public class RequestUserLogin {

    private String result;

    Context context1;

    public void postRequest(final Context context, final String email, final String password) {

        context1 = context;

        //Showing the progress dialog
        // final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, login_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //   loading.dismiss();
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
                                Intent it = new Intent(context, MainActivity.class);
                                context.startActivity(it);
                                if (context instanceof Activity) {
                                    ((Activity) context).finish();
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Invalid email address or password", Toast.LENGTH_LONG).show();
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
                params.put(registration_email, email);
                params.put(registration_password, password);
                /*String token = new SharedPreferencesData(context1).getDeviceToken();
                if (token != null) {*/
                    params.put(registration_device_id, "dojLCmd4hig:APA91bGSY25fTxt9Jdkhvf8oRRxz25geQ9yI9s-Gvju0Zs-WOsjNH8EoP1XEEg9AHALQE4tk4rf60NK4v8IrzQPtzmUFs0k1RnJYAgCnuVNwCb2XnxP3x8IVHbLsAgEftz7yLvF_aQjA");
               /* }
                else {
                    Toast.makeText(context1,"Something went wrong",Toast.LENGTH_LONG).show();
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
