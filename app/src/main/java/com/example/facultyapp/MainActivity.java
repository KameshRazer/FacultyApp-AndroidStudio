package com.example.facultyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageView bgapp, clover,markEntry;
    LinearLayout textsplash, texthome, menus;
    Animation frombottom;
    SharedPreferences logInfo;
    String name,department;
    TextView TV_name1,TV_name2,TV_department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);

        markEntry = findViewById(R.id.markEntry);
        bgapp = (ImageView) findViewById(R.id.bgapp);
        clover = (ImageView) findViewById(R.id.clover);
        textsplash = (LinearLayout) findViewById(R.id.textsplash);
        texthome = (LinearLayout) findViewById(R.id.texthome);
        menus = (LinearLayout) findViewById(R.id.menus);

        TV_name1 = findViewById(R.id.name1);
        TV_name2 = findViewById(R.id.name2);
        TV_department = findViewById(R.id.department);

        logInfo = getSharedPreferences("FacultyLoginInfo",MODE_PRIVATE);
        name = logInfo.getString("name","Error");
        department = logInfo.getString("department","Error");

        TV_name1.setText(name);
        TV_name2.setText(name);
        TV_department.setText(department);

        bgapp.animate().translationY(-1900).setDuration(800).setStartDelay(300);
        clover.animate().alpha(0).setDuration(800).setStartDelay(600);
        textsplash.animate().translationY(140).alpha(0).setDuration(800).setStartDelay(300);

        texthome.startAnimation(frombottom);
        menus.startAnimation(frombottom);

        markEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MarkEntryActivity.class));
            }
        });
    }
}
