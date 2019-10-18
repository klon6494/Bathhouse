package com.example.bathhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, PatternMenuActivity.class);
        intent.putExtra("parentId", 0);
        startActivity(intent);

 /*       ((Button)findViewById(R.id.button)).setText(Html.fromHtml("<br><b><big>" + "Виды бань и саун" + "</big></b>" +  "<br />" +
                "<small>" + "Все о русской, финской, турецкой и других банях и саунах" + "</small>" + "<br />"));
        //((Button)findViewById(R.id.button)).setBackgroundResource(R.drawable.main);
        ((Button)findViewById(R.id.button1)).setText(Html.fromHtml("<br><b><big>" + "Банные принадлежности и аксессуары" + "</big></b>" +  "<br />" +
                "<small>" + "Все о вениках, хуениках и прочих прекрасных приблудах" + "</small>" + "<br />"));
        ((Button)findViewById(R.id.button2)).setText(Html.fromHtml("<br><b><big>" + "Баня и здоровье" + "</big></b>" +  "<br />" +
                "<small>" + "Как и на что влияет процедура принятия бани или сауны" + "</small>" + "<br />"));
        ((Button)findViewById(R.id.button3)).setText(Html.fromHtml("<br><b><big>" + "Часто задаваемые вопросы" + "</big></b>" +  "<br />" +
                "<small>" + "Если у тебя остались вопросы - ты найдешь тут ответы на них" + "</small>" + "<br />"));
        ((Button)findViewById(R.id.button4)).setText(Html.fromHtml("<br><b><big>" + "Разное о банях и саунах" + "</big></b>" +  "<br />" +
                "<small>" + "Всякая дрибидень которая не вошла в остальные разделы" + "</small>" + "<br />"));
   */
    }
}
