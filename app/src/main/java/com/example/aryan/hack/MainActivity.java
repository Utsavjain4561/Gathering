package com.example.aryan.hack;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap=null;
    Detail details;
    String key;
    String lat = "";
    String lng = "";
    String address="";
    int PLACE_PICKER_REQUEST = 1;
    GroundOverlay groundOverlay1=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        address = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("garbage");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot post : dataSnapshot.getChildren())
                {
                    int index = post.getKey().indexOf("n");
                    lat = post.getKey().substring(0,index);
                    lng = post.getKey().substring(index+1);
                    lat = lat.replace('o','.');
                    lng = lng.replace('o','.');
                    Marker marker;
                    if (post.getValue(Detail.class).address == address) {
                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Details").snippet("Description: " + post.child("description") + "\n" + "Upvotes: " + post.child("upvotes")));
                    }else
                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng))).snippet("Description: " + post.child("description").getValue()+"\n"+"Upvotes: " + post.child("upvotes").getValue()).title("Title"));
                    marker.showInfoWindow();
                }
            }





            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("rd", "Place: " + place.getName());
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
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



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("rd", "Place: " + place.getLatLng());
            }
            else
                Toast.makeText(MainActivity.this,"no",Toast.LENGTH_LONG).show();
        }
    }
    Detail local_details = new Detail("hi kaise ho janeman",0,address);
    String local_key;
    String local_lat,local_lng;

    @Override
    public void onMapReady(GoogleMap googleMap) {
       mMap=googleMap;
        mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
        mMap.setOnInfoWindowClickListener(this);
       mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
           @Override
           public boolean onMarkerClick(Marker marker) {
               marker.setIcon(BitmapDescriptorFactory.defaultMarker());
               marker.setDraggable(false);
               FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
               local_lat = marker.getPosition().latitude+"";
               local_lng = marker.getPosition().longitude+"";
               local_lat=local_lat.replace('.','o');
               local_lng= local_lng.replace('.','o');
               local_key = local_lat + 'n' + local_lng;
               // Toast.makeText(MainActivity.this,local_key,Toast.LENGTH_LONG).show();
               DatabaseReference myref = firebaseDatabase.getReference("garbage").child(local_key);
               myref.setValue(local_details);
               return false;
           }
       });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                            @Override
                            public void onMarkerDragStart(Marker arg0) {
                                // TODO Auto-generated method stub
                                Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                                removeCircleToMap(arg0);

                            }
                            @Override
                            public void onMarkerDragEnd(Marker arg0) {
                                // TODO Auto-generated method stub
                                Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                                addCircleToMap(arg0);
                            }

                            @Override
                            public void onMarkerDrag(Marker arg0) {
                                // TODO Auto-generated method stub
                                Log.i("System out", "onMarkerDrag...");
                            }
                        });

//Don't forget to Set draggable(true) to marker, if this not set marker does not drag.

        }
    void removeCircleToMap(Marker marker)
    {
        if(groundOverlay1!=null)
        groundOverlay1.remove();
    }
    void addCircleToMap(Marker marker)
    {
        // circle settings
        int radiusM = 20;
        double latitude = marker.getPosition().latitude;
        double longitude = marker.getPosition().longitude;
                LatLng latLng = new LatLng(latitude,longitude);

        // draw circle
        int d = 500; // diameter
        Bitmap bm = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.colorAccent));
        c.drawCircle(d/2, d/2, d/2, p);

        // generate BitmapDescriptor from circle Bitmap
        BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);

// mapView is the GoogleMap
         groundOverlay1 = mMap.addGroundOverlay(new GroundOverlayOptions().
                image(bmD).
                position(latLng,radiusM*2,radiusM*2).
                transparency(0.4f));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String la,lo;
        final String ke;
        la = marker.getPosition().latitude+"";
        lo = marker.getPosition().longitude+"";
        la=la.replace('.','o');
        lo= lo.replace('.','o');
        ke = la + 'n' + lo;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Upvote Or Downvote smartly").setPositiveButton("Upvote", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myref = database.getReference("garbage").child(ke);
                myref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Detail detail = dataSnapshot.getValue(Detail.class);
                        int upvotes = detail.upvotes;
                        upvotes++;
                        detail.upvotes = upvotes;
                        myref.setValue(detail);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).setNegativeButton("Downvote", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myref = database.getReference("garbage").child(ke);
                myref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Detail detail = dataSnapshot.getValue(Detail.class);
                        int upvotes = detail.upvotes;
                        upvotes--;
                        detail.upvotes = upvotes;
                        myref.setValue(detail);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).show();
    }
}
