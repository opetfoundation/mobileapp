package com.opetbot.Config;

/**
 * Created by Softeligent on 16-01-18.
 */

public class ConfigUrl {
    /*main url*/
    public static String main_url = "https://opetbot-backend-dev.azurewebsites.net/index.php/";
    public static String imageaccesspath = "image_access_path";

    /*Login and registration fields and url*/
    public static String registration_url = main_url + "user/register";
    public static String login_url = main_url + "account/authenticate";
    public static String is_email_exists_url = main_url + "user/is_email_exist";
    public static String authenticate_or_register_url = main_url + "account/authenticate_or_register";

    public static String registration_fname = "first_name";
    public static String registration_lname = "last_name";
    public static String registration_email = "email";
    public static String registration_mobile = "mobile";
    public static String registration_password = "password";
    public static String registration_device_id = "device_id";
    public static String mobile_country_code="mobile_country_code";
public static String email_verification_url=main_url+"user/send_email_verificaton_code";
    /*Logout*/
    public static String user_logout_url = main_url + "account/logout";

    /*Fetching Power Functions*/
    public static String fetch_all_power_functions_url = main_url + "powerfunction/get_power_functions";
    public static String power_functions_array = "power_functions";
    public static String power_function = "power_function";
    public static String gc_response = "gc_response";
    public static String cc_response = "cc_response";

    /*Fetching Subject List*/
    public static String fetch_all_subject_url = main_url + "powerfunction/get_quiz_subjects";
    public static String quiz_subjects_array = "quiz_subjects";
    public static String subject_id = "subject_id";
    public static String subject = "subject";

    /*Fetching subject topics*/
    public static String fetch_subject_topic = main_url + "powerfunction/get_quiz_subject_topics/";
    public static String quiz_subj_topics_array = "quiz_subj_topics";
    public static String topic_id = "topic_id";
    public static String topic = "topic";


    /*Subjectwise Quiz*/
    public static String fetch_subjectwise_question_url = main_url + "quiz/get_subject_quiz/";
    public static String fetch_topicwise_question_url = main_url + "quiz/get_subject_topic_quiz/";
    public static String quiz_data = "quiz_data";
    public static String qa_id = "qa_id";
    public static String qt_id = "qt_id";
    public static String question_serial_heading = "question_serial_heading";
    public static String question_text = "question_text";
    public static String question_images = "question_images";
    public static String marks = "marks";
    public static String correct_option = "correct_option";
    public static String option_a_text = "option_a_text";
    public static String option_a_images = "option_a_images";
    public static String option_b_text = "option_b_text";
    public static String option_b_images = "option_b_images";
    public static String option_c_text = "option_c_text";
    public static String option_c_images = "option_c_images";
    public static String option_d_text = "option_d_text";
    public static String option_d_images = "option_d_images";
    public static String status = "status";

    /*Attempted Quiz Data*/
    public static String attempted_question_url = main_url + "quiz/attempted_quiz_qa/";
    public static String attempted_quiz_qa = "attempted_quiz_qa";

    /*Quiz Result*/
    public static String quiz_result_url = main_url + "quiz/save_quiz";
    public static String quiz_qa = "quiz_qa";
    public static String quiz_time = "quiz_time";
    public static String total_marks = "total_marks";
    public static String gained_marks = "gained_marks";
    public static String total_questions = "total_questions";
    public static String attempted_questions = "attempted_questions";
    public static String skipped_questions = "skipped_questions";
    public static String user_id = "user_id";
    public static String attempted_time = "attempted_time";

    /*Free Channel Listing*/
    public static String free_cannel_url = main_url + "plan/get_free_plans/";
    public static String paid_channel_url = main_url + "plan/get_subject_plans/";
    public static String subject_plans = "subject_plans";
    public static String free_plans = "free_plans";
    public static String plan_id = "plan_id";
    public static String duration = "duration";
    public static String plan_name = "plan_name";
    public static String total_price = "total_price";

    /*Free channel subscription*/
    public static String free_channel_subscription_url = main_url + "plan/subscribe_free_plan";

    /*search general qa*/
    public static String fetch_direct_question_general_chat_url = main_url + "qadata/search_general_qa";
    public static String skey_str = "search_key";

    /*University url*/
    public static String university_url = main_url + "universitydata/universities_ranking";
    public static String ranking_data = "ranking_data";
    public static String university_id = "university_id";
    public static String university_name = "university_name";
    public static String web_url = "web_url";
    public static String world_rank = "world_rank";
    public static String country_flag_img_url = "country_flag_img_url";
    public static String recommended_universities_url = main_url + "universitydata/recommended_universities";
    public static String universities = "universities";
    public static String country_flag = "country_flag";
    /*Random topics*/
    public static String random_topics_url = main_url + "generalchattopics/general_chat_topics_links";

    public static String topic_link_id = "topic_link_id";
    public static String vote = "vote";
    public static String upvote_downvote_random_topics_url = main_url + "generalchattopics/save_gctl_vote";
    /*Saving and sending general chat history*/
    public static String send_general_chat_history_data = main_url + "userchathistory/save_general_message";
    public static String question = "question";
    public static String answer = "answer";
    /*Fetching general chat history*/
    public static String get_general_chat_history_url = main_url + "userchathistory/general_chat_history/";
    public static String created_date = "created_date";
    public static String gc_history = "gc_history";

    /*Saving and sending subject chat history*/
    public static String send_subject_channel_url = main_url + "userchathistory/save_subject_message";
    public static String search_key = "search_key";
    public static String get_subject_chat_url = main_url + "userchathistory/subject_chat_history/";
    public static String sc_history = "sc_history";

    /*career interest tags*/
    public static String career_intrest_tag_url = main_url + "universitydata/career_interest_tags";
    public static String ci_tags = "ci_tags";
    public static String ci_tag_id = "ci_tag_id";
    public static String tag_name = "tag_name";

}
