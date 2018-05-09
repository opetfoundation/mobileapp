package com.opetbot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.opetbot.Adapter.BottomSheetPowerFunctionAdapter;
import com.opetbot.Adapter.BottomSheetSubjectAdapter;
import com.opetbot.Adapter.BottomSheetTopicAdapter;
import com.opetbot.Adapter.ChatChannelMessageAdapter;
import com.opetbot.GetterSetter.ChatMessage;
import com.opetbot.GetterSetter.gettersetterSubject;
import com.opetbot.GetterSetter.gettersetterTopics;
import com.opetbot.GetterSetter.gettersetterpowerfunction;
import com.opetbot.SharedPreferences.SharedPreferencesData;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Timer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.GsonFactory;
import ai.api.model.AIContext;
import ai.api.model.AIError;
import ai.api.model.AIEvent;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;
import pl.droidsonroids.gif.GifImageView;

import static android.util.JsonToken.NULL;
import static com.opetbot.Config.ConfigUrl.answer;
import static com.opetbot.Config.ConfigUrl.fetch_all_power_functions_url;
import static com.opetbot.Config.ConfigUrl.fetch_all_subject_url;
import static com.opetbot.Config.ConfigUrl.fetch_direct_question_general_chat_url;
import static com.opetbot.Config.ConfigUrl.fetch_subject_topic;
import static com.opetbot.Config.ConfigUrl.gc_history;
import static com.opetbot.Config.ConfigUrl.get_general_chat_history_url;
import static com.opetbot.Config.ConfigUrl.get_subject_chat_url;
import static com.opetbot.Config.ConfigUrl.power_function;
import static com.opetbot.Config.ConfigUrl.power_functions_array;
import static com.opetbot.Config.ConfigUrl.question;
import static com.opetbot.Config.ConfigUrl.question_text;
import static com.opetbot.Config.ConfigUrl.quiz_subj_topics_array;
import static com.opetbot.Config.ConfigUrl.quiz_subjects_array;
import static com.opetbot.Config.ConfigUrl.sc_history;
import static com.opetbot.Config.ConfigUrl.search_key;
import static com.opetbot.Config.ConfigUrl.skey_str;
import static com.opetbot.Config.ConfigUrl.subject;
import static com.opetbot.Config.ConfigUrl.subject_id;
import static com.opetbot.Config.ConfigUrl.topic;
import static com.opetbot.Config.ConfigUrl.topic_id;


