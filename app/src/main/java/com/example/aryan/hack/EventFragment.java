package com.example.aryan.hack;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by UTSAV JAIN on 1/26/2019.
 */

public class EventFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {
    private ListView listView;
    private ArrayList<Event> event = new ArrayList<>();
    private int flag =0   ;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GoogleMap mMap;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.event_fragment,container,false);
        listView = (ListView) rootView.findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });



       Intent intent = getActivity().getIntent();
        if (intent != null) {
            if (intent.getExtras() != null) {
                flag = intent.getExtras().getInt("flag");
            }
        }
        refresh();













        return rootView;
    }

    public void refresh(){
        event.clear();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference db = firebaseDatabase.getReference("gatherings").child("kumbhmela").child("events");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    Event eventobj = post.getValue(Event.class);
                    Log.e("Even", eventobj + " ");
                    event.add(eventobj);


                }
                EventsAdapter eventsAdapter = new EventsAdapter(getContext(), event);
                listView.setAdapter(eventsAdapter);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}

