package com.example.facultyapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StudentAtdAdapter extends RecyclerView.Adapter<StudentAtdAdapter.ViewHolder> {

    private ArrayList<ArrayList<String>> dataList;

    StudentAtdAdapter(ArrayList<ArrayList<String>> dataList){
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context =parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View DataListView = layoutInflater.inflate(R.layout.display_subject_atd,parent,false);
        ViewHolder viewHolder;
        viewHolder = new ViewHolder(DataListView);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rollNo.setText(dataList.get(position).get(0));
        holder.percentage.setText(dataList.get(position).get(1)+" %");
        if(Integer.parseInt(dataList.get(position).get(1))<80)
            holder.percentage.setTextColor(Color.RED);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView rollNo,percentage;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            rollNo = itemView.findViewById(R.id.rollno_atd);
            percentage = itemView.findViewById(R.id.precentage);
        }
    }
}
