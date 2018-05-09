package com.opetbot.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Softeligent on 27-10-17.
 */
/*Class to store registration throughout application*/
public class SharedPreferencesData {
    Context context;

    public SharedPreferencesData(Context context) {
        this.context = context;
    }

    /*save login details*/
    public void saveLoginDetails(String member_id, String cust_fname) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Opetbot", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("User_Id", member_id);
        editor.putString("cust_fname", cust_fname);
        editor.commit();
    }

    public String getCust_name() {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", MODE_PRIVATE);
        return sharedPreferences1.getString("cust_fname", "");
    }
    public boolean isUserLogedOut() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Opetbot", Context.MODE_PRIVATE);
        boolean isMember_idEmpty = sharedPreferences.getString("User_Id", "").isEmpty();
        boolean isCust_fnameEmpty = sharedPreferences.getString("cust_fname", "").isEmpty();
        return isMember_idEmpty || isCust_fnameEmpty ;
    }


    public void removeLoginDetails() {
        SharedPreferences preferences = context.getSharedPreferences("Opetbot", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Opetbot", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("device_token", token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Opetbot", Context.MODE_PRIVATE);
        return  sharedPreferences.getString("device_token", null);
    }


    /*Checkout status*/
    public void saveCheckoutStatus(String CheckoutStatus) {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", 0);
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        editor.putString("CheckoutStatus", CheckoutStatus);
        editor.commit();
    }

    public String getCheckoutStatus() {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", MODE_PRIVATE);
        return sharedPreferences1.getString("CheckoutStatus", "");
    }

    /*Save User Id*/
    public void saveUser_Id(String User_Id) {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", 0);
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        editor.putString("User_Id", User_Id);
        editor.commit();
    }

    public String getUser_Id() {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", MODE_PRIVATE);
        return sharedPreferences1.getString("User_Id", "");
    }

    /*Saving channels*/
    public void saveChannels(String channels) {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", 0);
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        editor.putString("Channels", channels);
        editor.commit();
    }

    public String getChannels() {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", MODE_PRIVATE);
        return sharedPreferences1.getString("Channels", "");
    }

    /*Saving Subject Id*/
    public void saveSubject_Id(String Subject_Id) {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", 0);
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        editor.putString("Subject_Id", Subject_Id);
        editor.commit();
    }

    public String getSubject_Id() {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", MODE_PRIVATE);
        return sharedPreferences1.getString("Subject_Id", "");
    }

    /*Saving Topic Id*/
    public void saveTopic_Id(String Topic_Id) {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", 0);
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        editor.putString("Topic_Id", Topic_Id);
        editor.commit();
    }

    public String getTopic_Id() {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", MODE_PRIVATE);
        return sharedPreferences1.getString("Topic_Id", "");
    }
    public void saveStatus(String Status) {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", 0);
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        editor.putString("Status", Status);
        editor.commit();
    }

    public String getStatus() {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", MODE_PRIVATE);
        return sharedPreferences1.getString("Status", "");
    }

    public void saveDirectQuestionData(String DirectQuestionData) {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", 0);
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        editor.putString("DirectQuestionData", DirectQuestionData);
        editor.commit();
    }

    public String getDirectQuestionData() {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", MODE_PRIVATE);
        return sharedPreferences1.getString("DirectQuestionData", "");
    }

    public void saveSearchKey(String SearchKey) {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", 0);
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        editor.putString("SearchKey", SearchKey);
        editor.commit();
    }

    public String getSearchKey() {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("Opetbot", MODE_PRIVATE);
        return sharedPreferences1.getString("SearchKey", "");
    }

}
