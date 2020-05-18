package com.example.facultyapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Collections;
import java.util.Objects;

public class MarkEntryActivity extends AppCompatActivity {

    Spinner subjectList,markList,testList;
    SharedPreferences logInfo;
    DatabaseReference dbRef;
    ProgressDialog progressDialog ;
    RecyclerView recyclerView;
    Button btnSave;

    String[] subjectCode,subjectName = new String[5];
    int codePosition =0;
    String selectedSubject,selectedCode,selectedTest,selectedMark,year;
    ArrayAdapter<String> dataAdapter,dataAdapter1;
    MarkEntryAdapter markAdapter;

    ArrayList<String> subjectArray = new ArrayList<>();
    ArrayList<String> markArray = new ArrayList<>();
    ArrayList<String> testArray = new ArrayList<>();
    ArrayList<ArrayList<String>> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_entry);

        subjectList = findViewById(R.id.subjectList);
        markList = findViewById(R.id.markList);
        testList = findViewById(R.id.selectMark);

        recyclerView = findViewById(R.id.result);
        btnSave = findViewById(R.id.save);

        markAdapter = new MarkEntryAdapter(dataList);
        recyclerView.setAdapter(markAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("LoDiNg...");

        logInfo = getSharedPreferences("FacultyLoginInfo",MODE_PRIVATE);
        subjectArray.add("Select Subject");

        String getSubjectCode = logInfo.getString("subjectCode","Error");
        String getSubjectName = logInfo.getString("subjectName","Error");
        subjectCode = getSubjectCode.split("-");
        subjectName = getSubjectName.split("-");

        subjectArray.addAll(Arrays.asList(subjectName).subList(0, subjectCode.length));

        dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,subjectArray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectList.setAdapter(dataAdapter);

        subjectList.setOnItemSelectedListener(new Marks());

        dataAdapter = new ArrayAdapter<>(MarkEntryActivity.this,android.R.layout.simple_spinner_item,markArray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        markList.setAdapter(dataAdapter);

        markList.setOnItemSelectedListener(new Test());

        dataAdapter1 = new ArrayAdapter<>(MarkEntryActivity.this,android.R.layout.simple_spinner_item,testArray);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        testList.setAdapter(dataAdapter1);

        testList.setOnItemSelectedListener(new TestResult());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dataList.clear();
//                markAdapter.notifyDataSetChanged();
                progressDialog.show();
                String rollNo;
                int n = markAdapter.getItemCount();
                for (int i = 0; i < n ; i++) {
                    View c = recyclerView.getLayoutManager().findViewByPosition(i);
                    TextView rollno = c.findViewById(R.id.rollno);
                    rollNo = rollno.getText().toString();
                    EditText mark = c.findViewById(R.id.marks);
                    dbRef = FirebaseDatabase.getInstance().getReference().child("Marks/"+year+"/"+selectedTest+"/"+rollNo+"/"+selectedCode+"/"+selectedMark);
                    dbRef.setValue(mark.getText().toString());
                }
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Update Success",Toast.LENGTH_LONG).show();

//                System.out.println("Path : "+"Marks/"+year+"/"+selectedTest+"/"+rollNo+"/"+selectedCode+"/"+selectedMark);
            }
        });

    }

    public class Marks implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            dataList.clear();
            markAdapter.notifyDataSetChanged();
            btnSave.setVisibility(View.INVISIBLE);
            if(position>0) {
                selectedSubject = parent.getSelectedItem().toString();
                selectedCode = subjectCode[position-1];
                progressDialog.show();
                markArray.clear();
                markArray.add("Select");
                testArray.clear();
                dataAdapter1.notifyDataSetChanged();
                dbRef = FirebaseDatabase.getInstance().getReference().child("Subject/"+selectedCode);
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if(!"name".equals(ds.getKey()) && !"class".equals(ds.getKey())) {
                                markArray.add(ds.getKey());
                            }
                            if("class".equals(ds.getKey()))
                                year=ds.getValue(String.class);
                        }
                        dataAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            else{
                markArray.clear();
                testArray.clear();
                dataList.clear();
                dataAdapter1.notifyDataSetChanged();
                dataAdapter.notifyDataSetChanged();
                markAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class Test implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selected = parent.getSelectedItem().toString();
            testArray.clear();
            dataList.clear();
            markAdapter.notifyDataSetChanged();
            btnSave.setVisibility(View.INVISIBLE);
            if(position>0){
                dataAdapter1.notifyDataSetChanged();
                selectedTest = parent.getSelectedItem().toString();
                progressDialog.show();
                dbRef = FirebaseDatabase.getInstance().getReference().child("Subject/"+selectedCode+"/"+selected);
                dbRef.addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String[] data = Objects.requireNonNull(dataSnapshot.getValue()).toString().split("-");
                        testArray.add("Select");
                        Collections.addAll(testArray, data);
                        dataAdapter1.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else{
                dataAdapter1.notifyDataSetChanged();
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class TestResult implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            dataList.clear();
            if(position>0) {
                selectedMark = parent.getSelectedItem().toString();
                progressDialog.show();
                dbRef = FirebaseDatabase.getInstance().getReference().child("Marks/" + year + "/" + selectedTest);
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String rolln = ds.getKey();
                            String mar = ds.child(selectedCode + "/" + selectedMark).getValue().toString();
                            add(rolln,mar);
                            markAdapter.notifyDataSetChanged();
                        }
                        progressDialog.dismiss();
                        btnSave.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else {
                markAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        void add(String roll,String mar){
            ArrayList<String> data = new ArrayList<>();
            data.clear();
            data.add(roll);
            data.add(mar);
            dataList.add(data);
//            markAdapter.notifyDataSetChanged();
        }
    }
}
