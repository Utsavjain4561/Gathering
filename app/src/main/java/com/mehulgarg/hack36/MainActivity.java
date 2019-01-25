package com.mehulgarg.hack36;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dialog box to add item on dashboard
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.dialog_box_add_event, null);
                mBuilder.setTitle("Add an event");

                Button ok = (Button) mView.findViewById(R.id.ok);
                final EditText eventName = mView.findViewById(R.id.event_name);
                final EditText startDateText = mView.findViewById(R.id.startDate);
                final EditText endDateText = mView.findViewById(R.id.endDate);
                EditText locationText = mView.findViewById(R.id.location);

                final String[] startDate = new String[1];
                final String[] endDate = new String[1];

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

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

                        new DatePickerDialog(MainActivity.this, date, myCalendar
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

                        new DatePickerDialog(MainActivity.this, date1, myCalendar1
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });




                PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                        getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        // TODO: Get info about the selected place.
                        Log.i("rd", "Place: " + place.getName());
                       // mMap.addMarker(new MarkerOptions().position(place.getLatLng()).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    }
                    @Override
                    public void onError(Status status) {
                        // TODO: Handle the error.
                        Log.i("rd", "An error occurred: " + status);
                    }
                });
                Intent intent =
                        null;
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, PLACE_PICKER_REQUEST);




                final String location = "Allahabad";


                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // new event added
                        String eventname = String.valueOf(eventName.getText());
                        Event newEvent = new Event(eventname, startDate[0], endDate[0], location);

                        private DatabaseReference mDatabase;
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("Events").child(eventname).setValue(newEvent);
                        dialog.dismiss();
                    }
                });
            }
        });
    }
}
