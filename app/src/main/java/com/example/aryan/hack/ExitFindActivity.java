package com.example.aryan.hack;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
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
import java.util.List;

public class ExitFindActivity extends AppCompatActivity implements LocationListener,OnMapReadyCallback {

    double la,ln;
    double lati,longi;
    GoogleMap map=null;
    static LocationManager locationManager;
    ArrayList<LatLng> coordinates = new ArrayList<LatLng>();
    private boolean flag=true;
    ArrayList<LatLng> lines = new ArrayList<>();
    public static final PatternItem DOT = new Dot();

    public static final PatternItem DASH = new Dash(20);
    public static final PatternItem GAP = new Gap(20);
    public static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(DOT,GAP, DASH);
    Runnable runnable1=null,runnable2=null;
    Handler handler1 = new Handler();
    Handler handler2 = new Handler();
    private Marker m1=null,m2=null;


    private  MapDetails mapDetails ;
    private final String API_KEY="AIzaSyDgxHo_XoHzO02UrBb0milqb4Y705zTN0w";
    ArrayList<LatLng> arrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit_find);
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
    locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(ExitFindActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ExitFindActivity.this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000,
                10, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10,
                this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (map!=null)
            map.clear();
        if (map!=null)
            map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())));
        la=location.getLatitude();
        ln=location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference("gatherings").child("kumbhmela").child("exitpoints");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot post:dataSnapshot.getChildren())
                    {
                        String latlng = String.valueOf(post.getValue());
                        int index = latlng.indexOf('n');
                        String lat = latlng.substring(0,index);
                        String lng = latlng.substring(index+1);
                        lat= lat.replace('o','.');
                        lng = lng.replace('o','.');
                        map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng))));
                        arrayList.add(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng)));
                    }
                    double distance = 10000000.0;
                    lati=-1;
                    longi=-1;
                    for (int i =0;i<arrayList.size();i++)
                    {
                        if (distance>distance(la,ln,arrayList.get(i).latitude,arrayList.get(i).longitude))
                        {
                            distance = distance(la,ln,arrayList.get(i).latitude,arrayList.get(i).longitude);
                            lati = arrayList.get(i).latitude;
                            longi = arrayList.get(i).longitude;
                        }
                    }
                    if (lati!=-1&&longi!=-1)
                    {
                        new Directions().execute();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private class Directions extends AsyncTask<String,Void,String>
    {

        public String makeConnection(String la,String ln,String lati,String longi)
        {
            HttpURLConnection httpURLConnection=null;
            URLConnection url;
            String data="";
            try{
                url = new URL(createUrl(la,ln,lati,longi)).openConnection();
                httpURLConnection=(HttpURLConnection) url;
                Log.e("URL",createUrl(la,ln,lati,longi));
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
        public String createUrl(String la,String ln,String lati,String longi)
        {
            String url="https://maps.googleapis.com/maps/api/directions/json?origin="+la+"%2C"+ln+"&destination="+
                    lati+"%2C"+longi+"&key="+API_KEY;
            return url;
        }
        @Override
        protected String doInBackground(String... strings) {
            return makeConnection(la+"",ln+"",lati+"",longi+"");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("ans",s);
            addStringToPolyline(s);
            addPolylineToMap(coordinates,lines);

        }
        private void addPolylineToMap(ArrayList<LatLng> coordinates, ArrayList<LatLng> polyline){
            if(map!=null){
                map.clear();
            }        Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.dustbin).copy(Bitmap.Config.ARGB_8888, true);

            if (coordinates!=null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates.get(0),10));

                for (int i = 0; i < coordinates.size(); i++) {
                    map.addMarker(new MarkerOptions()
                            .position(coordinates.get(i))
                            .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap)));
                }
            }
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.pattern(PATTERN_POLYGON_ALPHA);
            polylineOptions.startCap(new RoundCap());

            map.addPolyline(polylineOptions.addAll(polyline).width(10).color(Color.BLUE));
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.bus).copy(Bitmap.Config.ARGB_8888,true);

            Marker marker = map.addMarker(new MarkerOptions().position(polyline.get(0)).icon(BitmapDescriptorFactory.fromBitmap(bitmap)).flat(true));
            animateMarker(map, marker, polyline, false);
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
    }
}
