package com.example.aryan.hack;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by UTSAV JAIN on 1/26/2019.
 */

public class EventsAdapter extends ArrayAdapter<Event> {
    private Context mContext;
    private ArrayList<Event> eventList;
    public EventsAdapter(@NonNull Context context, @NonNull ArrayList<Event> objects) {
        super(context, 0, objects);
        mContext  =context;
        eventList = objects;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.events_list_item,parent,false);
        Event event = eventList.get(position);

        TextView titleView = (TextView)listItem.findViewById(R.id.event_title);
        titleView.setText(event.getEventName());

        TextView locationView = (TextView)listItem.findViewById(R.id.event_location);
        locationView.setText(event.getLocation());

        TextView dateView = (TextView)listItem.findViewById(R.id.event_date);
        dateView.setText(event.getStartDate());
        return listItem;
    }
}
