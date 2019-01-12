package com.example.aryan.hack;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by aryan on 10/1/19.
 */

public class Detail {
    public String description;
    public int upvotes;
    public LatLng latLng;
    Detail(String description,int upvotes,LatLng latLng)
    {
        this.upvotes = upvotes;
        this.description = description;
        this.latLng = latLng;
    }
}
