package com.example.aryan.hack;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

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

public class DriverActivity extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap=null;

    private boolean flag=true;

    private Marker m1=null,m2=null;
    private final String API_KEY="AIzaSyAypnLWOzSglbt3bTtZ4Cia3vSjD3npHyk";
    private double meanUpvotes =0.0;
    private  String lat = "";
    private  String lng = "";
    private ArrayList<ExtraDetail> extraDetails = new ArrayList<>();
    private ArrayList<String> latlng = new ArrayList<>();
    ArrayList<LatLng> coordinates = new ArrayList<LatLng>();
    ArrayList<LatLng> lines = new ArrayList<>();
    public static final PatternItem DOT = new Dot();

    public static final PatternItem DASH = new Dash(20);
    public static final PatternItem GAP = new Gap(20);
    public static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(DOT,GAP, DASH);
    Runnable runnable1=null,runnable2=null;
    Handler handler1 = new Handler();
    Handler handler2 = new Handler();
    SwipeRefreshLayout swipeRefreshLayout;
    private  MapDetails mapDetails ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        SupportMapFragment mapFragment1 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.smap);
        mapFragment1.getMapAsync(this);






            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Log.e("refershing", "Refresh");
                   // updateMap();
                    // while (!updateProgress){}
                    // swipeRefreshLayout.setRefreshing(false);
                    // updateProgress = false;
                }
            });

        }

    private  void animateMarker(GoogleMap myMap, final Marker marker, final List<LatLng> directionPoint,
                              final boolean hideMarker) {
        final long start = SystemClock.uptimeMillis();
        if(flag) {
            if(runnable2!=null)
                handler2.removeCallbacks(runnable2);
            if (m2!=null)
            {
                m2.setVisible(false);
            }
            m1 = marker;
            flag = false;
            handler1.post(runnable1 =new Runnable() {
                int i = 0;

                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    if (i < directionPoint.size())
                        marker.setPosition(directionPoint.get(i));
                    i++;
                    if (i == directionPoint.size())
                        i = 0;
                    // Post again 16ms later.
                    handler1.postDelayed(this, 16);
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            });
        }
        else
        {
            if (runnable1!=null)
                handler1.removeCallbacks(runnable1);
            if (m1!=null)
                m1.setVisible(false);
            m2=marker;
            flag = true;
            handler2.post(runnable2 = new Runnable() {
                int i = 0;

                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    if (i < directionPoint.size())
                        marker.setPosition(directionPoint.get(i));
                    i++;
                    if (i == directionPoint.size())
                        i = 0;
                    // Post again 16ms later.
                    handler1.postDelayed(this, 16);
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            });
        }
    }

    private void addPolylineToMap(ArrayList<LatLng> coordinates,ArrayList<LatLng> polyline){
        if(mMap!=null){
            mMap.clear();
        }        Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.dustbin).copy(Bitmap.Config.ARGB_8888, true);

        if (coordinates!=null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates.get(0),10));

            for (int i = 0; i < coordinates.size(); i++) {
                mMap.addMarker(new MarkerOptions()
                        .position(coordinates.get(i))
                        .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap)));
            }
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.pattern(PATTERN_POLYGON_ALPHA);
        polylineOptions.startCap(new RoundCap());

        mMap.addPolyline(polylineOptions.addAll(polyline).width(10).color(Color.BLUE));
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.bus).copy(Bitmap.Config.ARGB_8888,true);

        Marker marker = mMap.addMarker(new MarkerOptions().position(polyline.get(0)).icon(BitmapDescriptorFactory.fromBitmap(bitmap)).flat(true));
        animateMarker(mMap, marker, polyline, false);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE);

        Gson gson =new Gson();
        String mapDet = sharedPreferences.getString("mapdetails","");
        MapDetails myMapDetails = gson.fromJson(mapDet,MapDetails.class);
        if (myMapDetails!=null) {


            addPolylineToMap(myMapDetails.mCoordinates,myMapDetails.mPolyline);
        }
        else{
           // updateMap();
        }
    }

    private void updateMap(){


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

                        coordinates.add(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng)));


                        String latlngString = lat+"%2C"+lng;

                        Detail placeDetail = post.getValue(Detail.class);
                        Log.e("Place upvotes",placeDetail.upvotes+"");

                        extraDetails.add(new ExtraDetail(placeDetail.upvotes,latlngString,new Date()));
                        meanUpvotes+=placeDetail.upvotes;
                        Log.e("Sum ",meanUpvotes+"");

                    }


                    sortLocations();
                    new Directions().execute();
                }






                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    class SortComparator implements Comparator<ExtraDetail> {
        private int maxUpvotes;
        private int minUpvotes;
        public SortComparator(int maxUpvotes, int minUpvotes) {
            this.maxUpvotes = maxUpvotes;
            this.minUpvotes = minUpvotes;
        }

        @Override
        public int compare(ExtraDetail extraDetail, ExtraDetail t1) {
            double normalizedUpvotes1= (extraDetail.upvotes - minUpvotes)*1.0
                    /(maxUpvotes  - minUpvotes);
            double normalizedUpvotes2= (t1.upvotes - minUpvotes)*1.0
                    /(maxUpvotes  - minUpvotes);
            return -1*(int)((normalizedUpvotes1 - normalizedUpvotes2)*10000);
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }
    private void sortLocations(){
        int maxUpvotes=Integer.MIN_VALUE;
        int minUpvotes= Integer.MAX_VALUE;
        for(int i=0;i<extraDetails.size();i++){

            int upvotes = extraDetails.get(i).upvotes;
            if(upvotes>maxUpvotes){
                maxUpvotes = upvotes;
            }
            else if(upvotes<minUpvotes){
                minUpvotes = upvotes;

            }
        }

        Collections.sort(extraDetails,new SortComparator(maxUpvotes,minUpvotes));
    }
    private class Directions extends AsyncTask<String,Void,String> {




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
            while(i<extraDetails.size()&&meanUpvotes <= extraDetails.get(i).upvotes) {
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
            latlng.clear();
            return result;
        }
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

        private void addStringToPolyline(String s){
            try {
                JSONObject dataobj=new JSONObject(s);
                JSONObject overview=dataobj.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline");
                String polyline=overview.getString("points");
                if (lines==null)
                    lines = new ArrayList<>();
                for (LatLng p : decodePolyline(polyline)) {
                    lines.add(p);
                }
                Log.e("polyline ",lines+"");
               mapDetails = new MapDetails(coordinates,lines);
                SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson =new Gson();
                String mapDetailsString = gson.toJson(mapDetails);
                editor.putString("mapdetails",mapDetailsString);
                editor.commit();

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
            addPolylineToMap(coordinates,lines
            );

            //updateProgress = true;
            swipeRefreshLayout.setRefreshing(false);

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
            String url="";
            if(latlng.size() <=2)
                url = "https://maps.googleapis.com/maps/api/directions/json?origin="+latlng.get(0)+"&destination="+
                        latlng.get(latlng.size()-1)+"&key="+API_KEY;
            else
             url="https://maps.googleapis.com/maps/api/directions/json?origin="+latlng.get(0)+"&destination="+
                    latlng.get(latlng.size()-1)+
                    "&waypoints=optimize:true"+waypoints+"&key="+API_KEY;
            Log.e("Key",API_KEY);
            return url;
        }
    }


}

