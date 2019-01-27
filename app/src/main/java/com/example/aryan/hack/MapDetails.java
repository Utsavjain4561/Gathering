package com.example.aryan.hack;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by UTSAV JAIN on 1/19/2019.
 */

public class MapDetails  {
    public ArrayList<LatLng> mCoordinates;
    public ArrayList<LatLng> mPolyline;

    public MapDetails(ArrayList<LatLng> coordinates,ArrayList<LatLng> polyline) {
        this.mCoordinates = coordinates;
        this.mPolyline = polyline;

    }
}
