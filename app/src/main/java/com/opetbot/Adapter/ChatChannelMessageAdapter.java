package com.opetbot.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.opetbot.GetterSetter.ChatMessage;
import com.opetbot.GetterSetter.gettersetterQuiz;
import com.opetbot.R;
import com.opetbot.SharedPreferences.SharedPreferencesData;
import com.opetbot.UniversityList;
import com.opetbot.Webservices.RequestQuizResult;
import com.opetbot.Webservices.RequestSubscribePaidChannelList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.api.android.AIDataService;
import pl.droidsonroids.gif.GifImageView;

import static android.util.JsonToken.NULL;
import static com.opetbot.ChannelActivity.iv_image_tilde;
import static com.opetbot.ChannelActivity.mButtonSend;
import static com.opetbot.ChannelActivity.mEditTextMessage;
import static com.opetbot.ChannelActivity.scan_image;
import static com.opetbot.Config.ConfigUrl.correct_option;
import static com.opetbot.Config.ConfigUrl.fetch_subjectwise_question_url;
import static com.opetbot.Config.ConfigUrl.fetch_topicwise_question_url;
import static com.opetbot.Config.ConfigUrl.imageaccesspath;
import static com.opetbot.Config.ConfigUrl.marks;
import static com.opetbot.Config.ConfigUrl.option_a_text;
import static com.opetbot.Config.ConfigUrl.option_b_text;
import static com.opetbot.Config.ConfigUrl.option_c_text;
import static com.opetbot.Config.ConfigUrl.option_d_text;
import static com.opetbot.Config.ConfigUrl.qa_id;
import static com.opetbot.Config.ConfigUrl.question_images;
import static com.opetbot.Config.ConfigUrl.question_text;
import static com.opetbot.Config.ConfigUrl.quiz_data;
import static com.opetbot.Config.ConfigUrl.subject_id;


public class ChatChannelMessageAdapter extends ArrayAdapter<ChatMessage> {
    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1, MY_IMAGE = 2, OTHER_IMAGE = 3;
    private AIDataService aiDataService;
    List<gettersetterQuiz> quizList;
    String user_id;
    String subject_id_int;
    String topic_id;
    QuizAdapter adapter;
    DirectQuetionAdapter adapterdirect;
    Button btn_end_quiz;

    public ChatChannelMessageAdapter(Context context, List<ChatMessage> data) {
        super(context, R.layout.item_mine_message, data);
        //  aiDataService = new AIDataService(this, config);
    }

    @Override
    public int getViewTypeCount() {
        // my message, other message, my image, other image
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = getItem(position);
        if (item.isMine() && !item.isImage()) return MY_MESSAGE;
        else if (!item.isMine() && !item.isImage()) return OTHER_MESSAGE;
        else if (item.isMine() && item.isImage()) return MY_IMAGE;
        else return OTHER_IMAGE;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        user_id = new SharedPreferencesData(getContext()).getUser_Id();
        subject_id_int = new SharedPreferencesData(getContext()).getSubject_Id();
        topic_id = new SharedPreferencesData(getContext()).getTopic_Id();
        if (viewType == MY_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_message, parent, false);
            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setText(getItem(position).getContent());
        } else if (viewType == OTHER_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_message, parent, false);
            TextView textView = (TextView) convertView.findViewById(R.id.text);
            RecyclerView recyclerView = (RecyclerView) convertView.findViewById(R.id.recyclerView);

            btn_end_quiz = (Button) convertView.findViewById(R.id.btn_end_quiz);
            quizList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            textView.setText(getItem(position).getContent());

