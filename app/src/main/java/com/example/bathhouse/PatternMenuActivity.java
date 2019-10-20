package com.example.bathhouse;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
    DataBaseHelper myDbHelper = new DataBaseHelper(this);
    int COLOR_BUTTON = 0xFF5F9EA0;
    int COLOR_BACKGROUND = 0xFFADD8E6;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_menu);

        Bundle bundle = getIntent().getExtras();
        m_currentId = bundle.getInt("parentId", -1);

        setContentView(R.layout.activity_pattern_menu);

        // Now get a handle to any View contained
        // within the main layout you are using
        View someView = findViewById(R.id.imageView);

        // Find the root view
        View root = someView.getRootView();

        // Set the color
        root.setBackgroundColor(COLOR_BACKGROUND);

        getValues();
        fillWindow();
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
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }

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

    protected void fillWindow()
    {
        if(m_currentItem.id < 1) {
            setTitle("Название прилки");
            findViewById(R.id.home_button);
        }
        else {
            setTitle(m_currentItem.name);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        //Рисуем картинку сверху
        if(m_currentItem.image != null && !m_currentItem.image.isEmpty())
        {
            ImageView imageView = ((ImageView)findViewById(R.id.imageView));
            Context context = imageView.getContext();
            int id = context.getResources().getIdentifier(m_currentItem.image, "drawable", context.getPackageName());
            imageView.setImageResource(id);
        }
        else if(m_currentId == 0)
        {
            ((ImageView)findViewById(R.id.imageView)).setImageResource(R.drawable.main);
        }

        if(IS_MENU) //Два видо окон: меню и статьи
        {
            //Создаем кнопки
            int countID = 0;

            for (DBItem item : m_itemsList) {
                Button b = new Button(getApplicationContext());
                b.setText(Html.fromHtml("<br><b><big>" + item.name + "</big></b>" +  "<br />" +
                        "<small>" + item.comment + "</small>" + "<br />"));
                b.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT)
                );
                b.setId(USER_ID + item.id);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), PatternMenuActivity.class);
                        intent.putExtra("parentId", v.getId() - USER_ID);
                        startActivity(intent);
                    }
                });

                b.getBackground().setColorFilter(COLOR_BUTTON, PorterDuff.Mode.MULTIPLY);

                ((LinearLayout)findViewById(R.id.buttonsLayout)).addView(b);
                countID++;
            }
        }
        else //если статья
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
            tv.setTextSize(20);
            tv.setId(USER_ID+999);
            tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            tv.setGravity(View.TEXT_ALIGNMENT_CENTER);
            ((LinearLayout)findViewById(R.id.buttonsLayout)).addView(tv);


            //мутим случайные кнопки из категорий
            TextView interest = new TextView(this);
            interest.setText("\n Возможно вам будет интересно: ");
            interest.setTextSize(20);
            interest.setTypeface(interest.getTypeface(), Typeface.BOLD);
            interest.setId(USER_ID+1000);
            interest.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            interest.setGravity(View.TEXT_ALIGNMENT_CENTER);
            ((LinearLayout)findViewById(R.id.buttonsLayout)).addView(interest);

            for(int i = 0; i < 3; i++)
            {
                DBItem item =  myDbHelper.getRandomItem();
                if(item == null)
                    continue;
                Button b = new Button(getApplicationContext());
                b.setText(Html.fromHtml("<br><b><big>" + item.name + "</big></b>" +  "<br />" +
                        "<small>" + item.comment + "</small>" + "<br />"));
                b.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT)
                );
                b.setId(USER_ID + item.id);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), PatternMenuActivity.class);
                        intent.putExtra("parentId", v.getId() - USER_ID);
                        startActivity(intent);
                    }
                });
                b.getBackground().setColorFilter(COLOR_BUTTON, PorterDuff.Mode.MULTIPLY);
                ((LinearLayout)findViewById(R.id.buttonsLayout)).addView(b);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home_button:
                Intent intent = new Intent(this, PatternMenuActivity.class);
                intent.putExtra("parentId", 0);
                startActivity(intent);
                return true;
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
