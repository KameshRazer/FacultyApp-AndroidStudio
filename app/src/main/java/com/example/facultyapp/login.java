package com.example.facultyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {
    DatabaseReference dbRef;
    Button signIn;
    EditText ETuserId,ETpassword;
    SharedPreferences LogInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //EditText
        ETuserId = findViewById(R.id.inputEmail);
        ETpassword = findViewById(R.id.inputPassword);

        //Button
        signIn = findViewById(R.id.btnLogin);

        LogInfo = getSharedPreferences("FacultyLoginInfo",MODE_PRIVATE);

        //Checking previous Login
        if(LogInfo.getBoolean("isLogged",false)){
            startActivity(new Intent(login.this,MainActivity.class));
        }

        //SignInButton Action
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userId = ETuserId.getText().toString();
                final String password = ETpassword.getText().toString();
                dbRef = FirebaseDatabase.getInstance().getReference().child("Staff/"+userId.substring(0,7));
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(userId)){
                            String dbPassword = dataSnapshot.child(userId+"/password").getValue(String.class);
                            //Checking password
                            if(password.equals(dbPassword)) {
                                String name = dataSnapshot.child(userId + "/name").getValue(String.class);
                                String dept = dataSnapshot.child(userId + "/department").getValue(String.class);

                                LogInfo.edit().putString("name", name).apply();
                                LogInfo.edit().putString("department", dept).apply();
                                LogInfo.edit().putBoolean("isLogged",true).apply();
                                startActivity(new Intent(login.this, MainActivity.class));
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Invalid Password",Toast.LENGTH_SHORT).show();
                                ETpassword.setText("");
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Invalid User Id",Toast.LENGTH_SHORT).show();
                            ETuserId.setText("");
                            ETpassword.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


    }
}
