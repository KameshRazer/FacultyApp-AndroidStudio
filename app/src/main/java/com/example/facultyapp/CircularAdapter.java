package com.example.facultyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CircularAdapter extends RecyclerView.Adapter<CircularAdapter.ViewHolder> {

    private ArrayList<ArrayList<String>> dataList;

    CircularAdapter(ArrayList<ArrayList<String>> dataList){
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context =parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View DataListView = layoutInflater.inflate(R.layout.display_circularmessage,parent,false);
        ViewHolder viewHolder;
        viewHolder = new ViewHolder(DataListView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ArrayList<String> data = dataList.get(position);
        TextView topic = holder.topic;
        TextView message = holder.msg;
        topic.setText(data.get(0));
        message.setText(data.get(1));
        final String year = data.get(2);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Circular/"+year);
                dbRef.child(data.get(0)).setValue(null);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView msg,topic;
        Button delete;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.circularMessage);
            delete = itemView.findViewById(R.id.delete);
            topic = itemView.findViewById(R.id.circularTopic);
        }
    }
}
