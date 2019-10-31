package com.bolofilipp.bathhouse;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppRater {
    private final static String APP_TITLE = "Бани и сауны: руководство";// App Name
    private final static String APP_PNAME = "com.bolofilipp.bathhouse";// Package Name

    private final static int DAYS_UNTIL_PROMPT = 0;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 0;//Min number of launches
    private final static int MIN_STACK_SIZE = 3;//Min number of launches

    public static void app_launched(Context mContext, int stackSize) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                if(stackSize >= MIN_STACK_SIZE)
                    showRateDialog(mContext, editor);
            }
        }

        editor.apply();
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Оцените - " + APP_TITLE);

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(mContext);
        tv.setText(R.string.goToStore);
        tv.setWidth(700);
        tv.setPadding(4, 0, 4, 10);
        tv.setTextSize(20);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        ll.addView(tv);

        LinearLayout llh = new LinearLayout(mContext);
        llh.setOrientation(LinearLayout.HORIZONTAL);

        Button b3 = new Button(mContext);
        b3.setText("Нет");
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
        llh.addView(b3);

        Button b2 = new Button(mContext);
        b2.setText("Позже");
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        llh.addView(b2);

        Button b1 = new Button(mContext);
        b1.setText("Оценить");
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                dialog.dismiss();
            }
        });
        llh.addView(b1);


        ll.addView(llh);


        dialog.setContentView(ll);
        dialog.show();
    }
}