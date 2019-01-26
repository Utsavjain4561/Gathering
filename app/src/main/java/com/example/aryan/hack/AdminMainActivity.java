package com.example.aryan.hack;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdminMainActivity extends AppCompatActivity {

    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout ;
    private ArrayList<Event> event = new ArrayList<>();
    private int flag =0   ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        listView = (ListView) findViewById(R.id.list);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getExtras() != null) {
                flag = intent.getExtras().getInt("flag");
            }
        }
            refresh();
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh();
                }
            });

            FloatingActionButton fab = findViewById(R.id.floatingActionButton);
            if (flag == 1) {
                fab.setVisibility(View.INVISIBLE);
                fab.setClickable(false);
            }

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // dialog box to add item on dashboard

                    Intent addItemIntent =new Intent(AdminMainActivity.this,AddItem.class);
                    startActivityForResult(addItemIntent,9);







                }
            });
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
                    EventsAdapter eventsAdapter = new EventsAdapter(getApplicationContext(), event);
                    listView.setAdapter(eventsAdapter);
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
