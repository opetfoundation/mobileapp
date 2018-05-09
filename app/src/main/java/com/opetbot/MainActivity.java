package com.opetbot;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.opetbot.Adapter.ChannelAdapter;
import com.opetbot.Fragments.FragmentHome;
import com.opetbot.GetterSetter.gettersetterSubject;
import com.opetbot.SharedPreferences.SharedPreferencesData;
import com.opetbot.Webservices.RequestFreeChannelList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private ResideMenu resideMenu;
    private MainActivity mContext;
    String titles[];
    ResideMenuItem item[];
    Button title_bar_right_menu;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title_bar_right_menu = (Button) findViewById(R.id.title_bar_right_menu);

        title_bar_right_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.item_right_menu_general_chat);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.TOP | Gravity.RIGHT;

                wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);
                dialog.show();
            }
        });
        mContext = this;
        setUpMenu();
        changeFragment(new FragmentHome());

    }


    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.left_hamberger);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        resideMenu.setScaleValue(0.6f);
        resideMenu.setOnClickListener(this);
        resideMenu.addProfileName(new SharedPreferencesData(MainActivity.this).getCust_name());
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
        // create menu items;
        titles = new String[]{ "Search", "Share App",  "Rate App", "Logout"};
        int icon[] = { R.drawable.search,  R.drawable.share, R.drawable.rating, R.drawable.logout};

        item = new ResideMenuItem[titles.length ];

        for (int i = 0; i < titles.length; i++) {
            item[i] = new ResideMenuItem(this, icon[i], titles[i], 0);
            item[i].setOnClickListener(this);
            resideMenu.addMenuItem(item[i], ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
        }
        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });

        // You can disable a direction by setting ->
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {
        if (view == item[0]) {
            Intent it = new Intent(MainActivity.this, SearchingActivity.class);
            startActivity(it);
        } else if (view == item[1]) {
            openFeedback(this, "info@opetbot.com");
        } else if (view == item[2]) {
            try {
                Uri uri = Uri.parse("market://details?id=" + getPackageName() + "");
                Intent goMarket = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(goMarket);
            } catch (ActivityNotFoundException e) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName() + "");
                Intent goMarket = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(goMarket);
            }
        } else if (view == item[3]) {
            if (new SharedPreferencesData(MainActivity.this).isUserLogedOut()) {
                Intent it = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(it);
            } else {
                new SharedPreferencesData(getBaseContext()).removeLoginDetails();
                Intent it = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(it);
                finish();
            }
        }
        resideMenu.closeMenu();
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            //     Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
            //   Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };


    private void changeFragment(Fragment targetFragment) {
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }


    public static void openFeedback(Context paramContext, String email) {
        Intent localIntent = new Intent(Intent.ACTION_SEND);
        localIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        localIntent.putExtra(Intent.EXTRA_CC, "");
        String str = null;
        try {
            str = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionName;
            localIntent.putExtra(Intent.EXTRA_SUBJECT, "Enquiry");
            localIntent.setType("message/rfc822");
            paramContext.startActivity(Intent.createChooser(localIntent, "Choose an Email client :"));
        } catch (Exception e) {
            Log.d("OpenFeedback", e.getMessage());
        }
    }


    /*Saving subject id*/
    public void saveSubject_Id(String Subject_Id) {
        new SharedPreferencesData(MainActivity.this).saveSubject_Id(Subject_Id);
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
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select action");
        menu.add(0, 1, 0, "Action1");
        menu.add(0, 2, 0, "Action2");

        super.onCreateContextMenu(menu, v, menuInfo);
    }


}