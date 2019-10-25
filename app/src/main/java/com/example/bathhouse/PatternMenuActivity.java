package com.example.bathhouse;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_menu);

        MyApplication myApp = (MyApplication)this.getApplication();
        myDbHelper = myApp.db();

        Bundle bundle = getIntent().getExtras();
        m_currentId = bundle.getInt("parentId", -1);

        setContentView(R.layout.activity_pattern_menu);

        View someView = findViewById(R.id.imageView);
        View root = someView.getRootView();
        root.setBackgroundColor(getColor(R.color.colorBackground));

        getValues();
        fillWindow();
    }

    protected Button generateButton(DBItem item)
    {
        Button b = new Button(getApplicationContext());
        b.setText(Html.fromHtml("<b><big>" + item.name + "</big></b>" +  "<br />" +
                "<small>" + item.comment + "</small>"));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);

        b.setLayoutParams(params);
        b.setId(USER_ID + item.id);
        final Activity tmp = this;
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = null;
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(tmp);
                bundle = options.toBundle();

                Intent intent = new Intent(getApplicationContext(), PatternMenuActivity.class);
                intent.putExtra("parentId", v.getId() - USER_ID);
                startActivity(intent,bundle);
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
        return b;
    }

    public void onBackPressed() {
        if (m_currentId == 0) {
            return;
        } else {
            super.onBackPressed();
        }
    }

    protected void getValues()
    {
        Cursor data = myDbHelper.getDBValues(m_currentId);
        if (data.moveToFirst()) {
            while ( !data.isAfterLast() ) {
                DBItem item = new DBItem();
                item.id = data.getInt(cols.ID.ordinal());
                item.name = data.getString(cols.NAME.ordinal());
                item.comment = data.getString(cols.COMMENT.ordinal());
                item.parentId = data.getInt(cols.PARENT_ID.ordinal());
                item.image = data.getString(cols.IMAGE.ordinal());
                item.content = data.getString(cols.CONTENT.ordinal());
                m_itemsList.add(item);
                data.moveToNext();
            }
        }

        Cursor currentData = myDbHelper.getCurrentValue(m_currentId);
        if (currentData.moveToFirst()) {
            while ( !currentData.isAfterLast() ) {
                DBItem item = new DBItem();
                item.id = currentData.getInt(cols.ID.ordinal());
                item.name = currentData.getString(cols.NAME.ordinal());
                item.comment = currentData.getString(cols.COMMENT.ordinal());
                item.parentId = currentData.getInt(cols.PARENT_ID.ordinal());
                item.image = currentData.getString(cols.IMAGE.ordinal());
                item.content = currentData.getString(cols.CONTENT.ordinal());
                m_currentItem = item;
                currentData.moveToNext();
            }
        }
        IS_MENU = myDbHelper.isItMenu(m_currentId);
    }

    protected void printImage()
    {
        if(m_currentItem.image != null && !m_currentItem.image.isEmpty())
        {
            ImageView imageView = ((ImageView)findViewById(R.id.imageView));
            Context context = imageView.getContext();
            int id = context.getResources().getIdentifier(m_currentItem.image, "drawable", context.getPackageName());
            imageView.setImageResource(id);
        }
        else if(m_currentId == 0)
        {
            ImageView iv = (ImageView)findViewById(R.id.imageView);

            iv.setImageResource(R.drawable.main);
            iv.setMaxHeight(500);
            getSupportActionBar().hide();
        }
    }

    protected void setBackGroundAndTitle() {
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            ((LinearLayout)findViewById(R.id.linearLayout)).setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.background) );
        } else {
            ((LinearLayout)findViewById(R.id.linearLayout)).setBackground(ContextCompat.getDrawable(this, R.drawable.background));
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

            Button b = generateButton(item);
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
        llp.setMargins(10, 20, 10, 10); // llp.setMargins(left, top, right, bottom);
        tv.setLayoutParams(llp);
        tv.setTextColor(Color.BLACK);
        ((LinearLayout)findViewById(R.id.buttonsLayout)).addView(tv);
    }

    protected void printRandomButtons()
    {
        //мутим случайные кнопки из категорий
        TextView interest = new TextView(this);
        interest.setText("Возможно вам будет интересно: ");
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
            DBItem item =  myDbHelper.getRandomItem();
            while (item == null || used.contains(item.id))
                item =  myDbHelper.getRandomItem();

            used.add(item.id);

            Button b = generateButton(item);
            ((LinearLayout)findViewById(R.id.buttonsLayout)).addView(b);
        }
    }


    protected void fillWindow()
    {
        setBackGroundAndTitle();
        printImage();

        if(IS_MENU)
        {
            printButtons();
        }
        else
        {
            printArticle();
            printRandomButtons();
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
                startActivity(intent, bundle);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
