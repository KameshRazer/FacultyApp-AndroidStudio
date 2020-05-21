package com.example.facultyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
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

public class CircularActivity extends AppCompatActivity {

    EditText newMsg,newMsgTopic;
    Button btnSendMsg;
    Spinner selectClass;
    RecyclerView displayMessage;
    ArrayList<ArrayList<String>> dataList = new ArrayList<>();
    ArrayList<String> selectYear = new ArrayList<>();
    String selectedYear;
    CircularAdapter circularAdapter;
    DatabaseReference dbRef;
    ArrayAdapter<String> yearAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular);

        newMsgTopic = findViewById(R.id.newMsgTopic);
        newMsg = findViewById(R.id.newMessage);
        btnSendMsg = findViewById(R.id.sendNewMessage);
        selectClass = findViewById(R.id.selectClass);
        displayMessage = findViewById(R.id.displayMessage);

        circularAdapter = new CircularAdapter(dataList);
        displayMessage.setAdapter(circularAdapter);
        displayMessage.setLayoutManager(new LinearLayoutManager(this));

        selectYear.add("Select Year");
        selectYear.add("18MX");
        selectYear.add("19MX");

        yearAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,selectYear);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectClass.setAdapter(yearAdapter);
        selectClass.setOnItemSelectedListener(new selectedYear());


        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRef = FirebaseDatabase.getInstance().getReference().child("Circular/"+selectedYear);

                if(TextUtils.isEmpty(newMsg.getText().toString())){
                    newMsg.setError("Enter Message");
                }else if(TextUtils.isEmpty(newMsgTopic.getText().toString())) {
                    newMsgTopic.setError("Enter Topic");
                }else {
                    final String topic = newMsgTopic.getText().toString();
                    final String message = newMsg.getText().toString();
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(topic)){
                                newMsgTopic.setError("Topic Already Exist");
                                newMsgTopic.setText("");
                            }else {
                                final Message msg = new Message();
                                msg.setMessage(message);
                                dbRef.child(topic).setValue(msg);
                            }
                            viewMessage();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        });
    }

    void viewMessage(){
        dbRef = FirebaseDatabase.getInstance().getReference().child("Circular/"+selectedYear);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(ds.getKey());
                    list.add(ds.child("message").getValue().toString());
                    list.add(selectedYear);
                    dataList.add(list);
                }
                circularAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    class selectedYear implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(position>0) {
                btnSendMsg.setVisibility(View.VISIBLE);
                selectedYear = parent.getSelectedItem().toString();
                viewMessage();
            }else{
                btnSendMsg.setVisibility(View.INVISIBLE);
                dataList.clear();
                circularAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    static class Message{
        private String message;

        public String getMessage() {
            return message;
        }

        void setMessage(String message) {
            this.message = message;
        }
    }
}
