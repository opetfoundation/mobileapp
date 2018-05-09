package com.opetbot.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;

import com.opetbot.R;


/**
 * Created by softeligent on 3/10/2017.
 */

public class NoInternetClass extends Activity  {

    public static Activity activity;
    private Button button1,button2;
   public NoInternetClass(Activity activity)
 {
  this.activity = activity;

 }

    public  void showCustomDialog(String message, final boolean finish) {
        final Dialog dialog = new Dialog(activity);

        dialog.setContentView(R.layout.nointernetdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        button1=(Button)dialog.findViewById(R.id.Button01);
        button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

            }
        });
        button2=(Button)dialog.findViewById(R.id.Button02);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                Intent intent = activity.getIntent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);

            }
        });

        dialog.show();

    }


}
