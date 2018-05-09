package com.opetbot.Webservices;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import static com.opetbot.Config.ConfigUrl.is_email_exists_url;
import static com.opetbot.Config.ConfigUrl.registration_email;
import static com.opetbot.SignupActivity.email;
import static com.opetbot.SignupActivity.email_exists;
import static com.opetbot.SignupActivity.email_progress;
import static com.opetbot.SignupActivity.valid_email;
import static com.opetbot.SignupActivity._emailText;
/**
 * Created by Softeligent on 12-12-17.
 */

public class AsyncTaskSignupformIsEmailExists extends AsyncTask<String, Void, Integer> {
    public static int responseServer;
    public static Context context;

    public AsyncTaskSignupformIsEmailExists(Context context) {
        this.context = context;
        email_progress.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Integer doInBackground(String... params) {
        int result ;
        try {
            Log.e("customer data send url", is_email_exists_url+"?email="+email);
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httppost = new HttpGet(is_email_exists_url);


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(registration_email, email));
            Log.e("Customer data sent", nameValuePairs.toString());
            httppost.setURI(URI.create(is_email_exists_url+"?email="+email));
            HttpResponse response = httpclient.execute(httppost);
            InputStream inputStream = response.getEntity().getContent();
            InputStreamToString str = new InputStreamToString();
            responseServer = Integer.parseInt(str.getStringFromInputStream(inputStream));
            Log.e("responseText", "responseText -----" + responseServer);

        } catch (Exception e) {
            e.printStackTrace();
        }
            result = responseServer;
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        email_progress.setVisibility(View.GONE);
            if (result == 1) {
                valid_email=false;
                email_exists.setVisibility(View.GONE);
                _emailText.setError("Email address already exists");
            } else if (result == 0) {
                email_exists.setVisibility(View.VISIBLE);
                _emailText.setError(null);
                valid_email=true;
            }
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