            switch (getItem(position).getContent()) {
               /*Start quiz*/
                case "Quiz Started":
                    textView.setVisibility(View.VISIBLE);
                    mButtonSend.setClickable(false);
                    mEditTextMessage.setEnabled(false);
                    iv_image_tilde.setClickable(false);
                    scan_image.setClickable(false);
                    fetchTopicwiseQuiz(recyclerView);
                    break;

                    /*Direct Questions*/
                case "Here Are Some References":
                    textView.setVisibility(View.VISIBLE);
                    recyclerView = (RecyclerView) convertView.findViewById(R.id.recyclerView);
                    btn_end_quiz.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    mButtonSend.setClickable(true);
                    mEditTextMessage.setEnabled(true);
                    iv_image_tilde.setClickable(true);
                    scan_image.setClickable(true);

                    try {
                        JSONObject main_obj = new JSONObject(new SharedPreferencesData(getContext()).getDirectQuestionData());
                        JSONArray QuizArray = main_obj.getJSONArray("qa_data");
                        if (QuizArray.length() == 0) {
                            textView.setText("Sorry No Result Found");
                        } else {
                            directQuestion(new SharedPreferencesData(getContext()).getDirectQuestionData(), recyclerView);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                    }

                    break;

                    /*Default questions and answers*/
                default:
                    mButtonSend.setClickable(true);
                    mEditTextMessage.setEnabled(true);
                    iv_image_tilde.setClickable(true);
                    scan_image.setClickable(true);
                    recyclerView.setVisibility(View.GONE);
                    btn_end_quiz.setVisibility(View.GONE);
                    break;

            }


            /*When end quiz button click */
            btn_end_quiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    mButtonSend.setClickable(true);
                                    mEditTextMessage.setEnabled(true);
                                    iv_image_tilde.setClickable(true);
                                    scan_image.setClickable(true);
                                    try {
                                        JSONObject main_obj = new JSONObject();
                                        JSONArray quiz_array = new JSONArray();
                                        int total_marks = 0, gained_marks = 0, total_questions = 0, attempted_questions = 0, skipped_questions = 0;
                                        double attempted_time = 0.00;
                                        for (int i = 0; i < quizList.size(); i++) {
                                            try {
                                                JSONObject quiz_object = new JSONObject();
                                                quiz_object.put("qa_attempted_time", quizList.get(i).getPer_question_timer());
                                                quiz_object.put("qa_id", String.valueOf(quizList.get(i).getId()));
                                                quiz_object.put("is_attempted", String.valueOf(quizList.get(i).getIs_attempted()));
                                                quiz_object.put("is_correct", String.valueOf(quizList.get(i).getIs_correct()));
                                                quiz_array.put(quiz_object);
                                                attempted_time = attempted_time + Double.parseDouble(quizList.get(i).getPer_question_timer());
                                                total_marks = total_marks + quizList.get(i).getMark_per_question();
                                                if (quizList.get(i).getIs_attempted() == 1 && quizList.get(i).getIs_correct() == 1) {
                                                    gained_marks = gained_marks + quizList.get(i).getMark_per_question();
                                                }
                                                if (quizList.get(i).getIs_attempted() == 1) {
                                                    attempted_questions = attempted_questions + 1;
                                                }
                                                if (quizList.get(i).getIs_attempted() != 1) {
                                                    skipped_questions = skipped_questions + 1;
                                                }
                                            } catch (JSONException e) {
                                                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        total_questions = quizList.size();
                                        main_obj.put("quiz_qa", quiz_array);
                                        Log.e("JSONARRAY quiz", main_obj.toString());

                                        Log.e("total_marks", String.valueOf(total_marks));
                                        Log.e("gained_marks", String.valueOf(gained_marks));
                                        Log.e("total_questions", String.valueOf(total_questions));
                                        Log.e("attempted_questions", String.valueOf(attempted_questions));
                                        Log.e("skipped_questions", String.valueOf(skipped_questions));
                                        Log.e("user id", new SharedPreferencesData(getContext()).getUser_Id());
                                        Log.e("quiz_time", "15.00");
                                        Log.e("attempted_time", String.valueOf(attempted_time));
                                        Log.e("topic id", topic_id);
                                        new RequestQuizResult().postRequest(getContext(), main_obj.toString(), "15.00", String.valueOf(total_marks), String.valueOf(gained_marks), String.valueOf(total_questions), String.valueOf(attempted_questions), String.valueOf(attempted_time), String.valueOf(skipped_questions), new SharedPreferencesData(getContext()).getUser_Id(), topic_id);

                                        btn_end_quiz.setVisibility(View.GONE);
                                    } catch (JSONException e) {
                                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                                    }
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    mButtonSend.setClickable(false);
                                    mEditTextMessage.setEnabled(false);
                                    iv_image_tilde.setClickable(false);
                                    scan_image.setClickable(false);
                                    Toast.makeText(getContext(), "Still you are in quiz", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you sure you want end quiz?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });


        } else if (viewType == MY_IMAGE) {
            //convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_image, parent, false);
        } else {
            // convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_image, parent, false);
        }

        convertView.findViewById(R.id.chatMessageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    Toast.makeText(getContext(), "onClick", Toast.LENGTH_LONG).show();
            }
        });


        return convertView;
    }

    void fetchTopicwiseQuiz(final RecyclerView recyclerView) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        // Initialize a new JsonObjectRequest instance
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        quizList = new ArrayList<>();
        Log.e("quiz subject url", fetch_topicwise_question_url + subject_id_int + "/" + topic_id + "/" + user_id);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                fetch_topicwise_question_url + subject_id_int + "/" + topic_id + "/" + user_id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray QuizArray = response.getJSONArray(quiz_data);
                            // Loop through the array elements
                            for (int i = 0; i < QuizArray.length(); i++) {
                                JSONObject topicsObj = QuizArray.getJSONObject(i);
                                quizList.add(
                                        new gettersetterQuiz(
                                                topicsObj.getInt(qa_id),
                                                topicsObj.getString(question_text),
                                                topicsObj.getString(option_a_text),
                                                topicsObj.getString(option_b_text),
                                                topicsObj.getString(option_c_text),
                                                topicsObj.getString(option_d_text),
                                                "",
                                                "0.00",
                                                0, 0, topicsObj.getString(correct_option),
                                                topicsObj.getInt(marks),
                                                response.getString(imageaccesspath) + "0026b704-5b71-4057-93a3-0b2138c31b80",
                                                topicsObj.getString(subject_id)
                                        ));
                                quizList.get(i).setStatus(true);
                            }

                            // Save state
                            Parcelable recyclerViewState;
                            mButtonSend.setClickable(false);
                            mEditTextMessage.setEnabled(false);
                            iv_image_tilde.setClickable(false);
                            adapter = new QuizAdapter(getContext(), quizList);
                            recyclerView.setAdapter(adapter);
                            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                            adapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something when error occurred
                        new RequestSubscribePaidChannelList().postRequest(getContext(), subject_id);
                        //     Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }


    /*Direct question scan*/
    void directQuestion(String response, RecyclerView recyclerView) {

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        quizList = new ArrayList<>();
        try {
            JSONObject main_obj = new JSONObject(response);
            JSONArray QuizArray = main_obj.getJSONArray("qa_data");
            Log.e("Subject id", QuizArray.getJSONObject(0).getString(subject_id));
            if (!QuizArray.getJSONObject(0).getString(subject_id).equals(subject_id_int)) {
                new RequestSubscribePaidChannelList().postRequest(getContext(), subject_id_int);
            } else {
                // Loop through the array elements
                for (int i = 0; i < QuizArray.length(); i++) {
                    JSONObject topicsObj = QuizArray.getJSONObject(i);

                    quizList.add(
                            new gettersetterQuiz(
                                    topicsObj.getInt(qa_id),
                                    topicsObj.getString(question_text),
                                    topicsObj.getString(option_a_text),
                                    topicsObj.getString(option_b_text),
                                    topicsObj.getString(option_c_text),
                                    topicsObj.getString(option_d_text),
                                    "",
                                    "0.00",
                                    0, 0, topicsObj.getString(correct_option),
                                    topicsObj.getInt(marks), main_obj.getString(imageaccesspath) + topicsObj.getString(question_images),
                                    topicsObj.getString(subject_id)

                            ));
                    quizList.get(i).setStatus(true);
                    quizList.get(i).setSearch_key(new SharedPreferencesData(getContext()).getSearchKey());
                }

                progressDialog.dismiss();
                adapterdirect = new DirectQuetionAdapter(getContext(), quizList);
                recyclerView.setAdapter(adapterdirect);
                adapterdirect.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
