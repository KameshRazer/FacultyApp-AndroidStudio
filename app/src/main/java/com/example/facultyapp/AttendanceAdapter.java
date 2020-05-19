package com.example.facultyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    private ArrayList<String> dataList;
    private ArrayList<String> totalHour;
    AttendanceActivity aa = new AttendanceActivity();
    AttendanceAdapter(ArrayList<String> dataList,ArrayList<String> totalHour){
        this.dataList = dataList;
        this.totalHour = totalHour;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context =parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View DataListView = layoutInflater.inflate(R.layout.display_attendance,parent,false);
        AttendanceAdapter.ViewHolder viewHolder;
        viewHolder = new AttendanceAdapter.ViewHolder(DataListView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String rollno = dataList.get(position);
        String hr = totalHour.get(position);
        TextView rollNo = holder.rollNo;
        EditText hour = holder.hour;
        rollNo.setText(rollno);
        hour.setText(hr);

    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView rollNo;
        EditText hour;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            rollNo = itemView.findViewById(R.id.atd_Rollno);
            hour = itemView.findViewById(R.id.hours);
        }
    }

}
