package com.example.aryan.hack;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddItem extends AppCompatActivity {
    private EditText eventName;
    private EditText startDateText;
    private EditText endDateText;
    private EditText locationText;
    private String location="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        eventName = findViewById(R.id.event_name);
        startDateText = findViewById(R.id.startDate);
        endDateText = findViewById(R.id.endDate);
        locationText = findViewById(R.id.location);
        Button ok = (Button) findViewById(R.id.ok);

        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.e("Place name",String.valueOf(place.getName()));
                location = String.valueOf(place.getName());
            }

            @Override
            public void onError(Status status) {

            }
        });
        locationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =
                        null;
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(AddItem.this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, 1);



            }
        });

        final String[] startDate = new String[1];
        final String[] endDate = new String[1];

        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                startDate[0] = sdf.format(myCalendar.getTime());
                startDateText.setText(startDate[0]);
            }
        };

        startDateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(AddItem.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final Calendar myCalendar1 = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar1.set(Calendar.YEAR, year);
                myCalendar1.set(Calendar.MONTH, monthOfYear);
                myCalendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                endDate[0] = sdf.format(myCalendar1.getTime());
                endDateText.setText(endDate[0]);
            }
        };

        endDateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(AddItem.this, date1, myCalendar1
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // new event added
                String eventname = String.valueOf(eventName.getText());
                Event newEvent = new Event(eventname, startDate[0], endDate[0],location);
                DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference("gatherings").child("kumbhmela").child("events");
                mDatabase.push().setValue(newEvent);
                Intent backIntent  =  new Intent(AddItem.this,AdminMainActivity.class);
                startActivity(backIntent);




            }
        });


    }



}
