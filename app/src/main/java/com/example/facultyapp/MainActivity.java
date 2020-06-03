package com.example.facultyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageView bgapp, clover,markEntry,attendance,circular,student;
    LinearLayout textsplash, texthome, menus;
    Animation frombottom;
    SharedPreferences logInfo;
    String name,department;
    Boolean isLogged;
    TextView TV_name1,TV_name2,TV_department;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);

        markEntry = findViewById(R.id.markEntry);
        attendance = findViewById(R.id.attendance);
        student = findViewById(R.id.student_atd);

        bgapp = (ImageView) findViewById(R.id.bgapp);
        clover = (ImageView) findViewById(R.id.clover);
        textsplash = (LinearLayout) findViewById(R.id.textsplash);
        texthome = (LinearLayout) findViewById(R.id.texthome);
        menus = (LinearLayout) findViewById(R.id.menus);
        circular = findViewById(R.id.circular);

        TV_name1 = findViewById(R.id.name1);
        TV_name2 = findViewById(R.id.name2);
        TV_department = findViewById(R.id.department);

        btnLogout = findViewById(R.id.logout);

        logInfo = getSharedPreferences("FacultyLoginInfo",MODE_PRIVATE);
        name = logInfo.getString("name","Error");
        department = logInfo.getString("department","Error");
        isLogged = logInfo.getBoolean("isLogged",false);
        if(!isLogged)
            startActivity(new Intent(MainActivity.this,login.class));


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

        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AttendanceActivity.class));
            }
        });

        circular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CircularActivity.class));
            }
        });

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,StudentAtdActivity.class));
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInfo.edit().clear().apply();
                startActivity(new Intent(MainActivity.this,login.class));
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        System.out.println("Login onRestart invoked");
        //Checking previous Login
        isLogged = logInfo.getBoolean("isLogged",false);
        if(!isLogged)
           finishAffinity();
    }

}
