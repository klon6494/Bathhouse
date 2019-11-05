package com.bolofilipp.bathhouse;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

public class AppRater {
    private final static String APP_TITLE = "Бани и сауны: руководство";// App Name
    private final static String APP_PNAME = "com.bolofilipp.bathhouse";// Package Name

    private final static int DAYS_UNTIL_PROMPT = 3;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 3;//Min number of launches
    private static int MIN_STACK_SIZE = 3;//Min number of click cathegories

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
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
// Add the buttons
        builder.setPositiveButton("  Оценить  ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    mContext.startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
                }

                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }

                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Позже", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MIN_STACK_SIZE += 10;
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
// Set other dialog properties
        TextView textView = new TextView(mContext);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(22);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setPadding(10,10,10,10);
        textView.setText(R.string.goToStore);
        builder.setCustomTitle(textView);
// Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button negativeButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                Button positiveButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                Button neutralButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEUTRAL);
                // this not working because multiplying white background (e.g. Holo Light) has no effect
                negativeButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
                positiveButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
                neutralButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);

                negativeButton.setBackgroundColor(Color.WHITE);
                positiveButton.setBackgroundColor(Color.WHITE);
                neutralButton.setBackgroundColor(Color.WHITE);

                GradientDrawable shape =  new GradientDrawable();
                shape.setCornerRadius(30);
                shape.setColor(mContext.getColor(R.color.colorButton));
                shape.setAlpha(150);

                LinearLayout.LayoutParams paramsi = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsi.setMargins(5,0,5,0);

                neutralButton.setLayoutParams(paramsi);
                neutralButton.setPadding(0,0,0,0);
                neutralButton.setBackground(shape);
                neutralButton.setTextColor(Color.BLACK);

                negativeButton.setLayoutParams(paramsi);
                negativeButton.setPadding(0,0,0,0);
                negativeButton.setBackground(shape);
                negativeButton.setTextColor(Color.BLACK);

                positiveButton.setLayoutParams(paramsi);
                positiveButton.setPadding(0,0,0,0);
                positiveButton.setBackground(shape);
                positiveButton.setTextColor(Color.BLACK);

                negativeButton.invalidate();
                positiveButton.invalidate();
                neutralButton.invalidate();
            }
        });

        dialog.show();
    }
}