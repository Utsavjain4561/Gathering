package com.example.aryan.hack;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by aryan on 10/1/19.
 */

public class Detail {
    public String description;
    public int upvotes;
    public String address;
    Detail(String description,int upvotes,String address)
    {
        this.upvotes = upvotes;
        this.description = description;
        this.address = address;
    }
    Detail()
    {
        this.upvotes = 0;
        this.description = "no";
    }
}
