package com.bolofilipp.bathhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().hide(); // hide the title bar

        MyApplication myApp=(MyApplication)this.getApplication();
        myApp.initializeDb(this);

        View someView = findViewById(R.id.logoImage);
        View root = someView.getRootView();
        root.setBackgroundColor(getColor(R.color.colorBackground));

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                startApp();
            }
        }, 1000);
    }

    protected void startApp(){
        Bundle bundle = null;
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        bundle = options.toBundle();

        Intent intent = new Intent(this, ApplicationRuntime.class);
        intent.putExtra("parentId", 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (bundle == null) {
            startActivity(intent);
        } else {
            startActivity(intent, bundle);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finishAffinity();
            }
        }, 1000);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
}
