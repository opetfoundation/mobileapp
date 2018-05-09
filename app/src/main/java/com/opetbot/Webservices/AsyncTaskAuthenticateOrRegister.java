package com.opetbot.Webservices;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.opetbot.MainActivity;
import com.opetbot.SharedPreferences.SharedPreferencesData;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.opetbot.Config.ConfigUrl.authenticate_or_register_url;
import static com.opetbot.Config.ConfigUrl.registration_device_id;
import static com.opetbot.Config.ConfigUrl.registration_email;
import static com.opetbot.Config.ConfigUrl.registration_fname;
import static com.opetbot.Config.ConfigUrl.registration_lname;
import static com.opetbot.Config.ConfigUrl.registration_mobile;
import static com.opetbot.Config.ConfigUrl.registration_password;
import static com.opetbot.LoginActivity.email_str;
import static com.opetbot.LoginActivity.fname_str;
import static com.opetbot.LoginActivity.lname_str;
import static com.opetbot.LoginActivity.progressDialog;


/**
 * Created by Softeligent on 18-01-18.
 */
public class AsyncTaskAuthenticateOrRegister extends AsyncTask<String, Void, JSONObject> {
    public static String responseServer;
    public static Context context;

    public AsyncTaskAuthenticateOrRegister(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject result = null;
        try {
            Log.e("customer data send url", authenticate_or_register_url);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(authenticate_or_register_url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(registration_email, email_str));
            nameValuePairs.add(new BasicNameValuePair(registration_fname, fname_str));
            nameValuePairs.add(new BasicNameValuePair(registration_lname, lname_str));
            nameValuePairs.add(new BasicNameValuePair(registration_mobile, null));
            nameValuePairs.add(new BasicNameValuePair(registration_password, null));
            String token = new SharedPreferencesData(context).getDeviceToken();
            if (token != null) {
                nameValuePairs.add(new BasicNameValuePair(registration_device_id, token));
            }
            else {
                Toast.makeText(context,"Something went wrong",Toast.LENGTH_LONG).show();
            }
            Log.e("Customer data sent", nameValuePairs.toString());
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            InputStream inputStream = response.getEntity().getContent();
            InputStreamToString str = new InputStreamToString();
            responseServer = str.getStringFromInputStream(inputStream);
            Log.e("responseText", "responseText -----" + responseServer);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            result = new JSONObject(responseServer);

        } catch (JSONException e) {
        }
        return result;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        try {
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
        } catch (JSONException e) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
        }

    }

    /*Saving login details and channels*/
    public void saveLoginDetails(String member_id, String cust_fname) {
        new SharedPreferencesData(context).saveLoginDetails(member_id, cust_fname);
    }

    public void saveCust_Id(String MembershipId) {
        new SharedPreferencesData(context).saveUser_Id(MembershipId);
    }

    public void saveChannels(String Channels) {
        new SharedPreferencesData(context).saveChannels(Channels);
    }


    public static class InputStreamToString {

        public static void main(String[] args) throws IOException {
            InputStream is = new ByteArrayInputStream("file content".getBytes());
            String result = getStringFromInputStream(is);
            System.out.println(result);
            System.out.println("Done");
        }

        // convert InputStream to String
        private static String getStringFromInputStream(InputStream is) {

            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return sb.toString();
        }
    }

}

