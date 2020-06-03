package com.example.facultyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CircularActivity extends AppCompatActivity {

    EditText newMsg,newMsgTopic;
    Button btnSendMsg,back2;
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
        back2 = findViewById(R.id.back2);

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

        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CircularActivity.this,MainActivity.class));
            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRef = FirebaseDatabase.getInstance().getReference();

                if(TextUtils.isEmpty(newMsgTopic.getText().toString())){
                    newMsg.setError("Enter Topic");
                }else if(TextUtils.isEmpty(newMsg.getText().toString())) {
                    newMsgTopic.setError("Enter Message");
                }else {
                    final String topic = newMsgTopic.getText().toString().toUpperCase();
                    final String message = newMsg.getText().toString();
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("Circular/"+selectedYear+"/"+topic)){
                                newMsgTopic.setError("Topic Already Exist");
                                newMsgTopic.setText("");
                            }else {
                                final Message msg = new Message();
                                msg.setMessage(message);
                                dbRef.child("Circular/"+selectedYear+"/"+topic).setValue(msg);
                                for(DataSnapshot ds : dataSnapshot.child("Student/"+selectedYear).getChildren()) {
                                    if(ds.hasChild("TokenId")) {
                                        String token_id = ds.child("TokenId").getValue().toString();
                                        sendPush("Circular",topic,token_id);
                                        newMsgTopic.setText("");
                                        newMsg.setText("");
                                        Toast.makeText(getApplicationContext(),"Circular Posted",Toast.LENGTH_LONG).show();
                                    }
                                }
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

    public void sendPush(final String title,final String msg ,final String token_id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject payloadObj = new JSONObject();
                try {
                    JSONObject notifyObj = new JSONObject();
                    notifyObj.put("title", title);
                    notifyObj.put("body", msg);
                    notifyObj.put("text", msg);

                    payloadObj.put("to", token_id.trim());
                    payloadObj.put("priority", "high");
                    payloadObj.put("notification", notifyObj);

                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                sendMessage(payloadObj.toString());

//                dbRef = FirebaseDatabase.getInstance().getReference().child("Student/"+selectedYear);
//                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        String token_id;
//                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
//                            if(ds.hasChild("TokenId")) {
//
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

            }
        }).start();
    }

    public void sendMessage(String msg) {
        final String apiKey = "AAAAuR0ntEs:APA91bFm0VX8SjBQNyF-Jlmgr1WV9-3e1imHya7iPYeHcLGmY5_scz9i5xEX1-LL5ewyXqtY3KHq-vdhZBdAK_72yew7T9-epDU0ieGGu30Kq6GjE3PbpodcQ9deWY2xQMP7h8ePe36Q";
        StringBuffer response = new StringBuffer();
        try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);

            OutputStream os = conn.getOutputStream();
            os.write(msg.getBytes());
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

//            int responseCode = conn.getResponseCode();
//            System.out.println("\nSending 'POST' request to URL : " + url);
//            System.out.println("Post parameters : " + msg);
//            System.out.println("Response Code : " + responseCode);
//            String inputLine;
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
            in.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
        // print result
        System.out.println(response.toString());
    }
}
