package com.example.aryan.hack;

/**
 * Created by UTSAV JAIN on 1/26/2019.
 */

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by user on 1/26/19.
 */

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.MyViewHolder>{


    private Context context;
    private ArrayList<validationRequest> list;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameField, roleField, numberField, approveField;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            nameField = itemView.findViewById(R.id.reqName);
            roleField = itemView.findViewById(R.id.reqRole);
            numberField = itemView.findViewById(R.id.reqMob);
            approveField = itemView.findViewById(R.id.approve);
        }
    }

    public RequestListAdapter(Context context, ArrayList <validationRequest> list, Activity activity)
    {
        this.context=context;
        this.list=list;
        this.activity=activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_row,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final validationRequest temp = list.get(position);
        holder.nameField.setText(temp.getName());
        holder.roleField.setText(temp.getRole());
        holder.numberField.setText(temp.getNumber());
        holder.approveField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String numberX = list.get(position).getNumber();
                final DatabaseReference df = FirebaseDatabase.getInstance().getReference("gatherings").child("kumbhmela").child("requests");
                df.child(numberX).setValue(null);

                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                notifyDataSetChanged();
                //RequestListForAdmin requestListForAdmin = new RequestListForAdmin();
                //requestListForAdmin.refresh();
            }
        });
    }

    @Override
    public int getItemCount() {

        return list.size();
    }
}