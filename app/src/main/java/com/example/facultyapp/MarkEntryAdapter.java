package com.example.facultyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MarkEntryAdapter extends RecyclerView.Adapter<MarkEntryAdapter.ViewHolder> {

    private ArrayList<ArrayList<String>> dataList ;
    MarkEntryAdapter(ArrayList<ArrayList<String>> dataList){
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context =parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View DataListView = layoutInflater.inflate(R.layout.display_mark,parent,false);
        ViewHolder viewHolder;
        viewHolder = new ViewHolder(DataListView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        ArrayList<String> data = dataList.get(position);
        TextView rollNo = holder.rollNo;
        final EditText mark = holder.mark;
        final Button update = holder.update;

        rollNo.setText(data.get(0));
        mark.setText(data.get(1));
        mark.setFocusable(false);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = update.getText().toString();
                if(text.equals("Edit")){
                    update.setText("Done");
                    mark.setFocusableInTouchMode(true);
                }else{
                    update.setText("Edit");
                    mark.setFocusable(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView rollNo;
        EditText mark;
        Button update;
         ViewHolder(@NonNull View itemView) {
            super(itemView);
            rollNo = itemView.findViewById(R.id.rollno);
            mark = itemView.findViewById(R.id.marks);
            update = itemView.findViewById(R.id.update);
        }
    }
}
