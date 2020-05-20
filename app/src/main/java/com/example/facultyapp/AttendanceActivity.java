package com.example.facultyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class AttendanceActivity extends AppCompatActivity {

    DatabaseReference dbRef;
    SharedPreferences logInfo;
    Spinner subject,totalHours;
    RecyclerView recyclerView;
    Button btnSave;
    String[] subjectCode,subjectName = new String[5];
    String selectedSubject,selectedSubjectCode,year;
    int selectedHour;
    ArrayList<String> subjectArray = new ArrayList<>();
    ArrayList<String> dataList = new ArrayList<>();
    ArrayList<String> hours = new ArrayList<>();
    ArrayList<String> hour = new ArrayList<>();
    ArrayAdapter<String> subjectAdapter,hourAdapter;
    AttendanceAdapter attendanceAdapter;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        subject = findViewById(R.id.subject);
        recyclerView = findViewById(R.id.studentList);
        btnSave = findViewById(R.id.btnSave);
        totalHours = findViewById(R.id.totalHours);

        btnSave.setVisibility(View.INVISIBLE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("LoDiNg...");
        logInfo = getSharedPreferences("FacultyLoginInfo",MODE_PRIVATE);
        String getSubjectCode = logInfo.getString("subjectCode","Error");
        String getSubjectName = logInfo.getString("subjectName","Error");
        subjectCode = getSubjectCode.split("-");
        subjectName = getSubjectName.split("-");

        subjectArray.add("Select Subject");
        subjectArray.addAll(Arrays.asList(subjectName).subList(0, subjectCode.length));

        subjectAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,subjectArray);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject.setAdapter(subjectAdapter);
        subject.setOnItemSelectedListener(new CreateAttendanceList());

        hourAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,hours);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        totalHours.setAdapter(hourAdapter);
        totalHours.setOnItemSelectedListener(new TotalHour());

        attendanceAdapter = new AttendanceAdapter(dataList,hour);
        recyclerView.setAdapter(attendanceAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int n = attendanceAdapter.getItemCount();
                final int presentId = R.id.present;
                boolean isValid =true;
                for (int i = 0; i <n ; i++) {
                    View c = recyclerView.getLayoutManager().findViewByPosition(i);
                    EditText hour = c.findViewById(R.id.hours);
                    int hr = Integer.parseInt(hour.getText().toString());
                    if(hr>selectedHour){
                        hour.setError("Invalid Hour");
                        isValid = false;
                    }
                }
                if(isValid) {


                    dbRef = FirebaseDatabase.getInstance().getReference().child("Attendance");

                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (int i = 0; i < n; i++) {
                                View c = recyclerView.getLayoutManager().findViewByPosition(i);
                                TextView rollno = c.findViewById(R.id.atd_Rollno);
                                EditText hour = c.findViewById(R.id.hours);
                                RadioGroup radioGroup = c.findViewById(R.id.status);
                                final String rollNo = rollno.getText().toString();
                                int selectedStatusId = radioGroup.getCheckedRadioButtonId();
                                int presentHrs = Integer.parseInt(hour.getText().toString());
                                int absentHrs = selectedHour - presentHrs;
                                String path = rollNo.substring(0, 4) + "/" + rollNo + "/" + selectedSubject;
                                DatabaseReference upRef;
                                if (presentId == selectedStatusId && presentHrs == selectedHour) {
                                    int present = Integer.parseInt(dataSnapshot.child(path + "/Present").getValue().toString());
                                    upRef = dataSnapshot.child(path + "/Present").getRef();
                                    upRef.setValue(present + presentHrs);
                                } else if (presentId != selectedStatusId) {
                                    int absent = Integer.parseInt(dataSnapshot.child(path + "/Absent").getValue().toString());
                                    upRef = dataSnapshot.child(path + "/Absent").getRef();
                                    upRef.setValue(absent + selectedHour);
                                } else if (presentHrs < selectedHour) {
                                    int present = Integer.parseInt(dataSnapshot.child(path + "/Present").getValue().toString());
                                    upRef = dataSnapshot.child(path + "/Present").getRef();
                                    upRef.setValue(present + presentHrs);
                                    int absent = Integer.parseInt(dataSnapshot.child(path + "/Absent").getValue().toString());
                                    upRef = dataSnapshot.child(path + "/Absent").getRef();
                                    upRef.setValue(absent + absentHrs);
                                }
                                int totalHrs = Integer.parseInt(dataSnapshot.child(path + "/Total Hours").getValue().toString());
                                upRef = dataSnapshot.child(path + "/Total Hours").getRef();
                                upRef.setValue(totalHrs + selectedHour);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"Invalid Hour",Toast.LENGTH_LONG).show();
                }

            }

        });


    }
    class CreateAttendanceList implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            hours.clear();
//            attendanceAdapter.notifyDataSetChanged();
            btnSave.setVisibility(View.INVISIBLE);
            if(position>0){
                selectedSubject=parent.getSelectedItem().toString();
                selectedSubjectCode=subjectCode[position-1];
                hours.add("Select Hours");
                hours.add("1");
                hours.add("2");
                dataList.clear();
                attendanceAdapter.notifyDataSetChanged();
                hourAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
    class TotalHour implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {
            dataList.clear();
            hour.clear();
            if(position>0){
                selectedHour = Integer.parseInt(parent.getSelectedItem().toString());
                attendanceAdapter.notifyDataSetChanged();
                progressDialog.show();
                dbRef = FirebaseDatabase.getInstance().getReference().child("Subject/"+selectedSubjectCode);
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        year = dataSnapshot.child("class").getValue(String.class);
                        dbRef = FirebaseDatabase.getInstance().getReference().child("Student/"+year);
                        dbRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    dataList.add(ds.getKey());
                                    hour.add(parent.getSelectedItem().toString());
                                }
                                attendanceAdapter.notifyDataSetChanged();
                                btnSave.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else{
                attendanceAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
