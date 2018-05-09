package com.opetbot.Fragments;

/**
 * Created by Ravi on 29/07/15.
 */

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
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
import com.opetbot.Adapter.ChatGeneralMessageAdapter;
import com.opetbot.ChannelActivity;
import com.opetbot.GetterSetter.ChatMessage;
import com.opetbot.GetterSetter.gettersetterSubject;
import com.opetbot.GetterSetter.gettersetterTopics;
import com.opetbot.GetterSetter.gettersetterpowerfunction;
import com.opetbot.Help;
import com.opetbot.MainActivity;
import com.opetbot.QuestionCameraScan;
import com.opetbot.R;
import com.opetbot.SharedPreferences.SharedPreferencesData;
import com.opetbot.UniversityList;
import com.opetbot.UpdateProfile;

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

//import static com.google.android.gms.internal.zzahn.runOnUiThread;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.opetbot.Config.ConfigUrl.answer;
import static com.opetbot.Config.ConfigUrl.fetch_all_power_functions_url;
import static com.opetbot.Config.ConfigUrl.fetch_all_subject_url;
import static com.opetbot.Config.ConfigUrl.fetch_direct_question_general_chat_url;
import static com.opetbot.Config.ConfigUrl.fetch_subject_topic;
import static com.opetbot.Config.ConfigUrl.gc_history;
import static com.opetbot.Config.ConfigUrl.get_general_chat_history_url;
import static com.opetbot.Config.ConfigUrl.power_function;
import static com.opetbot.Config.ConfigUrl.power_functions_array;
import static com.opetbot.Config.ConfigUrl.question;
import static com.opetbot.Config.ConfigUrl.quiz_subj_topics_array;
import static com.opetbot.Config.ConfigUrl.quiz_subjects_array;
import static com.opetbot.Config.ConfigUrl.send_general_chat_history_data;
import static com.opetbot.Config.ConfigUrl.skey_str;
import static com.opetbot.Config.ConfigUrl.subject;
import static com.opetbot.Config.ConfigUrl.subject_id;
import static com.opetbot.Config.ConfigUrl.topic;
import static com.opetbot.Config.ConfigUrl.topic_id;
import static com.opetbot.Config.ConfigUrl.user_id;

public class FragmentHome extends Fragment implements View.OnClickListener, BottomSheetPowerFunctionAdapter.PowerFunctionItemListener, BottomSheetSubjectAdapter.SubjectItemListener, BottomSheetTopicAdapter.TopicItemListener {

