package com.example.aryan.hack;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap=null;
    Detail details;
    String key;
    String lat = "";
    String lng = "";
    String address="";
    int PLACE_PICKER_REQUEST = 1;
    GroundOverlay groundOverlay1=null;
    int meanUpvotes = 0;
    public static final PatternItem DOT = new Dot();
    public static final PatternItem DASH = new Dash(20);
    public static final PatternItem GAP = new Gap(20);
    public static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    private final String API_KEY="AIzaSyCU5oBs1VJutdJhnH5i3VKqYZNoN9TTtiY";
    private ArrayList<String> latlng=new ArrayList<>();
    private ArrayList<ExtraDetail> extraDetails = new ArrayList<>();
    private List<LatLng> lines = new ArrayList<LatLng>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        address = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        updateMap();
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
    Detail local_details;
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
               final DatabaseReference myref = firebaseDatabase.getReference("garbage").child(local_key);
               myref.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       if(dataSnapshot.exists()) {
                          // Toast.makeText(MainActivity.this,""+local_details.upvotes,Toast.LENGTH_LONG).show();
                       }
                       else
                       {
                           local_details = new Detail("hi kaise ho janeman",0,address);
                           myref.setValue(local_details);
                       }
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });
               marker.showInfoWindow();
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
    public void onInfoWindowClick(final Marker marker) {
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
                        marker.remove();
                        updateMap();
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
                        marker.remove();
                        updateMap();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).show();
        marker.showInfoWindow();
    }
    void updateMap()
    {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("garbage");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot post : dataSnapshot.getChildren())
                {
                    meanUpvotes = 0;
                    int index = post.getKey().indexOf("n");
                    lat = post.getKey().substring(0,index);
                    lng = post.getKey().substring(index+1);
                    lat = lat.replace('o','.');
                    lng = lng.replace('o','.');
                    String latlngString = String.valueOf(lat)+"%2C"+String.valueOf(lng);
                    //latlng.add(latlngString);
                    Detail placeDetail = post.getValue(Detail.class);
                    Log.e("Place upvotes",placeDetail.upvotes+"");
                    extraDetails.add(new ExtraDetail(latlngString,placeDetail.upvotes,new Date()));
                    meanUpvotes+=placeDetail.upvotes;
                    Log.e("Sum ",meanUpvotes+"");
                    Marker marker;
                    if (post.getValue(Detail.class).address == address) {
                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Details").snippet("Description: " + post.child("description").getValue() + "\n" + "Upvotes: " + post.child("upvotes").getValue()));
                    }else
                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng))).snippet("Description: " + post.child("description").getValue()+"\n"+"Upvotes: " + post.child("upvotes").getValue()).title("Title"));
                    marker.showInfoWindow();
                }
                sortLocations();
                new Directions().execute();
            }





            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private class SortComparator implements Comparator<ExtraDetail> {
        private int maxUpvotes;
        private int minUpvotes;
        private Date maxDate;
        private Date minDate;
        public SortComparator(int maxUpvotes, int minUpvotes, Date maxDate, Date minDate) {
            this.maxUpvotes = maxUpvotes;
            this.minUpvotes = minUpvotes;
            this.maxDate = maxDate;
            this.minDate = minDate;
        }

        @Override
        public int compare(ExtraDetail extraDetail, ExtraDetail t1) {
            double normalizedDate1 = 1-(extraDetail.date.getTime() - minDate.getTime())*1.0
                    /(maxDate.getTime() -minDate.getTime());
            double normalizedDate2 = 1-(t1.date.getTime() - minDate.getTime())*1.0
                    /(maxDate.getTime() -minDate.getTime());
            double normalizedUpvotes1= 1-(extraDetail.upvotes - minUpvotes)*1.0
                    /(maxUpvotes  - minUpvotes);
            double normalizedUpvotes2= 1-(t1.upvotes - minUpvotes)*1.0
                    /(maxUpvotes  - minUpvotes);
            return  (int)(((normalizedDate1-normalizedDate2)+(normalizedUpvotes1 - normalizedUpvotes2))*100000);
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }
    private void sortLocations(){
        int maxUpvotes=Integer.MIN_VALUE;
        int minUpvotes= Integer.MAX_VALUE;
        Date maxDate = new Date(Long.MIN_VALUE);
        Date minDate = new Date(Long.MAX_VALUE);

        for(int i=0;i<extraDetails.size();i++){

            int upvotes = extraDetails.get(i).upvotes;
            Date date = extraDetails.get(i).date;
            if(upvotes>maxUpvotes){
                maxUpvotes = upvotes;
            }
            else if(upvotes<minUpvotes){
                minUpvotes = upvotes;

            }
            if(date.compareTo(maxDate)>0){
                maxDate = date;
            }
            else if(date.compareTo(minDate)<0){
                minDate = date;
            }
        }
        Collections.sort(extraDetails,new SortComparator(maxUpvotes,minUpvotes,maxDate,minDate));
    }
    private class Directions extends AsyncTask<String,Void,String> {
        public String makeConnection(ArrayList<String> latlng){
            HttpURLConnection httpURLConnection=null;
            URLConnection url;
            String data="";
            try{
                url = new URL(createUrl(latlng)).openConnection();
                httpURLConnection=(HttpURLConnection) url;
                Log.e("URL",createUrl(latlng));
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Content-Type","application/json");
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
//                    Log.e("background","data "+data);
                }
                ((HttpURLConnection) url).disconnect();
                Log.e("Connection"," closed");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }



        @Override
        protected  String doInBackground(String... strings) {
            meanUpvotes/=extraDetails.size();
            Log.e("MeanUpvotes",meanUpvotes+"");
            Log.e("Size of extra",extraDetails.size()+"");
            Log.e("Extra",extraDetails.get(0).upvotes+"");
            for(int i=0;i<extraDetails.size();i++){
                Log.e(i+":",extraDetails.get(i).upvotes+"");
            }
            int i=0;
            while(i<extraDetails.size() && meanUpvotes <= extraDetails.get(i).upvotes ) {
                //TODO : Add threshold for upvotes
                latlng.add( extraDetails.get(i).latlng);

                i++;
            }
            Log.e("Before",latlng+"");

            String  result =makeConnection(latlng);




            i--;
            latlng.clear();
            while(i<extraDetails.size()){
                latlng.add(extraDetails.get(i).latlng);
                i++;
            }
            Log.e("After",latlng+" ");
            result+="#";
            result+= makeConnection(latlng);

            return result;
        }
        private void addStringToPolyline(String s){
            try {
                JSONObject dataobj=new JSONObject(s);
                JSONObject overview=dataobj.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline");
                String polyline=overview.getString("points");
                for (LatLng p : decodePolyline(polyline)) {
                    lines.add(p);
                }
                Log.e("polyline ",lines+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Data",s);
            String highPriority = s.substring(0,s.indexOf('#'));
            String lowPriority = s.substring(s.indexOf('#')+1);
            addStringToPolyline(highPriority);
            addStringToPolyline(lowPriority);
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.pattern(PATTERN_POLYGON_ALPHA);
            polylineOptions.startCap(new RoundCap());

            mMap.addPolyline(polylineOptions.addAll(lines).width(10).color(Color.BLUE));


        }
        private List<LatLng> decodePolyline(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();

            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
                poly.add(p);
            }

            return poly;
        }


        private String createUrl(ArrayList<String> latlng){
            String waypoints="";
            for(int i=1;i<latlng.size()-1;i++){
                waypoints+="|"+latlng.get(i);
            }
            String url="https://maps.googleapis.com/maps/api/directions/json?origin="+latlng.get(0)+"&destination="+
                    latlng.get(latlng.size()-1)+
                    "&waypoints=optimize:true"+waypoints+"&key="+API_KEY;
            Log.e("Key",API_KEY);
            return url;
        }
    }

}
