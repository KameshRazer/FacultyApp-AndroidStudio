package com.example.facultyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class StudentAtdActivity extends AppCompatActivity {
    Spinner subjectList;
    RecyclerView recyclerView;
    SharedPreferences logInfo;
    DatabaseReference dbRef;
    String[] subjectCode,subjectName = new String[5];
    String selectedSubjectCode,selectedSubjectName;
    ArrayAdapter<String> dataAdapter;
    ArrayList<String > subjectArray = new ArrayList<>();
    ArrayList<ArrayList<String>> dataList = new ArrayList<>();
    StudentAtdAdapter studentAtdAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_atd);

        subjectList = findViewById(R.id.subject_atd);
        recyclerView = findViewById(R.id.subject_atd_recyclerview);
        logInfo = getSharedPreferences("FacultyLoginInfo",MODE_PRIVATE);
        String getSubjectCode = logInfo.getString("subjectCode","Error");
        String getSubjectName = logInfo.getString("subjectName","Error");
        subjectCode = getSubjectCode.split("-");
        subjectName = getSubjectName.split("-");

        subjectArray.addAll(Arrays.asList(subjectName).subList(0, subjectName.length));
        dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,subjectArray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectList.setAdapter(dataAdapter);

        subjectList.setOnItemSelectedListener(new SubjectAttendance());

        studentAtdAdapter = new StudentAtdAdapter(dataList);
        recyclerView.setAdapter(studentAtdAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    class SubjectAttendance implements AdapterView.OnItemSelectedListener{

        String year;
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedSubjectCode = subjectCode[position];
            selectedSubjectName = subjectName[position];
            dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    year = dataSnapshot.child("Subject/"+selectedSubjectCode+"/class").getValue().toString();
                    dataList.clear();
                    for(DataSnapshot ds : dataSnapshot.child("Attendance/"+year).getChildren()){
                        ArrayList<String > list = new ArrayList<>();
                        list.add(ds.getKey());
                        float present = Float.parseFloat(ds.child(selectedSubjectName+"/Present").getValue().toString());
                        float total_hrs = Float.parseFloat(ds.child(selectedSubjectName+"/Total Hours").getValue().toString());
                        list.add((String.valueOf((int)((present/total_hrs)*100.0))));
                        dataList.add(list);
                    }
//                    System.out.println("DataList : "+dataList);
                    studentAtdAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