    private ListView mListView;
    private Button mButtonSend;
    private EditText mEditTextMessage;
    private ImageView mImageView, iv_image_tilde, scan_image;
    public Bot bot;
    public static Chat chat;
    public static ChatGeneralMessageAdapter mAdapter;
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
    pl.droidsonroids.gif.GifImageView loader;
    String end_str = "*";



    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        initView(rootView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));

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

        return rootView;
    }

    void initView(View view) {
        mListView = (ListView) view.findViewById(R.id.listView);
        mButtonSend = (Button) view.findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) view.findViewById(R.id.et_message);
        loader = (GifImageView) view.findViewById(R.id.loader);

        iv_image_tilde = (ImageView) view.findViewById(R.id.iv_image_tilde);
        scan_image = (ImageView) view.findViewById(R.id.scan_image);
        scan_image.setOnClickListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView1 = (RecyclerView) view.findViewById(R.id.recyclerView1);
        recyclerView2 = (RecyclerView) view.findViewById(R.id.recyclerView2);
        mAdapter = new ChatGeneralMessageAdapter(getContext(), new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);
        mButtonSend.setOnClickListener(this);
        iv_image_tilde.setOnClickListener(this);
        bottomSheet = view.findViewById(R.id.bottom_sheet);
        recyclerView.setVisibility(View.GONE);
        recyclerView1.setVisibility(View.GONE);
        recyclerView2.setVisibility(View.GONE);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        items = new ArrayList<>();
        itemsSubject = new ArrayList<>();
        itemsTopics = new ArrayList<>();
        fetch_general_chat_history();
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
        mListView.setSelection(mListView.getAdapter().getCount()-1);

        //sendNotification();
    }

    private void sendNotification() {
/*Defult notification*/
  /*      Notification.Builder builder = new Notification.Builder(getContext());
        Intent notificationIntent1 = new Intent(getContext(),MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0,notificationIntent1, 0);
        builder.setSmallIcon(R.drawable. opetbot_icon)
                .setContentTitle("Would you like to update your career preferences?")
                .addAction(android.R.drawable.ic_delete, "Yes", pendingIntent)
                .addAction(android.R.drawable.ic_delete, "No", pendingIntent)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getContext(). getSystemService(NOTIFICATION_SERVICE);
        Notification notification1 = builder.getNotification();
        notificationManager.notify(R.drawable.notification_template_icon_bg, notification1);*/
/*Custom*/

/*Career choices notification*/
       /* int icon1 = R.drawable.opetbot_icon;
        long when1 = System.currentTimeMillis();
        Notification notification1 = new Notification(icon1, "Custom Notification", when1);

        NotificationManager mNotificationManager1 = (NotificationManager)getContext().getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView1 = new RemoteViews(getContext().getPackageName(), R.layout.layout_notification1);
        contentView1.setImageViewResource(R.id.image, R.drawable.opetbot_icon);
        contentView1.setTextViewText(R.id.title, "Opet");
        contentView1.setTextViewText(R.id.text, "Hey Ami! Let's talk about you career interests?");
        notification1.contentView = contentView1;

        Intent notificationIntent1 = new Intent(getContext(), MainActivity.class);
        PendingIntent contentIntent1 = PendingIntent.getActivity(getContext(), 0, notificationIntent1, 0);
        notification1.contentIntent = contentIntent1;
        notification1.bigContentView = contentView1;
        notification1.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notification1.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        notification1.defaults |= Notification.DEFAULT_SOUND; // Sound
        mNotificationManager1.notify(1, notification1);
*/

        /*Score notification*/

       /* int icon2 = R.drawable.opetbot_icon;
        long when2 = System.currentTimeMillis();
        Notification notification2 = new Notification(icon2, "Custom Notification", when2);

        NotificationManager mNotificationManager2 = (NotificationManager)getContext().getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView2 = new RemoteViews(getContext().getPackageName(), R.layout.layout_notification2);
        contentView2.setImageViewResource(R.id.image, R.drawable.opetbot_icon);
        contentView2.setTextViewText(R.id.title, "Opet");
        contentView2.setTextViewText(R.id.text, "You scored a GPA of 2.8 in Maths last week! Congrats!\nLets chase Harvard with a 3.5!"+getEmojiByUnicode(0X1F60A));
        notification2.contentView = contentView2;

        Intent notificationIntent2 = new Intent(getContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, notificationIntent2, 0);
        notification2.contentIntent = contentIntent;
        notification2.bigContentView = contentView2;

        notification2.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notification2.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        notification2.defaults |= Notification.DEFAULT_SOUND; // Sound
        mNotificationManager2.notify(1, notification2);*/



      /*Notification3*/
        int icon2 = R.drawable.opetbot_icon;
        long when2 = System.currentTimeMillis();
        Notification notification2 = new Notification(icon2, "Custom Notification", when2);

        NotificationManager mNotificationManager2 = (NotificationManager)getContext().getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView2 = new RemoteViews(getContext().getPackageName(), R.layout.layout_notification2);
        contentView2.setImageViewResource(R.id.image, R.drawable.opetbot_icon);
        contentView2.setTextViewText(R.id.title, "Opet");
        contentView2.setTextViewText(R.id.text, "Only 2 quizzes left in this week!\nSolve one now!"+getEmojiByUnicode(	0X1F60A));
        notification2.contentView = contentView2;

        Intent notificationIntent2 = new Intent(getContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, notificationIntent2, 0);
        notification2.contentIntent = contentIntent;
        notification2.bigContentView = contentView2;

        notification2.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notification2.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        notification2.defaults |= Notification.DEFAULT_SOUND; // Sound
        mNotificationManager2.notify(1, notification2);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String message = mEditTextMessage.getText().toString();
                //bot
                response = chat.multisentenceRespond(mEditTextMessage.getText().toString());
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(getContext(), "Enter Message", Toast.LENGTH_LONG).show();
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
                    sendRequest(message);
                    sendMessage(message);
                    mEditTextMessage.setText("");
                    mListView.setSelection(mAdapter.getCount() - 1);
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }

                break;
            case R.id.iv_image_tilde:
                recyclerView.setVisibility(View.GONE);
                recyclerView1.setVisibility(View.GONE);
                recyclerView2.setVisibility(View.GONE);
                fetchPowerFunctions();
                break;
            case R.id.scan_image:
                Intent it = new Intent(getContext(), QuestionCameraScan.class);
                startActivity(it);
                break;
        }
    }

    public static void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        //mimicOtherMessage(message);
    }

    public static void mimicOtherMessage(String message) {
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
    private void sendRequest(final String message) {

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
                    onResult(response,message);
                } else {
                    onError(aiError);
                }
            }
        };
        task.execute(queryString, eventString, contextString);
    }

    private void onResult(final AIResponse response1,final String message) {
       getActivity(). runOnUiThread(new Runnable() {
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
                loader.setVisibility(View.GONE);
                mimicOtherMessage(String.valueOf(result.getFulfillment().getSpeech()));

                Log.e("DATA1.================", String.valueOf(result.getFulfillment().getSource()));
                Log.e("DATA2.================", String.valueOf(result.getFulfillment().getDisplayText()));
                Log.e("DATA3.================", String.valueOf(result.getFulfillment().getMessages().toString()));
                Log.e("DATA4.================", String.valueOf(result.getFulfillment().getSpeech()));


                final Metadata metadata = result.getMetadata();
                if (metadata != null) {
                    Log.e(TAG, "Result : " + result.getSource());
                    Log.e(TAG, "Intent name: " + metadata.getClass().getName());
                    Log.e(TAG, "Intent fulfillment: " + result.getFulfillment().getData());
                }
                String keyVal = null;
                final HashMap<String, JsonElement> params = result.getParameters();
                save_general_chat_history(message, result.getFulfillment().getSpeech());
            }
        });
    }


    private void onError(final AIError error) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                response = error.toString();
            }
        });
    }

    private void initService() {
        final AIConfiguration config = new AIConfiguration("f309930f65c241019bdc4cf1c8d0b8e2", AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);
        //  final AIConfiguration config = new AIConfiguration("5ea4dc7084e54278972910d3a1677815", AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);
        aiDataService = new AIDataService(getContext(), config);
    }


    @Override
    public void PowerFunctionItemListener(gettersetterpowerfunction item) {
        //
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        recyclerView.setVisibility(View.GONE);


        if (item.getTitle().equals("~quiz*")) {

                fetchSubjectFunctions(item.getTitle());

        }
        if (item.getTitle().equals("~uni")) {
            Intent it = new Intent(getContext(), UniversityList.class);
            startActivity(it);
        }

        if (item.getTitle().equals("~random")) {
            mimicOtherMessage("Here's some cool stuff for you I found on the internet");
        }
        if (item.getTitle().equals("~me")) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent it = new Intent(getContext(), UpdateProfile.class);
                            startActivity(it);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Do you want to update your profile?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }

        if (item.getTitle().equals("~mute")) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            Toast.makeText(getContext(), "Notification mute", Toast.LENGTH_LONG).show();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Are you sure you want to mute this notification?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }

        if (item.getTitle().equals("~help")) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent it = new Intent(getContext(), Help.class);
                            startActivity(it);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        fetchsubjecttopic(item.getTitle(), String.valueOf(item.getSubject_id()));
    }

    @Override
    public void TopicItemListener(gettersetterTopics item) {
        mEditTextMessage.setText(item.getTitle());
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        recyclerView2.setVisibility(View.GONE);
        saveTopic_Id(item.getTopic_id());
        Intent it = new Intent(getContext(), ChannelActivity.class);
        saveStatus("0");
        it.putExtra("data", "Quiz Started");
        startActivity(it);
        /*if(item.getTitle().endsWith(end_str)) {
            fetchsubjecttopic(item.getTitle(),item.getSubject_id());
        }*/
    }

    void fetchPowerFunctions() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        // Initialize a new JsonObjectRequest instance
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                fetch_all_power_functions_url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Get the JSON array
                            JSONArray power_functionsArray = response.getJSONArray(power_functions_array);
                            items = new ArrayList<>();
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
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    /*Fetch bottombar subject*/
    void fetchSubjectFunctions(final String power_function) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        // Initialize a new JsonObjectRequest instance
        Log.e("Subject url",fetch_all_subject_url + "/" + new SharedPreferencesData(getContext()).getUser_Id());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                fetch_all_subject_url + "/" + new SharedPreferencesData(getContext()).getUser_Id(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            itemsSubject = new ArrayList<>();
                            // Get the JSON array
                            JSONArray power_functionsArray = response.getJSONArray(quiz_subjects_array);

                            // Loop through the array elements
                            for (int i = 0; i < power_functionsArray.length(); i++) {
                                // Get current json object
                                JSONObject power_functionsObj = power_functionsArray.getJSONObject(i);
                                // Get the current student (json object) data
                                if (!power_functionsObj.getString(subject_id).equals("0")) {
                                    String power_functionstr = power_functionsObj.getString(subject);
                                    int subject_idstr = power_functionsObj.getInt(subject_id);
                                    itemsSubject.add(new gettersetterSubject(R.drawable.opetbot_icon, power_function + " " + power_functionstr, subject_idstr));
                                }
                            }
                            recyclerView.setVisibility(View.GONE);
                            recyclerView1.setVisibility(View.VISIBLE);
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
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }


    void fetchsubjecttopic(final String title, final String subject_id) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                                itemsTopics.add(new gettersetterTopics(R.drawable.opetbot_icon, title + topicsstr, topicsObj.getString(topic_id), subject_id));
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
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }


    void sendStringData(final String skeyStr) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
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
                        Intent it = new Intent(getContext(), ChannelActivity.class);
                        it.putExtra("data", "Here Are Some References");
                        saveDirectQuestionData(response);
                        startActivity(it);
                        //  mimicOtherMessage(String.valueOf(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    void fetch_general_chat_history() {
        ProgressDialog   progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_general_chat_history_url+new SharedPreferencesData(getContext()).getUser_Id(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response general chat history", response);

                        try {
                            JSONObject mainObj = new JSONObject(response);
                            JSONArray gc_history_array = mainObj.getJSONArray(gc_history);
                            for (int i = 0; i < gc_history_array.length(); i++) {
                                JSONObject history_obj = gc_history_array.getJSONObject(i);
                                sendMessage(history_obj.getString(question));
                                mimicOtherMessage(history_obj.getString(answer));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                      //notification1
               //    mimicOtherMessage("Hey Ami! Let's talk about you career interests?");
                        //notification2

                    /*   mimicOtherMessage("You scored a GPA of 2.8 in Maths last week! ");
                        mimicOtherMessage("Congratulations!"+getEmojiByUnicode(0X1F44F));
                        mimicOtherMessage("Lets chase Harvard with a 3.5!"+getEmojiByUnicode(0X1F603));
                        mimicOtherMessage("How about you finish 5 Maths quizzes this week and get better? ^_^");*/
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        progressDialog.hide();
    }
    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
    /*Saving subject id*/
    public void saveSubject_Id(String Subject_Id) {
        new SharedPreferencesData(getContext()).saveSubject_Id(Subject_Id);
    }

    public void saveTopic_Id(String Topic_Id) {
        new SharedPreferencesData(getContext()).saveTopic_Id(Topic_Id);
    }

    public void saveStatus(String Status) {
        new SharedPreferencesData(getContext()).saveStatus(Status);
    }

    public void saveDirectQuestionData(String DirectQuestionData) {
        new SharedPreferencesData(getContext()).saveDirectQuestionData(DirectQuestionData);
    }
    public void saveSearchKey(String SearchKey) {
        new SharedPreferencesData(getContext()).saveSearchKey(SearchKey);
    }
    void save_general_chat_history(final String questionstr, final String answerstr) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, send_general_chat_history_data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response general chat history", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(user_id, new SharedPreferencesData(getContext()).getUser_Id());
                params.put(question, questionstr);
                params.put(answer, answerstr);
                params.put("Content-Type", "application/json");
                Log.e("Data sent", params.toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}
