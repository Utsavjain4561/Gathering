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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by user on 1/26/19.
 */

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.MyViewHolder>{


    private Context context;
    private ArrayList<ReviewTemplate> list;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RatingBar ratingBar;
        public TextView description1;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingMul);
            description1 = itemView.findViewById(R.id.reviewDescription);
        }
    }

    public ReviewListAdapter(Context context, ArrayList <ReviewTemplate> list, Activity activity)
    {
        this.context=context;
        this.list=list;
        this.activity=activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_row,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ReviewTemplate temp = list.get(position);
        holder.ratingBar.setRating(temp.getRating());
        holder.ratingBar.setEnabled(false);
        holder.description1.setText(temp.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on pressing the card
                Toast.makeText(context, "Card clicked !", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {

        return list.size();
    }
}