public class ChannelActivity extends AppCompatActivity implements View.OnClickListener, BottomSheetPowerFunctionAdapter.PowerFunctionItemListener, BottomSheetSubjectAdapter.SubjectItemListener, BottomSheetTopicAdapter.TopicItemListener {
    private ListView mListView;
    public static Button mButtonSend;
    public static EditText mEditTextMessage;
    public static ImageView mImageView, iv_image_tilde, scan_image;
    public Bot bot;
    public static Chat chat;
    private ChatChannelMessageAdapter mAdapter;
    public static final String TAG = ChannelActivity.class.getName();
    private AIDataService aiDataService;
    private Gson gson = GsonFactory.getGson();
    String response;
    BottomSheetBehavior behavior;
    RecyclerView recyclerView, recyclerView1, recyclerView2;
    private BottomSheetPowerFunctionAdapter bottomsheetPowerFunctionAdapter;
    BottomSheetSubjectAdapter bottomSheetSubjectAdapter;
    BottomSheetTopicAdapter bottomSheetTopicAdapter;
    View bottomSheet;
    ArrayList<gettersetterpowerfunction> items;
    ArrayList<gettersetterSubject> itemsSubject;
    ArrayList<gettersetterTopics> itemsTopics;
    String end_str = "*";
    Intent intent;
    pl.droidsonroids.gif.GifImageView loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_chat);
        initView();

        Intent it = getIntent();
        // String response=it.getStringExtra("Direct_data");
       /* if(response!=null || !response.equals(null) || !response.equals("null"))
        {

        }*/
        setResult(RESULT_OK);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChannelActivity.this));
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(ChannelActivity.this));
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(ChannelActivity.this));

        mEditTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                recyclerView.setVisibility(View.GONE);
                recyclerView1.setVisibility(View.GONE);
                recyclerView2.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                recyclerView.setVisibility(View.GONE);
                recyclerView1.setVisibility(View.GONE);
                recyclerView2.setVisibility(View.GONE);
            }
        });
    }

    void initView() {
        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = (Button) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        loader = (GifImageView) findViewById(R.id.loader);
        //  mImageView = (ImageView) findViewById(R.id.iv_image);
        iv_image_tilde = (ImageView) findViewById(R.id.iv_image_tilde);
        scan_image = (ImageView) findViewById(R.id.scan_image);
        scan_image.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView1 = (RecyclerView) findViewById(R.id.recyclerView1);
        recyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);
        mAdapter = new ChatChannelMessageAdapter(ChannelActivity.this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);
        mButtonSend.setOnClickListener(this);
        iv_image_tilde.setOnClickListener(this);
        bottomSheet = findViewById(R.id.bottom_sheet);
        recyclerView.setVisibility(View.GONE);
        recyclerView1.setVisibility(View.GONE);
        recyclerView2.setVisibility(View.GONE);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        items = new ArrayList<>();
        itemsSubject = new ArrayList<>();
        itemsTopics = new ArrayList<>();
        //get the working directory
        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/hari";
        System.out.println("Working Directory = " + MagicStrings.root_path);
        AIMLProcessor.extension = new PCAIMLProcessorExtension();
        //Assign the AIML files to bot for processing
        bot = new Bot("Hari", MagicStrings.root_path, "chat");
        chat = new Chat(bot);
        String[] args = null;
        mainFunction(args);
        initService();
        fetch_subject_chat_history();
        mListView.setSelection(mListView.getAdapter().getCount()-1);
    }

    void setAdapter() {
        bottomsheetPowerFunctionAdapter = new BottomSheetPowerFunctionAdapter(items, this);
        recyclerView.setAdapter(bottomsheetPowerFunctionAdapter);
        bottomSheetSubjectAdapter = new BottomSheetSubjectAdapter(itemsSubject, this);
        recyclerView1.setAdapter(bottomSheetSubjectAdapter);
        bottomSheetTopicAdapter = new BottomSheetTopicAdapter(itemsTopics, this);
        recyclerView2.setAdapter(bottomSheetTopicAdapter);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String message = mEditTextMessage.getText().toString();
                //bot
                response = chat.multisentenceRespond(mEditTextMessage.getText().toString());
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(ChannelActivity.this, "Enter Message", Toast.LENGTH_LONG).show();
                    return;
                }

                if (message.startsWith("~")) {
                    sendMessage(message.substring(1));
                    mEditTextMessage.setText("");
                    saveSearchKey(message.substring(1));
                    sendStringData(message.substring(1));
                    // fetchPowerFunctions();
                } else {
                    loader.setVisibility(View.VISIBLE);
                    sendRequest();
                    sendMessage(message);
                    mEditTextMessage.setText("");
                    mListView.setSelection(mAdapter.getCount() - 1);
                    InputMethodManager imm = (InputMethodManager) ChannelActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }

                break;
            case R.id.iv_image_tilde:
                recyclerView.setVisibility(View.GONE);
                fetchPowerFunctions();
                break;
            case R.id.scan_image:
                Intent it = new Intent(ChannelActivity.this, QuestionCameraScan.class);
                startActivity(it);
                break;
        }
    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        //mimicOtherMessage(message);
    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(null, true, true);
        mAdapter.add(chatMessage);
        mimicOtherMessage();
    }

    private void mimicOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        mAdapter.add(chatMessage);
    }

    //check SD card availability
    public static boolean isSDCARDAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? true : false;
    }

    //copying the file
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    //Request and response of user and the bot
    public static void mainFunction(String[] args) {
        MagicBooleans.trace_mode = false;
        System.out.println("trace mode = " + MagicBooleans.trace_mode);
        Graphmaster.enableShortCuts = true;
        Timer timer = new Timer();
        String request = "Hello.";
        String response = chat.multisentenceRespond(request);

        System.out.println("Human: " + request);
        System.out.println("Robot: " + response);
    }


    /*Dialogflow call*/
    /*
   * AIRequest should have query OR event
   */
    private void sendRequest() {

        final String queryString = String.valueOf(mEditTextMessage.getText());
        final String eventString = null;
        final String contextString = String.valueOf(mEditTextMessage.getText());

        if (TextUtils.isEmpty(queryString) && TextUtils.isEmpty(eventString)) {
            onError(new AIError(getString(R.string.non_empty_query)));
            return;
        }

        final AsyncTask<String, Void, AIResponse> task = new AsyncTask<String, Void, AIResponse>() {

            private AIError aiError;

            @Override
            protected AIResponse doInBackground(final String... params) {
                final AIRequest request = new AIRequest();
                String query = params[0];
                String event = params[1];

                if (!TextUtils.isEmpty(query))
                    request.setQuery(query);
                if (!TextUtils.isEmpty(event))
                    request.setEvent(new AIEvent(event));
                final String contextString = params[2];
                RequestExtras requestExtras = null;
                if (!TextUtils.isEmpty(contextString)) {
                    final List<AIContext> contexts = Collections.singletonList(new AIContext(contextString));
                    requestExtras = new RequestExtras(contexts, null);
                }

                try {
                    return aiDataService.request(request, requestExtras);
                } catch (final AIServiceException e) {
                    aiError = new AIError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final AIResponse response) {

                if (response != null) {
                    onResult(response);
                } else {
                    onError(aiError);
                }
            }
        };
        task.execute(queryString, eventString, contextString);
    }

    private void onResult(final AIResponse response1) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "onResult");
                response = response1.toString();
                Log.e("Response AI...... ", response);
                Log.e(TAG, "Received success response");

                // this is example how to get different parts of result object
                final Status status = response1.getStatus();
                Log.e(TAG, "Status code: " + status.getCode());
                Log.e(TAG, "Status type: " + status.getErrorType());

                final Result result = response1.getResult();
                Log.e(TAG, "Resolved query: " + result.getResolvedQuery());

                Log.e(TAG, "Action: " + result.getAction());

                final String speech = result.getFulfillment().getSpeech();
                Log.e(TAG, "Speech: " + speech);
                Log.e("DATA.================", String.valueOf(result.getFulfillment().getData()));
                final Metadata metadata = result.getMetadata();
                if (metadata != null) {
                    Log.e(TAG, "Intent id: " + metadata.getIntentId());
                    Log.e(TAG, "Intent name: " + metadata.getIntentName());
                }
                if (String.valueOf(result.getFulfillment().getSpeech()).equals(null)) {
                    loader.setVisibility(View.GONE);
                    mimicOtherMessage("Sorry I dont understand");
                } else {
                    loader.setVisibility(View.GONE);
                    mimicOtherMessage(String.valueOf(result.getFulfillment().getSpeech()));
                }

            }
        });
    }


    private void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                response = error.toString();
            }
        });
    }

    private void initService() {
        final AIConfiguration config = new AIConfiguration("f309930f65c241019bdc4cf1c8d0b8e2", AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);
        //final AIConfiguration config = new AIConfiguration("5ea4dc7084e54278972910d3a1677815", AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);
        aiDataService = new AIDataService(ChannelActivity.this, config);
    }


    @Override
    public void PowerFunctionItemListener(gettersetterpowerfunction item) {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        recyclerView.setVisibility(View.GONE);
        if (item.getTitle().endsWith(end_str)) {
            //  fetchSubjectFunctions(item.getTitle());
            fetchsubjecttopic(new SharedPreferencesData(ChannelActivity.this).getSubject_Id());
        }

        if (item.getTitle().equals(R.string.quiz)) {
            mEditTextMessage.setText(item.getTitle());
        }
        if (item.getTitle().equals(R.string.uni)) {
            Intent it = new Intent(ChannelActivity.this, UniversityList.class);
            startActivity(it);
        }
        if (item.getTitle().equals(R.string.me)) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent it = new Intent(ChannelActivity.this, UpdateProfile.class);
                            startActivity(it);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(ChannelActivity.this);
            builder.setMessage("Do you want to update your profile?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }

        if (item.getTitle().equals(R.string.mute)) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            Toast.makeText(ChannelActivity.this, "Notification mute", Toast.LENGTH_LONG).show();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(ChannelActivity.this);
            builder.setMessage("Are you sure you want to mute this notification?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }

        if (item.getTitle().equals(R.string.help)) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent it = new Intent(ChannelActivity.this, Help.class);
                            startActivity(it);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(ChannelActivity.this);
            builder.setMessage("Do you need help?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }

    @Override
    public void SubjectItemListener(gettersetterSubject item) {
        mEditTextMessage.setText(item.getTitle());
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        recyclerView1.setVisibility(View.GONE);
        saveSubject_Id(String.valueOf(item.getSubject_id()));
        // fetchUniversitydata(item.getTitle(), item.getSubject_id());

    }

    @Override
    public void TopicItemListener(gettersetterTopics item) {
        mEditTextMessage.setText(item.getTitle());
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        recyclerView2.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        saveTopic_Id(item.getTopic_id());
        mAdapter.clear();
        mimicOtherMessage("Quiz Started");
    }

    void fetchPowerFunctions() {
        RequestQueue requestQueue = Volley.newRequestQueue(ChannelActivity.this);
        // Initialize a new JsonObjectRequest instance
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                fetch_all_power_functions_url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            items = new ArrayList<>();
                            // Get the JSON array
                            JSONArray power_functionsArray = response.getJSONArray(power_functions_array);

                            // Loop through the array elements
                            for (int i = 0; i < power_functionsArray.length(); i++) {
                                // Get current json object
                                JSONObject power_functionsObj = power_functionsArray.getJSONObject(i);

                                // Get the current student (json object) data
                                String power_functionstr = power_functionsObj.getString(power_function);
                                items.add(new gettersetterpowerfunction(R.drawable.opetbot_icon, power_functionstr));

                            }

                            recyclerView.setVisibility(View.VISIBLE);
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            setAdapter();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something when error occurred
                        Toast.makeText(ChannelActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    /*Fetch bottombar subject topics*/


    void fetchsubjecttopic(final String subject_id) {
        RequestQueue requestQueue = Volley.newRequestQueue(ChannelActivity.this);
        // Initialize a new JsonObjectRequest instance
        Log.e("Topic url", fetch_subject_topic + subject_id);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                fetch_subject_topic + subject_id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            itemsTopics = new ArrayList<>();
                            // Get the JSON array
                            JSONArray TopicsArray = response.getJSONArray(quiz_subj_topics_array);

                            // Loop through the array elements
                            for (int i = 0; i < TopicsArray.length(); i++) {
                                // Get current json object
                                JSONObject topicsObj = TopicsArray.getJSONObject(i);
                                // Get the current student (json object) data
                                String topicsstr = topicsObj.getString(topic);
                                itemsTopics.add(new gettersetterTopics(R.drawable.opetbot_icon, topicsstr, topicsObj.getString(topic_id), subject_id));
                            }
                            recyclerView1.setVisibility(View.GONE);
                            recyclerView2.setVisibility(View.VISIBLE);
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                            setAdapter();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something when error occurred
                        Toast.makeText(ChannelActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    /*Saving subject id*/
    public void saveSubject_Id(String Subject_Id) {
        new SharedPreferencesData(ChannelActivity.this).saveSubject_Id(Subject_Id);
    }

    public void saveTopic_Id(String Topic_Id) {
        new SharedPreferencesData(ChannelActivity.this).saveTopic_Id(Topic_Id);
    }

    /*Right hamberger menu for revision notes and attempted questions*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, "");
        menu.add(0, 2, 2, menuIconWithText(getResources().getDrawable(R.drawable.question_bank), "Topics"));
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent it;
        switch (item.getItemId()) {

            case 2:
                it = new Intent(ChannelActivity.this, RevisionModule.class);
                startActivity(it);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, 70, 70);
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    void fetch_subject_chat_history() {
        ProgressDialog progressDialog = new ProgressDialog(ChannelActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        Log.e("Channel history", get_subject_chat_url + "/" + new SharedPreferencesData(ChannelActivity.this).getSubject_Id() + "/" + new SharedPreferencesData(ChannelActivity.this).getUser_Id());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_subject_chat_url + new SharedPreferencesData(ChannelActivity.this).getSubject_Id() + "/" + new SharedPreferencesData(ChannelActivity.this).getUser_Id(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response general chat history", response);

                        try {
                            JSONObject mainObj = new JSONObject(response);
                            JSONArray gc_history_array = mainObj.getJSONArray(sc_history);
                            for (int i = 0; i < gc_history_array.length(); i++) {
                                JSONObject history_obj = gc_history_array.getJSONObject(i);
                                sendMessage(history_obj.getString(search_key));
                                mimicOtherMessage(history_obj.getString(question_text));
                            }
                            intent=getIntent();
                            Bundle extras = intent.getExtras();
                            if(extras!=null) {
                                if (extras.containsKey("data")) {
                                    mimicOtherMessage(getIntent().getStringExtra("data").toString());
                                }
                            }
                        } catch (JSONException e) {
                            Toast.makeText(ChannelActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChannelActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("Content-Type", "application/json");
                Log.e("Data sent", params.toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ChannelActivity.this);
        requestQueue.add(stringRequest);

        progressDialog.hide();
    }


    /*Sending direct string question*/
    void sendStringData(final String skeyStr) {
        final ProgressDialog progressDialog = new ProgressDialog(ChannelActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        //Showing the progress dialog
        StringRequest stringRequest = new StringRequest(Request.Method.POST, fetch_direct_question_general_chat_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //   loading.dismiss();
                        Log.e("Response  direct question", response);
                        progressDialog.dismiss();
                        mimicOtherMessage("Here Are Some References");
                        saveDirectQuestionData(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChannelActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(skey_str, skeyStr);
                params.put("Content-Type", "application/json");
                Log.e("Data sent", params.toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ChannelActivity.this);
        requestQueue.add(stringRequest);
    }

    public void saveSearchKey(String SearchKey) {
        new SharedPreferencesData(ChannelActivity.this).saveSearchKey(SearchKey);
    }

    public void saveStatus(String Status) {
        new SharedPreferencesData(ChannelActivity.this).saveStatus(Status);
    }

    public void saveDirectQuestionData(String DirectQuestionData) {
        new SharedPreferencesData(ChannelActivity.this).saveDirectQuestionData(DirectQuestionData);
    }
}
