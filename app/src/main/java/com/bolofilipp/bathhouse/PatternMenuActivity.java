package com.bolofilipp.bathhouse;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.util.ArrayList;

public class PatternMenuActivity extends AppCompatActivity {

    enum cols {
        ID,
        NAME,
        COMMENT,
        PARENT_ID,
        IMAGE,
        CONTENT
    }
    int m_currentId = -1;
    ArrayList<DBItem> m_itemsList = new ArrayList<>();
    DBItem m_currentItem = new DBItem();
    boolean IS_MENU = true;
    final int USER_ID = 6000;
    DataBaseHelper myDbHelper;
    final int ABOUT_ID = 114;
    MyApplication myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_menu);

        myApp = (MyApplication)this.getApplication();
        myDbHelper = myApp.db();

        Bundle bundle = getIntent().getExtras();
        m_currentId = bundle.getInt("parentId", -1);

        setContentView(R.layout.activity_pattern_menu);

        View someView = findViewById(R.id.imageView);
        View root = someView.getRootView();
        root.setBackgroundColor(getColor(R.color.colorBackground));

        AppRater.app_launched(this, myApp.stackSize());

        generateAds();
        getValues();
        fillWindow();
        amazingScroll();
    }
    protected void amazingScroll()
    {
        /* intially hide the view */
        //final TextView heading = findViewById(R.id.headerText);
        //heading.setText(m_currentItem.name);
        final ScrollView scrollView = findViewById(R.id.scrollView);
        final ImageView parallaxImage =  findViewById(R.id.imageView);

        // LinearLayout layout = (LinearLayout) findViewById(R.id.buttonsLayout);
        //ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
        //params.topMargin = parallaxImage.getHeight();


        //heading.setAlpha(0f);
        /* set the scroll change listener on scrollview */
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                /* get the maximum height which we have scroll before performing any action */
                int maxDistance = parallaxImage.getHeight();
                /* how much we have scrolled */
                int movement = scrollView.getScrollY();
                /*finally calculate the alpha factor and set on the view */
                //float alphaFactor = ((movement * 1.0f) / (maxDistance - heading.getHeight()));
                if (movement >= 0 && movement <= maxDistance) {
                    /*for image parallax with scroll */
                    parallaxImage.setTranslationY(-movement/2);
                    /* set visibility */
                    //heading.setAlpha(alphaFactor);
                }
            }
        });
    }

    public void addListenerOnPicture() {

        ImageView image = (ImageView) findViewById(R.id.imageView);

        image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onPictureClicked();
            }

        });

    }

    protected void onPictureClicked()
    {
        final Activity tmp = this;
        final MyApplication myApp = (MyApplication)this.getApplication();
        if(m_currentId == 0)
        {
            Bundle bundle = null;
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(tmp);
            bundle = options.toBundle();

            Intent intent = new Intent(getApplicationContext(), PatternMenuActivity.class);
            intent.putExtra("parentId", ABOUT_ID);
            myApp.pushStack(m_currentId);
            startActivity(intent,bundle);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 1000);
        }

        if(m_currentId == ABOUT_ID)
        {
            Bundle bundle = null;
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(tmp);
            bundle = options.toBundle();

            Intent intent = new Intent(getApplicationContext(), PatternMenuActivity.class);
            intent.putExtra("parentId", 0);
            myApp.pushStack(m_currentId);
            startActivity(intent,bundle);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 1000);
        }

    }

    protected void generateAds()
    {
        PublisherAdView mPublisherAdView = findViewById(R.id.publisherAdView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mPublisherAdView.loadAd(adRequest);
        mPublisherAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        LinearLayout ads = findViewById(R.id.adsLayout);
                        int adsHeight = ads.getHeight();
                        ScrollView sv = findViewById(R.id.scrollView);
                        sv.setPadding(0,0,0,adsHeight);
                    }
                }, 100);

            }
        });
    }
    protected RelativeLayout generateButton(DBItem item)
    {
        Button b = new Button(getApplicationContext());

        if(item.comment != null && !item.comment.isEmpty())
        b.setText(Html.fromHtml("<b><big>" + item.name + "</big></b>" +  "<br />" +
                "<small>" + item.comment + "</small>"));
        else
            b.setText(Html.fromHtml("<b><big>" + item.name + "</big></b>" ));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);

        b.setLayoutParams(params);
        b.setId(USER_ID + item.id);
        final String imageName = item.image;
        final Activity tmp = this;
        final MyApplication myApp = (MyApplication)this.getApplication();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Bundle bundle = null;
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(tmp);
                bundle = options.toBundle();

                Intent intent = new Intent(getApplicationContext(), PatternMenuActivity.class);
                intent.putExtra("parentId", v.getId() - USER_ID);
                myApp.pushStack(m_currentId);
                startActivity(intent,bundle);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 1000);*/

                int idd = v.getId();
                idd+=999;
                ImageView imView = tmp.findViewById(idd);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(tmp, imView, imageName);
                Bundle bundle = options.toBundle();

                Intent intent = new Intent(getApplicationContext(), PatternMenuActivity.class);
                intent.putExtra("parentId", v.getId() - USER_ID);
                myApp.pushStack(m_currentId);

                if (bundle == null) {
                    startActivity(intent);
                } else {
                    startActivity(intent, bundle);
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 1000);

            }
        });

        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius(30);

        shape.setColor(getColor(R.color.colorButton));

        shape.setAlpha(255);
        b.setPadding(20,20,20,20);
        b.setBackground(shape);
        b.setTextColor(Color.BLACK);
        b.setAllCaps(false);


        /*Drawable img = getDrawable(getResources().getIdentifier(item.image, "drawable", getPackageName()));
        img.setBounds( 0, 0, 320, 180 );
        b.setCompoundDrawables(img, null, null, null);*/

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(getResources().getIdentifier(item.image, "drawable", getPackageName()));

        RelativeLayout.LayoutParams lp_im = new RelativeLayout.LayoutParams(320,180);
        lp_im.addRule(RelativeLayout.ALIGN_LEFT, b.getId());
        //lp_im.addRule(RelativeLayout.ALIGN_TOP, b.getId());
        lp_im.addRule(RelativeLayout.CENTER_VERTICAL, b.getId());
        lp_im.setMargins(15,0,0,0);

        imageView.setLayoutParams(lp_im);
        imageView.setTransitionName(item.image);
        imageView.setAdjustViewBounds(true);
        int imageId = b.getId();
        imageId+=999;
        imageView.setId(imageId);

        RelativeLayout rl = new RelativeLayout(getApplicationContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        rl.setLayoutParams(lp);
        RelativeLayout.LayoutParams lp_tv = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        lp_tv.setMargins(10,10,10,10);
        b.setLayoutParams(lp_tv);
        b.setPadding(15 + 320,15,15,15);
        imageView.setElevation(99999);
        b.setMinHeight(200);
        rl.addView(b);
        rl.addView(imageView);
        return rl;
    }

    public void onBackPressed() {
       /* if (m_currentId == 0)
            return;
        else*/
       //super.onBackPressed();
        int id = myApp.popStack();
        if(id < 0)
            super.onBackPressed();
        else
        {
            Bundle bundle = null;
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            bundle = options.toBundle();

            Intent intent = new Intent(this, PatternMenuActivity.class);
            intent.putExtra("parentId", id);
            startActivity(intent, bundle);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 1000);
        }
    }

    protected void getValues()
    {
        m_itemsList = myApp.getDBValues(m_currentId);
        m_currentItem = myApp.getCurrentValue(m_currentId);
        IS_MENU = myApp.isItMenu(m_currentId);
    }

    protected void printImage()
    {
        ImageView imageView = ((ImageView)findViewById(R.id.imageView));
        if(m_currentItem.image != null && !m_currentItem.image.isEmpty())
        {
            Context context = imageView.getContext();
            int id = context.getResources().getIdentifier(m_currentItem.image, "drawable", context.getPackageName());
            imageView.setImageResource(id);
        }
        else if(m_currentId == 0 || m_currentId == ABOUT_ID)
        {
            imageView.setImageResource(R.drawable.main);

        }

        imageView.setTransitionName(m_currentItem.image);


    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        ImageView imageView = ((ImageView)findViewById(R.id.imageView));
        int ivHeight = imageView.getHeight();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, ivHeight, 0, 0);
        LinearLayout ll = findViewById(R.id.buttonsLayout);
        ll.setLayoutParams(layoutParams);

        View tmp = new View(this);
        tmp.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ivHeight));

        ll.addView(tmp);
    }

    protected void setBackGroundAndTitle() {
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            ((LinearLayout)findViewById(R.id.buttonsLayout)).setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.background) );
        } else {
            ((LinearLayout)findViewById(R.id.buttonsLayout)).setBackground(ContextCompat.getDrawable(this, R.drawable.background));
        }

        if(m_currentItem.id < 1) {
            setTitle(R.string.app_name);
        }
        else {
            setTitle(m_currentItem.name);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void printButtons()
    {
        for (DBItem item : m_itemsList) {

            RelativeLayout b = generateButton(item);
            ((LinearLayout)findViewById(R.id.buttonsLayout)).addView(b);
        }
    }

    protected void printArticle()
    {
        // заполняем текстом
        TextView tv = new TextView(this);
        if(m_currentItem.content == null || m_currentItem.content.isEmpty())
        {
            tv.setText("Тут пока нет статьи");
        }
        else
        {
            tv.setText(Html.fromHtml(m_currentItem.content));
        }
        tv.setTextSize(15);
        tv.setId(USER_ID+999);
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundResource(R.drawable.text_background);

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        llp.setMargins(10, 10, 10, 10); // llp.setMargins(left, top, right, bottom);
        tv.setLayoutParams(llp);
        tv.setTextColor(Color.BLACK);
        ((LinearLayout)findViewById(R.id.buttonsLayout)).addView(tv);
    }

    protected void printRateButton()
    {
        Button b = new Button(getApplicationContext());

        b.setText(Html.fromHtml("<b><big>Оценить приложение</big></b>"));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);

        b.setLayoutParams(params);
        b.setId(USER_ID);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });

        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius(50);

        shape.setColor(getColor(R.color.colorButton));

        shape.setAlpha(170);
        b.setPadding(40,40,40,40);
        b.setBackground(shape);
        b.setTextColor(Color.BLACK);
        b.setAllCaps(false);

        ((LinearLayout)findViewById(R.id.buttonsLayout)).addView(b);
    }

    protected void printRandomButtons()
    {
        //мутим случайные кнопки из категорий
        TextView interest = new TextView(this);
        interest.setText("Возможно, вам понравится: ");
        interest.setTextSize(18);
        interest.setTextColor(Color.BLACK);
        interest.setTypeface(interest.getTypeface(), Typeface.BOLD);
        interest.setId(USER_ID+1000);
        interest.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        interest.setGravity(View.TEXT_ALIGNMENT_CENTER);
        interest.setBackgroundColor(getColor(R.color.colorHeader));
        LinearLayout.LayoutParams paramsi = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        paramsi.setMargins(0,20,0,20);
        interest.setPadding(10,0,0,0);
        interest.setLayoutParams(paramsi);
        ((LinearLayout)findViewById(R.id.buttonsLayout)).addView(interest);

        ArrayList<Integer> used = new ArrayList<>();
        for(int i = 0; i < 3; i++)
        {
            DBItem item =  myApp.getRandomItem();
            while (item == null || used.contains(item.id))
                item =  myApp.getRandomItem();

            used.add(item.id);

            RelativeLayout b = generateButton(item);
            ((LinearLayout)findViewById(R.id.buttonsLayout)).addView(b);
        }
    }


    protected void fillWindow()
    {
        setBackGroundAndTitle();
        printImage();
        if(m_currentId == 0 || m_currentId == ABOUT_ID)
        {
            addListenerOnPicture();
            if(m_currentId == 0)
                getSupportActionBar().hide();
        }

        if(IS_MENU)
        {
            printButtons();
        }
        else
        {
            printArticle();
            if(m_currentId != ABOUT_ID)
                printRandomButtons();
            else
                printRateButton();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        if(m_currentId < 1)
        {
            MenuItem item = menu.findItem(R.id.home_button);
            item.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home_button:
                Bundle bundle = null;
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
                bundle = options.toBundle();

                Intent intent = new Intent(this, PatternMenuActivity.class);
                intent.putExtra("parentId", 0);
                MyApplication myApp = (MyApplication)this.getApplication();
                myApp.pushStack(m_currentId);
                startActivity(intent, bundle);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 1000);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
